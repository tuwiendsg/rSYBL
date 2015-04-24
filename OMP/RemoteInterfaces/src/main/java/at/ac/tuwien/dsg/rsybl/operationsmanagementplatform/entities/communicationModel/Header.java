/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author Georgiana
 */
public class Header implements Serializable {
    private String id;
    private IRole initiator;
    private IRole receiver;

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
    
}
