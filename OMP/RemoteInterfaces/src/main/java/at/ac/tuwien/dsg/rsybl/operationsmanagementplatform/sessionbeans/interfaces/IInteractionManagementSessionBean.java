/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Georgiana
 */
@Local
public interface IInteractionManagementSessionBean {
    public void createInteraction(IRole initiator, IRole receiver, String dialogID, IMessage message);
    public List<IInteraction> findAllInteractions();
     public List<IInteraction> findAllInteractionsForInitiator(String username) ;
    public List<IInteraction> findAllInteractionsForReceiver(String username);
}
