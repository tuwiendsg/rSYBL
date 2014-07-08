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

package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElasticityPrimitive")
public class ElasticityPrimitive {
    @XmlAttribute(name = "id")
	private String id="";
 @XmlAttribute(name = "name")
 private String name="";
 @XmlAttribute(name = "methodName",required=false)
 private String methodName="";
 
 @XmlAttribute(name = "parameters", required=false)
 private String parameters="";
 @XmlElement(name="PrimitiveDependency")
 private List<ElasticityPrimitiveDependency> primitiveDependencies = new ArrayList<ElasticityPrimitiveDependency>();
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getParameters() {
	return parameters;
}
public void setParameters(String parameters) {
	this.parameters = parameters;
}
public List<ElasticityPrimitiveDependency> getPrimitiveDependencies() {
	return primitiveDependencies;
}
public void setPrimitiveDependencies(List<ElasticityPrimitiveDependency> primitiveDependencies) {
	this.primitiveDependencies = primitiveDependencies;
}
public void addPrimitiveDependency(ElasticityPrimitiveDependency dependency){
	primitiveDependencies.add(dependency);
}
public String getMethodName() {
	return methodName;
}
public void setMethodName(String methodName) {
	this.methodName = methodName;
}

}
