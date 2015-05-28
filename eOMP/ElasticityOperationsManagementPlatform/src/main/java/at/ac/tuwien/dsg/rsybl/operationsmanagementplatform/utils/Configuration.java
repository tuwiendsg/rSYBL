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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.HashMap;
import java.util.List;

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

    public static String getQueueName() {
        return configuration.getProperty("QueueName");
    }

    public static String getInteractionTopicName() {
        return configuration.getProperty("InteractionTopicName");
    }

    public static String getCloudAMQPUsername() {
        return configuration.getProperty("CloudAMQPUsername");

    }

    public static String getCloudAMQPUri() {
        return configuration.getProperty("CloudAMQPUri");

    }

    public static String getCloudAMQPPassword() {
        return configuration.getProperty("CloudAMQPPassword");

    }

    public static String getCloudAMQPVirtualHost() {
        return configuration.getProperty("CloudAMQPVirtualHost");

    }

    //PluginName:Class
    public static HashMap<String, String> getInteractionMethods() {
        String[] enforcements;
        HashMap<String, String> enfPlugins = new HashMap<String, String>();
        if (configuration.getProperty("InteractionMethods") != null && !configuration.getProperty("InteractionMethods").equalsIgnoreCase("")) {
            enforcements = configuration.getProperty("InteractionMethods").split(",");
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

    public static boolean getMQEnabled() {
        if (configuration.getProperty("EventQueueEnabled") != null) {
            return Boolean.parseBoolean(configuration.getProperty("EventQueueEnabled"));
        } else {
            return false;
        }
    }

    public static String getQueueUrl() {
        return configuration.getProperty("QueueUrl");
    }

}
