/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import org.apache.openjpa.persistence.jdbc.Unique;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author Georgiana
 */
@NamedQueries( {
    @NamedQuery(name = "selectDialogsOnInitiators", 
			query = "SELECT o FROM Dialog o "+
                                "JOIN o.interactions AS i "+
                                "JOIN i.initiator AS r " +
                                "WHERE r.roleName = :rolename"),
    @NamedQuery(name = "selectDialogsOnReceivers", 
			query = "SELECT o FROM Dialog o "+
                                "JOIN o.interactions AS i "+
                                "JOIN i.receiver AS r " +
                                "WHERE r.roleName = :rolename")

})
@Entity
public class Dialog implements IDialog, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Unique
    private String uuid;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Interaction> interactions = new HashSet<>();


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the interactions
     */
    public Set<IInteraction> getInteractions() {
        return (Set<IInteraction>) (Set<?>)interactions;
    }

    /**
     * @param interactions the interactions to set
     */
    @Override
    public void setInteractions(Set<IInteraction> interactions) {
        this.interactions = (Set<Interaction>) (Set<?>) interactions;
    }

  
    @Override
    public void addInteraction(IInteraction role){
        interactions.add((Interaction) role);
                }/**
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
