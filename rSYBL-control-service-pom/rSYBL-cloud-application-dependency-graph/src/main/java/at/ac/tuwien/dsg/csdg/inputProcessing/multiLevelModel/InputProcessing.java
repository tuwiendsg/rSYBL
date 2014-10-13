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
package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.csdg.DataElasticityDependency;
import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.InstantiationElasticityDependency;
import at.ac.tuwien.dsg.csdg.LoadElasticityDependency;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.PolynomialElasticityRelationship;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityMetric;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation.AnnotationType;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.LinearRelationshipXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.RelationshipXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLAnnotationXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.Artifact;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.AssociatedVM;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.Container;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentUnit;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitivesDescription;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputProcessing {

    private SYBLElasticityRequirementsDescription syblSpecifications;
    private DeploymentDescription deploymentDescription;
    private CloudServiceXML cloudServiceXML;
    private DependencyGraph graph;

    private void loadDeploymentDescriptionFromFile() {
        try {

            JAXBContext a = JAXBContext.newInstance(DeploymentDescription.class);
            Unmarshaller u = a.createUnmarshaller();
            String deploymentDescriptionPath = Configuration.getDeploymentDescriptionPath();

            if (deploymentDescriptionPath != null) {
                deploymentDescription = (DeploymentDescription) u.unmarshal(InputProcessing.class.getClassLoader().getResourceAsStream(deploymentDescriptionPath));
            }

        } catch (Exception e) {
            DependencyGraphLogger.logger.error("Error in reading deployment description" + e.toString());
            e.printStackTrace();
        }

    }

    private void loadDeploymentDescriptionFromString(String deploymentDescr) {
        try {
            JAXBContext a = JAXBContext.newInstance(DeploymentDescription.class);
            Unmarshaller u = a.createUnmarshaller();
            deploymentDescription = (DeploymentDescription) u.unmarshal(new StringReader(deploymentDescr));
            // DependencyGraphLogger.logger.info("Read deployment Description" + deploymentDescription.toString());

        } catch (Exception e) {
            DependencyGraphLogger.logger.error("Error in reading deployment description" + e.toString());
            e.printStackTrace();
        }

    }

    private void loadModelFromFile() {
        JAXBContext jc;
        cloudServiceXML = null;
        try {
            jc = JAXBContext.newInstance(CloudServiceXML.class);
            Unmarshaller u = jc.createUnmarshaller();
            cloudServiceXML = (CloudServiceXML) u.unmarshal(this.getClass().getClassLoader().getResourceAsStream(Configuration.getModelDescrFile()));
        } catch (JAXBException e) {
            e.printStackTrace();
        }


        try {
            JAXBContext a = JAXBContext.newInstance(SYBLElasticityRequirementsDescription.class);
            Unmarshaller u = a.createUnmarshaller();
            String directivePath = Configuration.getDirectivesPath();
            if (directivePath != null) {
                syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal(this.getClass().getClassLoader().getResourceAsStream(directivePath));
            }
            for (SYBLAnnotation syblAnnotation : parseXMLInjectedAnnotations(cloudServiceXML)) {
                if (syblSpecifications == null) {
                    syblSpecifications = new SYBLElasticityRequirementsDescription();
                }
                syblSpecifications.getSyblSpecifications().add(SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(syblAnnotation));
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    private void loadModelFromString(String applicationDescr, String elasticityReq) {
        JAXBContext jc;
        cloudServiceXML = null;
        try {
            jc = JAXBContext.newInstance(CloudServiceXML.class);
            Unmarshaller u = jc.createUnmarshaller();

            cloudServiceXML = (CloudServiceXML) u.unmarshal(new StringReader(applicationDescr));

        } catch (JAXBException e) {
            DependencyGraphLogger.logger.info("Error unmarshalling cloud service" + e.getStackTrace().toString());
        }

        try {
            JAXBContext a = JAXBContext.newInstance(SYBLElasticityRequirementsDescription.class);
            Unmarshaller u = a.createUnmarshaller();
            if (!elasticityReq.equalsIgnoreCase("")) {
                syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal(new StringReader(elasticityReq));
            }
            //DependencyGraphLogger.logger.info(parseXMLInjectedAnnotations(cloudServiceXML).size());
            //for (SYBLAnnotation syblAnnotation:parseXMLInjectedAnnotations(cloudServiceXML)){

            //if (syblSpecifications==null)
            //	syblSpecifications=new SYBLElasticityRequirementsDescription();
            //SYBLDirectivesEnforcementLogger.logger.info("FOUND HERE THE STRATEGY "+syblAnnotation.getStrategies());
            //if (syblAnnotation.getStrategies()!=null && syblAnnotation.getStrategies()!="")
            //	syblSpecifications.getSyblSpecifications().add(SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(syblAnnotation));
            // }
        } catch (JAXBException e) {
            DependencyGraphLogger.logger.error("Error unmarshalling specifications" + e.getStackTrace().toString());
        }

    }

    public PolynomialElasticityRelationship mapToPolynomialRel(LinearRelationshipXML linearRelationshipXML) {
        PolynomialElasticityRelationship elasticityRelationship = new PolynomialElasticityRelationship();
        List<PolynomialElasticityRelationship.Monom> polynom = new ArrayList<PolynomialElasticityRelationship.Monom>();
        elasticityRelationship.setConfidence(linearRelationshipXML.getConfidence());
        elasticityRelationship.setCurrentElasticityMetric(linearRelationshipXML.getMetricName());
        elasticityRelationship.setServicePartID(linearRelationshipXML.getServicePartID());

        for (LinearRelationshipXML.Monom m : linearRelationshipXML.getDependencyMonoms()) {
            PolynomialElasticityRelationship.Monom monom = elasticityRelationship.new Monom();
            monom.setCoefficient(m.getCoefficient());
            monom.setPower(m.getPower());

            monom.setElasticityMetric(m.getMetricName());
            monom.setServicePartID(m.getServicePartID());
            polynom.add(monom);

        }
        elasticityRelationship.setPolynom(polynom);
        return elasticityRelationship;
    }

    public DependencyGraph replaceRequirements(DependencyGraph graph, String xmlRequirements) {
        for (Node n : graph.getAllServiceUnits()) {
            n.setElasticityRequirements(new ArrayList<ElasticityRequirement>());
        }
        for (Node n : graph.getAllServiceTopologies()) {
            n.setElasticityRequirements(new ArrayList<ElasticityRequirement>());
        }
        graph.getCloudService().setElasticityRequirements(new ArrayList<ElasticityRequirement>());
        try {
            JAXBContext a = JAXBContext.newInstance(SYBLElasticityRequirementsDescription.class);
            Unmarshaller u = a.createUnmarshaller();

            syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal(new StringReader(xmlRequirements));
            if (syblSpecifications != null && syblSpecifications.getSyblSpecifications() != null) {
                for (SYBLSpecification specification : syblSpecifications.getSyblSpecifications()) {
                    ElasticityRequirement elRequirement = new ElasticityRequirement();
                    elRequirement.setAnnotation(SYBLDirectiveMappingFromXML.mapFromXMLRepresentation(specification));
                    if (graph.getNodeWithID(specification.getComponentId()) != null) {
                        graph.getNodeWithID(specification.getComponentId()).addElasticityRequirement(elRequirement);
                    } else {
                        DependencyGraphLogger.logger.error("Specification targets entity which is not found: " + specification.getComponentId());
                    }
                }
            }

        } catch (Exception e) {
            DependencyGraphLogger.logger.error(e.getStackTrace().toString());
        }
        return graph;
    }

    public DependencyGraph replaceCloudServiceRequirements(DependencyGraph graph, String cloudService) {
        JAXBContext jc;
        cloudServiceXML = null;
        for (Node n : graph.getAllServiceUnits()) {
            n.setElasticityRequirements(new ArrayList<ElasticityRequirement>());
        }
        for (Node n : graph.getAllServiceTopologies()) {
            n.setElasticityRequirements(new ArrayList<ElasticityRequirement>());
        }
        graph.getCloudService().setElasticityRequirements(new ArrayList<ElasticityRequirement>());

        try {
            jc = JAXBContext.newInstance(CloudServiceXML.class);
            Unmarshaller u = jc.createUnmarshaller();

            //JAXBElement element=  u.unmarshal( new File(Configuration.getModelDescrFile()));
            // cloudS = (CloudServiceXML) element.getValue();
            cloudServiceXML = (CloudServiceXML) u.unmarshal(new StringReader(cloudService));
            //File f = new File(Configuration.getModelDescrFile());
            //cloudServiceXML = (CloudServiceXML) u.unmarshal(new File(Configuration.getModelDescrFile()));
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            DependencyGraphLogger.logger.info(e.getStackTrace().toString());
        }
        if (cloudServiceXML.getXMLAnnotation() != null) {
            ElasticityRequirement elReq = new ElasticityRequirement();
            elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(graph.getCloudService().getId(), cloudServiceXML.getXMLAnnotation(), AnnotationType.CLOUD_SERVICE));
            graph.getCloudService().getElasticityRequirements().add(elReq);
        }

        List<ServiceTopologyXML> remainingServiceTopologies = new ArrayList<ServiceTopologyXML>();
        for (ServiceTopologyXML serviceTopologyXML : cloudServiceXML.getServiceTopologies()) {
            remainingServiceTopologies.add(serviceTopologyXML);
            ServiceTopologyXML firstServTopology = serviceTopologyXML;
            if (firstServTopology.getXMLAnnotation() != null) {
                ElasticityRequirement elReq = new ElasticityRequirement();
                elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyXML.getId(), firstServTopology.getXMLAnnotation(), AnnotationType.SERVICE_TOPOLOGY));
                graph.getNodeWithID(serviceTopologyXML.getId()).getElasticityRequirements().add(elReq);
            }
        }

        while (!remainingServiceTopologies.isEmpty()) {
            ServiceTopologyXML serviceTopologyXML = remainingServiceTopologies.get(0);
            if (serviceTopologyXML.getServiceUnits() != null && !serviceTopologyXML.getServiceUnits().isEmpty()) {
                for (ServiceUnitXML serviceUnitXML : serviceTopologyXML.getServiceUnits()) {
                    if (serviceUnitXML.getXMLAnnotation() != null) {
                        ElasticityRequirement elReq = new ElasticityRequirement();
                        elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceUnitXML.getId(), serviceUnitXML.getXMLAnnotation(), AnnotationType.SERVICE_UNIT));
                        graph.getNodeWithID(serviceUnitXML.getId()).getElasticityRequirements().add(elReq);
                    }
                }
            }

            if (serviceTopologyXML.getServiceTopology() != null && !serviceTopologyXML.getServiceTopology().isEmpty()) {
                for (ServiceTopologyXML serviceTopologyXML2 : serviceTopologyXML.getServiceTopology()) {

                    if (serviceTopologyXML2.getXMLAnnotation() != null) {
                        ElasticityRequirement elReq = new ElasticityRequirement();
                        elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyXML2.getId(), serviceTopologyXML2.getXMLAnnotation(), AnnotationType.SERVICE_TOPOLOGY));
                        graph.getNodeWithID(serviceTopologyXML2.getId()).getElasticityRequirements().add(elReq);
                    }

                    remainingServiceTopologies.add(serviceTopologyXML2);
                }
            }
            remainingServiceTopologies.remove(0);
        }
        DependencyGraphLogger.logger.info("The current graph is  " + graph.graphToString());
        for (ElasticityRequirement req : graph.getAllElasticityRequirements()) {
            DependencyGraphLogger.logger.info("Requirement set is " + req.getAnnotation().getConstraints() + " " + req.getAnnotation().getStrategies());

        }
        return graph;
    }

    public List<SimpleRelationship> evaluateRelationships(ServiceTopologyXML serviceTopologyXML) {
        List<SimpleRelationship> resRelationships = new ArrayList<SimpleRelationship>();
        for (RelationshipXML relationshipXML : serviceTopologyXML.getRelationships()) {

            if (relationshipXML.getType() != null && relationshipXML.getType().toLowerCase().contains("data")) {
                DataElasticityDependency dataElasticityDependency = new DataElasticityDependency();

                dataElasticityDependency.setSourceElement(relationshipXML.getSource());
                dataElasticityDependency.setTargetElement(relationshipXML.getTarget());
                dataElasticityDependency.setDataMeasurementSource(relationshipXML.getMetricSource());
                dataElasticityDependency.setDataMeasurementTarget(relationshipXML.getMetricTarget());
                
                ElasticityRequirement requirement = new ElasticityRequirement();
                if (relationshipXML.getSyblAnnotationXML() != null) {
                    requirement.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(relationshipXML.getRelationshipID(), relationshipXML.getSyblAnnotationXML(), AnnotationType.RELATIONSHIP));
                }
                dataElasticityDependency.setRequirement(requirement);
                dataElasticityDependency.setId(relationshipXML.getRelationshipID());
                dataElasticityDependency.setType(RelationshipType.DATA);
                resRelationships.add(dataElasticityDependency);

            }
            if (relationshipXML.getType() != null && relationshipXML.getType().toLowerCase().contains("instant")) {
                InstantiationElasticityDependency instantiationElasticityDependency = new InstantiationElasticityDependency();

                instantiationElasticityDependency.setSourceElement(relationshipXML.getSource());
                instantiationElasticityDependency.setTargetElement(relationshipXML.getTarget());
                instantiationElasticityDependency.setFocusMetric(relationshipXML.getFocusMetric());
                ElasticityRequirement requirement = new ElasticityRequirement();
                if (relationshipXML.getSyblAnnotationXML() != null) {
                    requirement.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(relationshipXML.getRelationshipID(), relationshipXML.getSyblAnnotationXML(), AnnotationType.RELATIONSHIP));
                }
                instantiationElasticityDependency.setRequirement(requirement);
                instantiationElasticityDependency.setType(RelationshipType.INSTANTIATION);

                instantiationElasticityDependency.setId(relationshipXML.getRelationshipID());
                resRelationships.add(instantiationElasticityDependency);
            }
            if (relationshipXML.getType() != null && relationshipXML.getType().toLowerCase().contains("load")) {
                LoadElasticityDependency loadElasticityDependency = new LoadElasticityDependency();

                loadElasticityDependency.setSourceElement(relationshipXML.getSource());
                loadElasticityDependency.setTargetElement(relationshipXML.getTarget());
                loadElasticityDependency.setSourceLoadMetric(relationshipXML.getMetricSource());
                loadElasticityDependency.setTargetLoadMetric(relationshipXML.getMetricTarget());
                ElasticityRequirement requirement = new ElasticityRequirement();
                if (relationshipXML.getSyblAnnotationXML() != null) {
                    requirement.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(relationshipXML.getRelationshipID(), relationshipXML.getSyblAnnotationXML(), AnnotationType.RELATIONSHIP));
                }
                loadElasticityDependency.setRequirement(requirement);
                loadElasticityDependency.setType(RelationshipType.LOAD);
                loadElasticityDependency.setId(relationshipXML.getRelationshipID());
                resRelationships.add(loadElasticityDependency);
            }
        }
        return resRelationships;
    }

    public DependencyGraph constructDependencyGraph() {
        graph = new DependencyGraph();
        Node cloudService = new Node();
        cloudService.setId(cloudServiceXML.getId());
        cloudService.setNodeType(NodeType.CLOUD_SERVICE);
        List<SimpleRelationship> specialRelationships = new ArrayList<SimpleRelationship>();
        if (cloudServiceXML.getXMLAnnotation() != null) {
            ElasticityRequirement elReq = new ElasticityRequirement();
            elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(cloudService.getId(), cloudServiceXML.getXMLAnnotation(), AnnotationType.CLOUD_SERVICE));
            cloudService.getElasticityRequirements().add(elReq);

        }
        if (cloudServiceXML.getLinearRelationships() != null && cloudServiceXML.getLinearRelationships().size() > 0) {
            for (LinearRelationshipXML linearRelationshipXML : cloudServiceXML.getLinearRelationships()) {
                ElasticityMetric elasticityMetric = new ElasticityMetric();
                elasticityMetric.setMetricName(linearRelationshipXML.getMetricName());
                elasticityMetric.setServicePartID(linearRelationshipXML.getServicePartID());
                elasticityMetric.addRelationship(mapToPolynomialRel(linearRelationshipXML));

                cloudService.addElasticityMetric(elasticityMetric);
            }
        }
        List<ServiceTopologyXML> remainingServiceTopologies = new ArrayList<ServiceTopologyXML>();
        HashMap<String, Node> nodes = new HashMap<String, Node>();
        HashMap<Node, SimpleRelationship> serviceTopologiesFirst = new HashMap<Node, SimpleRelationship>();
        for (ServiceTopologyXML serviceTopologyXML : cloudServiceXML.getServiceTopologies()) {
            remainingServiceTopologies.add(serviceTopologyXML);
            ServiceTopologyXML firstServTopology = serviceTopologyXML;

            Node serviceTopologyFirst = new Node();
            serviceTopologyFirst.setId(firstServTopology.getId());
            serviceTopologyFirst.setNodeType(NodeType.SERVICE_TOPOLOGY);
            SimpleRelationship rel = new SimpleRelationship();

            specialRelationships.addAll(evaluateRelationships(serviceTopologyXML));
            rel.setSourceElement(cloudService.getId());
            rel.setTargetElement(serviceTopologyFirst.getId());
            rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
            if (firstServTopology.getXMLAnnotation() != null) {
                ElasticityRequirement elReq = new ElasticityRequirement();
                elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyFirst.getId(), firstServTopology.getXMLAnnotation(), AnnotationType.SERVICE_TOPOLOGY));
                serviceTopologyFirst.getElasticityRequirements().add(elReq);
            }


            if (firstServTopology.getLinearRelationships() != null && firstServTopology.getLinearRelationships().size() > 0) {
                for (LinearRelationshipXML linearRelationshipXML : firstServTopology.getLinearRelationships()) {
                    ElasticityMetric elasticityMetric = new ElasticityMetric();
                    elasticityMetric.setMetricName(linearRelationshipXML.getMetricName());
                    elasticityMetric.setServicePartID(linearRelationshipXML.getServicePartID());
                    elasticityMetric.addRelationship(mapToPolynomialRel(linearRelationshipXML));
                    serviceTopologyFirst.addElasticityMetric(elasticityMetric);
                }
            }

            nodes.put(serviceTopologyFirst.getId(), serviceTopologyFirst);
            nodes.put(cloudService.getId(), cloudService);
            serviceTopologiesFirst.put(serviceTopologyFirst, rel);
        }
        while (!remainingServiceTopologies.isEmpty()) {
            ServiceTopologyXML serviceTopologyXML = remainingServiceTopologies.get(0);
            Node serviceTopology = new Node();
            if (nodes.containsKey(serviceTopologyXML.getId())) {
                serviceTopology = nodes.get(serviceTopologyXML.getId());
            }

            if (serviceTopologyXML.getServiceUnits() != null && !serviceTopologyXML.getServiceUnits().isEmpty()) {
                for (ServiceUnitXML serviceUnitXML : serviceTopologyXML.getServiceUnits()) {
                    Node serviceUnit = new Node();
                    serviceUnit.setId(serviceUnitXML.getId());
                    serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
                    SimpleRelationship rel = new SimpleRelationship();
                    rel.setSourceElement(serviceTopology.getId());
                    rel.setTargetElement(serviceUnit.getId());
                    rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                    if (serviceUnitXML.getXMLAnnotation() != null) {
                        ElasticityRequirement elReq = new ElasticityRequirement();
                        elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceUnitXML.getId(), serviceUnitXML.getXMLAnnotation(), AnnotationType.SERVICE_UNIT));
                        serviceUnit.getElasticityRequirements().add(elReq);
                    }
                    if (serviceUnitXML.getLinearRelationships() != null && serviceUnitXML.getLinearRelationships().size() > 0) {
                        for (LinearRelationshipXML linearRelationshipXML : serviceUnitXML.getLinearRelationships()) {
                            ElasticityMetric elasticityMetric = new ElasticityMetric();
                            elasticityMetric.setMetricName(linearRelationshipXML.getMetricName());
                            elasticityMetric.setServicePartID(linearRelationshipXML.getServicePartID());
                            elasticityMetric.addRelationship(mapToPolynomialRel(linearRelationshipXML));
                            serviceUnit.addElasticityMetric(elasticityMetric);

                        }
                    }

//                    if (serviceUnitXML.getActions() != null && !serviceUnitXML.getActions().isEmpty()) {
//                        for (ActionXML actionXML : serviceUnitXML.getActions()) {
//                            ElasticityCapability elCapability = new ElasticityCapability();
//                            elCapability.setApiMethod(actionXML.getApiMethod());
//                            elCapability.setName(actionXML.getName());
//                            elCapability.setParameter(actionXML.getParameter());
//
//                            elCapability.setCallType(actionXML.getCallType());
//                            serviceUnit.addElasticityCapability(elCapability);
//                        }
//
//                    }
                    serviceTopology.addNode(serviceUnit, rel);
                    nodes.put(serviceUnit.getId(), serviceUnit);

                }
            }
            if (serviceTopologyXML.getServiceTopology() != null && !serviceTopologyXML.getServiceTopology().isEmpty()) {
                for (ServiceTopologyXML serviceTopologyXML2 : serviceTopologyXML.getServiceTopology()) {
                    Node serviceTopology2 = new Node();
                    serviceTopology2.setId(serviceTopologyXML2.getId());
                    serviceTopology2.setNodeType(NodeType.SERVICE_TOPOLOGY);
                    SimpleRelationship rel = new SimpleRelationship();
                    rel.setSourceElement(serviceTopology.getId());
                    rel.setTargetElement(serviceTopology2.getId());
                    rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
                    if (serviceTopologyXML2.getXMLAnnotation() != null) {
                        ElasticityRequirement elReq = new ElasticityRequirement();
                        elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyXML2.getId(), serviceTopologyXML2.getXMLAnnotation(), AnnotationType.SERVICE_TOPOLOGY));
                        serviceTopology2.getElasticityRequirements().add(elReq);
                    }
                    if (serviceTopologyXML2.getLinearRelationships() != null && serviceTopologyXML2.getLinearRelationships().size() > 0) {
                        for (LinearRelationshipXML linearRelationshipXML : serviceTopologyXML2.getLinearRelationships()) {
                            ElasticityMetric elasticityMetric = new ElasticityMetric();
                            elasticityMetric.setMetricName(linearRelationshipXML.getMetricName());
                            elasticityMetric.setServicePartID(linearRelationshipXML.getServicePartID());
                            elasticityMetric.addRelationship(mapToPolynomialRel(linearRelationshipXML));
                            serviceTopology2.addElasticityMetric(elasticityMetric);
                        }
                    }

                    serviceTopology.addNode(serviceTopology2, rel);
                    nodes.put(serviceTopology2.getId(), serviceTopology2);
                    remainingServiceTopologies.add(serviceTopologyXML2);
                }
            }
            nodes.put(serviceTopology.getId(), serviceTopology);
            remainingServiceTopologies.remove(0);
        }

        for (Entry<Node, SimpleRelationship> entry : serviceTopologiesFirst.entrySet()) {
            cloudService.addNode(entry.getKey(), entry.getValue());
        }

        graph.setCloudService(cloudService);
        for (SimpleRelationship rel : specialRelationships) {
            graph.getNodeWithID(rel.getSourceElement()).addNode(graph.getNodeWithID(rel.getTargetElement()), rel);
        }
        for (Relationship rel : graph.findAllElasticityRelationshipsAssociatedToMetrics()) {
            PolynomialElasticityRelationship elasticityRelationship = (PolynomialElasticityRelationship) rel;

            graph.getNodeWithID(elasticityRelationship.getServicePartID()).addNodes(graph.findAllRelatedNodesForPolynomialRel(elasticityRelationship), elasticityRelationship);
        }

        DependencyGraphLogger.logger.info("Deployment Description " + deploymentDescription.getAccessIP());
        cloudService.getStaticInformation().put("AccessIP", deploymentDescription.getAccessIP());
        //

        //Populate with deployment information
        for (DeploymentUnit deploymentUnit : deploymentDescription.getDeployments()) {

            Node node = graph.getNodeWithID(deploymentUnit.getServiceUnitID());
            if (node != null) {
                if (deploymentUnit.getDefaultFlavor() != null) {
                    node.getStaticInformation().put("DefaultFlavor", deploymentUnit.getDefaultFlavor());
                }
                if (deploymentUnit.getDefaultImage() != null) {
                    node.getStaticInformation().put("DefaultImage", deploymentUnit.getDefaultImage());
                }

                if (deploymentUnit.getArtifacts() != null && deploymentUnit.getArtifacts().size() > 0) {
                    for (Artifact artifact : deploymentUnit.getArtifacts()) {
                        Node artifactNode = new Node();
                        artifactNode.setNodeType(NodeType.ARTIFACT);
                        artifactNode.setId(artifact.getName());
                        artifactNode.getStaticInformation().put("Name", artifact.getName());
                        artifactNode.getStaticInformation().put("Path", artifact.getPath());
                        artifactNode.getStaticInformation().put("DownloadPath", artifact.getDownloadPath());
                        if (artifact.getContainer() != null) {
                            Node containerNode = new Node();

                            Container container = artifact.getContainer();
                            containerNode.setNodeType(NodeType.CONTAINER);
                            containerNode.setId(container.getId());
                            containerNode.getStaticInformation().put("Name", container.getName());
                            containerNode.getStaticInformation().put("Path", container.getPath());
                            AssociatedVM associatedVM = container.getVm();
                            if (associatedVM != null) {
                                Node vmNode = new Node();
                                vmNode.setId(associatedVM.getIp());
                                vmNode.getStaticInformation().put("UUID", associatedVM.getUuid());
                                vmNode.setNodeType(NodeType.VIRTUAL_MACHINE);
                                SimpleRelationship vmRel = new SimpleRelationship();
                                vmRel.setSourceElement(artifactNode.getId());
                                vmRel.setTargetElement(vmNode.getId());
                                vmRel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                                containerNode.addNode(vmNode, vmRel);
                            }
                            SimpleRelationship containerRel = new SimpleRelationship();
                            containerRel.setSourceElement(artifactNode.getId());
                            containerRel.setTargetElement(containerNode.getId());
                            containerRel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                            artifactNode.addNode(containerNode, containerRel);
                            for (at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.ElasticityCapability elasticityCapability : container.getElasticityCapabilities()) {
                                ElasticityCapability newElasticityCapability = new ElasticityCapability();
                                newElasticityCapability.setServicePartID(deploymentUnit.getServiceUnitID());
                                newElasticityCapability.setName(elasticityCapability.getName());
                                newElasticityCapability.setCallType(elasticityCapability.getType());
                                newElasticityCapability.setEndpoint(elasticityCapability.getScript());
                                newElasticityCapability.setPrimitiveOperations(elasticityCapability.getPrimitiveOperations());
                                containerNode.addElasticityCapability(newElasticityCapability);
                            }
                        } else {
                            if (artifact.getAssociatedVM() != null) {
                                AssociatedVM associatedVM = artifact.getAssociatedVM();
                                Node vmNode = new Node();
                                vmNode.setId(associatedVM.getIp());
                                vmNode.getStaticInformation().put("UUID", associatedVM.getUuid());
                                vmNode.setNodeType(NodeType.VIRTUAL_MACHINE);
                                SimpleRelationship vmRel = new SimpleRelationship();
                                vmRel.setSourceElement(artifactNode.getId());
                                vmRel.setTargetElement(vmNode.getId());
                                vmRel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                                artifactNode.addNode(vmNode, vmRel);
                            }
                            for (at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.ElasticityCapability elasticityCapability : artifact.getElasticityCapabilities()) {
                                ElasticityCapability newElasticityCapability = new ElasticityCapability();
                                                                newElasticityCapability.setServicePartID(deploymentUnit.getServiceUnitID());

                                newElasticityCapability.setName(elasticityCapability.getName());
                                newElasticityCapability.setCallType(elasticityCapability.getType());
                                newElasticityCapability.setEndpoint(elasticityCapability.getScript());
                                newElasticityCapability.setPrimitiveOperations(elasticityCapability.getPrimitiveOperations());
                                artifactNode.addElasticityCapability(newElasticityCapability);
                            }
                        }

                        SimpleRelationship artifactRel = new SimpleRelationship();
                        artifactRel.setSourceElement(node.getId());
                        artifactRel.setTargetElement(artifactNode.getId());
                        artifactRel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                        node.addNode(artifactNode, artifactRel);
                    }
                }
                if (deploymentUnit.getAssociatedVMs() != null) {
                    for (AssociatedVM associatedVM : deploymentUnit.getAssociatedVMs()) {
                        Node vmNode = new Node();
                        vmNode.setId(associatedVM.getIp());

                        vmNode.getStaticInformation().put("UUID", associatedVM.getUuid());
                        vmNode.setNodeType(NodeType.VIRTUAL_MACHINE);
                        SimpleRelationship vmRel = new SimpleRelationship();
                        vmRel.setSourceElement(node.getId());
                        vmRel.setTargetElement(vmNode.getId());
                        vmRel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                        node.addNode(vmNode, vmRel);
                        for (at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.ElasticityCapability elasticityCapability : associatedVM.getElasticityCapabilities()) {
                                ElasticityCapability newElasticityCapability = new ElasticityCapability();
                                                                newElasticityCapability.setServicePartID(deploymentUnit.getServiceUnitID());

                                newElasticityCapability.setName(elasticityCapability.getName());
                                newElasticityCapability.setCallType(elasticityCapability.getType());
                                newElasticityCapability.setEndpoint(elasticityCapability.getScript());
                                newElasticityCapability.setPrimitiveOperations(elasticityCapability.getPrimitiveOperations());
                                vmNode.addElasticityCapability(newElasticityCapability);
                            }
                    }
                }
                for (at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.ElasticityCapability elasticityCapability : deploymentUnit.getElasticityCapabilities()) {
                    ElasticityCapability newElasticityCapability = new ElasticityCapability();
                                                    newElasticityCapability.setServicePartID(deploymentUnit.getServiceUnitID());

                    newElasticityCapability.setName(elasticityCapability.getName());
                    newElasticityCapability.setCallType(elasticityCapability.getType());
                    newElasticityCapability.setEndpoint(elasticityCapability.getScript());
                    newElasticityCapability.setPrimitiveOperations(elasticityCapability.getPrimitiveOperations());
                    node.addElasticityCapability(newElasticityCapability);
                }


            } else {
                DependencyGraphLogger.logger.error("Cannot find node " + deploymentUnit.getServiceUnitID() + ". Current graph is " + graph.graphToString());

            }

        }

        //Populate with elasticity requirements information
        if (syblSpecifications != null && syblSpecifications.getSyblSpecifications() != null) {
            for (SYBLSpecification specification : syblSpecifications.getSyblSpecifications()) {
                ElasticityRequirement elRequirement = new ElasticityRequirement();
                elRequirement.setAnnotation(SYBLDirectiveMappingFromXML.mapFromXMLRepresentation(specification));
                if (graph.getNodeWithID(specification.getComponentId()) != null) {
                    graph.getNodeWithID(specification.getComponentId()).addElasticityRequirement(elRequirement);
                } else {
                    DependencyGraphLogger.logger.error("Specification targets entity which is not found: " + specification.getComponentId());
                }
            }
        }

        return graph;
    }

    public ElasticityPrimitivesDescription loadElasticityPrimitivesDescriptionFromFile() {
        JAXBContext jc;
        ElasticityPrimitivesDescription elasticityPrimitivesDescription = null;
        try {
            jc = JAXBContext.newInstance(ElasticityPrimitivesDescription.class);
            Unmarshaller u = jc.createUnmarshaller();

            elasticityPrimitivesDescription = (ElasticityPrimitivesDescription) u.unmarshal(this.getClass().getClassLoader().getResourceAsStream(Configuration.getPrimitivesDescriptionFile()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            try {
                jc = JAXBContext.newInstance(ElasticityPrimitivesDescription.class);

                Unmarshaller u = jc.createUnmarshaller();

                File f = new File(Configuration.getPrimitivesDescriptionFile());
                elasticityPrimitivesDescription = (ElasticityPrimitivesDescription) u.unmarshal(new FileInputStream(Configuration.getPrimitivesDescriptionFile()));

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return elasticityPrimitivesDescription;
    }

    public ElasticityPrimitivesDescription elasticityPrimitivesDescription(String elasticityPrimitives) {
        ElasticityPrimitivesDescription elasticityPrimitivesDescription = null;
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(ElasticityPrimitivesDescription.class);
            Unmarshaller u = jc.createUnmarshaller();
            try {
                elasticityPrimitivesDescription = (ElasticityPrimitivesDescription) u.unmarshal(new FileInputStream(elasticityPrimitives));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(InputProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JAXBException e) {
            DependencyGraphLogger.logger.info(e.getStackTrace().toString());
        }
        return elasticityPrimitivesDescription;
    }

    public DependencyGraph loadDependencyGraphFromFile() {

        loadModelFromFile();
        loadDeploymentDescriptionFromFile();

        return constructDependencyGraph();
    }

    public DependencyGraph loadDependencyGraphFromStrings(String applicationDescription, String additionalElasticityRequirements, String deploymentInfo) {
        DependencyGraphLogger.logger.info("Constructing cloud service dependency graph...");

        DependencyGraphLogger.logger.info("Service description \n " + applicationDescription);
        DependencyGraphLogger.logger.info("Service additionalElasticityRequirements \n " + additionalElasticityRequirements);
        DependencyGraphLogger.logger.info("Service deploymentInfo \n " + deploymentInfo);

        loadModelFromString(applicationDescription, additionalElasticityRequirements);
        DependencyGraphLogger.logger.info("Adding deployment info to cloud service dependency graph...");

        loadDeploymentDescriptionFromString(deploymentInfo);

        return constructDependencyGraph();
    }

    public DependencyGraph loadDependencyGraphFromObjects(CloudServiceXML cloudServiceXML, SYBLElasticityRequirementsDescription description, DeploymentDescription deploymentDescription) {
        this.cloudServiceXML = cloudServiceXML;
        this.syblSpecifications = description;
        this.deploymentDescription = deploymentDescription;
        return constructDependencyGraph();
    }

    public SYBLAnnotation mapFromXMLAnnotationToSYBLAnnotation(String entityID, SYBLAnnotationXML syblAnnotationXML, SYBLAnnotation.AnnotationType annotationType) {
        SYBLAnnotation syblannotation = new SYBLAnnotation();

        syblannotation = new SYBLAnnotation();
        syblannotation.setPriorities(syblAnnotationXML.getPriorities());
        syblannotation.setConstraints(syblAnnotationXML.getConstraints());
        syblannotation.setStrategies(syblAnnotationXML.getStrategies());
        syblannotation.setMonitoring(syblAnnotationXML.getMonitoring());
        syblannotation.setEntityID(entityID);
        syblannotation.setAnnotationType(annotationType);
        return syblannotation;
    }

    public List<SYBLAnnotation> parseXMLInjectedAnnotations(CloudServiceXML cloudService) {
        boolean found = false;
        List<SYBLAnnotation> annotations = new ArrayList<SYBLAnnotation>();
        if (cloudService.getXMLAnnotation() != null) {
            annotations.add(mapFromXMLAnnotationToSYBLAnnotation(cloudService.getId(), cloudService.getXMLAnnotation(), SYBLAnnotation.AnnotationType.CLOUD_SERVICE));
        }

        List<ServiceTopologyXML> topologies = new ArrayList<ServiceTopologyXML>();

        topologies.addAll(cloudService.getServiceTopologies());

        List<ServiceUnitXML> componentsToExplore = new ArrayList<ServiceUnitXML>();

        while (!found && !topologies.isEmpty()) {
            ServiceTopologyXML currentTopology = topologies.get(0);
            topologies.remove(0);
            if (currentTopology.getXMLAnnotation() != null) {
                annotations.add(mapFromXMLAnnotationToSYBLAnnotation(currentTopology.getId(), currentTopology.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_TOPOLOGY));
            }

            if (currentTopology.getServiceTopology() != null && currentTopology.getServiceTopology().size() > 0) {
                topologies.addAll(currentTopology.getServiceTopology());
            }
            if (currentTopology.getServiceUnits() != null && currentTopology.getServiceUnits().size() > 0) {
                componentsToExplore.addAll(currentTopology.getServiceUnits());
            }
        }

        while (!found && !componentsToExplore.isEmpty()) {
            ServiceUnitXML component = componentsToExplore.get(0);

            if (component.getXMLAnnotation() != null) {
                annotations.add(mapFromXMLAnnotationToSYBLAnnotation(component.getId(), component.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_UNIT));
            }

            componentsToExplore.remove(0);

        }

        return annotations;
    }
}
