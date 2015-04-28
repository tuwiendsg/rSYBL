/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
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
public class Dialog implements Serializable,IDialog{
    private Set<IInteraction> interactions = new HashSet<>();
    
    private Long id;
    private Set<IRole> participants = new HashSet<>();
    private String uuid;
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

  



    @Override
    public String getUuid() {
       return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid=uuid;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
       return id;
    }

   

}
