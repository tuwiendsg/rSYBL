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

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SYBLSpecification }
     * 
     */
    public SYBLSpecification createSYBLSpecification() {
        return new SYBLSpecification();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Priority }
     * 
     */
    public Priority createSYBLSpecificationPriority() {
        return new Priority();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Priority.Condition }
     * 
     */
    public Condition createCondition() {
        return new Condition();
    }


    /**
     * Create an instance of {@link SYBLSpecification.Priority.Condition.BinaryRestriction }
     * 
     */
    public BinaryRestriction createBinaryRestriction() {
        return new BinaryRestriction();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Priority.Condition.BinaryRestriction.RightHandSide }
     * 
     */
    public BinaryRestriction.RightHandSide createBinaryRestrictionRightHandSide() {
        return new BinaryRestriction.RightHandSide();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Priority.Condition.BinaryRestriction.LeftHandSide }
     * 
     */
    public BinaryRestriction.LeftHandSide createBinaryRestrictionLeftHandSide() {
        return new BinaryRestriction.LeftHandSide();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Priority.ToEnforce }
     * 
     */
    public ToEnforce createToEnforce() {
        return new ToEnforce();
    }

  


    /**
     * Create an instance of {@link SYBLSpecification.Monitoring }
     * 
     */
    public Monitoring createMonitoring() {
        return new Monitoring();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Monitoring.Monitor }
     * 
     */
    public Monitor createMonitor() {
        return new Monitor();
    }

   

    /**
     * Create an instance of {@link SYBLSpecification.Strategy }
     * 
     */
    public Strategy createSYBLSpecificationStrategy() {
        return new Strategy();
    }

    /**
     * Create an instance of {@link SYBLSpecification.Constraint }
     * 
     */
    public Constraint createSYBLSpecificationConstraint() {
        return new Constraint();
    }

}
