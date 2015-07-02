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
package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author Georgiana
 */
@XmlSeeAlso({ ActionPlanEvent.class, ActionEvent.class, CustomEvent.class })
@XmlAccessorType(XmlAccessType.FIELD)
public interface IEvent extends Serializable{
    public enum Stage {
		START, FINISHED, FAILED
	}
	public enum Type {
		NOTIFICATION, ERROR, UNHEALTHY_SP,ELASTICITY_CONTROL
	}
    public Stage getStage();
    public Type getType();
    

	public String getServiceId() ;

        
}
