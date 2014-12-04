/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.Clustering;
import java.util.ArrayList;

/**
 *
 * @author Georgiana
 */
public class ECPBehavioralModel {
    private ElasticityCapability capability = new ElasticityCapability();
    private ArrayList<NodeBehavior> nodeBehaviors = new ArrayList<NodeBehavior>();
    private MonitoringAPIInterface monitoringAPIInterface ;
    public ECPBehavioralModel(MonitoringAPIInterface aPIInterface){
        monitoringAPIInterface=aPIInterface;
    }
    public ArrayList<ArrayList<Double>> selectMetricValuesForECP(){
        ArrayList<ArrayList<Double>> metricVals = new ArrayList<ArrayList<Double>>();
        
        return metricVals;
    }
    public void initializeMetricClusters(){
        
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
    public ArrayList<NodeBehavior> getNodeBehaviors() {
        return nodeBehaviors;
    }

    /**
     * @param nodeBehaviors the nodeBehaviors to set
     */
    public void setNodeBehaviors(ArrayList<NodeBehavior> nodeBehaviors) {
        this.nodeBehaviors = nodeBehaviors;
    }

  

    
}
