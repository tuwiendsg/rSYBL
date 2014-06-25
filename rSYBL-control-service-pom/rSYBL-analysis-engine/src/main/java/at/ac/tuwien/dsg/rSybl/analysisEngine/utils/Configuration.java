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

package at.ac.tuwien.dsg.rSybl.analysisEngine.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static  Properties configuration ;
    private static String languageDescriptionFile="/config/languageDescription.xml";
    static{
        configuration = new Properties();
        try {
            configuration.load(new FileReader( new File("./config.properties")));

			
        } catch (Exception ex) {
        	InputStream is = Configuration.class.getClassLoader().getResourceAsStream("/config.properties");
			try {
				configuration.load(is);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }

    public static String getModelDescrFile(){
    	return configuration.getProperty("CloudServiceModelDescription");
    }
    
    public static String getLanguageDescription(){
    	return languageDescriptionFile;
        //return configuration.getProperty("SYBLLanguageDescription");
     }
    
    
    public static String getApplicationSpecificInformation(){
    	return configuration.getProperty("ApplicationSpecificInformation");
    }
    public static String getDirectivesPath(){
    	return configuration.getProperty("SYBLDirectives");
    }
    public static String getMonitoringServiceURL(){
    	return configuration.getProperty("MonitoringServiceURL");
    }

}