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
import at.ac.tuwien.dsg.rsybl.controllercommunication.utils.Configuration;
import at.ac.tuwien.dsg.rsybl.controllercommunication.utils.ControllerCommunicationLogger;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class CloudAMQPInteractions {

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private CommunicationManagement communicationManagement;
    private List<Interaction> cachedInteractions = new ArrayList<Interaction>();
    private String EXCHANGE_NAME = "eOMP";
    private InteractionProcessing interactionProcessing;

    /**
     * @return the cachedInteractions
     */
    public List<Interaction> getCachedInteractions() {
        return cachedInteractions;
    }

    /**
     * @param cachedInteractions the cachedInteractions to set
     */
    public synchronized void setCachedInteractions(List<Interaction> cachedInteractions) {
        synchronized (cachedInteractions) {
            this.cachedInteractions = cachedInteractions;
        }
    }

    public synchronized void addCachedInteractions(Interaction i) {
        cachedInteractions.add(i);
    }

    public synchronized List<Interaction> getAndClearCachedInteractions() {
        List<Interaction> interactions = new ArrayList<>();
        for (Interaction interaction : cachedInteractions) {
            interactions.add(interaction);
        }

        cachedInteractions.clear();
        return interactions;

    }

    class ListenQueue implements Runnable {

        Thread t;
        QueueingConsumer consumer;

        public ListenQueue() {
            t = new Thread(this);
            init();

        }

        private void init() {
            try {

                String queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, EXCHANGE_NAME, "Elasticity Controller");
                consumer = new QueueingConsumer(channel);
                channel.basicConsume(queueName, true, consumer);
//                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                t.start();
            } catch (Exception e) {
                ControllerCommunicationLogger.logger.info(e.getMessage());
            }
        }

        @Override
        public void run() {
            while (true) {
                if (connection.isOpen()) {
                    try {
                        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                        ByteArrayInputStream baos = new ByteArrayInputStream(delivery.getBody());
                        ObjectInputStream ois = new ObjectInputStream(baos);
                        Object obj = ois.readObject();
                        Interaction interaction = (Interaction) obj;
                        addCachedInteractions(interaction);
                        System.out.println(" [x] Received '" + interaction.getUuid() + "'");

                        interactionProcessing.processNewInteraction(interaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ControllerCommunicationLogger.logger.info(e.getMessage());
                    }
                } else {
                    break;
                }

            }
        }
    }

    public CloudAMQPInteractions(CommunicationManagement communicationManagement, InteractionProcessing interactionProcessing) {
        if (Configuration.getInteractionTopicName() != null) {
            EXCHANGE_NAME = Configuration.getInteractionTopicName();
        }
        this.interactionProcessing = interactionProcessing;
        this.communicationManagement = communicationManagement;
        initiateQueues();
//        ListenQueue listenerQueue = new ListenQueue();

    }

    public void startListeningToMessages() {
        ListenQueue listenerQueue = new ListenQueue();

    }

    private void initiateQueues() {
        try {
            factory = new ConnectionFactory();
            factory.setVirtualHost(Configuration.getCloudAMQPVirtualHost());
            factory.setUsername(Configuration.getCloudAMQPUsername());
            factory.setUri(Configuration.getCloudAMQPUri());
            factory.setPassword(Configuration.getCloudAMQPPassword());
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        } catch (Exception e) {
            ControllerCommunicationLogger.logger.info(e.getMessage());

        }
    }

    public void closeInteraction() {
        try {

            connection.close();

        } catch (IOException ex) {
            ControllerCommunicationLogger.logger.info(ex.getMessage());
        }
    }

    public void initiateInteraction(String role, Serializable message) {

        try {
//            connection = factory.newConnection();
//            channel = connection.createChannel();
//            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
            byte[] bytes;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            oos.flush();
            oos.reset();
            bytes = baos.toByteArray();
            oos.close();
            baos.close();

            channel.basicPublish(EXCHANGE_NAME, role, null, bytes);
            if (((Interaction) message).getMessage() != null) {
                System.out.println(" [x] Sent '" + role + "':'" + ((Interaction) message).getMessage().getDescription() + "'");
            }

        } catch (Exception e) {
            ControllerCommunicationLogger.logger.info(e.getMessage());

        }

    }

}
