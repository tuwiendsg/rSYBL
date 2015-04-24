/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing;

import at.ac.tuwien.dsg.rsybl.controllercommunication.CommunicationManagement;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Georgiana
 */
public class InitiateInteractions {

    CloudAMQPInteractions aMQPInteractions;
       private InteractionProcessing interactionProcessing;

    public InitiateInteractions(CommunicationManagement communicationManagement) {
        interactionProcessing = new InteractionProcessing(this);
        aMQPInteractions = new CloudAMQPInteractions(communicationManagement,interactionProcessing);
       
    }

    public void initiateInteraction(Interaction interaction) {
        aMQPInteractions.initiateInteraction(interaction.getReceiver().getRoleName(), interaction);
    }

    public void startListeningToMessages() {
        aMQPInteractions.startListeningToMessages();
    }

    public List<Interaction> getReadMessages() {
        return aMQPInteractions.getAndClearCachedInteractions();
    }

    public void closeInteraction() {
        aMQPInteractions.closeInteraction();
    }
}
