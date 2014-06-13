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


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.OfferedEnforcementCapabilities;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.MELA_API;



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
		RuntimeLogger.logger.info("~~~~~~~~~~~Trying to execute action executingControlaction="+executingControlAction);

		if (executingControlAction==false){
			if (arg0.getAllRelatedNodes().size()>1){
		executingControlAction=true;
		
		offeredCapabilities.scaleIn(arg0);
		List<String> metrics= monitoringAPIInterface.getAvailableMetrics(arg0);
		boolean checkIfMetrics=false;
		//monitoringAPIInterface.enforcingActionStarted("ScaleIn", arg0);
		while (!checkIfMetrics){
			boolean myMetrics=true;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RuntimeLogger.logger.info("Waiting for action....");
			
			
		for (String metricName:metrics){
			RuntimeLogger.logger.info("Metric "+metricName+" has value "+monitoringAPIInterface.getMetricValue(metricName,arg0));

			if (monitoringAPIInterface.getMetricValue(metricName,arg0)==null || monitoringAPIInterface.getMetricValue(metricName, arg0)<=0 ){
				myMetrics=false;
				RuntimeLogger.logger.info("~~~~Metric "+metricName+"smaller than 0");
			}
			
			
		}
		checkIfMetrics=myMetrics;
		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		executingControlAction=false;
		//monitoringAPIInterface.enforcingActionEnded("ScaleIn", arg0);
		RuntimeLogger.logger.info("Finished scaling in "+arg0.getId()+" ...");
			}else{
				RuntimeLogger.logger.info("Number of nodes associated with "+arg0.getAllRelatedNodes().size());
			}
		}

	}




	public void scaleout(Node arg0) {
		RuntimeLogger.logger.info("~~~~~~~~~~~Trying to execute action executingControlaction="+executingControlAction);
		if (executingControlAction==false && arg0!=null){

		RuntimeLogger.logger.info("Scaling out "+arg0+" ...");
		executingControlAction=true;
		offeredCapabilities.scaleOut(arg0);
		
		List<String> metrics= monitoringAPIInterface.getAvailableMetrics(arg0);
		//monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
		boolean checkIfMetrics=false;
		while (!checkIfMetrics){
			boolean myMetrics=true;
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			RuntimeLogger.logger.info("Waiting for action....");
		for (String metricName:metrics){
			
				RuntimeLogger.logger.info("Metric "+metricName+" has value "+monitoringAPIInterface.getMetricValue(metricName,arg0));
			if (monitoringAPIInterface.getMetricValue(metricName,arg0)==null || monitoringAPIInterface.getMetricValue(metricName, arg0)<=0 ){
				myMetrics=false;
				RuntimeLogger.logger.info("~~~Metric "+metricName+"smaller than 0");
			}
			
			
		}
		checkIfMetrics=myMetrics;
		}
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		executingControlAction=false;
		//monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
		RuntimeLogger.logger.info("Finished scaling out "+arg0.getId()+" ...");
		}else{
			RuntimeLogger.logger.info(arg0);
		}
	}




	@Override
	public void enforceAction(String actionName, Node e) {
		RuntimeLogger.logger.info("~~~~~~~~~~~Trying to execute action executingControlaction="+executingControlAction);

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


	//TODO depending on the protocol specified and the parameters, call the capability = default parameter - Service Part ID
	@Override
	public void enforceElasticityCapability(ElasticityCapability capability,
			Node e) {
		if (capability.getCallType().toLowerCase().contains("rest")){
			 URL url = null;
		        HttpURLConnection connection = null;
		        try {
		        	
		            url = new URL(capability.getEndpoint());
		            connection = (HttpURLConnection) url.openConnection();
		            connection.setDoOutput(true);
		            connection.setInstanceFollowRedirects(false);
		            if (capability.getCallType().toLowerCase().contains("post"))
		            connection.setRequestMethod("POST");
		            else
		            	connection.setRequestMethod("PUT");
		           

		            //write message body
		            OutputStream os = connection.getOutputStream();

		            if (capability.getParameter().size()==0){
		            	 connection.setRequestProperty("Content-Type", "text/plain");
				            connection.setRequestProperty("Accept", "text/plain");
		            	os.write(e.getId().getBytes());
		            }
		            else
		            {
		            	//tODO: add parameters here parameter=x
		            }
		            os.flush();
		            os.close();

		            InputStream errorStream = connection.getErrorStream();
		            if (errorStream != null) {
		                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
		                String line;
		                while ((line = reader.readLine()) != null) {
		                    Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, line);
		                }
		            }

		            InputStream inputStream = connection.getInputStream();
		            if (inputStream != null) {
		                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		                String line;
		                while ((line = reader.readLine()) != null) {
		                    Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, line);
		                }
		            }
		            actionName="";
		        } catch (Exception e) {
		        	actionName="";
		            Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		        } finally {
		        	actionName="";
		        	this.actionTargetEntity="";
		            if (connection != null) {
		                connection.disconnect();
		            }
		        }
		}
		
	}







	





	


}
