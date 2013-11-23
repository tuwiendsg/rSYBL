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

package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLAnnotationXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML.ActionXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.AssociatedVM;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentUnit;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;







public class InputProcessing {
	private  SYBLElasticityRequirementsDescription syblSpecifications;
	private DeploymentDescription deploymentDescription;
	private CloudServiceXML cloudServiceXML;
	private void loadDeploymentDescriptionFromFile(){
		try {		
			//load deployment description and populate the dependency graph
			
			JAXBContext a = JAXBContext.newInstance( DeploymentDescription.class );
			Unmarshaller u  = a.createUnmarshaller();
			String deploymentDescriptionPath = Configuration.getDeploymentDescriptionPath();
		//	RuntimeLogger.logger.info("Got here "+deploymentDescriptionPath);
		//	RuntimeLogger.logger.info("Got here "+this.getClass().getClassLoader().getResourceAsStream(deploymentDescriptionPath));
			
			if (deploymentDescriptionPath!=null){
				//deploymentDescription = (DeploymentDescription) u.unmarshal(new File(deploymentDescriptionPath));
				deploymentDescription = (DeploymentDescription) u.unmarshal( InputProcessing.class.getClassLoader().getResourceAsStream(deploymentDescriptionPath)) ;
			}
			//RuntimeLogger.logger.info("Read deployment Descrption"+deploymentDescription.toString());

		} catch (Exception e) {
			DependencyGraphLogger.logger.error("Error in reading deployment description"+e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void loadDeploymentDescriptionFromString(String deploymentDescr){
		try {		
			//load deployment description and populate the dependency graph
			
			JAXBContext a = JAXBContext.newInstance( DeploymentDescription.class );
			Unmarshaller u  = a.createUnmarshaller();
			//String deploymentDescriptionPath = Configuration.getDeploymentDescriptionPath();
			//DependencyGraphLogger.logger.info("Got here "+this.getClass().getClassLoader().getResourceAsStream(deploymentDescriptionPath));
			
				deploymentDescription = (DeploymentDescription) u.unmarshal( new StringReader(deploymentDescr)) ;
			DependencyGraphLogger.logger.info("Read deployment Descrption"+deploymentDescription.toString());

		} catch (Exception e) {
			DependencyGraphLogger.logger.error("Error in reading deployment description"+e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void loadModelFromFile(){
		 JAXBContext jc;
		    cloudServiceXML = null;
		try {			
			jc = JAXBContext.newInstance( CloudServiceXML.class );
			Unmarshaller u = jc.createUnmarshaller();
			


			//JAXBElement element=  u.unmarshal( new File(Configuration.getModelDescrFile()));
			// cloudS = (CloudServiceXML) element.getValue();
			 cloudServiceXML = (CloudServiceXML) u.unmarshal(this.getClass().getClassLoader().getResourceAsStream(Configuration.getModelDescrFile()));
			//File f = new File(Configuration.getModelDescrFile());
			//cloudServiceXML = (CloudServiceXML) u.unmarshal(new File(Configuration.getModelDescrFile()));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//populate ips for above levels
		//populateIps();
		
			try {			
				JAXBContext a = JAXBContext.newInstance( SYBLElasticityRequirementsDescription.class );
				Unmarshaller u  = a.createUnmarshaller();
				String directivePath = Configuration.getDirectivesPath();
				if (directivePath!=null){
			     syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal( this.getClass().getClassLoader().getResourceAsStream(directivePath)) ;
			    // syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal(new File(directivePath));
				}
				for (SYBLAnnotation syblAnnotation:parseXMLInjectedAnnotations(cloudServiceXML)){
					if (syblSpecifications==null)
						syblSpecifications=new SYBLElasticityRequirementsDescription();
					//SYBLDirectivesEnforcementLogger.logger.info("FOUND HERE THE STRATEGY "+syblAnnotation.getStrategies());
					//if (syblAnnotation.getStrategies()!=null && syblAnnotation.getStrategies()!="")
			    	syblSpecifications.getSyblSpecifications().add(SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(syblAnnotation));
			    }
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	private void loadModelFromString(String applicationDescr, String elasticityReq){
		 JAXBContext jc;
		    cloudServiceXML = null;
		try {			
			jc = JAXBContext.newInstance( CloudServiceXML.class );
			Unmarshaller u = jc.createUnmarshaller();
			


			//JAXBElement element=  u.unmarshal( new File(Configuration.getModelDescrFile()));
			// cloudS = (CloudServiceXML) element.getValue();
			 cloudServiceXML = (CloudServiceXML) u.unmarshal(new StringReader(applicationDescr));
			//File f = new File(Configuration.getModelDescrFile());
			//cloudServiceXML = (CloudServiceXML) u.unmarshal(new File(Configuration.getModelDescrFile()));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//populate ips for above levels
		//populateIps();
		
			try {			
				JAXBContext a = JAXBContext.newInstance( SYBLElasticityRequirementsDescription.class );
				Unmarshaller u  = a.createUnmarshaller();
				String directivePath = Configuration.getDirectivesPath();
				if (directivePath!=null){
			     syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal( new StringReader(elasticityReq)) ;
			    // syblSpecifications = (SYBLElasticityRequirementsDescription) u.unmarshal(new File(directivePath));
				}
				for (SYBLAnnotation syblAnnotation:parseXMLInjectedAnnotations(cloudServiceXML)){
					if (syblSpecifications==null)
						syblSpecifications=new SYBLElasticityRequirementsDescription();
					//SYBLDirectivesEnforcementLogger.logger.info("FOUND HERE THE STRATEGY "+syblAnnotation.getStrategies());
					//if (syblAnnotation.getStrategies()!=null && syblAnnotation.getStrategies()!="")
			    	syblSpecifications.getSyblSpecifications().add(SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(syblAnnotation));
			    }
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	public DependencyGraph constructDependencyGraph(){
		DependencyGraph graph = new DependencyGraph();
		Node cloudService = new Node();
		cloudService.setId(cloudServiceXML.getId());
		cloudService.setNodeType(NodeType.CLOUD_SERVICE);
		if (cloudServiceXML.getAnnotation()!=null){
		ElasticityRequirement elReq = new ElasticityRequirement();
		elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(cloudService.getId(),cloudServiceXML.getXMLAnnotation(),cloudServiceXML.getAnnotation().getAnnotationType()));
		}
		List<ServiceTopologyXML> remainingServiceTopologies = new ArrayList<ServiceTopologyXML>();
		HashMap<String,Node> nodes = new HashMap<String,Node>();
		HashMap<Node,Relationship> serviceTopologiesFirst = new HashMap<Node,Relationship>();
		for (ServiceTopologyXML serviceTopologyXML:cloudServiceXML.getServiceTopologies()){
		remainingServiceTopologies.add(serviceTopologyXML);
		ServiceTopologyXML firstServTopology = serviceTopologyXML;
		Node serviceTopologyFirst = new Node();
		serviceTopologyFirst.setId(firstServTopology.getId());
		serviceTopologyFirst.setNodeType(NodeType.SERVICE_TOPOLOGY);
		 Relationship rel = new Relationship();
		rel.setSourceElement(cloudService.getId());
		rel.setTargetElement(serviceTopologyFirst.getId());
		rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
		if (firstServTopology.getAnnotation()!=null){
			ElasticityRequirement elReq = new ElasticityRequirement();
			elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyFirst.getId(),firstServTopology.getXMLAnnotation(),firstServTopology.getAnnotation().getAnnotationType()));
			serviceTopologyFirst.getElasticityRequirements().add(elReq);
		}
		nodes.put(serviceTopologyFirst.getId(),serviceTopologyFirst);
		nodes.put(cloudService.getId(), cloudService);
		serviceTopologiesFirst.put(serviceTopologyFirst,rel);
		}
		while (!remainingServiceTopologies.isEmpty()){
			ServiceTopologyXML serviceTopologyXML=remainingServiceTopologies.get(0);
			Node serviceTopology  = new Node();
			if (nodes.containsKey(serviceTopologyXML.getId()))
			 serviceTopology = nodes.get(serviceTopologyXML.getId()); 
		
			if (serviceTopologyXML.getServiceUnits()!=null && !serviceTopologyXML.getServiceUnits().isEmpty())
				for (ServiceUnitXML serviceUnitXML:serviceTopologyXML.getServiceUnits()){
					Node serviceUnit = new Node();
					serviceUnit.setId(serviceUnitXML.getId());
					serviceUnit.setNodeType(NodeType.SERVICE_UNIT);
					Relationship  rel = new Relationship();
					rel.setSourceElement(serviceTopology.getId());
					rel.setTargetElement(serviceUnit.getId());
					rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
					if (serviceUnitXML.getAnnotation()!=null){
						ElasticityRequirement elReq = new ElasticityRequirement();
						elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceUnitXML.getId(),serviceUnitXML.getXMLAnnotation(),serviceUnitXML.getAnnotation().getAnnotationType()));
						serviceUnit.getElasticityRequirements().add(elReq);
					}
					
					if (serviceUnitXML.getActions()!=null && !serviceUnitXML.getActions().isEmpty()){
					for (ActionXML actionXML : serviceUnitXML.getActions()){
						ElasticityCapability elCapability = new ElasticityCapability();
						elCapability.setApiMethod(actionXML.getApiMethod());
						elCapability.setName(actionXML.getName());
						elCapability.setParameter(actionXML.getParameter());
						elCapability.setValue(actionXML.getValue());
						serviceUnit.addElasticityCapability(elCapability);
					}
					
				}
					serviceTopology.addNode(serviceUnit, rel);
					nodes.put(serviceUnit.getId(), serviceUnit);

				}
			if (serviceTopologyXML.getServiceTopology()!=null && !serviceTopologyXML.getServiceTopology().isEmpty())
				for (ServiceTopologyXML serviceTopologyXML2:serviceTopologyXML.getServiceTopology()){
					Node serviceTopology2 = new Node();
					serviceTopology2.setId(serviceTopologyXML2.getId());
					serviceTopology2.setNodeType(NodeType.SERVICE_TOPOLOGY);
					Relationship rel = new Relationship();
					rel.setSourceElement(serviceTopology.getId());
					rel.setTargetElement(serviceTopology2.getId());
					rel.setType(RelationshipType.COMPOSITION_RELATIONSHIP);
					if (serviceTopologyXML2.getAnnotation()!=null){
						ElasticityRequirement elReq = new ElasticityRequirement();
						elReq.setAnnotation(mapFromXMLAnnotationToSYBLAnnotation(serviceTopologyXML2.getId(),serviceTopologyXML2.getXMLAnnotation(),serviceTopologyXML2.getAnnotation().getAnnotationType()));
						serviceTopology2.getElasticityRequirements().add(elReq);
					}
					serviceTopology.addNode(serviceTopology2, rel);
					nodes.put(serviceTopology2.getId(), serviceTopology2);
					remainingServiceTopologies.add(serviceTopologyXML2);
				}
			nodes.put(serviceTopology.getId(), serviceTopology);
			remainingServiceTopologies.remove(0);
		}
		for (Entry<Node,Relationship> entry:serviceTopologiesFirst.entrySet() ){
		cloudService.addNode(entry.getKey(), entry.getValue());
		}
		graph.setCloudService(cloudService);
		DependencyGraphLogger.logger.info("Deployment Description "+deploymentDescription);
		DependencyGraphLogger.logger.info("Deployment Description "+deploymentDescription.getAccessIP());
		DependencyGraphLogger.logger.info("Static info cloud service "+cloudService.getStaticInformation());
		cloudService.getStaticInformation().put("AccessIP",deploymentDescription.getAccessIP());
		//
		
		//Populate with deployment information
		for (DeploymentUnit deploymentUnit :deploymentDescription.getDeployments()){
			
			Node node = graph.getNodeWithID(deploymentUnit.getServiceUnitID());
			if (node!=null){
				if (deploymentUnit.getDefaultFlavor()!=null)
				node.getStaticInformation().put("DefaultFlavor", deploymentUnit.getDefaultFlavor());
				if (deploymentUnit.getDefaultImage()!=null)
				node.getStaticInformation().put("DefaultImage", deploymentUnit.getDefaultImage());
			
				if (deploymentUnit.getAssociatedVM()!=null && deploymentUnit.getAssociatedVM().size()>0){
					for (AssociatedVM associatedVM:deploymentUnit.getAssociatedVM()){
					Node vmNode = new Node();
					vmNode.setId(associatedVM.getIp());
					vmNode.setNodeType(NodeType.VIRTUAL_MACHINE);
					Relationship vmRel = new Relationship();
					vmRel.setSourceElement(node.getId());
					vmRel.setTargetElement(vmNode.getId());
					vmRel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
					node.addNode(vmNode, vmRel);
					}
				}
			}else{
				DependencyGraphLogger.logger.error("Cannot find node "+deploymentUnit.getServiceUnitID()+". Current graph is "+graph.graphToString());
				
			}
			
			}
			
		
		
		//Populate with elasticity requirements information
		if (syblSpecifications.getSyblSpecifications()!=null)
		for (SYBLSpecification specification: syblSpecifications.getSyblSpecifications()){
			ElasticityRequirement elRequirement = new ElasticityRequirement();
			elRequirement.setAnnotation(SYBLDirectiveMappingFromXML.mapFromXMLRepresentation(specification));
			if (graph.getNodeWithID(specification.getComponentId())!=null)
			graph.getNodeWithID(specification.getComponentId()).addElasticityRequirement(elRequirement);
			else
				DependencyGraphLogger.logger.error("Specification targets entity which is not found: "+specification.getComponentId());
		}
		return graph;
	}
	public DependencyGraph loadDependencyGraphFromFile(){

		loadModelFromFile();
		loadDeploymentDescriptionFromFile();
		
		
		return constructDependencyGraph();
	}
	public DependencyGraph loadDependencyGraphFromStrings(String applicationDescription, String additionalElasticityRequirements, String deploymentInfo){

		loadModelFromString(applicationDescription,additionalElasticityRequirements);
		loadDeploymentDescriptionFromString(deploymentInfo);
		
		
		return constructDependencyGraph();
	}

	public SYBLAnnotation mapFromXMLAnnotationToSYBLAnnotation(String entityID,SYBLAnnotationXML syblAnnotationXML,SYBLAnnotation.AnnotationType annotationType){
		SYBLAnnotation syblannotation = new SYBLAnnotation();

			syblannotation=new SYBLAnnotation();
			syblannotation.setPriorities(syblAnnotationXML.getPriorities());
			syblannotation.setConstraints(syblAnnotationXML.getConstraints());
			syblannotation.setStrategies(syblAnnotationXML.getStrategies());
			syblannotation.setMonitoring(syblAnnotationXML.getMonitoring());
			syblannotation.setEntityID(entityID);
			syblannotation.setAnnotationType(annotationType);
		return syblannotation;
	}
	public List<SYBLAnnotation> parseXMLInjectedAnnotations(CloudServiceXML cloudService){
		boolean found=false;
		List<SYBLAnnotation> annotations = new ArrayList<SYBLAnnotation>();
		if (cloudService.getXMLAnnotation()!=null )
			annotations.add(mapFromXMLAnnotationToSYBLAnnotation(cloudService.getId(), cloudService.getXMLAnnotation(), SYBLAnnotation.AnnotationType.CLOUD_SERVICE));
		
		List<ServiceTopologyXML> topologies = new ArrayList<ServiceTopologyXML>();
		
		topologies.addAll(cloudService.getServiceTopologies());
		
		List<ServiceUnitXML> componentsToExplore = new ArrayList<ServiceUnitXML>();
		
		while (!found && !topologies.isEmpty()){
			ServiceTopologyXML currentTopology = topologies.get(0);
			topologies.remove(0);
			if (currentTopology.getXMLAnnotation()!=null )
				annotations.add(mapFromXMLAnnotationToSYBLAnnotation(currentTopology.getId(), currentTopology.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_TOPOLOGY));
		
				if (currentTopology.getServiceTopology()!=null && currentTopology.getServiceTopology().size()>0)
					topologies.addAll(currentTopology.getServiceTopology());
				if (currentTopology.getServiceUnits()!=null && currentTopology.getServiceUnits().size()>0)
					componentsToExplore.addAll(currentTopology.getServiceUnits());
		}
		
		while (!found && !componentsToExplore.isEmpty()){
			ServiceUnitXML component =componentsToExplore.get(0);
			
			if (component.getXMLAnnotation()!=null )
				annotations.add(mapFromXMLAnnotationToSYBLAnnotation(component.getId(), component.getXMLAnnotation(), SYBLAnnotation.AnnotationType.SERVICE_UNIT));
		
			componentsToExplore.remove(0);
			
		}
		
		return annotations;
	}
	
}
