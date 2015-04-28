/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public class Message implements Serializable {
    private Long id;
    private String uuid;
    private String messageType;
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
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
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
    public Interaction getInteraction() {
        return interaction;
    }

    /**
     * @param interaction the interaction to set
     */
    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
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
