package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitive;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitiveDependency;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitivesDescription;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ServiceElasticityPrimitives;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.ContextRepresentation.Pair;

import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

public class ActionPlanEnforcement {

	EnforcementAPIInterface enforcementAPI = null;
	ElasticityPrimitivesDescription primitivesDescription = null;
	public ActionPlanEnforcement(EnforcementAPIInterface apiInterface) {
		enforcementAPI = apiInterface;
		try{
		InputProcessing inputProcessing = new InputProcessing();
		primitivesDescription=inputProcessing.loadElasticityPrimitivesDescriptionFromFile();
		}catch(Exception e){
			PlanningLogger.logger.error("Failed to load enabled primitives, working with default case");
		}
	}

	public void enforceResult(ArrayList<Pair<ActionEffect, Integer>> result,DependencyGraph dependencyGraph) {
		PlanningLogger.logger.info("Number of actions to enforce" +result.size());
		List<ArrayList<Pair<ActionEffect, Integer>>> paralelRes = parallellizeResult(result);
                for (ArrayList<Pair<ActionEffect, Integer>> actionsToEnf : paralelRes){
                    List<Thread> threadsToExec = new ArrayList<Thread>();
                    PlanningLogger.logger.info("~~~ paralel actions executed "+actionsToEnf.size());
                    for (Pair<ActionEffect,Integer>action:actionsToEnf){
                        EnforceActionInThread enforceActionInThread =new EnforceActionInThread(action.getFirst(), dependencyGraph);

                        Thread t = new Thread(enforceActionInThread);
                        t.start();
                        threadsToExec.add(t);
                    
                        }
                    for(Thread actionInThread:threadsToExec){
                        try {
                            actionInThread.join();
                        } catch (InterruptedException ex) {
                            PlanningLogger.logger.error( "Exception when joininig threads"+ ex.getMessage());
                        }
                    }
                        
                }
//                for (Pair<ActionEffect, Integer> actionEffect : result) {
//			PlanningLogger.logger.info("Enforcing capability "
//					+ actionEffect.getFirst().getActionType() + " on "
//					+ actionEffect.getFirst().getTargetedEntity().getId());
//			boolean foundCapability = false;
//			for (ElasticityCapability capability : actionEffect.getFirst()
//					.getTargetedEntity().getElasticityCapabilities()) {
//				if (capability.getName() != null
//						&& !capability.getName().equalsIgnoreCase("")) {
//					if (capability.getEndpoint() != null
//							&& !capability.getEndpoint().equalsIgnoreCase("")) {
//						foundCapability = true;
//						PlanningLogger.logger.info("Found capability "
//								+ capability.getName()
//								+ " enforcing with capabilities ");
//						enforcementAPI.enforceElasticityCapability(capability,
//								actionEffect.getFirst().getTargetedEntity());
//					}
//				}
//			}
//
//			if (!foundCapability) {
//				if (actionEffect.getSecond() > 0) {
//					for (int i = 0; i < actionEffect.getSecond(); i++) {
//						enforceActionGivenPrimitives(actionEffect.getFirst(), dependencyGraph);
//						
//					}
//				}
//			}
//
//		}
	}
        public class EnforceActionInThread implements Runnable{
        ActionEffect actionEffect;
        DependencyGraph dependencyGraph;
            public EnforceActionInThread(ActionEffect actionEffect, DependencyGraph dependencyGraph){
                this.actionEffect=actionEffect;
                this.dependencyGraph=dependencyGraph;
            }
    @Override
    public void run() {
            PlanningLogger.logger.info("Executing action from thread......................... "+actionEffect.getActionType()+" on "+actionEffect.getTargetedEntityID());
		String actionName = actionEffect.getActionType().toLowerCase();
		for (ElasticityCapability elasticityCapability : actionEffect
				.getTargetedEntity().getElasticityCapabilities()) {
			if (elasticityCapability.getName().equalsIgnoreCase(actionName)) {
				if (!elasticityCapability.getName().toLowerCase().contains("scalein") || (elasticityCapability.getName().toLowerCase().contains("scalein")&& actionEffect.getTargetedEntity().getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size()>1)){
					
				String[] primitives = elasticityCapability
						.getPrimitiveOperations().split(";");
				for (int i = 0; i < primitives.length; i++) {
					if (!enforcePrimitive(primitivesDescription, primitives[i],
							actionEffect.getTargetedEntity(), dependencyGraph))
					{
						PlanningLogger.logger.info("Failed Enforcing "+primitives[i]+", cancelling the entire elasticity capability "+actionEffect.getActionType()+"-"+actionEffect.getTargetedEntityID());
						break;
					}else{
						PlanningLogger.logger.info("Successfully enforced "+primitives[i]+", continuing with capability "+actionEffect.getActionType()+"-"+actionEffect.getTargetedEntityID());

					}
				}
				break;
				}
				}
		}
	
    }


}
 public  List<ArrayList<Pair<ActionEffect,Integer>>> parallellizeResult(ArrayList<Pair<ActionEffect,Integer>> result){
     List<ArrayList<Pair<ActionEffect,Integer>>> parallelizedActions = new ArrayList<ArrayList<Pair<ActionEffect,Integer>>>();
     int i=0;
     int indexInBigList =0;
     parallelizedActions.add(new ArrayList<Pair<ActionEffect,Integer>>());
     List<String> targets = new ArrayList<String>();
     while (i<result.size()) {
         //PlanningLogger.logger.info("Index of result "+i);
         //PlanningLogger.logger.info("Index of parallel "+indexInBigList);
            List<String> actionTargets = getTargetsOfPrimitives(result.get(i).getFirst());
            boolean foundSimilar = false;
            for (String t:actionTargets){
                if (targets.contains(t))foundSimilar=true;
            }
            if (!foundSimilar && actionTargets.size()>0){
               targets.addAll(actionTargets);
                parallelizedActions.get(indexInBigList).add(result.get(i));
            }else{
                indexInBigList+=1;
               parallelizedActions.add(new ArrayList<Pair<ActionEffect,Integer>>());
               parallelizedActions.get(indexInBigList).add(result.get(i));
                targets = new ArrayList<String>();
               targets.addAll(actionTargets);
              
            }
            i++;

     }
     return parallelizedActions;
 }
public List<String> getTargetsOfPrimitives(ActionEffect actionEffect){
    List<String> targets = new ArrayList<String>();
    String ac = actionEffect.getActionType().toLowerCase();
    for (ElasticityCapability elasticityCapability : actionEffect
				.getTargetedEntity().getElasticityCapabilities()) {
			if (elasticityCapability.getName().equalsIgnoreCase(ac)) {
				if (!elasticityCapability.getName().toLowerCase().contains("scalein") || (elasticityCapability.getName().toLowerCase().contains("scalein")&& actionEffect.getTargetedEntity().getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size()>1)){
					
				String[] primitives = elasticityCapability
						.getPrimitiveOperations().split(";");
		for (String primitive: primitives){
		String actionName = primitive;

		if (actionName.contains(".")) {
			targets.add(actionName.split("\\.")[0]);
			actionName = actionName.split("\\.")[1];
		}else{
                    boolean foundCap = false;
                    for (ElasticityCapability capability : actionEffect.getTargetedEntity().getElasticityCapabilities()) {
				if (capability.getName().toLowerCase().contains(actionName)) {
                                    if (capability.getName().toLowerCase().contains("."))
					targets.add (capability.getName().split("\\.")[0].toLowerCase());
				foundCap=true;
                                }
			}
		if (!foundCap){
                    targets.add("");
                }
			}
                }
                                }
                        }
    }
    
return targets;
}
 public void enforceActionGivenPrimitives(
			
			ActionEffect actionEffect, DependencyGraph dependencyGraph) {
		String actionName = actionEffect.getActionType().toLowerCase();
		for (ElasticityCapability elasticityCapability : actionEffect
				.getTargetedEntity().getElasticityCapabilities()) {
			if (elasticityCapability.getName().equalsIgnoreCase(actionName)) {
				if (!elasticityCapability.getName().toLowerCase().contains("scalein") || (elasticityCapability.getName().toLowerCase().contains("scalein")&& actionEffect.getTargetedEntity().getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size()>1)){
					
				String[] primitives = elasticityCapability
						.getPrimitiveOperations().split(";");
				for (int i = 0; i < primitives.length; i++) {
					if (!enforcePrimitive(primitivesDescription, primitives[i],
							actionEffect.getTargetedEntity(), dependencyGraph))
					{
						PlanningLogger.logger.info("Failed Enforcing "+primitives[i]+", cancelling the entire elasticity capability "+actionEffect.getActionType()+"-"+actionEffect.getTargetedEntityID());
						break;
					}else{
						PlanningLogger.logger.info("Successfully enforced "+primitives[i]+", continuing with capability "+actionEffect.getActionType()+"-"+actionEffect.getTargetedEntityID());

					}
				}
				break;
				}
				}
		}
	}

	public Object parseParameter(String param, Node node,
			DependencyGraph dependencyGraph, String IP, String UUID) {
		String result = param;

		if (!param.contains(" ") && !param.contains(".")
				&& !param.contains("$")) {
			if (param.equalsIgnoreCase("IP")) {
				return IP;
			}
			if (param.equalsIgnoreCase("UUID")) {
				return UUID;
			}
		} else {

			if (param.contains("{") && param.contains(".")) {
				
				result=param;
				String REGEX_IP = "(\\{[A-Za-z0-9]+\\}\\.IP)";

				Pattern p = Pattern.compile(REGEX_IP,Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(param); // get a matcher object
				int nbFound = m.groupCount();

				int i=0;
				while (m.find()) {
					String value = m.group();		

					String nodeId = value.substring(1, value.indexOf('.')-1);
					Node searchNode = null;
					for (Node myNode : dependencyGraph.getAllServiceUnits()) {
						if (myNode.getId().toLowerCase()
								.contains(nodeId.toLowerCase())) {
							searchNode = myNode;
							break;
						}
					}
					if (searchNode == null)
						for (Node myNode : dependencyGraph.getAllServiceUnits()) {
							if (myNode.getId().toLowerCase()
									.contains(nodeId.toLowerCase())) {
								searchNode = myNode;
								break;
							}
						}
					String newRegex= "(\\{"+nodeId+"\\}\\.IP)";
					Pattern p1 = Pattern.compile(newRegex);
					Matcher m1 = p1.matcher(result); // get a matcher object
					if (searchNode.getNodeType()==NodeType.VIRTUAL_MACHINE){
						String ip=(String) searchNode.getStaticInformation().get("IP");
						if (ip==null || ip.equalsIgnoreCase(""))
							ip=searchNode.getId();
						result=m1.replaceAll(ip);
					}else{
						Node search = searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
						String ip=(String) search.getStaticInformation().get("IP");
						if (ip==null || ip.equalsIgnoreCase(""))
							ip=search.getId();
						result=m1.replaceAll(ip);
					}
					
				i++;
						
				}
				
				param=result;
				 REGEX_IP = "(\\{[A-Za-z0-9]+\\}\\.UUID)";

				 p = Pattern.compile(REGEX_IP,Pattern.CASE_INSENSITIVE);
				 m = p.matcher(param); // get a matcher object
				 nbFound = m.groupCount();

				 i=0;
				while (m.find()) {
					String value = m.group();		

					String nodeId = value.substring(1, value.indexOf('.')-1);
					Node searchNode = null;
					for (Node myNode : dependencyGraph.getAllServiceUnits()) {
						if (myNode.getId().toLowerCase()
								.contains(nodeId.toLowerCase())) {
							searchNode = myNode;
							break;
						}
					}
					if (searchNode == null)
						for (Node myNode : dependencyGraph.getAllServiceUnits()) {
							if (myNode.getId().toLowerCase()
									.contains(nodeId.toLowerCase())) {
								searchNode = myNode;
								break;
							}
						}
					
					String newRegex= "(\\{"+nodeId+"\\}\\.UUID)";
					Pattern p1 = Pattern.compile(newRegex);
					Matcher m1 = p1.matcher(result); // get a matcher object
					if (searchNode.getNodeType()==NodeType.VIRTUAL_MACHINE){
						String ip=(String) searchNode.getStaticInformation().get("UUID");
						if (ip==null || ip.equalsIgnoreCase(""))
							ip=searchNode.getId();
						result=m1.replaceAll(ip);
					}else{
						Node search = searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
						String ip=(String) search.getStaticInformation().get("UUID");
						if (ip==null || ip.equalsIgnoreCase(""))
							ip=search.getId();
						result=m1.replaceAll(ip);
					}
					
				i++;
						
				}
				
				param=result;

			} 
				if (param.contains("IP") || param.contains("UUID")) {
					String REGEX = "IP";
					Pattern p = Pattern.compile(REGEX);
					Matcher m = p.matcher(param); // get a matcher object
					param = m.replaceAll(IP);
					REGEX = "UUID";
					p = Pattern.compile(REGEX);
					m = p.matcher(param); // get a matcher object
					return m.replaceAll(UUID);
				}
			
		}
		return param;

	}

	public boolean enforcePrimitive  (
			ElasticityPrimitivesDescription primitivesDescription,
			String primitive, Node node, DependencyGraph dependencyGraph)  {
		String target = "";
		String actionName = primitive;

		if (actionName.contains(".")) {
			target = actionName.split("\\.")[0];
			actionName = actionName.split("\\.")[1];
		}
		if (target.equalsIgnoreCase("")) {
			for (ElasticityCapability capability : node
					.getElasticityCapabilities()) {
				if (capability.getName().toLowerCase().contains(actionName)) {
                                    if (capability.getName().toLowerCase().contains("."))
					target = capability.getName().split("\\.")[0].toLowerCase();
				}
			}
		}
		if (target.equalsIgnoreCase("")) {
			switch (actionName.toLowerCase()) {
			case "scaleout":
				return enforcementAPI.scaleout(node);
				
			case "scalein":
				return enforcementAPI.scalein(node);
			default:
				return enforcementAPI.enforceAction(actionName, node);
			}
		} else {
			boolean res=true;
			for (ServiceElasticityPrimitives serviceElasticityPrimitives : primitivesDescription
					.getElasticityPrimitives()) {
				if (serviceElasticityPrimitives.getId()
						.equalsIgnoreCase(target)) {
					
					for (ElasticityPrimitive elasticityPrimitive : serviceElasticityPrimitives
							.getElasticityPrimitives()) {
						if (elasticityPrimitive.getId().equalsIgnoreCase(
								actionName)) {

							if (elasticityPrimitive.getParameters()
									.equalsIgnoreCase("")){
								boolean x=enforcementAPI.enforceAction(target,
										elasticityPrimitive.getMethodName(), node);
										if (x==false) res=false;
							}
							else {
								List<ElasticityPrimitiveDependency> elasticityPrimitiveDependencies = elasticityPrimitive
										.getPrimitiveDependencies();
								List<ElasticityPrimitiveDependency> beforePrimitives = new ArrayList<ElasticityPrimitiveDependency>();
								
								List<ElasticityPrimitiveDependency> afterPrimitives = new ArrayList<ElasticityPrimitiveDependency>();
								for (ElasticityPrimitiveDependency dependency:elasticityPrimitiveDependencies){
									if (dependency.getDependencyType().equalsIgnoreCase("BEFORE")){
										beforePrimitives.add(dependency);
										}else{
											afterPrimitives.add(dependency);

										}
									//TODO:
								}
								Object[] parameters = new Object[elasticityPrimitive
										.getParameters().split(",").length];
								Object transferredParameters;
								String ip = "";
								String uuid = "";
								if (node.getNodeType() == NodeType.VIRTUAL_MACHINE) {
									ip = (String) node.getStaticInformation()
											.get("IP");
									if (ip==null|| ip.equalsIgnoreCase("")) {
										ip = node.getId();
									}
									uuid = (String) node.getStaticInformation()
											.get("UUID");
									if (uuid.equalsIgnoreCase("")) {
										uuid = node.getId();
									}
								} else {
									if (node.getNodeType() == NodeType.SERVICE_UNIT) {
										Node newNode=node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
										ip = (String) newNode.getStaticInformation()
												.get("IP");
										if (ip==null || ip.equalsIgnoreCase("")) {
											ip = newNode.getId();
										}
										uuid = (String) newNode.getStaticInformation()
												.get("UUID");
										if (uuid.equalsIgnoreCase("")) {
											uuid = newNode.getId();
										}	
									}
								}
								int i = 0;
								for (String parameter : elasticityPrimitive
										.getParameters().split(",")) {
									parameter.trim();
									parameters[i] = parseParameter(parameter,
											node, dependencyGraph, ip, uuid);
									i += 1;
								}
								for (ElasticityPrimitiveDependency elasticityPrimitiveDependency : beforePrimitives) {
									// check before primitives
									boolean x =enforcePrimitive(primitivesDescription,
											elasticityPrimitiveDependency
													.getPrimitiveID(), node,
											dependencyGraph);
									if (!x) res=false;

								}

								String methodName = elasticityPrimitive
										.getMethodName();
								
								if (methodName.equalsIgnoreCase(""))
								{
									boolean x=enforcementAPI.enforceAction(target,
											actionName, node, parameters);
									if (!x) res=false;
								}
								else{
									boolean x=enforcementAPI.enforceAction(target,
											methodName, node, parameters);
									if (!x) res=false;

								}
								
								for (ElasticityPrimitiveDependency elasticityPrimitiveDependency : afterPrimitives) {
									// check before primitives
									boolean x=enforcePrimitive(primitivesDescription,
											elasticityPrimitiveDependency
													.getPrimitiveID(), node,
											dependencyGraph);
									if (!x) res=false;
								}

							}
							break;
						}
					}
					break;
				}

			}
			return res;
//			switch (actionName) {
//			case "scaleout":
//				SYBLDirectivesEnforcementLogger.logger.info("Calling Scale out from planning ");
//
//				enforcementAPI.scaleout(target, node);
//				break;
//			case "scalein":
//				SYBLDirectivesEnforcementLogger.logger.info("Calling Scale in from planning ");
//
//				enforcementAPI.scalein(target, node);
//				break;
//			default:
//				if (target.equalsIgnoreCase("")) {
//					enforcementAPI.enforceAction(target, actionName, node);
//				}
//
//				break;
//			}
		}
	}

	public void enforceAction(ActionEffect actionEffect) {
		String target = "";
		String actionName = actionEffect.getActionType().toLowerCase();
		if (actionName.contains(".")) {
			target = actionName.split("\\.")[0];
			actionName = actionName.split("\\.")[1];
		}
		if (target.equalsIgnoreCase("")) {
			for (ElasticityCapability capability : actionEffect
					.getTargetedEntity().getElasticityCapabilities()) {
				if (capability.getName().toLowerCase().contains(actionName)) {
                                    if (capability.getName().toLowerCase().contains("."))
					target = capability.getName().split("\\.")[0].toLowerCase();
				}
			}
		}
		if (target.equalsIgnoreCase("")) {
			switch (actionName.toLowerCase()) {
			case "scaleout":
				enforcementAPI.scaleout(actionEffect.getTargetedEntity());
				break;
			case "scalein":
				enforcementAPI.scalein(actionEffect.getTargetedEntity());
				break;
			default:
				enforcementAPI.enforceAction(actionEffect.getActionType(),
						actionEffect.getTargetedEntity());
				break;
			}
		} else {
			switch (actionName.toLowerCase()) {
			case "scaleout":
				enforcementAPI.scaleout(target,
						actionEffect.getTargetedEntity());
				break;
			case "scalein":
				enforcementAPI
						.scalein(target, actionEffect.getTargetedEntity());
				break;
			default:
				if (target.equalsIgnoreCase("")) {
					enforcementAPI.enforceAction(target,
							actionEffect.getActionType(),
							actionEffect.getTargetedEntity());
				}

				break;
			}
		}
	}

	public void enforceFoundActions(ContextRepresentation contextRepresentation) {

		for (ActionEffect actionEffect : contextRepresentation
				.getActionsAssociatedToContext()) {
			enforceAction(actionEffect);
		}

	}
public void enforceFoundActions(List<ActionEffect> actions,DependencyGraph dependencyGraph) {

		for (ActionEffect actionEffect : actions) {
                    actionEffect.setTargetedEntity(dependencyGraph.getNodeWithID(actionEffect.getTargetedEntityID()));
			enforceAction(actionEffect);
		}

	}
}
