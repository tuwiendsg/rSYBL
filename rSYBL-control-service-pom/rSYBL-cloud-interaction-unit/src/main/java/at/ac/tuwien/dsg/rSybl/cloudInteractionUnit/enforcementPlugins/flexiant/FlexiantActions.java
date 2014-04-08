package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.flexiant;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;

import com.extl.jade.user.ExtilityException;
import com.extl.jade.user.Job;
import com.extl.jade.user.NetworkType;
import com.extl.jade.user.Nic;
import com.extl.jade.user.ResourceMetadata;
import com.extl.jade.user.Server;
import com.extl.jade.user.Condition;
import com.extl.jade.user.FilterCondition;
import com.extl.jade.user.ResourceType;
import com.extl.jade.user.SearchFilter;
import com.extl.jade.user.ListResult;
import com.extl.jade.user.QueryLimit;
import com.extl.jade.user.ServerStatus;
import com.extl.jade.user.UserAPI;
import com.extl.jade.user.UserService;

public class FlexiantActions  {
	String userEmailAddress = Configuration.getUserEMailAddress();
	String apiUserName = Configuration.getAPIUserName();
	String customerUUID = Configuration.getCustomerUUID();
	String password = Configuration.getPassword();
	String ENDPOINT_ADDRESS_PROPERTY = "https://api.sd1.flexiant.net:4442";

	public void removeServer(String serverUUID) {
		UserService service;

		URL url = ClassLoader.getSystemClassLoader()
				.getResource("UserAPI.wsdl");

		// Get the UserAPI
		UserAPI api = new UserAPI(url, new QName(
				"http://extility.flexiant.net", "UserAPI"));
		// and set the service port on the service
		service = api.getUserServicePort();
		BindingProvider portBP = (BindingProvider) service;

		// and set the service endpoint

		portBP.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS_PROPERTY);

		// and the caller's authentication details and password
		portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
				userEmailAddress + "/" + customerUUID);
		portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
				password);

		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		DatatypeFactory datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error(e.getMessage()); 
		}
		XMLGregorianCalendar now = datatypeFactory
				.newXMLGregorianCalendar(gregorianCalendar);

		Date date = new Date();
		datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error(e.getMessage()); 
		}
		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		int mins = date.getMinutes();
		int sec = date.getSeconds();
		int hours = date.getHours();
		sec += 10;
		if (sec >= 60) {
			sec -= 60;
			mins += 1;
		}
		if (mins==60){
			mins=59;
		}
		
		now.setTime(hours, mins, sec);
		RuntimeLogger.logger.info("Removing server at " + now.toString());

		Job stopServer = null;
		try {
			stopServer = service.changeServerStatus(serverUUID,
					ServerStatus.STOPPED, true, new ResourceMetadata(), now);
			service.waitForJob(stopServer.getResourceUUID(), false);
		
		date = new Date();
		datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error(e.getMessage()); 
		}
		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		 mins = date.getMinutes();
		 sec = date.getSeconds();
		 hours = date.getHours();
			sec += 10;
			if (sec >= 60) {
				sec -= 60;
				mins += 1;
			}
			if (mins==60){
				mins=59;
			}
			
		now.setTime(hours, mins, sec);
		Job deleteServer = null;
			deleteServer = service.deleteResource(serverUUID, true, now);

		
			service.waitForJob(deleteServer.getResourceUUID(), false);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error(e.getMessage()); 
			return;
		}

	}

	/*
	 * create new server
	 */
	public String createNewServer(String serverName, String imageUUID, int cpu,
			int mem) {

		UserService service;

		URL url = ClassLoader.getSystemClassLoader()
				.getResource("UserAPI.wsdl");

		// Get the UserAPI
		UserAPI api = new UserAPI(url, new QName(
				"http://extility.flexiant.net", "UserAPI"));

		// and set the service port on the service
		service = api.getUserServicePort();

		// Get the binding provider
		BindingProvider portBP = (BindingProvider) service;

		// and set the service endpoint
		portBP.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS_PROPERTY);

		// and the caller's authentication details and password
		portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
				userEmailAddress + "/" + customerUUID);
		portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
				password);

		Server skeletonServer = new Server();
		skeletonServer.setVdcUUID(Configuration.getVdcUUID());
		// skeletonServer.setCpu(cpu);
		skeletonServer.setInitialUser("ubuntu");

		skeletonServer.setCustomerUUID(Configuration.getCustomerUUID());
		// skeletonServer.setProductOfferUUID("8a657434-b0c5-3a99-83bf-87cf4c9dedb8");
		skeletonServer
				.setProductOfferUUID(Configuration.getDefaultProductOfferUUID());
		// skeletonServer.setRam(mem);
		// skeletonServer.setImageName(serverName);
		skeletonServer.setImageUUID(imageUUID);
		//skeletonServer.setDeploymentInstanceUUID(Configuration.getDeploymentInstanceUUID());
		skeletonServer.setClusterUUID(Configuration.getClusterUUID());
		List<String> sshs = new ArrayList<String>();
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		sshs.add(Configuration.getSSHKey());
		Nic networkInterface = new Nic();
		//networkInterface.setClusterUUID(Configuration.getClusterUUID());
		networkInterface
				.setCustomerUUID(Configuration.getCustomerUUID());
		//networkInterface.setDeploymentInstanceUUID(Configuration.getDeploymentInstanceUUID());
		//networkInterface.setProductOfferUUID("");
		networkInterface.setNetworkUUID(Configuration.getNetworkUUID());
		//networkInterface.setVdcUUID(Configuration.getVdcUUID());
		networkInterface.setNetworkType(NetworkType.IP);
		// networkInterface.setServerUUID("");
		Date date = new Date();

		DatatypeFactory datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RuntimeLogger.logger.error(e.getMessage()); return "";
		}
		XMLGregorianCalendar now = datatypeFactory
				.newXMLGregorianCalendar(gregorianCalendar);
		int mins = date.getMinutes();
		int sec = date.getSeconds();
		int hours = date.getHours();
		
		Job createServerJob = null;
		
		date = new Date();
		datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RuntimeLogger.logger.error(e.getMessage()); return "";
		}
		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		 mins = date.getMinutes();
		 sec = date.getSeconds();
		 hours = date.getHours();

			sec += 10;
			if (sec >= 60) {
				sec -= 60;
				mins += 1;
			}
			if (mins==60){
				mins=59;
			}
			
		now.setTime(hours, mins, sec);
		skeletonServer.setResourceName(serverName);
		skeletonServer.getNics().add(networkInterface);
		try {
			createServerJob = service.createServer(skeletonServer, sshs, now );
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RuntimeLogger.logger.error(e.getMessage()); return "";
		}

		try {
			service.waitForJob(createServerJob.getResourceUUID(), false);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RuntimeLogger.logger.error(e.getMessage()); return "";
		}
//		date=new Date();
//		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
//		 mins = date.getMinutes();
//		 sec = date.getSeconds();
//		 hours = date.getHours();
//
//			sec += 20;
//			if (sec >= 60) {
//				sec -= 60;
//				mins += 1;
//			}
//			if (mins==60){
//				mins=59;
//			}
//			
//		now.setTime(hours, mins, sec);
//
//		RuntimeLogger.logger.info("Creating NIC at " + now.toString());
//	
//		
//		Job j = null;
//		try {
//			j = service.createNetworkInterface(networkInterface, now);
//		} catch (ExtilityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			RuntimeLogger.logger.info(e.getMessage()); 
//			return "";
//		}
		//skeletonServer.getNics().add(networkInterface);
//		RuntimeLogger.logger.info("Nic UUID "+j.getItemUUID());
//		
//
//		try {
//			service.waitForJob(j.getResourceUUID(), false);
//		} catch (ExtilityException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//
//			return "";
//		}
//		
//		date = new Date();
//		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
//		 mins = date.getMinutes();
//		 sec = date.getSeconds();
//		 hours = date.getHours();
//		sec += 10;
//		if (sec >= 60) {
//			sec -= 60;
//			mins += 1;
//		}
//		if (mins==60){
//			mins=59;
//		}
//		
//		
//		now.setTime(hours, mins, sec);
//
//
//		try {
//			Job job = service.attachNetworkInterface(
//					createServerJob.getItemUUID(), j.getItemUUID(), 0, now);
//			service.waitForJob(job.getResourceUUID(), false);
//		} catch (ExtilityException e) {
//			e.printStackTrace();
//			RuntimeLogger.logger.error(e.getMessage()); return "";
//		}
		date = new Date();
		now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
		 mins = date.getMinutes();
		 sec = date.getSeconds();
		 hours = date.getHours();
		sec += 10;
		if (sec >= 60) {
			sec -= 60;
			mins += 1;
		}
		if (mins==60){
			mins=59;
		}
		
		
		now.setTime(hours, mins, sec);

		Job startServer = null;
		try {
			startServer = service.changeServerStatus(
					createServerJob.getItemUUID(), ServerStatus.RUNNING, true,
					skeletonServer.getResourceMetadata(), now);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RuntimeLogger.logger.error(e.getMessage()); return "";
		}

		try {
			service.waitForJob(startServer.getResourceUUID(), false);
		} catch (ExtilityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			RuntimeLogger.logger.error(e.getMessage()); return "";
		}
		
		return createServerJob.getItemUUID();

		// createdServer.getNics().get(0).getIpAddresses().get(0).getIpAddress();
	}
	public void cleanNics(){
		List<Nic> nics=listAllNics();
		UserService service;
		GregorianCalendar gregorianCalendar = new GregorianCalendar();

		List<Server> servers = new ArrayList<Server>();
		// Get the service WSDL from the client jar
		URL url = ClassLoader.getSystemClassLoader()
				.getResource("UserAPI.wsdl");

		// Get the UserAPI
		UserAPI api = new UserAPI(url, new QName(
				"http://extility.flexiant.net", "UserAPI"));

		// and set the service port on the service
		service = api.getUserServicePort();

		// Get the binding provider
		BindingProvider portBP = (BindingProvider) service;

		// and set the service endpoint
		portBP.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS_PROPERTY);

		// and the caller's authentication details and password
		portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
				userEmailAddress + "/" + customerUUID);
		portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
				password);
		Date date = new Date();

		DatatypeFactory datatypeFactory = null;
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			RuntimeLogger.logger.error(e.getMessage()); 
		}
		XMLGregorianCalendar now = datatypeFactory
				.newXMLGregorianCalendar(gregorianCalendar);
	
		System.err.println(nics.size());
		for (Nic nic:nics){
			if (nic.getIpAddresses()!=null && nic.getIpAddresses().size()>0)
			System.out.println(nic.getIpAddresses().get(0));
			if (nic.getServerUUID()==null)
				try {
					date = new Date();
					int mins = date.getMinutes();
					int sec = date.getSeconds();
					int hours = date.getHours();
					sec += 10;
					if (sec >= 60) {
						sec -= 60;
						mins += 1;
					}
					if (mins==60){
						mins=59;
					}
					
					
					now.setTime(hours, mins, sec);
					service.deleteResource(nic.getResourceUUID(), true, now);
				} catch (ExtilityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public List<Server> listServers() {
		UserService service;
		List<Server> servers = new ArrayList<Server>();
		// Get the service WSDL from the client jar
		URL url = ClassLoader.getSystemClassLoader()
				.getResource("UserAPI.wsdl");

		// Get the UserAPI
		UserAPI api = new UserAPI(url, new QName(
				"http://extility.flexiant.net", "UserAPI"));

		// and set the service port on the service
		service = api.getUserServicePort();

		// Get the binding provider
		BindingProvider portBP = (BindingProvider) service;

		// and set the service endpoint
		portBP.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS_PROPERTY);

		// and the caller's authentication details and password
		portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
				userEmailAddress + "/" + customerUUID);
		portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
				password);

		try {

			// List all servers in the running and starting states

			// Create an FQL filter and a filter condition
			SearchFilter sf = new SearchFilter();
			FilterCondition fc = new FilterCondition();

			// set the condition type
			fc.setCondition(Condition.IS_EQUAL_TO);

			// the field to be matched
			fc.setField("status");

			// and a list of values
			fc.getValue().add(ServerStatus.RUNNING.name());
			fc.getValue().add(ServerStatus.STARTING.name());

			// Add the filter condition to the query
			sf.getFilterConditions().add(fc);

			// Set a limit to the number of results
			QueryLimit lim = new QueryLimit();
			lim.setMaxRecords(20);

			// Call the service to execute the query
			ListResult result = service.listResources(sf, lim,
					ResourceType.SERVER);

			// Iterate through the results
			for (Object o : result.getList()) {
				Server s = ((Server) o);
				RuntimeLogger.logger.info("Server " + s.getResourceUUID());

				servers.add(s);

			}

		} catch (Exception e) {

			RuntimeLogger.logger.error(e.getMessage()); 
		}
		return servers;
	}

	public List<Nic> listAllNics() {
		List<Nic> nics = new ArrayList<Nic>();

		URL url = ClassLoader.getSystemClassLoader()
				.getResource("UserAPI.wsdl");

		// Get the UserAPI
		UserAPI api = new UserAPI(url, new QName(
				"http://extility.flexiant.net", "UserAPI"));

		// and set the service port on the service
		UserService service;
		service = api.getUserServicePort();

		// Get the binding provider
		BindingProvider portBP = (BindingProvider) service;

		// and set the service endpoint
		portBP.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				ENDPOINT_ADDRESS_PROPERTY);

		// and the caller's authentication details and password
		portBP.getRequestContext().put(BindingProvider.USERNAME_PROPERTY,
				userEmailAddress + "/" + customerUUID);
		portBP.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY,
				password);

		try {

			// List all servers in the running and starting states

			// Create an FQL filter and a filter condition
			SearchFilter sf = new SearchFilter();


			// set the condition type

			// Add the filter condition to the query
			// Set a limit to the number of results
			QueryLimit lim = new QueryLimit();
			lim.setMaxRecords(1000);

			// Call the service to execute the query
			ListResult result = service
					.listResources(null, null, ResourceType.NIC);

			// Iterate through the results
			for (Object o : result.getList()) {
				Nic s = ((Nic) o);
				RuntimeLogger.logger.info("Nic " + s.getResourceUUID()
						+ "Server "+s.getServerUUID()+" Ip addresses " +s.getIpAddresses().size());
        	 	nics.add(s);

			}

		} catch (Exception e) {

			RuntimeLogger.logger.info(e.getMessage()); 
		}

		return nics;
	}

}