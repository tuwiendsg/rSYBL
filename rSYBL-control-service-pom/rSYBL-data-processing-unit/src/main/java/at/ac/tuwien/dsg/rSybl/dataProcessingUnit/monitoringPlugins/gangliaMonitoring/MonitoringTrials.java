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

//import ch.ethz.ssh2.Connection;
//import ch.ethz.ssh2.StreamGobbler;

public class MonitoringTrials {
	/*
	   private String execute(String rootIPAddress, String securityCertificatePath, String gangliaPort,String command) throws JSchException {
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
	        
	        Session session = jSch.getSession("ubuntu", rootIPAddress, 22);
	        session.setConfig("StrictHostKeyChecking", "no"); //         Session session = jSch.getSession("ubuntu", rootIPAddress, 22);

	        UserInfo ui = new MyUserInfo(); // MyUserInfo implements UserInfo
	        session.setUserInfo(ui);
	        session.connect();
	        ChannelExec channel=(ChannelExec) session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(command);
	         channel.connect();
//	        channel.setInputStream(null);
	        InputStream stdout =null;
			try {
				stdout = channel.getInputStream();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	        InputStream stderr=null;
			try {
				stderr = channel.getErrStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BufferedReader reader =  new BufferedReader(new InputStreamReader(stdout));
			 String line = null;
	         String content = "";
	         try {
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				      //if ganglia does not respond
				      if (line.contains("Unable to connect")) {
				      	RuntimeLogger.logger.info( "" + rootIPAddress + " does not respond to monitoring request");
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
	         session.disconnect();
	         return content;

	    }
	   public static class MyUserInfo implements UserInfo{
		   
		@Override
		public String getPassphrase() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPassword() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean promptPassphrase(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean promptPassword(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean promptYesNo(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void showMessage(String arg0) {
			// TODO Auto-generated method stub
			
		}
		   
	   }
	   public class SSHManager
	   {
	   private   Logger LOGGER = RuntimeLogger.logger;
	   private JSch jschSSHChannel;
	   private String strUserName;
	   private String strConnectionIP;
	   private int intConnectionPort;
	   private String strPassword;
	   private Session sesConnection;
	   private int intTimeOut;
	   private String securityCertificatePath;
	   private void doCommonConstructorActions(String userName, 
	        String password, String connectionIP, String knownHostsFileName)
	   {
	      jschSSHChannel = new JSch();

	      try
	      {
	         jschSSHChannel.setKnownHosts(knownHostsFileName);
	      }
	      catch(JSchException jschX)
	      {
	         logError(jschX.getMessage());
	      }

	      strUserName = userName;
	      strPassword = password;
	      strConnectionIP = connectionIP;
	   }
	   public SSHManager(String userName, String securityCertificatePath,String rootIPAddress){
		   strUserName = userName;
		   this.securityCertificatePath=securityCertificatePath;
		   strConnectionIP = rootIPAddress;
		   intConnectionPort = 22;
		      intTimeOut = 60000;
		      jschSSHChannel = new JSch();
	   }
	   public SSHManager(String userName, String password, 
	      String connectionIP, String knownHostsFileName)
	   {
	      doCommonConstructorActions(userName, password, 
	                 connectionIP, knownHostsFileName);
	      intConnectionPort = 22;
	      intTimeOut = 60000;
	   }

	   public SSHManager(String userName, String password, String connectionIP, 
	      String knownHostsFileName, int connectionPort)
	   {
	      doCommonConstructorActions(userName, password, connectionIP, 
	         knownHostsFileName);
	      intConnectionPort = connectionPort;
	      intTimeOut = 60000;
	   }

	   public SSHManager(String userName, String password, String connectionIP, 
	       String knownHostsFileName, int connectionPort, int timeOutMilliseconds)
	   {
	      doCommonConstructorActions(userName, password, connectionIP, 
	          knownHostsFileName);
	      intConnectionPort = connectionPort;
	      intTimeOut = timeOutMilliseconds;
	   }

	   public String connect()
	   {
	      String errorMessage = null;
	      try{
	      byte [] prvkey=null;
	    	  try {
	  			prvkey = readFile(securityCertificatePath);
	  		} catch (IOException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		} // Private key must be byte array
	         final byte[] emptyPassPhrase = new byte[0]; // Empty passphrase for now, get real passphrase from MyUserInfo

	         jschSSHChannel.addIdentity(
	             "ubuntu",    // String userName
	             prvkey,          // byte[] privateKey 
	             null,            // byte[] publicKey //maybe generate a public key and try with it
	             emptyPassPhrase  // byte[] passPhrase
	         );
	         
	         sesConnection = jschSSHChannel.getSession("ubuntu", strConnectionIP, 22);
	         sesConnection.setConfig("StrictHostKeyChecking", "no"); //         Session session = jSch.getSession("ubuntu", rootIPAddress, 22);

	         UserInfo ui = new MyUserInfo(); // MyUserInfo implements UserInfo
	         sesConnection.setUserInfo(ui);
	         sesConnection.connect(intTimeOut);
	        
	      }
	      catch(JSchException jschX)
	      {
	         errorMessage = jschX.getMessage();
	      }

	      return errorMessage;
	   }

	   private String logError(String errorMessage)
	   {
	      if(errorMessage != null)
	      {
	         LOGGER.info( errorMessage);
	      }

	      return errorMessage;
	   }

	   private String logWarning(String warnMessage)
	   {
	      if(warnMessage != null)
	      {
	          LOGGER.info( warnMessage);

	      }

	      return warnMessage;
	   }

	   public String sendCommand(String command)
	   {
	      StringBuilder outputBuffer = new StringBuilder();

	      try
	      {
	         Channel channel = sesConnection.openChannel("exec");
	         ((ChannelExec)channel).setCommand(command);
	         channel.connect();
	         InputStream commandOutput = channel.getInputStream();
	         int readByte = commandOutput.read();

	         while(readByte != 0xffffffff)
	         {
	            outputBuffer.append((char)readByte);
	            readByte = commandOutput.read();
	         }

	         channel.disconnect();
	      }
	      catch(IOException ioX)
	      {
	         logWarning(ioX.getMessage());
	         return null;
	      }
	      catch(JSchException jschX)
	      {
	         logWarning(jschX.getMessage());
	         return null;
	      }

	      return outputBuffer.toString();
	   }

	   public void close()
	   {
	      sesConnection.disconnect();
	   }

	   }
	   
	   String content = ""; 
    	String hostname = rootIPAddress;
		String username = "ubuntu";

		File keyfile = new File(securityCertificatePath); // or "~/.ssh/id_dsa"

		try
		{

			Connection conn = new Connection(hostname);


			conn.connect();


			boolean isAuthenticated = conn.authenticateWithPublicKey(username, keyfile, "");
			
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");


			Session sess = conn.openSession();

			sess.execCommand("ls");

			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(sess.getStderr()));

			  String line = null;
		        while ((line = reader1.readLine()) != null) {

		            //if ganglia does not respond
		            if (line.contains("Unable to connect")) {
		            	RuntimeLogger.logger.info( "" + rootIPAddress + " does not respond to monitoring request");
		                return null;
		            }
		            if (line.contains("<") || line.endsWith("]>")) {
		                content += line + "\n";
		            }
		        }
	   
	  other lib 
	    
*/
}
