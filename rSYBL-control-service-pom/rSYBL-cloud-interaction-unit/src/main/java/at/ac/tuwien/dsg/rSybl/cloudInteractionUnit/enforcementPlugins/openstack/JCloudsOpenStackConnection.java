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


package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.openstack;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.gangliaMonitoring.GangliaMonitor.MyUserInfo;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/*  Georgiana Copil
 *  Vienna University of Technology
 *          ==============================
 */

public class JCloudsOpenStackConnection {

	NovaApi client;
	Node controlledService;
	private ServerApi serverApi;


	public JCloudsOpenStackConnection(Node controlledService){
		this.controlledService = controlledService;

		Iterable<Module> modules = ImmutableSet.<Module>of(
                new SLF4JLoggingModule());
		
		RuntimeLogger.logger.info(Configuration.getCloudAPIType()+" "+Configuration.getCloudUser()+" "+Configuration.getCloudPassword()+" "+Configuration.getCloudAPIEndpoint());
		ComputeServiceContext context = ContextBuilder.newBuilder(Configuration.getCloudAPIType())
                .credentials(Configuration.getCloudUser(), Configuration.getCloudPassword())
                .endpoint(Configuration.getCloudAPIEndpoint())
                //.modules(modules)
                .buildView(ComputeServiceContext.class);


		
        ComputeService computeService = context.getComputeService();


        client = (NovaApi) context.unwrap(NovaApiMetadata.CONTEXT_TOKEN).getApi();


        final String region = "myregion";

        serverApi = client.getServerApiForZone(region);
        for (Resource flavor : client.getFlavorApiForZone(region).list().concat()) {
            RuntimeLogger.logger.error( flavor.getId()+" "+flavor.getName());
          }
	}
	/**
	 * 
	 * @param newInstancesCount
	 *            increases nodes nr by newInstancesCount blocks until the newly
	 *            instantiated nodes are ACTIVE
	 */
	public void setControlledService(Node cloudService){
		this.controlledService = controlledService;
		Iterable<Module> modules = ImmutableSet.<Module>of(
                new SLF4JLoggingModule());
		ComputeServiceContext context = ContextBuilder.newBuilder(Configuration.getCloudAPIType())
                .credentials(Configuration.getCloudUser(), 
                		Configuration.getCloudPassword())
                .endpoint(Configuration.getCloudAPIEndpoint())
              //  .modules(modules)
                .buildView(ComputeServiceContext.class);


        ComputeService computeService = context.getComputeService();


        client = (NovaApi) context.unwrap(NovaApiMetadata.CONTEXT_TOKEN).getApi();


        final String region = "myregion";
        for (Resource flavor : client.getFlavorApiForZone(region).list().concat()) {
           RuntimeLogger.logger.error( flavor.getId()+" "+flavor.getName());
         }
        
        serverApi = client.getServerApiForZone(region);

	}
	public String scaleOutAndWaitUntilNewServerBoots(Node entity,Node controller){
		 try{  
        CreateServerOptions createNodeOptions = new CreateServerOptions();
        Map<String, String> nodeMetaData = new HashMap<String, String>();
        
        String metadata ="";
        if (entity.getId().equalsIgnoreCase("DataNodeServiceUnit"))metadata= "CASSANDRA_SEED_IP=10.99.0.44 \n CASSANDRA_RPC_PORT=9160 \n CASSANDRA_TCP_PORT=9161";
        else 
        	metadata="LOAD_BALANCER_IP=10.99.0.39 \n CASSANDRA_SEED_NODE_IP=10.99.0.44";
        nodeMetaData.put(metadata, "");
        createNodeOptions.metadata(nodeMetaData);
        createNodeOptions.userData(metadata.getBytes());
        
        createNodeOptions.keyPairName(Configuration.getCertificateName());
        String vmName = entity.getId();
       // RuntimeLogger.logger.info("FLAVOR ID "+(String)entity.getStaticInformation("DefaultImage")+" "+(String)entity.getStaticInformation("DefaultFlavor"));
        String flavorID = "";
        for (Resource flavor:client.getFlavorApiForZone("myregion").list().concat()){
    	//	RuntimeLogger.logger.error("Enumerating possible flavors Flavor "+flavor.getName()+" "+ flavor.getId());

        	if (((String)entity.getStaticInformation("DefaultFlavor")).equalsIgnoreCase(flavor.getName())){
       // 		RuntimeLogger.logger.info("Flavor found "+flavor.getId());
        		flavorID=flavor.getId();
        	}
        }
    //    RuntimeLogger.logger.info("Scaling out "+entity.getId()+" which has controller "+controller);
        
        boolean serverCorrectlyCreated = false;
        ServerCreated serverCreated = null;
        while (!serverCorrectlyCreated){
         serverCreated = serverApi.create(vmName,((String)entity.getStaticInformation("DefaultImage")), flavorID,createNodeOptions);
         
        //wait for all to become ACTIVE

            while(serverApi.get(serverCreated.getId()).getStatus() != Server.Status.ACTIVE){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (serverApi.get(serverCreated.getId()).getStatus() != Server.Status.ERROR){
            	RuntimeLogger.logger.info(serverApi.get(serverCreated.getId()).getStatus() );
            	serverCorrectlyCreated=true;
            }else{
            	serverApi.delete(serverCreated.getId());
            	RuntimeLogger.logger.error("Created with error new instance for "+entity.getId()+ " - deleting and retrying....");
            }
            
        }
            Server server = serverApi.get(serverCreated.getId());
        // SecureRandom random = new SecureRandom();  
        return server.getAddresses().get("private").iterator().next().getAddr();
        }catch(Exception e){
        	e.printStackTrace();
        	RuntimeLogger.logger.error("Error when scaling out "+e.getMessage()+e.toString());
        	return "ERR";
        }
		 
    }

	public void scaleOutCluster(Node controller, Node slave, int numberSlaves, String ipForControllerofHigherTopology,Node controlledSrevice){
        //create Cassandra MAIN NODE
		
		//scaleOutAndWaitUntilNewServerBoots (controller,controlledSrevice);
		//scaleOutAndWaitUntilNewServerBoots (slave,controlledSrevice);
/*
		this.controlledService = controlledSrevice;
        CreateServerOptions createServerOptions = new CreateServerOptions();
        
        createServerOptions.keyPairName(Configuration.getCertificateName());
        //add main node IP to metaData when creating. Node will get this IP and use it in cassandra config as SEED
       
        Map<String, String> nodeMetaData = new HashMap<String, String>();
        nodeMetaData.put(ipForControllerofHigherTopology, "");
        //createServerOptions.metadata(nodeMetaData);
        //add your key name
        nodeMetaData.put(ipForControllerofHigherTopology, "");
        createServerOptions.metadata(nodeMetaData);
        createServerOptions.userData(ipForControllerofHigherTopology.getBytes());
        
        ServerCreated serverCreated = serverApi.create(controller.getId(), controller.getDefaultImage(),controller.getGetFlavorID(), createServerOptions);
        
        //wait for main server to start
        RuntimeLogger.logger.info("Waiting for Controller to start ");
        while (serverApi.get(serverCreated.getId()).getStatus() != Server.Status.ACTIVE) {
            try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        //retrieve reference to the created server, since ServerCreated is not really usefull
        Server server = serverApi.get(serverCreated.getId());

        //get created server local IP to use in cassandra Nodes
        String privateIp = server.getAddresses().get("private").iterator().next().getAddr();
        serverApi.rename(server.getId(), controller.getId()+privateIp.split("\\.")[privateIp.split("\\.").length-1]);
        addRecursivelyIp(controller,privateIp);
        RuntimeLogger.logger.info("Cassandra main has Local IP " + privateIp);
        //just list to remember nodes
        List<String> createdNodesIDs = new ArrayList<String>();

        //wait for 30 seconds until central nodes completes bootstrap. Otherwise nodes might not joint the ring.
        //NOTE: 30 seconds is just a guess. it might need less. Or more. Currently i am leaning toward less.
        try {
			Thread.sleep(30000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        for (int i = 0; i < 1; i++) {
            //create Cassandra Node
            {
                CreateServerOptions createNodeOptions = new CreateServerOptions();
                //add main node IP to metaData when creating. Node will get this IP and use it in cassandra config as SEED
                nodeMetaData = new HashMap<String, String>();
                nodeMetaData.put(ipForControllerofHigherTopology, "");
                createNodeOptions.metadata(nodeMetaData);
                createNodeOptions.userData(ipForControllerofHigherTopology.getBytes());
                //add your key name
                createNodeOptions.keyPairName(Configuration.getCertificateName());  //your key name :P

                //create server
                ServerCreated node = serverApi.create(slave.getId()+privateIp.split("\\.")[privateIp.split("\\.").length-1], slave.getDefaultImage(), slave.getGetFlavorID(), createNodeOptions);
                createdNodesIDs.add(node.getId());
            }
        }

        //wait for all nodes to start

        for(String nodeID: createdNodesIDs){
            while (serverApi.get(nodeID).getStatus() != Server.Status.ACTIVE) {
                //sleep 10 seconds
                try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
         //   Date startDate = new Date();
         //   Date currentDate=new Date();
         //   System.out.println("For bla workload starting date is "+startDate.toString()+" end date "+currentDate.toString()+" elapsed time "+(currentDate.getTime()-startDate.getTime()));
          	  server = serverApi.get(nodeID);
                 //get created server local IP to use in cassandra Nodes
          	RuntimeLogger.logger.info("Adding new slave now i should add ips");
                  addRecursivelyIp(slave,server.getAddresses().get("private").iterator().next().getAddr());               
        }
     
*/
 
	}
	public void scaleInCluster(Node controller, Node slave,String ip, Node controlledService){
		//VERRY IMPORTANT. These are the cassandra MainNode and SimpleNode image IDs
       // String cassandraBlankNodeImageID = "be6b1aa4-2227-4f4d-9c63-7aabc9cc3a85";
       // String cassandraMainNodeImageID = "74c744c9-e981-44ce-a8d3-df12028aff37";	
		this.controlledService=controlledService;

        //create Cassandra MAIN NODE
        CreateServerOptions createServerOptions = new CreateServerOptions();
        createServerOptions.keyPairName(Configuration.getCertificateName());
        for (Server server:serverApi.listInDetail().concat()){
        	if (server.getName().equalsIgnoreCase(controller.getId()+ip.split("\\.")[ip.split("\\.").length-1])){
                serverApi.delete(server.getId());
                String controllerIp = server.getAddresses().get("private").iterator().next().getAddr();
				deleteRecursivelyIP(controllerIp);

				break;
        	}
        }
       
        
    	for (Server res : serverApi.listInDetail().concat()) {
			if (res.getName().contains(slave.getId()+ip.split("\\.")[ip.split("\\.").length-1])) {
					
					serverApi.delete(res.getId());
					String slaveIp = res.getAddresses().get("private").iterator().next().getAddr();
					deleteRecursivelyIP(slaveIp);
					break;
			}
    	}
 
	}

	private Node findParentNode(String entityID){
		if (controlledService.getId().equalsIgnoreCase(entityID)) return null;
		Node topology =  controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);
	
		if (topology.getId().equalsIgnoreCase(entityID)){
			//targetedNode=topology;
			return controlledService;
		}
		for (Node componentTopology:topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)){
			if (componentTopology.getId().equalsIgnoreCase(entityID)){
				//targetedNode=componentTopology;
				return topology;
			}
			for (Node subTopology:componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)){
				if (subTopology.getId().equalsIgnoreCase(entityID)){
					//targetedNode=subTopology;
					return componentTopology;
				}
				for (Node comp:subTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT))
					if (comp.getId().equalsIgnoreCase(entityID)){
						//targetedNode=comp;
						return subTopology;
					}
			}
			for (Node comp:componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT))
				if (comp.getId().equalsIgnoreCase(entityID)){
					//targetedNode=comp;
					return componentTopology;
				}
		}
		return null;
	}

	private void removeIpFromNode(Node entity, String ip){
		int indexSlave = entity.getAssociatedIps().indexOf(ip);
		if (indexSlave>0)
		entity.getAssociatedIps().remove(indexSlave);	
	}
	public void deleteRecursivelyIP(String ip){
		removeIpFromNode(controlledService,ip);
		Node topology =  controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);
		removeIpFromNode(topology,ip);

		
		List<Node> topologies =new ArrayList<Node>();
		topologies.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
		List<Node> componentsToExplore = new ArrayList<Node>();
		if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null)
		componentsToExplore.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
		while ( !topologies.isEmpty()){
			Node currentTopology = topologies.get(0);
			topologies.remove(0);
			removeIpFromNode(currentTopology,ip);

				if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)!=null)
					topologies.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
				if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null)
					componentsToExplore.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
			
		}
		
		while ( !componentsToExplore.isEmpty()){
			Node component =componentsToExplore.get(0);
			componentsToExplore.remove(0);
			removeIpFromNode(component,ip);

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
	    
	public static void main(String[] args){
   		
	}
	public void scaleIn(Node toBeScaled) {
	
			
		 if (toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP,NodeType.VIRTUAL_MACHINE).size()>1){
			 int tryRemove=0;
			 boolean removed=false;
			 while (!removed&&toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size()>tryRemove){
			 Node toBeRemoved = toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(tryRemove);
			 FluentIterable<? extends Server> servers=serverApi.listInDetail().concat();
			 for (Server server: servers) {
		         //  if( server.getName().equalsIgnoreCase(toBeScaled.getId())){
		        	  
		        	   String cmd = "";
		        	   
		        	   String ip = server.getAddresses().get("private").iterator().next().getAddr();
		        	   RuntimeLogger.logger.info("Trying to remove node "+toBeRemoved.getId());
		        	   if (ip.equalsIgnoreCase(toBeRemoved.getId())){
		        		   RuntimeLogger.logger.info("Removing node "+ip);
		        	   if (toBeScaled.getId().equalsIgnoreCase("EventProcessingServiceUnit"))
		        		   cmd = "decomissionWS " + ip ;
		        	   else
		        		   cmd = "decomissionCassandra "+ip;
		        	   				

		               if (!(controlledService.getStaticInformation("AccessIP").equals("localhost")))
		           	try {
		           		 executeAndExpectNothing((String)controlledService.getStaticInformation("AccessIP"), Configuration.getCertificatePath(), cmd);
		           	} catch (JSchException e1) {
		           		// TODO Auto-generated catch block
		           		e1.printStackTrace();
		           	}
		               else
		               {
		            	   try {
							Process p = Runtime.getRuntime().exec(cmd);
							int exitVal = p.waitFor();
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		               }
		               try {
		   				Thread.sleep(70000);
		   			} catch (InterruptedException e) {
		   				// TODO Auto-generated catch block
		   				e.printStackTrace();
		   			}
		               
		               toBeScaled.removeNode(toBeRemoved);
		               
		        	   serverApi.delete(server.getId());
		        	   removed=true;
		        	   break;
		        	   }else{
		        		   RuntimeLogger.logger.info("Not removing "+ip);

		        	   }
		        	   
		           //}
		         }
			 tryRemove++;
			 }
		 }
		
	}



	

	

}
