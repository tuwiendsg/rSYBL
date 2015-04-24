/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces;

import java.util.Date;

/**
 *
 * @author Georgiana
 */
public interface IInteraction {

    public Date getInitiationDate() ;

    public void setInitiationDate(Date initiationDate) ;

    public IRole getInitiator();

    public void setInitiator(IRole initiator) ;

    public IRole getReceiver();
    

     public void setReceiver(IRole receiver) ;
     public IMessage getMessage() ;

     public void setMessage(IMessage message) ;

    public String getId() ;

    public void setId(String id) ;

    public String getDialogId() ;

    public void setDialogId(String dialogId);

}
