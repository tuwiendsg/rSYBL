/** 
   Copyright 2013 Technische Universität Wien (TUW), Distributed Systems Group E184

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
package at.ac.tuwien.dsg.sybl.localService.processing;

import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.localService.syblLocalRuntime.SyblMonitorAndEnforceLocally;




public class SYBLProcessingThread implements Runnable {
    public HashMap<String,Boolean> cons = new HashMap<String,Boolean>();
    SyblMonitorAndEnforceLocally syblAPI ;
    Thread t;
	boolean ok = true;
	Utils utils ;
	long REFRESH_TIME=5000;
    public SYBLProcessingThread(SYBLAnnotation syblAnnotation){
    	
		    syblAPI = new SyblMonitorAndEnforceLocally();
		    	utils = new Utils(syblAnnotation.getPriorities(),syblAnnotation.getMonitoring(),syblAnnotation.getConstraints(),syblAnnotation.getStrategies(),syblAPI);
		t = new Thread(this);	
		
	}
    
	public void stop(){
		ok = false;
		try{
		t.stop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void singleRun(){
		utils.processSyblSpecifications();
	}
	
	public void start(){
		t.start();
	}
	
	public void run() {
		while (ok){
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




}
