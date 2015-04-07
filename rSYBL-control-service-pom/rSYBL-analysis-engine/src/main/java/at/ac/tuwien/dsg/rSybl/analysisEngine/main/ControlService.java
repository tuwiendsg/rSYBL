/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group
 * E184. This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
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
 * License for the senpecific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.tosca.TOSCAProcessing;
import at.ac.tuwien.dsg.csdg.outputProcessing.OutputProcessing;
import at.ac.tuwien.dsg.csdg.outputProcessing.OutputProcessingFactory;
import at.ac.tuwien.dsg.csdg.outputProcessing.OutputProcessingInterface;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.EventNotification;

import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.AnalysisLogger;
import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.MonitoringThread;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPI;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.MultipleEnforcementAPIs;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPI;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.HealthWatch;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningAlgorithmInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningGreedyAlgorithm;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningGreedyAlgorithmWithPolynomialElasticityRelationships;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningHeuristicSearch;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningHeuristicSearchWithPolynomialElasticityRelationships;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.processing.SYBLProcessingThread;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;

public class ControlService {

    private SYBLService syblService;
    private MonitoringAPIInterface monitoringAPI;
    private EnforcementAPIInterface enforcementAPI;
    private HealthWatch healthWatch;
    private DependencyGraph dependencyGraph;
    private PlanningAlgorithmInterface planningAlgorithm;
    private String applicationDescription = "";
    private String deploymentDescription = "";
    private String metricCompositionRules = "";
    private String effects = "";

    public ControlService() {
        new StartThread().start();

    }

    public String getApplicationDescriptionInfo() {
        return applicationDescription;
    }

    public class StartThread extends Thread {

        public void run() {
            if (Configuration.getApplicationSpecificInformation().equalsIgnoreCase(
                    "files")) {
                loadEverythingFromConfigurationFiles();
            }
        }
    }

    public void triggerHealthFix(String servicePartID) {
        healthWatch.triggerHealthFix(servicePartID);
    }
    public String getXMLRequirements(){
        OutputProcessing outputProcessing = new OutputProcessing();
        try{
        return outputProcessing.getXMLRequirements(dependencyGraph.getAllElasticityRequirements());
        }catch(Exception e ){
            return "";
        }
    }
    public void refreshApplicationDeploymentDescription(String deployment) {
        //TODO implement this, have to replace deployment conf 
        InputProcessing inputProcessing = new InputProcessing();

        deploymentDescription = deployment;
        dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(applicationDescription, "", deploymentDescription);
        replaceDependencyGraph();
    }

    public void start() {

        startSYBLProcessingAndPlanning();
        dependencyGraph.setControlState();
        }
    public void startControlOnExisting(){
        controlExistingService();
        dependencyGraph.setControlState();
    }
    public void stop() {
        if (dependencyGraph.isInControlState()){
        monitoringAPI.removeService(dependencyGraph.getCloudService());
        EventNotification eventNotification = EventNotification.getEventNotification();
        eventNotification.clearAllEvents();
        
        dependencyGraph.setWaitState();
        if (planningAlgorithm != null) {
            planningAlgorithm.stop();
        }
        if (syblService != null) {
            syblService.stopProcessingThreads();
        }
        planningAlgorithm = null;
        syblService = null;
        monitoringAPI = null;
        enforcementAPI = null;
        effects = "";
        metricCompositionRules = "";
        }
    }
    
    public void undeployService(){
        enforcementAPI.undeployService(dependencyGraph.getCloudService());
    }
 public void removeFromMonitoring(){
     if (monitoringAPI!=null){
        monitoringAPI.removeService(dependencyGraph.getCloudService());
     }
    }

    public void setDependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }

    public String getJSONStructureOfService() {
        try{
        return dependencyGraph.getStructuralDataInJSON();
        }catch(Exception e){
            
                return "";
        }
        }

    private void replaceDependencyGraph() {
        Node node = new Node();
        node = dependencyGraph.getCloudService();
                OutputProcessing outputProcessing = new OutputProcessing();
        
        applicationDescription= outputProcessing.getCloudServiceXML(dependencyGraph.getCloudService());
        monitoringAPI.setControlledService(node);

        monitoringAPI.submitElasticityRequirements(dependencyGraph
                .getAllElasticityRequirements());
        enforcementAPI.setControlledService(node);
        enforcementAPI.setMonitoringPlugin(monitoringAPI);
        syblService.stopProcessingThreads();
        syblService = new SYBLService(dependencyGraph, monitoringAPI,
                enforcementAPI);
        for (ElasticityRequirement syblSpecification : dependencyGraph
                .getAllElasticityRequirements()) {
            SYBLAnnotation annotation = syblSpecification.getAnnotation();
            syblService.processAnnotations(syblSpecification
                    .getAnnotation().getEntityID(), annotation);

        }

        planningAlgorithm.replaceDependencyGraph(dependencyGraph);
        if (!effects.equalsIgnoreCase("")) {
            planningAlgorithm.setEffects(effects);
        }

    }

    public void replaceRequirements(String requirements) {
        String[] reqs = requirements.split("\n");
        for (String req : reqs) {
            dependencyGraph.replaceRequirement(req);
        }
        replaceDependencyGraph();
    }
    public void controlExistingService(){
            InputProcessing inputProcessing = new InputProcessing();
            
            dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(applicationDescription, "", deploymentDescription);
            Node node = new Node();
            node = dependencyGraph.getCloudService();
            AnalysisLogger.logger.info("Current graph is" + dependencyGraph.graphToString());
            monitoringAPI = new MonitoringAPI();
            monitoringAPI.controlExistingCloudService(node);

            AnalysisLogger.logger.info("Have just started monitoring plugin for existing cloud service");

//            monitoringAPI.submitElasticityRequirements(dependencyGraph
//                    .getAllElasticityRequirements());
//            AnalysisLogger.logger.info("Have set the requirements on MELA");
            enforcementAPI = new MultipleEnforcementAPIs();

            enforcementAPI.setControlledService(node);

            enforcementAPI.setMonitoringPlugin(monitoringAPI);
            AnalysisLogger.logger.info("Have information on enforcement api");

            syblService = new SYBLService(dependencyGraph, monitoringAPI,
                    enforcementAPI);
            for (ElasticityRequirement syblSpecification : dependencyGraph
                    .getAllElasticityRequirements()) {
                SYBLAnnotation annotation = syblSpecification.getAnnotation();
                syblService.processAnnotations(syblSpecification
                        .getAnnotation().getEntityID(), annotation);

            }

            AnalysisLogger.logger.info("SYBL Service started");

            if (Configuration.getPlanningAlgorithm() != null && !Configuration.getPlanningAlgorithm().equals("") && Configuration.getPlanningAlgorithm().toLowerCase().contains("heuristic")) {
                planningAlgorithm = new PlanningHeuristicSearchWithPolynomialElasticityRelationships(
                        dependencyGraph, monitoringAPI, enforcementAPI);
            } else {
                planningAlgorithm = new PlanningGreedyAlgorithm(
                        dependencyGraph, monitoringAPI, enforcementAPI);
            }
            if (!effects.equalsIgnoreCase("")) {
                planningAlgorithm.setEffects(effects);
            }

            planningAlgorithm.start();
            AnalysisLogger.logger.info("Planning algorithm started");

    }
    public void setStateTEST(){
        InputProcessing inputProcessing = new InputProcessing();
            
            dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(applicationDescription, "", deploymentDescription);
            Node node = new Node();
            node = dependencyGraph.getCloudService();
            AnalysisLogger.logger.info("Test mode - Current graph is" + dependencyGraph.graphToString());
            monitoringAPI = new MonitoringAPI();
            monitoringAPI.setControlledService(node);
            if (!metricCompositionRules.equalsIgnoreCase("")) {
                AnalysisLogger.logger.info("Test mode - Set the composition rules sent via WS ");
                monitoringAPI.setCompositionRules(metricCompositionRules);
            } else {
                AnalysisLogger.logger.info("Test mode - Set the read composition rules");
                monitoringAPI.setCompositionRules();
            }
            AnalysisLogger.logger.info("Test mode - Have just set the cloud service. The number of elasticity requirements is " + dependencyGraph.getAllElasticityRequirements().size());

            monitoringAPI.submitElasticityRequirements(dependencyGraph
                    .getAllElasticityRequirements());
            AnalysisLogger.logger.info("Test mode - Have set the requirements on MELA");
            enforcementAPI = new MultipleEnforcementAPIs();

            enforcementAPI.setControlledService(node);

            enforcementAPI.setMonitoringPlugin(monitoringAPI);
            AnalysisLogger.logger.info("Test mode - Have set information on enforcement api");
            dependencyGraph.setTestingState();

    }
    public boolean testEnforcementCapability(String enforcementName, String componentID){
      return  enforcementAPI.enforceAction(enforcementName, dependencyGraph.getNodeWithID(componentID));
    }
    public boolean testEnforcementCapabilityOnPlugin(String target,String enforcementName, String componentID){
      return  enforcementAPI.enforceAction(target, enforcementName,dependencyGraph.getNodeWithID(componentID));
    }
    public void startSYBLProcessingAndPlanning() {
        try {
            InputProcessing inputProcessing = new InputProcessing();
            
            dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(applicationDescription, "", deploymentDescription);

            //AnalysisLogger.logger.info("Current graph is "
            //	+ dependencyGraph.graphToString());

            // at.ac.tuwien.dsg.sybl.monitorandenforcement.runtimeapi.Node
            // clService =
            // MappingToWS.mapNodeToNode(dependencyGraph.getCloudService());
            Node node = new Node();
            node = dependencyGraph.getCloudService();
            AnalysisLogger.logger.info("Current graph is" + dependencyGraph.graphToString());
            monitoringAPI = new MonitoringAPI();
            monitoringAPI.setControlledService(node);
            if (!metricCompositionRules.equalsIgnoreCase("")) {
                AnalysisLogger.logger.info("Set the composition rules sent via WS ");
                monitoringAPI.setCompositionRules(metricCompositionRules);
            } else {
                AnalysisLogger.logger.info("Set the read composition rules");
                monitoringAPI.setCompositionRules();
            }
            AnalysisLogger.logger.info("Have just set the cloud service. The number of elasticity requirements is " + dependencyGraph.getAllElasticityRequirements().size());

            monitoringAPI.submitElasticityRequirements(dependencyGraph
                    .getAllElasticityRequirements());
            AnalysisLogger.logger.info("Have set the requirements on MELA");
            enforcementAPI = new MultipleEnforcementAPIs();

            enforcementAPI.setControlledService(node);

            enforcementAPI.setMonitoringPlugin(monitoringAPI);
            AnalysisLogger.logger.info("Have information on enforcement api");

            syblService = new SYBLService(dependencyGraph, monitoringAPI,
                    enforcementAPI);
            for (ElasticityRequirement syblSpecification : dependencyGraph
                    .getAllElasticityRequirements()) {
                SYBLAnnotation annotation = syblSpecification.getAnnotation();
                syblService.processAnnotations(syblSpecification
                        .getAnnotation().getEntityID(), annotation);

            }

            AnalysisLogger.logger.info("SYBL Service started");
            // CloudService cloudService, ArrayList<SYBLSpecification>
            // syblSpecifications
            //disableConflictingConstraints();
            //AnalysisLogger.logger.info("Conflicting constraints disabled");
            if (Configuration.getPlanningAlgorithm() != null && !Configuration.getPlanningAlgorithm().equals("") && Configuration.getPlanningAlgorithm().toLowerCase().contains("heuristic")) {
                planningAlgorithm = new PlanningHeuristicSearchWithPolynomialElasticityRelationships(
                        dependencyGraph, monitoringAPI, enforcementAPI);
            } else {
                planningAlgorithm = new PlanningGreedyAlgorithm(
                        dependencyGraph, monitoringAPI, enforcementAPI);
            }
            if (!effects.equalsIgnoreCase("")) {
                planningAlgorithm.setEffects(effects);
            }

            planningAlgorithm.start();
            AnalysisLogger.logger.info("Planning algorithm started");
            healthWatch = new HealthWatch(monitoringAPI, enforcementAPI, dependencyGraph);
            AnalysisLogger.logger.info("Health watch started");
//			for (Node node1:dependencyGraph.getAllServiceUnits()){
//				try{
//				MonitoringThread monitoringThread = new MonitoringThread(node1, monitoringAPI);
//				monitoringThread.start();
//                                 }catch(Exception e){
//                            AnalysisLogger.logger.error ("Error in starting monitoring threads"+e.getMessage()+node1.getId());
//                        }
//
//			}
//			for (Node node1:dependencyGraph.getAllServiceTopologies()){
//                            try{
//				MonitoringThread monitoringThread = new MonitoringThread(node1, monitoringAPI);
//				monitoringThread.start();
//                                 }catch(Exception e){
//                            AnalysisLogger.logger.error ("Error in starting monitoring threads"+e.getMessage()+node1.getId());
//                        }
//			}
//			MonitoringThread monitoringThread = new MonitoringThread(dependencyGraph.getCloudService(), monitoringAPI);
//			monitoringThread.start();

        } catch (Exception e) {
            AnalysisLogger.logger.error("Control service Instantiation "
                    + e.toString() + "with message " + e.getMessage());
            e.printStackTrace();
        }

    }

//	public void setApplicationDescriptionInfoTOSCA(String toscaDescr){
//		applicationDescription = toscaDescr;
//		TOSCAProcessing inputProcessing = new TOSCAProcessing();
//
//		if (planningGreedyAlgorithm != null)
//			planningGreedyAlgorithm.stop();
//		if (syblService != null)
//			syblService.stopProcessingThreads();
//		planningGreedyAlgorithm = null;
//		syblService = null;
//		monitoringAPI = null;
//		enforcementAPI = null;
//
//		if (!applicationDescription.equalsIgnoreCase("")
//				&& !deploymentDescription.equalsIgnoreCase("")) {
//			dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(
//					applicationDescription, "", deploymentDescription);
//			startSYBLProcessingAndPlanning();
//			applicationDescription = "";
//			deploymentDescription = "";
//		}
//	}
    public void setApplicationDescriptionInfo(String applicationDescriptionXML) {
        applicationDescription = applicationDescriptionXML;




    }

    public void setApplicationDeployment(String deploymentDescriptionXML) {
        deploymentDescription = deploymentDescriptionXML;
    }

    public void setApplicationDescriptionInfoInternalModel(
            String applicationDescriptionXML, String elasticityRequirementsXML,
            String deploymentInfoXML) {
        InputProcessing inputProcessing = new InputProcessing();
        dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(
                applicationDescriptionXML, elasticityRequirementsXML,
                deploymentInfoXML);
        //startSYBLProcessingAndPlanning();
    }

    public void setApplicationDescriptionInfoTOSCABased(String tosca) {
        // TODO : continue this, parse tosca and start planning and stuff
        TOSCAProcessing toscaProcessing = new TOSCAProcessing();
        dependencyGraph = toscaProcessing.toscaDescriptionToDependencyGraph(tosca);
        OutputProcessing outputProcessing = new OutputProcessing();
        applicationDescription=outputProcessing.getCloudServiceXML(dependencyGraph.getCloudService());
        //startSYBLProcessingAndPlanning();
    }

    public void loadEverythingFromConfigurationFiles() {
        InputProcessing input = new InputProcessing();
        dependencyGraph = input.loadDependencyGraphFromFile();
        //AnalysisLogger.logger.info("Loaded graph from files ");
        startSYBLProcessingAndPlanning();
    }

    public void writeCurrentDirectivesToFile(String file) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ElasticityRequirement elReq : dependencyGraph
                .getAllElasticityRequirements()) {
            SYBLAnnotation annotation = elReq.getAnnotation();
            try {
                out.write(annotation.getEntityID() + "\n");
                out.write(annotation.getConstraints() + "\n");
                out.write(annotation.getStrategies() + "\n");
                out.write(annotation.getMonitoring() + "\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // syblService.processAnnotations(syblSpecification.getComponentId(),
            // annotation);
        }
        try {
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean checkDistribution(Node arg1) throws RemoteException {
        // TODO Auto-generated method stub
        return syblService.checkIfContained(arg1);
    }

    public void processAnnotation(String arg1, SYBLAnnotation arg2)
            throws RemoteException {
        syblService.processAnnotations(arg1, arg2);
    }

    // Conflict resolution for complex constraints
    public String checkIfConstraintsAreConflicting(
            BinaryRestriction binaryRestriction1,
            BinaryRestriction binaryRestriction2) {
        boolean conflict = false;
        String metricLeft1 = "";
        Float numberRight1 = 0.0f;

        if (binaryRestriction1.getLeftHandSide().getMetric() != null) {
            metricLeft1 = binaryRestriction1.getLeftHandSide().getMetric();
            numberRight1 = Float.parseFloat(binaryRestriction1
                    .getRightHandSide().getNumber());

        } else {
            metricLeft1 = binaryRestriction1.getRightHandSide().getMetric();
            numberRight1 = Float.parseFloat(binaryRestriction1
                    .getLeftHandSide().getNumber());
            if (binaryRestriction1.getType().contains("lessThan")) {
                binaryRestriction1.getType().replaceAll("lessThan",
                        "greaterThan");
            } else {
                binaryRestriction1.getType().replaceAll("greaterThan",
                        "lessThan");
            }
        }
        String metricLeft2 = "";
        Float numberRight2 = 0.0f;

        if (binaryRestriction2.getLeftHandSide().getMetric() != null) {
            metricLeft2 = binaryRestriction2.getLeftHandSide().getMetric();
            numberRight2 = Float.parseFloat(binaryRestriction2
                    .getRightHandSide().getNumber());

        } else {
            metricLeft2 = binaryRestriction2.getRightHandSide().getMetric();
            numberRight2 = Float.parseFloat(binaryRestriction2
                    .getLeftHandSide().getNumber());
            if (binaryRestriction2.getType().contains("lessThan")) {
                binaryRestriction2.getType().replaceAll("lessThan",
                        "greaterThan");
            } else {
                binaryRestriction2.getType().replaceAll("greaterThan",
                        "lessThan");
            }
        }

        if (metricLeft1.equalsIgnoreCase(metricLeft2)) {
            if (binaryRestriction1.getType().contains("lessThan")) {
                if (binaryRestriction2.getType().contains("greaterThan")) {
                    if (numberRight1 <= numberRight2) {
                        return metricLeft1;
                    }
                }
            } else {
                if (binaryRestriction1.getType().contains("greaterThan")) {
                    if (binaryRestriction2.getType().contains("lessThan")) {
                        if (numberRight1 >= numberRight2) {
                            return metricLeft1;
                        }
                    }
                }
            }
        }
        return "";
    }

    public void disableConflictingConstraints() {
        HashMap<String, String> toRemove = new HashMap<String, String>();
        for (ElasticityRequirement elReq : dependencyGraph
                .getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML
                    .mapFromSYBLAnnotation(elReq.getAnnotation());
            if (!syblSpecification.getType().equalsIgnoreCase("component")) {
                List<Constraint> constraints = syblSpecification
                        .getConstraint();
                // System.err.println("Searching for "+syblSpecification.getComponentId());
                Node entity = dependencyGraph.getNodeWithID(syblSpecification
                        .getComponentId());
                if (entity.getNodeType() == NodeType.CLOUD_SERVICE) {
                    entity = (entity).getAllRelatedNodesOfType(
                            RelationshipType.COMPOSITION_RELATIONSHIP,
                            NodeType.SERVICE_TOPOLOGY).get(0);
                }
                if (entity.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
                    // ComponentTopology componentTopology = (ComponentTopology)
                    // entity;
                    for (Node topology : entity.getAllRelatedNodesOfType(
                            RelationshipType.COMPOSITION_RELATIONSHIP,
                            NodeType.SERVICE_TOPOLOGY)) {
                        for (ElasticityRequirement el : topology
                                .getElasticityRequirements()) {
                            SYBLSpecification specification = SYBLDirectiveMappingFromXML
                                    .mapFromSYBLAnnotation(el.getAnnotation());
                            if (specification.getComponentId()
                                    .equalsIgnoreCase(topology.getId())) {
                                for (Constraint c1 : constraints) {
                                    for (Constraint c2 : specification
                                            .getConstraint()) {
                                        for (BinaryRestrictionsConjunction binaryRestrictionsC1 : c1
                                                .getCondition()
                                                .getBinaryRestriction()) {
                                            for (BinaryRestriction binaryRestrictionC1 : binaryRestrictionsC1.getBinaryRestrictions()) {
                                                for (BinaryRestrictionsConjunction binaryRestrictionsC2 : c2
                                                        .getCondition()
                                                        .getBinaryRestriction()) {
                                                    for (BinaryRestriction binaryRestrictionC2 : binaryRestrictionsC2.getBinaryRestrictions()) {
                                                        if (!checkIfConstraintsAreConflicting(
                                                                binaryRestrictionC1,
                                                                binaryRestrictionC2)
                                                                .equalsIgnoreCase(
                                                                "")) {
                                                            String metric = checkIfConstraintsAreConflicting(
                                                                    binaryRestrictionC1,
                                                                    binaryRestrictionC2);
                                                            toRemove.put(
                                                                    metric,
                                                                    c1.getId());

                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        if (topology.getAllRelatedNodesOfType(
                                RelationshipType.COMPOSITION_RELATIONSHIP,
                                NodeType.SERVICE_TOPOLOGY) != null) {
                            for (Node topology1 : topology
                                    .getAllRelatedNodesOfType(
                                    RelationshipType.COMPOSITION_RELATIONSHIP,
                                    NodeType.SERVICE_TOPOLOGY)) {
                                for (ElasticityRequirement el : topology
                                        .getElasticityRequirements()) {
                                    SYBLSpecification specification = SYBLDirectiveMappingFromXML
                                            .mapFromSYBLAnnotation(el
                                            .getAnnotation());
                                    if (specification
                                            .getComponentId()
                                            .equalsIgnoreCase(topology1.getId())) {
                                        for (Constraint c1 : constraints) {
                                            for (Constraint c2 : specification
                                                    .getConstraint()) {
                                                for (BinaryRestrictionsConjunction binaryRestrictionsC1 : c1
                                                        .getCondition()
                                                        .getBinaryRestriction()) {
                                                    for (BinaryRestriction binaryRestrictionC1 : binaryRestrictionsC1.getBinaryRestrictions()) {
                                                        for (BinaryRestrictionsConjunction binaryRestrictionsC2 : c2
                                                                .getCondition()
                                                                .getBinaryRestriction()) {
                                                            for (BinaryRestriction binaryRestrictionC2 : binaryRestrictionsC2.getBinaryRestrictions()) {
                                                                if (!checkIfConstraintsAreConflicting(
                                                                        binaryRestrictionC1,
                                                                        binaryRestrictionC2)
                                                                        .equalsIgnoreCase(
                                                                        "")) {
                                                                    String metric = checkIfConstraintsAreConflicting(
                                                                            binaryRestrictionC1,
                                                                            binaryRestrictionC2);
                                                                    toRemove.put(
                                                                            metric,
                                                                            c1.getId());

                                                                }
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            for (Node comp : topology.getAllRelatedNodesOfType(
                                    RelationshipType.COMPOSITION_RELATIONSHIP,
                                    NodeType.SERVICE_UNIT)) {
                                for (ElasticityRequirement el : topology
                                        .getElasticityRequirements()) {
                                    SYBLSpecification specification = SYBLDirectiveMappingFromXML
                                            .mapFromSYBLAnnotation(el
                                            .getAnnotation());
                                    if (specification.getComponentId()
                                            .equalsIgnoreCase(comp.getId())) {
                                        for (Constraint c1 : constraints) {
                                            for (Constraint c2 : specification
                                                    .getConstraint()) {
                                                for (BinaryRestrictionsConjunction binaryRestrictionsC1 : c1
                                                        .getCondition()
                                                        .getBinaryRestriction()) {
                                                    for (BinaryRestriction binaryRestrictionC1 : binaryRestrictionsC1.getBinaryRestrictions()) {
                                                        for (BinaryRestrictionsConjunction binaryRestrictionsC2 : c2
                                                                .getCondition()
                                                                .getBinaryRestriction()) {
                                                            for (BinaryRestriction binaryRestrictionC2 : binaryRestrictionsC2.getBinaryRestrictions()) {
                                                                if (!checkIfConstraintsAreConflicting(
                                                                        binaryRestrictionC1,
                                                                        binaryRestrictionC2)
                                                                        .equalsIgnoreCase(
                                                                        "")) {
                                                                    String metric = checkIfConstraintsAreConflicting(
                                                                            binaryRestrictionC1,
                                                                            binaryRestrictionC2);
                                                                    toRemove.put(
                                                                            metric,
                                                                            c1.getId());

                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                            if ((entity).getAllRelatedNodesOfType(
                                    RelationshipType.COMPOSITION_RELATIONSHIP,
                                    NodeType.SERVICE_UNIT) != null) {
                                for (Node comp : entity
                                        .getAllRelatedNodesOfType(
                                        RelationshipType.COMPOSITION_RELATIONSHIP,
                                        NodeType.SERVICE_UNIT)) {
                                    for (ElasticityRequirement el : comp
                                            .getElasticityRequirements()) {
                                        SYBLSpecification specification = SYBLDirectiveMappingFromXML
                                                .mapFromSYBLAnnotation(el
                                                .getAnnotation());
                                        if (specification.getComponentId()
                                                .equalsIgnoreCase(comp.getId())) {
                                            for (Constraint c1 : constraints) {
                                                for (Constraint c2 : specification
                                                        .getConstraint()) {
                                                    for (BinaryRestrictionsConjunction binaryRestrictionsC1 : c1
                                                            .getCondition()
                                                            .getBinaryRestriction()) {
                                                        for (BinaryRestriction binaryRestrictionC1 : binaryRestrictionsC1.getBinaryRestrictions()) {
                                                            for (BinaryRestrictionsConjunction binaryRestrictionsC2 : c2
                                                                    .getCondition()
                                                                    .getBinaryRestriction()) {
                                                                for (BinaryRestriction binaryRestrictionC2 : binaryRestrictionsC2.getBinaryRestrictions()) {
                                                                    if (!checkIfConstraintsAreConflicting(
                                                                            binaryRestrictionC1,
                                                                            binaryRestrictionC2)
                                                                            .equalsIgnoreCase(
                                                                            "")) {
                                                                        String metric = checkIfConstraintsAreConflicting(
                                                                                binaryRestrictionC1,
                                                                                binaryRestrictionC2);
                                                                        toRemove.put(
                                                                                metric,
                                                                                c1.getId());

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (ElasticityRequirement elasticityRequirement : dependencyGraph
                .getAllElasticityRequirements()) {
            SYBLSpecification specification = SYBLDirectiveMappingFromXML
                    .mapFromSYBLAnnotation(elasticityRequirement
                    .getAnnotation());
            List<Constraint> constr = new ArrayList<Constraint>();
            constr.addAll(specification.getConstraint());
            for (Constraint i : constr) {
                if (toRemove.containsValue(i.getId())) {
                    AnalysisLogger.logger
                            .info("Removing the constraint " + i.getId());
                    specification.getConstraint().remove(constr.indexOf(i));
                }
            }
        }
    }


    public void replaceCloudServiceRequirements(String newCloudServiceDescription) {
        InputProcessing inputProcessing = new InputProcessing();
        syblService.stopProcessingThreads();
        applicationDescription = newCloudServiceDescription;
        AnalysisLogger.logger.info("Stopped processing threads");
        dependencyGraph = inputProcessing.replaceCloudServiceRequirements(dependencyGraph, newCloudServiceDescription);
        monitoringAPI.submitElasticityRequirements(dependencyGraph.getAllElasticityRequirements());

        enforcementAPI = new MultipleEnforcementAPIs();

        enforcementAPI.setControlledService(dependencyGraph.getCloudService());

        enforcementAPI.setMonitoringPlugin(monitoringAPI);
        AnalysisLogger.logger.info("Have information on enforcement api");

        syblService = new SYBLService(dependencyGraph, monitoringAPI,
                enforcementAPI);
        for (ElasticityRequirement syblSpecification : dependencyGraph
                .getAllElasticityRequirements()) {
            SYBLAnnotation annotation = syblSpecification.getAnnotation();
            syblService.processAnnotations(syblSpecification
                    .getAnnotation().getEntityID(), annotation);

        }
        planningAlgorithm.stop();
        planningAlgorithm = new PlanningGreedyAlgorithm(
                dependencyGraph, monitoringAPI, enforcementAPI);
        if (!effects.equalsIgnoreCase("")) {
            planningAlgorithm.setEffects(effects);
        }
        planningAlgorithm.start();

    }

    public void replaceEffects(String effects) {
        planningAlgorithm.setEffects(effects);
    }

    public void replaceElasticityRequirements(String requirements) {
        InputProcessing inputProcessing = new InputProcessing();
        AnalysisLogger.logger.info("Replacing requirements from dependency graph " + dependencyGraph.graphToString());
        syblService.stopProcessingThreads();

        AnalysisLogger.logger.info("Stopped processing threads");
        //dependencyGraph.replaceRequirement(requirements);
        dependencyGraph = inputProcessing.replaceRequirements(dependencyGraph, requirements);
        monitoringAPI.submitElasticityRequirements(dependencyGraph.getAllElasticityRequirements());

        enforcementAPI = new MultipleEnforcementAPIs();

        enforcementAPI.setControlledService(dependencyGraph.getCloudService());

        enforcementAPI.setMonitoringPlugin(monitoringAPI);
        //AnalysisLogger.logger.info("Have information on enforcement api");

        syblService = new SYBLService(dependencyGraph, monitoringAPI,
                enforcementAPI);
        for (ElasticityRequirement syblSpecification : dependencyGraph
                .getAllElasticityRequirements()) {
            SYBLAnnotation annotation = syblSpecification.getAnnotation();
            syblService.processAnnotations(syblSpecification
                    .getAnnotation().getEntityID(), annotation);

        }
        planningAlgorithm.stop();
        planningAlgorithm = new PlanningGreedyAlgorithm(
                dependencyGraph, monitoringAPI, enforcementAPI);
        if (!effects.equalsIgnoreCase("")) {
            planningAlgorithm.setEffects(effects);
        }
        planningAlgorithm.start();

    }

    public void replaceCompositionRules(String composition) {
        monitoringAPI.setCompositionRules(composition);
    }

    public String getMetricCompositionRules() {
        return metricCompositionRules;
    }

    public void setMetricCompositionRules(String metricCompositionRules) {
        this.metricCompositionRules = metricCompositionRules;
    }

    public String getEffects() {
        return effects;
    }

    public void setEffects(String effects) {
        this.effects = effects;

    }
}
