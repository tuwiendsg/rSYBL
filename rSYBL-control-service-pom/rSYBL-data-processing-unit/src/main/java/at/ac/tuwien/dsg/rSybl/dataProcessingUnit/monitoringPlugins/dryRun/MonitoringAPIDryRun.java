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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.dryRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import java.util.HashMap;

public class MonitoringAPIDryRun  implements MonitoringInterface{
	private double FIXED_VALUE=0;
        private List<String> actions = new ArrayList<String>();
        private List<String> targets = new ArrayList<String>();
        private Node controlService= null;
	private Double returnRandomValue(){
		Random random = new Random();
		return random.nextDouble()*100;
	}
	@Override
	public Double getCpuUsage(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getMemoryAvailable(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getMemorySize(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getMemoryUsage(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getDiskSize(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getDiskAvailable(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getDiskUsage(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue()*100;
	}

	@Override
	public Double getCPUSpeed(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getPkts(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getPktsIn(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getPktsOut(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getReadLatency(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getWriteLatency(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getReadCount(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getCostPerHour(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getWriteCount(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getTotalCostSoFar(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public List<String> getAvailableMetrics(Node node) {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public void submitServiceConfiguration(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitCompositionRules(String composition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyControlActionStarted(String actionName, Node node) {
		this.actions.add(actionName);
		this.targets.add(node.getId());
	}

	@Override
	public void notifyControlActionEnded(String actionName, Node node) {
		this.actions.remove(actionName);
		this.targets.remove(node.getId());
		
	}

	@Override
	public Double getMetricValue(String metricName, Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public Double getNumberInstances(Node node) {
		// TODO Auto-generated method stub
		return  returnRandomValue();
	}

	@Override
	public void refreshServiceStructure(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getOngoingActionID() {
		// TODO Auto-generated method stub
		return actions;
	}

	@Override
	public List<String> getOngoingActionNodeID() {
		// TODO Auto-generated method stub
		return targets;
	}

	@Override
	public void submitCompositionRules() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkIfMetricsValid(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public void sendMessageToAnalysisService(String message) {
       
    }

    @Override
    public void sendControlIncapacityMessage(String message, List<ElasticityRequirement> cause) {
        
    }

    @Override
    public void removeService(Node service) {
        ;
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformation() {
      List<MonitoringSnapshot> metricsValues = new ArrayList<MonitoringSnapshot>();
       return metricsValues;
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformationOnPeriod(long time) {
               List<MonitoringSnapshot> metricsValues = new ArrayList<MonitoringSnapshot>();
       return metricsValues;
    }
    
    public boolean isHealthy(){
        return true;
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformationFromTimestamp(long timestamp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
   public void setCurrentCloudService (Node cloudService){
        this.controlService=cloudService;
    }
}
