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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public class Message implements Serializable,IMessage {
    private Long id;
    private String uuid;
    private String priority;
    private String cloudServiceId;
    private String cause;
    private String actionEnforced;
    private Interaction interaction;
    private Set<Double> values;
    private String description;
    private String targetPartId;

    /**
     * @return the cloudServiceId
     */
    public String getCloudServiceId() {
        return cloudServiceId;
    }

    /**
     * @param cloudServiceId the cloudServiceId to set
     */
    public void setCloudServiceId(String cloudServiceId) {
        this.cloudServiceId = cloudServiceId;
    }

    /**
     * @return the values
     */
    public Set<Double> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(Set<Double> values) {
        this.values = values;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the targetPartId
     */
    public String getTargetPartId() {
        return targetPartId;
    }

    /**
     * @param targetPartId the targetPartId to set
     */
    public void setTargetPartId(String targetPartId) {
        this.targetPartId = targetPartId;
    }


   

    /**
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return the cause
     */
    public String getCause() {
        return cause;
    }

    /**
     * @param cause the cause to set
     */
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * @return the interaction
     */
    public IInteraction getInteraction() {
        return (IInteraction) interaction;
    }

    /**
     * @param interaction the interaction to set
     */
    @Override
    public void setInteraction(IInteraction interaction) {
        this.interaction = (Interaction) interaction;
    }

    /**
     * @return the actionEnforced
     */
    public String getActionEnforced() {
        return actionEnforced;
    }

    /**
     * @param actionEnforced the actionEnforced to set
     */
    public void setActionEnforced(String actionEnforced) {
        this.actionEnforced = actionEnforced;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
