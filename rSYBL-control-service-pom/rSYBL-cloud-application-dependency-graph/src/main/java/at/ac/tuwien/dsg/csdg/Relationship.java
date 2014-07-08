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

import java.io.Serializable;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;

public class Relationship implements Serializable{
	private String sourceElement;// parent
	private String targetElement; //child
	private ElasticityRequirement requirement;
	private String id;
	private RelationshipType type = RelationshipType.COMPOSITION_RELATIONSHIP;
	public static enum RelationshipType{
		   COMPOSITION_RELATIONSHIP,HOSTED_ON_RELATIONSHIP, ASSOCIATED_AT_RUNTIME_RELATIONSHIP, RUNS_ON, MASTER_OF, PEER_OF,DATA,LOAD,INSTANTIATION;
		 }
	public String getSourceElement() {
		return sourceElement;
	}
	public void setSourceElement(String parent) {
		this.sourceElement = parent;
	}
	public String getTargetElement() {
		return targetElement;
	}
	public void setTargetElement(String child) {
		this.targetElement = child;
	}
	public RelationshipType getType() {
		return type;
	}
	public void setType(RelationshipType type) {
		this.type = type;
	}
	
	public ElasticityRequirement getRequirement() {
		return requirement;
	}

	public void setRequirement(ElasticityRequirement requirement) {
		this.requirement = requirement;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
