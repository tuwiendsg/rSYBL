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

package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription.MySchemaOutputResolver;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="CloudService", namespace="")
public class CloudServiceXML implements Serializable{

    @XmlElement(name = "ServiceTopology", required = true)
    protected List<ServiceTopologyXML> componentTopologies;
    protected List<String> associatedIps = new ArrayList<String>();


	
	 @XmlAttribute(name = "id")
	private String id;
	 @XmlElement(name = "SYBLDirective")
		private SYBLAnnotationXML syblAnnotationXML;
	 public SYBLAnnotationXML getXMLAnnotation(){
		 return syblAnnotationXML;
	 }
	 public void setXMLAnnotation(SYBLAnnotationXML annotation){
		 syblAnnotationXML=annotation;
	 }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
        
        @XmlElement(name = "LinearRelationship")
		private List<LinearRelationshipXML> linearRelationships;
          
    /**
     * @return the linearRelationships
     */
    public List<LinearRelationshipXML> getLinearRelationships() {
        return linearRelationships;
    }

    /**
     * @param linearRelationships the linearRelationships to set
     */
    public void setLinearRelationships(List<LinearRelationshipXML> linearRelationships) {
        this.linearRelationships = linearRelationships;
    }


    public CloudServiceXML(){
    	
    }
    /**
     * Gets the value of the componentTopology property.
     * 
     * @return
     *     possible object is
     *     {@link CloudServiceXML.ServiceTopologyXML }
     *     
     */
    public List<ServiceTopologyXML> getServiceTopologies() {
        return componentTopologies;
    }

    /**
     * Sets the value of the componentTopology property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloudServiceXML.ServiceTopologyXML }
     *     
     */
    public void setServiceTopologies(List<ServiceTopologyXML> value) {
        this.componentTopologies = value;
    }

  
    public List<String> getAssociatedIps() {
        if (associatedIps == null) {
            associatedIps = new ArrayList<String>();
        }
        return this.associatedIps;
    }
    public void setAssociatedIps(List<String> ips) {
        if (associatedIps == null) {
            associatedIps = new ArrayList<String>();
        }
        associatedIps=ips;
    }
    public  void generateXSD(String filename) throws Exception {
		 JAXBContext jaxbContext = JAXBContext.newInstance(CloudServiceXML.class);
		 SchemaOutputResolver sor = new MySchemaOutputResolver();
		 sor.createOutput("at.ac.tuwien.dsg.serviceDescription", filename);
		 jaxbContext.generateSchema(sor);
		 sor.createOutput("at.ac.tuwien.dsg.serviceDescription", filename);
	    }
    public class MySchemaOutputResolver extends SchemaOutputResolver {

	    public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException {
	        File file = new File(suggestedFileName);
	        StreamResult result = new StreamResult(file);
	        result.setSystemId(file.toURI().toURL().toString());
	        return result;
	    }

	}

}
