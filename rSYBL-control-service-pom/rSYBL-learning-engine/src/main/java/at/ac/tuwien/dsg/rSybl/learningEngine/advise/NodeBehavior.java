/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise;

import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.Clustering;
import java.util.HashMap;

/**
 *
 * @author Georgiana
 */
public class NodeBehavior {
    private String nodeID;
    private HashMap<String,Clustering> metricClusters = new HashMap<String,Clustering>();

    /**
     * @return the nodeID
     */
    public String getNodeID() {
        return nodeID;
    }

    /**
     * @param nodeID the nodeID to set
     */
    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * @return the metricClusters
     */
    public HashMap<String,Clustering> getMetricClusters() {
        return metricClusters;
    }

    /**
     * @param metricClusters the metricClusters to set
     */
    public void setMetricClusters(HashMap<String,Clustering> metricClusters) {
        this.metricClusters = metricClusters;
    }
    
}
