package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;

import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;

public class MultipleEnforcementAPIs implements EnforcementAPIInterface{
	HashMap<String,EnforcementAPI> enforcementAPIs = new HashMap<String, EnforcementAPI>() ;
	MonitoringAPIInterface monitoringAPIInterface ;
	//todo populate hashmap & use this in planning & sybl
	
	
	@Override
	public Node getControlledService() {
		for (EnforcementAPI api:enforcementAPIs.values()){
			return api.getControlledService();
		}
		return null;
	}

	@Override
	public void setControlledService(Node controlledService) {
		
		HashMap<String,String> plugins= Configuration.getEnforcementPlugins();
		if (!plugins.isEmpty()){
		for (String plugin : plugins.keySet()){
			EnforcementAPI enforcementAPI = new EnforcementAPI();
			enforcementAPI.setControlledService(controlledService, plugins.get(plugin));
			enforcementAPIs.put(plugin, enforcementAPI);
		}
		}else{
			EnforcementAPI enforcementAPI=new EnforcementAPI();
			enforcementAPI.setControlledService(controlledService);
			enforcementAPIs.put("", enforcementAPI);
		}
	}

	@Override
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.submitElasticityRequirements(description);
		}
		
	}

	@Override
	public void scalein(Node arg0) {
		if (arg0.getAllRelatedNodes().size()>1){
			EnforcementAPI enforcementAPI=enforcementAPIs.get("");
		if (!enforcementAPI.isExecutingControlAction() && arg0!=null){
			try{
			monitoringAPIInterface.enforcingActionStarted("ScaleIn",arg0 );
			RuntimeLogger.logger.info("Scaling in without target on node "+arg0.getId()+" with Enforcement plugin "+enforcementAPIs.get(""));
		
			enforcementAPI.setExecutingControlAction(true);
			enforcementAPI.scalein(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		
		RuntimeLogger.logger.info("Finished scaling in without target on node "+arg0.getId());
		monitoringAPIInterface.enforcingActionEnded("ScaleIn",arg0 );
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
			}catch(Exception e){
				RuntimeLogger.logger.error("Big big big error " +e.getMessage());
				RuntimeLogger.logger.error("Big big big error " +e.getCause());
				
				monitoringAPIInterface.enforcingActionEnded("ScaleIn",arg0 );
				
			}
		}
		}
	}

	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoringInterface) {
		monitoringAPIInterface=monitoringInterface;
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.setMonitoringPlugin(monitoringInterface);
		}
		
	}

	@Override
	public void scaleout(Node arg0) {
		
		
		EnforcementAPI enforcementAPI=enforcementAPIs.get("");
		
		if (!enforcementAPI.isExecutingControlAction() && arg0!=null){
			monitoringAPIInterface.enforcingActionStarted("ScaleOut",arg0 );
			RuntimeLogger.logger.info("Scaling out with default enforcement on node "+arg0.getId()+" with Enforcement plugin "+enforcementAPIs.get(""));

		enforcementAPI.setExecutingControlAction(true);
			enforcementAPI.scaleout(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
			RuntimeLogger.logger.info("Finished scaling out with default enforcement on node "+arg0.getId()+" with Enforcement plugin "+enforcementAPIs.get(""));

		monitoringAPIInterface.enforcingActionEnded("ScaleOut",arg0 );
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
	}

	@Override
	public void enforceAction(String actionName, Node e) {
		EnforcementAPI enforcementAPI=enforcementAPIs.get("");
		if (!enforcementAPI.isExecutingControlAction() && e!=null){
			monitoringAPIInterface.enforcingActionStarted(actionName,e );

		enforcementAPI.setExecutingControlAction(true);
			enforcementAPI.enforceAction(actionName,e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		
		monitoringAPIInterface.enforcingActionEnded(actionName,e );
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
	}

	@Override
	public void enforceElasticityCapability(ElasticityCapability capability,
			Node e) {
		EnforcementAPI enforcementAPI=enforcementAPIs.values().iterator().next();
		if (!enforcementAPI.isExecutingControlAction() && e!=null){
			monitoringAPIInterface.enforcingActionStarted(capability.getName(),e );
			
		enforcementAPI.setExecutingControlAction(true);
		enforcementAPI.enforceElasticityCapability(capability,e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		
		monitoringAPIInterface.enforcingActionEnded(capability.getName(),e );
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
	}
	

	@Override
	public void scalein(String target, Node arg0) {
		if (arg0.getAllRelatedNodes().size()>1){
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && arg0!=null){
			monitoringAPIInterface.enforcingActionStarted("ScaleIn - "+target,arg0 );
			RuntimeLogger.logger.info("Scaling in on plugin , "+target+" node "+arg0.getId());
		
		enforcementAPI.setExecutingControlAction(true);
		enforcementAPI.scalein(arg0);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		RuntimeLogger.logger.info("Finished Scaling in on plugin , "+target+" node "+arg0.getId());
		monitoringAPIInterface.enforcingActionEnded("ScaleIn - "+target,arg0 );
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
		}
	}

	@Override
	public void scaleout(String target, Node arg0) {
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && arg0!=null){
			monitoringAPIInterface.enforcingActionStarted("ScaleOut - "+target, arg0);
			
		enforcementAPI.setExecutingControlAction(true);
		enforcementAPI.scaleout( arg0);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		monitoringAPIInterface.enforcingActionEnded("ScaleOut - "+target, arg0);
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
	}

	@Override
	public void enforceAction(String target, String actionName, Node e) {
	
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && e!=null){
			monitoringAPIInterface.enforcingActionStarted(actionName+" - "+target, e);

		enforcementAPI.setExecutingControlAction(true);

		enforcementAPI.enforceAction(actionName, e);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		monitoringAPIInterface.enforcingActionEnded(actionName+" - "+target, e);
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
	}

	@Override
	public void enforceElasticityCapability(String target,
			ElasticityCapability capability, Node e) {
		monitoringAPIInterface.enforcingActionStarted(capability.getName()+" - "+target, e);

		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && e!=null){

		enforcementAPI.setExecutingControlAction(true);

		enforcementAPI.enforceElasticityCapability(capability, e);	

		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		monitoringAPIInterface.enforcingActionEnded(capability.getName()+" - "+target, e);
		try {
			Thread.sleep(60000);
		} catch (InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		enforcementAPI.setExecutingControlAction(false);
		}
	}

}
