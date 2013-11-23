/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.                 This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).

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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;



public interface MonitoringInterface {
    
	  public float getCpuUsage(Node node);
	    public float getMemoryAvailable(Node node);
	    public float getMemorySize(Node node);
	    public float getMemoryUsage(Node node);   
	    public float getDiskSize(Node node);
	    public float getDiskAvailable(Node node);
	    public float getDiskUsage(Node node);
	    public float getCPUSpeed(Node node);
	    public float getPkts(Node node);
	    public float getPktsIn(Node node);
	    public float getPktsOut(Node node);
	    public float getReadLatency(Node node);
	    public float getWriteLatency(Node node);
	    public float getReadCount(Node node);
	    public float getCostPerHour(Node node);
	    public float getWriteCount(Node node);
	    public float getTotalCostSoFar(Node node);
	    public List<String> getAvailableMetrics();
	    public void submitServiceConfiguration(Node node);
	    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description);
	    public void notifyControlActionStarted(String actionName, Node node);
		public void notifyControlActionEnded(String actionName, Node node);
	    public float getMetricValue(String metricName, Node node);
	    public float getNumberInstances(Node node);
	    public void refreshServiceStructure(Node node);
}
