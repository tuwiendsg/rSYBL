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

package at.ac.tuwien.dsg.csdg;

import java.util.ArrayList;

import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.tosca.TOSCAProcessing;




public class Main {

		//return root node
	public Node  constructExampleDependencyGraph(){
		Node rootCloudService = new Node();
	rootCloudService.setId("CloudService");
	rootCloudService.setNodeType(NodeType.CLOUD_SERVICE);
	
	Node serviceTopology = new Node();
	Relationship relationship = new Relationship();
	relationship.setSourceElement(rootCloudService.getId());
	relationship.setTargetElement(serviceTopology.getId());
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	serviceTopology.setId("DataServiceTopology");
	serviceTopology.setNodeType(NodeType.SERVICE_TOPOLOGY);
	ArrayList<ElasticityRequirement> elasticityRequirements = new ArrayList<ElasticityRequirement>();
	ElasticityRequirement elasticityRequirement = new ElasticityRequirement();
	SYBLAnnotation annotation = new SYBLAnnotation();
	annotation.setConstraints("Co1: CONSTRAINT cpu.usage>20%");
	annotation.setStrategies("St1: STRATEGY minimize(cost)");
	elasticityRequirement.setAnnotation(annotation);
	elasticityRequirements.add(elasticityRequirement);
	serviceTopology.setElasticityRequirements(elasticityRequirements);
	rootCloudService.addNode(serviceTopology, relationship);
	
	Node serviceUnit = new Node();
	relationship.setSourceElement(rootCloudService.getId());
	relationship.setTargetElement(serviceTopology.getId());
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	serviceUnit.setId("CassandraDB");
	serviceUnit.setNodeType(NodeType.SERVICE_TOPOLOGY);
	elasticityRequirements = new ArrayList<ElasticityRequirement>();
	elasticityRequirement = new ElasticityRequirement();
	annotation = new SYBLAnnotation();
	annotation.setConstraints("Co1: CONSTRAINT cpu.usage>20%");
	annotation.setStrategies("St1: STRATEGY minimize(cost)");
	elasticityRequirement.setAnnotation(annotation);
	elasticityRequirements.add(elasticityRequirement);
	serviceUnit.setElasticityRequirements(elasticityRequirements);

	
		
	Node virtualMachine1 = new Node();
	Node virtualMachine2 = new Node();
	Node virtualMachine3 = new Node();
	virtualMachine1.setId("VM1");
	virtualMachine2.setId("VM2");
	virtualMachine3.setId("VM3");
	Node virtualCluster = new Node();
	relationship = new Relationship();
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	relationship.setSourceElement(virtualCluster.getId());
	relationship.setTargetElement(virtualMachine1.getId());	
	virtualCluster.addNode(virtualMachine1, relationship);
	virtualCluster.setId("VirtualCluster1");
	relationship = new Relationship();
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	relationship.setSourceElement(virtualCluster.getId());
	relationship.setTargetElement(virtualMachine2.getId());
	virtualCluster.addNode(virtualMachine2, relationship);
	relationship = new Relationship();
	relationship.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
	relationship.setSourceElement(virtualCluster.getId());
	relationship.setTargetElement(virtualMachine3.getId());
	virtualCluster.addNode(virtualMachine3, relationship);
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualCluster.getId());
	serviceUnit.addNode(virtualCluster, relationship);
	
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualMachine1.getId());
	serviceUnit.addNode(virtualMachine1, relationship);
	
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualMachine2.getId());
	serviceUnit.addNode(virtualMachine2, relationship);
	
	relationship = new Relationship();
	relationship.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
	relationship.setSourceElement(serviceUnit.getId());
	relationship.setTargetElement(virtualMachine3.getId());
	serviceUnit.addNode(virtualMachine3, relationship);
	serviceTopology.addNode(serviceUnit, relationship);
	return rootCloudService;
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		Node cloudService = m.constructExampleDependencyGraph();
		
		DependencyGraph dependencyGraph = new DependencyGraph();
		dependencyGraph.setCloudService(cloudService);
		
		//System.out.println(dependencyGraph.graphToString());
		//DependencyGraph fromTosca = new TOSCAProcessing().toscaDescriptionToDependencyGraph();
		//System.out.println(fromTosca.graphToString());
		
		InputProcessing inputProcessing=new InputProcessing();
		System.out.println(inputProcessing.loadDependencyGraphFromFile().graphToString());
	}
	
	}
