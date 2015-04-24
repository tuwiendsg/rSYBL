/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author Georgiana
 */
public class Interaction implements Serializable, Comparable<Interaction> {
    private String id;
    private String dialogId;
    private Date initiationDate;
    private IRole initiator;
    private IRole receiver;
    private Message message;

    @Override
    public int compareTo(Interaction o) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (o == this) {
            return EQUAL;
        }
        if (o.getInitiationDate().equals(this.getInitiationDate()) && o.getInitiator().getRoleName().equalsIgnoreCase(initiator.getRoleName())
                && o.getMessage().equals(message) && o.getReceiver().getRoleName().equalsIgnoreCase(receiver.getRoleName())) {
            return EQUAL;
        }
        if (this.getInitiationDate().before(o.getInitiationDate())) {
            return BEFORE;
        } else {
            return AFTER;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.initiationDate);
        hash = 59 * hash + Objects.hashCode(this.initiator);
        hash = 59 * hash + Objects.hashCode(this.receiver);
        hash = 59 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass() != Interaction.class) {
            return false;
        }
        Interaction o = (Interaction) object;
        if (!o.getInitiationDate().equals(this.getInitiationDate()) || !o.getInitiator().getRoleName().equalsIgnoreCase(initiator.getRoleName())
                || !o.getMessage().equals(message) || !o.getReceiver().getRoleName().equalsIgnoreCase(receiver.getRoleName())) {
            return false;
        }
        return true;
    }

    /**
     * @return the initiationDate
     */
    public Date getInitiationDate() {
        return initiationDate;
    }

    /**
     * @param initiationDate the initiationDate to set
     */
    public void setInitiationDate(Date initiationDate) {
        this.initiationDate = initiationDate;
    }

    /**
     * @return the initiator
     */
    public IRole getInitiator() {
        return initiator;
    }

    /**
     * @param initiator the initiator to set
     */
    public void setInitiator(IRole initiator) {
        this.initiator = initiator;
    }

    /**
     * @return the receiver
     */
    public IRole getReceiver() {
        return receiver;
    }

    /**
     * @param receiver the receiver to set
     */
    public void setReceiver(IRole receiver) {
        this.receiver = receiver;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the dialogId
     */
    public String getDialogId() {
        return dialogId;
    }

    /**
     * @param dialogId the dialogId to set
     */
    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

}
