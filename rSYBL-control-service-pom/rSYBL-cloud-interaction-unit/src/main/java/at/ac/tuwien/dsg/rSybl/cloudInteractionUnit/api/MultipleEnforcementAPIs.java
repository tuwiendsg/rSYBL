package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;

import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;

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
		if (plugins!=null && !plugins.isEmpty()){
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
           if (Configuration.getCoordinatedMode().toLowerCase().contains("on")||Configuration.getCoordinatedMode().toLowerCase().contains("true")){
           enforceAction("scalein", arg0);
           
           }else{
		if (arg0.getAllRelatedNodes().size()>1){
			monitoringAPIInterface.enforcingActionStarted("ScaleIn",arg0 );

		RuntimeLogger.logger.info("Scaling in without target on node "+arg0.getId());

		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			RuntimeLogger.logger.info("Foound api ");
			enforcementAPI.scalein(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		}
		RuntimeLogger.logger.info("Finished scaling in in without target on node "+arg0.getId());
		monitoringAPIInterface.enforcingActionEnded("ScaleIn",arg0 );

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
            if (Configuration.getCoordinatedMode().toLowerCase().contains("on")||Configuration.getCoordinatedMode().toLowerCase().contains("true")){
           enforceAction("scaleout", arg0);
           
           }else{
		monitoringAPIInterface.enforcingActionStarted("ScaleOut",arg0 );

		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.scaleout(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		}
		monitoringAPIInterface.enforcingActionEnded("ScaleOut",arg0 );
            }
	}

	@Override
	public void enforceAction(String actionName, Node e) {
		monitoringAPIInterface.enforcingActionStarted(actionName,e );

		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.enforceAction(actionName,e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		}
		monitoringAPIInterface.enforcingActionEnded(actionName,e );

	}

	@Override
	public void enforceElasticityCapability(ElasticityCapability capability,
			Node e) {
		monitoringAPIInterface.enforcingActionStarted(capability.getName(),e );
		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.enforceElasticityCapability(capability,e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.refreshControlService(controlService);
			}
		}
		monitoringAPIInterface.enforcingActionEnded(capability.getName(),e );

	}
	

	@Override
	public void scalein(String target, Node arg0) {
		if (arg0.getAllRelatedNodes().size()>1){
			monitoringAPIInterface.enforcingActionStarted("ScaleIn - "+target,arg0 );
		RuntimeLogger.logger.info("Scaling in on plugin , "+target+" node "+arg0.getId());
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.scalein(arg0);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		RuntimeLogger.logger.info("Finished Scaling in on plugin , "+target+" node "+arg0.getId());
		monitoringAPIInterface.enforcingActionEnded("ScaleIn - "+target,arg0 );

		}
	}

	@Override
	public void scaleout(String target, Node arg0) {
		monitoringAPIInterface.enforcingActionStarted("ScaleOut - "+target, arg0);
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.scalein( arg0);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		monitoringAPIInterface.enforcingActionEnded("ScaleOut - "+target, arg0);

	}

	@Override
	public void enforceAction(String target, String actionName, Node e) {
		monitoringAPIInterface.enforcingActionStarted(actionName+" - "+target, e);

		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.enforceAction(actionName, e);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		monitoringAPIInterface.enforcingActionEnded(actionName+" - "+target, e);

	}

	@Override
	public void enforceElasticityCapability(String target,
			ElasticityCapability capability, Node e) {
		monitoringAPIInterface.enforcingActionStarted(capability.getName()+" - "+target, e);

		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.enforceElasticityCapability(capability, e);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.refreshControlService(controlService);
		}
		monitoringAPIInterface.enforcingActionEnded(capability.getName()+" - "+target, e);

	}

}
