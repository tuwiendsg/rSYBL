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
package at.ac.tuwien.dsg.sybl.localService.syblLocalRuntime;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/*
 * TODO : implement for the SYBLAPI to be able to delegate towards higher control levels
 */
public class SyblMonitorAndEnforceLocally {
	//SigarDataMonitor sigarDataMonitor = new SigarDataMonitor();
	private  static HashMap<String, ArrayList<Float>> avgRunningTimes = new HashMap<String,ArrayList<Float>>();
	private Date startingTime ;
	private Date refreshedCost ;
	private float totalCost = 0;

	private int MAX_COST =300;
    private String methodName = "";
   
 	public SyblMonitorAndEnforceLocally(){
		 
	       Date d = new Date();
	     startingTime = d;
	     refreshedCost =d;
	}

	
	public Float getAverageRunningTime(){
		if (!avgRunningTimes.containsKey(methodName))
			return 0f;
		float sum = 0;
		for (Float x:avgRunningTimes.get(methodName)){
			sum+=x;
		}
		return sum/avgRunningTimes.get(methodName).size();
	}
	
	public void refreshRunningTime(String methodName){
		if (!avgRunningTimes.containsKey(methodName))
			avgRunningTimes.put(methodName,new ArrayList<Float>());
		avgRunningTimes.get(methodName).add((new Date().getTime()-startingTime.getTime())/1000.0f);
	}
	public Float getElapsedTime(){
		return (new Date().getTime()-startingTime.getTime())/1000.0f;
	}
	
	
	public Float getCurrentAvailability() {
		return new Float(0);
	}

	public Float getCurrentResponseTime(){
		
		return 3f+(getInstantCost()-getAverageCost())/MAX_COST;
	}
	public Float getEstimatedAvailability() {
		return new Float(0);
	}


	public Float getAverageCost() {
		Date now = new Date();
	
		totalCost += getInstantCost()*((now.getTime()-refreshedCost.getTime())/1000.0f);
		System.out.println((now.getTime()-refreshedCost.getTime()));
		//System.out.println("Instant cost "+getInstantCost()+" Total cost "+totalCost+" runningTime "+((now.getTime()-startingTime.getTime())/1000.0f));
		float avgCost = 0;
		if (!now.equals(startingTime))
		 avgCost = totalCost/((now.getTime() - startingTime.getTime())/1000.0f);
		else
			avgCost = getInstantCost();
		refreshedCost = now;
		
		System.out.println("Average Cost "+avgCost);	
		return avgCost;
	}
	




	/*
	public Float getCurrentDataSourceMemUsage(){
		return elasticityMonitoringConnection.getMemoryUsagePerc();

	}*/

	/**
	 * TODO: if entity is present - maybe we can get something more interesting like application level stuff
	 * @param e
	 * @return
	 */
	

	public Float getCurrentRAMSpeed() {
		return new Float(0);
	}


	public Float getCurrentHDDSize() {
		return new Float(0);
	}


	public Float getCurrentHDDSpeed() {
		return new Float(0);
	}
	/**
	 * Instant cost at programming level is fixed for now - it can however depend on ios, network bla bla etc. 
	 * - depending on specific component type - (we can have diff types of sybl runtimes from which the developer can choose )
	 */
	public Float getInstantCost(){
		return 20.0f;
	}
	
	public Float getTotalCostSoFar(){
		Date now = new Date();
	
		totalCost += getInstantCost()*(((float)now.getTime()-refreshedCost.getTime())/1000.0f);
		refreshedCost = now;
		return totalCost;
	}
	
	
	////************Actions*****************////
/*	public void scaleOutDataSource(){
		System.out.println("Scaling out on datasource...");
		elasticityMonitoringConnection.scaleOut();
	}
	public void scaleInDataSource(){
		System.out.println("Scaling in on datasource...");
		elasticityMonitoringConnection.scaleIn();
	}
	*/
//	public void scaleUpOn(String onWhatToScale, Float howMuch){
//		System.out.println("Scaling the instance on "+onWhatToScale+" by "+howMuch);
//	}
//	public void scaleUp(){
//		System.out.println("Calling Scale Up...");
//
//		//cloudsOpenStackConnection.scaleUpInstance(instanceName);
//	}
//	public void scaleUp(Entity entity){
//		 //TODO send entity name (Component or application name) entity.getName(), and use an association between
//		//VM snapshots and the component to be able to scale out 
//		System.out.println("Calling Scale Up...");
//		cloudsOpenStackConnection.scaleUpInstance(entity.getName());
//		
//	}
//	public void scaleDown(){
//		System.out.println("Calling Scale down...");
//		//cloudsOpenStackConnection.scaleDownInstance(instanceName);
//	
//
//	}
//	
//	public void scaleDown(Entity entity){
//		cloudsOpenStackConnection.scaleDownInstance(entity.getName());
//	}
//	public void scaleDownOn(String onWhatToScale, Float howMuch){
//		System.out.println("Scaling the instance on "+onWhatToScale+" by "+howMuch);
//	}



}
