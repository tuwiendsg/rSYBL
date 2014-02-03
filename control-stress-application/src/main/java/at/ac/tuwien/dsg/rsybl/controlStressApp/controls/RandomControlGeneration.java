package at.ac.tuwien.dsg.rsybl.controlStressApp.controls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;

import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPI;

import com.extl.jade.user.Nic;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;



public class RandomControlGeneration implements Runnable{
	private Thread generateStress ;
	private Session session;
	private FlexiantActions  flexiantActions = new FlexiantActions();
	private List<String> elasticityCapabilities =new ArrayList<String>();
	private Node cloudService;
	Random actionGenerator = new Random();
	Random sleepGenerator = new Random();
	private MonitoringAPI monitoringAPI;
	private DependencyGraph dependencyGraph;
	public RandomControlGeneration(DependencyGraph dependencyGraph){
		this.dependencyGraph=dependencyGraph;
		cloudService=dependencyGraph.getCloudService();
		generateStress=new Thread(this);
		monitoringAPI = new MonitoringAPI();
		monitoringAPI.setControlledService(cloudService);
	
		for (Node node:dependencyGraph.getAllServiceUnits()){
			for (ElasticityCapability capability:node.getElasticityCapabilities())
			elasticityCapabilities.add(node.getId()+"_"+ capability.getName());
			MonitoringThread monitoringThread = new MonitoringThread(node, monitoringAPI);
			monitoringThread.start();
		}
		for (Node node:dependencyGraph.getAllServiceTopologies()){
			MonitoringThread monitoringThread = new MonitoringThread(node, monitoringAPI);
			monitoringThread.start();
		}
		MonitoringThread monitoringThread = new MonitoringThread(cloudService, monitoringAPI);
		monitoringThread.start();
		generateStress.start();
	}

	@Override
	public void run() {
		while (true){
			int randomAction = actionGenerator.nextInt(elasticityCapabilities.size());
			Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.INFO,"Randomly generated action = "+randomAction+" "+elasticityCapabilities.get(randomAction));
		String elString=elasticityCapabilities.get(randomAction);
		Node currentNode = dependencyGraph.getNodeWithID(elString.substring(0,elString.indexOf('_')));
		for (ElasticityCapability elCap:currentNode.getElasticityCapabilities()){
			if (elCap.getName().equalsIgnoreCase(elString.substring(elString.indexOf('_')+1))){
				Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.INFO,"Elasticity Capability "+elCap.getName()+" with script "+elCap.getScript());
				if (elCap.getName().equalsIgnoreCase("scalein")){
					monitoringAPI.enforcingActionStarted(elString,currentNode);
					scaleIn(currentNode,elCap.getScript());
					monitoringAPI.enforcingActionEnded(elString, currentNode);
				}else{
					monitoringAPI.enforcingActionStarted(elString,currentNode);
					scaleOut(currentNode,elCap.getScript());
					monitoringAPI.enforcingActionEnded(elString,currentNode);
				}
				break;
			}
		}
	
		try {
			int sleepPeriod=sleepGenerator.nextInt(Configuration.getMaxIntervalGeneration()-Configuration.getMinIntervalGeneration())+Configuration.getMinIntervalGeneration();
			Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.INFO,"Sleeping for "+sleepPeriod+" ... ");
			Thread.sleep(sleepPeriod);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
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
	    
	 public static class MyUserInfo implements UserInfo{
		   
			public String getPassphrase() {
				// TODO Auto-generated method stub
				return null;
			}

			public String getPassword() {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean promptPassphrase(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean promptPassword(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			public boolean promptYesNo(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			public void showMessage(String arg0) {
				// TODO Auto-generated method stub
				
			}
			   
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
	       
	        

	       
	          channel.disconnect();

	     }
	  
	public void scaleIn(Node toBeScaled, String scriptToRun) {	
		 if (toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.VIRTUAL_MACHINE).size()>1){
			 Node toBeRemoved = toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
			 
		        	   String cmd = "";
		        	   String ip=toBeRemoved.getId();
		        	   String uuid = toBeRemoved.getStaticInformation().get("UUID").toString();
		        	   
		        	   	if (scriptToRun!=""){
		        	   		
		        		   cmd = scriptToRun+" "+ip;
		        			               
		            	   try {
							Process p = Runtime.getRuntime().exec(cmd);
							int exitVal = p.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							try {
								executeAndExpectNothing(ip, Configuration.getCertificatePath(), scriptToRun);
							} catch (JSchException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							try {
								executeAndExpectNothing(ip, Configuration.getCertificatePath(), scriptToRun);
							} catch (JSchException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							e.printStackTrace();
						}
		               }
		        	   flexiantActions.removeServer(uuid);
		               try {
		   				Thread.sleep(70000);
		   			} catch (InterruptedException e) {
		   				// TODO Auto-generated catch block
		   				e.printStackTrace();
		   			}
		               
		               toBeScaled.removeNode(toBeRemoved);
	               
		        	

		        
		 }
			monitoringAPI.refreshServiceStructure(cloudService);

		        	 
		 }
	
	public void scaleOut(Node node,String scriptToRun){
		//TODO: return ip and uuid
		String uuid=flexiantActions.createNewServer(node.getId()+node.getStaticInformation().get("DefaultImage"),""+node.getStaticInformation("DefaultImage"), 2, 2);
		//TODO:
		List<com.extl.jade.user.Server> servers=flexiantActions.listServers();
		String ip="";
		for (com.extl.jade.user.Server server:servers){
			if (server.getResourceUUID().equalsIgnoreCase(uuid) && server.getNics()!=null && server.getNics().size()>0)
				for (Nic nic:server.getNics())
					if (nic.getIpAddresses()!=null && nic.getIpAddresses().size()>0){
					ip=server.getNics().get(0).getIpAddresses().get(0).getIpAddress();
					break;
					}
			
		}
		if (!ip.equalsIgnoreCase("err")){
			Node newNode = new Node();
			newNode.setId(ip);
            newNode.getStaticInformation().put("UUID", uuid);
			newNode.getStaticInformation().put("IP",ip);
			newNode.setNodeType(NodeType.VIRTUAL_MACHINE);
            Relationship rel = new Relationship();
            rel.setSourceElement(node.getId());
            rel.setTargetElement(newNode.getId());
            rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
            
            node.addNode(newNode,rel);
            
            String cmd = "";
     	    ip=newNode.getId();
     	    cmd = scriptToRun+" "+ip;
         	   try {
					Process p = Runtime.getRuntime().exec(cmd);
					int exitVal = p.waitFor();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					try {
						executeAndExpectNothing(ip, Configuration.getCertificatePath(), scriptToRun);
					} catch (JSchException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
					
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					try {
						executeAndExpectNothing(ip, Configuration.getCertificatePath(), scriptToRun);
					} catch (JSchException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
		}
            
		monitoringAPI.refreshServiceStructure(cloudService);
	}
//	 private static class MELA_ClientUtils {
//
//	        //works as side effect
//	        public static void convertServiceTopology(MonitoredElement serviceElement, Node cloudService) {
//	            //RuntimeLogger.logger.info("Related nodes for node "+ cloudService +" are "+ cloudService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP));
//	            List<Node> serviceTopologies = new ArrayList<Node>();
//	            		
//	            		serviceTopologies.addAll(cloudService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP));
//	            while (!serviceTopologies.isEmpty()){
//	                MonitoredElement serviceTopologyElement = new MonitoredElement();
//
//	            Node serviceTopology = serviceTopologies.get(0);
//	            serviceTopologyElement.setId(serviceTopology.getId());
//	            serviceTopologyElement.setLevel(MonitoredElement.MonitoredElementLevel.SERVICE_TOPOLOGY);
//
//	            if (serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
//
//	                for (Node serviceUnit : serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
//	                    if (serviceUnit.getNodeType() == NodeType.SERVICE_UNIT) {
//	                        MonitoredElement serviceUnitElement = new MonitoredElement();
//	                        serviceUnitElement.setId(serviceUnit.getId());
//	                        serviceUnitElement.setLevel(MonitoredElement.MonitoredElementLevel.SERVICE_UNIT);
//	                        for (Node vm: serviceUnit.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.VIRTUAL_MACHINE)){
//	                        	// RuntimeLogger.logger.info("Translating hosted on "+vm.getId()+" for node "+serviceUnitElement.getId());
//	                        	 MonitoredElement virtualMachine = new MonitoredElement();
//	                        	 virtualMachine.setId(vm.getId());
//	                        	 virtualMachine.setLevel(MonitoredElement.MonitoredElementLevel.VM);
//	                        	 serviceUnitElement.addElement(virtualMachine);
//	                        }
//	                        for (Node vm: serviceUnit.getAllRelatedNodesOfType(RelationshipType.ASSOCIATED_AT_RUNTIME_RELATIONSHIP,NodeType.VIRTUAL_MACHINE)){
//	                        	 MonitoredElement virtualMachine = new MonitoredElement();
//		                       	 virtualMachine.setId(vm.getId());
//		                       	 boolean alreadyContained=false;
//		                       	 virtualMachine.setLevel(MonitoredElement.MonitoredElementLevel.VM);
//		                       	 for (MonitoredElement el:serviceUnitElement.getContainedElements())
//		                       	 { 
//		                       		 if (el.getId().equalsIgnoreCase(vm.getId())){
//		                       			alreadyContained=true;
//		                       		 }
//		                       	 }
//		                       	 if (!alreadyContained)
//		                       		 serviceUnitElement.addElement(virtualMachine);
//	                        }
//	                        serviceTopologyElement.addElement(serviceUnitElement);
//
//	                    }
//	                }
//	            }
//
//	            serviceElement.addElement(serviceTopologyElement);
//
//	            if (serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
//	                for (Node subTopology : serviceTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
//	                    if (subTopology.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
//	                    	serviceTopologies.add(subTopology);
//	                    }
//	                }
//	            }
//	            serviceTopologies.remove(0);
//	            }
//	            
//	        }
//
//	        public static MonitoredElement.MonitoredElementLevel getElementLevelFromEntity(Node entity) {
//	            if (entity.getNodeType() == NodeType.CLOUD_SERVICE) {
//	                return MonitoredElement.MonitoredElementLevel.SERVICE;
//	            } else if (entity.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
//	                return MonitoredElement.MonitoredElementLevel.SERVICE_TOPOLOGY;
//	            } else if (entity.getNodeType() == NodeType.SERVICE_UNIT) {
//	                return MonitoredElement.MonitoredElementLevel.SERVICE_UNIT;
//	            } else {
//	                Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.SEVERE, "Error. Cannot determine the source class of entity " + entity);
//	                return null;
//	            }
//	        }
//	    }

	
//	private void notifyMELAOnUpdate(Node controlService){
		
//        MonitoredElement element = new MonitoredElement();
//        element.setId(cloudService.getId());
//        element.setLevel(MonitoredElement.MonitoredElementLevel.SERVICE);
//
//        MELA_ClientUtils.convertServiceTopology(element, cloudService);
//
//        URL url = null;
//        HttpURLConnection connection = null;
//        boolean notConnected = true;
//        while (notConnected){
//        try {
//           url = new URL(REST_API_URL + "/servicedescription");
//            connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setInstanceFollowRedirects(false);
//            connection.setRequestMethod("PUT");
//            connection.setRequestProperty("Content-Type", "application/xml");
//            connection.setRequestProperty("Accept", "application/json");
//
//            //write message body
//            OutputStream os = connection.getOutputStream();
//            JAXBContext jaxbContext = JAXBContext.newInstance(MonitoredElement.class);
//            jaxbContext.createMarshaller().marshal(element, os);
//            os.flush();
//            os.close();
//
//            InputStream errorStream = connection.getErrorStream();
//            if (errorStream != null) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.SEVERE, line);
//                }
//            }
//
//            InputStream inputStream = connection.getInputStream();
//            if (inputStream != null) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.SEVERE, line);
//                }
//            }
//            
//            notConnected=false;
//        } catch (Exception e) {
//        	Logger.getLogger(RandomControlGeneration.class.getName()).log(Level.WARNING, "Trying to connect to MELA - failing ... . Retrying later");
//        	try {
//				Thread.sleep( 1000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//        	} finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//	}
//	}
}
