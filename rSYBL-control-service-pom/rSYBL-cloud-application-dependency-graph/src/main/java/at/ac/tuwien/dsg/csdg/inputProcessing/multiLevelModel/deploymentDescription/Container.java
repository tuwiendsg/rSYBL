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
@XmlType(name = "Container")
public class Container {
@XmlAttribute(name = "Id")
    private String id = "";
    @XmlAttribute(name = "Name")
    private String name = "";
    @XmlAttribute(name = "Path")
    private String path = "";
    @XmlElement(name = "AssociatedVM")
    private AssociatedVM vm = new AssociatedVM();
 @XmlElement(name = "ElasticityCapability")
 private List<ElasticityCapability> elasticityCapabilities=new ArrayList<ElasticityCapability>();
    public String getName() {
        return name;
    }

    public void setName(String ip) {
        this.name = ip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String uuid) {
        this.path = uuid;
    }

    /**
     * @return the vm
     */
    public AssociatedVM getVm() {
        return vm;
    }

    /**
     * @param vm the vm to set
     */
    public void setVm(AssociatedVM vm) {
        this.vm = vm;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the elasticityCapabilities
     */
    public List<ElasticityCapability> getElasticityCapabilities() {
        return elasticityCapabilities;
    }

    /**
     * @param elasticityCapabilities the elasticityCapabilities to set
     */
    public void setElasticityCapabilities(List<ElasticityCapability> elasticityCapabilities) {
        this.elasticityCapabilities = elasticityCapabilities;
    }
    
}
