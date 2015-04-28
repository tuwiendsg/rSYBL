/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.DialogDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.InteractionDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.RoleDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Dialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Message;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.interactWithController.CloudAMQPInteractions;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IInteractionManagementBeanRemote;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IInteractionManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementBeanRemote;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.OMPLogger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Georgiana
 */
@Stateful
@LocalBean
@Remote(IInteractionManagementBeanRemote.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class InteractionManagementSessionBean implements IInteractionManagementBeanRemote, IInteractionManagementSessionBean {

    @Resource
    private javax.transaction.UserTransaction userTransaction;
    @PersistenceContext
    protected EntityManager em;
    private CloudAMQPInteractions aMQPInteractions;
    private ActionListener actionListener=null;
    @PostConstruct
    public void init() {
        RoleDAO roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        List<IRole> roles = roleDAO.findAll();
        List<String> myRoles = new ArrayList<String>();
        for (IRole role: roles){
            myRoles.add(role.getRoleName());
        }
        aMQPInteractions = new CloudAMQPInteractions(this);
        aMQPInteractions.startListeningToMessages(myRoles);
    }

    public void createMessage() {

    }
    
    @Override
    public void createInteraction(IRole initiator, IRole receiver, String dialogID, IMessage message) {
        Interaction interaction = new Interaction();
        try {
            userTransaction.begin();
            interaction.setUuid(UUID.randomUUID().toString());
            interaction.setInitiationDate(new Date());
            if (dialogID != null && !dialogID.equalsIgnoreCase("")) {
                interaction.setDialogUuid(dialogID);
            } else {
                interaction.setDialogUuid(UUID.randomUUID().toString());
            }
            em.persist(message);
            interaction.setMessage(em.merge(message));
            interaction.setInitiator(em.merge(initiator));
            interaction.setReceiver(em.merge(receiver));
            em.persist(interaction);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
            }
            OMPLogger.logger.info("failed creating interaction ~~~~ " + e.getMessage());
        }
    }

    @Override
    public List<IInteraction> findAllInteractions() {
        InteractionDAO interactionDAO = new InteractionDAO();
        interactionDAO.setEntityManager(em);
        return interactionDAO.findAll();
    }

    @Override
    public List<IInteraction> findAllInteractionsForInitiator(String roleName) {
        Query query = em
                .createNamedQuery("selectInteractionsOnInitiators");
        query.setParameter("rolename", roleName);

        return (List<IInteraction>) query.getResultList();

    }

    @Override
    public List<IInteraction> findAllInteractionsForReceiver(String roleName) {
        Query query = em
                .createNamedQuery("selectInteractionsOnReceivers");
        query.setParameter("rolename", roleName);

        return (List<IInteraction>) query.getResultList();

    }
  @Override
    public List<IDialog> findAllDialogsForReceiver(String roleName) {
        Query query = em
                .createNamedQuery("selectDialogsOnReceivers");
        query.setParameter("rolename", roleName);

        return (List<IDialog>) query.getResultList();

    }  @Override
    public List<IDialog> findAllDialogsForInitiator(String roleName) {
        Query query = em
                .createNamedQuery("selectDialogsOnInitiators");
        query.setParameter("rolename", roleName);

        return (List<IDialog>) query.getResultList();

    }
    public void addActionListener(ActionListener actionListener){
        this.actionListener=actionListener;
        
    }
    public void refreshNeeded(){
        if (actionListener!=null){
        actionListener.actionPerformed(new ActionEvent(this, 0, "NEW INTERACTION"));
        }
    }
    public void processInteraction(at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction interaction) {
        try {
            userTransaction.begin();
            Interaction mappedInteraction = mapInteraction(interaction);
            DialogDAO dialogDAO = new DialogDAO();
            dialogDAO.setEntityManager(em);
           
            
             Dialog d = (Dialog) dialogDAO.findByUUID(mappedInteraction.getUuid());
            
            if (d==null){
               
                d = new Dialog();
                d.addInteraction(mappedInteraction);
                d.setUuid(mappedInteraction.getDialogUuid());
                em.persist(d);
            }
            em.flush();
            userTransaction.commit();
          
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
            }
            OMPLogger.logger.info("failed creating interaction ~~~~ " + e.getMessage());
        }
         refreshNeeded();
    }

    public Interaction mapInteraction(at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction interaction) {
        Interaction jpaInteraction = new Interaction();
//        jpaInteraction.setId(interaction.getId());
        jpaInteraction.setDialogUuid(interaction.getDialogId());
        jpaInteraction.setInitiationDate(interaction.getInitiationDate());
        jpaInteraction.setMessage(mapMessage(interaction.getMessage()));
        jpaInteraction.setUuid(interaction.getUuid());
        RoleDAO roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        IRole initiatorRole =roleDAO.findByRoleName(interaction.getInitiator().getRoleName());
        if (initiatorRole==null){
            Role r = new Role();
            r.setRoleName(interaction.getInitiator().getRoleName());
            em.persist(r);
            em.flush();
        }
        jpaInteraction.setInitiator(initiatorRole);
        
        jpaInteraction.setReceiver(roleDAO.findByRoleName(interaction.getReceiver().getRoleName()));
        em.persist(jpaInteraction);
        return jpaInteraction;
    }

    public at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Message mapMessage(at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Message myMessage) {
        Message message = new Message();
        message.setActionEnforced(myMessage.getActionEnforced());
        message.setCause(myMessage.getCause());
        message.setCloudServiceId(myMessage.getCloudServiceId());
        message.setDescription(myMessage.getDescription());
        message.setUuid(myMessage.getDescription());
        if (myMessage.getInteraction() != null) {
            message.setInteraction((IInteraction) mapInteraction(myMessage.getInteraction()));
        }
        message.setMessageType(myMessage.getMessageType());
        message.setPriority(myMessage.getPriority());
        message.setTargetPartId(myMessage.getTargetPartId());
        message.setValues(myMessage.getValues());
        em.persist(message);
        return message;
    }
    
    public IDialog getDialog(String dialogID){
        DialogDAO dialogDAO = new DialogDAO();
        dialogDAO.setEntityManager(em);
        return dialogDAO.findByUUID(dialogID);
    }

}
