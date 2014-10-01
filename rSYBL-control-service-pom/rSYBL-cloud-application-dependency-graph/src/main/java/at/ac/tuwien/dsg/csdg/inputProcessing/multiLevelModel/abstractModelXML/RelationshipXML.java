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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Relationship", propOrder = {
    "source",
    "target","syblDirective","type","metricSource","metricTarget","focusMetric","id"
})
public class RelationshipXML {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6850194411292974553L;
	@XmlElement(name = "source", required = true)
    protected String source;
    @XmlElement(name = "target", required = true)
    protected String target;
    @XmlElement(name = "SYBLDirective")
	private SYBLAnnotationXML syblDirective;
    public static final  String[] relationshipTypes={"COMPOSITION_RELATIONSHIP","HOSTED_ON_RELATIONSHIP", "ASSOCIATED_AT_RUNTIME_RELATIONSHIP", "RUNS_ON", "MASTER_OF", "PEER_OF", "CONTROL","DATA","LOAD","INSTANTIATION","POLYNIMIAL_RELATIONSHIP"};
    
    @XmlAttribute(name = "type")
    protected String type;
    @XmlElement(name = "metricSource")
	private String metricSource;
    @XmlElement(name = "metricTarget")
	private String metricTarget;
    @XmlElement(name = "focusMetric")
  	private String focusMetric;

    @XmlAttribute(name = "id")
	private String id;
    
    

    /**
     * Gets the value of the master property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the master property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the slave property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the slave property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

	public SYBLAnnotationXML getSyblAnnotationXML() {
		return syblDirective;
	}

	public void setSyblAnnotationXML(SYBLAnnotationXML syblAnnotationXML) {
		this.syblDirective = syblAnnotationXML;
	}

	public String getMetricSource() {
		return metricSource;
	}

	public void setMetricSource(String metricSource) {
		this.metricSource = metricSource;
	}

	public String getMetricTarget() {
		return metricTarget;
	}

	public void setMetricTarget(String metricTarget) {
		this.metricTarget = metricTarget;
	}

	public String getRelationshipID() {
		return id;
	}

	public void setRelationshipID(String relationshipID) {
		this.id = relationshipID;
	}

	public String getFocusMetric() {
		return focusMetric;
	}

	public void setFocusMetric(String focusMetric) {
		this.focusMetric = focusMetric;
	}

	
}
