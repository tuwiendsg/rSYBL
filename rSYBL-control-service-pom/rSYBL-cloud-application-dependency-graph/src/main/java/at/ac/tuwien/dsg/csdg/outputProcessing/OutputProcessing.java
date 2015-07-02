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
package at.ac.tuwien.dsg.csdg.outputProcessing;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLAnnotationXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Georgiana
 */
public class OutputProcessing implements OutputProcessingInterface {
    private static FileWriter fstream = null;
    
    static {
        try {
    
	
            if (Configuration.getFileForStoringActionPlans()!=null && !Configuration.getFileForStoringActionPlans().equalsIgnoreCase(""))
              fstream = new FileWriter(Configuration.getFileForStoringActionPlans());
            else
                fstream = new FileWriter("actionplans.csv");
        } catch (IOException ex) {
            Logger.getLogger(OutputProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public OutputProcessing(){
     	  
		 
    }
    public String getXMLRequirements(List<ElasticityRequirement> requirement){
        String reqs="";
        SYBLElasticityRequirementsDescription bLElasticityRequirementsDescription=new SYBLElasticityRequirementsDescription();
        for (ElasticityRequirement elasticityRequirement:requirement){
             bLElasticityRequirementsDescription.getSyblSpecifications().add(SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation()));
        }
        try{
            
        JAXBContext jaxbContext = JAXBContext.newInstance(SYBLElasticityRequirementsDescription.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter w = new StringWriter();

        marshaller.marshal(bLElasticityRequirementsDescription, w);
        reqs=w.toString();
        }catch(Exception e){
            DependencyGraphLogger.logger.error(e.getMessage());
        }
        return reqs;
    }
    private SYBLAnnotationXML getXMLAnnotationGivenReqs(Node n){
       
        if (n.getElasticityRequirements()!=null && n.getElasticityRequirements().size()>0){
             SYBLAnnotationXML annotationXML= new SYBLAnnotationXML();
        annotationXML.setConstraints(n.getElasticityRequirements().get(0).getAnnotation().getConstraints());
        annotationXML.setMonitoring(n.getElasticityRequirements().get(0).getAnnotation().getMonitoring());
        annotationXML.setStrategies(n.getElasticityRequirements().get(0).getAnnotation().getStrategies());
        return annotationXML;
        }
        return null;
    }
    public String getCloudServiceXML(Node controlledService){
         String res="";
        CloudServiceXML cloudServiceXML=new CloudServiceXML();
        cloudServiceXML.setId(controlledService.getId());
        cloudServiceXML.setXMLAnnotation(getXMLAnnotationGivenReqs(controlledService));
        
        List<Node> toExplore = new ArrayList<Node>();
        toExplore.addAll(controlledService.getAllRelatedNodesOfType(Relationship.RelationshipType.COMPOSITION_RELATIONSHIP,Node.NodeType.SERVICE_TOPOLOGY));
        List<ServiceTopologyXML> initialServiceTopologies = new ArrayList<ServiceTopologyXML>();
        for (Node n : toExplore){
            ServiceTopologyXML servTopologyXML = new ServiceTopologyXML();
            servTopologyXML.setId(n.getId());
            servTopologyXML.setXMLAnnotation(getXMLAnnotationGivenReqs(n));
            List<ServiceUnitXML> serviceUnits = new ArrayList<ServiceUnitXML>();
            for (Node servUnit:n.getAllRelatedNodesOfType(Relationship.RelationshipType.COMPOSITION_RELATIONSHIP, Node.NodeType.SERVICE_UNIT)){
                ServiceUnitXML serviceUnitXML = new ServiceUnitXML();
                serviceUnitXML.setId(servUnit.getId());
                serviceUnitXML.setXMLAnnotation(getXMLAnnotationGivenReqs(servUnit));
               serviceUnits.add(serviceUnitXML);
            }
            servTopologyXML.setServiceUnits(serviceUnits);
            initialServiceTopologies.add(servTopologyXML);
        }
        cloudServiceXML.setServiceTopologies(initialServiceTopologies);
        
        try{
            
        JAXBContext jaxbContext = JAXBContext.newInstance(CloudServiceXML.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter w = new StringWriter();

        marshaller.marshal(cloudServiceXML, w);
        res=w.toString();
        }catch(Exception e){
            DependencyGraphLogger.logger.error(e.getMessage());
        }
        return res;
    }
    @Override
    public void saveActionPlan(HashMap<Node,ElasticityCapability> actionPlan) {
       	try {
				  Date date = new Date();
			 if (Configuration.getFileForStoringActionPlans()!=null && !Configuration.getFileForStoringActionPlans().equalsIgnoreCase(""))
              fstream = new FileWriter(Configuration.getFileForStoringActionPlans(),true);
            else
                fstream = new FileWriter("actionplans.csv",true);
					/**
					 * monitoring sequence
					 */
				  String toWrite = date.toString()+",";
				  for (Entry<Node,ElasticityCapability> entry:actionPlan.entrySet())
					try {
						toWrite+=entry.getKey().getId()+":"+entry.getValue().getName();
					} catch (Exception e) {
                                                toWrite+="";
						// TODO Auto-generated catch block
						DependencyGraphLogger.logger.error("Elasticity capability "+entry.getValue().getName()+" not valid");
                                                fstream.close();
					}
                                  
                                  
				  fstream.append(toWrite+'\n');
				  fstream.close();
				  
        }catch(Exception e){
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(OutputProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }
           DependencyGraphLogger.logger.error("Error when writing the action plan");
        }
    }
    
}
