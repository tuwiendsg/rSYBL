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

import java.io.Serializable;

public class SYBLAnnotation implements Serializable{
	private static final long serialVersionUID = 1L;
private String monitoring;
private String constraints;
private String strategies;
private String priorities;
private String notifications;
private String governanceScopes;
private AnnotationType annotationType;
private String entityID;

    /**
     * @return the notifications
     */
    public String getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the governanceScopes
     */
    public String getGovernanceScopes() {
        return governanceScopes;
    }

    /**
     * @param governanceScopes the governanceScopes to set
     */
    public void setGovernanceScopes(String governanceScopes) {
        this.governanceScopes = governanceScopes;
    }
public static enum AnnotationType{
	   CLOUD_SERVICE, SERVICE_UNIT, SERVICE_TOPOLOGY, CODE_REGION,RELATIONSHIP;
	 }
public String getStrategies() {
	return strategies;
}
public void setStrategies(String strategies) {
	this.strategies = strategies;
}
public String getPriorities() {
	return priorities;
}
public void setPriorities(String priorities) {
	this.priorities = priorities;
}
public String getConstraints() {
	return constraints;
}
public void setConstraints(String constraints) {
	this.constraints = constraints;
}
public String getMonitoring() {
	return monitoring;
}
public void setMonitoring(String monitoring) {
	this.monitoring = monitoring;
}
public AnnotationType getAnnotationType() {
	return annotationType;
}
public void setAnnotationType(AnnotationType annotationType) {
	this.annotationType = annotationType;
}
public String getEntityID() {
	return entityID;
}
public void setEntityID(String entityID) {
	this.entityID = entityID;
}
}
