package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;

import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;

public class MultipleEnforcementAPIs implements EnforcementAPIInterface{
	HashMap<String,EnforcementAPI> enforcementAPIs = new HashMap<String, EnforcementAPI>() ;
	
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
		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.scalein(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.setControlledService(controlService);
			}
		}
	}

	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoringInterface) {
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.setMonitoringPlugin(monitoringInterface);
		}
		
	}

	@Override
	public void scaleout(Node arg0) {
		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.scaleout(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.setControlledService(controlService);
			}
		}
	}

	@Override
	public void enforceAction(String actionName, Node e) {
		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.enforceAction(actionName,e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.setControlledService(controlService);
			}
		}
	}

	@Override
	public void enforceElasticityCapability(ElasticityCapability capability,
			Node e) {
		for (EnforcementAPI enforcementAPI:enforcementAPIs.values()){
			enforcementAPI.enforceElasticityCapability(capability,e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api:enforcementAPIs.values()){
				api.setControlledService(controlService);
			}
		}
	}
	

	@Override
	public void scalein(String target, Node arg0) {
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.scalein( arg0);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.setControlledService(controlService);
		}
	}

	@Override
	public void scaleout(String target, Node arg0) {
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.scalein( arg0);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.setControlledService(controlService);
		}
	}

	@Override
	public void enforceAction(String target, String actionName, Node e) {
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.enforceAction(actionName, e);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.setControlledService(controlService);
		}
	}

	@Override
	public void enforceElasticityCapability(String target,
			ElasticityCapability capability, Node e) {
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		enforcementAPI.enforceElasticityCapability(capability, e);	
		Node controlService = enforcementAPI.getControlledService();
		for (EnforcementAPI api:enforcementAPIs.values()){
			api.setControlledService(controlService);
		}
	}

}
