/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
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
