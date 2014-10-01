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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.gangliaMonitoring;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.ganglia.gangliaInfo.GangliaClusterInfo;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.ganglia.gangliaInfo.GangliaMetricInfo;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;



public class MonitoringGangliaAPI implements Runnable,MonitoringInterface{
	private Node controlledService;
	private GangliaClusterInfo clusterInfo;
	private GangliaMonitor gangliaMonitor;
	private Date startingTime;
	private Date refreshedCost;
	private Double totalCost = 0.0;
	private int MAX_COST =300;
    private String methodName = "";
	private Thread t ;
	private boolean isExecutingControlAction = false;
	public MonitoringGangliaAPI(Node cloudService){
		try{
		controlledService=cloudService;
		gangliaMonitor=new GangliaMonitor();
		t = new Thread(this);
		Date d  = new Date();
	     startingTime = d;
	     refreshedCost =d;
	     List <String> ips =controlledService.getAssociatedIps();
	     String certificatePath = Configuration.getCertificatePath();
	    	try {
				clusterInfo = gangliaMonitor.monitorGangliaNodesIndirectly(ips,(String)controlledService.getStaticInformation("AccessIP"),certificatePath,Configuration.getGangliaPort()+"");
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				RuntimeLogger.logger.error("Error when creating new cluster info"+e.toString()+" "+e.getMessage());
			}catch ( IOException   ex){
				RuntimeLogger.logger.error("Error when creating new cluster info"+ex.toString());
			}
			start();

	    	createMonitoringThreads();
		}catch(Exception e){
			RuntimeLogger.logger.error(e.toString()+" in Monitoring Ganglia API isntantiation");

		}
	}
    public void start(){
    	t.start();
    }
    public void stop(){
    	t.stop();
    }
    public void run(){
    	while (true){
    	List <String> ips =controlledService.getAssociatedIps();
    	String certificatePath = Configuration.getCertificatePath();
    	try {
			clusterInfo = gangliaMonitor.monitorGangliaNodesIndirectly(ips,Configuration.getGangliaIP(),certificatePath,Configuration.getGangliaPort()+"");
    	} catch (JAXBException e) {
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error("JAXB ex when creating new cluster info"+e.toString());
		}catch ( IOException   ex){
			RuntimeLogger.logger.error("IOException when creating new cluster info"+ex.toString()+" "+ex.getMessage());
		}
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    }
    public Double getAverageCost(Node entity){
    	Date now = new Date();
    	
		totalCost += getInstantCost(entity)*((now.getTime()-refreshedCost.getTime())/1000.0f);
		RuntimeLogger.logger.info((now.getTime()-refreshedCost.getTime()));
		//RuntimeLogger.logger.info("Instant cost "+getInstantCost()+" Total cost "+totalCost+" runningTime "+((now.getTime()-startingTime.getTime())/1000.0f));
		Double avgCost = 0.0;
		if (!now.equals(startingTime))
		 avgCost = totalCost/((now.getTime() - startingTime.getTime())/1000.0f);
		else
			avgCost = getInstantCost(entity);
		refreshedCost = now;
		
		RuntimeLogger.logger.info("Average Cost "+avgCost);	
		return avgCost;
    }
    public Double getCpuUsage(Node e){
    	try{
    		Double retValue = 0.0;
    		int nb=0;	
    		for (String ip:e.getAssociatedIps()){
    			if(ip.split("\\.")[0].length()==2){
    			retValue += (100.0f - Double.parseDouble(searchMetricsByExactName("cpu_idle",ip).getValue()));
    			nb++;
    			}
    		}
    		
    		if (nb>0)
    		 retValue=retValue/nb;
    		RuntimeLogger.logger.info("Current cpu usage for entity "+ e.getId()+" is "+retValue);
    	return retValue;
    		}catch (Exception ex){
    			return 0.0;
    		}
    	
    	    }
    public Double getMemoryAvailable(Node e){
    	Double retValue = 0.0;
		for (String ip:e.getAssociatedIps()){
			if(ip.split("\\.")[0].length()==2)
	    	try{
	    		retValue+=(Double.parseDouble(searchMetricsByExactName("mem_free",ip).getValue()));
	    	}catch(Exception ex){
	    		
	    		RuntimeLogger.logger.info("Metric mem_total not found for ip "+ip);
	    		
	    	}
    	}
    	return retValue ;  	
    	
    }
    public Double getMemoryAvailable(String ip){
    	Double retValue = 0.0;

	    	try{
	    		retValue+=(Double.parseDouble(searchMetricsByExactName("mem_free",ip).getValue()));
	    	}catch(Exception ex){
	    		
	    		
	    	}
    	
    return retValue;
       }
    
    public Double getMemorySize(Node e){
    	Double retValue = 0.0;
		for (String ip:e.getAssociatedIps()){
			if(ip.split("\\.")[0].length()==2)
	    	try{
	    		retValue+=(Double.parseDouble(searchMetricsByExactName("mem_total",ip).getValue()));
	    	}catch(Exception ex){
	    		
	    		RuntimeLogger.logger.info("Metric mem_total not found for ip "+ip);
	    		
	    	}
    	}
    	return retValue ;
    }
    public Double getMemorySize(String ip){
    	return (Double.parseDouble(searchMetricsByExactName("mem_total",ip).getValue()));
    }
    public Double getMemoryUsage(Node e){
		try{
		Double retValue = 0.0;
		int nb=0;
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
			Double val = (getMemoryAvailable(ip)/getMemorySize(ip)*100);
			if (val>=0 && val <=100){
			retValue += val;
			nb++;
			}
			}
		if (nb>0)
		return retValue/nb;
		else return 0.0;


	}catch (Exception ex){
		return 0.0;
	}

    }
   public Double getInstantCost (Node e){
	   int privateIps = 0;
	   for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				privateIps++;
			}
		return 20.0*privateIps;
   }
   public Double getTotalCostSoFar(Node e) {
		Date now = new Date();
	
		totalCost += getInstantCost(e)*((now.getTime()-refreshedCost.getTime())/1000.0f);
		refreshedCost = now;
		return totalCost;
	}
   private void createMonitoringThreads(){
		
		ArrayList<MonitoringThread> monitoringThreads = new ArrayList<MonitoringThread>();
		
		monitoringThreads.add(new MonitoringThread(controlledService,this));
		Node topology =  controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP).get(0);
		monitoringThreads.add(new MonitoringThread(topology,this));
		List<Node> topologies =new ArrayList<Node>();
		topologies.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY));
		
		List<Node> componentsToExplore = new ArrayList<Node>();
		if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null)
		componentsToExplore.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
		while (!topologies.isEmpty()){
			Node currentTopology = topologies.get(0);
			topologies.remove(0);
			monitoringThreads.add(new MonitoringThread(currentTopology,this));

				if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)!=null && currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY).size()>0)
					topologies.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY));
				if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null && currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT).size()>0)
					componentsToExplore.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
			}
		
		while ( !componentsToExplore.isEmpty()){
			Node component =componentsToExplore.get(0);
			componentsToExplore.remove(0);
			monitoringThreads.add(new MonitoringThread(component,this));

		}
	for (MonitoringThread monitoringThread:monitoringThreads){
		monitoringThread.start();
	}
	}
    public Double getDiskSize(Node e){
    	Double retValue = 0.0;
		
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
			retValue +=Double.parseDouble(searchMetricsByExactName("disk_total",ip).getValue());
			}
		return retValue;	
    }
    
    public Double getDiskSize(String ip){
    	RuntimeLogger.logger.error("Get disk size for ip "+ip+" "+searchMetricsByExactName("disk_total",ip));
			return Double.parseDouble(searchMetricsByExactName("disk_total",ip).getValue());
	
    }
    public Double getDiskAvailable(Node e){
Double retValue = 0.0;
		
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				try{
		    		retValue+=(Double.parseDouble(searchMetricsByExactName("disk_free",ip).getValue()));
		    	}catch(Exception ex){   		
    		
		    	}
			}
		return retValue;	
    	
    }
    public Double getDiskAvailable(String ip){
    	Double result = 0.0;
    	try{
    		result =(Double.parseDouble(searchMetricsByExactName("disk_free",ip).getValue()));
    	}catch(Exception e){
    		
    	//	RuntimeLogger.logger.info("Metric disk_free not found for ip "+ip);
    		
    	}
    	return result ;
    }
    public Double getDiskUsage(Node e){
    	
		Double retValue = 0.0;
		int nb=0;	
		//RuntimeLogger.logger.info("Trying to find hdd usage for entity "+e.getId()+" "+e.getAssociatedIps());
		for (String ip:e.getAssociatedIps()){
			if(ip.split("\\.")[0].length()==2){
				//RuntimeLogger.logger.info("Got Here "+e.getId());
				try{
				if (getDiskSize(ip)>0 && getDiskAvailable(ip)>0)
					retValue =100-(getDiskAvailable(ip)/getDiskSize(ip)*100);
			nb++;
				}catch(Exception ex){
					
				}
			}
		}
		
		if (nb>0)
		 retValue=retValue/nb;
		RuntimeLogger.logger.info("Current hdd usage for entity "+ e.getId()+" is "+retValue);
	return retValue;
	

    }
    public Double getCPUSpeed(Node e){
    		Double retValue = 0.0;
		
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				try{
					retValue +=( Double.parseDouble(searchMetricsByExactName("cpu_speed",ip).getValue()));
		    	}catch(Exception ex){
		    	}
			}
		return retValue;	
		
    	
    }
    public Double getPkts(Node e){
    	
    	return getPktsIn(e)+getPktsOut(e);
    }
    public Double getPktsIn(Node e){
	Double retValue = 0.0;
		
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				try{
					retValue +=( Double.parseDouble(searchMetricsByExactName("pkts_in",ip).getValue()));
		    	}catch(Exception ex){
		    	}
			}
		return retValue;
    	
    }
    public Double getPktsOut(Node e){
Double retValue = 0.0;
		
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				try{
					retValue +=(Double.parseDouble(searchMetricsByExactName("pkts_out",ip).getValue()));
		    	}catch(Exception ex){
		    	}
			}
		return retValue;
    	
    }
    public Double getReadLatency(Node e){
    	Double result = 0.0;
    	Double retValue = 0.0;
		int nb = 0;
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
			
				String val = searchMetricsByExactName("read_latency",ip).getValue();
				 if (!val.equalsIgnoreCase("nan")){			
					 if (!result.isNaN()){
				retValue+=result;
				nb++;
			}
				 }
			}
		return retValue/nb;
		
      	
    }
    public Double getWriteLatency(Node e){Double result = 0.0;
	try{
		Double retValue = 0.0;
		int nb = 0;
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				 try{
					 String val = searchMetricsByExactName("write_latency",ip).getValue();
					 if (!val.equalsIgnoreCase("nan")){
					 result=(Double.parseDouble(val));
					if (!result.isNaN()){	
					   retValue+=result;
						nb++;
					}}
					}catch(Exception ex){
						
					//	RuntimeLogger.logger.info("Metric write_latency not found for ip "+ip);
						
					}
				 
			}
		return retValue/nb;			
		}catch (Exception ex){
			return 0.0;
		}
	
   
    }
    public Double getReadCount(Node e){
    	try{
    		Double retValue = 0.0;
    		int nb=0;
    		for (String ip:e.getAssociatedIps())
    			if(ip.split("\\.")[0].length()==2){
    				try{
    					retValue += (Double.parseDouble(searchMetricsByExactName("read_count",ip).getValue()));
    	    			nb++;
    		    	}catch(Exception ex){
    		    		
    		    //		RuntimeLogger.logger.info("Metric read_count not found for ip "+ip);
    		    		
    		    	}
    			}
    		return retValue/nb;	
    	}catch (Exception ex){
    		return 0.0;
    	}
    	
    
    }
    public Double getWriteCount(Node e){
    	
    	Double retValue = 0.0;
		int nb=0;
		for (String ip:e.getAssociatedIps())
			if(ip.split("\\.")[0].length()==2){
				try{
					retValue += ( Double.parseDouble(searchMetricsByExactName("write_count",ip).getValue()));
					nb++;

				}catch(Exception ex){
					
				}
			}
		return retValue/nb;		
		
    	
    }
    
    
 public  GangliaMetricInfo  searchMetricsByExactName(String name,String ip) {
	 Collection<GangliaMetricInfo>metrics =null;

		 if (clusterInfo==null) RuntimeLogger.logger.error("Cluster Info is null");
	 if (clusterInfo.searchHostsByExactIP(ip)!=null)	 
	 metrics = clusterInfo.searchHostsByExactIP(ip).searchMetricsByName(name);
	 else{
		 RuntimeLogger.logger.error("For the ip "+ip +" this thing can't be found!!!!");
	 }
	 GangliaMetricInfo myNewMetric = null;
        for (GangliaMetricInfo metricInfo : metrics) {
            if (metricInfo.getName().contains(name)) {
            	if (myNewMetric!=null){
           		
            		if (((Double.parseDouble(myNewMetric.getValue()))<=0.0f && Double.parseDouble(metricInfo.getValue())>0)){
            			myNewMetric=metricInfo;
            		}
            	}else{
            		myNewMetric = metricInfo;
            	}
            }
        }
        
        return myNewMetric;

    }

 public Double getCostPerHour(Node e)  {

		try{
			
		int nb=0;
		for (String ip:e.getAssociatedIps()){
			if(ip.split("\\.")[0].length()==2)
				nb++;
		}
		
		
		RuntimeLogger.logger.info("Current cost cost per hour for entity "+e.getId()+" is "+(50*nb));

		return 50.0*nb;
		}catch(Exception ex){
			return 0.0;
		}
	}
	
	public Node getControlledService() {
		return controlledService;
	}
	public void setControlledService(Node controlledService) {
		this.controlledService = controlledService;
	}


	
	public List<String> getAvailableMetrics(Node node) {
		List<String> avMetrics = new ArrayList<String>();
		
		return avMetrics;
	}
	public Double getMetricValue(String metricName, Node e) {
		
		switch (metricName.toLowerCase()){
		case "cpuspeed":
			return getCPUSpeed(e);
		case "memoryusage":
			return getMemoryUsage(e);
		default:
			return 0.0;
		}
	}
	public void notifyControlActionStarted(){
		setExecutingControlAction(true);
	}
	public void notifyControlActionEnded(){
		setExecutingControlAction(false);
	}
	public boolean isExecutingControlAction() {
		return isExecutingControlAction;
	}
	public void setExecutingControlAction(boolean isExecutingControlAction) {
		this.isExecutingControlAction = isExecutingControlAction;
	}

	
	@Override
	public void submitServiceConfiguration(Node node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyControlActionStarted(String actionName, Node node) {
		isExecutingControlAction=true;
		
	}
	@Override
	public void notifyControlActionEnded(String actionName, Node node) {
		isExecutingControlAction=false;
		
	}
	@Override
	public Double getNumberInstances(Node node) {
		// TODO Auto-generated method stub
		return 0.0;
	}
	@Override
	public void refreshServiceStructure(Node node) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<String> getOngoingActionID() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getOngoingActionNodeID() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void submitCompositionRules(String composition) {
		// TODO Auto-generated method stub
		
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

	

	
}
