/**
 * Copyright 2015 Technische Universitat Wien (TUW), Distributed SystemsGroup
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
package at.ac.tuwien.dsg.csdg.inputProcessing.checkpointing;

import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class CheckpointingUtils {

    public static String path = "checkpoint";
    private static CheckpointingUtils checkpointingUtils = new CheckpointingUtils();

    public static CheckpointingUtils getInstance() {
        return checkpointingUtils;
    }

    private CheckpointingUtils() {
        createFolder();
    }

    public boolean checkpointingFolderExists() {
        File dir = new File(path);
        return dir.exists();
    }

    private void createFolder() {
        File theDir = new File(path);

        if (!theDir.exists()) {
            try {
                theDir.mkdir();

            } catch (SecurityException se) {
                DependencyGraphLogger.logger.error("Error when creating checkpointing folder: " + se.getMessage());
            }

        }
    }

    public HashMap<String, String> getTOSCADescription() {
        String type = "toscaDescription";
        HashMap<String, String> descriptions = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(type)) {
                String id = file.getName().split(type + "_")[1].split(".xml")[0];
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String content = "";

                    while (reader.ready()) {
                        content += reader.readLine();

                    }
                    descriptions.put(id, content);
                } catch (FileNotFoundException ex) {
                    DependencyGraphLogger.logger.error("Error while reading tosca description " + ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(CheckpointingUtils.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return descriptions;
    }

    public HashMap<String, String> getDeploymentDescription() {
        String type = "deploymentDescription";
        HashMap<String, String> descriptions = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(type)) {
                String id = file.getName().split(type + "_")[1].split(".xml")[0];
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String content = "";

                    while (reader.ready()) {
                        content += reader.readLine();

                    }
                    descriptions.put(id, content);
                } catch (FileNotFoundException ex) {
                    DependencyGraphLogger.logger.error("Error while reading tosca description " + ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(CheckpointingUtils.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return descriptions;
    }

    public HashMap<String, String> getServiceDescription() {
        String type = "serviceDescription";
        HashMap<String, String> descriptions = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(type)) {
                String id = file.getName().split(type + "_")[1].split(".xml")[0];
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String content = "";

                    while (reader.ready()) {
                        content += reader.readLine();

                    }
                    descriptions.put(id, content);
                } catch (FileNotFoundException ex) {
                    DependencyGraphLogger.logger.error("Error while reading tosca description " + ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(CheckpointingUtils.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return descriptions;
    }

    public HashMap<String, String> getEffects() {
        String type = "effects";
        HashMap<String, String> descriptions = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(type)) {
                String id = file.getName().split(type + "_")[1].split(".xml")[0];
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String content = "";

                    while (reader.ready()) {
                        content += reader.readLine();

                    }
                    descriptions.put(id, content);
                } catch (FileNotFoundException ex) {
                    DependencyGraphLogger.logger.error("Error while reading tosca description " + ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(CheckpointingUtils.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return descriptions;
    }

    public HashMap<String, String> getCompositionRules() {
        String type = "compositionRules";
        HashMap<String, String> descriptions = new HashMap<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().contains(type)) {
                String id = file.getName().split(type + "_")[1].split(".xml")[0];
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String content = "";

                    while (reader.ready()) {
                        content += reader.readLine();

                    }
                    descriptions.put(id, content);
                } catch (FileNotFoundException ex) {
                    DependencyGraphLogger.logger.error("Error while reading tosca description " + ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(CheckpointingUtils.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return descriptions;
    }

    public void storeTOSCADescription(String tosca, String id) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/toscaDescription_" + id + ".xml")));
            bufferedWriter.write(tosca);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            DependencyGraphLogger.logger.error("Error while checkpointing tosca: " + ex.getMessage());
        }
    }

    public void storeDeploymentDescription(String deployment, String id) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/deploymentDescription_" + id + ".xml")));
            bufferedWriter.write(deployment);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            DependencyGraphLogger.logger.error("Error while checkpointing deploymentDescription: " + ex.getMessage());
        }
    }

    public void storeServiceDescription(String service, String id) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/serviceDescription_" + id + ".xml")));
            bufferedWriter.write(service);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            DependencyGraphLogger.logger.error("Error while checkpointing serviceDescription: " + ex.getMessage());
        }
    }

    public void storeServiceEffects(String effects, String id) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/effects_" + id + ".xml")));
            bufferedWriter.write(effects);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            DependencyGraphLogger.logger.error("Error while checkpointing effects: " + ex.getMessage());
        }
    }

    public void storeServiceCompositionRules(String compositionRules, String id) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(new File(path + "/compositionRules_" + id + ".xml")));
            bufferedWriter.write(compositionRules);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ex) {
            DependencyGraphLogger.logger.error("Error while checkpointing composition rules: " + ex.getMessage());
        }
    }

    public void stopGracefullyControl(String serviceID) {
        File theDir = new File(path);

        if (theDir.exists()) {
            File[] listOfFiles = theDir.listFiles();

            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().contains(serviceID)) {

                    file.delete();
                }

            }
        }
    }
}
