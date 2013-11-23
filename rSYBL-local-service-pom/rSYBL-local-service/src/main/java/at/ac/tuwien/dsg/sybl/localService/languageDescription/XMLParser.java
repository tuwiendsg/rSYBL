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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.ac.tuwien.dsg.sybl.localService.utils.Configuration;


public class XMLParser
{
	private Document doc = null;
	
	public XMLParser()
	{
		try
		{
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(Configuration.getLanguageDescription());
			doc = parserXML(is);
				
			//visit(doc, 0);
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}
	}
	
	public void visit(Node node, int level)
	{
		NodeList nl = node.getChildNodes();
		
		for(int i=0, cnt=nl.getLength(); i<cnt; i++)
		{
			//System.out.println("["+nl.item(i)+"]");
			
			visit(nl.item(i), level+1);

		}
	}
	
	public Node searchConcept(String conceptName){
		//split concept name into more concepts to be able to search for it 
		String[] conc = conceptName.split("\\.");
		LinkedList<String> concepts = new LinkedList<String>();
		for (String s:conc){
			concepts.addLast(s);
		}
		LinkedList<Node> nodes = new LinkedList<Node>();
		nodes.addFirst(doc);
		return searchComplexConcept(concepts,nodes);
	}
	
	private Node searchComplexConcept(LinkedList<String> conceptNames,LinkedList<Node> toVisit){
		if (!toVisit.isEmpty()){
		Node currentNode= toVisit.getFirst();	
		toVisit.removeFirst();
		Node n = currentNode.getFirstChild();
		while (n!=null){
			toVisit.addFirst(n);
			n=n.getNextSibling();
		}
		
		if (currentNode.getNodeName().equalsIgnoreCase(conceptNames.getFirst()))
			conceptNames.removeFirst();
		if (conceptNames.isEmpty()) return currentNode;
		return searchComplexConcept(conceptNames,toVisit);
		}
		return null;
		
	}
	
	private Node searchConcept(Node root,String conceptName){
		LinkedList<Node> queue = new LinkedList<Node>();
		queue.addFirst(root);
		while (queue.size()>0){
		//System.out.println(queue.getFirst().getNodeName());
	    Node n =queue.getFirst().getFirstChild();
	    queue.removeFirst();
		while (n!=null){ 
			 queue.addLast(n);
			if (n.getNodeName().equalsIgnoreCase(conceptName)) return n;
			n = n.getNextSibling();
		}
		
		}
		return null;
		
	}
	
	public Document getDocument(){
		return doc;
	}
	public Document parserXML(InputStream file) throws SAXException, IOException, ParserConfigurationException
	{
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	}
}