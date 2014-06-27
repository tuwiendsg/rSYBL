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

package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.lang.reflect.Method;
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
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Condition;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitoring;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.UnaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.UnaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription.SYBLDescriptionParser;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;




public class ContextRepresentation {
	
	private MonitoredCloudService monitoredCloudService = new MonitoredCloudService(); 
	private DependencyGraph dependencyGraph;
	private MonitoringAPIInterface monitoringAPI ;
	
	private List<ActionEffect> actionsAssociatedToContext=new ArrayList<ActionEffect>();
	private double PREVIOUS_CS_UNHEALTHY_STATE =0;
	private double CS_UNHEALTHY_STATE=0;
	public ContextRepresentation(DependencyGraph cloudService, MonitoringAPIInterface monitoringAPI){
		this.dependencyGraph=cloudService;
		this.monitoringAPI=monitoringAPI;
		
	}
	public ContextRepresentation(MonitoredCloudService cloudService, MonitoringAPIInterface monitoringAPI){
		monitoredCloudService=cloudService;
		this.monitoringAPI=monitoringAPI;
		
	}
	
	public void initializeContext(){
		//find all targeted metrics
		createMonitoredService();	
	}
	
	
	
	private MonitoredEntity findTargetedMetrics(Node entity,MonitoredEntity monitoredEntity){
		monitoredEntity.setId(entity.getId());
			SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();

		for (ElasticityRequirement elasticityRequirement :entity.getElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
			for (Strategy strategy:syblSpecification.getStrategy()){
					String methodName="";
	   				Double value = 0.0;	

					if (methodName.equals("") && strategy.getToEnforce().getParameter()!=null && strategy.getToEnforce().getParameter()!=""){
						try {
							
								value=	monitoringAPI.getMetricValue(strategy.getToEnforce().getParameter(), entity);
							}catch(Exception e){
							e.printStackTrace();
						}
						
					}
	   				
					
	   				monitoredEntity.setMonitoredValue(strategy.getToEnforce().getParameter(),value );	
		   			if (strategy.getCondition()!=null)
	   				for (BinaryRestrictionsConjunction restrictions:strategy.getCondition().getBinaryRestriction()){
		   			for (BinaryRestriction restriction:restrictions.getBinaryRestrictions()){
		   				String right = restriction.getRightHandSide().getMetric();
		   				String left = restriction.getLeftHandSide().getMetric();
		   				String metric="";
		   				if (right!=null)
		   					metric =right;
		   				else metric=left;
		   				 methodName = descriptionParser.getMethod(metric);
		   				 value = 0.0;	
		   				if (!methodName.equals("")) {
							try {
								Class partypes[] = new Class[1];
								Object[] parameters = new Object[1];
								parameters[0]=entity;
								partypes[0]= Node.class;
								Method method = MonitoringInterface.class.getMethod(methodName,partypes);
								value = (Double)method.invoke(monitoringAPI,parameters);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							 try {
								value= (Double) monitoringAPI.getMetricValue(metric, entity);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								PlanningLogger.logger.error("Metric "+metric+"not valid");
							}

						}
		   				monitoredEntity.setMonitoredValue(metric,value );	
		   				
		   			}
		   			}
	   			}
   		
			
	   			for (Monitoring monitoring:syblSpecification.getMonitoring()){
					monitoredEntity.setMonitoredVar(monitoring.getMonitor().getEnvVar(),monitoring.getMonitor().getMetric());
		   			String methodName = descriptionParser.getMethod(monitoring.getMonitor().getMetric());
					Double value = 0.0;	
					if (!methodName.equals("")) {
							try {
								Class partypes[] = new Class[1];
								Object[] parameters = new Object[1];
								parameters[0]=entity;
								partypes[0]=Node.class;
								
								Method method = MonitoringInterface.class.getMethod(methodName,partypes);

								value = (Double)method.invoke(monitoringAPI,parameters);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							
						 try {
							value= (Double) monitoringAPI.getMetricValue(monitoring.getMonitor().getMetric(), entity);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							PlanningLogger.logger.error("Metric "+monitoring.getMonitor().getMetric()+"not valid");
						}
							
						}
					monitoredEntity.setMonitoredValue(monitoring.getMonitor().getMetric(),value );	
	   				
	   		}
	   			
	   			for (Constraint constraint:syblSpecification.getConstraint()){
		   			for (BinaryRestrictionsConjunction restrictions:constraint.getToEnforce().getBinaryRestriction()){
		   			for (BinaryRestriction restriction:restrictions.getBinaryRestrictions()){
		   				String right = restriction.getRightHandSide().getMetric();
		   				String left = restriction.getLeftHandSide().getMetric();
		   				String metric="";
		   				if (right!=null)
		   					metric =right;
		   				else metric=left;
		   				String methodName = descriptionParser.getMethod(metric);
		   				Double value = 0.0;	
		   				if (!methodName.equals("")) {
							try {
								Class partypes[] = new Class[1];
								Object[] parameters = new Object[1];
								parameters[0]=entity;
								partypes[0]= Node.class;
								Method method = MonitoringInterface.class.getMethod(methodName,partypes);
								value = (Double)method.invoke(monitoringAPI,parameters);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							 try {
								value= (Double) monitoringAPI.getMetricValue(metric, entity);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								PlanningLogger.logger.error("Metric "+metric+"not valid");
							}

						}
		   				monitoredEntity.setMonitoredValue(metric,value );	
		   				
		   			}
		   			}
		   			if (constraint.getCondition()!=null)
		   			for (BinaryRestrictionsConjunction restrictions:constraint.getCondition().getBinaryRestriction()){
		   			for (BinaryRestriction restriction:restrictions.getBinaryRestrictions()){
		   				String right = restriction.getRightHandSide().getMetric();
		   				String left = restriction.getLeftHandSide().getMetric();
		   				String metric="";
		   				if (right!=null)
		   					metric =right;
		   				else metric=left;
		   				String methodName = descriptionParser.getMethod(metric);
		   				Double value = 0.0;	
		   				if (!methodName.equals("")) {
							try {
								Class partypes[] = new Class[1];
								Object[] parameters = new Object[1];
								parameters[0]=entity;
								partypes[0]= Node.class;
								Method method = MonitoringInterface.class.getMethod(methodName,partypes);
								value = (Double)method.invoke(monitoringAPI,parameters);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							 try {
								value= (Double) monitoringAPI.getMetricValue(metric, entity);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								PlanningLogger.logger.error("Metric " +metric+" not valid");
							}

						}
		   				monitoredEntity.setMonitoredValue(metric,value );	
		   				
		   			}
		   			}
	   	}
	}
		return monitoredEntity;
	}
	private void propagateMonitoredMetrics(){
		List<MonitoredComponentTopology> monitoredTopologies = new ArrayList<MonitoredComponentTopology>();
		monitoredTopologies.addAll(monitoredCloudService.getMonitoredTopologies());
		while (!monitoredTopologies.isEmpty()){
			Node entity = dependencyGraph.getNodeWithID(monitoredTopologies.get(0).getId());
			for (Node n:entity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)){
					Double v1 = -1.0;
					MonitoredEntity newMon = findMonitoredEntity(n.getId());
	   				if (entity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)!=null)
					for (String metric: monitoredTopologies.get(0).getMonitoredData().keySet()){
                                            try {
												v1=monitoringAPI.getMetricValue(metric, n);
											} catch (Exception e) {
												PlanningLogger.logger.error("Metric " +metric+" not valid");

											}
					if (v1>-1 && newMon!=null)
					findMonitoredEntity(n.getId()).setMonitoredValue(metric, v1);
					}
				}
			monitoredTopologies.remove(0);
		}
	}
	private MonitoredCloudService createMonitoredService(){
		
		setMonitoredCloudService((MonitoredCloudService) findTargetedMetrics(dependencyGraph.getCloudService(), getMonitoredCloudService()));
		
		
		List<Node> topologies =new ArrayList<Node>();
		topologies.addAll(dependencyGraph.getCloudService().getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY));
		
			
		for (Node currentTopology:topologies){
			MonitoredComponentTopology monitoredTopology = new MonitoredComponentTopology();
			monitoredTopology = (MonitoredComponentTopology) findTargetedMetrics( currentTopology,monitoredTopology);
			monitoredTopology.setId(currentTopology.getId());
		    if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)!=null)
			for(Node componentTopology: currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)){
		    	MonitoredComponentTopology monitoredTopology1 = new MonitoredComponentTopology();
				monitoredTopology1 = (MonitoredComponentTopology) findTargetedMetrics(componentTopology,monitoredTopology1);
				monitoredTopology1.setId(componentTopology.getId());
				for (Node component:componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_UNIT)){
					MonitoredComponent monitoredComponent = new MonitoredComponent();
					monitoredComponent = (MonitoredComponent) findTargetedMetrics(component,monitoredComponent);
					monitoredTopology1.addMonitoredComponent(monitoredComponent);
				}
				monitoredTopology.addMonitoredTopology(monitoredTopology1);

		    }
		    for(Node component: currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_UNIT)){
		    	MonitoredComponent monitoredComponent = new MonitoredComponent();
				monitoredComponent = (MonitoredComponent) findTargetedMetrics(component,monitoredComponent);
				monitoredTopology.addMonitoredComponent(monitoredComponent);
		    }
			getMonitoredCloudService().addMonitoredTopology(monitoredTopology);

		}
		propagateMonitoredMetrics();

		return getMonitoredCloudService();
	}
	
	public void doAction(ActionEffect action){
		//PlanningLogger.logger.info("~~~~~~~~~~~~~~Trying action ~~~ "+action.getActionName());
		for (String currentMetric:getMonitoredCloudService().getMonitoredMetrics()){
			if (action.getActionEffectForMetric(currentMetric,getMonitoredCloudService().getId())!=null){
                         Double oldValue = monitoredCloudService.getMonitoredValue(currentMetric);   
                        // PlanningLogger.logger.info("Setting effect for "+getMonitoredCloudService().getId());
			getMonitoredCloudService().setMonitoredValue(currentMetric, oldValue + action.getActionEffectForMetric(currentMetric,getMonitoredCloudService().getId()) );
		}
                }
		for (MonitoredComponentTopology componentTopology:getMonitoredCloudService().getMonitoredTopologies()){
			for (String currentMetric:componentTopology.getMonitoredMetrics()){
				if(action.getActionEffectForMetric(currentMetric,componentTopology.getId())!=null){
                                 Double oldValue = componentTopology.getMonitoredValue(currentMetric);   
				componentTopology.setMonitoredValue(currentMetric, oldValue +action.getActionEffectForMetric(currentMetric,componentTopology.getId()));
				 //PlanningLogger.logger.info("Setting effect for "+currentMetric+componentTopology.getId()+" new value"+oldValue +action.getActionEffectForMetric(currentMetric,componentTopology.getId()));
				}
                        }
			for (MonitoredComponentTopology componentTopology2:componentTopology.getMonitoredTopologies()){
				for (String currentMetric:componentTopology2.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,componentTopology2.getId())!=null){
                                         Double oldValue = componentTopology2.getMonitoredValue(currentMetric);   
                                        componentTopology2.setMonitoredValue(currentMetric, oldValue +action.getActionEffectForMetric(currentMetric,componentTopology2.getId()) );
                                        //PlanningLogger.logger.info("Setting effect for "+currentMetric+componentTopology.getId()+" new value"+oldValue +action.getActionEffectForMetric(currentMetric,componentTopology.getId()));
                        									}
                                        }
				for (MonitoredComponent comp:componentTopology2.getMonitoredComponents()){
					for (String currentMetric:comp.getMonitoredMetrics()){
						if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null){
                                                     Double oldValue = comp.getMonitoredValue(currentMetric);
                                                      Double newValue =   oldValue +action.getActionEffectForMetric(currentMetric,comp.getId());
                                                     // PlanningLogger.logger.info("Setting effect for "+currentMetric+comp.getId()+" new value"+oldValue +action.getActionEffectForMetric(currentMetric,comp.getId()));
                                                     comp.setMonitoredValue(currentMetric, newValue );
                                                }
					}
				}
			}
			for (MonitoredComponent comp:componentTopology.getMonitoredComponents()){
				for (String currentMetric:comp.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null)
                                        {
                                             Double oldValue = comp.getMonitoredValue(currentMetric);
                                                  Double newValue =   oldValue +action.getActionEffectForMetric(currentMetric,comp.getId());
                                                 // PlanningLogger.logger.info("Setting effect for "+currentMetric+comp.getId()+" new value"+oldValue +action.getActionEffectForMetric(currentMetric,comp.getId()));
					comp.setMonitoredValue(currentMetric, newValue );
                                        }
                                        }
			}
		}
	}
	public void undoAction(ActionEffect action){
		//PlanningLogger.logger.info("~~~~~~~~~~~~~~Undoing action ~~~ "+action.getActionName());
		for (String currentMetric:getMonitoredCloudService().getMonitoredMetrics()){
			if (action.getActionEffectForMetric(currentMetric,getMonitoredCloudService().getId())!=null){
                            Double oldValue = monitoredCloudService.getMonitoredValue(currentMetric);
                            monitoredCloudService.setMonitoredValue(currentMetric, oldValue +(-1)*action.getActionEffectForMetric(currentMetric,getMonitoredCloudService().getId()) );
                        
                        }
                        }
		for (MonitoredComponentTopology componentTopology:getMonitoredCloudService().getMonitoredTopologies()){
			for (String currentMetric:componentTopology.getMonitoredMetrics()){
				if (action.getActionEffectForMetric(currentMetric,componentTopology.getId())!=null){
                                    Double oldValue = componentTopology.getMonitoredValue(currentMetric);
                                    componentTopology.setMonitoredValue(currentMetric, oldValue +(-1)*action.getActionEffectForMetric(currentMetric,componentTopology.getId()));
			
                                }}
			for (MonitoredComponentTopology componentTopology2:componentTopology.getMonitoredTopologies()){
				for (String currentMetric:componentTopology2.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,componentTopology2.getId())!=null){
					 Double oldValue = componentTopology2.getMonitoredValue(currentMetric);
                                   
                                            componentTopology2.setMonitoredValue(currentMetric, componentTopology2.getMonitoredValue(currentMetric) +(-1)*action.getActionEffectForMetric(currentMetric,componentTopology2.getId()) );
                                        }
                                        }
				for (MonitoredComponent comp:componentTopology2.getMonitoredComponents()){
					for (String currentMetric:comp.getMonitoredMetrics()){
						if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null){
						 Double oldValue = comp.getMonitoredValue(currentMetric);
                                   
                                                    comp.setMonitoredValue(currentMetric,oldValue +(-1)*action.getActionEffectForMetric(currentMetric,comp.getId()) );
					}}
				}
			}
			for (MonitoredComponent comp:componentTopology.getMonitoredComponents()){
				for (String currentMetric:comp.getMonitoredMetrics()){
					if (action.getActionEffectForMetric(currentMetric,comp.getId())!=null){
                                         Double oldValue = comp.getMonitoredValue(currentMetric);
					comp.setMonitoredValue(currentMetric, oldValue +(-1)*action.getActionEffectForMetric(currentMetric,comp.getId()) );
				}}
			}
		}
		
	}
	public MonitoredEntity findMonitoredEntity(String id){
		boolean found=false;
		if (!found){
			if (id.equalsIgnoreCase(getMonitoredCloudService().getId())){
				found = true;
				return getMonitoredCloudService();
			}
		}

		List<MonitoredComponentTopology> topologies =new ArrayList<MonitoredComponentTopology>();
		if (getMonitoredCloudService().getMonitoredTopologies()!=null)
		topologies.addAll(getMonitoredCloudService().getMonitoredTopologies());
		
		List<MonitoredComponent> componentsToExplore = new ArrayList<MonitoredComponent>();
		while (!found && !topologies.isEmpty()){
			MonitoredComponentTopology currentTopology = topologies.get(0);
			if (currentTopology!=null){
				//PlanningLogger.logger.info("id "+id+" current topology "+currentTopology+ "  "+ currentTopology.getId()+" ");
				
			if (currentTopology.getId().equalsIgnoreCase(id)){
				found=true;
				return currentTopology;
			}else{
				if (currentTopology.getMonitoredTopologies()!=null && currentTopology.getMonitoredTopologies().size()>0)
					topologies.addAll(currentTopology.getMonitoredTopologies());
				if (currentTopology.getMonitoredComponents()!=null && currentTopology.getMonitoredComponents().size()>0)
				componentsToExplore.addAll(currentTopology.getMonitoredComponents());
			}
			if (currentTopology.getMonitoredComponents()!=null && currentTopology.getMonitoredComponents().size()>0)
				componentsToExplore.addAll(currentTopology.getMonitoredComponents());
		}
			topologies.remove(0);
}
		
		while (!found && !componentsToExplore.isEmpty()){
			MonitoredComponent component =componentsToExplore.get(0);
			componentsToExplore.remove(0);
			if (component.getId().equalsIgnoreCase(id)){
				//System.out.println(component.getId());
				found=true;
				return component;
			}
		}
		return null;
	}
	public String getViolatedConstraints(){
		String constr = "";
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());

			//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
			MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
			if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());

			for (Constraint constraint:syblSpecification.getConstraint()){
				
				if (evaluateCondition(constraint.getCondition(),monitoredEntity) && !evaluateCondition(constraint.getToEnforce(), monitoredEntity)) constr+=constraint.getId()+" ";
					
		}
		}
		return constr;
	}
	public String getImprovedStrategies(ContextRepresentation previousContextRepresentation,String strategiesNeedingToBe){
		String str = "";
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
		//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
				MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
				if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());
			for (Strategy strategy:syblSpecification.getStrategy()){
				Condition condition = strategy.getCondition();		
				if (strategiesNeedingToBe.contains(strategy.getId())){
				if (evaluateCondition(condition, monitoredEntity)){
				if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")||strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")){
						//PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
						
						if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())>previousContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter())){
							str+=strategy.getId()+ " ";
						}
					}
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
					//	PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
						
						if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())<previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter())){
							str+=strategy.getId()+ " ";
						}
					}
				}
			}}
			}
		}
		return str;	}
	public double quantifyBinaryRestriction(BinaryRestriction binaryRestriction,MonitoredEntity monitoredEntity){
		double fulfilled=0.0;
		Double currentLeftValue=0.0;
		Double currentRightValue = 0.0;
		if (binaryRestriction.getLeftHandSide().getMetric()!=null){
			String metric = binaryRestriction.getLeftHandSide().getMetric();
			//PlanningLogger.logger.info(monitoredEntity+" "+metric);
			currentLeftValue = monitoredEntity.getMonitoredValue(metric);
			if (currentLeftValue<0){
				if (monitoredEntity.getMonitoredVar(metric)!=null)
				currentLeftValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
				else currentRightValue=0.0;
			}
			currentRightValue=Double.parseDouble(binaryRestriction.getRightHandSide().getNumber());
		}else
		if (binaryRestriction.getRightHandSide().getMetric()!=null){
			String metric = binaryRestriction.getRightHandSide().getMetric();
			currentRightValue = monitoredEntity.getMonitoredValue(metric);
			//System.out.println("Current value for metric is  "+ currentRightValue);
			if (currentRightValue<0){
				if (monitoredEntity.getMonitoredVar(metric)!=null)
				currentRightValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
				else currentRightValue=0.0;
			}
			currentLeftValue=Double.parseDouble(binaryRestriction.getLeftHandSide().getNumber());
		}
		switch (binaryRestriction.getType()){
		case "lessThan":
			if (currentLeftValue>=currentRightValue){
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);
			}
			break;
		case "greaterThan":
			if (currentLeftValue<=currentRightValue){
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);

			}
			break;
		case "lessThanOrEqual":
			if (currentLeftValue>currentRightValue){
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);

			}
			break;
		case "greaterThanOrEqual":
			if (currentLeftValue<currentRightValue){
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);
			}
			break;
		case "differentThan":
			if (currentLeftValue==currentRightValue){
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);
			}
			break;
		case "equals":
			if (currentLeftValue!=currentRightValue){
				//System.out.println("Violated constraint "+constraint.getId());
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);
			}
			break;
		default:
			if (currentLeftValue>=currentRightValue){
				//System.out.println("Violated constraint "+constraint.getId());
				fulfilled=Math.abs((currentLeftValue-currentRightValue)/currentRightValue);
			}
			break;
		}
		return fulfilled;
	}
	public boolean evaluateBinaryRestriction(BinaryRestriction binaryRestriction,MonitoredEntity monitoredEntity){
		boolean fulfilled=true;
		Double currentLeftValue=0.0;
		Double currentRightValue = 0.0;
		if (binaryRestriction.getLeftHandSide().getMetric()!=null){
			String metric = binaryRestriction.getLeftHandSide().getMetric();
			//PlanningLogger.logger.info(monitoredEntity+" "+metric);
			currentLeftValue = monitoredEntity.getMonitoredValue(metric);
			if (currentLeftValue<0){
				if (monitoredEntity.getMonitoredVar(metric)!=null)
				currentLeftValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
				else currentRightValue=0.0;
			}
			currentRightValue=Double.parseDouble(binaryRestriction.getRightHandSide().getNumber());
		}else
		if (binaryRestriction.getRightHandSide().getMetric()!=null){
			String metric = binaryRestriction.getRightHandSide().getMetric();
			currentRightValue = monitoredEntity.getMonitoredValue(metric);
			//System.out.println("Current value for metric is  "+ currentRightValue);
			if (currentRightValue<0){
				if (monitoredEntity.getMonitoredVar(metric)!=null)
				currentRightValue = monitoredEntity.getMonitoredValue(monitoredEntity.getMonitoredVar(metric));
				else currentRightValue=0.0;
			}
			currentLeftValue=Double.parseDouble(binaryRestriction.getLeftHandSide().getNumber());
		}
		switch (binaryRestriction.getType()){
		case "lessThan":
			if (currentLeftValue>=currentRightValue){
				fulfilled=false;
			}
			break;
		case "greaterThan":
			if (currentLeftValue<=currentRightValue){
				fulfilled=false;

			}
			break;
		case "lessThanOrEqual":
			if (currentLeftValue>currentRightValue){
				fulfilled=false;

			}
			break;
		case "greaterThanOrEqual":
			if (currentLeftValue<currentRightValue){
				fulfilled=false;
			}
			break;
		case "differentThan":
			if (currentLeftValue==currentRightValue){
				fulfilled=false;
			}
			break;
		case "equals":
			if (currentLeftValue!=currentRightValue){
				//System.out.println("Violated constraint "+constraint.getId());
				fulfilled=false;
			}
			break;
		default:
			if (currentLeftValue>=currentRightValue){
				//System.out.println("Violated constraint "+constraint.getId());
				fulfilled=false;
			}
			break;
		}
		return fulfilled;
	}
	public int countFixedStrategies(ContextRepresentation previousContextRepresentation,String strategiesThatNeedToBeImproved){
		int nbFixedStrategies = 0;
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
		//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
				MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
				if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());
			for (Strategy strategy:syblSpecification.getStrategy()){
				Condition condition = strategy.getCondition();		
				
				if (strategiesThatNeedToBeImproved.contains(strategy.getId()) && evaluateCondition(condition, monitoredEntity)){
				if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")||strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")){
						//PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
						
						if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())>previousContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter())){
							nbFixedStrategies+=1;
						}
					}
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
					//	PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
						
						if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())<previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter())){
							nbFixedStrategies+=1;
						}
					}
				}
			}
			}
		}
		return nbFixedStrategies;
	}
	
	public int countFixedStrategies(ContextRepresentation previousContextRepresentation){
		int nbFixedStrategies = 0;
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
		//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
				MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
				if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());
			for (Strategy strategy:syblSpecification.getStrategy()){
				Condition condition = strategy.getCondition();		
				
				if (evaluateCondition(condition, monitoredEntity)){
				if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")||strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")){
						//PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
						
						if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())>previousContextRepresentation.getValueForMetric(monitoredEntity, strategy.getToEnforce().getParameter())){
							nbFixedStrategies+=1;
						}
					}
					if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")){
					//	PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));
						
						if (monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())<previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter())){
							nbFixedStrategies+=1;
						}
					}
				}
			}
			}
		}
		return nbFixedStrategies;
	}
	
	public boolean evaluateCondition(Condition c, MonitoredEntity monitoredEntity){
		if (c==null) return true;
		
		if (monitoredEntity==null) {
			PlanningLogger.logger.info("Monitored entity is null ");
			return true;
		}
			boolean oneEvaluatedToTrueFound=false;

			for (BinaryRestrictionsConjunction restrictions:c.getBinaryRestriction()){
				boolean value=true;
			for (BinaryRestriction binaryRestriction:restrictions.getBinaryRestrictions()){
				if (!evaluateBinaryRestriction(binaryRestriction, monitoredEntity)) value =false;
				}
			
			if (value==true) oneEvaluatedToTrueFound=true;
			}	
			
			for (UnaryRestrictionsConjunction restrictions:c.getUnaryRestrictions()){
				boolean value=true;
			for (UnaryRestriction unaryRestriction:restrictions.getUnaryRestrictions()){
				if (!evaluateUnaryRestriction(unaryRestriction, monitoredEntity)) value =false;
				}
			
			if (value==true) oneEvaluatedToTrueFound=true;
			}
			
		if (oneEvaluatedToTrueFound)
			return true;
		else return false;

	}
	
	public double quantifyCondition(Condition c, MonitoredEntity monitoredEntity){
		if (c==null) return 0;
		
		if (monitoredEntity==null) {
			PlanningLogger.logger.info("Monitored entity is null ");
			return 0;
		}
			double evaluatedConstraints= 0;
			double binaryRestrictionNb=0;
			for (BinaryRestrictionsConjunction restrictions:c.getBinaryRestriction()){
			
			for (BinaryRestriction binaryRestriction:restrictions.getBinaryRestrictions()){
				if (!evaluateBinaryRestriction(binaryRestriction, monitoredEntity)) {
					evaluatedConstraints +=quantifyBinaryRestriction(binaryRestriction, monitoredEntity);
					
				}
				binaryRestrictionNb++;
				}
			
			}	
			
			for (UnaryRestrictionsConjunction restrictions:c.getUnaryRestrictions()){
		
			for (UnaryRestriction unaryRestriction:restrictions.getUnaryRestrictions()){
				if (!evaluateUnaryRestriction(unaryRestriction, monitoredEntity)) {
					evaluatedConstraints+=quantifyUnaryRestriction(unaryRestriction, monitoredEntity);
				}
				binaryRestrictionNb++;
				}
			

			}
			
		return evaluatedConstraints;

	}
	public double quantifyUnaryRestriction(UnaryRestriction unaryRestriction, MonitoredEntity monitoredEntity){
		if (unaryRestriction.getReferenceTo().getFunction().equalsIgnoreCase("fulfilled")){
			if (getViolatedConstraints().contains(unaryRestriction.getReferenceTo().getName()))
				return 1;
			else return 0;
		}else{
			if (getViolatedConstraints().contains(unaryRestriction.getReferenceTo().getName()))
				return 0;
			else return 1;
		}
	}
	public boolean evaluateUnaryRestriction(UnaryRestriction unaryRestriction, MonitoredEntity monitoredEntity){
		if (unaryRestriction.getReferenceTo().getFunction().equalsIgnoreCase("fulfilled")){
			if (getViolatedConstraints().contains(unaryRestriction.getReferenceTo().getName()))
				return false;
			else return true;
		}else{
			if (getViolatedConstraints().contains(unaryRestriction.getReferenceTo().getName()))
				return true;
			else return false;
		}
	}
	public Double getValueForMetric(MonitoredEntity monitoredEntity,String metricName){
		return findMonitoredEntity(monitoredEntity.getId()).getMonitoredValue(metricName);
	}
	public int countViolatedConstraints(){
		int numberofViolatedConstraints=0;
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
			//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
			MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
			if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());

			for (Constraint constraint:syblSpecification.getConstraint()){
				if (evaluateCondition(constraint.getCondition(), monitoredEntity) && !evaluateCondition(constraint.getToEnforce(), monitoredEntity))
						numberofViolatedConstraints=numberofViolatedConstraints+1;
				
			}
		}
		//PlanningLogger.logger.info("Number of violated constraints"+ numberofViolatedConstraints);
		return numberofViolatedConstraints;
	}
	
	public int evaluateViolationPercentage(){
		int numberofViolatedConstraints=0;
		for (ElasticityRequirement elReq:dependencyGraph.getAllElasticityRequirements()){
			SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
			//System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
			MonitoredEntity monitoredEntity = findMonitoredEntity(syblSpecification.getComponentId());
			if (monitoredEntity==null) PlanningLogger.logger.info("Not finding monitored entity "+monitoredEntity+ " "+syblSpecification.getComponentId());

			for (Constraint constraint:syblSpecification.getConstraint()){
				if (evaluateCondition(constraint.getCondition(), monitoredEntity) && !evaluateCondition(constraint.getToEnforce(), monitoredEntity))
						numberofViolatedConstraints=numberofViolatedConstraints+1;
				
			}
		}
		//PlanningLogger.logger.info("Number of violated constraints"+ numberofViolatedConstraints);
		return numberofViolatedConstraints;
	}
	
	public MonitoredCloudService getMonitoredCloudService() {
		return monitoredCloudService;
	}
	public void setMonitoredCloudService(MonitoredCloudService monitoredCloudService) {
		this.monitoredCloudService = monitoredCloudService;
	}
	public double getPREVIOUS_CS_UNHEALTHY_STATE() {
		return PREVIOUS_CS_UNHEALTHY_STATE;
	}
	public void setPREVIOUS_CS_UNHEALTHY_STATE(double pREVIOUS_CS_UNHEALTHY_STATE) {
		PREVIOUS_CS_UNHEALTHY_STATE = pREVIOUS_CS_UNHEALTHY_STATE;
	}
	public double getCS_UNHEALTHY_STATE() {
		return CS_UNHEALTHY_STATE;
	}
	public void setCS_UNHEALTHY_STATE(double cS_UNHEALTHY_STATE) {
		CS_UNHEALTHY_STATE = cS_UNHEALTHY_STATE;
	}
	public List<ActionEffect> getActionsAssociatedToContext() {
		return actionsAssociatedToContext;
	}
	public void addActionToContext(ActionEffect actionsAssociatedToContext) {
		
		this.actionsAssociatedToContext.add(actionsAssociatedToContext);
	}
}
