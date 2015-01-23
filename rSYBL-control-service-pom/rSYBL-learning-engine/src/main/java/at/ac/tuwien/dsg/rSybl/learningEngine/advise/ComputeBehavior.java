/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.ServicePartMonitor;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.MyEntry;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;
import at.ac.tuwien.dsg.rSybl.learningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.learningEngine.utils.LearningLogger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author Georgiana
 */
public class ComputeBehavior {

    private final HashMap<String, HashMap<String, ECPBehavioralModel>> behaviors = new HashMap<>();
    private MonitoringAPIInterface monitoringInterface;
    private DependencyGraph dependencyGraph;
    private Timer reLearnTimer = new Timer();
    private int LEARNING_PERIOD = Configuration.getLearningPeriod();

    public ComputeBehavior(Node cloudService, MonitoringAPIInterface interface1) {
        monitoringInterface = interface1;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);

        initializeBehaviors();
//        reLearnTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                refreshBehaviors();
//            }
//        }, LEARNING_PERIOD, LEARNING_PERIOD);
    }
    public void initializeBehaviors()
    {
        List<MonitoringSnapshot> snapshots = null;
        while (snapshots==null || snapshots.size()==0){
       snapshots= monitoringInterface.getAllMonitoringInformation();
       if (snapshots==null || snapshots.size()==0){
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                LearningLogger.logger.info("Waiting to get monitoring information....");
            }}
        }
        Node cloudService=dependencyGraph.getCloudService();
        synchronized(behaviors){
                for (Node node : dependencyGraph.getAllServiceUnits()) {
            if (node.getElasticityCapabilities() != null && node.getElasticityCapabilities().size() > 0) {
                if (!behaviors.containsKey(node.getId())) {
                    behaviors.put(node.getId(), new HashMap<String, ECPBehavioralModel>());
                }
                for (ElasticityCapability capability : node.getElasticityCapabilities()) {
                    ECPBehavioralModel behavioralModel = new ECPBehavioralModel(cloudService, monitoringInterface);
                    behavioralModel.setCapability(capability);
                    
                    behaviors.get(node.getId()).put(capability.getName(), behavioralModel);
                }
            }
        }
        for (Node node : dependencyGraph.getAllServiceTopologies()) {
            if (node.getElasticityCapabilities() != null && node.getElasticityCapabilities().size() > 0) {
                if (!behaviors.containsKey(node.getId())) {
                    behaviors.put(node.getId(), new HashMap<String, ECPBehavioralModel>());
                }
                for (ElasticityCapability capability : node.getElasticityCapabilities()) {
                    behaviors.get(node.getId()).put(capability.getName(), new ECPBehavioralModel(cloudService, monitoringInterface));
                }
            }
        }
        if (cloudService.getElasticityCapabilities() != null && cloudService.getElasticityCapabilities().size() > 0) {
            if (!behaviors.containsKey(cloudService.getId())) {
                behaviors.put(cloudService.getId(), new HashMap<String, ECPBehavioralModel>());
            }
            for (ElasticityCapability capability : cloudService.getElasticityCapabilities()) {
                behaviors.get(cloudService.getId()).put(capability.getName(), new ECPBehavioralModel(cloudService, monitoringInterface));
            }
        }
         for (String node : behaviors.keySet()) {
                for (String ec : behaviors.get(node).keySet()) {
                    ECPBehavioralModel behavioralModel = behaviors.get(node).get(ec);
                    for (ElasticityCapability capability : dependencyGraph.getNodeWithID(node).getElasticityCapabilities()) {
                        if (capability.getName().equalsIgnoreCase(ec)) {
                            behavioralModel.setCapability(capability);
                            break;
                        }
                    }
                    behavioralModel.initializeBehaviorClusters(snapshots);
                    behavioralModel.refreshCorrelationMatrix();
                    behaviors.get(node).put(ec,behavioralModel);
                }
            }
        }
                reLearnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshBehaviors();
            }
        }, LEARNING_PERIOD, LEARNING_PERIOD);
    }
    public void refreshBehaviors() {
        synchronized(behaviors){
            for (String node : behaviors.keySet()) {
                for (String ec : behaviors.get(node).keySet()) {
                    ECPBehavioralModel behavioralModel = behaviors.get(node).get(ec);
                    behavioralModel.refreshBehaviorClusters();
                    behavioralModel.refreshCorrelationMatrix();
                    behaviors.get(node).put(ec, behavioralModel);
                }
            }
      
    }
    }

    /**
     * @return the behaviors
     */
    public HashMap<String, HashMap<String, ECPBehavioralModel>> getBehaviors() {
        return behaviors;
    }


    public ECPBehavioralModel getBehaviorModel(ElasticityCapability capability, Node node) {
        return behaviors.get(node.getId()).get(capability.getName());
    }
    public double avgActionTime(ElasticityCapability capability, Node node){
        if (behaviors!=null && behaviors.size()>0 && behaviors.containsKey(node.getId()) && behaviors.get(node.getId()).containsKey(capability.getName())) {
            return behaviors.get(node.getId()).get(capability.getName()).avgActionTime();
        }
        else {
            return -1;
        }
    }
     public double stdDevActionTime(ElasticityCapability capability, Node node){
        return behaviors.get(node.getId()).entrySet().iterator().next().getValue().stdDeviationActionTime();
    }
    public LinkedHashMap<String, LinkedHashMap<String, MyEntry<Double, NDimensionalPoint>>> computeExpectedBehavior(ElasticityCapability capability) {
        LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> currentBehavior = new LinkedHashMap<>();
        
         List<MonitoringSnapshot> snapshots = monitoringInterface.getAllMonitoringInformationOnPeriod(ECPBehavioralModel.CHANGE_INTERVAL);
        while (snapshots.isEmpty()){
            snapshots=monitoringInterface.getAllMonitoringInformationOnPeriod(ECPBehavioralModel.CHANGE_INTERVAL);
        }
       synchronized(behaviors){
        if (snapshots.size() > 0) {
            
            for (MonitoringSnapshot snapshot : snapshots){
            for (String SP : snapshot.getServiceParts().keySet()) {
                if (!currentBehavior.containsKey(SP)) {
                    LinkedHashMap<String, NDimensionalPoint> metricsWithPoints = new LinkedHashMap<>();
                    currentBehavior.put(SP, metricsWithPoints);
                }
                ServicePartMonitor monitor = snapshot.getServiceParts().get(SP);
                    for (Map.Entry<String, Double> recording : monitor.getMetrics().entrySet()) {

                        if (!currentBehavior.get(SP).containsKey(recording.getKey())) {
                            NDimensionalPoint nDimPoint = new NDimensionalPoint();
                            currentBehavior.get(SP).put(recording.getKey(), nDimPoint);
                            
                        }

                        currentBehavior.get(SP).get(recording.getKey()).addValue(recording.getValue());
                    }
                }
            }
        }
   

        return behaviors.get(capability.getServicePartID()).get(capability.getName()).computeExpectedBehavior(currentBehavior);
       }
    }
}
