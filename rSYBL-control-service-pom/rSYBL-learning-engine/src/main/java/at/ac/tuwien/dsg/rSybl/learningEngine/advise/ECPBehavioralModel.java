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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author Georgiana
 */
public class ECPBehavioralModel {
    private ElasticityCapability capability = new ElasticityCapability();
    private HashMap<String,NodeBehavior> nodeBehaviors = new HashMap<>();
    private MonitoringAPIInterface monitoringAPIInterface ;
    private DependencyGraph dependencyGraph;
    private SimpleMatrix coocurenceMatrix;
    public static int CHANGE_INTERVAL = 20;
    private int totalNumberOfClusters = 0;
    private ArrayList<String> clusterNames = new ArrayList<String>();
    private String lastRefreshedTimestamp="";
    private HashMap<String,HashMap<String,ArrayList<NDimensionalPoint>>> spsWithNDimForEachMetric = new HashMap<>();
    public ECPBehavioralModel(Node cloudService, MonitoringAPIInterface aPIInterface){
        monitoringAPIInterface=aPIInterface;
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        
    }
    public void selectRelevantTimeSeries(){
        List<MonitoringSnapshot> snapshots = monitoringAPIInterface.getAllMonitoringInformation();
        int start=-1;
        int end=-1;
        List<Integer> significatIndexes = new ArrayList<Integer>();
        if (snapshots.size()>0){
            int i=0;
            for (MonitoringSnapshot snapshot: snapshots){
                for (Entry<String,String> myAction:snapshot.getOngoingActions().entrySet()){
                   if (myAction.getKey().equalsIgnoreCase(capability.getServicePartID()) && myAction.getValue().equalsIgnoreCase(capability.getName())){
                       if (start==-1){
                           start=i;
                       }
                       if (start>-1){
                           end=i;
                       }
                   }else{
                       if (start>0&&end>0){
                           significatIndexes.add(start+(end-start)/2);
                           start=-1;
                           end=-1;
                       }
                   }
                }
                i++;
            }

        }
        //now we have significant indexes, we get the behaviors for this capability (nDim points, where n=2*CHANGE_INTERVAL)
        int generalIndex = 0;
        for (int significantIndex:significatIndexes){
            for (int i=significantIndex-CHANGE_INTERVAL;i<significantIndex+CHANGE_INTERVAL;i++){
                MonitoringSnapshot snapshot = snapshots.get(i);
                for (String SP:snapshot.getServiceParts().keySet()){
                    if (!spsWithNDimForEachMetric.containsKey(SP)){
                        HashMap<String, ArrayList<NDimensionalPoint>> metricsWithPoints= new HashMap<>();
                        spsWithNDimForEachMetric.put(SP, metricsWithPoints);
                    }
                    for (ServicePartMonitor monitor:snapshot.getServiceParts().values()){
                        for (Entry<String,Double>recording:monitor.getMetrics().entrySet()){
                            if (!spsWithNDimForEachMetric.get(SP).containsKey(recording.getKey())){
                                ArrayList<NDimensionalPoint> nDimPoint = new ArrayList<NDimensionalPoint>();
                                spsWithNDimForEachMetric.get(SP).put(recording.getKey(), nDimPoint);
                            }
                            if (spsWithNDimForEachMetric.get(SP).get(recording.getKey()).size()<generalIndex){
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
    }
    
    public void initializeBehaviorClusters(){
      selectRelevantTimeSeries();
        
      for (String sp:spsWithNDimForEachMetric.keySet()){
          NodeBehavior behavior = new NodeBehavior();
          behavior.setNodeID(sp);
          HashMap<String,Clustering> clustering = new HashMap<>();
          for (String metric: spsWithNDimForEachMetric.get(sp).keySet()){
             Clustering cl = new Clustering();
             int nbClusters = (int) Math.sqrt(spsWithNDimForEachMetric.get(sp).get(metric).size());
             cl.initialize(spsWithNDimForEachMetric.get(sp).get(metric),nbClusters,0.2);
            clustering.put(metric, cl);
            
            int x = 0;
            for (Cluster cluster:cl.getClusters()){
                if (cluster!=null && cluster.getPoints()!=null && cluster.getPoints().size()>0)
                {clusterNames.add("Cl"+x+"_"+metric+"_"+sp);
                x++;
                }
            }
            totalNumberOfClusters+=x;
          }
          
          behavior.setMetricClusters(clustering);
      }  
        
    }

 
        public void computeCorrelationMatrix(){
        coocurenceMatrix = new SimpleMatrix(totalNumberOfClusters,totalNumberOfClusters);
        
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
    public HashMap<String,NodeBehavior> getNodeBehaviors() {
        return nodeBehaviors;
    }

    /**
     * @param nodeBehaviors the nodeBehaviors to set
     */
    public void setNodeBehaviors(HashMap<String,NodeBehavior> nodeBehaviors) {
        this.nodeBehaviors = nodeBehaviors;
    }

  

    
}
