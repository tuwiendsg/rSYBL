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
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;
import at.ac.tuwien.dsg.rSybl.learningEngine.utils.Configuration;
import java.util.ArrayList;
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

    private HashMap<String, HashMap<String, ECPBehavioralModel>> behaviors = new HashMap<>();
    private MonitoringAPIInterface monitoringInterface;
    private DependencyGraph dependencyGraph;
    private Timer reLearnTimer = new Timer();
    private int LEARNING_PERIOD = Configuration.getLearningPeriod();
    private String latestTimestamp = "";

    public ComputeBehavior(Node cloudService, MonitoringAPIInterface interface1) {
        monitoringInterface = interface1;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        for (Node node : dependencyGraph.getAllServiceUnits()) {
            if (node.getElasticityCapabilities() != null && node.getElasticityCapabilities().size() > 0) {
                if (!behaviors.containsKey(node.getId())) {
                    behaviors.put(node.getId(), new HashMap<String, ECPBehavioralModel>());
                }
                for (ElasticityCapability capability : node.getElasticityCapabilities()) {
                    behaviors.get(node.getId()).put(capability.getName(), new ECPBehavioralModel(cloudService, interface1));
                }
            }
        }
        for (Node node : dependencyGraph.getAllServiceTopologies()) {
            if (node.getElasticityCapabilities() != null && node.getElasticityCapabilities().size() > 0) {
                if (!behaviors.containsKey(node.getId())) {
                    behaviors.put(node.getId(), new HashMap<String, ECPBehavioralModel>());
                }
                for (ElasticityCapability capability : node.getElasticityCapabilities()) {
                    behaviors.get(node.getId()).put(capability.getName(), new ECPBehavioralModel(cloudService, interface1));
                }
            }
        }
        if (cloudService.getElasticityCapabilities() != null && cloudService.getElasticityCapabilities().size() > 0) {
            if (!behaviors.containsKey(cloudService.getId())) {
                behaviors.put(cloudService.getId(), new HashMap<String, ECPBehavioralModel>());
            }
            for (ElasticityCapability capability : cloudService.getElasticityCapabilities()) {
                behaviors.get(cloudService.getId()).put(capability.getName(), new ECPBehavioralModel(cloudService, interface1));
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
        if (latestTimestamp.equalsIgnoreCase("")) {
            for (String node : behaviors.keySet()) {
                for (String ec : behaviors.get(node).keySet()) {
                    ECPBehavioralModel behavioralModel = behaviors.get(node).get(ec);
                    for (ElasticityCapability capability : dependencyGraph.getNodeWithID(node).getElasticityCapabilities()) {
                        if (capability.getName().equalsIgnoreCase(ec)) {
                            behavioralModel.setCapability(capability);
                            break;
                        }
                    }
                    behavioralModel.initializeBehaviorClusters();
                    behavioralModel.refreshCorrelationMatrix();
                }
            }
        } else {
            for (String node : behaviors.keySet()) {
                for (String ec : behaviors.get(node).keySet()) {
                    ECPBehavioralModel behavioralModel = behaviors.get(node).get(ec);
                    for (ElasticityCapability capability : dependencyGraph.getNodeWithID(node).getElasticityCapabilities()) {
                        if (capability.getName().equalsIgnoreCase(ec)) {
                            behavioralModel.setCapability(capability);
                            break;
                        }
                    }
                    behavioralModel.refreshBehaviorClusters();
                    behavioralModel.refreshCorrelationMatrix();

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

    /**
     * @param behaviors the behaviors to set
     */
    public void setBehaviors(HashMap<String, HashMap<String, ECPBehavioralModel>> behaviors) {
        this.behaviors = behaviors;
    }

    public ECPBehavioralModel getBehaviorModel(ElasticityCapability capability, Node node) {
        return behaviors.get(node.getId()).get(capability.getName());
    }
    public double avgActionTime(ElasticityCapability capability, Node node){
        if (behaviors.get(node.getId()).containsKey(capability.getName())) {
            return behaviors.get(node.getId()).get(capability.getName()).avgActionTime();
        }
        else {
            return -1;
        }
    }
     public double stdDevActionTime(ElasticityCapability capability, Node node){
        return behaviors.get(node.getId()).entrySet().iterator().next().getValue().stdDeviationActionTime();
    }
    public LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> computeExpectedBehavior(ElasticityCapability capability, Node node) {
        LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> currentBehavior = new LinkedHashMap<>();

        List<MonitoringSnapshot> snapshots = monitoringInterface.getAllMonitoringInformationOnPeriod(ECPBehavioralModel.CHANGE_INTERVAL);
        if (snapshots.size() > 0) {
            
            MonitoringSnapshot snapshot = snapshots.get(snapshots.size() - 1);
            for (String SP : snapshot.getServiceParts().keySet()) {
                if (!currentBehavior.containsKey(SP)) {
                    LinkedHashMap<String, NDimensionalPoint> metricsWithPoints = new LinkedHashMap<>();
                    currentBehavior.put(SP, metricsWithPoints);
                }
                for (ServicePartMonitor monitor : snapshot.getServiceParts().values()) {
                    for (Map.Entry<String, Double> recording : monitor.getMetrics().entrySet()) {

                        if (!currentBehavior.get(SP).containsKey(recording.getKey())) {
                            NDimensionalPoint nDimPoint = new NDimensionalPoint();
                            currentBehavior.get(SP).put(recording.getKey(), nDimPoint);
                            currentBehavior.get(SP).put(recording.getKey(), nDimPoint);
                        }

                        currentBehavior.get(SP).get(recording.getKey()).addValue(recording.getValue());
                        currentBehavior.get(SP).get(recording.getKey()).addValue(recording.getValue());
                    }
                }
            }

        }







        return behaviors.get(node.getId()).get(capability.getName()).computeExpectedBehavior(currentBehavior);
    }
}
