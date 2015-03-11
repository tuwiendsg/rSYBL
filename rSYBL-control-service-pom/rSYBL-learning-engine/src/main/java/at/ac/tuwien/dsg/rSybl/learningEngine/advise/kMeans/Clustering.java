/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans;

import at.ac.tuwien.dsg.rSybl.learningEngine.utils.LearningLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 *
 * @author Georgiana
 */
public class Clustering {
    private ArrayList<NDimensionalPoint> points = new ArrayList<>();
    private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
    public Clustering(){
        
    }
    public void refresh(ArrayList<NDimensionalPoint> newPoints, int k, double cutoff){
        points.addAll(newPoints);
        if (k>clusters.size()){
           int oldSize = clusters.size();
           for (int i=0;i<k-oldSize;i++){
           Cluster newCluster = new Cluster();
            newCluster.addPoint(newPoints.get(i));
            newCluster.setCentroid(newPoints.get(i));
            clusters.add(newCluster);
           }
        }
        
         double biggestShift= NDimensionalPoint.MAX_DIST;
       while (biggestShift>cutoff){
           ArrayList<ArrayList<NDimensionalPoint>> lists = new ArrayList<ArrayList<NDimensionalPoint>>();
           for (int i=0;i<clusters.size();i++){
               lists.add(new ArrayList<NDimensionalPoint>());
           }
           for (NDimensionalPoint nDimensionalPoint:points){
               double smallestDistance = nDimensionalPoint.computeDistance(clusters.get(0).getCentroid());
               int index=0;
               for (int i=1;i<clusters.size();i++){
                   double distance = nDimensionalPoint.computeDistance(clusters.get(i).getCentroid());
                   if (distance<smallestDistance){
                       smallestDistance=distance;
                       index=i;
                   }
               }
               lists.get(index).add(nDimensionalPoint);
           }
           biggestShift=0.0;
           for (int i=0;i<clusters.size();i++){
               double shift = clusters.get(i).update(lists.get(i));
               if (shift>biggestShift){
                   biggestShift=shift;
               }
           }
     
        }
       
    }
    public void initialize(ArrayList<NDimensionalPoint> points, int k, double cutoff){
        this.points=points;
        for (int i=0;i<k;i++){
            Cluster newCluster = new Cluster();
            newCluster.addPoint(points.get(i));
            newCluster.setCentroid(points.get(i));
            clusters.add(newCluster);
            
        }
       double biggestShift= NDimensionalPoint.MAX_DIST;
       while (biggestShift>cutoff){
           ArrayList<ArrayList<NDimensionalPoint>> lists = new ArrayList<ArrayList<NDimensionalPoint>>();
           for (int i=0;i<clusters.size();i++){
               lists.add(new ArrayList<NDimensionalPoint>());
           }
           for (NDimensionalPoint nDimensionalPoint:points){
               double smallestDistance = nDimensionalPoint.computeDistance(clusters.get(0).getCentroid());
               int index=0;
               for (int i=1;i<clusters.size();i++){
                   double distance = nDimensionalPoint.computeDistance(clusters.get(i).getCentroid());
                   if (distance<smallestDistance){
                       smallestDistance=distance;
                       index=i;
                   }
               }
               lists.get(index).add(nDimensionalPoint);
           }
           biggestShift=-200000;
           for (int i=0;i<clusters.size();i++){
               double shift = clusters.get(i).update(lists.get(i));
               if (shift>biggestShift){
                   biggestShift=shift;
               }
           }
     
        }
    }


    
    public  ArrayList<MyEntry<Double,NDimensionalPoint>> getClustersByDistance(NDimensionalPoint point){
        ArrayList<MyEntry<Double,NDimensionalPoint>> orderedCluster = new ArrayList<> ();
        for (Cluster c:clusters){
           // c.computeCentroidAsAverage();
            double dist = NDimensionalPoint.MAX_DIST;
            NDimensionalPoint res  = null;
            
            if (c.getCentroid()!=null && c.getClosestPoint(point).computeDistance(point,point.getValues().size())<dist){
            // dist = c.computeDistance(point, point.getValues().size());
             res=c.getClosestPoint(point);
             dist = res.computeDistance(point,point.getValues().size());
            }
            if (orderedCluster.size()==0){
               orderedCluster.add(new MyEntry<>(dist,res));

            }else{
                int x = -1;
            for (int i =0;i<orderedCluster.size();i++){
                if (orderedCluster.get(i).getKey()<dist){
                    x=i;
                }
            }
            if (x==-1){
                orderedCluster.add(0,new MyEntry<>(dist,res));
            }else{
            if (x+1<orderedCluster.size())
                orderedCluster.add(x+1, new MyEntry<>(dist,res));
            else
                orderedCluster.add(new MyEntry<>(dist,res));
            }
            }    
        }
   
        return orderedCluster;
    }
   public void addNewPointsAndRefreshClusters(ArrayList<NDimensionalPoint> newPoints, int k, double cutoff){
           this.points.addAll(newPoints);
        for (int i=0;i<k;i++){
            Cluster newCluster = new Cluster();
            newCluster.addPoint(points.get(i));
        }
       double biggestShift= NDimensionalPoint.MAX_DIST;
       while (biggestShift>cutoff){
           ArrayList<ArrayList<NDimensionalPoint>> lists = new ArrayList<ArrayList<NDimensionalPoint>>();
           for (int i=0;i<clusters.size();i++){
               lists.add(new ArrayList<NDimensionalPoint>());
           }
           for (NDimensionalPoint nDimensionalPoint:points){
               double smallestDistance = nDimensionalPoint.computeDistance(clusters.get(0).getCentroid());
               int index=0;
               for (int i=1;i<clusters.size();i++){
                   double distance = nDimensionalPoint.computeDistance(clusters.get(i).getCentroid());
                   if (distance<smallestDistance){
                       smallestDistance=distance;
                       index=i;
                   }
               }
               lists.get(index).add(nDimensionalPoint);
           }
           biggestShift=0.0;
           for (int i=0;i<clusters.size();i++){
               double shift = clusters.get(i).update(lists.get(i));
               if (shift>biggestShift){
                   biggestShift=shift;
               }
           }
     
        }
   }
    /**
     * @return the points
     */
    public ArrayList<NDimensionalPoint> getPoints() {
        return points;
    }

    /**
     * @param points the points to set
     */
    public void setPoints(ArrayList<NDimensionalPoint> points) {
        this.points = points;
    }

    /**
     * @return the clusters
     */
    public ArrayList<Cluster> getClusters() {
        return clusters;
    }

    /**
     * @param clusters the clusters to set
     */
    public void setClusters(ArrayList<Cluster> clusters) {
        this.clusters = clusters;
    }
}
