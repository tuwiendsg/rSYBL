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


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.ganglia.gangliaInfo.GangliaClusterInfo;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.ganglia.gangliaInfo.GangliaHostInfo;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.ganglia.gangliaInfo.GangliaInfo;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;



public class GangliaMonitor {
    /**
     *
     * @param ipsToBeMonitored list of local IPS for the machines to be
     * monitored
     * @param accessIP = IP of the machine from which all data is retrieved
     * @param securityCertificatePath security certificate used to SSH to the
     * accessIPmachine
     * @param gangliaPort usually 8649
     * @return
     * @throws JAXBException
     * @throws IOException
     * @throws JSchException 
     */
    public GangliaClusterInfo monitorGangliaNodesIndirectly(List<String> ipsToBeMonitored, String accessIP, String securityCertificatePath, String gangliaPort) throws JAXBException, IOException { 	 

        GangliaClusterInfo monitoredData = monitorGangliaMachineIndirectly(accessIP, securityCertificatePath, gangliaPort);
        //contains only IPs that are wanted
        GangliaClusterInfo filteredData = new GangliaClusterInfo();
        Collection<GangliaHostInfo> filteredDataHostsInfo = filteredData.getHostsInfo();

        filteredData.setLatlong(monitoredData.getLatlong());
        filteredData.setLocaltime(monitoredData.getLocaltime());
        filteredData.setName(monitoredData.getName());
        filteredData.setOwner(monitoredData.getOwner());
        filteredData.setUrl(monitoredData.getUrl());

        //extract from cluster all hosts that are not equal to the desired IP
        for (String address : ipsToBeMonitored) {
            filteredDataHostsInfo.add(monitoredData.searchHostsByExactIP(address));
        }
       
        return filteredData;
    }

    public GangliaClusterInfo monitorGangliaNodesDirectly(List<String> ipsToBeMonitored, String accessIP, String gangliaPort) throws JAXBException, IOException {

        GangliaClusterInfo monitoredData = monitorGangliaMachineDirectly(accessIP, gangliaPort);

        //contains only IPs that are wanted
        GangliaClusterInfo filteredData = new GangliaClusterInfo();
        Collection<GangliaHostInfo> filteredDataHostsInfo = filteredData.getHostsInfo();

        filteredData.setLatlong(monitoredData.getLatlong());
        filteredData.setLocaltime(monitoredData.getLocaltime());
        filteredData.setName(monitoredData.getName());
        filteredData.setOwner(monitoredData.getOwner());
        filteredData.setUrl(monitoredData.getUrl());

        //extract from cluster all hosts that are not equal to the desired IP
        for (String address : ipsToBeMonitored) {
            filteredDataHostsInfo.add(monitoredData.searchHostsByExactIP(address));
        }

        return filteredData;
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
    private Session session;
    private String execute(String rootIPAddress, String securityCertificatePath, String gangliaPort,String command) throws JSchException {
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
         return content;

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
 
    /**
     * Indirect means a CERTIFICATE is required for connecting to GANGLIA target
     *
     * @param rootIPAddress
     * @return Information about ALL hosts monitored by Ganglia
     * @throws IOException
     * @throws JAXBException
     * @throws JSchException 
     */
    public GangliaClusterInfo monitorGangliaMachineIndirectly(String rootIPAddress, String securityCertificatePath, String gangliaPort)  {
   //    String cmd = "ssh -i " + Configuration.getSecurityCertificatePath() + " ubuntu@" + rootIPAddress + " telnet " + targetIP + " " + Configuration.getGangliaPort();
        
    	//String cmd = "ssh -i " + securityCertificatePath + " ubuntu@" + rootIPAddress + " telnet localhost " + gangliaPort;
    	//Process p = Runtime.getRuntime().exec(cmd);
        //BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      
      
     //   p.getInputStream().close();
       // p.getErrorStream().close();
       // p.getOutputStream().close();
      //  p.destroy();
    	String command = "telnet localhost " + gangliaPort;

    	String content= "";
		try {
			content = execute(rootIPAddress, securityCertificatePath, gangliaPort, command);
		} catch (JSchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
    	StringReader stringReader = new StringReader(content);
  
        JAXBContext jc=null;
		try {
			jc = JAXBContext.newInstance(GangliaInfo.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Unmarshaller unmarshaller = null;
		try {
			unmarshaller = jc.createUnmarshaller();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GangliaInfo info=null;
		try {
			info = (GangliaInfo) unmarshaller.unmarshal(stringReader);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GangliaClusterInfo gangliaClusterInfo = info.getClusters().iterator().next();
        stringReader.close();

        return gangliaClusterInfo;

    }

    /**
     * NO CERTIFICATE is required for connecting to GANGLIA target
     *
     * @param targetIP
     * @param gangliaPort = usually 8649
     * @return ALL the monitored data Ganglia reports to that particular IP
     * ()data from multiple machines
     * @throws IOException
     * @throws JAXBException
     */
    private GangliaClusterInfo monitorGangliaMachineDirectly(String targetIP, String gangliaPort) throws IOException, JAXBException {
        String cmd = "telnet " + targetIP + " " + gangliaPort;

        Process p = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        String content = "";
        while ((line = reader.readLine()) != null) {

            //if ganglia does not respond
            if (line.contains("Unable to connect")) {
            	RuntimeLogger.logger.info( "" + targetIP + " does not respond to monitoring request");
                return null;
            }
            if (line.contains("<") || line.endsWith("]>")) {
                content += line + "\n";
            }
        }

        p.getInputStream().close();
        p.getErrorStream().close();
        p.getOutputStream().close();
        p.destroy();


        //if ganglia does not respond
        if (content == null || content.length() == 0) {
        	RuntimeLogger.logger.info( "" + targetIP + " does not respond to monitoring request");
            return null;
        }

        StringReader stringReader = new StringReader(content);

        JAXBContext jc = JAXBContext.newInstance(GangliaInfo.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GangliaInfo info = (GangliaInfo) unmarshaller.unmarshal(stringReader);
        GangliaClusterInfo gangliaClusterInfo = info.getClusters().iterator().next();
        stringReader.close();
       
        return gangliaClusterInfo;

    }

    public void close() {
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
