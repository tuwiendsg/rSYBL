/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Georgiana
 */
public class NDimensionalPoint implements Cloneable{
    private LinkedList<Double> values = new LinkedList<>();
    public static double MAX_DIST=9999999;
    
    
    
    
    /**
     * @return the values
     */
    public LinkedList<Double> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(LinkedList<Double> values) {
        this.values =  values;
    }
    @Override
        protected Object clone() throws CloneNotSupportedException {
        NDimensionalPoint newNDimensionalPoint= (NDimensionalPoint) super.clone();
        newNDimensionalPoint.setValues((LinkedList<Double>)values.clone());
        return newNDimensionalPoint;
        }
    @Override
    public boolean equals(Object o){
        if (o.getClass()==this.getClass()){
            NDimensionalPoint newPoint = (NDimensionalPoint) o;
            if (newPoint.getValues().size()==values.size()){
                for (int i=0;i<values.size();i++){
                    if (newPoint.getValues().get(i) !=values.get(i))
                    {
                        return false;
                    }
                }
                return true;
            }else
            {
                return false;
            }
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + values.size();
        hash = 59 * hash + Arrays.hashCode(this.values.toArray());
        return hash;
    }
    /*
     * @param o - the object towards which the distance is computed
     * @param newSize - the size of the smaller object
     */
      public double computeDistance(Object o, int newSize){
        if (o.getClass()==this.getClass()){
            NDimensionalPoint newPoint = (NDimensionalPoint) o;
            if (newPoint.values.size()==newSize && newPoint.values.size()<=newSize){
                double dist = 0.0;
                for (int i=0;i<newSize;i++){
                        dist+=Math.pow(newPoint.getValues().get(i) -values.get(i),2);
                }
                return Math.sqrt(dist);
            }else
            {
                return MAX_DIST;
            }
        }else{
            return MAX_DIST;
        }
    }
    /*
     * Computing the full distance between the two objects
     * @pre - it is expected that the two objects have the same size
     */
    public double computeDistance(Object o){
        
        if (o!=null && o.getClass()==this.getClass()){
            NDimensionalPoint newPoint = (NDimensionalPoint) o;
            if (newPoint.values.size()==values.size()){
                double dist = 0.0;
                for (int i=0;i<values.size();i++){
                        dist+=Math.pow(newPoint.getValues().get(i) -values.get(i),2);
                }
                return Math.sqrt(dist);
            }else
            {
                return MAX_DIST;
            }
        }else{
            return MAX_DIST;
        }
    }
    public void addValue(Double val){
        values.add(val);
    }
    @Override
    public String toString(){
        String s=  "The point is "+values.size()+"-dimensional, and it the coordinates are ";
        for (int i=0;i<values.size();i++){
            s+=values.get(i) +" ";
        }
        
        return s;
    }

 
}
