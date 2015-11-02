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

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapabilityInformation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import com.google.api.services.compute.model.MachineType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class GoogleComputeAPI implements EnforcementInterface {

    private Node cloudService;
    private DependencyGraph dependencyGraph;
    private MonitoringAPIInterface monitoringAPI;
    private GoogleConnectionUtils googleConnectionUtils;
    private List<MachineType> flavors;

    public GoogleComputeAPI() {
        googleConnectionUtils = new GoogleConnectionUtils();
        flavors = googleConnectionUtils.getAvailableFlavors();
    }

    @Override
    public boolean scaleOut(Node toBeScaled) {
        boolean res = false;
        HashMap<String, String> metadata = null;

        if (toBeScaled.getStaticInformation().get("Contextualize") != null) {
            metadata = new HashMap<String, String>();
            String contextualize = (String) toBeScaled.getStaticInformation().get("Contextualize");
            for (String m : contextualize.split(";")) {
                if (m.contains("=")) {
                    String val = m.split("=")[1];

                    while (val.contains("$")) {
                        String toBeReplaced = val.split("$")[0].split("$")[0];
                        if (toBeReplaced.contains("{")) {
                            String entity = toBeReplaced.split("{")[0].split("}")[0];
                            String info = (String) dependencyGraph.getNodeWithID(entity).getStaticInformation().get(toBeReplaced.split("}")[1]);
                            val = val.replace("$" + toBeReplaced + "$", info);

                        }
                    }
                    val = val.replace(" ", "");
                    metadata.put(m.split("=")[0], val);
                }
            }
        }
        String ip = "";
        if (metadata != null) {
            if (metadata.containsKey("startup-script-url")) {
                ip = createVirtualMachine(toBeScaled.getId() + UUID.randomUUID(), metadata.get("startup-script-url"), metadata);
            }
        } else {
            ip = createVirtualMachine(toBeScaled.getId() + UUID.randomUUID());

        }
        Node node = new Node();
        Node artifact = null;
        Node container = null;

        if (toBeScaled.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.ARTIFACT) != null && toBeScaled.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.ARTIFACT).size() > 0) {
            artifact = toBeScaled.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.ARTIFACT).get(0);

            if (artifact.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.CONTAINER) != null && artifact.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.CONTAINER).size() > 0) {

                container = artifact.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.CONTAINER).get(0);
            }
        }
        if (!ip.equalsIgnoreCase("err") && !ip.equalsIgnoreCase("")) {
            node.getStaticInformation().put("IP", ip);
            node.setId(ip);
            node.setNodeType(Node.NodeType.VIRTUAL_MACHINE);
            SimpleRelationship rel = new SimpleRelationship();

            rel.setTargetElement(node.getId());
            rel.setType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP);
            RuntimeLogger.logger.info("Adding to " + toBeScaled.getId() + " vm with ip "
                    + ip);

            if (artifact == null && container == null) {
                rel.setSourceElement(toBeScaled.getId());
                toBeScaled.addNode(node, rel);
            } else {
                if (container == null) {
                    rel.setSourceElement(artifact.getId());
                    artifact.addNode(node, rel);
                } else {
                    rel.setSourceElement(container.getId());
                    container.addNode(node, rel);
                }
            }
            res = true;
        } else {
            res = false;
            RuntimeLogger.logger.info("IP is empty");
        }
        RuntimeLogger.logger.info("The controlled service is now "
                + cloudService.toString());

        monitoringAPI.refreshServiceStructure(cloudService);
        return res;
    }

    @Override
    public boolean scaleOut(Node toBeScaled, double violationDegree) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean scaleIn(Node toscale) {
        boolean res = false;
        DependencyGraph graph = new DependencyGraph();
        graph.setCloudService(cloudService);

        Node toBeScaled = graph.getNodeWithID(toscale.getId());
        Node toBeRemoved = graph.getNodeWithID(toscale.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.VIRTUAL_MACHINE).get(0).getId());
        RuntimeLogger.logger.info("Trying to remove  " + toBeRemoved.getId() + " From " + toBeScaled.getId());

        try {
            res = googleConnectionUtils.deleteInstance(toBeRemoved.getId());
        } catch (Exception ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
            res=false;
        }


        toBeScaled.removeNode(toBeRemoved);
        monitoringAPI.refreshServiceStructure(cloudService);

        return res;
    }
    @Override
    public boolean scaleIn(Node toBeScaled, double violationDegree) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getElasticityCapabilities() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setControlledService(Node controlledService) {
        cloudService = controlledService;
    }

    public List<String> listImages() {
        return googleConnectionUtils.listImages();
    }

    @Override
    public Node getControlledService() {
        return cloudService;
    }

    @Override
    public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
        monitoringAPI = monitoring;
    }

    @Override
    public boolean containsElasticityCapability(Node entity, String capability) {
        for (ElasticityCapabilityInformation ec : entity.getElasticityCapabilities()) {
            if (ec.getName().equalsIgnoreCase(capability)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void undeployService(Node serviceID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean enforceAction(Node serviceID, String actionName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void diagonallyScale(Node service, Strategy strategy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String createVirtualMachine(String name) {
        String ip = "";
        try {
            ip = googleConnectionUtils.startInstance(name, "", "", null);
        } catch (Exception ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }

    public String createVirtualMachine(String name, String pathToScript) {
        String ip = "";
        try {
            ip = googleConnectionUtils.startInstance(name, "", pathToScript, null);
        } catch (Exception ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }

    public String createVirtualMachine(String name, String pathToScript, HashMap<String, String> meta) {
        String ip = "";
        try {
            ip = googleConnectionUtils.startInstance(name, "", pathToScript, meta);
        } catch (Exception ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }

    public void listInstances() {
        try {
            googleConnectionUtils.printInstances();
        } catch (IOException ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteVirtualMachine(String name) {
        try {
            googleConnectionUtils.deleteInstance(name);
        } catch (Exception ex) {
            Logger.getLogger(GoogleComputeAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
