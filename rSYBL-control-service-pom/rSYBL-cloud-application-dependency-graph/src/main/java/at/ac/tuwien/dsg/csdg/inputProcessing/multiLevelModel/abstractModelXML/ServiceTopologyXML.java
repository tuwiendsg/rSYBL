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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceTopology", propOrder = {
	    "componentTopology",
	    "relationships","associatedIps","components","syblAnnotationXML",
	})
public  class ServiceTopologyXML {

	private static final long serialVersionUID = 1L;
	@XmlElement(name = "ServiceTopology")
    protected List<ServiceTopologyXML> componentTopology;
    @XmlElement(name = "Relationship", required = true)
    protected List<RelationshipXML> relationships = new ArrayList<RelationshipXML>();
    @XmlElement(name="associatedIps")
    protected List<String> associatedIps = new ArrayList<String>();
    @XmlElement(name = "ServiceUnit")
	private List<ServiceUnitXML> components;
  
	
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
    public List<ServiceTopologyXML> getServiceTopology() {
        if (componentTopology == null) {
            componentTopology = new ArrayList<ServiceTopologyXML>();
        }
        return this.componentTopology;
    }
    public void setServiceTopology(List<ServiceTopologyXML> a) {
         this.componentTopology = a;
    }

    /**
     * Gets the value of the relationship property.
     * 
     * @return
     *     possible object is
     *     {@link CloudServiceXML.ServiceTopologyXML.RelationshipXML }
     *     
     */
    public List<RelationshipXML> getRelationships() {
        return relationships;
    }

    /**
     * Sets the value of the relationship property.
     * 
     * @param value
     *     allowed object is
     *     {@link CloudServiceXML.ServiceTopologyXML.RelationshipXML }
     *     
     */
    public void setRelationships(List<RelationshipXML> value) {
        this.relationships = value;
    }
    public void addRelationship(RelationshipXML value) {
        this.relationships.add(value);
    }
 	public List<ServiceUnitXML> getServiceUnits() {
		return components;
	}
	public void setServiceUnits(List<ServiceUnitXML> components) {
		this.components = components;
	}
	public List<String> getAssociatedIps() {
		return associatedIps;
	}
	public void setAssociatedIps(List<String> associatedIps) {
		this.associatedIps = associatedIps;
	}

}