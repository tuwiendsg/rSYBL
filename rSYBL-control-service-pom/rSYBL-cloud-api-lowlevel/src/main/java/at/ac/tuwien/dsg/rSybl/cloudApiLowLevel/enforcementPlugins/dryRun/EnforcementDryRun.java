package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.dryRun;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;

public class EnforcementDryRun {

	   private FileWriter fstream;
	   private String fileName;
	   public EnforcementDryRun(){
		   Date date = new Date();
		    fileName = "./Enforcement_"+"_"+date.getHours()+"_"+date.getMinutes()+".csv";
			try {
				fstream = new FileWriter(fileName);
			
			 String headers = "Time , Action ";

			  headers += "\n";
			  fstream.write(headers);
			  fstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	   
	   public void enforceAction(String action){
		   Date date = new Date();
		  	try {
				fstream = new FileWriter(fileName);
			
			fstream.write(date.toString()+","+action);
			
			  fstream.close();
		  	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		
		
	
}
