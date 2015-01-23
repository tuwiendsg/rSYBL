/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.Cluster;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.Clustering;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Georgiana
 */
public class Main {

    public static void main(String[] args) {
        ArrayList<NDimensionalPoint> points = new ArrayList<NDimensionalPoint>();
        int size = 10;
        for (int i = 0; i < size; i++) {
            NDimensionalPoint p = new NDimensionalPoint();
           // p.setSize(3);
            LinkedList<Double> myPoints = new LinkedList<>();
            
            myPoints.add(i+0.0d);
            myPoints.add(i+0.0);
            myPoints.add(i+0.0d);
            p.setValues(myPoints);
            points.add(p);
        }
        
         for (int i = 0; i < size; i++) {
            NDimensionalPoint p = new NDimensionalPoint();
//            p.setSize(3);
                        LinkedList<Double> myPoints = new LinkedList<>();
            
            myPoints.add(i*3+0.0d);
            myPoints.add(i*3+0.0);
            myPoints.add(i*3+0.0d);
            p.setValues(myPoints);

            points.add(p);
        }
         
         
          for (int i = 0; i < size; i++) {
            NDimensionalPoint p = new NDimensionalPoint();
//            p.setSize(3);
                        LinkedList<Double> myPoints = new LinkedList<>();
            
            myPoints.add(i*7+0.0d);
            myPoints.add(i*7+0.0);
            myPoints.add(i*7+0.0d);
            p.setValues(myPoints);
            points.add(p);
        }
          
        Clustering c = new Clustering();
        c.initialize(points, 3, 3);
        for (Cluster cluster : c.getClusters()) {
            System.out.println("####################################");
            if (cluster.getPoints()!=null)
            for (NDimensionalPoint nDimensionalPoint : cluster.getPoints()) {
                System.out.println(nDimensionalPoint.toString());
            }
            if (cluster!=null && cluster.getCentroid()!=null)
                System.out.println("With centroid " + cluster.getCentroid().toString());

        }
    }
}
