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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityMetric;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import com.sun.org.apache.xalan.internal.xsltc.dom.CurrentNodeListFilter;
import java.util.Map;
import javax.xml.bind.annotation.XmlEnumValue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DependencyGraph implements Serializable{
        private String STATE="CONTROL"; //wait
	private Node cloudService;

	public Node getCloudService() {
		return cloudService;
	}
        public boolean isInControlState(){
            if (STATE.toLowerCase().equalsIgnoreCase("control")) return true;
            else return false;
        }
        public void setControlState(){
            STATE="CONTROL";
        }
        public void setWaitState(){
            STATE="WAIT";
        }
	public void setCloudService(Node cloudService) {
		this.cloudService = cloudService;
	}
	public ArrayList<Node> getAllServiceTopologies() {
		ArrayList<Node> topologies = new ArrayList<Node>();
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				topologies.add(n);
				if (n.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
					for (Node n1 : n
							.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
						if (n1.getNodeType() == NodeType.SERVICE_TOPOLOGY)
							unexploredNodes.add(n1);
					}
				}
			}
			unexploredNodes.remove(0);
		}

		return topologies;
	}
         public ElasticityMetric findMetricWithName(String metricName, String servicePartID){
             Node n = getNodeWithID(servicePartID);
             return n.findMetricWithName(metricName);
           
        }
         public List<Node> findAllRelatedNodesForPolynomialRel(PolynomialElasticityRelationship relationship){
             List<Node> nodes = new ArrayList<Node>();
             for (PolynomialElasticityRelationship.Monom monom:relationship.getPolynom()){
                 if (monom.getServicePartID()!=null && !monom.getServicePartID().equalsIgnoreCase("") && getNodeWithID(monom.getServicePartID())!=null){
                    nodes.add(getNodeWithID(monom.getServicePartID()));
                 }
             }
             return nodes;
         }
        public List<Relationship> findAllElasticityRelationshipsAssociatedToMetrics(){
            List<Relationship> rel = new ArrayList<Relationship>();
            for (ElasticityMetric elasticityMetric:findAllElasticityMetrics()){
                rel.addAll(elasticityMetric.getRelationships());
            }
            return rel;
        }
         public List<ElasticityMetric> findAllElasticityMetrics(){
             List<ElasticityMetric> result = new ArrayList<ElasticityMetric>();
             for (Node node:getAllServiceUnits()){
                 result.addAll(node.getElasticityMetrics());
             }
             for (Node node:getAllServiceTopologies()){
                 result.addAll(node.getElasticityMetrics());
             }
             for (Node node:getAllVMs()){
                 result.addAll(node.getElasticityMetrics());
             }
             result.addAll(cloudService.getElasticityMetrics());
             return result;
         }
	public ArrayList<Node> getAllServiceUnits(){
		ArrayList<Node> units = new ArrayList<Node>();
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				if (n.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP) != null) {
					for (Node n1 : n
							.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
							unexploredNodes.add(n1);
					}
				}
			}else{
				 if (n.getNodeType()==NodeType.SERVICE_UNIT)
					 units.add(n);
			}
			unexploredNodes.remove(0);
		}

		return units;
	}
	
	public ArrayList<Node> getAllServiceTopologiesOfCloudService() {
		ArrayList<Node> topologies = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP)) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				topologies.add(n);
			}
		}
		return topologies;
	}
	public ArrayList<Node> getAllVMs() {
		ArrayList<Node> units = new ArrayList<Node>();
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodes()) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
				if (n.getAllRelatedNodes() != null) {
					for (Node n1 : n.getAllRelatedNodes() ){
							unexploredNodes.add(n1);
					}
				}
			
				 if (n.getNodeType()==NodeType.VIRTUAL_MACHINE)
					 units.add(n);
			
			unexploredNodes.remove(0);
		}

		return units;
	}
	public Node findParentNode(String entityID){
		Node parent = null;
		Node currentNode = getNodeWithID(entityID);
//		for (Node n: currentNode.getAllRelatedNodes()){
//			SimpleRelationship rel = (SimpleRelationship) currentNode.getRelationshipOfTypeWithNode(RelationshipType.COMPOSITION_RELATIONSHIP,currentNode);
//			if (rel!=null && rel.getTargetElement().equalsIgnoreCase(entityID))
//				parent=n;
//		}
		for (Node n: cloudService.getAllRelatedNodes()){
			if (n.getId().equalsIgnoreCase(entityID)){
				parent=cloudService;
			return parent;}
		}
		for (Node n:getAllServiceTopologies()){
			if (n.getRelatedNode(entityID)!=null ){
				SimpleRelationship rel =(SimpleRelationship) n.getRelationshipOfTypeWithNode(RelationshipType.COMPOSITION_RELATIONSHIP,currentNode);
				if (rel!=null &&  rel.getTargetElement().equalsIgnoreCase(entityID)){
					
					parent=n;
					return parent;
				}
				
			}
		}
		for (Node n:getAllServiceUnits()){
			if (n.getRelatedNode(entityID)!=null ){
				SimpleRelationship rel =(SimpleRelationship)n.getRelationshipOfTypeWithNode(RelationshipType.COMPOSITION_RELATIONSHIP,currentNode);
				if ( rel!=null && rel.getTargetElement().equalsIgnoreCase(entityID)){
					
					parent=n;
					return parent;
				}
			}
		}
		return parent;
	}
	
			
	public Node getNodeWithID(String id) {
		if (cloudService.getId().equalsIgnoreCase(id)) return cloudService;
		List<Node> unexploredNodes = new ArrayList<Node>();
		for (Node n : cloudService
				.getAllRelatedNodes()) {
			if (n.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
				unexploredNodes.add(n);
			}
		}
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			if (n.getId().equalsIgnoreCase(id))
				return n;
			else if (n.getRelatedNode(id) != null)
				return n.getRelatedNode(id);
			else
				unexploredNodes
						.addAll(n
								.getAllRelatedNodes());
			unexploredNodes.remove(0);

		}
		return null;
	}

	public Object getMetricForNode(String nodeID, String metricName) {
		Node currentNode = getNodeWithID(nodeID);
		return currentNode.getElasticityMetricValue(metricName);
	}

	

	public void setMetricForNode(String nodeID, String metricName,
			Object value, String unit) {
		Node currentNode = getNodeWithID(nodeID);
		if (currentNode.hasElasticityMetric(metricName))
			currentNode.setValueForElasticityMetric(metricName, value);
		else
			currentNode.addElasticityMetric(metricName, unit, value);
	}
        public void setMetricValue (String nodeID, String metricName, Object value){
            getNodeWithID(nodeID).setValueForElasticityMetric(metricName, value);
        }
	public void setMetricForNode(String nodeID, ElasticityMetric metric) {
		Node currentNode = getNodeWithID(nodeID);
		if (currentNode.getElasticityMetrics().contains(metric))
			currentNode.setValueForElasticityMetric(metric, metric.getValue());
		else
			currentNode.addElasticityMetric(metric);
	}

	public void setMetricForNode(String nodeID, ElasticityMetric metric,
			Object value) {
		Node currentNode = getNodeWithID(nodeID);
		if (currentNode.getElasticityMetrics().contains(metric))
			currentNode.setValueForElasticityMetric(metric, value);
		else
			currentNode.addElasticityMetric(metric);
	}
	public ArrayList<ElasticityRequirement> getAllElasticityRequirements(){
		ArrayList<ElasticityRequirement> elasticityRequirements= new ArrayList<ElasticityRequirement>();
		elasticityRequirements.addAll(cloudService.getElasticityRequirements());
		for (Node n:getAllServiceTopologies()){
			elasticityRequirements.addAll(n.getElasticityRequirements());
			
		}
		for (Node n:getAllServiceUnits()){
			elasticityRequirements.addAll(n.getElasticityRequirements());
			}
		return elasticityRequirements;
	}
        public ArrayList<ElasticityCapability> getAllElasticityCapabilities(){
		ArrayList<ElasticityCapability> elasticityCapabilities= new ArrayList<ElasticityCapability>();
		elasticityCapabilities.addAll(cloudService.getElasticityCapabilities());
		for (Node n:getAllServiceTopologies()){
			elasticityCapabilities.addAll(n.getElasticityCapabilities());
			
		}
		for (Node n:getAllServiceUnits()){
			elasticityCapabilities.addAll(n.getElasticityCapabilities());
			}
		return elasticityCapabilities;
	}
	public ArrayList<Relationship> getAllRelationshipsOfType(RelationshipType type){
		ArrayList<Relationship> relationship= new ArrayList<Relationship>();
		relationship.addAll(cloudService.getAllRelationshipsOfType(type));
		for (Node n:getAllServiceTopologies()){
			relationship.addAll(n.getAllRelationshipsOfType(type));
			
		}
		for (Node n:getAllServiceUnits()){
			relationship.addAll(n.getAllRelationshipsOfType(type));
			}
		return relationship;
	}
	public String graphToString(){
		String message = "";
		
		
		ArrayList<Node> unexploredNodes = new ArrayList<Node>();
		unexploredNodes.add(cloudService);
		
		while (unexploredNodes.size() > 0) {
			Node n = unexploredNodes.get(0);
			message+=n.toString()+" \n ";
			unexploredNodes
						.addAll(n
								.getAllRelatedNodes());
             
			unexploredNodes.remove(0);

		}
		return message;
	}
        private String convertLevel(NodeType nodeType){
            if (nodeType==NodeType.CLOUD_SERVICE) return "SERVICE";
                        if (nodeType==NodeType.SERVICE_TOPOLOGY) return "SERVICE_TOPOLOGY";
            if (nodeType==NodeType.SERVICE_UNIT) return "SERVICE_UNIT";
            if (nodeType==NodeType.VIRTUAL_MACHINE) return "VM";

            return "SERVICE_UNIT";
        }
        private static class MyPair {

        public Node node;
        public JSONObject jsonObject;

        private MyPair() {
        }

        public MyPair(Node node, JSONObject jsonObject) {
            this.node = node;
            this.jsonObject = jsonObject;
        }
    }
        public String getStructuralDataInJSON(){
             JSONObject root = new JSONObject();
            root.put("name", cloudService.getId());
            root.put("type", "" + convertLevel(cloudService.getNodeType()));

            List<MyPair> processing = new ArrayList<MyPair>();
            processing.add(new MyPair(cloudService, root));

            while (!processing.isEmpty()) {
                MyPair myPair = processing.remove(0);
                JSONObject object = myPair.jsonObject;
                Node element = myPair.node;

                JSONArray children = (JSONArray) object.get("children");
                if (children == null) {
                    children = new JSONArray();
                    object.put("children", children);
                }

                //add children
                for (Node child : element.getAllRelatedNodes()) {
                    JSONObject childElement = new JSONObject();
                    if (child.getNodeType()!=NodeType.VIRTUAL_MACHINE){
                    childElement.put("name", child.getId());
                    
                    childElement.put("type", "" + convertLevel(child.getNodeType()));
                    JSONArray childrenChildren = new JSONArray();
                    childElement.put("children", childrenChildren);
                    
                    processing.add(new MyPair(child, childElement));
                    children.add(childElement);
                }
                }
                for (ElasticityRequirement req:element.getElasticityRequirements()){
                    JSONObject childElement = new JSONObject();
                    String content= "";
                    String id="Req_"+element.getId();
                    if (req!=null && req.getAnnotation()!=null && req.getAnnotation().getConstraints()!=null && !req.getAnnotation().getConstraints().equalsIgnoreCase("")){
                        content+=req.getAnnotation().getConstraints();
                        
                    }
                    if (req!=null && req.getAnnotation()!=null && req.getAnnotation().getStrategies()!=null && !req.getAnnotation().getStrategies().equalsIgnoreCase("")){
                       // if (req.getAnnotation().getStrategies().toLowerCase().contains("and"))
                        //{
                          //  int index = req.getAnnotation().getStrategies().toLowerCase().indexOf("and");
                            
                      //      content+=req.getAnnotation().getStrategies().substring(0,index)+"\n "+req.getAnnotation().getStrategies().substring(index);
                       // }
                       // else{
                            content+=req.getAnnotation().getStrategies();
                       // }
                        
                    }
                    childElement.put("name", content);
                    
                    childElement.put("type", "requirement");
                    JSONArray childrenChildren = new JSONArray();
                    childElement.put("children", childrenChildren);
                    
                   
                    children.add(childElement);
                }

                           }
            
           
            return root.toJSONString();
            
        }
       public void replaceRequirement(String requirement){
           String[] splitReq=requirement.split(":");
           String reqID= splitReq[0];
           String reqtype=splitReq[1].trim().split(" ")[0];
           if (reqtype.equalsIgnoreCase("constraint")){
              for(ElasticityRequirement elasticityRequirement: getAllElasticityRequirements()){
                  if (elasticityRequirement.getAnnotation().getConstraints().split(":")[0].equalsIgnoreCase(reqID)){
                      ElasticityRequirement newReq=elasticityRequirement;
                      newReq.getAnnotation().setConstraints(requirement);
                      Node n=getNodeWithID(elasticityRequirement.getAnnotation().getEntityID());
                      n.getElasticityRequirements().remove(elasticityRequirement);
                      n.getElasticityRequirements().add(newReq);
                      
                  }
              }
           }else
           {
               if (reqtype.equalsIgnoreCase("strategy")){
                     for(ElasticityRequirement elasticityRequirement: getAllElasticityRequirements()){
                  if (elasticityRequirement.getAnnotation().getStrategies().split(":")[0].equalsIgnoreCase(reqID)){
                      ElasticityRequirement newReq=elasticityRequirement;
                      newReq.getAnnotation().setStrategies(requirement);
                      Node n=getNodeWithID(elasticityRequirement.getAnnotation().getEntityID());
                      n.getElasticityRequirements().remove(elasticityRequirement);
                      n.getElasticityRequirements().add(newReq);
                      
                  }
              }
               }else{
                   if (reqtype.equalsIgnoreCase("monitoring")){
                        for(ElasticityRequirement elasticityRequirement: getAllElasticityRequirements()){
                  if (elasticityRequirement.getAnnotation().getMonitoring().split(":")[0].equalsIgnoreCase(reqID)){
                      ElasticityRequirement newReq=elasticityRequirement;
                      newReq.getAnnotation().setMonitoring(requirement);
                      Node n=getNodeWithID(elasticityRequirement.getAnnotation().getEntityID());
                      n.getElasticityRequirements().remove(elasticityRequirement);
                      n.getElasticityRequirements().add(newReq);
                      
                  }
                   }
               }
               
           }
           
       }
       }
      
}
