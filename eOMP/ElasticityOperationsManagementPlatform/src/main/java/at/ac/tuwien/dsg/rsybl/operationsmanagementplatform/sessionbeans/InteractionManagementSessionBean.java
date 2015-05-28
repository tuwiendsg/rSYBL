/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans;

import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
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
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.mapper.MapToCommunicationObjects;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.interactWithController.CloudAMQPInteractions;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IInteractionManagementBeanRemote;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IInteractionManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementBeanRemote;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.OMPLogger;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.transaction.SystemException;
import javax.validation.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Georgiana
 */
@Stateless
@LocalBean
@Remote(IInteractionManagementBeanRemote.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class InteractionManagementSessionBean implements IInteractionManagementBeanRemote, IInteractionManagementSessionBean {

    @Resource
    private javax.transaction.UserTransaction userTransaction;
    @PersistenceContext
    protected EntityManager em;
    private CloudAMQPInteractions aMQPInteractions;
    private ActionListener actionListener = null;
    private List<String> services = new ArrayList<String>();
    private HashMap<String, HashMap<String, String>> response = new HashMap<String, HashMap<String, String>>();

    @PostConstruct
    public void init() {
        RoleDAO roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        List<IRole> roles = roleDAO.findAll();
        List<String> myRoles = new ArrayList<String>();
        for (IRole role : roles) {
            if (!role.getRoleName().equalsIgnoreCase("Elasticity Controller")) {
                myRoles.add(role.getRoleName());
            }
        }
        aMQPInteractions = new CloudAMQPInteractions(this);
        aMQPInteractions.startListeningToMessages(myRoles);

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

    }

    @Override
    public List<IDialog> findAllDialogsForInitiator(String roleName) {
        Query query = em
                .createNamedQuery("selectDialogsOnInitiators");
        query.setParameter("rolename", roleName);

        return (List<IDialog>) query.getResultList();

    }
        @Override
    public Date findEarliestDate() {
        Query query = em
                .createNamedQuery("selectEarliestInteractionDate");
      

        return (Date) query.getSingleResult();

    }
    public List<IDialog> findAllDialogsForRole(String roleName) {
        Query query = em
                .createNamedQuery("selectDialogs");
        query.setParameter("rolename", roleName);

        return (List<IDialog>) query.getResultList();

    }
    public List<IDialog> findAllDialogsForRoleWithType(String roleName, String type){
        
                  Query query = em
                .createNamedQuery("selectDialogsWithType");
        query.setParameter("rolename", roleName);
        query.setParameter("type", type);

        return (List<IDialog>) query.getResultList();
    }
    public void addActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;

    }

    public void refreshNeeded() {
        if (actionListener != null) {
            actionListener.actionPerformed(new ActionEvent(this, 0, "NEW INTERACTION"));
        }
    }

    public void findAllServices(String roleName) {

        RoleDAO roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        Interaction interaction = new Interaction();
        if (roleName.equalsIgnoreCase("")) {
            roleName = "Systems Administrator";
        }
        try {

            userTransaction.begin();
            interaction.setUuid(UUID.randomUUID().toString());
            interaction.setType(IInteraction.InteractionType.REQUEST);
            interaction.setInitiator(em.merge(roleDAO.findByRoleName(roleName)));
            interaction.setReceiver(em.merge(roleDAO.findByRoleName("Elasticity Controller")));
            interaction.setInitiationDate(new Date());
            Message message = new Message();
            message.setUuid(UUID.randomUUID().toString());
            message.setActionEnforced(IMessage.RequestTypes.GET_SERVICES);
            em.persist(message);
            interaction.setMessage(message);

            Dialog d = new Dialog();
            d.setUuid(UUID.randomUUID().toString());
            interaction.setDialogUuid(d.getUuid());
            em.persist(interaction);
            d.addInteraction(interaction);
            em.persist(d);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                OMPLogger.logger.info("Cannot create interaction for getting services");
                userTransaction.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(InteractionManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            this.aMQPInteractions.initiateInteraction(interaction.getReceiver().getRoleName(), MapToCommunicationObjects.mapFromInteraction(interaction));

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void postRequestForElasticityRequirements(String roleName, String serviceID, String dialogUUID) {

        Interaction interaction = new Interaction();
        if (roleName.equalsIgnoreCase("")) {
            roleName = "Systems Administrator";
        }
        try {

            userTransaction.begin();
            RoleDAO roleDAO = new RoleDAO();
            roleDAO.setEntityManager(em);
            interaction.setUuid(UUID.randomUUID().toString());
            interaction.setType(IInteraction.InteractionType.REQUEST);
            interaction.setInitiator(em.merge(roleDAO.findByRoleName(roleName)));
            interaction.setReceiver(em.merge(roleDAO.findByRoleName("Elasticity Controller")));
            interaction.setInitiationDate(new Date());
            Message message = new Message();
            message.setUuid(UUID.randomUUID().toString());
            message.setCloudServiceId(serviceID);
            message.setActionEnforced(IMessage.RequestTypes.GET_REQUIREMENTS);
            em.persist(message);
            interaction.setMessage(message);
            DialogDAO dialogDAO = new DialogDAO();
            dialogDAO.setEntityManager(em);
            Dialog d = (Dialog) dialogDAO.findByUUID(dialogUUID);

            if (d == null) {
                d = new Dialog();
                d.setUuid(UUID.randomUUID().toString());
            }
            interaction.setDialogUuid(d.getUuid());

            em.persist(interaction);
            d.addInteraction(interaction);
            em.persist(d);
            em.flush();
            userTransaction.commit();

        } catch (Exception e) {
            try {
                OMPLogger.logger.info("Cannot create interaction for getting services");
                userTransaction.rollback();
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(InteractionManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        try {
            this.aMQPInteractions.initiateInteraction(interaction.getReceiver().getRoleName(), MapToCommunicationObjects.mapFromInteraction(interaction));
            if (!response.containsKey(serviceID)) {
                response.put(serviceID, new HashMap<String, String>());
            }
            response.get(serviceID).put(IMessage.RequestTypes.GET_REQUIREMENTS, null);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public String[] getElasticityRequirements(String role, String serviceID) {
        if (response.containsKey(serviceID) && response.get(serviceID).containsKey(IMessage.RequestTypes.GET_REQUIREMENTS)) {

            if (response.get(serviceID).get(IMessage.RequestTypes.GET_REQUIREMENTS) != null) {
                String[] responseValue = response.get(serviceID).get(IMessage.RequestTypes.GET_REQUIREMENTS).split("~");
                return responseValue;

            }

        } else {
            this.postRequestForElasticityRequirements(role, serviceID, "");
        }
        return new String[1];
    }

    public void postRequestForServiceDescription(String roleName, String serviceID, String dialogUUID) {

        Interaction interaction = new Interaction();
        if (roleName.equalsIgnoreCase("")) {
            roleName = "Systems Administrator";
        }
        try {

            userTransaction.begin();
            RoleDAO roleDAO = new RoleDAO();
            roleDAO.setEntityManager(em);
            interaction.setUuid(UUID.randomUUID().toString());
            interaction.setType(IInteraction.InteractionType.REQUEST);
            interaction.setInitiator(em.merge(roleDAO.findByRoleName(roleName)));
            interaction.setReceiver(em.merge(roleDAO.findByRoleName("Elasticity Controller")));
            interaction.setInitiationDate(new Date());
            Message message = new Message();
            message.setUuid(UUID.randomUUID().toString());
            message.setCloudServiceId(serviceID);
            message.setActionEnforced(IMessage.RequestTypes.GET_SERVICE);
            em.persist(message);
            interaction.setMessage(message);
            DialogDAO dialogDAO = new DialogDAO();
            dialogDAO.setEntityManager(em);
            Dialog d = (Dialog) dialogDAO.findByUUID(dialogUUID);

            if (d == null) {
                d = new Dialog();
                d.setUuid(UUID.randomUUID().toString());
            }
            interaction.setDialogUuid(d.getUuid());

            em.persist(interaction);
            d.addInteraction(interaction);
            interaction.setDialogUuid(d.getUuid());
            em.persist(interaction);
            d.addInteraction(interaction);
            em.persist(d);
            em.flush();
            userTransaction.commit();

        } catch (Exception e) {
            try {
                OMPLogger.logger.info("Cannot create interaction for getting services");
                userTransaction.rollback();
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(InteractionManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        try {
            this.aMQPInteractions.initiateInteraction(interaction.getReceiver().getRoleName(), MapToCommunicationObjects.mapFromInteraction(interaction));
            if (!response.containsKey(serviceID)) {
                response.put(serviceID, new HashMap<String, String>());
            }
            response.get(serviceID).put(IMessage.RequestTypes.GET_SERVICE, null);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public CloudServiceXML getServiceDescription(String role, String serviceID) {
        if (response.containsKey(serviceID) && response.get(serviceID).containsKey(IMessage.RequestTypes.GET_SERVICE)) {
            if (response.get(serviceID).get(IMessage.RequestTypes.GET_SERVICE) != null) {
                JAXBContext jc;
                try {
                    jc = JAXBContext.newInstance(CloudServiceXML.class);
                    Unmarshaller unmarshaller = jc.createUnmarshaller();
                    CloudServiceXML service = (CloudServiceXML) unmarshaller.unmarshal(new StringReader(response.get(serviceID).get(IMessage.RequestTypes.GET_SERVICE)));
                    return service;
                } catch (JAXBException ex) {
                    Logger.getLogger(InteractionManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;

            }
        } else {
            this.postRequestForServiceDescription(role, serviceID, "");
        }
        return null;
    }
    public synchronized void initiateInteraction(String dialogUUID,String cloudService, String interactionType, String initiator, String receiver, String action, String description){

         Interaction interaction = new Interaction();

        try {

            userTransaction.begin();
            RoleDAO roleDAO = new RoleDAO();
            roleDAO.setEntityManager(em);
            interaction.setUuid(UUID.randomUUID().toString());
            interaction.setType(interactionType);
            interaction.setInitiator(em.merge(roleDAO.findByRoleName(initiator)));
            interaction.setReceiver(em.merge(roleDAO.findByRoleName(receiver)));
            interaction.setInitiationDate(new Date());
            Message message = new Message();
            message.setUuid(UUID.randomUUID().toString());
            message.setCloudServiceId(cloudService);
            message.setActionEnforced(action);
            message.setDescription(description.replace("\n", "").replace("\r", ""));
            em.persist(message);
            interaction.setMessage(message);
            DialogDAO dialogDAO = new DialogDAO();
            dialogDAO.setEntityManager(em);
            Dialog d = (Dialog) dialogDAO.findByUUID(dialogUUID);

            if (d == null) {
                d = new Dialog();
                d.setUuid(UUID.randomUUID().toString());
            }
            interaction.setDialogUuid(d.getUuid());

            em.persist(interaction);
            d.addInteraction(interaction);
            interaction.setDialogUuid(d.getUuid());
            em.persist(interaction);
            d.addInteraction(interaction);
            em.persist(d);
            em.flush();
            userTransaction.commit();

        } catch (Exception e) {
            try {
                OMPLogger.logger.info("Cannot create interaction for getting services");
                userTransaction.rollback();
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(InteractionManagementSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        if (interaction.getReceiver().getRoleName().equalsIgnoreCase("Elasticity Controller")){
        aMQPInteractions.initiateInteraction(interaction.getReceiver().getRoleName(), MapToCommunicationObjects.mapFromInteraction(interaction));
        }else{
              aMQPInteractions.initiateInteraction("role", MapToCommunicationObjects.mapFromInteraction(interaction));
      
        }
        response.get(interaction.getMessage().getCloudServiceId()).put(interaction.getMessage().getActionEnforced(), "No answer yet");
    }
    public synchronized void processInteraction(at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction interaction) {
        Interaction mappedInteraction = null;
        try {

            userTransaction.begin();
            mappedInteraction = mapInteraction(interaction);
            DialogDAO dialogDAO = new DialogDAO();
            dialogDAO.setEntityManager(em);

            Dialog d = (Dialog) dialogDAO.findByUUID(mappedInteraction.getDialogUuid());

            if (d == null) {

                d = new Dialog();
                //d.addInteraction(mappedInteraction);
                d.setUuid(mappedInteraction.getDialogUuid());
                
            }
            d.addInteraction(mappedInteraction);
            em.persist(d);
            em.flush();
            userTransaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (Exception ex) {
            }
            OMPLogger.logger.info("failed creating interaction ~~~~ " + e.getMessage());
        }
        if (interaction.getType().equalsIgnoreCase(IInteraction.InteractionType.REQUEST) && interaction.getMessage().getActionEnforced().equalsIgnoreCase(IMessage.RequestTypes.GET_SERVICES)) {
            if (interaction.getMessage().getDescription() != null && !interaction.getMessage().getDescription().equalsIgnoreCase("")) {
                for (String s : interaction.getMessage().getDescription().split(",")) {
                    if (!services.contains(s)) {
                        services.add(s);
                        postRequestForElasticityRequirements("",s, "");
                        postRequestForServiceDescription("", s,"");
                    }
                }
//                services.addAll(Arrays.asList(interaction.getMessage().getDescription().split(",")));
            }
        }
        if (interaction.getType().equalsIgnoreCase(IInteraction.InteractionType.REQUEST) && interaction.getMessage().getActionEnforced().equalsIgnoreCase(IMessage.RequestTypes.GET_SERVICE)) {
            if (interaction.getMessage().getDescription() != null && !interaction.getMessage().getDescription().equalsIgnoreCase("")) {
                response.get(interaction.getMessage().getCloudServiceId()).put(IMessage.RequestTypes.GET_SERVICE, interaction.getMessage().getDescription());
            }
        }
        if (interaction.getType().equalsIgnoreCase(IInteraction.InteractionType.REQUEST) && interaction.getMessage().getActionEnforced().equalsIgnoreCase(IMessage.RequestTypes.GET_REQUIREMENTS)) {
            if (interaction.getMessage().getDescription() != null && !interaction.getMessage().getDescription().equalsIgnoreCase("")) {
                response.get(interaction.getMessage().getCloudServiceId()).put(IMessage.RequestTypes.GET_REQUIREMENTS, interaction.getMessage().getDescription());
            }
        }
        refreshNeeded();
    }

    public Interaction mapInteraction(at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction interaction) {
        Interaction jpaInteraction = new Interaction();
//        jpaInteraction.setId(interaction.getId());
        jpaInteraction.setDialogUuid(interaction.getDialogUuid());
        jpaInteraction.setInitiationDate(interaction.getInitiationDate());
        jpaInteraction.setMessage(mapMessage((at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Message) interaction.getMessage()));
        jpaInteraction.setUuid(interaction.getUuid());
        jpaInteraction.setType(interaction.getType());
        RoleDAO roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        IRole initiatorRole = roleDAO.findByRoleName(interaction.getInitiator().getRoleName());
        if (initiatorRole == null) {
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
        message.setUuid(myMessage.getUuid());
        if (myMessage.getInteraction() != null) {
            message.setInteraction((IInteraction) mapInteraction((at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction) myMessage.getInteraction()));
        }
        message.setPriority(myMessage.getPriority());
        message.setTargetPartId(myMessage.getTargetPartId());
        message.setValues(myMessage.getValues());
        em.persist(message);
        return message;
    }

    public IDialog getDialog(String dialogID) {
        DialogDAO dialogDAO = new DialogDAO();
        dialogDAO.setEntityManager(em);
        return dialogDAO.findByUUID(dialogID);
    }

    /**
     * @return the services
     */
    public List<String> getServices() {
        if (services.isEmpty()) {
            findAllServices("");
        }
        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<IDialog> getDialogsForRoles(Set<IRole> roles) {
        List<IDialog> dialogs = new ArrayList<IDialog>();
        for (IRole role : roles) {
            List<IDialog> cD = findAllDialogsForRole(role.getRoleName());
            for (IDialog d : cD) {
                if (!dialogs.contains(d)) {
                    dialogs.add(d);
                }
            }
        }
        return dialogs;
    }

}
