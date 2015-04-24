/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public class Dialog implements Serializable{
    private Set<IInteraction> interactions = new HashSet<>();
    
    private String id;
    private Set<IRole> participants = new HashSet<>();
    
    /**
     * @return the interactions
     */
    public Set<IInteraction> getInteractions() {
        return interactions;
    }

    /**
     * @param interactions the interactions to set
     */
    public void setInteractions(Set<IInteraction> interactions) {
        this.interactions =  interactions;
    }
    
    public void addInteraction(IInteraction interaction){
        interactions.add(interaction);
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
     * @return the participants
     */
    public Set<IRole> getParticipants() {
        return participants;
    }

    /**
     * @param participants the participants to set
     */
    public void setParticipants(Set<IRole> participants) {
        this.participants = participants;
    }
    
    public void addParticipants(IRole participant){
        participants.add(participant);
    }
}
