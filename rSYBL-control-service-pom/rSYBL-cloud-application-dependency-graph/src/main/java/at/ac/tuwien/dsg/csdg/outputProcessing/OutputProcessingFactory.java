/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
