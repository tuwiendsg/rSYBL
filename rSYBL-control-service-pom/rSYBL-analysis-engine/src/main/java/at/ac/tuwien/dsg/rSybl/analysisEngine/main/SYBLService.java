
package at.ac.tuwien.dsg.rSybl.analysisEngine.main;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.processing.SYBLProcessingThread;



public class SYBLService {

	private HashMap<String,Node> nodes = new HashMap<String,Node>();
	private HashMap<Node,SYBLProcessingThread> myProcessingThreads = new HashMap<Node,SYBLProcessingThread>() ;
	private DependencyGraph dependencyGraph ;
	private MonitoringAPIInterface monitoringAPI;
	private EnforcementAPIInterface enforcementAPI;
	private List<SYBLProcessingThread> processingThreads=new ArrayList<SYBLProcessingThread>();
	public SYBLService(DependencyGraph dependencyGraph,MonitoringAPIInterface monitoringAPI,EnforcementAPIInterface enforcementAPI){
		    this.dependencyGraph=dependencyGraph;
		    this.monitoringAPI=monitoringAPI;
		    this.enforcementAPI=enforcementAPI;

			initializeEntities();
			
	}

	public void initializeEntities(){
		//Initialize the entities without the annotations
		nodes.put(dependencyGraph.getCloudService().getId(), dependencyGraph.getCloudService());
		List<Node> topologies = new ArrayList<Node>();
		List<Node> componentsToExplore = new ArrayList<Node>();

		for (Node topology : dependencyGraph.getCloudService().getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP,NodeType.SERVICE_TOPOLOGY)){
			nodes.put(topology.getId(), topology);

			topologies.add(topology);
		}
			//if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)!=null)
		//	componentsToExplore.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
			while (!topologies.isEmpty()){
				Node currentTopology = topologies.get(0);
				nodes.put(currentTopology.getId(), currentTopology);
				topologies.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
				componentsToExplore.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
				
					topologies.remove(0);
			}
			List<Node> codeRegions = new ArrayList<Node>();
			while (!componentsToExplore.isEmpty() ){
				Node component =componentsToExplore.get(0);
				nodes.put(component.getId(), component);
				
				if (component.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.CODE_REGION)!=null)
				codeRegions.addAll(component.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.CODE_REGION));
				componentsToExplore.remove(0);
				
			}
			
			while (!codeRegions.isEmpty()){
				Node codeRegion =codeRegions.get(0);
				nodes.put(codeRegion.getId(), codeRegion);
				codeRegions.remove(0);
				
			}
	
		
	}

	public void processAnnotations( String componentID, SYBLAnnotation syblAnnotation){
		if (! myProcessingThreads.containsKey(componentID)){
			Node e = nodes.get(componentID); 
		
		 SYBLProcessingThread p = new SYBLProcessingThread(syblAnnotation, e, dependencyGraph,monitoringAPI, enforcementAPI);
		 processingThreads.add(p);
		 p.start();
		}
	
	}
	public void stopProcessingThreads(){
		for (SYBLProcessingThread processingThread:processingThreads){
			processingThread.stop();
		}
	}
	public boolean checkIfContained(Node componentId){
		if (myProcessingThreads.containsKey(componentId))return true;
		else return false;
	}
	public Node getControlledService() {
		return dependencyGraph.getCloudService();
		
	}
	public void setControlledService(Node controlledService) {
		dependencyGraph.setCloudService(controlledService) ;
	}
	
}
