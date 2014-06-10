package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.SortedMap;
import java.util.TreeMap;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffects;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

public class PlanningHeuristicSearch implements PlanningAlgorithmInterface  {

	private int  REFRESH_PERIOD = 0;
	private DependencyGraph dependencyGraph= null;
	private MonitoringAPIInterface monitoringAPI=null;
	private EnforcementAPIInterface enforcementAPI=null;
	private Thread currentThread=null;
	private SortedMap<Double, ContextRepresentation> searchContext;
	private double LAMBDA =0.0;
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
		double bestContext= searchContext.firstKey();
		if (bestContext>dependencyGraph.getAllElasticityRequirements().size()/6 && searchContext.size()>1){
			
		ContextRepresentation contextRepresentation=searchContext.get(bestContext);
		searchContext.remove(bestContext);
		for (List<ActionEffect> list : actionEffects.values()) {
			for (ActionEffect action: list){
				MonitoredCloudService monitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
				ContextRepresentation beforeActionContextRepresentation = new ContextRepresentation(monitoredCloudService, monitoringAPI);
				contextRepresentation.setPREVIOUS_CS_UNHEALTHY_STATE(contextRepresentation.getCS_UNHEALTHY_STATE());
				contextRepresentation.doAction(action);
				double CS_UNHEALTHY_STATE=evaluateUnhealthyState(beforeActionContextRepresentation,contextRepresentation);
				contextRepresentation.addActionToContext(action);
				if (CS_UNHEALTHY_STATE<contextRepresentation.getPREVIOUS_CS_UNHEALTHY_STATE())
					searchContext.put(CS_UNHEALTHY_STATE,contextRepresentation );
				contextRepresentation.undoAction(action);
			}
		}
		recursiveBranchAndBoundEvaluation();
		}
	}
	public void enforceFoundActions(ContextRepresentation contextRepresentation){
		for (ActionEffect actionEffect:contextRepresentation.getActionsAssociatedToContext()){
			if (actionEffect.getActionType().equalsIgnoreCase("scaleout")) {
				enforcementAPI.scaleout(actionEffect.getTargetedEntity());
			} else {
				if (actionEffect.getActionType().equalsIgnoreCase("scalein")) {
					enforcementAPI.scalein(actionEffect.getTargetedEntity());	
				}else{
					enforcementAPI.enforceAction(actionEffect.getActionName(), actionEffect.getTargetedEntity());
				}
		}
			
		}
	}
	@Override
	public void run() {
		while (true){
			ContextRepresentation contextRepresentation = new ContextRepresentation(dependencyGraph,monitoringAPI);
			int continuousViolatedReq=0;
			double CS_UNHEALTHY_STATE=LAMBDA*continuousViolatedReq+(contextRepresentation.countViolatedConstraints());
			contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
			contextRepresentation.setPREVIOUS_CS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
			SortedMap<Double, ContextRepresentation> searchContext = new TreeMap();
			searchContext.put(contextRepresentation.getCS_UNHEALTHY_STATE(), contextRepresentation);
			recursiveBranchAndBoundEvaluation();
			enforceFoundActions(searchContext.get(searchContext.firstKey()));
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
