/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class Cluster  {

    private ArrayList<NDimensionalPoint> points = new ArrayList<NDimensionalPoint>();
    private NDimensionalPoint centroid;

    public Cluster() {
    }

    public Cluster(ArrayList<NDimensionalPoint> points, NDimensionalPoint centroid) {
        this.points = points;
        this.centroid = centroid;
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
     * @return the centroid
     */
    public NDimensionalPoint getCentroid() {
        return centroid;
    }

    /**
     * @param centroid the centroid to set
     */
    public void setCentroid(NDimensionalPoint centroid) {
        this.centroid = centroid;
    }

    public NDimensionalPoint computeCentroidAsAverage() {
        NDimensionalPoint center = new NDimensionalPoint();
        if (points != null && points.size() > 0) {
           // center.setSize(points.get(0).getValues().size());
            ArrayList<Double> myPoints = points.get(0).getValues();

            for (int x = 1; x < points.size(); x++) {
                for (int i = 0; i < points.get(0).getValues().size(); i++) {
                    myPoints.set(i,  myPoints.get(i) + points.get(x).getValues().get(i));
                }
            }
            for (int i = 0; i < points.get(0).getValues().size(); i++) {
                myPoints.set(i, myPoints.get(i) / points.size());
            }
            center.setValues(myPoints);
            return center;
        } else {
            return null;
        }

    }

    public void addPoint(NDimensionalPoint dimensionalPoint) {
        points.add(dimensionalPoint);
    }
//     def update(self, points):
//        old_centroid = self.centroid
//        self.points = points
//        self.centroid = self.calculateCentroid()
//        return getDistance(old_centroid, self.centroid)

    public double update(ArrayList<NDimensionalPoint> newPoints) {
        if (centroid != null) {
            try {
                NDimensionalPoint oldCentroid = (NDimensionalPoint) centroid.clone();
                this.points = newPoints;
                this.centroid = computeCentroidAsAverage();
                if (centroid!=null){
                return centroid.computeDistance(oldCentroid);
                }else{
                    return NDimensionalPoint.MAX_DIST;
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(Cluster.class.getName()).log(Level.SEVERE, null, ex);
                return NDimensionalPoint.MAX_DIST;
            }

        } else {
            centroid = computeCentroidAsAverage();
            this.points = newPoints;
            return NDimensionalPoint.MAX_DIST;
        }
    }

    public NDimensionalPoint computeCentroidWithSmallestDistance() {
        NDimensionalPoint center = points.get(0);
        double minDist = NDimensionalPoint.MAX_DIST;
        for (NDimensionalPoint point : points) {
            double totalDist = 0.0;
            for (NDimensionalPoint toCheck : points) {
                totalDist += point.computeDistance(toCheck);
            }
            if (totalDist < minDist) {
                minDist = totalDist;
                center = point;
            }
        }
        return center;
    }
}
