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

package at.ac.tuwien.dsg.csdg.inputProcessing.tosca;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.oasis_open.docs.tosca.ns._2011._12.Definitions;
import org.oasis_open.docs.tosca.ns._2011._12.TExtensibleElements;
import org.oasis_open.docs.tosca.ns._2011._12.TDefinitions.Extensions;
import org.oasis_open.docs.tosca.ns._2011._12.TDefinitions.Types;
import org.oasis_open.docs.tosca.ns._2011._12.TNodeTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TPolicy;
import org.oasis_open.docs.tosca.ns._2011._12.TRelationshipTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TServiceTemplate;
import org.oasis_open.docs.tosca.ns._2011._12.TTopologyTemplate;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.csdg.utils.Configuration;



public class TOSCAProcessing {

	public Definitions readTOSCADescriptionsFile(){
		   
		try {	 
			 
			 JAXBContext a = JAXBContext.newInstance( Definitions.class );
			 Unmarshaller u  = a.createUnmarshaller();
			//Definitions	def = (Definitions) u.unmarshal(new File(Configuration.getCloudServiceTOSCADescription()));
   
			 Definitions def = (Definitions) u.unmarshal( InputProcessing.class.getClassLoader().getResourceAsStream(Configuration.getCloudServiceTOSCADescription())) ;

			    return def;			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

	}
	public Definitions readTOSCADescriptionsString(String tosca){
		   
		try {	 
			 
			 JAXBContext a = JAXBContext.newInstance( Definitions.class );
			 Unmarshaller u  = a.createUnmarshaller();
			//Definitions	def = (Definitions) u.unmarshal(new File(Configuration.getCloudServiceTOSCADescription()));
   
			 Definitions def = (Definitions) u.unmarshal(new StringReader(tosca)) ;

			    return def;			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

	}
	public HashMap<String,Node> parseTOSCAGraph(HashMap<String,Node> nodes,List<TExtensibleElements> currentElements,String parentID){
		for (TExtensibleElements extensibleElements: currentElements){
			if (extensibleElements instanceof TServiceTemplate){
				Node n = new Node();
				TServiceTemplate serviceTemplate = (TServiceTemplate)extensibleElements;
				n.setId(serviceTemplate.getId());
				n.setNodeType(NodeType.CLOUD_SERVICE);
				
				Node topology = new Node();
				
				topology.setId(serviceTemplate.getTopologyTemplate().getOtherAttributes().get(new QName("id")));
				
				topology.setNodeType(NodeType.SERVICE_TOPOLOGY);
				Relationship rel  = new Relationship();
				rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
				rel.setSourceElement(n.getId());
				rel.setTargetElement(topology.getId());
				n.addNode(topology,rel );
				nodes.put(topology.getId(), topology);
				nodes.put(((TServiceTemplate) extensibleElements).getId(), n);
				List<TExtensibleElements> c = new ArrayList<TExtensibleElements>();
				c.add(serviceTemplate.getTopologyTemplate());
				return parseTOSCAGraph(nodes, c, n.getId());
			}
			if (extensibleElements instanceof TTopologyTemplate){
				Node serviceTopology = null;
				
				TTopologyTemplate topologyTemplate = (TTopologyTemplate)extensibleElements;
				
				if (nodes.containsKey(topologyTemplate.getOtherAttributes().get(new QName("id")))){
					serviceTopology=nodes.get(topologyTemplate.getOtherAttributes().get(new QName("id")));
				}else{
					serviceTopology = new Node();
					serviceTopology.setId(topologyTemplate.getOtherAttributes().get(new QName("id")));
					serviceTopology.setNodeType(NodeType.SERVICE_TOPOLOGY);

				}
				List<TExtensibleElements> c = new ArrayList<TExtensibleElements>();
				for (TExtensibleElements tExt:topologyTemplate.getNodeTemplateOrRelationshipTemplate()){
					if (tExt instanceof TNodeTemplate){
						TNodeTemplate nodeTemplate =(TNodeTemplate)tExt;
						Node serviceUnit=null;
						if (nodes.containsKey(nodeTemplate.getId())){
							serviceUnit=nodes.get(nodeTemplate.getId());
						}else{
							serviceUnit = new Node();
							serviceUnit.setId(nodeTemplate.getId());
							serviceUnit.setNodeType(NodeType.SERVICE_UNIT);

						}
						serviceUnit.setId(nodeTemplate.getId());
						serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
						Relationship rel  = new Relationship();
						rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
						serviceTopology.addNode(serviceUnit,rel );
						nodes.put(serviceUnit.getId(), serviceUnit);
						
					}else{
						if (tExt instanceof TTopologyTemplate){
							TTopologyTemplate topTemplate =(TTopologyTemplate)tExt;
							Node serviceUnit=null;
							if (nodes.containsKey(topTemplate.getOtherAttributes().get(new QName("id")))){
								serviceUnit=nodes.get(topTemplate.getOtherAttributes().get(new QName("id")));
							}else{
								serviceUnit = new Node();
								serviceUnit.setId(topTemplate.getOtherAttributes().get(new QName("id")));
								serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);

							}
							serviceUnit.setId(topTemplate.getOtherAttributes().get(new QName("id")));
							serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);
							Relationship rel  = new Relationship();
							rel.setSourceElement(serviceTopology.getId());
							rel.setTargetElement(serviceUnit.getId());
							rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
							serviceTopology.addNode(serviceUnit,rel );
							nodes.put(serviceUnit.getId(), serviceUnit);
						}else{
							if (tExt instanceof TRelationshipTemplate){
								TRelationshipTemplate relationship = (TRelationshipTemplate) tExt;
							
							}
						}
					}
					
					
					
				
				}
				nodes.put(serviceTopology.getId(), serviceTopology);

			}
			if (extensibleElements instanceof TNodeTemplate){
				
				
			}
		}
		return nodes;
	}
	public DependencyGraph toscaDescriptionToDependencyGraph(){
		DependencyGraph dependencyGraph = new DependencyGraph();
		HashMap<String,Node> nodes = new HashMap<String,Node>();//String - id of the node, for easier access and modification of its relationships
		Relationship rel = new Relationship();
		//TODO: take each construct present in TOSCA and transform it to our model
		Definitions definitions = readTOSCADescriptionsFile();
		parseTOSCAGraph(nodes, definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation(), "");
		for (Node n:nodes.values())
			if (n.getNodeType()==NodeType.CLOUD_SERVICE)
				dependencyGraph.setCloudService(n);
		return dependencyGraph;
	}
	
	public DependencyGraph toscaDescriptionToDependencyGraph(String tosca){
		DependencyGraph dependencyGraph = new DependencyGraph();
		HashMap<String,Node> nodes = new HashMap<String,Node>();//String - id of the node, for easier access and modification of its relationships
		Relationship rel = new Relationship();
		//TODO: take each construct present in TOSCA and transform it to our model
		Definitions definitions = readTOSCADescriptionsString(tosca);
		TPolicy policy=new TPolicy();

		parseTOSCAGraph(nodes, definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation(), "");
		for (Node n:nodes.values())
			if (n.getNodeType()==NodeType.CLOUD_SERVICE)
				dependencyGraph.setCloudService(n);
		return dependencyGraph;
	}
	

}
