
package at.ac.tuwien.dsg.rSybl.planningEngine;
/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Condition;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffects;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

public class PlanningGreedyAlgorithm implements PlanningAlgorithmInterface {
	private Thread t;
	private boolean toCleanup=true;
	private ContextRepresentation contextRepresentation;
	private MonitoringAPIInterface monitoringAPI;
	private EnforcementAPIInterface enforcementAPI;
	private DependencyGraph dependencyGraph;
	private ContextRepresentation lastContextRepresentation;
	private String strategiesThatNeedToBeImproved ="";
	private int REFRESH_PERIOD = 120000;
	public class Pair<A, B> {
		private A first;
		private B second;

		public Pair(A first, B second) {
			super();
			this.first = first;
			this.second = second;
		}

		public int hashCode() {
			int hashFirst = first != null ? first.hashCode() : 0;
			int hashSecond = second != null ? second.hashCode() : 0;

			return (hashFirst + hashSecond) * hashSecond + hashFirst;
		}

		public boolean equals(Object other) {
			if (other instanceof Pair) {
				Pair otherPair = (Pair) other;
				return ((this.first == otherPair.first || (this.first != null
						&& otherPair.first != null && this.first
							.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
						&& otherPair.second != null && this.second
							.equals(otherPair.second))));
			}

			return false;
		}

		public String toString() {
			return "(" + first + ", " + second + ")";
		}

		public A getFirst() {
			return first;
		}

		public void setFirst(A first) {
			this.first = first;
		}

		public B getSecond() {
			return second;
		}

		public void setSecond(B second) {
			this.second = second;
		}
	}

	public PlanningGreedyAlgorithm(DependencyGraph cloudService,
			MonitoringAPIInterface monitoringAPI,EnforcementAPIInterface enforcementAPI) {
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
			if (entity.getNodeType()==NodeType.CLOUD_SERVICE) {
				List<String> ips = entity.getAssociatedIps();
				PlanningLogger.logger.info("For action "+actionEffect.getActionName()+entity.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size()+" hosts");
				if (entity.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size() > 4)
					return true;
			}
			if (entity.getNodeType()==NodeType.SERVICE_TOPOLOGY) {
				
				Node master = dependencyGraph.findParentNode(entity.getId());
				List<String> ips = master.getAssociatedIps();
				int numberPrivateIps = 0;
				for (String ip : ips) {
					if (ip.split("\\.")[0].length() == 2) {
						numberPrivateIps++;
					}
				}
				if (entity.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size() > 4)
					return true;
			}
		}
		return possible;
	}

	public void findStrategies(){

		
		for (ElasticityRequirement elasticityRequirement:dependencyGraph.getAllElasticityRequirements()){
				SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
				MonitoredEntity monitoredEntity = contextRepresentation.findMonitoredEntity(syblSpecification.getComponentId());
				if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());
			for (Strategy strategy:syblSpecification.getStrategy()){
				Condition condition = strategy.getCondition();		
				
				if (contextRepresentation.evaluateCondition(condition, monitoredEntity)){
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")||strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
						if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")){
							//PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
							
							if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())<=lastContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter())){
								strategiesThatNeedToBeImproved+=strategy.getId()+ " ";
							}
						}
						if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
						
							PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+lastContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
							
							if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())>=lastContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter())){
								strategiesThatNeedToBeImproved+=strategy.getId()+ " ";
							}
						}
					}

				}
				}
		}

	}
	public void findAndExecuteBestActions() {
		
			strategiesThatNeedToBeImproved="";
			
			if (lastContextRepresentation!=null)
			findStrategies();
			lastContextRepresentation=new ContextRepresentation(dependencyGraph, monitoringAPI);
			lastContextRepresentation.initializeContext();
			//PlanningLogger.logger.info("Strategies that could be enforced. ... "+strategiesThatNeedToBeImproved+" Violated constraints: "+contextRepresentation.getViolatedConstraints());
		HashMap<String, List<ActionEffect>> actionEffects = ActionEffects.getActionEffects();

		int numberOfBrokenConstraints = contextRepresentation
				.countViolatedConstraints();

		int lastFixed = 1;
		ArrayList<Pair<ActionEffect, Integer>> result = new ArrayList<Pair<ActionEffect, Integer>>();
	
		int numberOfRemainingConstraints=numberOfBrokenConstraints;
		if (!strategiesThatNeedToBeImproved.equalsIgnoreCase("") || numberOfBrokenConstraints>0){
//		while (contextRepresentation.countViolatedConstraints() > 0
//				&& numberOfRemainingConstraints > 0 && lastFixed>0) {
			Date date = new Date();
			HashMap<Pair<ActionEffect, Integer>, Integer> fixedDirectives = new HashMap<Pair<ActionEffect, Integer>, Integer>();
			HashMap<Pair<ActionEffect, Integer>, Integer> fixedStrategies = new HashMap<Pair<ActionEffect, Integer>, Integer>();
			
			for (List<ActionEffect> list : actionEffects.values()) {
				
				for (ActionEffect actionEffect : list)
					if (checkIfActionPossible(actionEffect)) {
						for (Pair<ActionEffect, Integer> a : result) {
							for (int i = 0; i < a.getSecond(); i++) {
								PlanningLogger.logger.info("Executing the already found action"+a.getFirst().getActionName());
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
						
						int fixedStr = contextRepresentation.countFixedStrategies(beforeActionContextRepresentation,strategiesThatNeedToBeImproved);
						PlanningLogger.logger.info("Trying the action "+actionEffect.getActionName()+"constraints violated : "+ contextRepresentation.getViolatedConstraints()+" Strategies improved "+contextRepresentation.getImprovedStrategies(beforeActionContextRepresentation,strategiesThatNeedToBeImproved));
						
						fixedDirectives
									.put(new Pair<ActionEffect, Integer>(
											actionEffect, 1),
											initiallyBrokenConstraints
													- contextRepresentation
															.countViolatedConstraints()+fixedStr);
						fixedStrategies
						.put(new Pair<ActionEffect, Integer>(
								actionEffect, 1),
								fixedStr);
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
								PlanningLogger.logger.info("Undo-ing the already found action"+result.get(i).getFirst().getActionName());
								contextRepresentation.undoAction(result.get(i)
										.getFirst());
							}
						}
					}
				toCleanup=true;
			}

			int maxAction = -20;
			Pair action = null;
			
			for (Integer val : fixedDirectives.values()) {
				if (val > maxAction) {
					maxAction = val;
				}
				
			}
			Pair actionTargetingComponent = null;
			Pair actionTargetingComponentTopology = null;
			int minStrat = 100;
			for (Pair<ActionEffect, Integer> pair : fixedDirectives.keySet()) {
				if (fixedDirectives.get(pair) == maxAction) {
					
					for(Pair<ActionEffect,Integer> p:fixedStrategies.keySet()){
					//	PlanningLogger.logger.info("Action Effect "+p.getFirst().getActionName()+" strategies "+fixedStrategies.get(p));
						if (p.getFirst().equals(pair.getFirst())){
							if (minStrat>fixedStrategies.get(p)){
								if (dependencyGraph.getNodeWithID(pair.getFirst().getTargetedEntityID()).getNodeType()==NodeType.SERVICE_UNIT)
									actionTargetingComponent = pair;
								else
									actionTargetingComponentTopology = pair;
								minStrat=fixedStrategies.get(p);
							}
						}
					}
					
				}
			}
			
			if (actionTargetingComponent != null)
				action = actionTargetingComponent;
			else
				action = actionTargetingComponentTopology;
		//	PlanningLogger.logger.info("Found action "+ action);
			// Find cloudService = SYBLRMI enforce action with action type,
			if (maxAction > 0 && !result.contains(action)) {
				PlanningLogger.logger.info("Found action "
						+ ((ActionEffect) action.getFirst()).getActionType()
						+ " on "
						+ ((ActionEffect) action.getFirst())
								.getTargetedEntityID() + " Number of directives fixed: "
						+ fixedDirectives.get(action));
				lastFixed = fixedDirectives.get(action);
				Node entity = dependencyGraph.getNodeWithID(((ActionEffect) action.getFirst())
						.getTargetedEntityID());
				((ActionEffect)action.getFirst()).setTargetedEntity(entity);
				if (fixedDirectives.get(action) > 0) {
					result.add(action);
				}

			} else {
				lastFixed = 0;
			}
			numberOfRemainingConstraints-=lastFixed;
			
	//	}

		for (Pair<ActionEffect, Integer> actionEffect : result)
			if (actionEffect.getFirst().getActionType()
					.equalsIgnoreCase("scaleout")) {
			//	for (int i = 0; i < actionEffect.getSecond(); i++) {
				enforcementAPI.scaleout(actionEffect.getFirst()
						.getTargetedEntity());
				
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						PlanningLogger.logger.error(e.toString());
					}
				
		//		}
				//PlanningLogger.logger.info("Scale out for "+ actionEffect.getFirst().getTargetedEntity() + "  ");

			} else {
				if (actionEffect.getFirst().getActionType()
						.equalsIgnoreCase("scalein")) {
//					for (int i = 0; i < actionEffect.getSecond(); i++) {
					enforcementAPI.scalein(actionEffect.getFirst()
							.getTargetedEntity());
				
						
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							PlanningLogger.logger.error(e.toString());
						}
					
//					}
				//	PlanningLogger.logger.info("Scale in for "+ actionEffect.getFirst().getTargetedEntity());

				}
			}
		}
	}

	

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

	public void start() {
		t.start();
	}

	public void stop() {
		t.stop();
	}
}
