/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ECPBehavioralModel;
import at.ac.tuwien.dsg.rSybl.planningEngine.adviseEffects.SimpleExpectedBehavior;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffects;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

/**
 *
 * @author Georgiana
 */
public class Main {
    public static void main(String[] args){
        
      SimpleExpectedBehavior expectedBehavior = new SimpleExpectedBehavior(3);
                double[] observations = new double[ECPBehavioralModel.CHANGE_INTERVAL];
                double[] samplePoints = new double[ECPBehavioralModel.CHANGE_INTERVAL];
                for (int i=0;i<ECPBehavioralModel.CHANGE_INTERVAL;i++){
                    observations[i]=i;
                    double val =Math.pow(i, 3);
                    
                    samplePoints[i]=val;
                    System.out.println(val);
                }
                expectedBehavior.fit( observations,samplePoints);
                
                double[] coef = expectedBehavior.getCoef();
               for (int x=0;x<coef.length;x++){
                  System.out.println("Coefficient "+x+" is " +coef[x]); 
               }
                 for (int i=0;i<observations.length;i++){
                    
                    double val = 0;
                    for (int j=0;j<coef.length;j++){
                        val+=coef[j]*Math.pow(i, j);
                    }
                  
                    System.out.println(val);
                }
                for (int i=0;i<ECPBehavioralModel.CHANGE_INTERVAL;i++){
                    double value = 0.0;
                    for (int j=0;j<coef.length;j++){
                        value+=coef[j]*Math.pow(i+ECPBehavioralModel.CHANGE_INTERVAL, j);
                    }
                  System.out.println("New Value "+value);  
                }
    }
    
}
