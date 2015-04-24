/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public interface IDialog {

    public Set<IInteraction> getInteractions();
    public void setInteractions(Set<IInteraction> interactions) ;
    public void addInteraction(IInteraction interaction);
    public String getId() ;
    public void setId(String id);
    public Set<IRole> getParticipants() ;
    public void setParticipants(Set<IRole> participants) ;
    public void addParticipants(IRole participant);
}
