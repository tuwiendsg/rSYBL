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

package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeploymentUnit")
public class DeploymentUnit {
    @XmlAttribute(name = "defaultImage")
	private String defaultImage="";
 @XmlAttribute(name = "defaultFlavor")
 private String defaultFlavor="";
 @XmlAttribute(name = "serviceUnitID")
 private String serviceUnitID="";
 @XmlAttribute(name="contextualization")
 private String contextualization="";
   @XmlElement(name="AssociatedVM")
 private List<AssociatedVM> associatedVMs = new ArrayList<AssociatedVM>();
 @XmlElement(name = "Artifact")
 private List<Artifact> artifacts=new ArrayList<Artifact>();
 @XmlElement(name = "ElasticityCapability")
 private List<ElasticityCapability> elasticityCapabilities=new ArrayList<ElasticityCapability>();
public String getDefaultFlavor() {
	return defaultFlavor;
}
public void setDefaultFlavor(String defaultFlavor) {
	this.defaultFlavor = defaultFlavor;
}
public String getDefaultImage() {
	return defaultImage;
}
public void setDefaultImage(String defaultImage) {
	this.defaultImage = defaultImage;
}
public String getServiceUnitID() {
	return serviceUnitID;
}
public void setServiceUnitID(String serviceUnitID) {
	this.serviceUnitID = serviceUnitID;
}
public List<Artifact> getArtifacts() {
	return artifacts;
}
public void setArtifacts(List<Artifact> associatedVMs) {
	this.artifacts = associatedVMs;
}
public void addArtifact(Artifact associatedVMs) {
	this.artifacts.add(associatedVMs);
}
public List<ElasticityCapability> getElasticityCapabilities() {
	return elasticityCapabilities;
}
public void setElasticityCapabilities(List<ElasticityCapability> elasticityCapabilities) {
	this.elasticityCapabilities = elasticityCapabilities;
}
public void addElasticityCapability(ElasticityCapability elasticityCapability){
	this.elasticityCapabilities.add(elasticityCapability);
}

    /**
     * @return the associatedVMs
     */
    public List<AssociatedVM> getAssociatedVMs() {
        return associatedVMs;
    }

    /**
     * @param associatedVMs the associatedVMs to set
     */
    public void setAssociatedVMs(List<AssociatedVM> associatedVMs) {
        this.associatedVMs = associatedVMs;
    }

    /**
     * @return the contextualization
     */
    public String getContextualization() {
        return contextualization;
    }

    /**
     * @param contextualization the contextualization to set
     */
    public void setContextualization(String contextualization) {
        this.contextualization = contextualization;
    }
}
