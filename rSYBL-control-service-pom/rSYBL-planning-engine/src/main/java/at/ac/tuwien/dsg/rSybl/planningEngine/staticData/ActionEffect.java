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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;



public class ActionEffect {
	private String actionName;
	private Node targetedEntity;
	private String targetedEntityID;
	private String actionType;
	
	public HashMap<String,HashMap<String,Float>> effects = new HashMap<String,HashMap<String,Float>>();

	public void setActionEffectForMetric(String metricName, Float result,String entityID){
		//System.out.println("Setting action effect for metric "+metricName+" entity "+entityID);
		if (effects.get(entityID)!=null)
		effects.get(entityID).put(metricName, result);
		else{
			HashMap<String, Float> metrics = new HashMap<String,Float>();
			metrics.put(metricName, result);
			effects.put(entityID, metrics);
		}
	}
	public Float getActionEffectForMetric(String metricName,String entityID){	
		if (effects.get(entityID)==null)return 0.0f;
		return effects.get(entityID).get(metricName);
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
			HashMap<String,Float> resultedEffectAtServiceUnitLevel = new HashMap<String,Float>();
			HashMap<String, Float> values = new HashMap<String,Float>();
			for (Node serviceUnit: currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_UNIT)){
				HashMap<String,Float> effect = effects.get(serviceUnit.getId());
				if (effect!=null)
				for (Map.Entry<String, Float> entry:effect.entrySet()){
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
						     values.put(entry.getKey(), 1.0f);

						 } 
					 }
				}

					}
			if ((currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY))!=null)
			for (Node serviceTopology:currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)){
				HashMap<String,Float> effect = effects.get(serviceTopology.getId());
				if (effect!=null)
				for (Map.Entry<String, Float> entry:effect.entrySet()){
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
						     values.put(entry.getKey(), 1.0f);

						 } 
					 }
				}

					}
			for (Map.Entry<String, Float> entry:resultedEffectAtServiceUnitLevel.entrySet())
				if (entry.getKey().toLowerCase().contains("cost")||entry.getKey().toLowerCase().contains("size") || entry.getKey().toLowerCase().contains("throughput"))
					{
						setActionEffectForMetric(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey()), currentEntity.getId());
					}
					else{
						setActionEffectForMetric(entry.getKey(), resultedEffectAtServiceUnitLevel.get(entry.getKey())/values.get(entry.getKey()), currentEntity.getId());

					}
				}
		if (currentEntity.getNodeType()==NodeType.CLOUD_SERVICE){
			Node serviceTopology = currentEntity.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY).get(0);
			HashMap<String,Float> effect = effects.get(serviceTopology.getId());
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
//		PlanningLogger.logger.error("Entity id is "+currentEntityID);
		Node currentEntity = dependencyGraph.getNodeWithID(currentEntityID);
		if (currentEntity==null)
		{
			PlanningLogger.logger.error("The id "+currentEntityID+" is null");
		}
			if (effects.get(currentEntityID)==null)
		{
				HashMap<String, Float> metrics = new HashMap<String,Float>();

				effects.put(currentEntityID, metrics);
			}
		
		aggregateMetrics(currentEntity,monitoringAPI);
		
		Node parent = dependencyGraph.findParentNode(currentEntityID);
		while (parent!=null){
			//Compute metrics
			if (!effects.containsKey(parent.getId())){
				HashMap<String, Float> metrics = new HashMap<String,Float>();

				effects.put(parent.getId(), metrics);
				}
			aggregateMetrics(parent,monitoringAPI);

			//Set computed metrics to effects?	
			
			parent = dependencyGraph.findParentNode(parent.getId());
			//System.out.println("Parent "+parent.getId());
		}
		
		}
	}
	
}
