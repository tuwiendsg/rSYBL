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

package at.ac.tuwien.dsg.sybl.syblProcessingUnit.processing;


import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.Configuration;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;

public class SYBLProcessingThread implements Runnable {
    public HashMap<Node,Boolean> cons = new HashMap<Node,Boolean>();
    MonitoringAPIInterface monitoringAPI ;
    Thread t;
	boolean ok = true;
	Utils utils ;
	long REFRESH_TIME=Configuration.getRefreshPeriod();
	Node currentEntity ;
	private DependencyGraph dependencyGraph;
    public SYBLProcessingThread(SYBLAnnotation syblAnnotation, Node ent, DependencyGraph dependencyGraph,MonitoringAPIInterface monitoringAPI,EnforcementAPIInterface enforcementAPI){
    	this.dependencyGraph = dependencyGraph;
    	try {
		    currentEntity = ent;
		    this.monitoringAPI=monitoringAPI;
		    REFRESH_TIME=Configuration.getRefreshPeriod();
		} catch (Exception e) {
		    SYBLDirectivesEnforcementLogger.logger.error("Client exception rmiSYBLRuntime: " + e.toString());
		    e.printStackTrace();
		}

    	utils = new Utils(currentEntity,syblAnnotation.getNotifications(),syblAnnotation.getPriorities(),syblAnnotation.getMonitoring(),syblAnnotation.getConstraints(),syblAnnotation.getStrategies(),syblAnnotation.getGovernanceScopes(),monitoringAPI,enforcementAPI,dependencyGraph);
		t = new Thread(this);	
		
	}
    
	public void stop(){
		while (utils.isEnforcingAction()){
			try {
				t.wait(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ok = false;
		try{
		t.stop();
		SYBLDirectivesEnforcementLogger.logger.info("Stopping thread ... ");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void singleRun(){
		utils.processSyblSpecifications();
	}
	@Override
	public void run() {
		while (ok){
                    utils.processGovernanceScopes();
                    utils.processSyblSpecifications();
                        
			utils.clearDisabledRules();
		
			try {
				Thread.sleep(REFRESH_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public void start(){
		t.start();
	}
}
