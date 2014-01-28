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

package at.ac.tuwien.dsg.rsybl.controlStressApp.controls;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Configuration {

    private static  Properties configuration ;
    private static String languageDescriptionFile="/config/languageDescription.xml";
    static{
        configuration = new Properties();
        try {
//        	 String current = new java.io.File( "." ).getCanonicalPath();
//        	 
//             System.err.println("Current dir:"+current);
//             String currentDir = System.getProperty("user.dir");
//             System.err.println("Current dir using System:" +currentDir);
//
//        	 File folder = new File(".");
//        	  File[] listOfFiles = folder.listFiles(); 
//        	  String files;
//        	  for (int i = 0; i < listOfFiles.length; i++) 
//        	  {
//        	 
//        	   if (listOfFiles[i].isFile()) 
//        	   {
//        	   files = listOfFiles[i].getName();
//        	       System.err.println(files);
//        	      }
//        	  }
        	  try {
        		//  DependencyGraphLogger.logger.info("Current inputStream "+Configuration.class.getClassLoader().getResourceAsStream("./config.properties"));
                 
               	  configuration.load(Configuration.class.getClassLoader().getResourceAsStream("/config.properties"));
             //     configuration.load(new FileReader( new File("config.properties")));
        	  } catch (Exception ex) {
                  Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
              }
        	  
			//InputStream is = Configuration.class.getClassLoader().getResourceAsStream("./config.properties");
            //configuration.load(is);
        } catch (Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static String getUserEMailAddress()
    {
    	return configuration.getProperty("UserEmailAddress");
    }
    public static String getAPIUserName(){
    	return configuration.getProperty("ApiUserName");
    }
    
    public static String getCustomerUUID(){
        return configuration.getProperty("CustomerUUID");
     }
    
 
    public static String getPassword(){
    	return configuration.getProperty("Password");
    }
    
    public static String getEndPointAddress()
    {
    	
    	return configuration.getProperty("ENDPOINT_ADDRESS_PROPERTY");
    }

}