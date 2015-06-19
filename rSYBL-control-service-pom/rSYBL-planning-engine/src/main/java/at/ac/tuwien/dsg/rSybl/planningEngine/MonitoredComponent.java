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

package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MonitoredComponent extends MonitoredEntity{
	private String id;
	private HashMap<String,Double> monitoredData=new HashMap<String,Double>();
	private HashMap<String,String> monitoredVariables = new HashMap<String,String>();

	public void setMonitoredValue(String data, Double value){
            
		monitoredData.put(data,value);
	}
	public Double getMonitoredValue(String data){
		return getMonitoredData().get(data);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}		
	public Collection<String> getMonitoredMetrics(){
		return getMonitoredData().keySet();
	}
	public void setMonitoredVar(String data, String value){
		getMonitoredVariables().put(data,value);
	}
	public String getMonitoredVar(String data){
		return getMonitoredVariables().get(data);
	}
	public MonitoredComponent clone(){
		MonitoredComponent cloudService=new MonitoredComponent();
		cloudService.setId(id);
		HashMap<String,Double> newMonitoredData = new HashMap<String,Double>();
		for (String entry:getMonitoredData().keySet()){
			
			newMonitoredData.put(entry, getMonitoredData().get(entry).doubleValue());
		}

		HashMap<String,String> newMonitoredVariables = new HashMap<String,String>();
		for (String entry:getMonitoredVariables().keySet()){
			newMonitoredVariables.put(entry, getMonitoredVariables().get(entry));
		}

		newMonitoredVariables.putAll(getMonitoredVariables());
		cloudService.setMonitoredData(newMonitoredData);
		cloudService.setMonitoredVariables(newMonitoredVariables);
		return cloudService;
	}
	public HashMap<String,Double> getMonitoredData() {
		return monitoredData;
	}
	public void setMonitoredData(HashMap<String,Double> monitoredData) {
		this.monitoredData = monitoredData;
	}
	public HashMap<String,String> getMonitoredVariables() {
		return monitoredVariables;
	}
	public void setMonitoredVariables(HashMap<String,String> monitoredVariables) {
		this.monitoredVariables = monitoredVariables;
	}
}
