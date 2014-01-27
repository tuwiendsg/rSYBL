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

package at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BinaryRestriction">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LeftHandSide">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Metric">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="RightHandSide">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Metric">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                                     &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="UnaryRestriction">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Metric">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="ReferenceTo">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                           &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "binaryRestrictions",
    "unaryRestrictions"
})
public class Condition {

    @XmlElement(name = "BinaryRestrictionsConjunction", required = true) // in disjunctive normal form 
    protected List<BinaryRestrictionsConjunction> binaryRestrictions = new ArrayList<BinaryRestrictionsConjunction>();
    @XmlElement(name = "BinaryRestrictionsConjunction", required = true) // in disjunctive normal form 
    protected  List<UnaryRestrictionsConjunction> unaryRestrictions=new ArrayList<UnaryRestrictionsConjunction>();

    /**
     * Gets the value of the binaryRestriction property.
     * 
     * @return
     *     possible object is
     *     {@link SYBLSpecification.Strategy.Condition.BinaryRestriction }
     *     
     */
    public  List<BinaryRestrictionsConjunction> getBinaryRestriction() {
        return binaryRestrictions;
    }

    /**
     * Sets the value of the binaryRestriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link SYBLSpecification.Strategy.Condition.BinaryRestriction }
     *     
     */
    public void addBinaryRestrictionConjunction(  BinaryRestrictionsConjunction value) {
        this.binaryRestrictions.add(value);
    }

    /**
     * Gets the value of the unaryRestriction property.
     * 
     * @return
     *     possible object is
     *     {@link SYBLSpecification.Strategy.Condition.UnaryRestriction }
     *     
     */
    public  List<UnaryRestrictionsConjunction> getUnaryRestrictions() {
        return unaryRestrictions;
    }

    /**
     * Sets the value of the unaryRestriction property.
     * 
     * @param value
     *     allowed object is
     *     {@link SYBLSpecification.Strategy.Condition.UnaryRestriction }
     *     
     */
    public void addUnaryRestrictionConjunction( UnaryRestrictionsConjunction value) {
        this.unaryRestrictions.add(value);
    }

    public String toString(){
    	String res = "";
    	if (binaryRestrictions.size()>1)
    		res="(";
    	for (BinaryRestrictionsConjunction binaryRes:binaryRestrictions){
    		
    	for (BinaryRestriction restriction : binaryRes.getBinaryRestrictions()){
    		res+=restriction.toString()+" ";
    		if (binaryRes.getBinaryRestrictions().size()>1 && (!binaryRes.getBinaryRestrictions().get(binaryRes.getBinaryRestrictions().size()-1).equals(restriction))){
    			res+=" AND ";
    		}
    	}
    	if (binaryRestrictions.size()>1 && (!binaryRestrictions.get(binaryRes.getBinaryRestrictions().size()-1).equals(binaryRes))){
    		res+= ") OR (";
    	}else
    	{
    		res+= ")";
    	}
    	}
    	return res;
    }

   
}
