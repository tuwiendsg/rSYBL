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
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
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
    private String lastRefreshedTimestamp = "";
    private int maxNbMetrics = 0;
    private int maxNbClusters = 0;
    public static double SENSITIVITY = 1.0;

    public ECPBehavioralModel(Node cloudService, MonitoringAPIInterface aPIInterface) {
        monitoringAPIInterface = aPIInterface;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);

    }
    public double stdDeviationActionTime(){
        double stdDev =0.0;
        double avgTime = avgActionTime();
        for (Integer time:actionLengths){
            stdDev += Math.pow(avgTime-time, 2);
        }
        
        return Math.sqrt(stdDev/actionLengths.size());
    }
    public double avgActionTime (){
        double sumTime = 0.0;
        for (Integer time:actionLengths){
            sumTime+=time;
        }
        return sumTime/actionLengths.size();
    }
    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> refreshRelevantTimeseries() {
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> newSPsWithNDimForEachMetric = new LinkedHashMap<>();
        List<MonitoringSnapshot> snapshots = monitoringAPIInterface.getAllMonitoringInformationOnPeriod(lastRefreshedTimestamp);
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
        //now we have significant indexes, we get the behaviors for this capability (nDim points, where n=2*CHANGE_INTERVAL)
        int generalIndex = 0;

        for (int significantIndex : significatIndexes) {
            for (int i = significantIndex - CHANGE_INTERVAL; i < significantIndex + CHANGE_INTERVAL; i++) {
                MonitoringSnapshot snapshot = snapshots.get(i);
                for (String SP : snapshot.getServiceParts().keySet()) {
                    if (!newSPsWithNDimForEachMetric.containsKey(SP)) {
                        LinkedHashMap<String, ArrayList<NDimensionalPoint>> metricsWithPoints = new LinkedHashMap<>();
                        newSPsWithNDimForEachMetric.put(SP, metricsWithPoints);
                    }
                    for (ServicePartMonitor monitor : snapshot.getServiceParts().values()) {
                        for (Entry<String, Double> recording : monitor.getMetrics().entrySet()) {
                            if (monitor.getMetrics().entrySet().size() > maxNbMetrics) {
                                maxNbMetrics = monitor.getMetrics().entrySet().size();
                            }
                            if (!newSPsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())) {
                                ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
                                newSPsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
                                allSPsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
                            }
                            if (newSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).size() < generalIndex) {
                                NDimensionalPoint nDimensionalPoint = new NDimensionalPoint();
                                newSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);
                                allSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);

                            }
                            newSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).addValue(recording.getValue());
                            allSPsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).addValue(recording.getValue());
                        }
                    }
                }



            }
            generalIndex++;
        }
        return newSPsWithNDimForEachMetric;

    }

    public LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> selectRelevantTimeSeries() {
        List<MonitoringSnapshot> snapshots = monitoringAPIInterface.getAllMonitoringInformation();
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> spsWithNDimForEachMetric = new LinkedHashMap<>();

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
        //now we have significant indexes, we get the behaviors for this capability (nDim points, where n=2*CHANGE_INTERVAL)
        int generalIndex = 0;

        for (int significantIndex : significatIndexes) {
            for (int i = significantIndex - CHANGE_INTERVAL; i < significantIndex + CHANGE_INTERVAL; i++) {
                MonitoringSnapshot snapshot = snapshots.get(i);
                for (String SP : snapshot.getServiceParts().keySet()) {
                    if (!spsWithNDimForEachMetric.containsKey(SP)) {
                        LinkedHashMap<String, ArrayList<NDimensionalPoint>> metricsWithPoints = new LinkedHashMap<>();
                        spsWithNDimForEachMetric.put(SP, metricsWithPoints);
                    }
                    for (ServicePartMonitor monitor : snapshot.getServiceParts().values()) {
                        for (Entry<String, Double> recording : monitor.getMetrics().entrySet()) {
                            if (monitor.getMetrics().entrySet().size() > maxNbMetrics) {
                                maxNbMetrics = monitor.getMetrics().entrySet().size();
                            }
                            if (!spsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())) {
                                ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
                                spsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
                            }
                            if (spsWithNDimForEachMetric.get(SP).get(recording.getKey()).size() < generalIndex) {
                                NDimensionalPoint nDimensionalPoint = new NDimensionalPoint();
                                spsWithNDimForEachMetric.get(SP).get(recording.getKey()).add(nDimensionalPoint);
                            }
                            spsWithNDimForEachMetric.get(SP).get(recording.getKey()).get(generalIndex).addValue(recording.getValue());
                        }
                    }
                }



            }
            generalIndex++;
        }
        return spsWithNDimForEachMetric;
    }

    public void refreshBehaviorClusters() {
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> newSPsWithNDimForEachMetric = refreshRelevantTimeseries();
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
                cl.refresh(newSPsWithNDimForEachMetric.get(sp).get(metric), nbClusters, 0.2);
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
        }
    }

    public void initializeBehaviorClusters() {
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<NDimensionalPoint>>> spsWithNDimForEachMetric = selectRelevantTimeSeries();

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
                        clusterNames.add("Cl_" + x + "_" + metric + "_" + sp);
                        x++;
                    }
                }
                totalNumberOfClusters += x;
            }

            behavior.setMetricClusters(clustering);
        }

    }

    public void refreshCorrelationMatrix() {
        coocurenceMatrix = new SimpleMatrix(totalNumberOfClusters, totalNumberOfClusters);

        for (int i = 0; i < totalNumberOfClusters; i++) {
            for (int j = 0; j < totalNumberOfClusters; j++) {
                String[] s1 = clusterNames.get(i).split("_");
                String[] s2 = clusterNames.get(j).split("_");
                int clusterNb1 = Integer.parseInt(s1[1]);
                int clusterNb2 = Integer.parseInt(s2[1]);
                String metricName1 = s1[2];
                String metricName2 = s2[2];
                String sp1 = s1[3];
                String sp2 = s2[3];
                if (!metricName1.equalsIgnoreCase(metricName2) || !sp1.equalsIgnoreCase(sp2)) {

                    for (int x = 0; x < allSPsWithNDimForEachMetric.get(sp1).get(metricName1).size(); i++) {
                        double dist1 = allSPsWithNDimForEachMetric.get(sp1).get(metricName1).get(x).computeDistance(nodeBehaviors.get(sp1).getMetricClusters().get(metricName1).getClusters().get(clusterNb1).getCentroid());
                        double dist2 = allSPsWithNDimForEachMetric.get(sp1).get(metricName1).get(x).computeDistance(nodeBehaviors.get(sp2).getMetricClusters().get(metricName2).getClusters().get(clusterNb2).getCentroid());
                        if (dist1 < SENSITIVITY && dist2 < SENSITIVITY) {

                            coocurenceMatrix.set(i, j, coocurenceMatrix.get(i, j) + 1);
                            coocurenceMatrix.set(j, i, coocurenceMatrix.get(j, i) + 1);

                        }
                    }

                }
            }
        }

    }

    //Still to do - get monitoring data from MELA, and move it into hashmaps  - ALSO REFRESH CLUSTERS
    public LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> computeExpectedBehavior(LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> currentBehavior) {
        LinkedHashMap<String, LinkedHashMap<String, List<Clustering.MyEntry<Double, NDimensionalPoint>>>> expectedBehavior = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> behaviors = new LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>>();


        for (String sp : currentBehavior.keySet()) {

            for (String metric : currentBehavior.get(sp).keySet()) {

                Clustering cluster = nodeBehaviors.get(sp).getMetricClusters().get(metric);
                List<Clustering.MyEntry<Double, NDimensionalPoint>> b = cluster.getClustersByDistance(currentBehavior.get(sp).get(metric));
                if (!expectedBehavior.containsKey(sp)) {
                    expectedBehavior.put(sp, new LinkedHashMap<String, List<Clustering.MyEntry<Double, NDimensionalPoint>>>());

                }

                if (!expectedBehavior.get(sp).containsKey(metric)) {
                    expectedBehavior.get(sp).put(metric, b);
                }
            }
        }

        //TODO: compute best corelation 
        int spNb = 0;
        for (String sp : currentBehavior.keySet()) {
            Double[][] distancesMetrics = new Double[maxNbClusters][maxNbMetrics];
            int row = 0;
            int column = 0;
            String[] metricNames = new String[expectedBehavior.get(sp).size()];
            Integer[] columnSizes = new Integer[expectedBehavior.get(sp).size()];
            for (String metric : expectedBehavior.get(sp).keySet()) {
                int i = 0;
                metricNames[row] = metric;
                for (i = 0; i < expectedBehavior.get(sp).get(metric).size(); i++) {
                    distancesMetrics[row][i] = expectedBehavior.get(sp).get(metric).get(i).getKey();
                }
                columnSizes[row] = expectedBehavior.get(sp).get(metric).size();
                row++;
            }
            double minSum = 100000;
            double maxCoocurence = 0;
            Integer[] selected = new Integer[expectedBehavior.get(sp).values().size()];
            for (int i = 0; i < row; i++) {
                Integer[] sel = new Integer[expectedBehavior.get(sp).values().size()];
                for (int x = 0; x < columnSizes[i]; x++) {
                    for (int y = 0; y < row; y++) {
                        if (sel[y] != 0) {
                            if (expectedBehavior.get(sp).get(metricNames[y]).get(sel[y]).getKey() > expectedBehavior.get(sp).get(metricNames[y]).get(x).getKey()) {
                                sel[y] = x;
                            }
                        } else {
                            sel[y] = x;
                        }
                    }
                }
                double sum = 0;
                double coocurence = 0;
                for (int x = 0; x < expectedBehavior.get(sp).values().size(); i++) {
                    sum += expectedBehavior.get(sp).get(metricNames[x]).get(sel[x]).getKey();

                }
                for (int x = 0; x < expectedBehavior.get(sp).values().size(); i++) {
                    String name = "Cl_" + sel[x] + "_" + x + "_" + spNb;
                    int cl1 = clusterNames.indexOf(name);

                    for (int y = 0; x < expectedBehavior.get(sp).values().size(); i++) {
                        String name2 = "Cl_" + sel[x] + "_" + x + "_" + spNb;
                        int cl2 = clusterNames.indexOf(name2);

                        coocurence += coocurenceMatrix.get(cl1, cl2);
                    }
                }


                if (sum < minSum && (maxCoocurence < coocurence / 2.0)) {
                    minSum = sum;
                    maxCoocurence = coocurence;
                    selected = sel.clone();
                }
            }

            int i = 0;
            for (String metric : currentBehavior.get(sp).keySet()) {
                if (!behaviors.containsKey(sp)) {
                    behaviors.put(sp, new LinkedHashMap<String, NDimensionalPoint>());

                }
                behaviors.get(sp).put(metric, expectedBehavior.get(sp).get(metric).get(selected[i]).getValue());
                i++;
            }
            spNb++;
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

    /**
     * @param nodeBehaviors the nodeBehaviors to set
     */
    public void setNodeBehaviors(LinkedHashMap<String, NodeBehavior> nodeBehaviors) {
        this.nodeBehaviors = nodeBehaviors;
    }
}
