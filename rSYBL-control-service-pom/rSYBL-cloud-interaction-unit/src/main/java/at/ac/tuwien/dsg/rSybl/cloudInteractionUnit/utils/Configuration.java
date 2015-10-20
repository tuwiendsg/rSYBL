/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184.  *
 * This work was partially supported by the European Commission in terms of the
 * CELAR FP7 project (FP7-ICT-2011-8 #317790).
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
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.OfferedEnforcementCapabilities;

public class Configuration {

    private static Properties configuration;

    static {
        configuration = new Properties();
        try {
            configuration.load(new FileReader(new File("./config.properties")));

        } catch (Exception ex) {
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream("/config.properties");
            try {
                configuration.load(is);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static String getUserEMailAddress() {
        return configuration.getProperty("UserEmailAddress");
    }

    public static String getAPIUserName() {
        return configuration.getProperty("ApiUserName");
    }

    public static String getCustomerUUID() {
        return configuration.getProperty("CustomerUUID");
    }

    public static String getVdcUUID() {
        return configuration.getProperty("VdcUUID");
    }
    
    public static String getGoogleProjectID(){
        return configuration.getProperty("GoogleProjectID");
    }
    public static String getGoogleClientSecretsPath(){
        return configuration.getProperty("GoogleClientSecrets");
    }
     public static String getGoogleZoneName(){
        return configuration.getProperty("GoogleZoneName");
    }
    public static String getDefaultProductOfferUUID() {
        return configuration.getProperty("DefaultProductOfferUUID");
    }

    public static String getDeploymentInstanceUUID() {
        return configuration.getProperty("DeploymentInstanceUUID");
    }

    public static String getClusterUUID() {
        return configuration.getProperty("ClusterUUID");
    }
public static boolean resourceLevelControlEnabled(){
    return Boolean.parseBoolean(configuration.getProperty("ResourceLevelControlEnabled"));
}
    public static String getNetworkUUID() {
        return configuration.getProperty("NetworkUUID");
    }

    public static String getSSHKey() {
        return configuration.getProperty("SSHKey");
    }
     public static String getGoogleAccount(){
         return configuration.getProperty("GoogleAccount");
     }

    public static String getPassword() {
        return configuration.getProperty("Password");
    }

    public static String getEndPointAddress() {
        return configuration.getProperty("ENDPOINT_ADDRESS_PROPERTY");
    }

    public static String getRuntimeRegistryName() {
        return configuration.getProperty("SYBLRuntimeRegistryName");
    }

    public static String getMonitoringPlugin() {
        return configuration.getProperty("MonitoringPlugin");
    }

    public static String getEnforcementPlugin() {
        return configuration.getProperty("EnforcementPlugin");
    }
public static int getRefreshPeriod(){
    	return Integer.parseInt(configuration.getProperty("REFRESH_PERIOD"));
    }
     public static String getResourcesEffectsPath(){
    	return configuration.getProperty("ResourcesEffects");
    }
    //PluginName:Class
    public static HashMap<String, String> getEnforcementPlugins() {
        String[] enforcements;
        HashMap<String, String> enfPlugins = new HashMap<String, String>();
        if (configuration.getProperty("MultipleEnforcementPlugins") != null && !configuration.getProperty("MultipleEnforcementPlugins").equalsIgnoreCase("")) {
            enforcements = configuration.getProperty("MultipleEnforcementPlugins").split(",");
            for (String enf : enforcements) {
                String[] splits = enf.split(":");
                if (splits.length > 1) {
                    enfPlugins.put(splits[0].replace(" ", ""), splits[1].replace(" ", ""));
                } else {
                    enfPlugins.put(enf.replace(" ", ""), enf.replace(" ", ""));
                }
            }
        }
        return enfPlugins;
    }

    public static String getRuntimeRegistryPort() {
        return configuration.getProperty("SYBLRuntimePort");
    }

    public static String getGangliaPort() {
        return configuration.getProperty("GangliaPort");
    }

    public static String getGangliaIP() {
        return configuration.getProperty("GangliaIP");
    }

    public static String getCompositionRulesPath() {
        return configuration.getProperty("CompositionRulesMELA");
    }

    public static String getCertificateName() {
        return configuration.getProperty("CertificateName");
    }

    public static String getCertificatePath() {
        return configuration.getProperty("CertificatePath");
    }

    public static String getCloudAPIType() {
        return configuration.getProperty("CloudAPIType");
    }

    public static String getCloudAPIEndpoint() {
        return configuration.getProperty("CloudAPIEndpoint");
    }

    public static String getCloudUser() {
        return configuration.getProperty("CloudUser");
    }

    public static String getEnforcementServiceURL() {
        return configuration.getProperty("EnforcementServiceURL");
    }

    public static String getAccessIP() {
        return configuration.getProperty("AccessIP");
    }

    public static String getCloudPassword() {
        return configuration.getProperty("CloudPassword");
    }

}
