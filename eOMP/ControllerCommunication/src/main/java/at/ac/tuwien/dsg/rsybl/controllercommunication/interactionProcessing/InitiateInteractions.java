/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
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
        aMQPInteractions = new CloudAMQPInteractions(communicationManagement, interactionProcessing);

    }

    public void initiateInteraction(Interaction interaction) {
        aMQPInteractions.initiateInteraction("role", interaction);
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
