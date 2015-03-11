/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;
import org.bouncycastle.math.ec.ECCurve;


/**
 *
 * @author Georgiana
 */
public class EventNotification {
    String lastEvent="";
    private static EventNotification eventNotification;
    private MQProducer rabbitMQProducer;
    private EventNotification(){
        if (Configuration.getMQEnabled()==true){
        rabbitMQProducer = new MQProducer();
        }
    }
    public static EventNotification getEventNotification(){
        if (eventNotification==null){
            eventNotification=new EventNotification();
            
        }
        return eventNotification;
    }
    public void sendEvent(String event) {
        if (Configuration.getMQEnabled()==true){
         rabbitMQProducer.sendMessage(event);
        }
    }
}
