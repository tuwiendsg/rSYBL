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
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.OfferedEnforcementCapabilities;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;



public class EnforcementAPI implements EnforcementAPIInterface{
	private  static HashMap<Node, ArrayList<Float>> avgRunningTimes = new HashMap<Node,ArrayList<Float>>();
	
	private boolean executingControlAction = false;
	private MonitoringAPIInterface monitoringAPIInterface;
    private Node controlledService;
    private EnforcementInterface offeredCapabilities;
	public EnforcementAPI(){
		
	}
	

	
	public void setControlledService(Node controlledService) {
		this.controlledService = controlledService;
		offeredCapabilities = OfferedEnforcementCapabilities.getInstance(this.controlledService);
	}
	
	
	
	

	public boolean isExecutingControlAction() {
		return executingControlAction;
	}



	public void scalein(Node arg0) {
		if (executingControlAction==false){
			if (arg0.getAllRelatedNodes().size()>1){
		executingControlAction=true;
		
		offeredCapabilities.scaleIn(arg0);
		List<String> metrics= monitoringAPIInterface.getAvailableMetrics(arg0);
		boolean checkIfMetrics=false;
		monitoringAPIInterface.enforcingActionStarted("ScaleIn", arg0);
		while (!checkIfMetrics){
			boolean myMetrics=false;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RuntimeLogger.logger.info("Waiting for action....");
			
			
		for (String metricName:metrics){
			
			if (monitoringAPIInterface.getMetricValue(metricName, arg0)<0){
				myMetrics=true;
				RuntimeLogger.logger.info("Metric "+metricName+"smaller than 0");
			}
			
			
		}
		checkIfMetrics=myMetrics;
		}
		executingControlAction=false;
		monitoringAPIInterface.enforcingActionEnded("ScaleIn", arg0);
		RuntimeLogger.logger.info("Finished scaling in "+arg0.getId()+" ...");
			}else{
				RuntimeLogger.logger.info("Number of nodes associated with "+arg0.getAllRelatedNodes().size());
			}
		}

	}




	public void scaleout(Node arg0) {
		if (executingControlAction==false && arg0!=null){
		RuntimeLogger.logger.info("~~~~~~~~~~~~~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		RuntimeLogger.logger.info("Scaling out "+arg0+" ...");
		executingControlAction=true;
		offeredCapabilities.scaleOut(arg0);
		
		List<String> metrics= monitoringAPIInterface.getAvailableMetrics(arg0);
		monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
		boolean checkIfMetrics=false;
		while (!checkIfMetrics){
			boolean myMetrics=false;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RuntimeLogger.logger.info("Waiting for action....");
		for (String metricName:metrics){
			
			
			if (monitoringAPIInterface.getMetricValue(metricName, arg0)<0){
				myMetrics=true;
				RuntimeLogger.logger.info("Metric "+metricName+"smaller than 0");
			}
			
			
		}
		checkIfMetrics=myMetrics;
		}
		executingControlAction=false;
		monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
		RuntimeLogger.logger.info("Finished scaling out "+arg0.getId()+" ...");
		}else{
			RuntimeLogger.logger.info(arg0);
		}
	}




	@Override
	public void enforceAction(String actionName, Node e) {
		if (executingControlAction==false){
		RuntimeLogger.logger.info("Enforcing action "+actionName+" on the node "+e+" ...");

		executingControlAction=true;

		offeredCapabilities.enforceAction(actionName, e);
		try {
			Thread.sleep(30000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		executingControlAction=false;
		RuntimeLogger.logger.info("Finished enforcing action "+actionName+" on the node "+e+" ...");
		}
	}


	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoringInterface) {
		monitoringAPIInterface=monitoringInterface;
		offeredCapabilities.setMonitoringPlugin(monitoringInterface);
	}



	
	

	@Override
	public Node getControlledService() {
		// TODO Auto-generated method stub
		return offeredCapabilities.getControlledService();
	}



	@Override
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		// TODO Auto-generated method stub
		
	}



	





	


}
