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
import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;





public class ActionEffectsCassandraCluster {
	public static ActionEffect scaleOutEffectForCassandraDB = new ActionEffect();
	public static ActionEffect scaleOutEffectForCassandraTopology=new ActionEffect();
//	public static ActionEffect scaleOutEffectForHadoopSlave= new ActionEffect();
	public static ActionEffect scaleInEffectForCassandraDB = new ActionEffect();
	public static ActionEffect scaleInEffectForCassandraTopology= new ActionEffect();
//	public static ActionEffect scaleInEffectForHadoopSlave= new ActionEffect();

	

	public static HashMap<String,List<ActionEffect>> getActionEffects(DependencyGraph dependencyGraph,MonitoringAPIInterface syblAPI){
		HashMap<String,List<ActionEffect>> actionEffects = new HashMap<String,List<ActionEffect>>();
		
		{
			scaleOutEffectForCassandraDB.setTargetedEntityID("CassandraDB");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cpu.usage", -40.0f,"CassandraDB");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("mem.usage", -40.0f,"CassandraDB");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("latency.average", -3.0f,"CassandraDB");
			scaleOutEffectForCassandraDB.setActionEffectForMetric("cost", 50.0f,"CassandraDB");
			scaleOutEffectForCassandraDB.setActionName("scaleOutEffectForCassandraDB");
			scaleOutEffectForCassandraDB.setActionType("scaleout");

		}


		{
			scaleOutEffectForCassandraTopology.setTargetedEntityID("CassandraServiceTopology") ; /// different than data serving topology
			scaleOutEffectForCassandraTopology.setActionEffectForMetric("cpu.usage", -40.0f,"CassandraDB");
			scaleOutEffectForCassandraTopology.setActionEffectForMetric("cost", 50.0f,"CassandraDB");
			scaleOutEffectForCassandraTopology.setActionEffectForMetric("latency.average", -3.0f,"CassandraDB");
			scaleOutEffectForCassandraTopology.setActionEffectForMetric("cpu.usage", -30.0f,"CassandraController");
			scaleOutEffectForCassandraTopology.setActionEffectForMetric("cost", 50.0f,"CassandraController");
			scaleOutEffectForCassandraTopology.setActionName("scaleOutEffectForCassandraTopology");
			scaleOutEffectForCassandraTopology.setActionType("scaleout");

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
			scaleInEffectForCassandraDB.setTargetedEntityID("CassandraDB");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cpu.usage", 40.0f,"CassandraDB");
			scaleInEffectForCassandraDB.setActionEffectForMetric("mem.usage", 40.0f,"CassandraDB");
			scaleInEffectForCassandraDB.setActionEffectForMetric("latency.average", 3.0f,"CassandraDB");
			scaleInEffectForCassandraDB.setActionEffectForMetric("cost", -50.0f,"CassandraDB");
			scaleInEffectForCassandraDB.setActionName("scaleInEffectForCassandraDB");
			scaleInEffectForCassandraDB.setActionType("scalein");

		}

		{
			scaleInEffectForCassandraTopology.setTargetedEntityID("CassandraServiceTopology") ; /// different than data serving topology
			scaleInEffectForCassandraTopology.setActionEffectForMetric("cpu.usage", 40.0f,"CassandraDB");
			scaleInEffectForCassandraTopology.setActionEffectForMetric("cost", -50.0f,"CassandraDB");
			scaleInEffectForCassandraTopology.setActionEffectForMetric("cpu.usage", 30.0f,"CassandraController");
			scaleInEffectForCassandraTopology.setActionEffectForMetric("cost", -50.0f,"CassandraController");
			scaleInEffectForCassandraTopology.setActionName("scaleInEffectForCassandraTopology");
			scaleInEffectForCassandraTopology.setActionType("scalein");

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
		 
		 if (actionEffects.containsKey(scaleOutEffectForCassandraTopology.getTargetedEntityID()))
			 actionEffects.get(scaleOutEffectForCassandraTopology.getTargetedEntityID()).add(scaleOutEffectForCassandraTopology);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleOutEffectForCassandraTopology);
			 actionEffects.put(scaleOutEffectForCassandraTopology.getTargetedEntityID(), l);
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
		 
		 if (actionEffects.containsKey(scaleInEffectForCassandraTopology.getTargetedEntityID()))
			 actionEffects.get(scaleInEffectForCassandraTopology.getTargetedEntityID()).add(scaleInEffectForCassandraTopology);
		 else{
			 List <ActionEffect > l = new ArrayList<ActionEffect>();
			 l.add(scaleInEffectForCassandraTopology);
			 actionEffects.put(scaleInEffectForCassandraTopology.getTargetedEntityID(), l);
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
	
}
