package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;

import java.util.List;
import java.util.Map.Entry;


public class MultipleEnforcementAPIs implements EnforcementAPIInterface {
	HashMap<String, EnforcementAPI> enforcementAPIs = new HashMap<String, EnforcementAPI>();
	MonitoringAPIInterface monitoringAPIInterface;
        
	// todo populate hashmap & use this in planning & sybl

	@Override
	public Node getControlledService() {
		for (EnforcementAPI api : enforcementAPIs.values()) {
			return api.getControlledService();
		}
		return null;
	}

	@Override
	public void setControlledService(Node controlledService) {

		HashMap<String, String> plugins = Configuration.getEnforcementPlugins();
		if (!plugins.isEmpty()) {
			for (String plugin : plugins.keySet()) {
				EnforcementAPI enforcementAPI = new EnforcementAPI();
				enforcementAPI.setControlledService(controlledService,
						plugins.get(plugin));
				enforcementAPIs.put(plugin, enforcementAPI);
			}
		} else {
			EnforcementAPI enforcementAPI = new EnforcementAPI();
			enforcementAPI.setControlledService(controlledService,Configuration.getEnforcementPlugin());
			enforcementAPIs.put("", enforcementAPI);
		}
	}

	@Override
	public void submitElasticityRequirements(
			ArrayList<ElasticityRequirement> description) {
		for (EnforcementAPI api : enforcementAPIs.values()) {
			api.submitElasticityRequirements(description);
		}

	}

	@Override
	public boolean scalein(Node arg0) {
		if (arg0.getAllRelatedNodes().size() > 1) {
			boolean res=false;
			EnforcementAPI enforcementAPI = enforcementAPIs.get("");
			if (!enforcementAPI.isExecutingControlAction() && arg0 != null) {
                            enforcementAPI.setExecutingControlAction(true);
				try {
					monitoringAPIInterface.enforcingActionStarted("ScaleIn",
							arg0);
					RuntimeLogger.logger
							.info("Scaling in without target on node "
									+ arg0.getId()
									+ " with Enforcement plugin "
									+ enforcementAPIs.get(""));

					
					res=enforcementAPI.scalein(arg0);
					Node controlService = enforcementAPI.getControlledService();
					for (EnforcementAPI api : enforcementAPIs.values()) {
						api.refreshControlService(controlService);
					}

					RuntimeLogger.logger
							.info("Finished scaling in without target on node "
									+ arg0.getId());
					monitoringAPIInterface
							.enforcingActionEnded("ScaleIn", arg0);
                                  //                              monitoringAPIInterface.refreshCompositionRules();

					if (res)
					try {
						Thread.sleep(60000);
					} catch (InterruptedException ex) {
						enforcementAPI.setExecutingControlAction(false);
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
					enforcementAPI.setExecutingControlAction(false);
				} catch (Exception e) {
					RuntimeLogger.logger.error("Big big big error "
							+ e.getMessage());
					RuntimeLogger.logger.error("Big big big error "
							+ e.getCause());

					monitoringAPIInterface
							.enforcingActionEnded("ScaleIn", arg0);
                                  //                              monitoringAPIInterface.refreshCompositionRules();

					enforcementAPI.setExecutingControlAction(false);
					return false;
				}
			}
			return res;
		}
		return false;
		
	}

	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoringInterface) {
		monitoringAPIInterface = monitoringInterface;
		for (EnforcementAPI api : enforcementAPIs.values()) {
			api.setMonitoringPlugin(monitoringInterface);
		}

	}
@Override
	public boolean scaleout(double violationDegree,Node arg0) {

		EnforcementAPI enforcementAPI = enforcementAPIs.get("");
		boolean res=false;
		if (!enforcementAPI.isExecutingControlAction() && arg0 != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
			RuntimeLogger.logger
					.info("Scaling out with default enforcement on node "
							+ arg0.getId() + " with Enforcement plugin "
							+ enforcementAPIs.get(""));

			
			res=enforcementAPI.scaleout(violationDegree,arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			RuntimeLogger.logger
					.info("Finished scaling out with default enforcement on node "
							+ arg0.getId()
							+ " with Enforcement plugin "
							+ enforcementAPIs.get(""));
			monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
                        //monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}
	@Override
	public boolean scaleout(Node arg0) {

		EnforcementAPI enforcementAPI = enforcementAPIs.get("");
		boolean res=false;
		if (!enforcementAPI.isExecutingControlAction() && arg0 != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
			RuntimeLogger.logger
					.info("Scaling out with default enforcement on node "
							+ arg0.getId() + " with Enforcement plugin "
							+ enforcementAPIs.get(""));

			
			res=enforcementAPI.scaleout(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			RuntimeLogger.logger
					.info("Finished scaling out with default enforcement on node "
							+ arg0.getId()
							+ " with Enforcement plugin "
							+ enforcementAPIs.get(""));
			monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
                    //    monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}

	@Override
	public boolean enforceAction(String actionName, Node e) {
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get("");
		if (!enforcementAPI.isExecutingControlAction() && e != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted(actionName, e);

			
			res=enforcementAPI.enforceAction(actionName, e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}

			monitoringAPIInterface.enforcingActionEnded(actionName, e);
                              //                  monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}
	@Override
	public boolean enforceAction(double violationDegree,String actionName, Node e) {
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get("");
		if (!enforcementAPI.isExecutingControlAction() && e != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted(actionName, e);

			
			res=enforcementAPI.enforceAction(violationDegree,actionName, e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}

			monitoringAPIInterface.enforcingActionEnded(actionName, e);
                                                //monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}

	@Override
	public boolean enforceElasticityCapability(ElasticityCapability capability,
			Node e) {
		EnforcementAPI enforcementAPI = enforcementAPIs.values().iterator()
				.next();
		boolean res=false;
		if (!enforcementAPI.isExecutingControlAction() && e != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted(capability.getName(),
					e);

			
			res=enforcementAPI.enforceElasticityCapability(capability, e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}

			monitoringAPIInterface
					.enforcingActionEnded(capability.getName(), e);
                                              //monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}

	@Override
	public boolean scalein(String target, Node arg0) {
		RuntimeLogger.logger.info("----------------------Trying to scale in "+ target + " "+arg0.getId());
		boolean res=false;
		if (arg0.getAllRelatedNodes().size() > 1) {
			EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
			if (!enforcementAPI.isExecutingControlAction() && arg0 != null) {
                            enforcementAPI.setExecutingControlAction(true);
				monitoringAPIInterface.enforcingActionStarted("ScaleIn - "
						+ target, arg0);
				RuntimeLogger.logger.info("Scaling in on plugin , " + target
						+ " node " + arg0.getId());

				
				res=enforcementAPI.scalein(arg0);
				Node controlService = enforcementAPI.getControlledService();
				for (EnforcementAPI api : enforcementAPIs.values()) {
					api.refreshControlService(controlService);
				}
				RuntimeLogger.logger.info("Finished Scaling in on plugin , "
						+ target + " node " + arg0.getId());
				monitoringAPIInterface.enforcingActionEnded("ScaleIn - "
						+ target, arg0);
                                                   //     monitoringAPIInterface.refreshCompositionRules();

				if (res)
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				enforcementAPI.setExecutingControlAction(false);
			}
		}
		return res;
	}

	@Override
	public boolean scaleout(String target, Node arg0) {
		RuntimeLogger.logger.info("----------------------Trying to scale out "+ target + " "+arg0.getId());
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && arg0 != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted("ScaleOut - "
					+ target, arg0);

			
			res=enforcementAPI.scaleout(arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			monitoringAPIInterface.enforcingActionEnded("ScaleOut - " + target,
					arg0);
                                              //  monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}else{
			RuntimeLogger.logger.info("--------------------Not possible. It is already enforcing an action or arg0 is null "+ target + " "+arg0.getId());

		}
		return res;
	}
        @Override
	public boolean scaleout(double violationDegree,String target, Node arg0) {
		RuntimeLogger.logger.info("----------------------Trying to scale out "+ target + " "+arg0.getId());
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && arg0 != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted("ScaleOut - "
					+ target, arg0);

			
			res=enforcementAPI.scaleout(violationDegree,arg0);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			monitoringAPIInterface.enforcingActionEnded("ScaleOut - " + target,
					arg0);
                                        //        monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}else{
			RuntimeLogger.logger.info("--------------------Not possible. It is already enforcing an action or arg0 is null "+ target + " "+arg0.getId());

		}
		return res;
	}

	@Override
	public boolean enforceAction(String target, String actionName, Node e) {
		RuntimeLogger.logger.info("----------------------Trying to "+actionName+" on "+ target + " "+e);
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && e != null) {
                    enforcementAPI.setExecutingControlAction(true);

			monitoringAPIInterface.enforcingActionStarted(actionName + " - "
					+ target, e);

			
			res=enforcementAPI.enforceAction(actionName, e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			monitoringAPIInterface.enforcingActionEnded(actionName + " - "
					+ target, e);
                                     //           monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}

	@Override
	public boolean enforceAction(double violationDegree,String target, String actionName, Node e) {
		RuntimeLogger.logger.info("----------------------Trying to "+actionName+" on "+ target + " "+e);
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && e != null) {
                    enforcementAPI.setExecutingControlAction(true);
			monitoringAPIInterface.enforcingActionStarted(actionName + " - "
					+ target, e);

			

			res=enforcementAPI.enforceAction(violationDegree,actionName, e);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			monitoringAPIInterface.enforcingActionEnded(actionName + " - "
					+ target, e);
                                        //        monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}

	@Override
	public boolean enforceElasticityCapability(String target,
			ElasticityCapability capability, Node e) {
		monitoringAPIInterface.enforcingActionStarted(capability.getName()
				+ " - " + target, e);
		boolean res=false;
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		if (!enforcementAPI.isExecutingControlAction() && e != null) {

			enforcementAPI.setExecutingControlAction(true);
			
			res=enforcementAPI.enforceElasticityCapability(capability, e);
			RuntimeLogger.logger.info("Answer from enforcement plugin with regard to enforcement successful completion is "+res);
			Node controlService = enforcementAPI.getControlledService();
			for (EnforcementAPI api : enforcementAPIs.values()) {
				api.refreshControlService(controlService);
			}
			monitoringAPIInterface.enforcingActionEnded(capability.getName()
					+ " - " + target, e);
                                       //         monitoringAPIInterface.refreshCompositionRules();

			if (res)
			try {
				Thread.sleep(60000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			enforcementAPI.setExecutingControlAction(false);
		}
		return res;
	}

	@Override
	public boolean enforceAction (String target, String actionName, Node node,
			Object[] parameters) {
		
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		RuntimeLogger.logger.info("----------------------Trying to "+actionName+" on "+ target + " "+node+" params ");
		boolean res=false;

		

		if (!enforcementAPI.isExecutingControlAction() && node != null) {
			if (!actionName.toLowerCase().contains("scalein")||(actionName.toLowerCase().contains("scalein") && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP).size()>1)  ){
				enforcementAPI.setExecutingControlAction(true);
				RuntimeLogger.logger.info("----------------------Enforcing "+actionName+" on "+ target + " "+node.getId()+" params "+parameters.length);
				monitoringAPIInterface.enforcingActionStarted(actionName
						+ " - " + target, node);

				res=enforcementAPI.enforceAction(target, actionName, node, parameters);
				//RuntimeLogger.logger.info("Answer from enforcement plugin with regard to enforcement successful completion is "+res);

				Node controlService = enforcementAPI.getControlledService();
				for (EnforcementAPI api : enforcementAPIs.values()) {
					api.refreshControlService(controlService);
				}
				monitoringAPIInterface.enforcingActionEnded(actionName + " - "
						+ target, node);
                           //    monitoringAPIInterface.refreshCompositionRules();
                                         
				if (res)
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
                                
				enforcementAPI.setExecutingControlAction(false);

			}
			
		}
		return res;
	}
        public List<String> getPluginsExecutingActions(){
            List<String> pluginsExec = new ArrayList<String>();
            for (Entry<String, EnforcementAPI> enforcementAPI:enforcementAPIs.entrySet()){
                if (enforcementAPI.getValue().isExecutingControlAction()){
                    pluginsExec.add(enforcementAPI.getKey());
                }
            }
            return pluginsExec;
        }

    @Override
    public boolean enforceAction(double violationDegree, String target, String actionName, Node node, Object[] parameters) {
        
		EnforcementAPI enforcementAPI = enforcementAPIs.get(target);
		RuntimeLogger.logger.info("----------------------Trying to "+actionName+" on "+ target + " "+node+" params ");
		boolean res=false;

		

		if (!enforcementAPI.isExecutingControlAction() && node != null) {
			if (!actionName.toLowerCase().contains("scalein")||(actionName.toLowerCase().contains("scalein") && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP).size()>1)  ){
				enforcementAPI.setExecutingControlAction(true);
				RuntimeLogger.logger.info("----------------------Enforcing "+actionName+" on "+ target + " "+node.getId()+" params "+parameters.length);
				monitoringAPIInterface.enforcingActionStarted(actionName
						+ " - " + target, node);

				res=enforcementAPI.enforceAction(violationDegree,target, actionName, node, parameters);
				//RuntimeLogger.logger.info("Answer from enforcement plugin with regard to enforcement successful completion is "+res);

				Node controlService = enforcementAPI.getControlledService();
				for (EnforcementAPI api : enforcementAPIs.values()) {
					api.refreshControlService(controlService);
				}
				monitoringAPIInterface.enforcingActionEnded(actionName + " - "
						+ target, node);
                              // monitoringAPIInterface.refreshCompositionRules();
                                         
				if (res)
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
                                
				enforcementAPI.setExecutingControlAction(false);

			}
			
		}
		return res;
    }

    @Override
    public void undeployService(Node service) {
         for (Entry<String, EnforcementAPI> enforcementAPI:enforcementAPIs.entrySet()){
                if (enforcementAPI.getValue().isExecutingControlAction()){
                   enforcementAPI.getValue().undeployService(service);
                }
            }
    }
    
    
}
