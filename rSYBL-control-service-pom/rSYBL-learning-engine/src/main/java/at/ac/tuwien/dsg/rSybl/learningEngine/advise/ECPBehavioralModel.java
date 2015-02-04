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
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.Cluster;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.Clustering;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.MyEntry;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;
import at.ac.tuwien.dsg.rSybl.learningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.learningEngine.utils.LearningLogger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author Georgiana
 */
public class ECPBehavioralModel {

    private ElasticityCapability capability = new ElasticityCapability();
    private LinkedHashMap<String, NodeBehavior> nodeBehaviors = new LinkedHashMap<>();
    private MonitoringAPIInterface monitoringAPIInterface;
    private DependencyGraph dependencyGraph;
    private SimpleMatrix coocurenceMatrix;
    public static int CHANGE_INTERVAL = 20;
    private int totalNumberOfClusters = 0;
    private ArrayList<String> clusterNames = new ArrayList<String>();
    private LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> allSPsWithNDimForEachMetric = new LinkedHashMap<>();
    private LinkedList<Integer> actionLengths = new LinkedList<>();
    private long lastRefreshedTimestamp = 0;
    private int maxNbMetrics = 0;
    private int maxNbClusters = 0;
    public static double SENSITIVITY = 2.0;

    public ECPBehavioralModel(Node cloudService, MonitoringAPIInterface aPIInterface) {
        monitoringAPIInterface = aPIInterface;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        if (Configuration.getIntervalSize() > 0) {
            CHANGE_INTERVAL = Configuration.getIntervalSize();
        }

    }

    public double stdDeviationActionTime() {
        double stdDev = 0.0;
        double avgTime = avgActionTime();
        for (Integer time : actionLengths) {
            stdDev += Math.pow(avgTime - time, 2);
        }

        return Math.sqrt(stdDev / actionLengths.size());
    }

    public double avgActionTime() {
        double sumTime = 0.0;
        for (Integer time : actionLengths) {
            sumTime += time;
        }
        return sumTime / actionLengths.size();
    }

    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> refreshRelevantTimeseries() {
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> newSPsWithNDimForEachMetric = new LinkedHashMap<>();
        List<MonitoringSnapshot> snapshots = monitoringAPIInterface.getAllMonitoringInformationFromTimestamp(lastRefreshedTimestamp);
        int start = -1;
        if (snapshots.size() > 0) {
            lastRefreshedTimestamp = snapshots.get(snapshots.size() - 1).getTimestamp();
            int end = -1;
            List<Integer> significatIndexes = new ArrayList<Integer>();
            if (snapshots.size() > 0) {
                int i = 0;
                for (MonitoringSnapshot snapshot : snapshots) {
                    for (Entry<String, String> myAction : snapshot.getOngoingActions().entrySet()) {
                        if (myAction.getKey().equalsIgnoreCase(capability.getServicePartID()) && myAction.getValue().equalsIgnoreCase(capability.getName())) {
                            if (start == -1) {
                                start = i;
                            }
                            if (start > -1) {
                                end = i;
                            }
                        } else {
                            if (start > 0 && end > 0) {
                                significatIndexes.add(start + (end - start) / 2);
                                actionLengths.add(end - start);
                                start = -1;
                                end = -1;

                            }
                        }
                    }
                    i++;
                }

            }
//               if (start > 0 && end > 0) {
//                significatIndexes.add(start + (end - start) / 2);
//                actionLengths.add(end - start);
//                start = -1;
//                end = -1;
//            }

            //now we have significant indexes, we get the behaviors for this capability (nDim points, where n=2*CHANGE_INTERVAL)
            int generalIndex = 0;
            for (int significantIndex : significatIndexes) {
                for (int i = significantIndex - CHANGE_INTERVAL; i < significantIndex + CHANGE_INTERVAL; i++) {
                    MonitoringSnapshot snapshot = snapshots.get(i);
                    for (String SP : snapshot.getServiceParts().keySet()) {
                        if (!newSPsWithNDimForEachMetric.containsKey(SP) && (dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.SERVICE_UNIT || dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.SERVICE_TOPOLOGY || dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.CLOUD_SERVICE)) {
                            LinkedHashMap<String, ArrayList<NDimensionalPoint>> metricsWithPoints = new LinkedHashMap<>();
                            newSPsWithNDimForEachMetric.put(SP, metricsWithPoints);


                            if (!allSPsWithNDimForEachMetric.containsKey(SP) && (dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.SERVICE_UNIT || dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.SERVICE_TOPOLOGY || dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.CLOUD_SERVICE)) {
                                metricsWithPoints = new LinkedHashMap<>();
                                allSPsWithNDimForEachMetric.put(SP, metricsWithPoints);
                            }
                            ServicePartMonitor monitor = snapshot.getServiceParts().get(SP);
                            //for (ServicePartMonitor monitor : snapshot.getServiceParts().values()) {
                            for (Entry<String, Double> recording : monitor.getMetrics().entrySet()) {
                                if (monitor.getMetrics().entrySet().size() > maxNbMetrics) {
                                    maxNbMetrics = monitor.getMetrics().entrySet().size();
                                }
                                if (!newSPsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())) {
                                    ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
                                    newSPsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);

                                }
                                if (!allSPsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())) {
                                    ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
                                    allSPsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
                                }
                                if (newSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).size() < generalIndex+1) {
                                    NDimensionalPoint nDimensionalPoint = new NDimensionalPoint();
                                    newSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);
                                    allSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);

                                }
                                newSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).addValue(recording.getValue());
                                if (allSPsWithNDimForEachMetric.containsKey(recording.getKey())) {
                                    allSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).addValue(recording.getValue());
                                }
                                // }
                            }
                        }
                    }


                }
                generalIndex++;
            }
           
            return newSPsWithNDimForEachMetric;
        } else {
            return null;
        }

    }

    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> selectRelevantTimeSeries(List<MonitoringSnapshot> snapshots) {


        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> spsWithNDimForEachMetric = new LinkedHashMap<>();
        if (snapshots != null && snapshots.size() > 0) {
            int start = -1;

            lastRefreshedTimestamp = snapshots.get(snapshots.size() - 1).getTimestamp();
            int end = -1;
            List<Integer> significatIndexes = new ArrayList<Integer>();
            if (snapshots.size() > 0) {
                int i = 0;
                for (MonitoringSnapshot snapshot : snapshots) {
                    for (Entry<String, String> myAction : snapshot.getOngoingActions().entrySet()) {
                        if (myAction.getKey().equalsIgnoreCase(capability.getServicePartID()) && myAction.getValue().equalsIgnoreCase(capability.getName())) {
                            if (start == -1) {
                                start = i;
                            }
                            if (start > -1) {
                                end = i;
                            }
                        } else {
                            if (start > 0 && end > 0) {
                                significatIndexes.add(start + (end - start) / 2);
                                actionLengths.add(end - start);
                                start = -1;
                                end = -1;
                            }
                        }
                    }
                    i++;
                }

            }
//            if (start > 0 && end > 0) {
//                significatIndexes.add(start + (end - start) / 2);
//                actionLengths.add(end - start);
//                start = -1;
//                end = -1;
//            }

            //now we have significant indexes, we get the behaviors for this capability (nDim points, where n=2*CHANGE_INTERVAL)
            int generalIndex = 0;

            for (int significantIndex : significatIndexes) {
               int finalI = significantIndex + CHANGE_INTERVAL;
                if (snapshots.size()<significantIndex+CHANGE_INTERVAL){
                    finalI=snapshots.size()-1;
                }
                for (int i = significantIndex - CHANGE_INTERVAL; i < finalI; i++) {
                    MonitoringSnapshot snapshot = snapshots.get(i);
                    
                    for (String SP : snapshot.getServiceParts().keySet()) {
                        if (dependencyGraph.getNodeWithID(SP) != null && (dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.SERVICE_UNIT || dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.SERVICE_TOPOLOGY || dependencyGraph.getNodeWithID(SP).getNodeType() == Node.NodeType.CLOUD_SERVICE)) {
                            if (!spsWithNDimForEachMetric.containsKey(SP)) {
                                LinkedHashMap<String, ArrayList<NDimensionalPoint>> metricsWithPoints = new LinkedHashMap<>();
                                spsWithNDimForEachMetric.put(SP, metricsWithPoints);
//                                allSPsWithNDimForEachMetric.put(SP, metricsWithPoints);

                            }
                            ServicePartMonitor monitor = snapshot.getServiceParts().get(SP);
                            
                            for (Entry<String, Double> recording : monitor.getMetrics().entrySet()) {
                                if (monitor.getMetrics().entrySet().size() > maxNbMetrics) {
                                    maxNbMetrics = monitor.getMetrics().entrySet().size();
                                }
                                if (!spsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())) {
                                    ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
                                    spsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
                                }
//                                if (!allSPsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())) {
//                                    ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
//                                    allSPsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
//                                }
                                if (spsWithNDimForEachMetric.get(SP).get(recording.getKey()).size() < (generalIndex + 1)) {
                                    NDimensionalPoint nDimensionalPoint = new NDimensionalPoint();
                                    
                                    spsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);
                                   // allSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);


                                }
                                    //allSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).getValues().add(recording.getValue());
                                if (spsWithNDimForEachMetric.get(SP).get(recording.getKey()).size()>=generalIndex+1){
                                      spsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).getValues().add(recording.getValue());
                                }else{
                                    LearningLogger.logger.info("Could not add "+ recording.getKey() +" measurements to "+SP );
                                }
                           
                            }
                        }



                    }
                
                }
                generalIndex++;
                
            }
            allSPsWithNDimForEachMetric=spsWithNDimForEachMetric;
            return spsWithNDimForEachMetric;
        } else {
            return null;
        }
    }

    public void refreshBehaviorClusters() {
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> newSPsWithNDimForEachMetric = refreshRelevantTimeseries();

        if (newSPsWithNDimForEachMetric != null && newSPsWithNDimForEachMetric.size() > 0) {
            clusterNames = new ArrayList<>();
            totalNumberOfClusters = 0;
            for (String sp : newSPsWithNDimForEachMetric.keySet()) {
                NodeBehavior behavior = new NodeBehavior();
                behavior.setNodeID(sp);
                LinkedHashMap<String, Clustering> clustering = new LinkedHashMap<>();
                for (String metric : newSPsWithNDimForEachMetric.get(sp).keySet()) {

                    Clustering cl = behavior.getMetricClusters().get(metric);
                    int nbClusters = (int) Math.sqrt(allSPsWithNDimForEachMetric.get(sp).get(metric).size());
                    //TO implement refresh on clusters 
                    cl.addNewPointsAndRefreshClusters(newSPsWithNDimForEachMetric.get(sp).get(metric), nbClusters, 0.6);
                    clustering.put(metric, cl);
                    if (cl.getClusters().size() > maxNbClusters) {
                        maxNbClusters = cl.getClusters().size();
                    }
                    int x = 0;

                    for (Cluster cluster : cl.getClusters()) {
                        if (cluster != null && cluster.getPoints() != null && cluster.getPoints().size() > 0) {

                            clusterNames.add("Cl_" + x + "_" + metric + "_" + sp);
                            x++;
                        }
                    }
                    totalNumberOfClusters += x;
                }

                behavior.setMetricClusters(clustering);
                nodeBehaviors.put(sp, behavior);
            }
        }
    }

    public void initializeBehaviorClusters(List<MonitoringSnapshot> snapshots) {
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> spsWithNDimForEachMetric = selectRelevantTimeSeries(snapshots);

        for (String sp : spsWithNDimForEachMetric.keySet()) {
            NodeBehavior behavior = new NodeBehavior();
            behavior.setNodeID(sp);
            LinkedHashMap<String, Clustering> clustering = new LinkedHashMap<>();

            for (String metric : spsWithNDimForEachMetric.get(sp).keySet()) {
                Clustering cl = new Clustering();
                int nbClusters = (int) Math.sqrt(spsWithNDimForEachMetric.get(sp).get(metric).size());
                cl.initialize(spsWithNDimForEachMetric.get(sp).get(metric), nbClusters, 0.2);
                clustering.put(metric, cl);
                if (cl.getClusters().size() > maxNbClusters) {
                    maxNbClusters = cl.getClusters().size();
                }
                int x = 0;
                for (Cluster cluster : cl.getClusters()) {
                    if (cluster != null && cluster.getPoints() != null && cluster.getPoints().size() > 0) {
                        clusterNames.add("Cl::" + x + "::" + metric + "::" + sp);
                        x++;
                    }
                }
                totalNumberOfClusters += x;
            }

            behavior.setMetricClusters(clustering);
            this.nodeBehaviors.put(sp, behavior);
        }

    }

    public void refreshCorrelationMatrix() {
        coocurenceMatrix = new SimpleMatrix(totalNumberOfClusters, totalNumberOfClusters);

        for (int i = 0; i < totalNumberOfClusters; i++) {
            for (int j = 0; j < totalNumberOfClusters; j++) {
                String[] s1 = clusterNames.get(i).split("::");
                String[] s2 = clusterNames.get(j).split("::");
                int clusterNb1 = Integer.parseInt(s1[1]);
                int clusterNb2 = Integer.parseInt(s2[1]);
                String metricName1 = s1[2];
                String metricName2 = s2[2];
                String sp1 = s1[3];
                String sp2 = s2[3];
                if (!metricName1.equalsIgnoreCase("")
                        && !metricName1.equalsIgnoreCase(metricName2) || !sp1.equalsIgnoreCase(sp2)) {
                    if (allSPsWithNDimForEachMetric.get(sp1) != null) {
                        for (int x = 0; x < allSPsWithNDimForEachMetric.get(sp1).get(metricName1).size(); x++) {
                            double dist1 = allSPsWithNDimForEachMetric.get(sp1).get(metricName1).get(x).computeDistance(nodeBehaviors.get(sp1).getMetricClusters().get(metricName1).getClusters().get(clusterNb1).getCentroid());
                            double dist2 = SENSITIVITY;
                            try{
                                dist2=allSPsWithNDimForEachMetric.get(sp2).get(metricName2).get(x).computeDistance(nodeBehaviors.get(sp2).getMetricClusters().get(metricName2).getClusters().get(clusterNb2).getCentroid());
                            }catch(Exception e){
                                LearningLogger.logger.error(e.getMessage());
                            }
                            if (dist1 < SENSITIVITY && dist2 < SENSITIVITY) {

                                coocurenceMatrix.set(i, j, coocurenceMatrix.get(i, j) + 1);
                                coocurenceMatrix.set(j, i, coocurenceMatrix.get(j, i) + 1);

                            }
                        }
                    }
                }
            }
        }

    }

    //Still to do - get monitoring data from MELA, and move it into hashmaps  - ALSO REFRESH CLUSTERS
    public LinkedHashMap<String, LinkedHashMap<String, MyEntry<Double, NDimensionalPoint>>> computeExpectedBehavior(LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> currentBehavior) {
        LinkedHashMap<String, LinkedHashMap<String, List<MyEntry<Double, NDimensionalPoint>>>> expectedBehavior = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashMap<String, MyEntry<Double, NDimensionalPoint>>> behaviors = new LinkedHashMap<String, LinkedHashMap<String, MyEntry<Double, NDimensionalPoint>>>();


        for (String sp : currentBehavior.keySet()) {
            if (nodeBehaviors.containsKey(sp)) {
                for (String metric : currentBehavior.get(sp).keySet()) {

                    Clustering cluster = nodeBehaviors.get(sp).getMetricClusters().get(metric);
                    if (cluster != null) {
                        List<MyEntry<Double, NDimensionalPoint>> b = cluster.getClustersByDistance(currentBehavior.get(sp).get(metric));
                        if (!expectedBehavior.containsKey(sp)) {
                            expectedBehavior.put(sp, new LinkedHashMap<String, List<MyEntry<Double, NDimensionalPoint>>>());

                        }

                        if (!expectedBehavior.get(sp).containsKey(metric)) {
                            expectedBehavior.get(sp).put(metric, b);
                        }
                    }
                }
            } else {
                LearningLogger.logger.info(sp + " SP not existent in node behaviors");
            }


        }
        int spNb = 0;
        for (String sp : currentBehavior.keySet()) {
            if (expectedBehavior.containsKey(sp)) {
                Double[][] distancesMetrics = new Double[100][100];
                int row = 0;
                int column = 0;
                String[] metricNames = new String[expectedBehavior.get(sp).size()];
                Integer[] columnSizes = new Integer[expectedBehavior.get(sp).size()];
                for (String metric : expectedBehavior.get(sp).keySet()) {
                    int i = 0;
                    metricNames[row] = metric;
                    for (i = 0; i < expectedBehavior.get(sp).get(metric).size(); i++) {
                        try{
                        distancesMetrics[row][i] = expectedBehavior.get(sp).get(metric).get(i).getKey();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        }
                    columnSizes[row] = expectedBehavior.get(sp).get(metric).size();
                    row++;
                }
             
                Integer[] selected = new Integer[expectedBehavior.get(sp).values().size()];
                  double minSum = Double.MAX_VALUE;
                double maxCoocurence = 0;
                for (int i = 0; i < row; i++) {
                     
                    Integer[] sel = new Integer[100];
                    for (int y = 0; y < expectedBehavior.get(sp).values().size(); y++) {
                        sel[y] = 0;
                    }
                    for (int x = 0; x < columnSizes[i]; x++) {
                        for (int y = 0; y < expectedBehavior.get(sp).values().size(); y++) {
                            try{
                                if (expectedBehavior.get(sp).get(metricNames[y]).get(sel[y]).getKey() > expectedBehavior.get(sp).get(metricNames[y]).get(x).getKey()) {
                                    sel[y] = x;
                                }
                            }catch(Exception e){
                                LearningLogger.logger.info("Too many metrics for  "+sp);
                            }
                        }
                    }
                    double sum = 0;
                    double coocurence = 0;
                    for (int x = 0; x < expectedBehavior.get(sp).values().size(); x++) {
                        sum += expectedBehavior.get(sp).get(metricNames[x]).get(sel[x]).getKey();
                    }
                    for (int x = 0; x < expectedBehavior.get(sp).values().size(); x++) {
                        
                        String name = "Cl::" + sel[x] + "::" + metricNames[x] + "::" + sp;
                        int cl1 = clusterNames.indexOf(name);

                        for (int y = 0; y < expectedBehavior.get(sp).values().size(); y++) {
                            String name2 = "Cl::" + sel[y] + "::" + metricNames[y] + "::" + sp;
                            int cl2 = clusterNames.indexOf(name2);
                            if (cl1 >= 0 && cl2 >= 0) {
                                coocurence += coocurenceMatrix.get(cl1, cl2);
                            }
                        }
                    }

                    if (sum <= minSum && (maxCoocurence <= coocurence / 2.0)) {
                        minSum = sum;
                        maxCoocurence = coocurence;
                        selected = sel.clone();
                    }
                   
                }

                int i = 0;
                for (String metric : expectedBehavior.get(sp).keySet()) {
                    if (!behaviors.containsKey(sp)) {
                        behaviors.put(sp, new LinkedHashMap<String, MyEntry<Double, NDimensionalPoint>>());

                    }
                    List<Double> values =null ;
                    try{
                     values = expectedBehavior.get(sp).get(metric).get(selected[i]).getValue().getValues();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                     NDimensionalPoint nDimensionalPoint = new NDimensionalPoint();
                    LinkedList<Double> sublist = new LinkedList<>();
                    for (Double d :values.subList(CHANGE_INTERVAL, values.size())){
                        sublist.add(d);
                    }
                    nDimensionalPoint.setValues(sublist);

                    
                    behaviors.get(sp).put(metric, new MyEntry(expectedBehavior.get(sp).get(metric).get(selected[i]).getKey(),nDimensionalPoint));
                    i++;
                }
                spNb++;
            } else {
//                if (!behaviors.containsKey(sp)) {
//                    behaviors.put(sp, new LinkedHashMap<String, MyEntry<Double,NDimensionalPoint>>());
//                }
            }
        }



        return behaviors;
    }

    /**
     * @return the capability
     */
    public ElasticityCapability getCapability() {
        return capability;
    }

    /**
     * @param capability the capability to set
     */
    public void setCapability(ElasticityCapability capability) {
        this.capability = capability;
    }

    /**
     * @return the nodeBehaviors
     */
    public LinkedHashMap<String, NodeBehavior> getNodeBehaviors() {
        return nodeBehaviors;
    }
}
