/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Georgiana
 */
public class NDimensionalPoint implements Cloneable{
    private int size;
    private ArrayList<Double> values = new ArrayList<>();
    public static double MAX_DIST=9999999;
    
    
    
    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return the values
     */
    public ArrayList<Double> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }
    @Override
        protected Object clone() throws CloneNotSupportedException {
        NDimensionalPoint newNDimensionalPoint= (NDimensionalPoint) super.clone();
        newNDimensionalPoint.setSize(size);
        newNDimensionalPoint.setValues((ArrayList<Double>)values.clone());
        return newNDimensionalPoint;
        }
    @Override
    public boolean equals(Object o){
        if (o.getClass()==this.getClass()){
            NDimensionalPoint newPoint = (NDimensionalPoint) o;
            if (newPoint.getSize()==size){
                for (int i=0;i<size;i++){
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
        hash = 59 * hash + this.size;
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
            if (newPoint.getSize()<=newSize && newPoint.getSize()<=newSize){
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
            if (newPoint.getSize()==size){
                double dist = 0.0;
                for (int i=0;i<size;i++){
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
        String s=  "The point is "+size+"-dimensional, and it the coordinates are ";
        for (int i=0;i<size;i++){
            s+=values.get(i) +" ";
        }
        
        return s;
    }
}
