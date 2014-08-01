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
package at.ac.tuwien.dsg.csdg;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityMetric;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import java.util.ArrayList;
import java.util.List;


public class PolynomialElasticityRelationship extends Relationship{

    private List<Monom> polynom = new ArrayList<Monom>();
    private RelationshipType type = RelationshipType.POLYNIMIAL_RELATIONSHIP;
    private double confidence = 0.0;
    private String currentElasticityMetric = "";
    private String servicePartID="";
    private ElasticityRequirement requirement;
    /**
     * @return the polynom
     */
    public List<Monom> getPolynom() {
        return polynom;
    }

    public void addMonomeToPolynom(Monom monom) {
        polynom.add(monom);
    }

    public double findCoefficientForMetric(String metricName, String servicePartID) {
        for (Monom linearMonom : polynom) {
            if (linearMonom.getElasticityMetric().equalsIgnoreCase(metricName) && linearMonom.getServicePartID().equalsIgnoreCase(servicePartID)) {
                return linearMonom.getCoefficient();
            }
        }
        return 0;
    }
  public double findPowerForMetric(String metricName, String servicePartID) {
        for (Monom linearMonom : polynom) {
            if (linearMonom.getElasticityMetric().equalsIgnoreCase(metricName) && linearMonom.getServicePartID().equalsIgnoreCase(servicePartID)) {
                return linearMonom.getPower();
            }
        }
        return 0;
    }
    public void addCoefficientForMetric(String metricName, String servicePartID, double coefficient) {
        ElasticityMetric elasticityMetric = new ElasticityMetric();
        elasticityMetric.setMetricName(metricName);
        elasticityMetric.setServicePartID(servicePartID);

    }

    /**
     * @param polynom the polynom to set
     */
    public void setPolynom(List<Monom> polynom) {
        this.polynom = polynom;
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

    @Override
    public RelationshipType getType() {
        return type;
    }

    /**
     * @return the currentElasticityMetric
     */
    public String getCurrentElasticityMetric() {
        return currentElasticityMetric;
    }

    /**
     * @param currentElasticityMetric the currentElasticityMetric to set
     */
    public void setCurrentElasticityMetric(String currentElasticityMetric) {
        this.currentElasticityMetric = currentElasticityMetric;
    }

    /**
     * @return the targetNodeID
     */
    public String getServicePartID() {
        return servicePartID;
    }

    /**
     * @param targetNodeID the targetNodeID to set
     */
    public void setServicePartID(String targetNodeID) {
        this.servicePartID = targetNodeID;
    }

    /**
     * @return the requirement
     */
    public ElasticityRequirement getRequirement() {
        return requirement;
    }

    /**
     * @param requirement the requirement to set
     */
    public void setRequirement(ElasticityRequirement requirement) {
        this.requirement = requirement;
    }

  
    public class Monom {

        private double coefficient;
        private String elasticityMetric;
        private String servicePartID;
        private double power;
        public Monom() {
        }

        public Monom(double c, String elasticityMetric1,String servicePartID) {
            coefficient = c;
            elasticityMetric = elasticityMetric1;
            this.servicePartID=servicePartID;
            power =1;
        }
        public Monom(double c, String elasticityMetric1,String servicePartID, double power) {
            coefficient = c;
            elasticityMetric = elasticityMetric1;
               this.servicePartID=servicePartID;
               this.power=power;
        }
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
         * @return the elasticityMetric
         */
        public String getElasticityMetric() {
            return elasticityMetric;
        }

        /**
         * @param elasticityMetric the elasticityMetric to set
         */
        public void setElasticityMetric(String elasticityMetric) {
            this.elasticityMetric = elasticityMetric;
        }

        public int hashCode() {
            int hash = 3;
            hash = 7 * hash + elasticityMetric.hashCode();
            hash = 7 * hash + servicePartID.hashCode();
            hash = 7 * hash + Double.toString(power).hashCode();
            return hash;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Monom)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            Monom m = (Monom) obj;
        
            if (m.getElasticityMetric().equalsIgnoreCase(this.elasticityMetric) && servicePartID.equalsIgnoreCase(m.getServicePartID()) && power==m.getPower()) {
                return true;
            } else {
                return false;
            }

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
    }
    public double evaluateElasticityRelationshipGivenValues(DependencyGraph dependencyGraph){
        double result=0.0;
        for (Monom  linearMonom: polynom){
            if (linearMonom.elasticityMetric!=null && linearMonom.elasticityMetric!=null && !linearMonom.elasticityMetric.equalsIgnoreCase("") && linearMonom.servicePartID!=null)
            {
                Node node = dependencyGraph.getNodeWithID(linearMonom.servicePartID);
                Object value = node.getElasticityMetricValue(linearMonom.elasticityMetric);
                if (value!=null){
                     result+=linearMonom.coefficient*Math.pow((Double)value,linearMonom.power);   
                }
            }else{
                result+=linearMonom.coefficient;
            }
                }
        return result;
    }
}
