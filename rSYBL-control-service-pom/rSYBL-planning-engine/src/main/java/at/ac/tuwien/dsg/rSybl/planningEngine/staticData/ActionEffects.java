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
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;





public class ActionEffects {
	public static ActionEffect scaleOutEffectForCassandraDB = new ActionEffect();
	public static ActionEffect scaleOutEffectForWebServer=new ActionEffect();

	public static ActionEffect scaleInEffectForCassandraDB = new ActionEffect();
	public static ActionEffect scaleInEffectForWebServer= new ActionEffect();


	public static HashMap<String,List<ActionEffect>> getActionEffects(DependencyGraph dependencyGraph,MonitoringAPIInterface syblAPI,ContextRepresentation currentContextRepr){
		
		HashMap<String,List<ActionEffect>> actionEffects = new HashMap<String,List<ActionEffect>>();
		//MonitoredEntity cassandraNode = currentContextRepr.findMonitoredEntity("CassandraNode");
//		MonitoredEntity ycsbClient = currentContextRepr.findMonitoredEntity("YCSBClient");
		

//		{
//			scaleOutEffectForCassandraDB.setTargetedEntityID("CassandraNode");
//			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -30.0f,"CassandraNode");
////			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -40.0f,"DataControllerServiceUnit");
//			if (ycsbClient!=null){
//                            float val = 0;
//                            if (ycsbClient.getMonitoredValue("throughput")==null){
//                               val= syblAPI.getMetricValue("throughput", dependencyGraph.getNodeWithID("YCSBClient"));
//                            }else{
//                                val=ycsbClient.getMonitoredValue("throughput");
//                            }
//			if (val>1500){
//				scaleOutEffectForCassandraDB.setActionEffectForMetric("latency", -1000.0f,"YCSBClient");
//				}
//			else{
//				scaleOutEffectForCassandraDB.setActionEffectForMetric("latency", -2.0f,"YCSBClient");
//				}
//			}else{
//				PlanningLogger.logger.info("YCSB Client is null as shown by the context representation ");
//			}
//			scaleOutEffectForCassandraDB.setActionEffectForMetric("cost", 0.12f,"CassandraNode");
//			scaleOutEffectForCassandraDB.setActionName("scaleOutEffectForDataNode");
//			scaleOutEffectForCassandraDB.setActionType("scaleout");
//
//		}
		{
			scaleOutEffectForCassandraDB.setTargetedEntityID("DataNodeServiceUnit");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -30.0,"DataNodeServiceUnit");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpuUsage", -40.0,"DataControllerServiceUnit");
//			if (ycsbClient!=null){
//                            float val = 0;
//                            if (ycsbClient.getMonitoredValue("throughput")==null){
//                               val= syblAPI.getMetricValue("throughput", dependencyGraph.getNodeWithID("YCSBClient"));
//                            }else{
//                                val=ycsbClient.getMonitoredValue("throughput");
//                            }
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
//		{
//			scaleOutEffectForHadoopSlave.setTargetedEntityID("HadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionEffectForMetric("cpu.usage", -30.0f,"HadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionEffectForMetric("cost", 50.0f,"HadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionName("scaleOutEffectForHadoopSlave");
//			scaleOutEffectForHadoopSlave.setActionType("scaleout");
//
//		}
		
		

		{
			scaleInEffectForCassandraDB.setTargetedEntityID("DataNodeServiceUnit");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cpuUsage", 35.0,"DataNodeServiceUnit");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cpuUsage", 20.0,"DataControllerServiceUnit");

                   scaleInEffectForCassandraDB.setActionEffectForMetric("latency", 1.0,"DataNodeServiceUnit");
                   scaleInEffectForCassandraDB.setActionEffectForMetric("latency", 1.0,"DataControllerServiceUnit");

			scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -0.12,"DataNodeServiceUnit");
		//	scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -0.12f,"CloudService");
			
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
//		{
//			scaleInEffectForHadoopSlave.setTargetedEntityID("HadoopSlave");
//			scaleInEffectForHadoopSlave.setActionEffectForMetric("cpu.usage", 30.0f,"HadoopSlave");
//			scaleInEffectForHadoopSlave.setActionEffectForMetric("cost", -50.0f,"HadoopSlave");
//			scaleInEffectForHadoopSlave.setActionName("scaleInEffectForHadoopSlave");
//			scaleInEffectForHadoopSlave.setActionType("scalein");
//		}
		 if (actionEffects.containsKey(scaleOutEffectForCassandraDB.getTargetedEntityID()))
			 actionEffects.get(scaleOutEffectForCassandraDB.getTargetedEntityID()).add(scaleOutEffectForCassandraDB);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleOutEffectForCassandraDB);
			 actionEffects.put(scaleOutEffectForCassandraDB.getTargetedEntityID(), l);
		 }
		 
		 if (actionEffects.containsKey(scaleOutEffectForWebServer.getTargetedEntityID()))
			 actionEffects.get(scaleOutEffectForWebServer.getTargetedEntityID()).add(scaleOutEffectForWebServer);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleOutEffectForWebServer);
			 actionEffects.put(scaleOutEffectForWebServer.getTargetedEntityID(), l);
		 }
		 
//		 if (actionEffects.containsKey(scaleOutEffectForHadoopSlave.getTargetedEntityID()))
//			 actionEffects.get(scaleOutEffectForHadoopSlave.getTargetedEntityID()).add(scaleOutEffectForHadoopSlave);
//		 else{
//			 List <ActionEffect > l = new ArrayList<ActionEffect>();
//			 l.add(scaleOutEffectForHadoopSlave);
//			 actionEffects.put(scaleOutEffectForHadoopSlave.getTargetedEntityID(), l);
//		 }
		 
		 if (actionEffects.containsKey(scaleInEffectForCassandraDB.getTargetedEntityID()))
			 actionEffects.get(scaleInEffectForCassandraDB.getTargetedEntityID()).add(scaleInEffectForCassandraDB);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleInEffectForCassandraDB);
			 actionEffects.put(scaleInEffectForCassandraDB.getTargetedEntityID(), l);
		 }
		 
		 if (actionEffects.containsKey(scaleInEffectForWebServer.getTargetedEntityID()))
			 actionEffects.get(scaleInEffectForWebServer.getTargetedEntityID()).add(scaleInEffectForWebServer);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleInEffectForWebServer);
			 actionEffects.put(scaleInEffectForWebServer.getTargetedEntityID(), l);
		 }
//	 if (actionEffects.containsKey(scaleInEffectForHadoopSlave.getTargetedEntityID()))
//			 actionEffects.get(scaleInEffectForHadoopSlave.getTargetedEntityID()).add(scaleInEffectForHadoopSlave);
//		 else{
//			 List <ActionEffect > l = new ArrayList<ActionEffect>();
//			 l.add(scaleInEffectForHadoopSlave);
//			 actionEffects.put(scaleInEffectForHadoopSlave.getTargetedEntityID(), l);
//		 }
		 

		for (Entry<String, List<ActionEffect>> e:actionEffects.entrySet()){
			for (ActionEffect ef:e.getValue()){
				
				ef.refreshMetricsForAboveLevels(dependencyGraph,syblAPI);
				
			}
		}
		return actionEffects;
	}
	 public static HashMap<String,List<ActionEffect>> getActionEffects () {
			HashMap<String,List<ActionEffect>> actionEffects = new HashMap<String,List<ActionEffect>>();

			JSONParser parser = new JSONParser();
		 
			try {
				InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(Configuration.getEffectsPath());
				Object obj = parser.parse(new InputStreamReader(inputStream));
		 
				JSONObject jsonObject = (JSONObject) obj;
				
				for (Object actionName:jsonObject.keySet()){
					ActionEffect actionEffect = new ActionEffect();
					String myaction = (String )actionName;
					actionEffect.setActionType((String)actionName);
					actionEffect.setActionName(myaction);
					JSONObject object=(JSONObject) jsonObject.get(myaction);
				for (Object actions: object.keySet()){
					
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
							
							//System.out.println("metricName="+metricName+" metric effect="+metriceffects.get(metricName)+"Affected unit="+affectedUnit);
							
							}catch(Exception e){
								
								actionEffect.setActionEffectForMetric(metricName, ((Long)metriceffects.get(metricName)+0.0), affectedUnit);
								
							//	System.out.println("metricName="+metricName+" metric effect="+metriceffects.get(metricName)+"Affected unit="+affectedUnit);
					
								}
						}}
				}
				 List <ActionEffect > l = new ArrayList<ActionEffect>();
				 l.add(actionEffect);
				actionEffects.put(actionEffect.getTargetedEntityID(), l);
				}
		 
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
	return actionEffects;	 
		     }
	 
}
