package test.queue;

import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.ActionPlanEvent;
import static org.junit.Assert.*;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.MQProducer;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import javax.jms.Destination;
import javax.jms.Message;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TestQueue {

    private String QUEUE_NAME = Configuration.getQueueName();
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageConsumer consumer = null;
    MQProducer mQProducer;

    @Before
    public void setUpProducer() {
        if (Configuration.getMQEnabled() == true) {
            QUEUE_NAME = Configuration.getQueueName();
            mQProducer = new MQProducer();
            ActionPlanEvent actionPlanEvent = new ActionPlanEvent();
            actionPlanEvent.setServiceId("lala");
            mQProducer.sendMessage(actionPlanEvent);
        }

    }

    
    @Test
    public void testProducer() {
        if (Configuration.getMQEnabled() == true) {
           
            try {
                factory = new ActiveMQConnectionFactory(
                        "tcp://localhost:61616");
                connection = factory.createConnection();
                connection.start();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                destination = session.createQueue(QUEUE_NAME);
                consumer = session.createConsumer(destination);
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    TextMessage text = (TextMessage) message;
                    System.out.println("Message is : " + text.getText());

                }
                Thread.sleep(2000);
             
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

    @After
    public void closeQueue() {
        if (Configuration.getMQEnabled() == true) {
            mQProducer.clearAllEvents();
            mQProducer.closeQueue();
        }
    }
}
