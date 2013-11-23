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

package at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;







public class SYBLDescriptionParser {


public String getMethod(String conceptName) {

	XMLParser domParser = new XMLParser();

     Node node = domParser.searchConcept(conceptName);
	if (node!=null){
	if (node.getAttributes().getLength()>0)	{
     NamedNodeMap map = node.getAttributes();
    
     return  map.getNamedItem("apiMethod").getNodeValue();}
	else{
		//SYBLDirectiveEnforcementLogger.logger.info("No method found");
		return "";
	}
	}else{
		//SYBLDirectiveEnforcementLogger.logger.info("No method found");
		return "";
	}
	}
public String getParams(String conceptName) {

	XMLParser domParser = new XMLParser();

     Node node = domParser.searchConcept(conceptName);
	if (node!=null){
	if (node.getAttributes().getLength()>0)	{
     NamedNodeMap map = node.getAttributes();
    
     return  map.getNamedItem("dependsOn").getNodeValue();}
	else{
		SYBLDirectivesEnforcementLogger.logger.info("No params found");
		return "";
	}
	}else{
		SYBLDirectivesEnforcementLogger.logger.info("No params found");
		return "";
	}
	}

}
