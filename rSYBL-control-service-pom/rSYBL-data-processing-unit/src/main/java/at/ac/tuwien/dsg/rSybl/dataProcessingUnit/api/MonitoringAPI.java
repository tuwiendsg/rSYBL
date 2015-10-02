/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapabilityInformation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.EventNotification;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.OfferedMonitoredMetrics;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;

public class MonitoringAPI implements MonitoringAPIInterface {

    private static HashMap<Node, ArrayList<Double>> avgRunningTimes = new HashMap<Node, ArrayList<Double>>();

    private boolean executingControlAction = false;
    private String compositionRules = "";
    private Node controlledService;
    private MonitoringInterface offeredMonitoringMetrics;
    
    public MonitoringAPI() {

    }

    public void setCompositionRules(String compositionRules) {
        this.compositionRules=compositionRules;
        offeredMonitoringMetrics.submitCompositionRules(compositionRules);
    }

    public Double getCurrentCPUSize(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getCPUSpeed(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("In get current cpu" + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCostPerHour(Node e) {

        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getCostPerHour(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("In get cost per hour " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }

    }

    public Double getCurrentRAMSize(Node e) {

        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getMemorySize(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error(" In get ram size " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public String Test() {

        return "TEST Working";
    }

    public Double getCurrentMemUsage(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getMemoryUsage(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("In get memory usage " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getTotalCostSoFar(Node e) {
        return offeredMonitoringMetrics.getTotalCostSoFar(e);
    }

    public Node getControlledService() {
		//RuntimeLogger.logger.info("Returning cloud service"+controlledService);
        //RuntimeLogger.logger.info("example of component" + controlledService.getComponentTopology().getComponentTopology().get(0).getComponents().get(0).getAssociatedIps());

        return controlledService;
    }

    public void setControlledService(Node controlledService) {
        this.controlledService = controlledService;
        RuntimeLogger.logger.info("Setting the service " + controlledService.toString());
        offeredMonitoringMetrics = OfferedMonitoredMetrics.getInstance(this.controlledService);
        offeredMonitoringMetrics.submitServiceConfiguration(this.controlledService);
        DependencyGraph dependencyGraph = new DependencyGraph();

        RuntimeLogger.logger.info("Set the service configuration on the monitoring api to " + controlledService.toString());

    }
    public void controlExistingCloudService(Node controlledService){
        this.controlledService=controlledService;
              
        offeredMonitoringMetrics = OfferedMonitoredMetrics.getInstance(this.controlledService);
        offeredMonitoringMetrics.setCurrentCloudService(controlledService);
        RuntimeLogger.logger.info("Set the service configuration on the monitoring api to " + controlledService.toString());
    }
    public Double getCurrentReadLatency(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getReadLatency(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Read Latency " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCurrentReadCount(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getReadCount(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Read Count " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCurrentWriteLatency(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getWriteLatency(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Write Latency " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCurrentWriteCount(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getWriteCount(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Write Count " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCurrentCPUUsage(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
	//	System.err.println("Cpu usage method for entity "+e.getId()+" ips"+e.getAssociatedIps().size());
        //RuntimeLogger.logger.info("At cpu usage"+offeredMonitoringMetrics);
        try {
            return offeredMonitoringMetrics.getCpuUsage(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("CPU Usage " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCurrentHDDSize(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getDiskSize(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("HDD Size " + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getCurrentLatency(Node arg0) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            Double returnedLatency = 0.0;
            if (!getCurrentReadLatency(arg0).isNaN() && !getCurrentWriteLatency(arg0).isNaN()) {
                returnedLatency = (getCurrentReadLatency(arg0) + getCurrentWriteLatency(arg0)) / 2;
            }
            if (getCurrentReadLatency(arg0).isNaN()) {
                if (getCurrentWriteLatency(arg0).isNaN()) {
                    returnedLatency = 0.0;
                } else {
                    returnedLatency = getCurrentWriteLatency(arg0);
                }
            } else {
                returnedLatency = getCurrentReadLatency(arg0);
            }
            //RuntimeLogger.logger.info("Current latency for entity "+arg0.getId()+" is "+returnedLatency);

            return returnedLatency;
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Current latency " + ex.toString() + "for node " + arg0.getId());
            return 0.0;
        }

    }

    public Double getCurrentOperationCount(Node arg0) {
        return (getCurrentReadCount(arg0) + getCurrentWriteCount(arg0));

    }

    public Double getCurrentHDDUsage(Node e) {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            return offeredMonitoringMetrics.getDiskUsage(e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Current HDD Usage" + ex.toString() + "for node " + e.getId());
            return 0.0;
        }
    }

    public Double getMetricValue(String metricName, Node e) throws Exception {
        if (isExecutingControlAction()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        try {
            //RuntimeLogger.logger.info("The value of offered monitoring metrics " + offeredMonitoringMetrics);
            return offeredMonitoringMetrics.getMetricValue(metricName, e);
        } catch (Exception ex) {
            RuntimeLogger.logger.error("Current " + metricName + ex.toString() + "for node " + e.getId());
           // throw new Exception();
            return 0.0;
        }
    }

    public boolean isExecutingControlAction() {
        return executingControlAction;
    }

    public void scaleinstarted(Node arg0) {
        executingControlAction = true;
        offeredMonitoringMetrics.notifyControlActionStarted("scaleIn", arg0);
        
    }

    public void scaleinended(Node arg0) {

        offeredMonitoringMetrics.notifyControlActionEnded("scaleIn", arg0);
        executingControlAction = false;

    }

    public void scaleoutstarted(Node arg0) {
        executingControlAction = true;
        offeredMonitoringMetrics.notifyControlActionStarted("scaleOut", arg0);
    }

    public void scaleoutended(Node arg0) {

        offeredMonitoringMetrics.notifyControlActionEnded("scaleOut", arg0);
        executingControlAction = false;

    }

    public Double getNumberInstances(Node n) {
        return offeredMonitoringMetrics.getNumberInstances(n);
    }

    @Override
    public void submitElasticityRequirements(
            ArrayList<ElasticityRequirement> description) {
//        for (ElasticityRequirement elasticityRequirement : description) {
//
//            RuntimeLogger.logger.info(elasticityRequirement.getAnnotation().getEntityID() + " Setting elasticity requirements ");
//          
//        }
        offeredMonitoringMetrics.submitElasticityRequirements(description);

    }

    @Override
    public void enforcingActionStarted(String actionName, Node e) {

        executingControlAction = true;
        offeredMonitoringMetrics.notifyControlActionStarted(actionName, e);
    }

    @Override
    public void enforcingActionEnded(String actionName, Node e) {

        offeredMonitoringMetrics.notifyControlActionEnded(actionName, e);
        executingControlAction = false;
    }

    @Override
    public void refreshServiceStructure(Node cloudService) {
        offeredMonitoringMetrics.refreshServiceStructure(cloudService);
    }

    @Override
    public List<String> getAvailableMetrics(Node node) {
        return offeredMonitoringMetrics.getAvailableMetrics(node);
    }

    @Override
    public List<String> getOngoingActionID() {
        // TODO Auto-generated method stub
        return offeredMonitoringMetrics.getOngoingActionID();
    }

    @Override
    public List<String> getOngoingActionNodeID() {
        // TODO Auto-generated method stub
        return offeredMonitoringMetrics.getOngoingActionNodeID();
    }

    @Override
    public void refreshCompositionRules() {
        // TODO Auto-generated method stub
        offeredMonitoringMetrics.submitCompositionRules(compositionRules);
    }

    @Override
    public boolean checkIfMetricsValid(Node node) {
        // TODO Auto-generated method stub
        return offeredMonitoringMetrics.checkIfMetricsValid(node);
    }

    @Override
    public void setCompositionRules() {
        offeredMonitoringMetrics.submitCompositionRules();
    }

    @Override
    public boolean checkHealthy(Node node) {
       return checkIfMetricsValid(node);
    }

    @Override
    public void sendMessageToAnalysisService(String message) {
        offeredMonitoringMetrics.sendMessageToAnalysisService(message);
    }

    @Override
    public void sendControlIncapacityMessage(String message, List<ElasticityRequirement> cause) {
        offeredMonitoringMetrics.sendControlIncapacityMessage(message, cause);
    }
    @Override 
    public void removeService(Node service){
        offeredMonitoringMetrics.removeService(service);
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformation() {
        return offeredMonitoringMetrics.getAllMonitoringInformation();
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformationOnPeriod( long time) {
        return offeredMonitoringMetrics.getAllMonitoringInformationOnPeriod(time);
    }

    @Override
    public boolean isHealthy() {
        return offeredMonitoringMetrics.isHealthy();
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformationFromTimestamp(long timestamp) {
        return offeredMonitoringMetrics.getAllMonitoringInformationFromTimestamp(timestamp);
    }
    

 
}
