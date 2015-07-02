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
package at.ac.tuwien.dsg.csdg.outputProcessing;

import at.ac.tuwien.dsg.csdg.utils.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class OutputProcessingFactory {
    public static OutputProcessingInterface createNewOutputProcessing(){
                   OutputProcessingInterface result = null;
        try {
            try {
                if (Configuration.getOutputProcessingClass()!=null && Configuration.getOutputProcessingClass().equalsIgnoreCase("")){
                result = (OutputProcessingInterface) Class.forName(Configuration.getOutputProcessingClass()).newInstance();
                }else{
                    result=new OutputProcessing();
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(OutputProcessingFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
          
        } catch (InstantiationException ex) {
            Logger.getLogger(OutputProcessingFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(OutputProcessingFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
          return result;
    }
}
