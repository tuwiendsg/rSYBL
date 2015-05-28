/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.Constants;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.apache.openjpa.persistence.jdbc.Unique;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author Georgiana
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "selectInteractionsOnInitiators", 
			query = "SELECT o FROM Interaction o "+
                                "JOIN o.initiator AS r " +
                                "WHERE r.roleName = :rolename"),
    @NamedQuery(name = "selectInteractionsOnReceivers", 
			query = "SELECT o from Interaction o " +
                                "JOIN o.receiver AS r " +
                                "WHERE r.roleName = :rolename"),
    @NamedQuery(name = "selectEarliestInteractionDate", 
			query = "SELECT min(o.initiationDate) from Interaction o " )
})
public class Interaction implements IInteraction,Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Unique
    private String uuid;
    private String dialogUUID;
    private Date initiationDate;
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Role initiator;
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Role receiver;
    @OneToOne(cascade =  CascadeType.ALL)
    private Message message;
    private String type;
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
     * @return the dialogId
     */
    public String getDialogUuid() {
        return dialogUUID;
    }

    /**
     * @param dialogId the dialogId to set
     */
    public void setDialogUuid(String dialogUUID) {
        this.dialogUUID = dialogUUID;
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
        this.initiator = (Role) initiator;
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
        this.receiver = (Role) receiver;
    }

    /**
     * @return the message
     */
    public IMessage getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(IMessage message) {
        this.message = (Message) message;
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

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
   
}
