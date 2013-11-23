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
package at.ac.tuwien.dsg.sybl.localService.languageDescription;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;



public class SYBLDescriptionParser {


public String getMethod(String conceptName) {

	XMLParser domParser = new XMLParser();

     Node node = domParser.searchConcept(conceptName);
	if (node!=null){
	if (node.getAttributes().getLength()>0)	{
     NamedNodeMap map = node.getAttributes();
    
     return  map.getNamedItem("apiMethod").getNodeValue();}
	else{
		//System.out.println("No method found");
		return "";
	}
	}else{
		//System.out.println("No method found");
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
		System.out.println("No params found");
		return "";
	}
	}else{
		System.out.println("No params found");
		return "";
	}
	}

	
}
