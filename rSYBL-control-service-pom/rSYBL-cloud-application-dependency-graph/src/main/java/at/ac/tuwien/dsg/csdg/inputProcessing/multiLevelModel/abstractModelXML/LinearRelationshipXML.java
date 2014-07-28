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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinearElasticityRelationship", propOrder = {
    "servicePartID",
    "metricName","confidence","dependencyMonoms"
})
public class LinearRelationshipXML {
    @XmlElement(name="servicePart", required=false)
    private String servicePartID;
    @XmlElement(name="metricName", required=false)
    private String metricName;
    @XmlElement(name="confidence", required=false)
    private double confidence;
    /**
     * @return the servicePartID
     */
    public String getServicePartID() {
        return servicePartID;
    }

    /**
     * @param servicePartID the servicePartID to set
     */
    public void setServicePartID(String servicePartID) {
        this.servicePartID = servicePartID;
    }

    /**
     * @return the metricName
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * @param metricName the metricName to set
     */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * @return the dependencyMonoms
     */
    public List<Monom> getDependencyMonoms() {
        return dependencyMonoms;
    }

    /**
     * @param dependencyMonoms the dependencyMonoms to set
     */
    public void setDependencyMonoms(List<Monom> dependencyMonoms) {
        this.dependencyMonoms = dependencyMonoms;
    }

    /**
     * @return the confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

   
  
    
    @XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Monom", propOrder = {
    "coefficient","metricName","power",
    "servicePartID"
})
    public class Monom{
        	@XmlElement(name = "coefficient", required = true)
    private double coefficient;
        	@XmlElement(name = "metricName", required = true)
    private String metricName;
    
                @XmlElement(name = "power", required = false)
    private double power;
    @XmlElement(name = "servicePartID", required = false)
    private String servicePartID;
    
    
        /**
         * @return the coefficient
         */
        public double getCoefficient() {
            return coefficient;
        }

        /**
         * @param coefficient the coefficient to set
         */
        public void setCoefficient(double coefficient) {
            this.coefficient = coefficient;
        }

        /**
         * @return the servicePartID
         */
        public String getServicePartID() {
            return servicePartID;
        }

        /**
         * @param servicePartID the servicePartID to set
         */
        public void setServicePartID(String servicePartID) {
            this.servicePartID = servicePartID;
        }

        /**
         * @return the power
         */
        public double getPower() {
            return power;
        }

        /**
         * @param power the power to set
         */
        public void setPower(double power) {
            this.power = power;
        }

        /**
         * @return the metricName
         */
        public String getMetricName() {
            return metricName;
        }

        /**
         * @param metricName the metricName to set
         */
        public void setMetricName(String metricName) {
            this.metricName = metricName;
        }
    }
    
    @XmlElement(name="DependencyMonom")
    private List<Monom>  dependencyMonoms ;
    
     
}
