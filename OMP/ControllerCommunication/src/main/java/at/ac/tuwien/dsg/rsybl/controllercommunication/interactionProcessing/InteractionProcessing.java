/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing;

import at.ac.tuwien.dsg.rsybl.controllercommunication.CommunicationManagement;
import at.ac.tuwien.dsg.rsybl.controllercommunication.SYBLControlClient;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Dialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Message;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class InteractionProcessing {

    private SYBLControlClient syblControlClient;
    private InitiateInteractions initiateInteractions;
    private HashMap<UUID, Dialog> ongoingDialogs = new HashMap<UUID, Dialog>();


    public InteractionProcessing(InitiateInteractions initiateInteractions) {
        this.initiateInteractions = initiateInteractions;
    }



    public void processNewInteraction(Interaction interaction) {
        Interaction interactionResponse = null;
        Message message = null;
        if (ongoingDialogs.containsKey(UUID.fromString(interaction.getDialogId()))) {
            ongoingDialogs.get(UUID.fromString(interaction.getDialogId())).addInteraction((IInteraction) interaction);
        } else {
            Dialog d = new Dialog();
            if (interaction.getDialogId() != null && !interaction.getDialogId().equalsIgnoreCase("")) {
                d.setUuid(interaction.getDialogId());
            } else {
                d.setUuid(UUID.randomUUID().toString());
            }

        }
        if (interaction.getMessage().getMessageType() == IMessage.MessageType.REQUEST) {
            switch (interaction.getMessage().getActionEnforced()) {
                case IMessage.RequestTypes.PAUSE_CONTROL:
                    syblControlClient.stopApplication(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.PREPARE_CONTROL:
                    syblControlClient.prepareControl(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.REMOVE_SERVICE:
                    syblControlClient.removeApplicationFromControl(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.REPLACE_REQUIREMENTS:
                    syblControlClient.replaceRequirements(interaction.getMessage().getDescription(), interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.REPLACE_CUSTOM_METRICS:
                    syblControlClient.replaceCompositionRules(interaction.getMessage().getDescription(), interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.RESUME_CONTROL:
                    syblControlClient.resumeControl(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.SEND_CUSTOM_METRICS:
                    syblControlClient.setMetricsCompositionRules(interaction.getMessage().getDescription(), interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.SEND_DEPLOYMENT_DESCRIPTION:
                    syblControlClient.setApplicationDeployment(interaction.getMessage().getDescription(), interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.SEND_SERVICE_DESCRIPTION:
                    syblControlClient.setApplicationDescription(interaction.getMessage().getDescription(), interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.START_CONTROL:
                    syblControlClient.startApplication(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.START_ENFORCEMENT:
                    syblControlClient.startEnforcement(interaction.getMessage().getCloudServiceId(), interaction.getMessage().getTargetPartId(),
                            interaction.getMessage().getDescription());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                   initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.START_TEST:
                    syblControlClient.startTest(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.UNDEPLOY_SERVICE:
                    syblControlClient.undeployService(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.GET_SERVICE:
                    String services = syblControlClient.getServices();
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogId(interaction.getDialogId());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription(services);

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                    
            }

        }
    }

    /**
     * @return the syblControlClient
     */
    public SYBLControlClient getSyblControlClient() {
        return syblControlClient;
    }

    /**
     * @param syblControlClient the syblControlClient to set
     */
    public void setSyblControlClient(SYBLControlClient syblControlClient) {
        this.syblControlClient = syblControlClient;
    }

   
}
