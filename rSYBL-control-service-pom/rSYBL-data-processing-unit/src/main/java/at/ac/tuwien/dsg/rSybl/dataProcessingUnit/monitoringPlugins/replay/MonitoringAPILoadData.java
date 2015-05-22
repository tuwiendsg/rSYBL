/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.replay;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.CustomEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.EventNotification;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.IEvent;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class MonitoringAPILoadData implements MonitoringInterface {

    private Node controlledService;
    private int MONITORING_DATA_REFRESH_INTERVAL = 1;
    private List<String> actions = new ArrayList<String>();
    private List<String> targets = new ArrayList<String>();
    private HashMap<String, BufferedReader> readers = new HashMap<String, BufferedReader>();
    private HashMap<String, HashMap<Integer, String>> headers = new HashMap<String, HashMap<Integer, String>>();
    private HashMap<String, HashMap<String, Double>> currentValues = new HashMap<String, HashMap<String, Double>>();

    {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (controlledService != null) {
                    refreshMonitoringData();
                }
            }
        };

        Timer monitoringDataRefreshTimer = new Timer();
        monitoringDataRefreshTimer.schedule(task, 0, MONITORING_DATA_REFRESH_INTERVAL * 1000);
    }

    public MonitoringAPILoadData() {

    }

    public MonitoringAPILoadData(Node controledService) {
        init(controledService);
        controlledService = controledService;

    }

    public synchronized void init(Node service) {
        List<Node> topologies = service.getAllRelatedNodesOfType(Relationship.RelationshipType.COMPOSITION_RELATIONSHIP, Node.NodeType.SERVICE_TOPOLOGY);
        List<Node> nodes = new ArrayList<Node>();
        try {
            BufferedReader csF = new BufferedReader(new InputStreamReader(new FileInputStream("./load/" + service.getId() + ".csv"), Charset.forName("UTF-8")));
            this.readers.put(service.getId(), csF);
            String[] string = null;
            try {
                String line = csF.readLine();
                string = line.split(",");
            } catch (IOException ex) {
                Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
            }
            int i = 0;
            headers.put(service.getId(), new HashMap<Integer, String>());
            for (String s : string) {
                
                headers.get(service.getId()).put(i, s.toLowerCase().replace(" ", ""));
                i++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader csF = null;
        for (Node node : topologies) {
            try {
                csF = new BufferedReader(new InputStreamReader(new FileInputStream("./load/" + node.getId() + ".csv"), Charset.forName("UTF-8")));
                String[] string = null;
                try {
                    String line = csF.readLine();
                    string = line.split(",");
                } catch (IOException ex) {
                    Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
                }
                int i = 0;
                 headers.put(node.getId(), new HashMap<Integer, String>());
                for (String s : string) {
                   
                    headers.get(node.getId()).put(i, s.toLowerCase().replace(" ", ""));
                    i++;
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.readers.put(node.getId(), csF);
            nodes.addAll(node.getAllRelatedNodesOfType(Relationship.RelationshipType.COMPOSITION_RELATIONSHIP, Node.NodeType.SERVICE_UNIT));

        }
        for (Node node : nodes) {
            try {
                csF = new BufferedReader(new InputStreamReader(new FileInputStream("./load/" + node.getId() + ".csv"), Charset.forName("UTF-8")));
                String[] string = null;
                try {
                    String line = csF.readLine();
                    string = line.split(",");
                } catch (IOException ex) {
                    Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
                }
                int i = 0;
                headers.put(node.getId(), new HashMap<Integer, String>());
                for (String s : string) {
                    

                    headers.get(node.getId()).put(i, s.toLowerCase().replace(" ", ""));
                    i++;
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.readers.put(node.getId(), csF);
        }

    }

    public synchronized void refreshMonitoringData() {
        List<String> unhealthyTargets = new ArrayList<String>();
        for (String part : this.readers.keySet()) {
            String[] string = null;
            boolean ok= true;
            try {
                String line = readers.get(part).readLine();
                if (line!=null){
                string = line.split(",");
                }else{
                     if (readers.get(part) != null) {
                        readers.get(part).close();
                    }
                    readers.put(part, new BufferedReader(new InputStreamReader(new FileInputStream("./load/" + part + ".csv"), Charset.forName("UTF-8"))));
                    line = readers.get(part).readLine();
                    string = line.split(",");
                }
            } catch (IOException ex) {
                try {
                    if (readers.get(part) != null) {
                        readers.get(part).close();
                    }
                    readers.put(part, new BufferedReader(new InputStreamReader(new FileInputStream("./load/" + part + ".csv"), Charset.forName("UTF-8"))));
                    String line = readers.get(part).readLine();
                    string = line.split(",");
                } catch (FileNotFoundException ex1) {
                    ex1.printStackTrace();
                    Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex1);
                } catch (IOException ex1) {
                    Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex1);
                }

//                Logger.getLogger(MonitoringAPILoadData.class.getName()).log(Level.SEVERE, null, ex);
            }
            int i = 0;
            for (String s : string) {
                if (currentValues.get(part) == null) {
                    currentValues.put(part, new HashMap<String, Double>());
                }
                this.currentValues.get(part).put(headers.get(part).get(i), Double.parseDouble(s));
                if (this.currentValues.get(part).get(headers.get(part).get(i))<0){
                    ok=false;
                }
                i++;
            }
            if (!ok){
                unhealthyTargets.add(part);
            }
        }
        if (unhealthyTargets.size()>0){
            for (String part:unhealthyTargets){
            sendUnhealthyMessageOnServicePart(part);
            }
        }
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformation() {
        return new ArrayList<MonitoringSnapshot>();
    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformationOnPeriod(long time) {
        return new ArrayList<MonitoringSnapshot>();
    }

    @Override
    public Double getCpuUsage(Node node) {
        return this.currentValues.get(node.getId()).get("cpuUsage");
    }

    @Override
    public void removeService(Node service) {
        controlledService = null;
    }

    @Override
    public Double getMemoryAvailable(Node node) {
        return this.currentValues.get(node.getId()).get("memAvailable");

    }

    @Override
    public boolean isHealthy() {
        boolean healthy = true;
        for (String nodeID : currentValues.keySet()) {
            for (String metric : currentValues.get(nodeID).keySet()) {
                if (currentValues.get(nodeID).get(metric) < 0) {
                    healthy = false;
                }
            }
        }
        return healthy;
    }

    @Override
    public Double getMemorySize(Node node) {
        return this.currentValues.get(node.getId()).get("memSize");

    }

    @Override
    public Double getMemoryUsage(Node node) {
        return this.currentValues.get(node.getId()).get("memUsage");

    }

    @Override
    public Double getDiskSize(Node node) {
        return this.currentValues.get(node.getId()).get("diskSize");

    }

    @Override
    public Double getDiskAvailable(Node node) {
        return this.currentValues.get(node.getId()).get("diskAvailable");

    }

    @Override
    public Double getDiskUsage(Node node) {
        return this.currentValues.get(node.getId()).get("diskUsage");

    }

    @Override
    public List<MonitoringSnapshot> getAllMonitoringInformationFromTimestamp(long timestamp) {
        return new ArrayList<MonitoringSnapshot>();
    }

    @Override
    public Double getCPUSpeed(Node node) {
        return this.currentValues.get(node.getId()).get("cpuSpeed");

    }

    @Override
    public Double getPkts(Node node) {
        return this.currentValues.get(node.getId()).get("pkts");

    }

    @Override
    public Double getPktsIn(Node node) {
        return this.currentValues.get(node.getId()).get("pktsIn");

    }

    @Override
    public Double getPktsOut(Node node) {
        return this.currentValues.get(node.getId()).get("pktsOut");

    }

    @Override
    public Double getReadLatency(Node node) {
        return this.currentValues.get(node.getId()).get("readLatency");

    }

    @Override
    public Double getWriteLatency(Node node) {
        return this.currentValues.get(node.getId()).get("writeLatency");

    }

    @Override
    public Double getReadCount(Node node) {
        return this.currentValues.get(node.getId()).get("readCount");

    }

    @Override
    public Double getCostPerHour(Node node) {
        return this.currentValues.get(node.getId()).get("costPerHour");

    }

    @Override
    public Double getWriteCount(Node node) {
        return this.currentValues.get(node.getId()).get("writeCount");

    }

    @Override
    public Double getTotalCostSoFar(Node node) {
        return this.currentValues.get(node.getId()).get("totalCostSoFar");

    }

    @Override
    public List<String> getAvailableMetrics(Node node) {
        List<String> metrics = new ArrayList<String>();
        metrics.addAll(currentValues.get(node.getId()).keySet());
        return metrics;
    }

    @Override
    public void submitServiceConfiguration(Node node) {
        init(node);

        this.controlledService = node;
    }

    @Override
    public void submitCompositionRules(String composition) {
        //TODO not now
    }

    @Override
    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description) {
    }

    @Override
    public void notifyControlActionStarted(String actionName, Node node) {
    }

    @Override
    public void notifyControlActionEnded(String actionName, Node node) {
    }

    @Override
    public Double getMetricValue(String metricName, Node node) {
        return this.currentValues.get(node.getId()).get(metricName.toLowerCase());

    }

    @Override
    public Double getNumberInstances(Node node) {
        return this.currentValues.get(node.getId()).get("vmNb");

    }

    @Override
    public void refreshServiceStructure(Node node) {
        this.controlledService = node;
    }

    @Override
    public List<String> getOngoingActionID() {
        return actions;
    }

    @Override
    public List<String> getOngoingActionNodeID() {
        return actions;
    }

    @Override
    public void submitCompositionRules() {
    }

    @Override
    public boolean checkIfMetricsValid(Node node) {
        boolean healthy = true;
        for (String nodeID : currentValues.keySet()) {
            for (String metric : currentValues.get(nodeID).keySet()) {
                if (currentValues.get(nodeID).get(metric) < 0) {
                    healthy = false;
                }
            }
        }
        return healthy;
    }

    @Override
    public void sendMessageToAnalysisService(String message) {

    }

    @Override
    public void sendControlIncapacityMessage(String message, List<ElasticityRequirement> cause) {
    }
    public void sendUnhealthyMessageOnServicePart(String target){
        EventNotification eventNotification = EventNotification.getEventNotification();
            CustomEvent customEvent = new CustomEvent();
            customEvent.setCloudServiceID(controlledService.getId());
            customEvent.setType(IEvent.Type.UNHEALTHY_SP);
            customEvent.setTarget(target);
            
            customEvent.setMessage("Service part "+target+" from service "+controlledService.getId()+" is unhealthy. Please check.");
            eventNotification.sendEvent(customEvent);
    }
    @Override
    public void setCurrentCloudService(Node cloudService) {
        init(cloudService);

        this.controlledService = cloudService;
    }
}
