/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.planningEngine;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffects;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class PlanningHeuristicSearchWithPolynomialElasticityRelationships implements PlanningAlgorithmInterface{
      private int REFRESH_PERIOD = 0;
    private DependencyGraph dependencyGraph = null;
    private MonitoringAPIInterface monitoringAPI = null;
    private ContextRepresentation contextRepresentation = null;
    private EnforcementAPIInterface enforcementAPI = null;
    private Thread currentThread = null;
    private SortedMap<Double, List<ActionEffect>> searchContext = new TreeMap<Double, List<ActionEffect>>();
    private double LAMBDA = 1.0;

    public PlanningHeuristicSearchWithPolynomialElasticityRelationships(DependencyGraph cloudService,
            MonitoringAPIInterface monitoringAPI, EnforcementAPIInterface enforcementAPI) {
        this.dependencyGraph = cloudService;
        this.monitoringAPI = monitoringAPI;
        this.enforcementAPI = enforcementAPI;

        REFRESH_PERIOD = Configuration.getRefreshPeriod();
        currentThread = new Thread(this);
    }

    public void stop() {
          boolean ok= false;
        while (!ok){
            if (enforcementAPI.getPluginsExecutingActions().size()>0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PlanningGreedyAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                ok=true;
            }
        }
        currentThread.stop();
    }

    public void start() {
        currentThread.start();
    }
    //recursive 

    public void evaluationStep() {
        // current evaluation
        //
    }

    public double evaluateUnhealthyState(ContextRepresentation previousContextRepresentation, ContextRepresentation contextRepresentation) {
        //Todo replace continuosViolation with computation based on ADVISE
        int continuousViolatedReq = 0;
        //Lambda discount factor for continuous violations in time due to the enforcement 

        double CS_UNHEALTHY_STATE = LAMBDA * continuousViolatedReq + (contextRepresentation.countFixedStrategies(previousContextRepresentation) + contextRepresentation.countViolatedConstraints());
        contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
        return CS_UNHEALTHY_STATE;
    }

    public void checkInstantiation() {
        List<Relationship> instantiationRelationships = dependencyGraph.getAllRelationshipsOfType(Relationship.RelationshipType.INSTANTIATION);
        //for
    }

    public ActionEffect checkActions(String target) {
        HashMap<String, List<ActionEffect>> actionEffects = ActionEffects.getActionEffects();
        int maxConstraints = 0;
        ActionEffect maxConstraintsAction = null;
        PlanningLogger.logger.info("~~~~~~~~~~~Evaluating complimentary actions for " + target);

        for (List<ActionEffect> actionEffect : actionEffects.values()) {
            for (ActionEffect effect : actionEffect) {
                if (effect.getAffectedNodes().contains(target)) {
                    int beforeConstraints = contextRepresentation.countViolatedConstraints();
                    MonitoredCloudService monitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
                    ContextRepresentation beforeContext = new ContextRepresentation(monitoredCloudService, monitoringAPI);
                    contextRepresentation.doAction(effect);
                    int improvedStrategies = contextRepresentation.countFixedStrategies(beforeContext);
                    int afterConstraints = contextRepresentation.countViolatedConstraints();
                    PlanningLogger.logger.info("With " + effect.getActionType() + " on " + effect.getTargetedEntityID() + improvedStrategies + " and constraints  " + (beforeConstraints - afterConstraints) + " violated constraints " + contextRepresentation.getViolatedConstraints());
                    contextRepresentation.undoAction(effect);
                    if (beforeConstraints - afterConstraints + improvedStrategies > maxConstraints && (beforeConstraints - afterConstraints + improvedStrategies) > 0) {
                        maxConstraints = beforeConstraints - afterConstraints + improvedStrategies;
                        maxConstraintsAction = effect;
                    }
                }
            }
        }

        if (maxConstraintsAction != null) {
            PlanningLogger.logger.info("Returning " + maxConstraintsAction.getActionType() + " on " + maxConstraintsAction.getTargetedEntityID());
        } else {
            PlanningLogger.logger.info("Returning null ");
        }
        return maxConstraintsAction;
    }

    public void recursiveBranchAndBoundEvaluation() {

        HashMap<String, List<ActionEffect>> actionEffects = ActionEffects.getActionEffects();
        //ToDo - order actions units according to violated requirements
        if (searchContext.size() > 0) {
            double bestContext = searchContext.firstKey();

            if (bestContext > 0 && searchContext.size() > 0) {

                List<ActionEffect> actionsSoFar = searchContext.get(bestContext);
                for (ActionEffect actionEffect : actionsSoFar) {
                    contextRepresentation.doAction(actionEffect);
                }
                double prev = contextRepresentation.getCS_UNHEALTHY_STATE();

                searchContext.remove(bestContext);
                for (List<ActionEffect> list : actionEffects.values()) {
                    for (ActionEffect action : list) {
                        MonitoredCloudService monitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();

                        ContextRepresentation beforeActionContextRepresentation = new ContextRepresentation(monitoredCloudService, monitoringAPI);
                        ArrayList<ActionEffect> actions = new ArrayList<ActionEffect>();
                        actions.addAll(actionsSoFar);
                        contextRepresentation.doAction(action);
                        actions.add(action);

                        double unhealthy = contextRepresentation.getCS_UNHEALTHY_STATE();
                        double CS_UNHEALTHY_STATE = evaluateUnhealthyState(beforeActionContextRepresentation, contextRepresentation);
                        contextRepresentation.addActionToContext(action);
                        contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
                        if (CS_UNHEALTHY_STATE < contextRepresentation.getPREVIOUS_CS_UNHEALTHY_STATE()) {
                            searchContext.put(CS_UNHEALTHY_STATE, actions);
                        }


                        /**
                         * ##################################### Explore actions to compensate data impact ###################################################
                         */
                        List<String> targets = contextRepresentation.simulateDataImpact(beforeActionContextRepresentation, action);
                        for (String target : targets) {
                            ActionEffect dataAction = checkActions(target);
                            if (dataAction != null) {
                                MonitoredCloudService newMonitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
                                ContextRepresentation beforeContext = new ContextRepresentation(newMonitoredCloudService, monitoringAPI);
                                int beforeC = contextRepresentation.countViolatedConstraints();
                                contextRepresentation.doAction(dataAction);
                                actions.add(dataAction);
                                double unhealthy_Compl = contextRepresentation.getCS_UNHEALTHY_STATE();
                                double CS_UNHEALTHY_STATE_Compl = evaluateUnhealthyState(beforeContext, contextRepresentation);
                                contextRepresentation.addActionToContext(dataAction);
                                contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE_Compl);
                                //PlanningLogger.logger.info("PlanningAlgorithm: Trying the action due to DATA " + dataAction.getActionName() + "constraints violated : " + contextRepresentation.getViolatedConstraints() + " Strategies improved " + contextRepresentation.getImprovedStrategies(beforeActionContextRepresentation, strategiesThatNeedToBeImproved));
                                 contextRepresentation.addActionToContext(action);
                                    contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE_Compl);
                                    if (CS_UNHEALTHY_STATE_Compl < CS_UNHEALTHY_STATE) {
                                        searchContext.put(CS_UNHEALTHY_STATE, actions);
                                    }
                              
                                contextRepresentation.undoAction(dataAction);
                            }
                        }
                        contextRepresentation.undoDataImpactSimulation(beforeActionContextRepresentation, action);

                        /****
                         * ########################################  Explore actions to compensate load impact #################################################
                        */
                        targets = contextRepresentation.simulateLoadImpact(beforeActionContextRepresentation, action);
                        for (String target : targets) {
                            ActionEffect dataAction = checkActions(target);
                            if (dataAction != null) {
                                MonitoredCloudService newMonitoredCloudService = contextRepresentation.getMonitoredCloudService().clone();
                                ContextRepresentation beforeContext = new ContextRepresentation(newMonitoredCloudService, monitoringAPI);
                                int beforeC = contextRepresentation.countViolatedConstraints();
                                contextRepresentation.doAction(dataAction);
                                actions.add(dataAction);
                                double unhealthy_Compl = contextRepresentation.getCS_UNHEALTHY_STATE();
                                double CS_UNHEALTHY_STATE_Compl = evaluateUnhealthyState(beforeContext, contextRepresentation);
                                contextRepresentation.addActionToContext(dataAction);
                                contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE_Compl);
                                //PlanningLogger.logger.info("PlanningAlgorithm: Trying the action due to DATA " + dataAction.getActionName() + "constraints violated : " + contextRepresentation.getViolatedConstraints() + " Strategies improved " + contextRepresentation.getImprovedStrategies(beforeActionContextRepresentation, strategiesThatNeedToBeImproved));
                                 contextRepresentation.addActionToContext(action);
                                    contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE_Compl);
                                    if (CS_UNHEALTHY_STATE_Compl < CS_UNHEALTHY_STATE) {
                                        searchContext.put(CS_UNHEALTHY_STATE, actions);
                                    }
                              
                                contextRepresentation.undoAction(dataAction);
                            }
                        }
                        contextRepresentation.undoLoadImpactSimulation(beforeActionContextRepresentation, action);
                        
                        /*
                         * ############################Actions to compensate complex relationships (MELA Discovered) #################################
                         */
                        
                        
                        
                        contextRepresentation.undoAction(action);

                        contextRepresentation.setCS_UNHEALTHY_STATE(unhealthy);

                    }
                }
                contextRepresentation.setPREVIOUS_CS_UNHEALTHY_STATE(prev);
                int i = actionsSoFar.size() - 1;
                while (i > 0) {
                    contextRepresentation.undoAction(actionsSoFar.get(i));
                    i--;
                }
                recursiveBranchAndBoundEvaluation();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
                        if (dependencyGraph.isInControlState()){

            Node cloudService = monitoringAPI.getControlledService();

            dependencyGraph.setCloudService(cloudService);
            contextRepresentation = new ContextRepresentation(dependencyGraph, monitoringAPI);
            contextRepresentation.initializeContext();

            int continuousViolatedReq = 0;
            PlanningLogger.logger.info("Violated requirements = " + contextRepresentation.countViolatedConstraints());
            double CS_UNHEALTHY_STATE = LAMBDA * continuousViolatedReq + (contextRepresentation.countViolatedConstraints());
            contextRepresentation.setCS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
            contextRepresentation.setPREVIOUS_CS_UNHEALTHY_STATE(CS_UNHEALTHY_STATE);
            searchContext = new TreeMap<Double, List<ActionEffect>>();
            ArrayList<ActionEffect> actionEffects = new ArrayList<ActionEffect>();
            searchContext.put(CS_UNHEALTHY_STATE, actionEffects);
            recursiveBranchAndBoundEvaluation();
            ActionPlanEnforcement actionPlanEnforcement = new ActionPlanEnforcement(enforcementAPI);
            ArrayList<ContextRepresentation.Pair<ActionEffect, Integer>> res = new ArrayList<ContextRepresentation.Pair<ActionEffect, Integer>>();
            if (searchContext.size() > 0 && searchContext.firstKey() != null) {
                for (ActionEffect actionEffect : searchContext.get(searchContext.firstKey())) {
                    actionEffect.setTargetedEntity(dependencyGraph.getNodeWithID(actionEffect.getTargetedEntityID()));
                    res.add(contextRepresentation.new Pair<ActionEffect, Integer>(actionEffect, 1));
                }
            }
            if (res.size() > 0) {
                actionPlanEnforcement.enforceResult(res, dependencyGraph);
            }
            try {
                currentThread.sleep(REFRESH_PERIOD);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }}

    }

    @Override
    public void setEffects(String effects) {
        // TODO Auto-generated method stub
        ActionEffects.setActionEffects(effects);
    }
}
