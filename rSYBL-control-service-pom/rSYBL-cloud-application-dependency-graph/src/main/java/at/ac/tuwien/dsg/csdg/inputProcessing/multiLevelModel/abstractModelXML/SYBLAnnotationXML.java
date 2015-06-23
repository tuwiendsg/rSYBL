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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SYBLDirective")
public class SYBLAnnotationXML {
       private String entityID;
	   @XmlAttribute(name = "Constraints")
	   private String constraints ="";
	   	@XmlAttribute(name="Monitoring")
		private String monitoring ="";
	   	@XmlAttribute (name="Priorities")
	   	private String priorities ="";
	   	@XmlAttribute(name="Strategies")
		private String strategies ="";
                @XmlAttribute(name="GovernanceScopes")
                private String governanceScope="";
                @XmlAttribute(name="Notifications")
		private String notifications ="";

		public String getStrategies() {
			return strategies;
		}

		public void setStrategies(String strategies) {
			this.strategies = strategies;
		}

		public String getMonitoring() {
			return monitoring;
		}

		public void setMonitoring(String monitoring) {
			this.monitoring = monitoring;
		}

		public String getConstraints() {
			return constraints;
		}

		public void setConstraints(String constraint) {
			this.constraints = constraint;
		}

		public String getEntityID() {
			return entityID;
		}

		public void setEntityID(String entityID) {
			this.entityID = entityID;
		}

		public String getPriorities() {
			return priorities;
		}

		public void setPriorities(String priorities) {
			this.priorities = priorities;
		}

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
     * @return the governanceScope
     */
    public String getGovernanceScope() {
        return governanceScope;
    }

    /**
     * @param governanceScope the governanceScope to set
     */
    public void setGovernanceScope(String governanceScope) {
        this.governanceScope = governanceScope;
    }
	   	
	   	
	   	

}
