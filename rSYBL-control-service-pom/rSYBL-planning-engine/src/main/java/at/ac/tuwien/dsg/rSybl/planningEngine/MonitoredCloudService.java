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
import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;

public class MonitoredCloudService extends MonitoredEntity{
	private String id;
	private HashMap<String,Float> monitoredData=new HashMap<String,Float>();
	private ArrayList<MonitoredComponentTopology> monitoredTopologies = new ArrayList<MonitoredComponentTopology>();
	private HashMap<String,String> monitoredVariables = new HashMap<String,String>();
	public MonitoredCloudService(){
		
	}
	public void setMonitoredValue(String data, Float value){
		getMonitoredData().put(data,value);
	}
	public Float getMonitoredValue(String data){
		return getMonitoredData().get(data);
	}	
	public void setMonitoredVar(String data, String value){
		getMonitoredVariables().put(data,value);
	}
	public String getMonitoredVar(String data){
		return getMonitoredVariables().get(data);
	}
	public void addMonitoredTopology(MonitoredComponentTopology componentTopology){
		getMonitoredTopologies().add(componentTopology);
	}
	public List<MonitoredComponentTopology> getMonitoredTopologies(){
		return monitoredTopologies;
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
	public MonitoredCloudService clone(){
		MonitoredCloudService cloudService=new MonitoredCloudService();
		cloudService.setId(id);
		HashMap<String,Float> newMonitoredData = new HashMap<String,Float>();
		for (String entry:getMonitoredData().keySet()){
			newMonitoredData.put(entry, getMonitoredData().get(entry).floatValue());
		}
		ArrayList<MonitoredComponentTopology> newMonitoredTopology=new ArrayList<MonitoredComponentTopology>();
		for (MonitoredComponentTopology monitoredComponentTopology:getMonitoredTopologies()){
		    newMonitoredTopology.add(monitoredComponentTopology.clone());
		}
		HashMap<String,String> newMonitoredVariables = new HashMap<String,String>();
		for (String entry:getMonitoredVariables().keySet()){
		
			newMonitoredVariables.put(entry, getMonitoredVariables().get(entry));
		}

		cloudService.setMonitoredData(newMonitoredData);
		cloudService.setMonitoredTopologies(newMonitoredTopology);
		cloudService.setMonitoredVariables(newMonitoredVariables);
		return cloudService;
	}
	public HashMap<String,Float> getMonitoredData() {
		return monitoredData;
	}
	public void setMonitoredData(HashMap<String,Float> monitoredData) {
		this.monitoredData = monitoredData;
	}
	public void setMonitoredTopologies(ArrayList<MonitoredComponentTopology> monitoredTopologies) {
		this.monitoredTopologies = monitoredTopologies;
	}
	public HashMap<String,String> getMonitoredVariables() {
		return monitoredVariables;
	}
	public void setMonitoredVariables(HashMap<String,String> monitoredVariables) {
		this.monitoredVariables = monitoredVariables;
	}
}
