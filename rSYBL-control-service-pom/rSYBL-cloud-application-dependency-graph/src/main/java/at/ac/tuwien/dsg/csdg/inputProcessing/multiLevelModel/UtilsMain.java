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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Condition;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitor;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitoring;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.ToEnforce;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.LeftHandSide;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.RightHandSide;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.RelationshipXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLAnnotationXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;










public class UtilsMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	 InputProcessing inputProc = new InputProcessing();
	 //DependencyGraph dependencyGraph=inputProc.loadDependencyGraphFromFile();
	 //System.out.println(dependencyGraph.graphToString());
		new UtilsMain().writeDefaultData();
	}
	
	
	public void writeElasticityRequirementsDirectives(){
		SYBLElasticityRequirementsDescription elasticityRequirementsDescription = new SYBLElasticityRequirementsDescription();
		List<SYBLSpecification> syblSpecifications = new ArrayList<SYBLSpecification>();
		
		///Cassandra Controller
		SYBLSpecification spec = new SYBLSpecification();
		spec.setType("Component");
		spec.setComponentId("CassandraController");
		
		Constraint constraint = new Constraint();
		constraint.setId("Co1");
		Condition condition1 = new Condition();
		BinaryRestriction bRestriction = new BinaryRestriction();
		LeftHandSide leftHandSide = new LeftHandSide();
		RightHandSide rightHandSide = new RightHandSide();
		leftHandSide.setMetric("latency.average");
		rightHandSide.setMetric("10");
		bRestriction.setLeftHandSide(leftHandSide);
		bRestriction.setRightHandSide(rightHandSide);
		bRestriction.setType("lessThan");
		BinaryRestrictionsConjunction binaryRestrictions = new BinaryRestrictionsConjunction();
		binaryRestrictions.add(bRestriction);
		condition1.addBinaryRestrictionConjunction(binaryRestrictions);
		constraint.setToEnforce(condition1);
		spec.addConstraint(constraint);
		
		 constraint = new Constraint();
		constraint.setId("Co2");
		 condition1 = new Condition();
		 bRestriction = new BinaryRestriction();
		 leftHandSide = new LeftHandSide();
		 rightHandSide = new RightHandSide();
		leftHandSide.setMetric("cpu.usage");
		rightHandSide.setMetric("80");
		bRestriction.setLeftHandSide(leftHandSide);
		bRestriction.setRightHandSide(rightHandSide);
		bRestriction.setType("lessThan");
		 binaryRestrictions = new BinaryRestrictionsConjunction();
		binaryRestrictions.add(bRestriction);
	
		condition1.addBinaryRestrictionConjunction(binaryRestrictions);
		constraint.setToEnforce(condition1);
		spec.addConstraint(constraint);
		
	   syblSpecifications.add(spec);
	   //Cassandra db
	   spec = new SYBLSpecification();
		spec.setType("ServiceUnit");
		spec.setComponentId("CassandraDB");
		
		 constraint = new Constraint();
		constraint.setId("Co2");
		 condition1 = new Condition();
		 bRestriction = new BinaryRestriction();
		 leftHandSide = new LeftHandSide();
		 rightHandSide = new RightHandSide();
		leftHandSide.setMetric("latency.average");
		rightHandSide.setMetric("8");
		bRestriction.setLeftHandSide(leftHandSide);
		bRestriction.setRightHandSide(rightHandSide);
		bRestriction.setType("lessThan");
		 binaryRestrictions = new BinaryRestrictionsConjunction();
			binaryRestrictions.add(bRestriction);
		condition1.addBinaryRestrictionConjunction(binaryRestrictions);
		constraint.setToEnforce(condition1);
		spec.addConstraint(constraint);
		
		 constraint = new Constraint();
			constraint.setId("Co4");
			 condition1 = new Condition();
			 bRestriction = new BinaryRestriction();
			 leftHandSide = new LeftHandSide();
			 rightHandSide = new RightHandSide();
			leftHandSide.setMetric("cpu.usage");
			rightHandSide.setMetric("70");
			bRestriction.setLeftHandSide(leftHandSide);
			bRestriction.setRightHandSide(rightHandSide);
			bRestriction.setType("lessThan");
			 binaryRestrictions = new BinaryRestrictionsConjunction();
				binaryRestrictions.add(bRestriction);
			condition1.addBinaryRestrictionConjunction(binaryRestrictions);
			constraint.setToEnforce(condition1);
			spec.addConstraint(constraint);
		
		syblSpecifications.add(spec);
		
		 spec = new SYBLSpecification();
			spec.setType("ServiceTopology");
			spec.setComponentId("DataServiceTopology");
			
			 constraint = new Constraint();
			constraint.setId("Co3");
			 condition1 = new Condition();
			 bRestriction = new BinaryRestriction();
			 leftHandSide = new LeftHandSide();
			 rightHandSide = new RightHandSide();
			leftHandSide.setMetric("cost");
			rightHandSide.setMetric("800");
			bRestriction.setLeftHandSide(leftHandSide);
			bRestriction.setRightHandSide(rightHandSide);
			bRestriction.setType("lessThan");
			 binaryRestrictions = new BinaryRestrictionsConjunction();
				binaryRestrictions.add(bRestriction);
			condition1.addBinaryRestrictionConjunction(binaryRestrictions);
			constraint.setToEnforce(condition1);
			spec.addConstraint(constraint);
			syblSpecifications.add(spec);
			syblSpecifications.add(spec);
			
			spec = new SYBLSpecification();
			spec.setType("ServiceTopology");
			spec.setComponentId("CassandraTopology");
			
			 constraint = new Constraint();
			constraint.setId("Co6");
			 condition1 = new Condition();
			 bRestriction = new BinaryRestriction();
			 leftHandSide = new LeftHandSide();
			 rightHandSide = new RightHandSide();
			leftHandSide.setMetric("cost");
			rightHandSide.setMetric("800");
			bRestriction.setLeftHandSide(leftHandSide);
			bRestriction.setRightHandSide(rightHandSide);
			bRestriction.setType("lessThan");
			 binaryRestrictions = new BinaryRestrictionsConjunction();
				binaryRestrictions.add(bRestriction);
			condition1.addBinaryRestrictionConjunction(binaryRestrictions);
			constraint.setToEnforce(condition1);
			spec.addConstraint(constraint);
			
			
			
			Strategy strategy = new Strategy();
			strategy.setId("St1");
			Condition condition2 = new Condition();
			
			BinaryRestriction bRestriction2 = new BinaryRestriction();
			LeftHandSide leftHandSide2 = new LeftHandSide();
			RightHandSide rightHandSide2 = new RightHandSide();
			leftHandSide2.setMetric("cpu.usage");
			rightHandSide2.setNumber("80");
			bRestriction2.setLeftHandSide(leftHandSide);
			bRestriction2.setRightHandSide(rightHandSide);
			bRestriction2.setType("greaterThan");
			binaryRestrictions = new BinaryRestrictionsConjunction();
			binaryRestrictions.add(bRestriction);
			condition2.addBinaryRestrictionConjunction(binaryRestrictions);
			
			ToEnforce enforce = new ToEnforce();
			enforce.setActionName("scaleout");
			
			strategy.setCondition(condition2);
			strategy.setToEnforce(enforce);
			
			Monitoring monitoring = new Monitoring();
			monitoring.setId("Mo1");
			Monitor m = new Monitor();
			m.setEnvVar("cpuUsage");
			m.setMetric("cpu.usage");
			monitoring.setMonitor(m);
					
			spec.addStrategy(strategy);
			spec.addMonitoring(monitoring);
			syblSpecifications.add(spec);
			
			elasticityRequirementsDescription.setSyblSpecifications(syblSpecifications);
			 JAXBContext jc;
			   try{
				   jc = JAXBContext.newInstance(SYBLElasticityRequirementsDescription.class);
				   Marshaller marshaller = jc.createMarshaller();
				   marshaller.marshal(elasticityRequirementsDescription,new File("newElasticityRequirementsSpecification.xml"));
			   }catch(Exception e){
				   e.printStackTrace();
			   }
			   
	}
	
	
	


	
	public void writeDefaultData(){
		   JAXBContext jc;
		   try{
			   jc = JAXBContext.newInstance(CloudServiceXML.class);
			   Marshaller m = jc.createMarshaller();
			   CloudServiceXML c = new CloudServiceXML();
			   c.setId("CloudService");
			   
			   ServiceTopologyXML webServiceTopology = new ServiceTopologyXML();
			   webServiceTopology.setId("WebServiceTopology");
			   List<ServiceUnitXML> components1 = new ArrayList<ServiceUnitXML>();
			   ServiceUnitXML loadBalancer = new ServiceUnitXML();
			   ServiceUnitXML webService = new ServiceUnitXML();
			   //loadBalancer.setDefaultImage("6ed288e8-3da1-48f8-abd2-1d45ee7e77a5");
			   
			   loadBalancer.setId("LoadBalancer");
			   SYBLAnnotationXML annotation=new SYBLAnnotationXML();
			   
			   annotation.setStrategies("St1:STRATEGY CASE responseTime < 360 ms AND throughput_average < 300 : scalein");
			   webService.setXMLAnnotation(annotation);
			   //webService.setAssociatedOpenstackSnapshot("hadoopSlave");
			   webService.setId("WebService");
			   //webService.setDefaultImage("");
			   components1.add(loadBalancer);
			   RelationshipXML relationshipXML = new RelationshipXML();
			   relationshipXML.setSource("LoadBalancer");
			   relationshipXML.setTarget("WebService");
			   relationshipXML.setType("DATA");
			   relationshipXML.setMetricSource("dataIoT");
			   relationshipXML.setMetricTarget("dataCloud");
			   relationshipXML.setRelationshipID("DataRelationship");
			   webServiceTopology.addRelationship(relationshipXML);
			   components1.add(webService);
			   annotation=new SYBLAnnotationXML();
			   
			   annotation.setStrategies("St1:STRATEGY CASE responseTime < 360 ms AND throughput_average < 300 : scalein");
			   webService.setXMLAnnotation(annotation);
			   annotation=new SYBLAnnotationXML();
			   
			   annotation.setConstraints("Co3:CONSTRAINT responseTime < 450 ms");
			   webServiceTopology.setXMLAnnotation(annotation);

			   webServiceTopology.setServiceUnits(components1);
			   
			   ServiceTopologyXML dataServiceTopology = new ServiceTopologyXML();
			   ServiceTopologyXML cassandraComponentTopology = new ServiceTopologyXML();
			   
			   ServiceUnitXML cassandraController = new ServiceUnitXML();
			   //cassandraController.setDefaultImage("74c744c9-e981-44ce-a8d3-df12028aff37");
			   cassandraController.setId("CassandraController");
			   //cassandraController.setGetFlavorID("2");
			   
			   ServiceUnitXML cassandraNode = new ServiceUnitXML();
			   //cassandraNode.setDefaultImage("be6b1aa4-2227-4f4d-9c63-7aabc9cc3a85");
			   cassandraNode.setId("CassandraNode");
			   //cassandraNode.setGetFlavorID("2");
			   List<ServiceUnitXML> comps  = new ArrayList<ServiceUnitXML>();
			   comps.add(cassandraController);
			   comps.add(cassandraNode);
			   cassandraComponentTopology.setId("DataServiceTopology");
			   cassandraComponentTopology.setServiceUnits(comps);
			   RelationshipXML cassandraRelationship = new RelationshipXML();
			   cassandraRelationship.setSource("CassandraController");
			   cassandraRelationship.setTarget("CassandraNode");		   
			   cassandraComponentTopology.addRelationship(cassandraRelationship);
			   List<ServiceTopologyXML> componentTopologiesCassandra = new ArrayList<ServiceTopologyXML>();
			   componentTopologiesCassandra.add(cassandraComponentTopology);
			   
			   dataServiceTopology.setServiceTopology(componentTopologiesCassandra);
			   annotation=new SYBLAnnotationXML();
			   annotation.setConstraints("Co1:CONSTRAINT latency.average < 30 ms; Co2:CONSTRAINT cpu.usage < 80 %");
			   
			   
			   dataServiceTopology.setXMLAnnotation(annotation);
			   annotation=new SYBLAnnotationXML();
			   annotation.setConstraints("Co4:CONSTRAINT cost.PerHour < 60 Euro; Co5:CONSTRAINT cost.PerClientPerHour < 3 Euro");	
			   c.setXMLAnnotation(annotation);
			   ServiceTopologyXML mainTopology = new ServiceTopologyXML();
			   List<ServiceTopologyXML> topologies = new ArrayList<ServiceTopologyXML>();
			   topologies.add(webServiceTopology);
			   dataServiceTopology.setId("DataServiceTopology");
			   topologies.add(dataServiceTopology);
			   mainTopology.setId("MainTopology");
			   mainTopology.setServiceTopology(topologies);
			   List<ServiceTopologyXML> servTop = new ArrayList<ServiceTopologyXML>();
			   servTop.add(mainTopology);
			   c.setServiceTopologies(servTop);
			  /**
			    * Add connection data
			    * <ConnectionData>
				<User name="CLOUD_USER" val="CELAR:ecopil"/>
				<Password name="CLOUD_PASSWORD" val="Aeb2Piec"/>
				<Certificate name="PEM_CERT_PATH" val="./keypair_1.pem"/>
				<Ganglia port="8650" ip="128.130.172.213" />
				<KeyPairName name="MACHINE_ACCESS_KEY_PAIR_NAME" val= "keypair_1"/>
				<DefaultImage id="e9832752-f945-47aa-80f9-cf053589d0cb">
				<DefaultFlavor id="2"/>
				</DefaultImage>
				<CloudAPI type="openstack-nova" endpoint="http://openstack.infosys.tuwien.ac.at:5000/v2.0" />
				</ConnectionData>
			    *
			    */
			  
			   
			   	   
			   
			   m.marshal(c, new File("newServiceDescription.xml"));
				 
		   }catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
