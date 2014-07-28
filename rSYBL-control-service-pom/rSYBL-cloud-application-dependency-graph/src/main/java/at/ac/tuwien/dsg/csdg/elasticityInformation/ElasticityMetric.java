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

package at.ac.tuwien.dsg.csdg.elasticityInformation;

import at.ac.tuwien.dsg.csdg.PolynomialElasticityRelationship;
import java.util.ArrayList;
import java.util.List;

public class ElasticityMetric{
	private String metricName = "";
        private String servicePartID="";
	private Object value;
	private String measurementUnit="";
        
        private List<PolynomialElasticityRelationship> relationships = new ArrayList<PolynomialElasticityRelationship>();
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getMeasurementUnit() {
		return measurementUnit;
	}
	public void setMeasurementUnit(String measurementUnit) {
		this.measurementUnit = measurementUnit;
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
     * @return the relationships
     */
    public List<PolynomialElasticityRelationship> getRelationships() {
        return relationships;
    }

    /**
     * @param relationships the relationships to set
     */
    public void setRelationships(List<PolynomialElasticityRelationship> relationships) {
        this.relationships = relationships;
    }
     public void addRelationship(PolynomialElasticityRelationship relationship) {
        this.relationships.add(relationship);
    }
     public void addAllRelationship(List<PolynomialElasticityRelationship> relationships) {
        this.relationships.addAll(relationships);
    }
}
