/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.planningEngine.adviseEffects;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.EventNotification;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.ServicePartMonitor;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.RecordedInfoProcessing;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ComputeBehavior;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ECPBehavioralModel;
import at.ac.tuwien.dsg.rSybl.planningEngine.ActionPlanEnforcement;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningAlgorithmInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningGreedyAlgorithmWithPolynomialElasticityRelationships;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class PlanningGreedyWithADVISE implements PlanningAlgorithmInterface {

    private List<ContextRepresentation> withoutEnforcing = new LinkedList<ContextRepresentation>();
    private ContextRepresentation initialContext;
    private Node cloudService;
    private DependencyGraph dependencyGraph;
    private MonitoringAPIInterface monitoringInterface;
    private LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> measurements = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>();
    private int POLYNOMIAL_EQUATION_DEGREE = 3;
    private LinkedHashMap<ElasticityCapability, Double> expectedOverallEffect = new LinkedHashMap<>();
    private LinkedHashMap<ElasticityCapability, Double> expectedFinalEffect = new LinkedHashMap<>();
    private double noActionOverallViolatedConstraints = 0;
    private double noActionFinalViolatedConstraints = 0;
    private int MINUTES_TO_WAIT_AFTER_ABNORMAL_DISTANCE = 5;
    private Timer timer = new Timer();
    private EnforcementAPIInterface enforcementAPI;
    private ComputeBehavior behavior;
    private Date timeITWasNotOK;
    private PlanningAlgorithmInterface initialPlanning;
    private boolean planning = false;
        private EventNotification eventNotification;

    public PlanningGreedyWithADVISE(MonitoringAPIInterface monitoringAPIInterface, Node cloudService, EnforcementAPIInterface enforcementAPI, PlanningAlgorithmInterface mainPlanning) {
        monitoringInterface = monitoringAPIInterface;
        this.cloudService = cloudService;
        initialPlanning = mainPlanning;
        this.enforcementAPI = enforcementAPI;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        this.eventNotification = EventNotification.getEventNotification();
        
        initialContext = new ContextRepresentation(cloudService, new LinkedHashMap<String, LinkedHashMap<String, Double>>());
        createFile();
    }
    public void startLearningProcess() {
        behavior = new ComputeBehavior(cloudService, monitoringInterface);

    }
    public ElasticityCapability findCapabilityToEnforce() {
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        boolean sufficientInfo = true;
        double constraintsViolatedWithout = 0;
        ContextEvaluation contextEvaluation = new ContextEvaluation();
        for (ContextRepresentation contextRepresentation : withoutEnforcing) {
            constraintsViolatedWithout += contextEvaluation.evaluateViolationDegree(dependencyGraph, contextRepresentation);
        }
        constraintsViolatedWithout /= withoutEnforcing.size() * 1.0;
        if (constraintsViolatedWithout > 0) {
            ElasticityCapability elasticityCapability = null;
            double minViolatedConstraints = 100000000.0;
            double minViolatedFinalConstraints = 100000000.0;
            ElasticityCapability overallEc = null;
            ElasticityCapability finalEc = null;
            List<MonitoringSnapshot> snapshots = monitoringInterface.getAllMonitoringInformationOnPeriod(ECPBehavioralModel.CHANGE_INTERVAL);
            String justification="";
//                       double diff1 =0 ;
//                double diff2 = 0;
           boolean foundNotPredictable=false;
            for (ElasticityCapability ec : dependencyGraph.getAllElasticityCapabilities()) {

                ECEnforcementEffect ecEnforcementEffect = new ECEnforcementEffect(behavior, cloudService, monitoringInterface, ec,snapshots);
                expectedOverallEffect.put(ec, ecEnforcementEffect.overallViolatedConstraints());
                expectedFinalEffect.put(ec, ecEnforcementEffect.getFinalStateViolatedConstraints());
                justification+=", "+ec.getName()+", overall:"+expectedOverallEffect.get(ec)+", final"+expectedFinalEffect.get(ec);
        //        if (expectedFinalEffect.get(ec) == ecEnforcementEffect.MAX_CONSTRAINTS) {
//                    sufficientInfo = false;
//                    timeITWasNotOK = new Date();
//                    stop();
//                    initialPlanning.takeMainRole();
          //          writeJustification(justification+"returning to Greedy control \n", fileName);
          //          return null;

            //    }
     
                if (constraintsViolatedWithout > 0 && expectedOverallEffect.get(ec) < constraintsViolatedWithout && expectedOverallEffect.get(ec)!=ECEnforcementEffect.MAX_CONSTRAINTS) {
                    if (minViolatedConstraints > expectedOverallEffect.get(ec)) {
//                         diff1= minViolatedConstraints-expectedOverallEffect.get(ec);
                        minViolatedConstraints = expectedOverallEffect.get(ec);
                        overallEc = ec;
                    }
                }
                 if (constraintsViolatedWithout > 0 && expectedFinalEffect.get(ec) < constraintsViolatedWithout && expectedFinalEffect.get(ec)!=ECEnforcementEffect.MAX_CONSTRAINTS) {
                if (minViolatedFinalConstraints>expectedFinalEffect.get(ec)) {
//                                             diff2= minViolatedConstraints-expectedFinalEffect.get(ec);

                    minViolatedFinalConstraints = expectedFinalEffect.get(ec);
                    finalEc = ec;
                }
                 }
            }
            if (sufficientInfo) {
                
                if (constraintsViolatedWithout > 0 && null != overallEc ) {
                    elasticityCapability = overallEc;
                    writeJustification(justification, elasticityCapability.getName());
                    return elasticityCapability;
                }
//                if (constraintsViolatedWithout > 0 && null != finalEc ) {
//                    elasticityCapability = finalEc;
//                    writeJustification(justification, elasticityCapability.getName());
//
//                    return elasticityCapability;
//                }
            } }else {
                LinkedHashMap<ElasticityCapability, Double> strategies = new LinkedHashMap<ElasticityCapability, Double>();
                double maxImprovedStrategies = 0;
                ElasticityCapability forStrategiesEC = null;
                boolean foundNotPredictable = false;
                            List<MonitoringSnapshot> snapshots = monitoringInterface.getAllMonitoringInformationOnPeriod(ECPBehavioralModel.CHANGE_INTERVAL);
            String justification="";
double diff=-10;
                for (ElasticityCapability ec : dependencyGraph.getAllElasticityCapabilities()) {
                    ECEnforcementEffect eCEnforcementEffect = new ECEnforcementEffect(behavior, cloudService, monitoringInterface, ec,snapshots);
                    if (eCEnforcementEffect.getImprovedStrategies(initialContext)!=ECEnforcementEffect.MAX_CONSTRAINTS && eCEnforcementEffect.getFinalStateViolatedConstraints()!=ECEnforcementEffect.MAX_CONSTRAINTS)
                    strategies.put(ec, eCEnforcementEffect.getImprovedStrategies(initialContext)-eCEnforcementEffect.getFinalStateViolatedConstraints());
                    else
                        strategies.put(ec, eCEnforcementEffect.MAX_CONSTRAINTS);
                justification+=", "+ec.getName()+", overall:"+strategies.get(ec)+", final"+eCEnforcementEffect.getImprovedStrategies(initialContext)+" final violated constraints "+eCEnforcementEffect.getFinalStateViolatedConstraints();

                    if (strategies.get(ec)==ECEnforcementEffect.MAX_CONSTRAINTS){
                        foundNotPredictable=true;
                    }
                    if (maxImprovedStrategies < strategies.get(ec)) {
                        diff= strategies.get(ec)-maxImprovedStrategies;
                        maxImprovedStrategies = strategies.get(ec);
                       
                        forStrategiesEC = ec;
                        
                    }
                    
                }
                
                if (!foundNotPredictable && forStrategiesEC != null && constraintsViolatedWithout == 0 && diff>0.2) {
                    writeJustification(justification, forStrategiesEC.getName());

                    return forStrategiesEC;
                }

            }
        
        return null;
    }
    private Date currentDate = new Date();
    private String fileName = "./reporting/decisionJustification"+currentDate.getDay()+"Feb_time_"+currentDate.getHours()+"_"+currentDate.getMinutes()+".csv";
    public void createFile(){
                  File theDir = new File("./reporting");

  // if the directory does not exist, create it
  if (!theDir.exists()) {
    System.out.println("creating directory: " + theDir.getName());
    boolean result = false;

    try{
        theDir.mkdir();
        result = true;
     } catch(SecurityException se){
        //handle it
     }        
     if(result) {    
       System.out.println("DIR created");  
     }
  }
		try{
		 FileWriter fstream = new FileWriter(fileName);
		  String headers = "Time ,";		 
		  headers +=" evaluation, decision";
		  headers += "\n";
		  fstream.write(headers);
		  fstream.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
              
            
    }
public void writeJustification(String justification, String decision){
               FileWriter fstream=null;
                try {
                    fstream = new FileWriter(fileName,true);
                } catch (IOException ex) {
                    Logger.getLogger(RecordedInfoProcessing.class.getName()).log(Level.SEVERE, null, ex);
                }
				  String toWrite = new Date()+","+justification+", decision: "+decision;
				
                try {
                    fstream.write(toWrite+'\n');
                      fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(RecordedInfoProcessing.class.getName()).log(Level.SEVERE, null, ex);
                }
}
    public boolean checkWhetherPerformanceIsAcceptable() {
        double stdDevSum = 0;
        double bigNb = 1000000;

        int nbActions = 0;
        if (timeITWasNotOK != null) {
            Date now = new Date();

            long diff = now.getTime() - this.timeITWasNotOK.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            if (diffMinutes < MINUTES_TO_WAIT_AFTER_ABNORMAL_DISTANCE) {
                return false;
            }
        }

        for (ElasticityCapability capability : dependencyGraph.getAllElasticityCapabilities()) {
            if (behavior.avgActionTime(capability, dependencyGraph.getNodeWithID(capability.getServicePartID())) > 0) {
                stdDevSum += behavior.stdDevActionTime(capability, dependencyGraph.getNodeWithID(capability.getServicePartID()));
                nbActions += 1;
            } else {
                stdDevSum += bigNb;
                nbActions += 1;

            }
        }
        if (stdDevSum / nbActions < bigNb) {
            return true;
        } else {
            return false;
        }

    }

    public void initializeContexts() {
        withoutEnforcing = new LinkedList<ContextRepresentation>();
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
                         //   measurements.get(SP).put(recording.getKey(), nDimPoint);
                        }

                        measurements.get(SP).get(recording.getKey()).add(recording.getValue());
                        //measurements.get(SP).get(recording.getKey()).add(recording.getValue());
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
                    expectedValues.get(node).get(metric).add(samplePoints[observations.length - 1]);
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

    public void discoverPlan() {
        planning = true;
        initializeContexts();
        ElasticityCapability capability = findCapabilityToEnforce();
        if (capability != null) {
            ActionPlanEnforcement actionPlanEnforcement = new ActionPlanEnforcement(enforcementAPI);
            actionPlanEnforcement.enforceElasticityCapability(dependencyGraph, capability);
        }
        planning=false;
    }

    @Override
    public void run() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!planning){
                discoverPlan();
                }
            }
        }, 0, Configuration.getRefreshPeriod());
    }

    @Override
    public void takeMainRole() {
        start();
    }
}
