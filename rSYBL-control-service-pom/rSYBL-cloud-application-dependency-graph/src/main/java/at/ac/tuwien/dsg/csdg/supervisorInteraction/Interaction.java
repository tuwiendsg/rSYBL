/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.supervisorInteraction;

/**
 *
 * @author Georgiana
 */
public class Interaction {
    private Long ID;
    private Role sourceRole;
    private Role targetRole;
    private String messageDescription;
    private Object data;
    private Long referenceInteraction;
    


    public Role getSourceRole() {
        return sourceRole;
    }

    public void setSourceRole(Role sourceRole) {
        this.sourceRole = sourceRole;
    }

    public Role getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(Role targetRole) {
        this.targetRole = targetRole;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getMessageDescription() {
        return messageDescription;
    }

    public void setMessageDescription(String messageDescription) {
        this.messageDescription = messageDescription;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getReferenceInteraction() {
        return referenceInteraction;
    }

    public void setReferenceInteraction(Long referenceInteraction) {
        this.referenceInteraction = referenceInteraction;
    }
}
