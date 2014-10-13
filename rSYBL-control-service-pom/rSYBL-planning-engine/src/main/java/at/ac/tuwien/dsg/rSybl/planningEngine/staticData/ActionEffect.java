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

package at.ac.tuwien.dsg.rSybl.planningEngine.staticData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ActionEffect {
	private String actionName;
	private Node targetedEntity;
	private String targetedEntityID;
	private String actionType;	
	private HashMap<String,HashMap<String,Double>> effects = new HashMap<String,HashMap<String,Double>>();
    private List<String> conditions = new ArrayList<String>();

	public void setActionEffectForMetric(String metricName, Double result,String entityID){
		//PlanningLogger.logger.info("bbb"+entityID+"bbb");
		if (effects.get(entityID)==null)
			effects.put(entityID, new HashMap<String,Double>());
		
		effects.get(entityID).put(metricName, result);
		//PlanningLogger.logger.info("~~~~~~~~~~ Just set "+actionType+" with action name "+actionName+", for entity "+entityID+" the effect "+effects.get(entityID).get(metricName)+" for metric "+metricName);

	}
	public Set<String> getAffectedNodes(){
		return effects.keySet();
	} 
	public Double getActionEffectForMetric(String metricName,String entityID){
            
		//PlanningLogger.logger.info("aaa"+entityID+"aaa");
		if (effects.get(entityID)==null){
			//PlanningLogger.logger.info("Not found entity "+entityID+" for action "+actionName);
			return 0.0;
		}
		return effects.get(entityID).get(metricName);
            
	}
        public boolean isConditional(){
            if (conditions.size()>0) return true;
                else return false;
        }
	public Node getTargetedEntity() {
		return targetedEntity;
	}
	public void setTargetedEntity(Node targetedEntity) {
		
		this.targetedEntity = targetedEntity;
	}
	public String getTargetedEntityID() {
		return targetedEntityID;
	}
	public void setTargetedEntityID(String targetedEntityID) {
		this.targetedEntityID = targetedEntityID;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	
	
	public void aggregateMetrics(Node currentEntity,MonitoringAPIInterface monitoringAPI){
		if (currentEntity.getNodeType()==NodeType.SERVICE_TOPOLOGY){
			HashMap<String,Double> resultedEffectAtServiceUnitLevel = new HashMap<String,Double>();
			HashMap<String, Double> values = new HashMap<String,Double>();
			for (Node serviceUnit: currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_UNIT)){
				HashMap<String,Double> effect = effects.get(serviceUnit.getId());
				if (effect!=null)
				for (Map.Entry<String, Double> entry:effect.entrySet()){
					if (entry.getKey().toLowerCase().contains("cost")||entry.getKey().toLowerCase().contains("size") || entry.getKey().toLowerCase().contains("throughput")){
					 if (resultedEffectAtServiceUnitLevel.containsKey(entry.getKey())){
						 resultedEffectAtServiceUnitLevel.put(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey())+effect.get(entry.getKey()));
					 }else{
						 resultedEffectAtServiceUnitLevel.put(entry.getKey(),effect.get(entry.getKey()));
					 }}else{
						 if (resultedEffectAtServiceUnitLevel.containsKey(entry.getKey())){
							 resultedEffectAtServiceUnitLevel.put(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey())+effect.get(entry.getKey()));
						     values.put(entry.getKey(), values.get(entry.getKey())+1);
						 }else{
							 resultedEffectAtServiceUnitLevel.put(entry.getKey(),effect.get(entry.getKey()));
						     values.put(entry.getKey(), 1.0);

						 } 
					 }
				}

					}
			if ((currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY))!=null)
			for (Node serviceTopology:currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)){
				HashMap<String,Double> effect = effects.get(serviceTopology.getId());
				if (effect!=null)
				for (Map.Entry<String, Double> entry:effect.entrySet()){
					if (entry.getKey().toLowerCase().contains("cost")||entry.getKey().toLowerCase().contains("size") || entry.getKey().toLowerCase().contains("throughput")){
					 if (resultedEffectAtServiceUnitLevel.containsKey(entry.getKey())){
						 resultedEffectAtServiceUnitLevel.put(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey())+effect.get(entry.getKey()));
					 }else{
						 resultedEffectAtServiceUnitLevel.put(entry.getKey(),effect.get(entry.getKey()));
					 }}else{
						 if (resultedEffectAtServiceUnitLevel.containsKey(entry.getKey())){
							 resultedEffectAtServiceUnitLevel.put(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey())+effect.get(entry.getKey()));
						     values.put(entry.getKey(), values.get(entry.getKey())+1);
						 }else{
							 resultedEffectAtServiceUnitLevel.put(entry.getKey(),effect.get(entry.getKey()));
						     values.put(entry.getKey(), 1.0);

						 } 
					 }
				}

					}
			for (Map.Entry<String, Double> entry:resultedEffectAtServiceUnitLevel.entrySet())
				if (getActionEffectForMetric(entry.getKey(),currentEntity.getId())==0)
				if (entry.getKey().toLowerCase().contains("cost")||entry.getKey().toLowerCase().contains("size") || entry.getKey().toLowerCase().contains("throughput"))
					{
						//PlanningLogger.logger.info("The unit is "+entry.getKey()+" resultedEffect");
						setActionEffectForMetric(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey()), currentEntity.getId());
					}
					else{
						//PlanningLogger.logger.info("The unit is "+entry.getKey()+" resultedEffect");
						setActionEffectForMetric(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey())/values.get(entry.getKey()), currentEntity.getId());

					}
				}
		if (currentEntity.getNodeType()==NodeType.CLOUD_SERVICE){
			Node serviceTopology = currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY).get(0);
			HashMap<String,Double> effect = effects.get(serviceTopology.getId());
			effects.put(currentEntity.getId(), effect);

		}

				
		
	}
	
	
	public void refreshMetricsForAboveLevels(DependencyGraph dependencyGraph,MonitoringAPIInterface monitoringAPI){
		//Get each base entity, recursively find parents and set metrics on current level based on sublevels
		Set<String> coll= new HashSet<String> ();
//		PlanningLogger.logger.error("Entity id is "+targetedEntityID);
		targetedEntity=dependencyGraph.getNodeWithID(targetedEntityID);
		coll.addAll(effects.keySet());
		Iterator<String> iterator= coll.iterator();
		
		while (iterator.hasNext()){
			String currentEntityID = iterator.next();
		PlanningLogger.logger.info("Entity id is "+currentEntityID);
		Node currentEntity = dependencyGraph.getNodeWithID(currentEntityID);
		if (currentEntity==null)
		{
			PlanningLogger.logger.error("The id "+currentEntityID+" is null");
		}
			if (effects.get(currentEntityID)==null)
		{
				HashMap<String, Double> metrics = new HashMap<String,Double>();
				
				effects.put(currentEntityID, metrics);
			}
		
		aggregateMetrics(currentEntity,monitoringAPI);
		
		Node parent = dependencyGraph.findParentNode(currentEntityID);
		while (parent!=null){
			//Compute metrics
			if (!effects.containsKey(parent.getId())){
				HashMap<String, Double> metrics = new HashMap<String,Double>();

				effects.put(parent.getId(), metrics);
				}
			aggregateMetrics(parent,monitoringAPI);

			//Set computed metrics to effects?	
			
			parent = dependencyGraph.findParentNode(parent.getId());
			PlanningLogger.logger.info("Parent "+parent.getId());
		}
		
		}
	}
        
    public void addCondition(String condition) {
        conditions.add(condition);
    }

    public boolean evaluateConditions(DependencyGraph dependencyGraph, MonitoringAPIInterface monitoringInterface) {
        boolean ok = true;
        for (String condition : conditions) {

            if (condition.trim().contains(">")) {
                String metric = condition.trim().split(">")[0];
                String value = condition.trim().split(">")[1];
                double val = Double.parseDouble(value);
                String target = metric.split(".")[0];
                String metricName = metric.split(".")[1];
                try {
                    if (monitoringInterface.getMetricValue(metricName, dependencyGraph.getNodeWithID(target))<val){
                        ok = false;
                        
                    }
                } catch (Exception ex) {
                    PlanningLogger.logger.error(ex.getMessage());
                }
            } else {
                if (condition.trim().contains("<")) {
                    String metric = condition.trim().split("<")[0];
                    String value = condition.trim().split("<")[1];
                    double val = Double.parseDouble(value);
                    String target = metric.split(".")[0];
                    String metricName = metric.split(".")[1];
                    try {
                    if (monitoringInterface.getMetricValue(metricName, dependencyGraph.getNodeWithID(target))>val){
                        ok = false;
                        
                    }
                } catch (Exception ex) {
                    PlanningLogger.logger.error(ex.getMessage());
                }
                }
            }
        }

        return ok;
    }
    
	public ActionEffect clone(){
            ActionEffect clone = new ActionEffect();
            clone.actionName=this.actionName;
            clone.actionType=this.actionType;
            clone.targetedEntityID=this.targetedEntityID;
            clone.targetedEntity=this.targetedEntity;
            for (String cond:this.conditions){
                clone.addCondition(cond);
            }
            HashMap<String,HashMap<String,Double>> eff = new HashMap<String,HashMap<String,Double>>();
            for (String target:effects.keySet()){
                eff.put(target, effects.get(target));
            }
            clone.effects=eff;
            return clone;
        }
}
