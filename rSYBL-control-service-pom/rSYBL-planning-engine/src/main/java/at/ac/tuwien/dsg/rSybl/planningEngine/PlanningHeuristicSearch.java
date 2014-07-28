package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.mela.common.monitoringConcepts.Action;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.ContextRepresentation.Pair;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffects;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import java.util.ArrayList;

public class PlanningHeuristicSearch implements PlanningAlgorithmInterface  {

	private int  REFRESH_PERIOD = 0;
	private DependencyGraph dependencyGraph= null;
	private MonitoringAPIInterface monitoringAPI=null;
        private ContextRepresentation contextRepresentation  = null;
	private EnforcementAPIInterface enforcementAPI=null;
	private Thread currentThread=null;
	private SortedMap<Double, List<ActionEffect>> searchContext = new TreeMap<Double,List<ActionEffect>>();
	private double LAMBDA =1.0;
	public PlanningHeuristicSearch(DependencyGraph cloudService,
			MonitoringAPIInterface monitoringAPI,EnforcementAPIInterface enforcementAPI) {
		this.dependencyGraph = cloudService;
		this.monitoringAPI = monitoringAPI;
	   this.enforcementAPI = enforcementAPI;
	   
	   REFRESH_PERIOD = Configuration.getRefreshPeriod();
	   currentThread = new Thread(this);
	}
	
	public void stop(){
		currentThread.stop();
	}
	public void start(){
		currentThread.start();
	}
	//recursive 
	public void evaluationStep(){
		// current evaluation
		
		//
	}
	public double evaluateUnhealthyState(ContextRepresentation previousContextRepresentation,ContextRepresentation contextRepresentation){
		//Todo replace continuosViolation with computation based on ADVISE
		int continuousViolatedReq=0;
		//Lambda discount factor for continuous violations in time due to the enforcement 
		
				double CS_UNHEALTHY_STATE=LAMBDA*continuousViolatedReq+(contextRepresentation.countFixedStrategies(previousContextRepresentation )+contextRepresentation.countViolatedConstraints());
				contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
				return CS_UNHEALTHY_STATE;
	}
	public void recursiveBranchAndBoundEvaluation(){
	
		HashMap<String, List<ActionEffect>> actionEffects = ActionEffects.getActionEffects();
		//ToDo - order actions units according to violated requirements
                if (searchContext.size()>0){
		double bestContext= searchContext.firstKey();
               
		if (bestContext>0 && searchContext.size()>0){
                
		List<ActionEffect> actionsSoFar=searchContext.get(bestContext);
                for (ActionEffect actionEffect:actionsSoFar){
                    contextRepresentation.doAction(actionEffect);
                }
                double prev=contextRepresentation.getCS_UNHEALTHY_STATE();
                              
		searchContext.remove(bestContext);
		for (List<ActionEffect> list : actionEffects.values()) {
			for (ActionEffect action: list){
				MonitoredCloudService monitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();

                                ContextRepresentation beforeActionContextRepresentation = new ContextRepresentation(monitoredCloudService, monitoringAPI);
                                ArrayList<ActionEffect> actions = new ArrayList<ActionEffect>();
                                actions.addAll(actionsSoFar);
				contextRepresentation.doAction(action);
                                actions.add(action);
                                
                                double unhealthy = contextRepresentation.getCS_UNHEALTHY_STATE();
				double CS_UNHEALTHY_STATE=evaluateUnhealthyState(beforeActionContextRepresentation,contextRepresentation);
				contextRepresentation.addActionToContext(action);
                                contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
				if (CS_UNHEALTHY_STATE<contextRepresentation.getPREVIOUS_CS_UNHEALTHY_STATE())
					searchContext.put(CS_UNHEALTHY_STATE,actions );
                                
				contextRepresentation.undoAction(action);
                                
                                contextRepresentation.setCS_UNHEALTHY_STATE(unhealthy);
                               
			}
		}
                contextRepresentation.setPREVIOUS_CS_UNHEALTHY_STATE(prev);
                int i = actionsSoFar.size()-1;
                while (i>0){
                    contextRepresentation.undoAction(actionsSoFar.get(i));
                    i--;
                }
		recursiveBranchAndBoundEvaluation();
		}
                }
	}
	@Override
	public void run() {
		while (true){
                         Node cloudService = monitoringAPI.getControlledService();

                        dependencyGraph.setCloudService(cloudService);
			contextRepresentation = new ContextRepresentation(dependencyGraph,monitoringAPI);
                        contextRepresentation.initializeContext();

			int continuousViolatedReq=0;
                        PlanningLogger.logger.info("Violated requirements = "+contextRepresentation.countViolatedConstraints());
			double CS_UNHEALTHY_STATE=LAMBDA*continuousViolatedReq+(contextRepresentation.countViolatedConstraints());
			contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
			contextRepresentation.setPREVIOUS_CS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
			searchContext = new TreeMap<Double,  List<ActionEffect>>();
                        ArrayList<ActionEffect> actionEffects = new ArrayList<ActionEffect>();
			searchContext.put(CS_UNHEALTHY_STATE, actionEffects);
			recursiveBranchAndBoundEvaluation();
			ActionPlanEnforcement actionPlanEnforcement = new ActionPlanEnforcement(enforcementAPI);
                        ArrayList<Pair<ActionEffect,Integer>> res =new ArrayList<Pair<ActionEffect,Integer>>();
                        if (searchContext.size()>0 && searchContext.firstKey()!=null)
                        for (ActionEffect actionEffect:searchContext.get(searchContext.firstKey())){
                           actionEffect.setTargetedEntity(dependencyGraph.getNodeWithID(actionEffect.getTargetedEntityID())); 
                           res.add(contextRepresentation.new Pair<ActionEffect,Integer>(actionEffect,1));
                        }
                        if (res.size()>0)
			actionPlanEnforcement.enforceResult(res, dependencyGraph);
		    try {
				currentThread.sleep(REFRESH_PERIOD);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void setEffects(String effects) {
		// TODO Auto-generated method stub
		ActionEffects.setActionEffects(effects);
	}
}
