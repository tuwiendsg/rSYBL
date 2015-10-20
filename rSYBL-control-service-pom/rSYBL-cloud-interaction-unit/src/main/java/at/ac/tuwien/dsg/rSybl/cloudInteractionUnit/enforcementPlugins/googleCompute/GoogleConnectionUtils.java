/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. * This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.googleCompute;

import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.Compute.MachineTypes;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.AccessConfig;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.Firewall;
import com.google.api.services.compute.model.Firewall.Allowed;
import com.google.api.services.compute.model.Image;
import com.google.api.services.compute.model.ImageList;

import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.MachineType;
import com.google.api.services.compute.model.MachineTypeList;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ServiceAccount;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class GoogleConnectionUtils {

    private FileDataStoreFactory dataStoreFactory;
    private String projectId;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private List<String> SCOPES;
    private static final java.io.File DATA_STORE_DIR
            = new java.io.File(System.getProperty("user.home"), ".store/compute_engine_sample");
    private String zoneName;
    private String clientSecretsPath;
    private Compute compute;

    private static final String SOURCE_IMAGE_PREFIX = "https://www.googleapis.com/compute/v1/projects/";
    private static final String SOURCE_IMAGE_PATH = "debian-cloud/global/images/debian-7-wheezy-v20150710";
    private static final String NETWORK_INTERFACE_CONFIG = "ONE_TO_ONE_NAT";
    private static final String NETWORK_ACCESS_CONFIG = "External NAT";
    private static final long OPERATION_TIMEOUT_MILLIS = 60 * 1000;

    public GoogleConnectionUtils() {
        projectId = Configuration.getGoogleProjectID();

        zoneName = Configuration.getGoogleZoneName();
        clientSecretsPath = Configuration.getGoogleClientSecretsPath();
        SCOPES = new ArrayList<String>();
        SCOPES.add(ComputeScopes.COMPUTE);
        SCOPES.add(ComputeScopes.DEVSTORAGE_READ_ONLY);
        initialize();

    }

    public List<MachineType> getAvailableFlavors() {
        List<MachineType> types = new ArrayList<MachineType>();
        MachineTypes machineTypes = compute.machineTypes();
        try {
            Compute.MachineTypes.List list = machineTypes.list(projectId, zoneName);
            MachineTypeList machineTypeList = list.execute();
            for (MachineType machineType : machineTypeList.getItems()) {
                types.add(machineType);
               // System.out.println("Machine type for "+projectId+" for zone "+zoneName+" : "+machineType.getName());
            }
        } catch (IOException ex) {
            Logger.getLogger(GoogleConnectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return types;

    }

    public String startInstance(String instanceName, String image, String pathToScript, HashMap<String, String> metadata) throws IOException {
        // Select a machine type.
        Instance instance = new Instance();
        String machine = "https://www.googleapis.com/compute/v1/projects/" + projectId + "/zones/" + this.zoneName + "/machineTypes/n1-standard-1";
        instance.setMachineType(machine);

        // Get a name from the user.
        instance.setName(instanceName);
        // Use the default network.  Could select here if needed.
        NetworkInterface iface;
        iface = new NetworkInterface();
        //iface.setFactory(jsonFactory);
        iface.setNetwork("https://www.googleapis.com/compute/v1/projects/" + projectId + "/global/networks/default");
        List<AccessConfig> configs = new ArrayList<>();
        AccessConfig config = new AccessConfig();
        config.setType(NETWORK_INTERFACE_CONFIG);
        config.setName(NETWORK_ACCESS_CONFIG);
        configs.add(config);
        iface.setAccessConfigs(configs);
        instance.setNetworkInterfaces(Collections.singletonList(iface));
        Firewall firewall = new Firewall();
        firewall.setName(instanceName);
        Allowed allowedTCP8080 = new Allowed();
        allowedTCP8080.setIPProtocol("tcp");
        List<String> ports = new ArrayList<String>();
        ports.add("80");
        ports.add("8080");
        ports.add("8280");
        ports.add("8180");
        ports.add("9610");
        allowedTCP8080.setPorts(ports);
        List<Allowed> allowedPorts = new ArrayList<Allowed>();
        allowedPorts.add(allowedTCP8080);
        firewall.setAllowed(allowedPorts);
        firewall.setDescription("httpports");
        firewall.setNetwork(iface.getNetwork());
        List<ServiceAccount> serviceAccount = new ArrayList<ServiceAccount>();
        ServiceAccount account = new ServiceAccount();
        account.setEmail(Configuration.getGoogleAccount());
        account.setScopes(SCOPES);
        
        serviceAccount.add(account);
        instance.setServiceAccounts(serviceAccount);
        compute.firewalls().insert(instanceName, firewall);
        //Create the Persistent Disk parameters
        AttachedDiskInitializeParams diskParamsToInsert = new AttachedDiskInitializeParams();
        diskParamsToInsert.setDiskName(instanceName);
        diskParamsToInsert.setDiskSizeGb(10L);
        if (image.equals("")) {
            diskParamsToInsert.setSourceImage("https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1404-trusty-v20150909a");
        } else {
            diskParamsToInsert.setSourceImage("projects/" + projectId + "/global/images/" + image);
        }
        //Create the disk
        AttachedDisk diskToInsert = new AttachedDisk();
        diskToInsert.setBoot(true);
        diskToInsert.setType("PERSISTENT");
        diskToInsert.setMode("READ_WRITE");
        diskToInsert.setInitializeParams(diskParamsToInsert);

        //Add Disk to List to be added to Instance
        List<AttachedDisk> listOfDisks = new ArrayList<AttachedDisk>();
        listOfDisks.add(diskToInsert);

        //Add disk to instance
        instance.setDisks(listOfDisks);
        Compute.Instances.Insert ins = compute.instances().insert(projectId, zoneName, instance);
        // Optional - Add a startup script to be used by the VM Instance.
        if (pathToScript != null && !pathToScript.equalsIgnoreCase("")) {
            Metadata meta = new Metadata();

            Metadata.Items item = new Metadata.Items();
            item.setKey("startup-script-url");
            if (pathToScript.contains("gs://")) {
                item.setValue(pathToScript);
            } else {
                item.setValue("gs://" + pathToScript);
            }
            if (metadata != null) {
                List<Metadata.Items> items = new ArrayList<Metadata.Items>();
                items.add(item);
                if (Configuration.getSSHKey() != null && !Configuration.getSSHKey().equalsIgnoreCase("")) {
                    String sshKey = readFile(Configuration.getSSHKey());
                    Metadata.Items sshKeyItem = new Metadata.Items();
                    sshKeyItem.setKey("sshKeys");
                    sshKeyItem.setValue(sshKey);
                }
                for (String key : metadata.keySet()) {
                    Metadata.Items myItem = new Metadata.Items();
                    myItem.setKey(key);
                    myItem.setValue(metadata.get(key));
                    items.add(myItem);
                }
                meta.setItems(items);
            } else {
                meta.setItems(Collections.singletonList(item));
            }
            instance.setMetadata(meta);
        }
        Operation op = ins.execute();
        try {
            op = blockUntilComplete(op, OPERATION_TIMEOUT_MILLIS);
        } catch (Exception ex) {
            // RuntimeLogger.logger.info(ex.getMessage());
            Logger.getLogger(GoogleConnectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ip = "";
        Compute.Instances.List instances = compute.instances().list(projectId, zoneName);
        InstanceList list = instances.execute();
        if (list != null) {
            for (Instance inst : list.getItems()) {
                if (inst.getName().equalsIgnoreCase(instance.getName())) {
                    instance = inst;
                    ip = instance.getNetworkInterfaces().get(0).getNetworkIP();
                }
            }
        }
        System.out.println("Operation is  " + op.toPrettyString());
        System.out.println("The instance is " + ip + instance.toPrettyString());

        return ip;
    }

    public String readFile(String filename) {
        String content = null;
        File file = new File(filename); //for ex foo.txt
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(GoogleConnectionUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return content;
    }

    public boolean deleteInstance(String instanceName) throws Exception {
        System.out.println("================== Deleting Instance " + instanceName + " ==================");
        Compute.Instances.Delete delete = compute.instances().delete(projectId, zoneName, instanceName);
        Operation op = delete.execute();
        try {
            op = blockUntilComplete(op, OPERATION_TIMEOUT_MILLIS);
        } catch (Exception ex) {
            // RuntimeLogger.logger.info(ex.getMessage());
            Logger.getLogger(GoogleConnectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        System.out.println("Operation is  " + op.toPrettyString());
        Compute.Disks.Delete deleteDisk = compute.disks().delete(projectId, zoneName, instanceName);
        Operation opDisk = deleteDisk.execute();
        try {
            opDisk = blockUntilComplete(opDisk, OPERATION_TIMEOUT_MILLIS);
        } catch (Exception ex) {
            // RuntimeLogger.logger.info(ex.getMessage());
            Logger.getLogger(GoogleConnectionUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        System.out.println("Operation of disk deletion is  " + op.toPrettyString());
        return true;
    }

    private Credential authorize() {
        try {
            GoogleClientSecrets clientSecrets = null;
            try {
                FileReader fileReader = new FileReader(new File(clientSecretsPath));
                clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, fileReader);
            } catch (IOException ex) {
                InputStreamReader streamReader = new InputStreamReader(GoogleComputeAPI.class.getResourceAsStream(clientSecretsPath));
                clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, streamReader);
                Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                    || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
                System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
                        + "into compute-engine-cmdline-sample/src/main/resources/client_secrets.json");
                System.exit(1);
            }
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES).setDataStoreFactory(dataStoreFactory)
                    .build();
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void printInstances() throws IOException {
        System.out.println("================== Listing Compute Engine Instances ==================");
        Compute.Instances.List instances = compute.instances().list(projectId, zoneName);
        InstanceList list = instances.execute();
        if (list.getItems() == null) {
            System.out.println("No instances found. Sign in to the Google APIs Console and create "
                    + "an instance at: code.google.com/apis/console");
        } else {
            for (Instance instance : list.getItems()) {
                System.err.println(instance.getName());
                System.out.println(instance.toPrettyString());
            }
        }
    }

    private void initialize() {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // Authorization
            Credential credential = authorize();
            
            compute = new Compute.Builder(
                    httpTransport, JSON_FACTORY, null).setApplicationName("app1")
                    .setHttpRequestInitializer(credential).build();
        } catch (Exception e) {
            e.printStackTrace();
            //  RuntimeLogger.logger.info(e.getMessage());
        }
    }

    public Operation blockUntilComplete(Operation operation, long timeout)
            throws Exception {
        long start = System.currentTimeMillis();
        final long POLL_INTERVAL = 5 * 1000;
        String zone = operation.getZone();
        if (zone != null) {
            String[] bits = zone.split("/");
            zone = bits[bits.length - 1];
        }
        String status = operation.getStatus();
        String opId = operation.getName();
        while (operation != null && !status.equals("DONE")) {
            Thread.sleep(POLL_INTERVAL);
            long elapsed = System.currentTimeMillis() - start;
            if (elapsed >= timeout) {
                throw new InterruptedException("Timed out waiting for operation to complete");
            }
            if (zone != null) {
                Compute.ZoneOperations.Get get = compute.zoneOperations().get(this.projectId, this.zoneName, opId);
                operation = get.execute();
            } else {
                Compute.GlobalOperations.Get get = compute.globalOperations().get(projectId, opId);
                operation = get.execute();
            }
            if (operation != null) {
                status = operation.getStatus();
            }
        }
        return operation;
    }

    public List<String> listImages() {
        List<String> imagesIds = new ArrayList<String>();
        try {
            Compute.Images.List images = compute.images().list(projectId);

            ImageList imagesList = images.execute();
            if (imagesList != null && imagesList.getItems()!=null) {
                for (Image image : imagesList.getItems()) {
                    imagesIds.add(image.getName());
                    //System.out.println("Image "+image.getName());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GoogleConnectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return imagesIds;
    }
}
