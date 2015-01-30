/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.planningEngine.adviseEffects;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.ServicePartMonitor;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ComputeBehavior;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ECPBehavioralModel;
import at.ac.tuwien.dsg.rSybl.planningEngine.ActionPlanEnforcement;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningAlgorithmInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningGreedyAlgorithmWithPolynomialElasticityRelationships;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Georgiana
 */
public class PlanningGreedyWithADVISE implements PlanningAlgorithmInterface {

    private List<ContextRepresentation> withoutEnforcing = new LinkedList<ContextRepresentation>();
    private ContextRepresentation initialContext ;
    private Node cloudService;
    private DependencyGraph dependencyGraph;
    private MonitoringAPIInterface monitoringInterface;
    private LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> measurements = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>();
    private int POLYNOMIAL_EQUATION_DEGREE = 3;
    private LinkedHashMap<ElasticityCapability, Double> expectedOverallEffect = new LinkedHashMap<>();
    private LinkedHashMap<ElasticityCapability, Double> expectedFinalEffect = new LinkedHashMap<>();
    private double noActionOverallViolatedConstraints = 0;
    private double noActionFinalViolatedConstraints = 0;
    private int MINUTES_TO_WAIT_AFTER_ABNORMAL_DISTANCE = 20;
    private Timer timer = new Timer();
    private EnforcementAPIInterface enforcementAPI;
    private ComputeBehavior behavior;
    private Date timeITWasNotOK;
    private PlanningAlgorithmInterface initialPlanning;
    public PlanningGreedyWithADVISE(MonitoringAPIInterface monitoringAPIInterface, Node cloudService, EnforcementAPIInterface enforcementAPI, PlanningAlgorithmInterface mainPlanning) {
        monitoringInterface = monitoringAPIInterface;
        this.cloudService = cloudService;
        initialPlanning = mainPlanning;
        this.enforcementAPI = enforcementAPI;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        initialContext=new ContextRepresentation(cloudService, new LinkedHashMap<String, LinkedHashMap<String, Double>>());
    }

    public void startLearningProcess() {
        behavior = new ComputeBehavior(cloudService, monitoringInterface);

    }

    public ElasticityCapability findCapabilityToEnforce() {
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        ElasticityCapability elasticityCapability = null;
        double minViolatedConstraints = 100000000.0;
        double minViolatedFinalConstraints = 100000000.0;
        ElasticityCapability overallEc = null;
        ElasticityCapability finalEc = null;
        boolean sufficientInfo = true;
        double constraintsViolatedWithout = 0;
        ContextEvaluation contextEvaluation = new ContextEvaluation();
        for (ContextRepresentation contextRepresentation : withoutEnforcing) {
            constraintsViolatedWithout += contextEvaluation.countViolatedConstraints(dependencyGraph, contextRepresentation);
        }
        constraintsViolatedWithout /= withoutEnforcing.size();
        for (ElasticityCapability ec : dependencyGraph.getAllElasticityCapabilities()) {
            
            ECEnforcementEffect ecEnforcementEffect = new ECEnforcementEffect(behavior, cloudService, monitoringInterface, ec);
            expectedOverallEffect.put(ec, ecEnforcementEffect.overallViolatedConstraints());
            expectedFinalEffect.put(ec, ecEnforcementEffect.getFinalStateViolatedConstraints());
           if (expectedFinalEffect.get(ec)==ecEnforcementEffect.MAX_CONSTRAINTS){
               sufficientInfo=false;
               timeITWasNotOK = new Date();
               stop();
               break;
           }
            if (constraintsViolatedWithout > 0 && expectedOverallEffect.get(ec) < constraintsViolatedWithout) {
                if (minViolatedConstraints > expectedOverallEffect.get(ec)) {
                    minViolatedConstraints = expectedOverallEffect.get(ec);
                    overallEc = ec;
                }
            }
            if (constraintsViolatedWithout > 0 && minViolatedFinalConstraints > expectedFinalEffect.get(ec)) {
                minViolatedFinalConstraints = expectedFinalEffect.get(ec);
                finalEc = ec;
            }

        }
        if (sufficientInfo){
        if (constraintsViolatedWithout > 0 && overallEc == finalEc) {
            elasticityCapability = overallEc;
            return elasticityCapability;
        }
        LinkedHashMap<ElasticityCapability, Double> strategies = new LinkedHashMap<ElasticityCapability, Double>();
        double maxImprovedStrategies = 0;
        ElasticityCapability forStrategiesEC = null;
        for (ElasticityCapability ec : dependencyGraph.getAllElasticityCapabilities()) {
            ECEnforcementEffect eCEnforcementEffect = new ECEnforcementEffect(behavior, cloudService, monitoringInterface, ec);
            strategies.put(ec, eCEnforcementEffect.getImprovedStrategies(initialContext));
            if (maxImprovedStrategies < strategies.get(ec)) {
                maxImprovedStrategies = strategies.get(ec);
                forStrategiesEC = ec;
            }
        }
        if (finalEc == forStrategiesEC) {
            return forStrategiesEC;
        }

        return elasticityCapability;
        }
        return null;
    }

    public boolean checkWhetherPerformanceIsAcceptable() {
        double stdDevSum = 0;
        double bigNb = 1000000;
        
        int nbActions=0;
        if (timeITWasNotOK!=null){
        Date now = new Date();
        
         long diff = now.getTime() - this.timeITWasNotOK.getTime();
        long diffMinutes = diff / (60 * 1000) % 60;
        if (diffMinutes<MINUTES_TO_WAIT_AFTER_ABNORMAL_DISTANCE){
            return false;
        }
        }
        
        for (ElasticityCapability capability : dependencyGraph.getAllElasticityCapabilities()) {
            if (behavior.avgActionTime(capability, dependencyGraph.getNodeWithID(capability.getServicePartID())) > 0) {
                stdDevSum += behavior.stdDevActionTime(capability,  dependencyGraph.getNodeWithID(capability.getServicePartID()));
                nbActions +=1;
            } else {
                stdDevSum += bigNb;
                nbActions +=1;

            }
        }
        if (stdDevSum/nbActions < bigNb) {
            return true;
        } else {
            return false;
        }

    }

    public void initializeContexts() {
        List<MonitoringSnapshot> snapshots = monitoringInterface.getAllMonitoringInformationOnPeriod(ECPBehavioralModel.CHANGE_INTERVAL);
        if (snapshots.size() > 0) {

            MonitoringSnapshot snapshot = snapshots.get(snapshots.size() - 1);
            for (String SP : snapshot.getServiceParts().keySet()) {
                if (!measurements.containsKey(SP)) {
                    LinkedHashMap<String, LinkedList<Double>> metricsWithPoints = new LinkedHashMap<>();
                    measurements.put(SP, metricsWithPoints);
                }
                for (ServicePartMonitor monitor : snapshot.getServiceParts().values()) {
                    for (Map.Entry<String, Double> recording : monitor.getMetrics().entrySet()) {

                        if (!measurements.get(SP).containsKey(recording.getKey())) {
                            LinkedList<Double> nDimPoint = new LinkedList<Double>();
                            measurements.get(SP).put(recording.getKey(), nDimPoint);
                            measurements.get(SP).put(recording.getKey(), nDimPoint);
                        }

                        measurements.get(SP).get(recording.getKey()).add(recording.getValue());
                        measurements.get(SP).get(recording.getKey()).add(recording.getValue());
                    }
                }
            }

        }
        MonitoringSnapshot monitoringSnapshot = snapshots.get(snapshots.size() - 1);
        for (String node : monitoringSnapshot.getServiceParts().keySet()) {
            for (String metric : monitoringSnapshot.getServiceParts().get(node).getMetrics().keySet()) {
                initialContext.setMetricValue(node, metric, monitoringSnapshot.getServiceParts().get(node).getMetrics().get(metric));
            }
        }
        LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> expectedValues = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>();

        //find polinomial equations
        for (String node : measurements.keySet()) {
            if (!expectedValues.containsKey(node)) {
                expectedValues.put(node, new LinkedHashMap<String, LinkedList<Double>>());
            }
            for (String metric : measurements.get(node).keySet()) {
                if (!expectedValues.get(node).containsKey(metric)) {
                    expectedValues.get(node).put(metric, new LinkedList<Double>());
                }
                SimpleExpectedBehavior expectedBehavior = new SimpleExpectedBehavior(POLYNOMIAL_EQUATION_DEGREE);
                double[] observations = new double[measurements.get(node).get(metric).size()];
                double[] samplePoints = new double[measurements.get(node).get(metric).size()];
                for (int i = 0; i < observations.length; i++) {
                    observations[i] = i;
                    samplePoints[i] = measurements.get(node).get(metric).get(i);
                }
                expectedBehavior.fit(observations, samplePoints);
                double[] coef = expectedBehavior.getCoef();
                PlanningLogger.logger.info("Coefficients are " + coef.toString());
                for (int i = 0; i < ECPBehavioralModel.CHANGE_INTERVAL; i++) {
                    double value = 0.0;
                    for (int j = 0; j < coef.length; j++) {
                        value += coef[j] * Math.pow(i + ECPBehavioralModel.CHANGE_INTERVAL, j);
                    }
                    //expectedValues.get(node).get(metric).add(value);
                    expectedValues.get(node).get(metric).add(samplePoints[observations.length-1]);
                }
            }
        }
        LinkedHashMap<String, LinkedHashMap<String, Double>> metrics;
        ContextEvaluation contextEvaluation = new ContextEvaluation();
        DependencyGraph dependencyGraph = new DependencyGraph();
        noActionOverallViolatedConstraints = 0.0;
        noActionFinalViolatedConstraints = 0.0;
        dependencyGraph.setCloudService(cloudService);
        for (int i = 0; i < ECPBehavioralModel.CHANGE_INTERVAL; i++) {
            metrics = new LinkedHashMap<>();
            for (String node : expectedValues.keySet()) {
                if (!metrics.containsKey(node)) {
                    metrics.put(node, new LinkedHashMap<String, Double>());
                }
                for (String metric : expectedValues.get(node).keySet()) {
                    metrics.get(node).put(metric, expectedValues.get(node).get(metric).get(i));
                }
            }
            ContextRepresentation contextRepresentation = new ContextRepresentation(cloudService, metrics);
            withoutEnforcing.add(contextRepresentation);
            noActionOverallViolatedConstraints += contextEvaluation.countViolatedConstraints(dependencyGraph, contextRepresentation);
        }
        noActionOverallViolatedConstraints /= withoutEnforcing.size();
        noActionFinalViolatedConstraints = contextEvaluation.countViolatedConstraints(dependencyGraph, withoutEnforcing.get(withoutEnforcing.size() - 1));
    }

    @Override
    public void start() {
        run();
    }

    @Override
    public void stop() {
        timer.purge();
        timer.cancel();
    }

    @Override
    public void setEffects(String effects) {
    }

    @Override
    public void replaceDependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
    }
    public void discoverPlan(){
         initializeContexts();
                ElasticityCapability capability = findCapabilityToEnforce();
                if (capability!=null){
                ActionPlanEnforcement actionPlanEnforcement = new ActionPlanEnforcement(enforcementAPI);
                actionPlanEnforcement.enforceElasticityCapability(dependencyGraph, capability);
    }
    }
    @Override
    public void run() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    discoverPlan();
                
            }
        }, Configuration.getRefreshPeriod(), Configuration.getRefreshPeriod());
    }

    @Override
    public void takeMainRole() {
        start();
    }
}
