package at.ac.tuwien.dsg.rsybl.controllercommunication;

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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;

import javax.xml.bind.JAXBContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import static sun.misc.RequestProcessor.postRequest;

public class SYBLControlClient {

    protected String REST_API_URL = "http://localhost:8280/rSYBL/restWS";
    protected String compRules;

    public SYBLControlClient(String rsyblurl) {
        REST_API_URL = rsyblurl;
    }

    public void initialize() {
        try {
            String applicationID = "SCAN";
            String tosca = readFile("test.tosca", Charset.defaultCharset());
            String deployment = readFile("deployment_SCAN.xml", Charset.defaultCharset());
            String compositionRules = readFile("compositionRules.xml", Charset.defaultCharset());
            SYBLControlClient sYBLControlClient = new SYBLControlClient(REST_API_URL);
//            sYBLControlClient.prepareControl(applicationID);
//            sYBLControlClient.setApplicationDescription(applicationID, tosca);
//            sYBLControlClient.setApplicationDeployment(applicationID, deployment);
//            sYBLControlClient.startTest(applicationID);
//            sYBLControlClient.testElasticityCapability(applicationID, "cassandraSeedNode", "balance");

            sYBLControlClient.prepareControl(applicationID);

            sYBLControlClient.setApplicationDescription(applicationID, tosca);
            sYBLControlClient.setApplicationDeployment(applicationID, deployment);
            sYBLControlClient.setMetricsCompositionRules(applicationID, compositionRules);
            sYBLControlClient.startApplication(applicationID);

        } catch (IOException ex) {
            Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void modifyAppDescription(String applicationID, String newAppDescription, String appDeployment, String effects) {
        stopApplication(applicationID);
        initialInstantiationLifecycle(applicationID, newAppDescription, appDeployment, effects, compRules);
    }

    public void initialInstantiationLifecycle(String applicationID, String appDescription, String appDeployment, String effects, String compRules) {
        this.compRules = compRules;
        prepareControl(applicationID);
        setApplicationDescription(applicationID, appDescription);
        setApplicationDeployment(applicationID, appDeployment);
        setElasticityCapabilitiesEffects(applicationID, effects);
        setMetricsCompositionRules(applicationID, compRules);
        startApplication(applicationID);
    }

    public void setApplicationDescription(String applicationID, String appDescription) {

        callPUTMethod(applicationID + "/description/tosca", "application/xml", appDescription);

    }

    public void testElasticityCapability(String applicationID, String componentID, String elasticityCapability) {

        callPUTMethod(applicationID + "/" + componentID + "/testElasticityCapability/" + elasticityCapability, "application/xml", "");

    }

    public void startTest(String applicationID) {

        callPUTMethod(applicationID + "/startTEST", "application/xml", "");

    }

    public void testElasticityCapabilityWithPlugin(String applicationID, String componentID, String pluginID, String elasticityCapability) {

        callPUTMethod(applicationID + "/" + componentID + "/testElasticityCapability/" + pluginID + "/" + elasticityCapability, "application/xml", "");

    }

    public void setApplicationDeployment(String applicationID, String appDescription) {
        callPUTMethod(applicationID + "/deployment", "application/xml", appDescription);

    }

    public void prepareControl(String id) {

        callPUTMethod(id + "/prepareControl", "application/xml", id);

    }

    public void setMetricsCompositionRules(String id, String rules) {

        callPUTMethod(id + "/compositionRules", "application/xml", rules);

    }

    public void startApplication(String applicationID) {
        callPUTMethod(applicationID + "/startControl", "application/xml", "");
    }

    public void stopApplication(String applicationID) {
        callPUTMethod(applicationID + "/stopControl", "application/xml", "");
    }

    public void removeApplicationFromControl(String applicationID) {
        callDELETEMethod("managedService/" + applicationID, "application/xml");

    }

    public void setElasticityCapabilitiesEffects(String id, String effects) {

        callPUTMethod(effects, "application/xml", id + "/elasticityCapabilitiesEffects");

    }

    public void undeployService(String id) {
        callDELETEMethod(id, "application/xml");
    }

    public void replaceCompositionRules(String id, String compositionRules) {
        callPOSTMethod(id + "/compositionRules", "application/xml", compositionRules);
    }

    public void replaceRequirements(String id, String requirements) {
//            String req=    requirements.replaceAll("<", "&lt;");
//        String req2= req.replaceAll(">", "&gt;");
        callPOSTMethod(id + "/replaceRequirements/plain", "text/plain", requirements);
    }

    public void replaceEffects(String id, String effects) {
        callPOSTMethod(id + "/elasticityCapabilitiesEffects", "application/xml", effects);
    }

    public void startEnforcement(String id, String target, String capabilityID) {
        callPOSTMethod(id + "/" + target + "/" + "/testElasticityCapability/" + capabilityID, "application/xml", "");

    }

    public String callPUTMethod(String methodName, String reqType, String body) {
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPut putRequest = new HttpPut(REST_API_URL + "/" + methodName);
            putRequest.addHeader("accept", reqType);
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            putRequest.setEntity(entity);
            HttpResponse response = httpClient.execute(putRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + reqType + ":" + methodName);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String o;
            System.out.println("============Output:============");
            String output = "";
            while ((o = br.readLine()) != null) {
                output += o;
            }
            return output;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String callPOSTMethod(String methodName, String reqType, String body) {
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(REST_API_URL + "/" + methodName);
            postRequest.addHeader("accept", reqType);
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            postRequest.setEntity(entity);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                System.err.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + reqType + ":" + methodName);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String o;
            System.out.println("============Output:============");
            String output = "";
            while ((o = br.readLine()) != null) {
                output += o;
            }
            return output;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String callDELETEMethod(String methodName, String reqType) {
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpDelete delReq = new HttpDelete(REST_API_URL + "/" + methodName);
            delReq.addHeader("accept", reqType);
            HttpResponse response = httpClient.execute(delReq);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + reqType + ":" + methodName);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String o;
            System.out.println("============Output:============");
            String output = "";
            while ((o = br.readLine()) != null) {
                output += o;
            }
            return output;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String callGETMethod(String methodName, String reqType) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(REST_API_URL + "/" + methodName);
            getRequest.addHeader("accept", reqType);
            HttpResponse response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + reqType + ":" + methodName);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String o;
            System.out.println("============Output:============");
            String output = "";
            while ((o = br.readLine()) != null) {
                output += o;
            }
            return output;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getServices() {
        return callGETMethod("elasticservices", "text/plain");

    }

    public String getService(String id) {
        return callGETMethod(id + "/description", "application/xml");

    }

    public String getRequirements(String id) {
        return callGETMethod(id + "/elasticityRequirements/plain", "text/plain");
    }

    public void resumeControl(String id) {
        callPOSTMethod(id + "/startControlOnExisting", "application/xml", "");

    }
}
