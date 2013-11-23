package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.celar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;

public class EnforcementPluginCELAR implements EnforcementInterface {
	private MonitoringAPIInterface monitoringAPI;
	private Node cloudService;
	boolean cleanupGoingOn =false;
	boolean cleanupNecessary = true;
	public static String API_URL="http://localhost:8080/celar-orchestrator/deployment/";
	
	public EnforcementPluginCELAR(Node cloudService){
		this.cloudService=cloudService;
		API_URL=Configuration.getEnforcementServiceURL();
		
	}
	public static String executeResizingCommand(String actionType){
		String ip = "";
		 URL url = null;
	        HttpURLConnection connection = null;
	        try {
	            url = new URL(API_URL+"resize/?action="+ actionType);
	       

	            InputStream is = url.openStream();
	            try {
	              BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

	              StringBuilder sb = new StringBuilder();


	              String cp = new String();

	              while((cp=rd.readLine())!=null){

	                  sb.append(cp);
	              }
	               
	              String jsonText = sb.toString();

	              JSONObject array = new JSONObject(jsonText);
	              if (array.getJSONObject("1").getString("stderr").equalsIgnoreCase("")){
	              if ((array.getJSONObject("1").getString("stdout")).contains("Removing:"))
	              {
	            	  String strs[]=(array.getJSONObject("1").getString("stdout")).split("Removing: ");
	            	

	            	  return strs[strs.length-1];
	              }
	            	  else{
	            		  if((array.getJSONObject("1").getString("stdout")).contains("Adding: ")){
	            			  String strs[]=(array.getJSONObject("1").getString("stdout")).split("Adding: ");
	            			//  System.out.println(strs[strs.length-1].split("xss")[0]);
	    	            	 // System.out.println(strs[strs.length-1].split("xss")[1]);
	    	            	  return strs[strs.length-1].split("xss")[0];  
	            		  }
	            	  }
	              if (array.getJSONObject("1").getString("stdout").charAt(0)>='0'&&array.getJSONObject("1").getString("stdout").charAt(0)<='9'&& !array.getJSONObject("1").getString("stdout").contains(" "))
	            		  return array.getJSONObject("1").getString("stdout");
	              else
	            	  return "";
	              }else
	              {
	            	  RuntimeLogger.logger.error(array.getJSONObject("1").getString("stderr"));
	    	       return "";
	              }
	            }catch(Exception e){
	            	
		        	//Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Failing the action "+actionType);
		        	RuntimeLogger.logger.error("Failing the action "+actionType+" with error "+e.getMessage());
		        
	            }
	        } catch (Exception e) {
	           // Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE, e.getMessage(), e);
	        	e.printStackTrace();
	        	Logger.getLogger(EnforcementPluginCELAR.class.getName()).log(Level.WARNING, "Trying to connect to the Orchestrator - failing ... . Retrying later");
	        	RuntimeLogger.logger.error("Failing to connect to Orchestrator");
	        	
	        } finally {
	            if (connection != null) {
	                connection.disconnect();
	            }
	        }
	        return "";
//		try{
//		Process p = Runtime.getRuntime().exec(command);                                                                                                                                                     
//		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//		String s ="";
//		while ((s = stdInput.readLine()) != null) {
//	        if (s.contains("Adding")){
//	        	String[] x=s.split("[ :]");
//	        	if (x.length>=2)
//	        	if (x[1].charAt(0)>='0'&&x[1].charAt(0)<='9'){
//	        		ip=x[1];
//	        	}
//	        }
//	        RuntimeLogger.logger.info("From scaling command " +s);
//		}
//		
//		if (ip.length()>0 && ip.charAt(0)>='0'&&ip.charAt(0)<='9'){
//			return ip;}
//		else
//		{
//			RuntimeLogger.logger.info("Answer from scale command "+ip+" ");
//			return "";
//		}
//		}catch(Exception e ){
//			RuntimeLogger.logger.info("Answer from scale command "+ip+" "+e.getMessage());
//			return "";
//		}
		
	}

	public static void main(String[] args){
		String ip=executeResizingCommand("addvm");
		if (!ip.equalsIgnoreCase("")){
			System.err.println(ip);
		}else{
			System.err.println("IP is empty "+ip);
		}

		//System.err.println(executeCommand("removevm"));
	}
	public void cleanup(){
		 URL url = null;
	        try {
	            url = new URL(API_URL+"resize/?action=cleanup");            
	            InputStream is = url.openStream();
	               BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

	              StringBuilder sb = new StringBuilder();


	              String cp = new String();
	              String s = "";
	              while((cp=rd.readLine())!=null){

	                  sb.append(cp);
	                  s+=cp;
	              }
	              RuntimeLogger.logger.info("Cleanup returning "+s);
	              while (!checkStatus("","cleanup")){
	      			try {
	      				Thread.sleep(10000);
	      			} catch (InterruptedException e) {
	      				// TODO Auto-generated catch block
	      				e.printStackTrace();
	      			}
	      		}
	        }catch(Exception e ){
	        	RuntimeLogger.logger.error("Error at cleanup"+e.getMessage()+" "+e.getCause());
	        }
	}
	public boolean checkStatus(String ip, String action){
		 URL url = null;
	        HttpURLConnection connection = null;
	        try {
	        	
	        	if (!ip.equalsIgnoreCase(""))
	            url = new URL(API_URL+"resizestatus/?action="+ action+"&ip="+ip);
	        	else{
		            url = new URL(API_URL+"resizestatus/?action="+ action);
		              RuntimeLogger.logger.info("Check cleanup status " +url);

	        	}

	            InputStream is = url.openStream();
	               BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

	              StringBuilder sb = new StringBuilder();


	              String cp = new String();
	              String s = "";
	              while((cp=rd.readLine())!=null){

	                  sb.append(cp);
	                  s+=cp;
	              }
	              RuntimeLogger.logger.info("STATUS:" +sb);
	          
	             if (s.contains("true"))return true;
	             else return false;
	            }catch(Exception e){
	            	RuntimeLogger.logger.error("Error when checking for status of "+ip+" with action "+action);
	            	return false;
	            }
	              
	              
	}
	@Override
	public void scaleOut(Node toBeScaled) {
		while (cleanupGoingOn){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String ip = executeResizingCommand("addvm");
		if (!ip.equalsIgnoreCase("")){
			monitoringAPI.scaleoutstarted(toBeScaled);
			RuntimeLogger.logger.info("The IP of the Virtual Machine to be ADDED is "+ip);	

			while (!checkStatus(ip,"addvm")){
			try {
				RuntimeLogger.logger.info("Waiting for scale out...");
				Thread.sleep(10000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		}
		if (!ip.equalsIgnoreCase("")){
			RuntimeLogger.logger.info("The IP of the Virtual Machine to be ADDED is "+ip);	
		DependencyGraph dependencyGraph=new DependencyGraph();
		dependencyGraph.setCloudService(cloudService);
			Node toAdd = dependencyGraph.getNodeWithID(toBeScaled.getId());
		Node newVM = new Node();
		newVM.setNodeType(NodeType.VIRTUAL_MACHINE);
		Relationship rel = new Relationship();
		rel.setSourceElement(toBeScaled.getId());
		rel.setTargetElement(ip);
		rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
		newVM.setId(ip);
		toAdd.addNode(newVM,rel);
		RuntimeLogger.logger.info("Cloud new service is "+dependencyGraph.graphToString());
		monitoringAPI.scaleoutended(toBeScaled);

		monitoringAPI.refreshServiceStructure(cloudService);
		cleanupNecessary=true;
}else{
	RuntimeLogger.logger.error("IP is empty "+ip);
}
	}

	@Override
	public void scaleIn(Node toBeScaled) {
		while (cleanupGoingOn){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		DependencyGraph d = new DependencyGraph();
		d.setCloudService(cloudService);
		DependencyGraph dep = new DependencyGraph();
		dep.setCloudService(cloudService);
		toBeScaled=dep.getNodeWithID(toBeScaled.getId());
		if (d.getNodeWithID(toBeScaled.getId()).getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.VIRTUAL_MACHINE).size()>4){
	    RuntimeLogger.logger.info("Executing scaling action, number of VMs available for node "+ toBeScaled.getId()+" "+d.getNodeWithID(toBeScaled.getId()).getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.VIRTUAL_MACHINE).size());
				
		String ip = executeResizingCommand("removevm");
		if(!ip.equalsIgnoreCase("")){
			monitoringAPI.scaleinstarted(toBeScaled);
		while (!checkStatus(ip,"removevm")){
			try {
				RuntimeLogger.logger.info("Waiting for scale in...");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
			RuntimeLogger.logger.info("The IP of the Virtual Machine to be REMOVED is "+ip);	
			try{
			Node toBeDel = dep.getNodeWithID(ip); 
			toBeScaled.removeNode(toBeDel);
			RuntimeLogger.logger.info("Cloud new service is "+dep.graphToString());
			monitoringAPI.scaleinended(toBeScaled);
			monitoringAPI.refreshServiceStructure(cloudService);
			cleanupNecessary=true;
			}catch(Exception e){
				RuntimeLogger.logger.info("Failed to remove node "+ip);
			}
		}
//		}else{
//			if (cleanupNecessary)
//			{
//				cleanup();
//				cleanupNecessary=false;
//			}
//			
		}
	}

	@Override
	public List<String> getElasticityCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enforceAction(String actionName, Node entity) {
	if (actionName.equalsIgnoreCase("cleanup")){
		cleanupGoingOn=true;
		cleanup();
		cleanupGoingOn=false;
	}
		
	}

	@Override
	public void setControlledService(Node controlledService) {
		cloudService=controlledService;
		
	}

	@Override
	public Node getControlledService() {
		return cloudService;
	}

	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
		monitoringAPI=monitoring;
	}

}
