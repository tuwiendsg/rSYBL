/** 
   Copyright 2013 Technische Universität Wien (TUW), Distributed Systems Group E184

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
package at.ac.tuwien.dsg.sybl.localService.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
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
    private static String languageDescriptionFile="./config/languageDescription.xml";
    static{
        configuration = new Properties();
        try {
//        	 String current = new java.io.File( "." ).getCanonicalPath();
//             System.out.println("Current dir:"+current);
//      String currentDir = System.getProperty("user.dir");
//             System.out.println("Current dir using System:" +currentDir);
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
//        	   //System.out.println(files);
//        	      }
//        	  }
        	  try {
                  configuration.load(new FileInputStream(new File("./config.properties")));
              } catch (Exception ex) {
                  Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
              }
        	  
			//InputStream is = Configuration.class.getClassLoader().getResourceAsStream("./config.properties");
            //configuration.load(is);
        } catch (Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getModelDescrFile(){
    	return configuration.getProperty("CloudServiceModelDescription");
    }
    
    public static String getLanguageDescription(){
    	return languageDescriptionFile;
        //return configuration.getProperty("SYBLLanguageDescription");
     }
    public static String getCloudServiceURL(){
    	return configuration.getProperty("CloudServiceURL");
        //return configuration.getProperty("SYBLLanguageDescription");
     }
   
    
    
    public static String[] getDirectivesPaths(){
    	return configuration.getProperty("SYBLDirectives").split(";");
    }
    
    public static int getRMIPort()
    {
    	return Integer.parseInt(configuration.getProperty("RMIRegistryPort"));
    }
    public static String getRMIName()
    {
    	return configuration.getProperty("RMIRegistryName");
    }
}