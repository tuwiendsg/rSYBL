/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184.
 * 
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
package at.ac.tuwien.dsg.rSybl.cloudApiLowLevel.enforcementPlugins.openstack;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
    private static Properties configuration;
    static {
        Configuration.configuration = new Properties();
        try {
            final InputStream is = Configuration.class.getClassLoader()
                    .getResourceAsStream("/config.properties");
            Configuration.configuration.load(is);
            // configuration.load(new FileReader( new
            // File("./config.properties")));
        } catch (final Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }

    public static String getAccessIP() {
        return Configuration.configuration.getProperty("AccessIP");
    }

    public static String getAPIUserName() {
        return Configuration.configuration.getProperty("ApiUserName");
    }

    public static String getCertificateName() {
        return Configuration.configuration.getProperty("CertificateName");
    }

    public static String getCertificatePath() {
        return Configuration.configuration.getProperty("CertificatePath");
    }

    public static String getCloudAPIEndpoint() {
        return Configuration.configuration.getProperty("CloudAPIEndpoint");
    }

    public static String getCloudAPIType() {
        return Configuration.configuration.getProperty("CloudAPIType");
    }

    public static String getCloudPassword() {
        return Configuration.configuration.getProperty("CloudPassword");
    }

    public static String getCloudUser() {
        return Configuration.configuration.getProperty("CloudUser");
    }

    public static String getClusterUUID() {
        return Configuration.configuration.getProperty("ClusterUUID");
    }

    public static String getCompositionRulesPath() {
        return Configuration.configuration.getProperty("CompositionRulesMELA");
    }

    public static String getCustomerUUID() {
        return Configuration.configuration.getProperty("CustomerUUID");
    }

    public static String getDefaultProductOfferUUID() {
        return Configuration.configuration
                .getProperty("DefaultProductOfferUUID");
    }

    public static String getDeploymentInstanceUUID() {
        return Configuration.configuration
                .getProperty("DeploymentInstanceUUID");
    }

    public static String getEndPointAddress() {
        return Configuration.configuration
                .getProperty("ENDPOINT_ADDRESS_PROPERTY");
    }

    public static String getEnforcementPlugin() {
        return Configuration.configuration.getProperty("EnforcementPlugin");
    }

    public static String getEnforcementServiceURL() {
        return Configuration.configuration.getProperty("EnforcementServiceURL");
    }

    public static String getGangliaIP() {
        return Configuration.configuration.getProperty("GangliaIP");
    }

    public static String getGangliaPort() {
        return Configuration.configuration.getProperty("GangliaPort");
    }

    public static String getMonitoringPlugin() {
        return Configuration.configuration.getProperty("MonitoringPlugin");
    }

    public static String getNetworkUUID() {
        return Configuration.configuration.getProperty("NetworkUUID");
    }

    public static String getPassword() {
        return Configuration.configuration.getProperty("Password");
    }

    public static String getRuntimeRegistryName() {
        return Configuration.configuration
                .getProperty("SYBLRuntimeRegistryName");
    }

    public static String getRuntimeRegistryPort() {
        return Configuration.configuration.getProperty("SYBLRuntimePort");
    }

    public static String getSSHKey() {
        return Configuration.configuration.getProperty("SSHKey");
    }

    public static String getUserEMailAddress() {
        return Configuration.configuration.getProperty("UserEmailAddress");
    }

    public static String getVdcUUID() {
        return Configuration.configuration.getProperty("VdcUUID");
    }
}
