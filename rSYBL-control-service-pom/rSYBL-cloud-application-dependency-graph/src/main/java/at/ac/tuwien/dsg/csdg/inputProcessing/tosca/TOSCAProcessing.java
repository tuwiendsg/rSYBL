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
package at.ac.tuwien.dsg.csdg.inputProcessing.tosca;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.Definitions;
import org.oasis_open.docs.tosca.ns._2011._12.TBoundaryDefinitions;
import org.oasis_open.docs.tosca.ns._2011._12.TEntityTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TExtensibleElements;
import org.oasis_open.docs.tosca.ns._2011._12.TDefinitions.Extensions;
import org.oasis_open.docs.tosca.ns._2011._12.TDefinitions.Types;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate.Policies;
import org.oasis_open.docs.tosca.ns._2011._12.TPolicy;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TServiceTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TTopologyTemplate;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation.AnnotationType;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.IOUtils;

public class TOSCAProcessing {
    
    public Definitions readTOSCADescriptionsFile() {
        
        try {
            
            JAXBContext a = JAXBContext.newInstance(Definitions.class);
            Unmarshaller u = a.createUnmarshaller();
            Definitions def = (Definitions) u.unmarshal(new File("./tosca_sybl_example.xml"));

            // Definitions def = (Definitions) u.unmarshal( Definitions.class.getClassLoader().getResourceAsStream("./tosca_sybl_example.xml")) ;

            return def;
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    public Definitions readTOSCADescriptionsString(String tosca) {
        
        try {
            
            JAXBContext a = JAXBContext.newInstance(Definitions.class);
            Unmarshaller u = a.createUnmarshaller();
            //Definitions	def = (Definitions) u.unmarshal(new File(Configuration.getCloudServiceTOSCADescription()));

            Definitions def = (Definitions) u.unmarshal(new StringReader(tosca));
            
            return def;
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    private void setElasticityRequirementsForService(Node node, TBoundaryDefinitions reqs) {
        SYBLAnnotation annotation = new SYBLAnnotation();
        ElasticityRequirement elasticityRequirement = new ElasticityRequirement();
        
        for (TPolicy policy : reqs.getPolicies().getPolicy()) {
            
            switch (node.getNodeType()) {
                case CLOUD_SERVICE:
                    annotation.setAnnotationType(AnnotationType.CLOUD_SERVICE);
                    break;
                case SERVICE_TOPOLOGY:
                    annotation.setAnnotationType(AnnotationType.SERVICE_TOPOLOGY);
                    break;
                case SERVICE_UNIT:
                    annotation.setAnnotationType(AnnotationType.SERVICE_UNIT);
                    break;
                
            }
            switch (policy.getPolicyType().getLocalPart()) {
                case "SYBLConstraint":
                    annotation.setConstraints(annotation.getConstraints() + "; " + policy.getName());
                    break;
                case "SYBLStrategy":
                    annotation.setConstraints(annotation.getStrategies() + "; " + policy.getName());
                    break;
                case "SYBLMonitoring":
                    annotation.setConstraints(annotation.getMonitoring() + "; " + policy.getName());
                    break;
                
            }
            
        }
        annotation.setEntityID(node.getId());
        
        elasticityRequirement.setAnnotation(annotation);
        node.addElasticityRequirement(elasticityRequirement);
        System.out.println(annotation.getConstraints() + " " + annotation.getStrategies() + " " + annotation.getMonitoring());
    }
    
    private void setElasticityRequirements(Node node, TNodeTemplate.Policies policies) {
        SYBLAnnotation annotation = new SYBLAnnotation();
        ElasticityRequirement elasticityRequirement = new ElasticityRequirement();
        //System.out.println("Node "+node.getId());
        for (TPolicy policy : policies.getPolicy()) {
            //System.out.println(policy);
            switch (node.getNodeType()) {
                case CLOUD_SERVICE:
                    annotation.setAnnotationType(AnnotationType.CLOUD_SERVICE);
                    break;
                case SERVICE_TOPOLOGY:
                    annotation.setAnnotationType(AnnotationType.SERVICE_TOPOLOGY);
                    break;
                case SERVICE_UNIT:
                    annotation.setAnnotationType(AnnotationType.SERVICE_UNIT);
                    break;
                
            }
            switch (policy.getPolicyType().getLocalPart()) {
                case "Constraint":
                    if (annotation.getConstraints() != null) {
                        annotation.setConstraints(annotation.getConstraints() + "; " + policy.getPolicyRef()+":"+policy.getName());
                    } else {
                        annotation.setConstraints( policy.getPolicyRef()+":"+policy.getName());
                    }
                    break;
                case "Strategy":
                    if (annotation.getStrategies() != null) {
                        annotation.setStrategies(annotation.getStrategies() + "; " + policy.getPolicyRef()+":"+ policy.getName());
                    } else {
                        annotation.setStrategies( policy.getPolicyRef()+":"+policy.getName());
                    }
                    break;
                case "Monitoring":
                    if (annotation.getMonitoring()!=null){
                    annotation.setMonitoring(annotation.getMonitoring() + "; " + policy.getPolicyRef()+":"+ policy.getName());
                    }else{
                        annotation.setMonitoring( policy.getPolicyRef()+":"+policy.getName());
                    }
                    break;
                
            }
            
        }
        annotation.setEntityID(node.getId());
        
        elasticityRequirement.setAnnotation(annotation);
        node.addElasticityRequirement(elasticityRequirement);
       // System.out.println(annotation.getConstraints() + " " + annotation.getStrategies() + " " + annotation.getMonitoring());
    }
    
    public HashMap<String, Node> parseTOSCAGraph(HashMap<String, Node> nodes, List<TExtensibleElements> currentElements) {
        List<TExtensibleElements> c = new ArrayList<TExtensibleElements>();
        String cloudServiceName = "";
        for (TExtensibleElements extensibleElements : currentElements) {
            if (extensibleElements instanceof TServiceTemplate) {
                //System.out.println("Found of type service "+extensibleElements);
                Node n = new Node();
                TServiceTemplate serviceTemplate = (TServiceTemplate) extensibleElements;
                
                n.setId(serviceTemplate.getName());
                n.setNodeType(NodeType.CLOUD_SERVICE);
                if (serviceTemplate.getSubstitutableNodeType() != null) {
                    if (nodes.containsKey(serviceTemplate.getSubstitutableNodeType().getLocalPart())) {
                        n = nodes.get(serviceTemplate.getSubstitutableNodeType().getLocalPart());
                    }
                    n.setId(serviceTemplate.getSubstitutableNodeType().getLocalPart());
                    
                    n.setNodeType(NodeType.SERVICE_TOPOLOGY);
                    SimpleRelationship rel = new SimpleRelationship();
                    rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                    rel.setSourceElement(cloudServiceName);
                    rel.setTargetElement(n.getId());
                    if (!nodes.get(cloudServiceName).getAllRelatedNodes().contains(n)) {
                        nodes.get(cloudServiceName).addNode(n, rel);
                        nodes.put(n.getId(), n);
                    }
                    TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
                    if (topologyTemplate != null) {
                        for (TEntityTemplate tExt : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
                            if (tExt instanceof TNodeTemplate) {
                                TNodeTemplate nodeTemplate = (TNodeTemplate) tExt;
                                Node serviceUnit = null;
                                if (nodes.containsKey(nodeTemplate.getId())) {
                                    serviceUnit = nodes.get(nodeTemplate.getId());
                                } else {
                                    serviceUnit = new Node();
                                    serviceUnit.setId(nodeTemplate.getId());
                                    serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
                                    
                                }
                                //System.out.println(n+" "+nodeTemplate.getId());
                                //serviceUnit.setId(nodeTemplate.getId());
                                //	serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
                                rel = new SimpleRelationship();
                                rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                                n.addNode(serviceUnit, rel);
                                nodes.put(serviceUnit.getId(), serviceUnit);
                                if (nodeTemplate.getPolicies() != null) {
                                    setElasticityRequirements(serviceUnit, nodeTemplate.getPolicies());
                                }
                            } else {
                                if (tExt instanceof TRelationshipTemplate) {
                                    TRelationshipTemplate relationship = (TRelationshipTemplate) tExt;
                                    
                                }
                            }
                        }
                    }
                    
                    
                } else {                  
                    cloudServiceName = n.getId();
                   
                    
                    nodes.put(n.getId(), n);
                    TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
                     Node newServiceTemplate = new Node();
                    newServiceTemplate.setNodeType(NodeType.SERVICE_TOPOLOGY);
                    newServiceTemplate.setId("Composite Component");
                    SimpleRelationship newRelationship = new SimpleRelationship();
                    newRelationship.setSourceElement(cloudServiceName);
                    newRelationship.setTargetElement(newServiceTemplate.getId());
                    newRelationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                    n.addNode(newServiceTemplate, newRelationship);
                            
                    if (topologyTemplate != null) {
                        for (TEntityTemplate tExt : topologyTemplate.getNodeTemplateOrRelationshipTemplate()) {
//							if (tExt.getType()!=null){
//								for (String node:nodes.keySet()){
//									if (tExt.getType().getLocalPart().equalsIgnoreCase(node)){
//																		
//										if (((TNodeTemplate)tExt).getPolicies()!=null)
//											setElasticityRequirements(nodes.get(node),((TNodeTemplate)tExt).getPolicies());
//									
//									}
//								}
//								
//							}else
                            if (tExt instanceof TNodeTemplate) {
                                TNodeTemplate nodeTemplate = (TNodeTemplate) tExt;
                                Node serviceUnit = null;
                                if (nodes.containsKey(nodeTemplate.getId())) {
                                    serviceUnit = nodes.get(nodeTemplate.getId());
                                } else {
                                    if (tExt.getType() != null) {
                                        serviceUnit = new Node();
                                        serviceUnit.setId(tExt.getType().getLocalPart());
                                        serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);
                                    } else {
                                        serviceUnit = new Node();
                                        serviceUnit.setId(nodeTemplate.getId());
                                        serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
                                    }
                                }
                                //serviceUnit.setId(nodeTemplate.getId());
                                //serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
                                SimpleRelationship rel = new SimpleRelationship();
                                rel.setSourceElement(newServiceTemplate.getId());
                                rel.setTargetElement(serviceUnit.getId());
                                rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                                newServiceTemplate.addNode(serviceUnit, rel);
                                nodes.put(serviceUnit.getId(), serviceUnit);
                                if (nodeTemplate.getPolicies() != null) {
                                    setElasticityRequirements(serviceUnit, nodeTemplate.getPolicies());
                                }
                            } else {
                                if (tExt instanceof TRelationshipTemplate) {
                                    TRelationshipTemplate relationship = (TRelationshipTemplate) tExt;
                                    
                                }
                            }
                        }
                    }
                }
                
                if (serviceTemplate.getBoundaryDefinitions() != null && serviceTemplate.getBoundaryDefinitions().getPolicies() != null) {
                    setElasticityRequirementsForService(n, serviceTemplate.getBoundaryDefinitions());
                }
            }
            
            
        }
        
        
        return nodes;
    }
    
    public DependencyGraph toscaDescriptionToDependencyGraph() {
        DependencyGraph dependencyGraph = new DependencyGraph();
        HashMap<String, Node> nodes = new HashMap<String, Node>();//String - id of the node, for easier access and modification of its relationships
        SimpleRelationship rel = new SimpleRelationship();
        //TODO: take each construct present in TOSCA and transform it to our model
        Definitions definitions = readTOSCADescriptionsFile();
        parseTOSCAGraph(nodes, definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation());
        for (Node n : nodes.values()) {
            if (n.getNodeType() == NodeType.CLOUD_SERVICE) {
                dependencyGraph.setCloudService(n);
            }
        }
        return dependencyGraph;
    }
    
    public DependencyGraph toscaDescriptionToDependencyGraph(String tosca) {
        DependencyGraph dependencyGraph = new DependencyGraph();
        HashMap<String, Node> nodes = new HashMap<String, Node>();//String - id of the node, for easier access and modification of its relationships
        SimpleRelationship rel = new SimpleRelationship();
        //TODO: take each construct present in TOSCA and transform it to our model
        Definitions definitions = readTOSCADescriptionsString(tosca);
        TPolicy policy = new TPolicy();
        
        parseTOSCAGraph(nodes, definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation());
        for (Node n : nodes.values()) {
            if (n.getNodeType() == NodeType.CLOUD_SERVICE) {
                dependencyGraph.setCloudService(n);
            }
        }
        return dependencyGraph;
    }
    
    public static void main(String[] args) {
        String content = null;
        File file = new File("application.tosca"); //for ex foo.txt
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
            TOSCAProcessing tOSCAProcessing = new TOSCAProcessing();
            System.out.println(tOSCAProcessing.toscaDescriptionToDependencyGraph(content).graphToString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
