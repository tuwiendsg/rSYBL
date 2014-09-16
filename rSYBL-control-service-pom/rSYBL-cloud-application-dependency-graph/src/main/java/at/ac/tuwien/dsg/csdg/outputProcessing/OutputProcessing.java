/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class OutputProcessing implements OutputProcessingInterface {
    private static FileWriter fstream = null;
    
    static {
        try {
    
	
            if (Configuration.getFileForStoringActionPlans()!=null && !Configuration.getFileForStoringActionPlans().equalsIgnoreCase(""))
              fstream = new FileWriter(Configuration.getFileForStoringActionPlans());
            else
                fstream = new FileWriter("actionplans.csv");
        } catch (IOException ex) {
            Logger.getLogger(OutputProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public OutputProcessing(){
     	  
		 
    }
    @Override
    public void saveActionPlan(HashMap<Node,ElasticityCapability> actionPlan) {
       	try {
				  Date date = new Date();
			 if (Configuration.getFileForStoringActionPlans()!=null && !Configuration.getFileForStoringActionPlans().equalsIgnoreCase(""))
              fstream = new FileWriter(Configuration.getFileForStoringActionPlans(),true);
            else
                fstream = new FileWriter("actionplans.csv",true);
					/**
					 * monitoring sequence
					 */
				  String toWrite = date.toString()+",";
				  for (Entry<Node,ElasticityCapability> entry:actionPlan.entrySet())
					try {
						toWrite+=entry.getKey().getId()+":"+entry.getValue().getName();
					} catch (Exception e) {
                                                toWrite+="";
						// TODO Auto-generated catch block
						DependencyGraphLogger.logger.error("Elasticity capability "+entry.getValue().getName()+" not valid");
                                                fstream.close();
					}
                                  
                                  
				  fstream.append(toWrite+'\n');
				  fstream.close();
				  
        }catch(Exception e){
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(OutputProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }
           DependencyGraphLogger.logger.error("Error when writing the action plan");
        }
    }
    
}
