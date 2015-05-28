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
import java.util.Date;
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
        syblControlClient = new SYBLControlClient("http://localhost:8280/rSYBL/restWS");
    }

    public void processNewInteraction(Interaction interaction) {
        Interaction interactionResponse = null;
        Message message = null;
        if (ongoingDialogs.containsKey(UUID.fromString(interaction.getDialogUuid()))) {
            ongoingDialogs.get(UUID.fromString(interaction.getDialogUuid())).addInteraction((IInteraction) interaction);
        } else {
            Dialog d = new Dialog();
            if (interaction.getDialogUuid() != null && !interaction.getDialogUuid().equalsIgnoreCase("")) {
                d.setUuid(interaction.getDialogUuid());
            } else {
                d.setUuid(UUID.randomUUID().toString());
            }

        }
        if (interaction.getType().equalsIgnoreCase(IInteraction.InteractionType.REQUEST)) {
            switch (interaction.getMessage().getActionEnforced()) {
                case IMessage.RequestTypes.PAUSE_CONTROL:
                    syblControlClient.stopApplication(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PAUSE_CONTROL);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());
                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.PREPARE_CONTROL:
                    syblControlClient.prepareControl(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.PREPARE_CONTROL);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.REMOVE_SERVICE:
                    syblControlClient.removeApplicationFromControl(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.REMOVE_SERVICE);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.REPLACE_REQUIREMENTS:
                    syblControlClient.replaceRequirements(interaction.getMessage().getCloudServiceId(), interaction.getMessage().getDescription());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.REPLACE_REQUIREMENTS);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.REPLACE_CUSTOM_METRICS:
                    syblControlClient.replaceCompositionRules(interaction.getMessage().getCloudServiceId(), interaction.getMessage().getDescription());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.REPLACE_CUSTOM_METRICS);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.RESUME_CONTROL:
                    syblControlClient.resumeControl(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.RESUME_CONTROL);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.SEND_CUSTOM_METRICS:
                    syblControlClient.setMetricsCompositionRules(interaction.getMessage().getCloudServiceId(), interaction.getMessage().getDescription());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.SEND_CUSTOM_METRICS);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.SEND_DEPLOYMENT_DESCRIPTION:
                    syblControlClient.setApplicationDeployment(interaction.getMessage().getCloudServiceId(), interaction.getMessage().getDescription());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.SEND_DEPLOYMENT_DESCRIPTION);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.SEND_SERVICE_DESCRIPTION:
                    syblControlClient.setApplicationDescription(interaction.getMessage().getCloudServiceId(), interaction.getMessage().getDescription());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.SEND_SERVICE_DESCRIPTION);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.START_CONTROL:
                    syblControlClient.startApplication(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.START_CONTROL);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

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
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.START_ENFORCEMENT);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.START_TEST:
                    syblControlClient.startTest(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.START_TEST);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.UNDEPLOY_SERVICE:
                    syblControlClient.undeployService(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.UNDEPLOY_SERVICE);
                    message.setDescription("OK");
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setType(interaction.getType());
                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.GET_SERVICES:
                    String services = syblControlClient.getServices();
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.GET_SERVICES);
                    message.setDescription(services);
                    message.setUuid(UUID.randomUUID().toString());
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());

                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    interactionResponse.setType(interaction.getType());
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.GET_SERVICE:
                    String servs = syblControlClient.getService(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.GET_SERVICE);
                    message.setDescription(servs);
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());
                    message.setUuid(UUID.randomUUID().toString());

                    interactionResponse.setInitiationDate(new Date());

                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    interactionResponse.setType(interaction.getType());
                    initiateInteractions.initiateInteraction(interactionResponse);
                    break;
                case IMessage.RequestTypes.GET_REQUIREMENTS:
                    String reqs = syblControlClient.getRequirements(interaction.getMessage().getCloudServiceId());
                    interactionResponse = new Interaction();
                    interactionResponse.setUuid(UUID.randomUUID().toString());
                    interactionResponse.setDialogUuid(interaction.getDialogUuid());
                    message = new Message();
                    message.setCause(interaction.getMessage().getCause());
                    message.setActionEnforced(IMessage.RequestTypes.GET_REQUIREMENTS);
                    message.setDescription(reqs);
                    message.setCloudServiceId(interaction.getMessage().getCloudServiceId());
                    message.setUuid(UUID.randomUUID().toString());

                    interactionResponse.setInitiationDate(new Date());
                    interactionResponse.setInitiator(interaction.getReceiver());
                    interactionResponse.setReceiver(interaction.getInitiator());
                    interactionResponse.setMessage(message);
                    interactionResponse.setType(interaction.getType());
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
