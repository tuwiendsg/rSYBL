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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Georgiana
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class CustomEvent implements IEvent{
    private Stage stage;
    private Type type ;
    private String cloudServiceID;
    private String target;
    private String message;
    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public Type getType() {
        return type;
    }

    
    @Override
    public String getServiceId() {
       return getCloudServiceID();
    }

    /**
     * @return the cloudServiceID
     */
    public String getCloudServiceID() {
        return cloudServiceID;
    }

    /**
     * @param cloudServiceID the cloudServiceID to set
     */
    public void setCloudServiceID(String cloudServiceID) {
        this.cloudServiceID = cloudServiceID;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
}
