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
