package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.applicationControl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.gangliaMonitoring.GangliaMonitor.MyUserInfo;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class M2MApplicationControl {
	
	public void decommission(String nodeId, String ip, Node controlledService){
		DependencyGraph dependencyGraph=new DependencyGraph();
		dependencyGraph.setCloudService(controlledService);
		
		String cmd="";
		
		if (nodeId.contains("EventProcessing")){
			String ip1="";
			for (Node node:dependencyGraph.getAllServiceUnits()){
				if (node.getId().contains("Load")){
					ip1=node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0).getId();
				}
			}         
	   		   cmd = "decomissionWS " + ip1+" "+ip ;
		}
	   	   else{
	   		String ip1="";
			for (Node node:dependencyGraph.getAllServiceUnits()){
				if (node.getId().contains("Controller")){
					ip1=node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0).getId();
				}
			}    
	   	   
	   		   cmd = "decomissionCassandra "+ip1+" "+ip;
	   	   }				

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
}
