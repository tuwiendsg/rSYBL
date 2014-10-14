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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.ContextRepresentation;
import at.ac.tuwien.dsg.rSybl.planningEngine.MonitoredEntity;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;





public class ActionEffects {
	public static ActionEffect scaleOutEffectForCassandraDB = new ActionEffect();
	public static ActionEffect scaleOutEffectForWebServer=new ActionEffect();

	public static ActionEffect scaleInEffectForCassandraDB = new ActionEffect();
	public static ActionEffect scaleInEffectForWebServer= new ActionEffect();

	public static HashMap<String,List<ActionEffect>> applicationSpecificActionEffects = new HashMap<String,List<ActionEffect>>();
        public static HashMap<String,ActionEffect> defaultActionEffects = new HashMap<String,ActionEffect>();
	public static HashMap<String,List<ActionEffect>> getActionEffects(DependencyGraph dependencyGraph,MonitoringAPIInterface syblAPI,ContextRepresentation currentContextRepr){
		
	        applicationSpecificActionEffects = new HashMap<String,List<ActionEffect>>();
//		MonitoredEntity cassandraNode = currentContextRepr.findMonitoredEntity("CassandraNode");
//		MonitoredEntity ycsbClient = currentContextRepr.findMonitoredEntity("YCSBClient");
		

		{
//			scaleOutEffectForCassandraDB.setTargetedEntityID("CassandraNode");
//			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -30.0,"CassandraNode");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -40.0,"DataControllerServiceUnit");
//			if (ycsbClient!=null){
//                            Double val = 0.0;
//                            if (ycsbClient.getMonitoredValue("throughput")==null){
//                               val= syblAPI.getMetricValue("throughput", dependencyGraph.getNodeWithID("YCSBClient"));
//                            }else{
//                                val=ycsbClient.getMonitoredValue("throughput");
//                            }
//			if (val>1500){
//				scaleOutEffectForCassandraDB.setActionEffectForMetric("latency", -1000.0,"YCSBClient");
//				}
//			else{
//				scaleOutEffectForCassandraDB.setActionEffectForMetric("latency", -2.0,"YCSBClient");
//				}
//			}else{
//				PlanningLogger.logger.info("YCSB Client is null as shown by the context representation ");
//			}
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cost", 0.12,"CassandraNode");
			scaleOutEffectForCassandraDB.setActionName("scaleOutEffectForDataNode");
			scaleOutEffectForCassandraDB.setActionType("scaleout");

		}
		{
			scaleOutEffectForCassandraDB.setTargetedEntityID("DataNodeServiceUnit");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -30.0,"DataNodeServiceUnit");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -40.0,"DataControllerServiceUnit");
			
			scaleOutEffectForCassandraDB.setActionEffectForMetric("latency", -2.0,"DataNodeServiceUnit");
		
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cost", 0.12,"DataNodeServiceUnit");
			scaleOutEffectForCassandraDB.setActionName("scaleOutEffectForDataNode");
			scaleOutEffectForCassandraDB.setActionType("scaleout");

		}

		{
			scaleOutEffectForWebServer.setTargetedEntityID("EventProcessingServiceUnit") ; 
			scaleOutEffectForWebServer.setActionEffectForMetric("cpuUsage", -40.0,"EventProcessingServiceUnit");
			scaleOutEffectForWebServer.setActionEffectForMetric("cost",0.12,"EventProcessingServiceUnit");
			scaleOutEffectForWebServer.setActionEffectForMetric("responseTime", -1000.0,"EventProcessingServiceUnit");
			scaleOutEffectForWebServer.setActionEffectForMetric("throughput", 1000.0,"EventProcessingServiceUnit");
			scaleOutEffectForWebServer.setActionName("scaleOutEffectForEventProcessingServiceUnit");
			scaleOutEffectForWebServer.setActionType("scaleout");

		}
	
		
		
		{
		scaleInEffectForCassandraDB.setTargetedEntityID("CassandraNode");
		scaleInEffectForCassandraDB.setActionEffectForMetric("cpuUsage", 35.0,"CassandraNode");
                scaleInEffectForCassandraDB.setActionEffectForMetric("latency", 2.0,"YCSBClient");
		scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -0.12,"CassandraNode");
		scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -1.0,"CloudService");
		scaleInEffectForCassandraDB.setActionName("scaleInEffectForDataNode");
		scaleInEffectForCassandraDB.setActionType("scalein");

	}
		
		
		{
			scaleInEffectForCassandraDB.setTargetedEntityID("DataNodeServiceUnit");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cpuUsage", 35.0,"DataNodeServiceUnit");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cpuUsage", 20.0,"DataControllerServiceUnit");

                   scaleInEffectForCassandraDB.setActionEffectForMetric("latency", 1.0,"DataNodeServiceUnit");
                   scaleInEffectForCassandraDB.setActionEffectForMetric("latency", 1.0,"DataControllerServiceUnit");

			scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -0.12,"DataNodeServiceUnit");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -0.12,"CloudService");
			
			scaleInEffectForCassandraDB.setActionName("scaleInEffectForDataNode");
			scaleInEffectForCassandraDB.setActionType("scalein");

		}


		{
			scaleInEffectForWebServer.setTargetedEntityID("EventProcessingServiceUnit") ; 
			scaleInEffectForWebServer.setActionEffectForMetric("cpuUsage", 40.0,"EventProcessingServiceUnit");
			scaleInEffectForWebServer.setActionEffectForMetric("cost", 0.12,"EventProcessingServiceUnit");
			scaleInEffectForWebServer.setActionEffectForMetric("responseTime", 400.0,"EventProcessingServiceUnit");
			scaleInEffectForWebServer.setActionEffectForMetric("throughput", -1000.0,"EventProcessingServiceUnit");
			scaleInEffectForWebServer.setActionName("scaleInEffectForEventProcessingServiceUnit");
			scaleInEffectForWebServer.setActionType("scalein");

		}
		
		 if (applicationSpecificActionEffects.containsKey(scaleOutEffectForCassandraDB.getTargetedEntityID()))
			 applicationSpecificActionEffects.get(scaleOutEffectForCassandraDB.getTargetedEntityID()).add(scaleOutEffectForCassandraDB);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleOutEffectForCassandraDB);
			 applicationSpecificActionEffects.put(scaleOutEffectForCassandraDB.getTargetedEntityID(), l);
		 }
		 
		 if (applicationSpecificActionEffects.containsKey(scaleOutEffectForWebServer.getTargetedEntityID()))
			 applicationSpecificActionEffects.get(scaleOutEffectForWebServer.getTargetedEntityID()).add(scaleOutEffectForWebServer);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleOutEffectForWebServer);
			 applicationSpecificActionEffects.put(scaleOutEffectForWebServer.getTargetedEntityID(), l);
		 }
		 
		 
		 
		 if (applicationSpecificActionEffects.containsKey(scaleInEffectForCassandraDB.getTargetedEntityID()))
			 applicationSpecificActionEffects.get(scaleInEffectForCassandraDB.getTargetedEntityID()).add(scaleInEffectForCassandraDB);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleInEffectForCassandraDB);
			 applicationSpecificActionEffects.put(scaleInEffectForCassandraDB.getTargetedEntityID(), l);
		 }
		 
		 if (applicationSpecificActionEffects.containsKey(scaleInEffectForWebServer.getTargetedEntityID()))
			 applicationSpecificActionEffects.get(scaleInEffectForWebServer.getTargetedEntityID()).add(scaleInEffectForWebServer);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleInEffectForWebServer);
			 applicationSpecificActionEffects.put(scaleInEffectForWebServer.getTargetedEntityID(), l);
		 }
	
		 

		for (Entry<String, List<ActionEffect>> e:applicationSpecificActionEffects.entrySet()){
			for (ActionEffect ef:e.getValue()){
				
				ef.refreshMetricsForAboveLevels(dependencyGraph,syblAPI);
				
			}
		}
		return applicationSpecificActionEffects;
	}
	public static void setActionEffects(String eff){
		PlanningLogger.logger.info("~~~~~~~~~~Action effects set through web serv, setting the effects ! ");

		JSONParser parser = new JSONParser();
		applicationSpecificActionEffects = new HashMap<String,List<ActionEffect>>();
		Object obj;
		try {
			obj = parser.parse(eff);
		
		 
		JSONObject jsonObject = (JSONObject) obj;
		
		for (Object actionName:jsonObject.keySet()){
			
			String myaction = (String )actionName;
			
			
			JSONObject object=(JSONObject) jsonObject.get(myaction);

		for (Object actions: object.keySet()){
			ActionEffect actionEffect = new ActionEffect();
			actionEffect.setActionType((String)myaction);
			actionEffect.setActionName((String) actions);
			JSONObject scaleinDescription=(JSONObject) object.get(actions);
                        if (scaleinDescription.containsKey("conditions")){
                                          JSONArray conditions=(JSONArray) jsonObject.get("conditions");
                                          for (int i=0;i<conditions.size();i++){
                                              actionEffect.addCondition((String)conditions.get(i));
                                          }
                                        }
			String targetUnit = (String) scaleinDescription.get("targetUnit");
			actionEffect.setTargetedEntityID(targetUnit);
			
			JSONObject effects = (JSONObject) scaleinDescription.get("effects");
			

			for (Object effectPerUnit:effects.keySet()){
				//System.out.println(effects.toString());
				String affectedUnit = (String) effectPerUnit;
				JSONObject metriceffects=(JSONObject) effects.get(affectedUnit);
				for (Object metric:metriceffects.keySet()){
					String metricName=(String)metric;
					try{
						actionEffect.setActionEffectForMetric(metricName, (Double)metriceffects.get(metricName), affectedUnit);
					}catch(Exception e){
						actionEffect.setActionEffectForMetric(metricName, ((Long)metriceffects.get(metricName)).doubleValue(), affectedUnit);
						}
				}
				 
				}
			
			if (! applicationSpecificActionEffects.containsKey(actionEffect.getTargetedEntityID().trim()) ){
				List <ActionEffect > l = new ArrayList<ActionEffect>();
				l.add(actionEffect);
				applicationSpecificActionEffects.put(actionEffect.getTargetedEntityID().trim(), l);
				//PlanningLogger.logger.info("New Action effects "+actionEffect.getActionType()+" "+actionEffect.getActionName()+" "+actionEffect.getTargetedEntityID());
				
			}else{
				applicationSpecificActionEffects.get(actionEffect.getTargetedEntityID().trim()).add(actionEffect);
				//PlanningLogger.logger.info("Adding Action effects "+actionEffect.getActionType()+" "+actionEffect.getActionName()+" "+actionEffect.getTargetedEntityID());

			}
		}
		}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
        public static HashMap<String,List<ActionEffect>> getActionConditionalEffects(){
            if (applicationSpecificActionEffects.isEmpty() && defaultActionEffects.isEmpty()){
				PlanningLogger.logger.info("~~~~~~~~~~Action effects is empty, reading the effects ! ");
			JSONParser parser = new JSONParser();
		 
			try {
				InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(Configuration.getEffectsPath());
				Object obj = parser.parse(new InputStreamReader(inputStream));
		 
				JSONObject jsonObject = (JSONObject) obj;
				
				for (Object actionName:jsonObject.keySet()){
					
					String myaction = (String )actionName;
					
					
					JSONObject object=(JSONObject) jsonObject.get(myaction);
					
				for (Object actions: object.keySet()){
                                        
                                        
					ActionEffect actionEffect = new ActionEffect();
					actionEffect.setActionType((String)myaction);
					actionEffect.setActionName((String) actions);
					JSONObject scaleinDescription=(JSONObject) object.get(actions);
					if (scaleinDescription.containsKey("conditions")){
                                          JSONArray conditions=(JSONArray) jsonObject.get("conditions");
                                          for (int i=0;i<conditions.size();i++){
                                              actionEffect.addCondition((String)conditions.get(i));
                                          }
                                        }
                                        String targetUnit = (String) scaleinDescription.get("targetUnit");
					actionEffect.setTargetedEntityID(targetUnit);
					
					JSONObject effects = (JSONObject) scaleinDescription.get("effects");
					

					for (Object effectPerUnit:effects.keySet()){
						//System.out.println(effects.toString());
						String affectedUnit = (String) effectPerUnit;
						JSONObject metriceffects=(JSONObject) effects.get(affectedUnit);
						for (Object metric:metriceffects.keySet()){
							String metricName=(String)metric;
							try{
								actionEffect.setActionEffectForMetric(metricName, (Double)metriceffects.get(metricName), affectedUnit);
							}catch(Exception e){
								actionEffect.setActionEffectForMetric(metricName, ((Long)metriceffects.get(metricName)).doubleValue(), affectedUnit);
								}
						}
						 
						}
					
					if (applicationSpecificActionEffects.get(actionEffect.getTargetedEntityID())==null ){
						List <ActionEffect > l = new ArrayList<ActionEffect>();
						l.add(actionEffect);
						applicationSpecificActionEffects.put(actionEffect.getTargetedEntityID(), l);
					
					}else{
						applicationSpecificActionEffects.get(actionEffect.getTargetedEntityID()).add(actionEffect);
					}
				}
				 
				
				}
		 
				
			} catch (Exception e) {
                            
				PlanningLogger.logger.info("~~~~~~~~~~Retrying reading the effects  ");
			 parser = new JSONParser();
		 
			try {
				InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(Configuration.getEffectsPath());
				Object obj = parser.parse(new InputStreamReader(inputStream));
		 
				JSONObject jsonObject = (JSONObject) obj;
				
				for (Object actionName:jsonObject.keySet()){
					
					String myaction = (String )actionName;
					ActionEffect actionEffect = new ActionEffect();
					actionEffect.setActionType((String)myaction);
					actionEffect.setActionName((String) myaction);
					
					JSONObject object=(JSONObject) jsonObject.get(myaction);
                                        JSONObject metrics = (JSONObject) object.get("effects");
					for (Object me:metrics.keySet()){
                                            String metric = (String) me;
                                            Double metricEffect = (Double) metrics.get(metric);
                                            actionEffect.setActionEffectForMetric(metric, metricEffect, "");
                                            
                                        }
				defaultActionEffects.put(myaction, actionEffect);
				 
				
				}
                                
			}catch(Exception ex){
                            PlanningLogger.logger.error("Error when reading the effects!!!!!!!!!!!!!!!!!!"+ex.getMessage());
                        }
			
			}
            }
			return applicationSpecificActionEffects;	 
		     
        }
        public static HashMap<String,ActionEffect> getActionDefaultEffects(){
            return defaultActionEffects;
        }
	 public static HashMap<String,List<ActionEffect>> getActionEffects () {
			if (applicationSpecificActionEffects.isEmpty()){
				PlanningLogger.logger.info("~~~~~~~~~~Action effects is empty, reading the effects ! ");
			JSONParser parser = new JSONParser();
		 
			try {
				InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(Configuration.getEffectsPath());
				Object obj = parser.parse(new InputStreamReader(inputStream));
		 
				JSONObject jsonObject = (JSONObject) obj;
				
				for (Object actionName:jsonObject.keySet()){
					
					String myaction = (String )actionName;
					
					
					JSONObject object=(JSONObject) jsonObject.get(myaction);
					
				for (Object actions: object.keySet()){
					ActionEffect actionEffect = new ActionEffect();
					actionEffect.setActionType((String)myaction);
					actionEffect.setActionName((String) actions);
					JSONObject scaleinDescription=(JSONObject) object.get(actions);
					String targetUnit = (String) scaleinDescription.get("targetUnit");
					actionEffect.setTargetedEntityID(targetUnit);
					
					JSONObject effects = (JSONObject) scaleinDescription.get("effects");
					

					for (Object effectPerUnit:effects.keySet()){
						//System.out.println(effects.toString());
						String affectedUnit = (String) effectPerUnit;
						JSONObject metriceffects=(JSONObject) effects.get(affectedUnit);
						for (Object metric:metriceffects.keySet()){
							String metricName=(String)metric;
							try{
								actionEffect.setActionEffectForMetric(metricName, (Double)metriceffects.get(metricName), affectedUnit);
							}catch(Exception e){
								actionEffect.setActionEffectForMetric(metricName, ((Long)metriceffects.get(metricName)).doubleValue(), affectedUnit);
								}
						}
						 
						}
					
					if (applicationSpecificActionEffects.get(actionEffect.getTargetedEntityID())==null ){
						List <ActionEffect > l = new ArrayList<ActionEffect>();
						l.add(actionEffect);
						applicationSpecificActionEffects.put(actionEffect.getTargetedEntityID(), l);
					
					}else{
						applicationSpecificActionEffects.get(actionEffect.getTargetedEntityID()).add(actionEffect);
					}
				}
				 
				
				}
		 
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			}
			return applicationSpecificActionEffects;	 
		     }
	 
}
