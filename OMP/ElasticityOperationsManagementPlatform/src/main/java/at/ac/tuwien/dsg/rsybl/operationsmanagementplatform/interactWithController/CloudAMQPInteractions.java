/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.interactWithController;


import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.InteractionManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.Configuration;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.OMPLogger;
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
    private List<Interaction> cachedInteractions = new ArrayList<Interaction>();
    private String EXCHANGE_NAME = "eOMP";
    private InteractionManagementSessionBean interactionManagementSessionBean;
    /**
     * @return the cachedInteractions
     */
    public List<Interaction> getCachedInteractions() {
        return cachedInteractions;
    }

    /**
     * @param cachedInteractions the cachedInteractions to set
     */
    public synchronized  void setCachedInteractions(List<Interaction> cachedInteractions) {
        synchronized (cachedInteractions) {
            this.cachedInteractions = cachedInteractions;
        }
    }

    public synchronized void addCachedInteractions(Interaction i) {
            cachedInteractions.add(i);
    }

    public synchronized  List<Interaction> getAndClearCachedInteractions() {
            List<Interaction> interactions = new ArrayList<>();
            for (Interaction interaction : cachedInteractions) {
                interactions.add(interaction);
            }

            cachedInteractions.clear();
            return interactions;
       
    }

    /**
     * @return the controllerCommunication
     */
    public InteractionManagementSessionBean getControllerCommunication() {
        return interactionManagementSessionBean;
    }

    /**
     * @param controllerCommunication the controllerCommunication to set
     */
    public void setControllerCommunication(InteractionManagementSessionBean interactionManagementSessionBean) {
        this.interactionManagementSessionBean = interactionManagementSessionBean;
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
                channel.queueBind(queueName, EXCHANGE_NAME, "EC");
                consumer = new QueueingConsumer(channel);
                channel.basicConsume(queueName, true, consumer);
//                System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                t.start();
            } catch (Exception e) {
                OMPLogger.logger.info(e.getMessage());
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
                        System.out.println(" [x] Received '" + interaction.getId() + "'");

                        interactionManagementSessionBean.processInteraction(interaction);
                    } catch (Exception e) {
                        OMPLogger.logger.info(e.getMessage());
                    }
                } else {
                    break;
                }

            }
        }
    }

    public CloudAMQPInteractions(InteractionManagementSessionBean interactionManagementSessionBean) {
        if (Configuration.getInteractionTopicName() != null) {
            EXCHANGE_NAME = Configuration.getInteractionTopicName();
        }
        this.interactionManagementSessionBean = interactionManagementSessionBean;
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
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException | IOException e) {
            OMPLogger.logger.info(e.getMessage());

        }
    }

    public void closeInteraction() {
        try {

            connection.close();

        } catch (IOException ex) {
            OMPLogger.logger.info(ex.getMessage());
        }
    }

    public void initiateInteraction(String role, Serializable message) {

        try {

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

            System.out.println(" [x] Sent '" + role + "':'" + message + "'");

//            connection.close();
        } catch (Exception e) {
            OMPLogger.logger.info(e.getMessage());

        }

    }

}
