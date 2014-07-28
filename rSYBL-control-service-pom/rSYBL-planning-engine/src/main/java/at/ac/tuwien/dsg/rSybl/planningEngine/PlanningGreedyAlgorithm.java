package at.ac.tuwien.dsg.rSybl.planningEngine;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Condition;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.ContextRepresentation.Pair;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffects;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

public class PlanningGreedyAlgorithm implements PlanningAlgorithmInterface {

    private Thread t;
    private boolean toCleanup = true;
    private ContextRepresentation contextRepresentation;
    private MonitoringAPIInterface monitoringAPI;
    private EnforcementAPIInterface enforcementAPI;
    private DependencyGraph dependencyGraph;
    private ContextRepresentation lastContextRepresentation;
    private String strategiesThatNeedToBeImproved = "";
    private int REFRESH_PERIOD = 120000;

    
    public PlanningGreedyAlgorithm(DependencyGraph cloudService,
            MonitoringAPIInterface monitoringAPI, EnforcementAPIInterface enforcementAPI) {
        this.dependencyGraph = cloudService;
        this.monitoringAPI = monitoringAPI;
        this.enforcementAPI = enforcementAPI;
        REFRESH_PERIOD = Configuration.getRefreshPeriod();
        t = new Thread(this);
    }

    public boolean checkIfActionPossible(ActionEffect actionEffect) {
        Node entity = dependencyGraph.getNodeWithID(actionEffect.getTargetedEntityID());
        // System.out.println("Targeted entity id "
        // +actionEffect.getTargetedEntityID()+entity);

        boolean possible = true;
        if (actionEffect.getActionType().equalsIgnoreCase("scalein")) {
            if (entity.getNodeType() == NodeType.CLOUD_SERVICE) {
                List<String> ips = entity.getAssociatedIps();
                PlanningLogger.logger.info("For action " + actionEffect.getActionName() + entity.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size() + " hosts");
                if (entity.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size() > 4) {
                    return true;
                }
            }
            if (entity.getNodeType() == NodeType.SERVICE_TOPOLOGY) {

                Node master = dependencyGraph.findParentNode(entity.getId());
                List<String> ips = master.getAssociatedIps();
                int numberPrivateIps = 0;
                for (String ip : ips) {
                    if (ip.split("\\.")[0].length() == 2) {
                        numberPrivateIps++;
                    }
                }
                if (entity.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size() > 4) {
                    return true;
                }
            }
        }
        return possible;
    }

    public void findStrategies() {

        for (ElasticityRequirement elasticityRequirement : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
            MonitoredEntity monitoredEntity = contextRepresentation.findMonitoredEntity(syblSpecification.getComponentId());
            if (monitoredEntity == null) {
                PlanningLogger.logger.info("Not finding monitored entity " + monitoredEntity + " " + syblSpecification.getComponentId());
            }
            for (Strategy strategy : syblSpecification.getStrategy()) {
                Condition condition = strategy.getCondition();

                if (contextRepresentation.evaluateCondition(condition, monitoredEntity)) {
                    if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize") || strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")) {
                            //PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                            if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter()) <= lastContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                strategiesThatNeedToBeImproved += strategy.getId() + " ";
                            }
                        }
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {

                            PlanningLogger.logger.info("Current value for " + strategy.getToEnforce().getParameter() + " is " + monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter()) + " .Previous value was " + lastContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter()));

                            if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter()) >= lastContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                strategiesThatNeedToBeImproved += strategy.getId() + " ";
                            }
                        }
                    }

                }
            }
        }

    }
    public void checkInstantiation(){
    	List<Relationship> instantiationRelationships=dependencyGraph.getAllRelationshipsOfType(RelationshipType.INSTANTIATION);
    	//for
    }
	public ActionEffect checkActions( String target){
		HashMap<String, List<ActionEffect>> actionEffects = ActionEffects.getActionEffects();
		int maxConstraints= 0;
		ActionEffect maxConstraintsAction=null;
		PlanningLogger.logger.info("~~~~~~~~~~~Evaluating complimentary actions for "+target);

		for (List<ActionEffect> actionEffect:actionEffects.values()){
			for (ActionEffect effect:actionEffect){
				if (effect.getAffectedNodes().contains(target)){
					int beforeConstraints= contextRepresentation.countViolatedConstraints();
					MonitoredCloudService monitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
			        ContextRepresentation beforeContext = new ContextRepresentation(monitoredCloudService, monitoringAPI);
					contextRepresentation.doAction(effect);
					int improvedStrategies = contextRepresentation.countFixedStrategies(beforeContext);
					int afterConstraints = contextRepresentation.countViolatedConstraints();
					PlanningLogger.logger.info("With "+effect.getActionType()+" on "+effect.getTargetedEntityID()+improvedStrategies+" and constraints  "+(beforeConstraints-afterConstraints)+" violated constraints "+contextRepresentation.getViolatedConstraints());
					contextRepresentation.undoAction(effect);
					if (beforeConstraints-afterConstraints+improvedStrategies>maxConstraints && (beforeConstraints-afterConstraints+improvedStrategies)>0){
						maxConstraints=beforeConstraints-afterConstraints+improvedStrategies;
						maxConstraintsAction=effect;
					}
				}
			}
		}
		
		if (maxConstraintsAction!=null)
			PlanningLogger.logger.info("Returning "+maxConstraintsAction.getActionType()+" on "+maxConstraintsAction.getTargetedEntityID());
		else
			PlanningLogger.logger.info("Returning null ");
		return maxConstraintsAction;
	}


      
    public void findAndExecuteBestActions() {

        strategiesThatNeedToBeImproved = "";

        if (lastContextRepresentation != null) {
            findStrategies();
        }
        lastContextRepresentation = new ContextRepresentation(dependencyGraph, monitoringAPI);
        lastContextRepresentation.initializeContext();
        //PlanningLogger.logger.info("Strategies that could be enforced. ... "+strategiesThatNeedToBeImproved+" Violated constraints: "+contextRepresentation.getViolatedConstraints());
        HashMap<String, List<ActionEffect>> actionEffects = ActionEffects.getActionEffects();

        int numberOfBrokenConstraints = contextRepresentation
                .countViolatedConstraints();

        PlanningLogger.logger.info("Violated constraints number: " + numberOfBrokenConstraints);

        int lastFixed = 1;
        ArrayList<Pair<ActionEffect, Integer>> result = new ArrayList<Pair<ActionEffect, Integer>>();

        int numberOfRemainingConstraints = numberOfBrokenConstraints;
        if (!strategiesThatNeedToBeImproved.equalsIgnoreCase("") || numberOfBrokenConstraints > 0) {
//		while (contextRepresentation.countViolatedConstraints() > 0
//				&& numberOfRemainingConstraints > 0 && lastFixed>0) {
            Date date = new Date();
            HashMap<Integer,List<Pair<ActionEffect,Integer>>> fixedDirectives = new HashMap<Integer,List<Pair<ActionEffect,Integer>>>();
            HashMap<Integer,List<Pair<ActionEffect,Integer>>> fixedStrategies = new HashMap<Integer,List<Pair<ActionEffect,Integer>>>();
           // PlanningLogger.logger.info("~~~~~~~~~~~Number of actions possible: "+actionEffects.values().size());
            for (List<ActionEffect> list : actionEffects.values()) {

                for (ActionEffect actionEffect : list) {
                    if (checkIfActionPossible(actionEffect)) {
                    	
                    	List<Pair<ActionEffect,Integer>> foundActions = new ArrayList<Pair<ActionEffect,Integer>>();
                    	
                    	
                        for (Pair<ActionEffect, Integer> a : result) {
                            for (int i = 0; i < a.getSecond(); i++) {
                                PlanningLogger.logger.info("Executing the already found action" + a.getFirst().getActionName());
                                contextRepresentation.doAction(a.getFirst());
                                PlanningLogger.logger.info("At " + date.getDay() + "_"
                                        + date.getMonth() + "_" + date.getHours() + "_"
                                        + date.getMinutes()
                                        + ". The violated constraints are the following: "
                                        + contextRepresentation.getViolatedConstraints());

                            }
                        }
                        int initiallyBrokenConstraints = contextRepresentation
                                .countViolatedConstraints();
                        MonitoredCloudService monitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
                        ContextRepresentation beforeActionContextRepresentation = new ContextRepresentation(monitoredCloudService, monitoringAPI);
                        // TODO: Try from 1 to 10 actions of the same type
//						for (int i = 0; i < 10; i++) {
//							for (int current = 0; current < i; current++) {
//								contextRepresentation.doAction(actionEffect);
//							}

                        contextRepresentation.doAction(actionEffect);
                        foundActions.add(contextRepresentation.new Pair<ActionEffect, Integer>(actionEffect, 1));
                       
                        int fixedStr = contextRepresentation.countFixedStrategies(beforeActionContextRepresentation, strategiesThatNeedToBeImproved);
                        PlanningLogger.logger.info("PlanningAlgorithm: Trying the action " + actionEffect.getActionName() + "constraints violated : " + contextRepresentation.getViolatedConstraints() + " Strategies improved " + contextRepresentation.getImprovedStrategies(beforeActionContextRepresentation, strategiesThatNeedToBeImproved));

                        fixedDirectives
                        .put(initiallyBrokenConstraints
                                - contextRepresentation
                                .countViolatedConstraints() + fixedStr, foundActions);
		                fixedStrategies
		                        .put(
		                                fixedStr,foundActions);
                        /////////////////////~~~~~~~~~~~Check complimentary actions needed~~~~~~~~~~~~~~~~//
                        List<String> targets = contextRepresentation.simulateDataImpact(beforeActionContextRepresentation, actionEffect);
		                for (String target:targets){
               			 ActionEffect dataAction=checkActions(target);
               			if (dataAction!=null){
                        	MonitoredCloudService newMonitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
                	        ContextRepresentation beforeContext = new ContextRepresentation(newMonitoredCloudService, monitoringAPI);
                        	int beforeC = contextRepresentation.countViolatedConstraints();
                        	contextRepresentation.doAction(dataAction);
                        	int afterC= contextRepresentation.countViolatedConstraints();
                  			int improvedStrategies=contextRepresentation.countFixedStrategies(beforeContext);
                  			int req = initiallyBrokenConstraints-afterC+improvedStrategies;
                           
                               PlanningLogger.logger.info("PlanningAlgorithm: Trying the action due to DATA " + dataAction.getActionName() + "constraints violated : " + contextRepresentation.getViolatedConstraints() + " Strategies improved " + contextRepresentation.getImprovedStrategies(beforeActionContextRepresentation, strategiesThatNeedToBeImproved));

                               foundActions.add(contextRepresentation.new Pair<ActionEffect, Integer>(dataAction, 1));
                               fixedDirectives
                               .put(req, foundActions);
       		                fixedStrategies
       		                        .put(
       		                                improvedStrategies,foundActions);
                           contextRepresentation.undoAction(dataAction);
                           }
                        }
		                contextRepresentation.undoDataImpactSimulation(beforeActionContextRepresentation, actionEffect);
                         targets = contextRepresentation.simulateLoadImpact(beforeActionContextRepresentation, actionEffect);
                        for (String target:targets){
                        	MonitoredCloudService newMonitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
                	        ContextRepresentation beforeContext = new ContextRepresentation(newMonitoredCloudService, monitoringAPI);
                	       
                        	ActionEffect loadAction=checkActions(target);
                        	if (loadAction!=null){
                        	int beforeC = contextRepresentation.countViolatedConstraints();
                        	contextRepresentation.doAction(loadAction);
                        	int afterC= contextRepresentation.countViolatedConstraints();
                  			int improvedStrategies=contextRepresentation.countFixedStrategies(beforeContext);
                  			int req = initiallyBrokenConstraints-afterC+improvedStrategies;
                           
                               PlanningLogger.logger.info("PlanningAlgorithm: Trying the action due to LOAD " + loadAction.getActionName() + "constraints violated : " + contextRepresentation.getViolatedConstraints() + " Strategies improved " + contextRepresentation.getImprovedStrategies(beforeActionContextRepresentation, strategiesThatNeedToBeImproved));
                               
                               foundActions.add(contextRepresentation.new Pair<ActionEffect, Integer>(loadAction, 1));
                               fixedDirectives
                               .put(req, foundActions);
       		                fixedStrategies
       		                        .put(
       		                                improvedStrategies,foundActions);
                           contextRepresentation.undoAction(loadAction);
                           }
                           }
                        
                        contextRepresentation.undoLoadImpactSimulation(beforeActionContextRepresentation, actionEffect);
                        
                        
                        contextRepresentation.undoAction(actionEffect);
//							for (int current = 0; current < i; current++) {
//								contextRepresentation.undoAction(actionEffect);
//							}

//						}
                        // System.out.println("Action "+actionEffect.getTargetedEntityID()+" "+actionEffect.getActionType()+" fixes "+(numberOfBrokenConstraints-contextRepresentation.countViolatedConstraints())+" constraints.");
                        for (int i = result.size() - 1; i > 0; i--) {
                            //System.out.println("Undoing action "
                            //+ actionEffect.getActionName());
                            for (int j = 0; j < result.get(i).getSecond(); j++) {
                                PlanningLogger.logger.info("Undo-ing the already found action" + result.get(i).getFirst().getActionName());
                                contextRepresentation.undoAction(result.get(i)
                                        .getFirst());
                            }
                        }
                    }
                }
                toCleanup = true;
            }

            int maxAction = -20;
            List<Pair<ActionEffect, Integer>> action = null;

            for (Integer val : fixedDirectives.keySet()) {
                PlanningLogger.logger.info("fixed directives  "+fixedDirectives.get(val).size());
                if (val > maxAction) {
                    maxAction = val;
                    action=fixedDirectives.get(val);
                }

            }
            int minStrat = 0;
            for (Integer v : fixedStrategies.keySet()){
            	if (fixedStrategies.get(v).equals(fixedDirectives.get(maxAction)) && minStrat<v){
            		minStrat=v;
            		action = fixedStrategies.get(minStrat);
            	}
            }
         
            //	PlanningLogger.logger.info("Found action "+ action);
            // Find cloudService = SYBLRMI enforce action with action type,
            if (maxAction>0 && action!=null && !result.contains(action)) {
            	for (Pair<ActionEffect, Integer> actionEffect:action){
            	
            	for (int i=0;i<actionEffect.getSecond();i++){
                PlanningLogger.logger.info("Found action "+(i+1)+"x"
                        + ((ActionEffect) actionEffect.getFirst()).getActionType()
                        + " on "
                        + ((ActionEffect) actionEffect.getFirst())
                        .getTargetedEntityID() + " Number of directives fixed: "
                        + maxAction);
                lastFixed = maxAction;
                Node entity = dependencyGraph.getNodeWithID(((ActionEffect) actionEffect.getFirst())
                        .getTargetedEntityID());
                ((ActionEffect) actionEffect.getFirst()).setTargetedEntity(entity);
                if (maxAction > 0) {
                  //  result.add(actionEffect);
                }
            	}
            	
            }
            	result.addAll(action);	
            } else {
            	
                lastFixed = 0;
            }
            numberOfRemainingConstraints -= lastFixed;

        }
        
        ActionPlanEnforcement actionPlanEnforcement = new ActionPlanEnforcement(enforcementAPI);
        actionPlanEnforcement.enforceResult(result,dependencyGraph);
    }

    @Override
	public void run() {
        while (true) {
            try {
                Thread.sleep(REFRESH_PERIOD);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                PlanningLogger.logger.error(e.toString());
            }

            Node cloudService = monitoringAPI.getControlledService();

            dependencyGraph.setCloudService(cloudService);

            contextRepresentation = new ContextRepresentation(dependencyGraph,
                    monitoringAPI);

            contextRepresentation.initializeContext();

            findAndExecuteBestActions();

        }
    }

    @Override
	public void start() {
        t.start();
    }

    @Override
	public void stop() {
        t.stop();
    }

    @Override
    public void setEffects(String effects) {
        // TODO Auto-generated method stub
        ActionEffects.setActionEffects(effects);
    }

}
