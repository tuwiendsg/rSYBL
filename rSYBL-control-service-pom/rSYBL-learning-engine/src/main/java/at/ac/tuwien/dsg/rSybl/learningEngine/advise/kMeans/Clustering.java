/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans;

import java.util.ArrayList;

/**
 *
 * @author Georgiana
 */
public class Clustering {
    private ArrayList<NDimensionalPoint> points = new ArrayList<>();
    private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
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
           biggestShift=0.0;
           for (int i=0;i<clusters.size();i++){
               double shift = clusters.get(i).update(lists.get(i));
               if (shift>biggestShift){
                   biggestShift=shift;
               }
           }
     
        }
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
