/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.queue;

import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.ActionPlanEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.MQProducer;
import at.ac.tuwien.dsg.csdg.utils.Configuration;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Georgiana
 */
public class QueueListener {
        private String QUEUE_NAME = Configuration.getQueueName();
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageConsumer consumer = null;
    MQProducer mQProducer;
//      @Test
//    public void testProducer() {
//        if (Configuration.getMQEnabled() == true) {
//           
//            try {
//                                factory = new ActiveMQConnectionFactory(
//                        "tcp://localhost:61616");
//                connection = factory.createConnection();
//                connection.start();
//                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//                destination = session.createQueue(QUEUE_NAME);
//                consumer = session.createConsumer(destination);
//              while(true){
//                Message message = consumer.receive();
//                ObjectMessage msg = (ObjectMessage)message;
//                    System.err.println("Message is : " + msg.toString());
//                System.out.println(msg.getObject().getClass());
//                    try{
//                ActionPlanEvent actionPlanEvent = (ActionPlanEvent)msg.getObject();
//                System.out.println(actionPlanEvent.getConstraints().size());
//                System.out.println(actionPlanEvent.getStrategies().size());
//                System.out.println(actionPlanEvent.getStage());
//
//                }catch(Exception e){
//                    
//                }
//                
//             
//                Thread.sleep(2000);
//
//                }
//              
//            } catch (Exception e) {
//                fail(e.getMessage());
//            }
//        }
//    }

}
