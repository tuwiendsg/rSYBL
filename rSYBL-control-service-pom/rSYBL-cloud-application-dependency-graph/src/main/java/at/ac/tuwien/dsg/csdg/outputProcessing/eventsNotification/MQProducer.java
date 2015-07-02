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
package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.store.MessageStore;

/**
 *
 * @author Georgiana
 */
public class MQProducer {

    private String QUEUE_NAME = "events";
    private String QUEUE_url = "tcp://localhost:61616";
    private Connection connection;
    private Session session = null;
    private MessageProducer producer;
    private TransportConnector connector;
    private BrokerService broker;
    public MQProducer() {
        if (Configuration.getQueueName() != null) {
            QUEUE_NAME = Configuration.getQueueName();
        }
        if (Configuration.getQueueUrl() != null) {
            QUEUE_url = Configuration.getQueueUrl();
        }
        initiateQueue();

    }

    public void clearAllEvents() {
        try {
            connection.stop();
            
            try {
//                broker.stopGracefully(connector.getName(), QUEUE_NAME, 2000, 2000);
                               broker.stop();
            } catch (Exception ex) {
//                ex.printStackTrace();;
                DependencyGraphLogger.logger.error(ex.getMessage());
            }
            try {
                broker.stop();
            } catch (Exception ex) {
                DependencyGraphLogger.logger.error(ex.getMessage());
            }
        } catch (JMSException ex) {
            DependencyGraphLogger.logger.error(ex.getMessage());
        }

    }
   public void initiateRoleInteractionQueue() {
        // get the initial context
        broker = new BrokerService();


        try {
            // configure the broker

            connector = new TransportConnector();
            connector.setUri(new URI(QUEUE_url));
            broker.addConnector(connector);
            broker.start();
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(QUEUE_NAME);

            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        } catch (Exception e) {
            DependencyGraphLogger.logger.error(e.getMessage());
        }

    }
   
    public void initiateQueue() {
        // get the initial context
        broker = new BrokerService();


        try {
            // configure the broker

            connector = new TransportConnector();
            connector.setUri(new URI(QUEUE_url));
            broker.addConnector(connector);
            broker.start();
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

            // Create a Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(QUEUE_NAME);
            Queue queue = (Queue) destination;
            
            // Create a MessageProducer from the Session to the Topic or Queue
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        } catch (Exception e) {
            DependencyGraphLogger.logger.error(e.getMessage());
        }

    }

    public void sendMessage(IEvent event) {
        try {
            ObjectMessage message = session.createObjectMessage(event);

            // Tell the producer to send the message
            producer.send(message);

        } catch (Exception e) {
            DependencyGraphLogger.logger.error(e.getCause());
        }
    }

    public void closeQueue() {
        try {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            DependencyGraphLogger.logger.error(e.getCause());
        }
    }
}
