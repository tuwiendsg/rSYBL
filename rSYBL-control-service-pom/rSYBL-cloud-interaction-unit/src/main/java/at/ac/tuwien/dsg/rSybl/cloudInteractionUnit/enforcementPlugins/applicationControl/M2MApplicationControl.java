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

package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.applicationControl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.gangliaMonitoring.GangliaMonitor.MyUserInfo;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class M2MApplicationControl implements EnforcementInterface{
	private Node controlledService;
	public M2MApplicationControl(Node cloudService){
		controlledService=cloudService;
	}
	public boolean decommission(String nodeId, String ip, Node controlledService){
		DependencyGraph dependencyGraph=new DependencyGraph();
		dependencyGraph.setCloudService(controlledService);
		
		String cmd="";
		
		if (nodeId.contains("EventProcessing")){
			String ip1="";
			for (Node node:dependencyGraph.getAllServiceUnits()){
				if (node.getId().contains("Load")){
                                    if (node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.ARTIFACT)!=null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.ARTIFACT).size()>0){
                                        Node artifact = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.ARTIFACT).get(0);
                                        ip1 = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0).getId();
                                    }else
					ip1=node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0).getId();
				}
			}         
	   		   cmd = "decomissionWS " + ip1+" "+ip ;
		}
	   	   else{
	   		String ip1="";
			for (Node node:dependencyGraph.getAllServiceUnits()){
				if (node.getId().contains("Controller")){
                                     if (node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.ARTIFACT)!=null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.ARTIFACT).size()>0){
                                        Node artifact = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.ARTIFACT).get(0);
					ip1=artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0).getId();
                                     }else{
                                        ip1=node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0).getId(); 
                                     }
                                     }
			}    
	   	   
	   		   cmd = "decomissionCassandra "+ip1+" "+ip;
	   	   }				
		RuntimeLogger.logger.info("~~~~~~~~~~~~~~~Appl level enforcement~~~~~~~~ Enforcing command "+cmd);
	          if (!(controlledService.getStaticInformation("AccessIP").equals("localhost")))
	      	try {
	      		 executeAndExpectNothing((String)controlledService.getStaticInformation("AccessIP"), Configuration.getCertificatePath(), cmd);
	      	} catch (JSchException e1) {
	      		// TODO Auto-generated catch block
	      		e1.printStackTrace();
	      		return false;
	      	}
	          else
	          {
	       	   try {
					Process p = Runtime.getRuntime().exec(cmd);
					int exitVal = p.waitFor();
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
	          }
	          return true;
	}
	public boolean decommissionWS( Node node,String commandParameters){
		
		
		String cmd="";

	   		   cmd = "decomissionWS " + commandParameters ;
		RuntimeLogger.logger.info("~~~~~~~~~~~~~~~Appl level enforcement~~~~~~~~ Enforcing command "+cmd);
	          if (!(controlledService.getStaticInformation("AccessIP").equals("localhost")))
	      	try {
	      		 executeAndExpectNothing((String)controlledService.getStaticInformation("AccessIP"), Configuration.getCertificatePath(), cmd);
	      	} catch (JSchException e1) {
	      		// TODO Auto-generated catch block
	      		RuntimeLogger.logger.info("Failed to enforce decomission ws on "+node.getId());
	      		return false;
	      	}
	          else
	          {
	       	   try {
					Process p = Runtime.getRuntime().exec(cmd);
					int exitVal = p.waitFor();
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					RuntimeLogger.logger.info("Failed to enforce decomissionWS on "+node.getId());
		      		return false;
				}
	          }
	          return true;
	          
	}
	public boolean decommissionNode( Node node,String cmdParameters) {
		DependencyGraph dependencyGraph=new DependencyGraph();
		dependencyGraph.setCloudService(controlledService);
		
		String cmd="";
    
	   		   cmd = "decomissionNode " + cmdParameters;
		RuntimeLogger.logger.info("~~~~~~~~~~~~~~~Appl level enforcement~~~~~~~~ Enforcing command "+cmd);
	          if (!(controlledService.getStaticInformation("AccessIP").equals("localhost")))
				try {
					executeAndExpectNothing((String)controlledService.getStaticInformation("AccessIP"), Configuration.getCertificatePath(), cmd);
				} catch (JSchException e) {
			
					RuntimeLogger.logger.info("Failed to enforce decomission ws on "+node.getId());
		      		return false;
				}
			else
	          {
				try {
					Process p = Runtime.getRuntime().exec(cmd);
					
						int exitVal = p.waitFor();
					} catch (InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						RuntimeLogger.logger.info("Failed to enforce decomission ws on "+node.getId());
			      		return false;
					}
				
	          }
	          return true;
	          
	}
	 private byte[] readFile (String file) throws IOException {
	        // Open file
	    	InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
	    	

	        try {
	            // Get and check length
	        	ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	        	int nRead;
	        	byte[] data = new byte[16384];

	        	while ((nRead = is.read(data, 0, data.length)) != -1) {
	        	  buffer.write(data, 0, nRead);
	        	}

	        	buffer.flush();
	            return buffer.toByteArray();
	        }
	        finally {
	            is.close();
	        }
	    }
	    
	 Session session;
	    private String execute(String rootIPAddress, String securityCertificatePath,String command) throws JSchException {
	        if (session==null){
	     	   JSch jSch = new JSch();
	    		
	            byte[] prvkey=null;
	    		try {
	    			prvkey = readFile(securityCertificatePath);
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		} // Private key must be byte array
	            final byte[] emptyPassPhrase = new byte[0]; // Empty passphrase for now, get real passphrase from MyUserInfo

	            jSch.addIdentity(
	                "ubuntu",    // String userName
	                prvkey,          // byte[] privateKey 
	                null,            // byte[] publicKey //maybe generate a public key and try with it
	                emptyPassPhrase  // byte[] passPhrase
	            );
	            
	             session = jSch.getSession("ubuntu", rootIPAddress, 22);
	            session.setConfig("StrictHostKeyChecking", "no"); //         Session session = jSch.getSession("ubuntu", rootIPAddress, 22);

	            UserInfo ui = new MyUserInfo(); // MyUserInfo implements UserInfo
	            session.setUserInfo(ui);
	            session.connect();
	        }
	         ChannelExec channel=(ChannelExec) session.openChannel("exec");
	         ((ChannelExec)channel).setCommand(command);
	          channel.connect();
	         InputStream stdout =null;
	 		try {
	 			stdout = channel.getInputStream();
	 		} catch (IOException e2) {
	 			// TODO Auto-generated catch block
	 			e2.printStackTrace();
	 		}
	       
	        
	 		BufferedReader reader =  new BufferedReader(new InputStreamReader(stdout));
	 		String line = null;
	 		String content = "";
	       
	          try {
	 			while ((line = reader.readLine()) != null) {
	 			      //if ganglia does not respond
	 			      if (line.contains("Unable to connect")) {
	 			    	 Logger.getLogger(this.getClass().getName()).log(Level.WARNING,  "" + rootIPAddress + " does not respond to monitoring request");
	 			          return null;
	 			      }
	 			      if (line.contains("<") || line.endsWith("]>")) {
	 			          content += line + "\n";
	 			      }
	 			  }
	 		} catch (IOException e1) {
	 			// TODO Auto-generated catch block
	 			e1.printStackTrace();
	 		}
	          channel.disconnect();
	          return content;

	     }
	private void executeAndExpectNothing(String rootIPAddress, String securityCertificatePath,String command) throws JSchException {
        if (session==null){
     	   JSch jSch = new JSch();
    		
            byte[] prvkey=null;
    		try {
    			prvkey = readFile(securityCertificatePath);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} // Private key must be byte array
            final byte[] emptyPassPhrase = new byte[0]; // Empty passphrase for now, get real passphrase from MyUserInfo

            jSch.addIdentity(
                "ubuntu",    // String userName
                prvkey,          // byte[] privateKey 
                null,            // byte[] publicKey //maybe generate a public key and try with it
                emptyPassPhrase  // byte[] passPhrase
            );
            
             session = jSch.getSession("ubuntu", rootIPAddress, 22);
            session.setConfig("StrictHostKeyChecking", "no"); //         Session session = jSch.getSession("ubuntu", rootIPAddress, 22);

            UserInfo ui = new MyUserInfo(); // MyUserInfo implements UserInfo
            session.setUserInfo(ui);
            session.connect();
        }
         ChannelExec channel=(ChannelExec) session.openChannel("exec");
         ((ChannelExec)channel).setCommand(command);
          channel.connect();
          
         InputStream stdout =null;
 		try {
 			stdout = channel.getInputStream();
 		} catch (IOException e2) {
 			// TODO Auto-generated catch block
 			e2.printStackTrace();
 		}
       
        
 		BufferedReader reader =  new BufferedReader(new InputStreamReader(stdout));
 		String line = null;
 		String content = "";
       
          channel.disconnect();

     }

	public List<String> getElasticityCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean enforceAction(String actionName, Node entity) {
		// TODO Auto-generated method stub
		return false;
	}
	public void setControlledService(Node controlledService) {
		// TODO Auto-generated method stub
		
	}
	public Node getControlledService() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
		// TODO Auto-generated method stub
		
	}
	public boolean containsElasticityCapability(Node entity, String capability) {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean scaleOut(Node toBeScaled) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public boolean scaleIn(Node toBeScaled) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undeployService(Node serviceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

   
    @Override
    public boolean scaleOut(Node toBeScaled, double violationDegree) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean scaleIn(Node toBeScaled, double violationDegree) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public  boolean enforceAction(Node serviceID, String actionName) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    
    }
}
