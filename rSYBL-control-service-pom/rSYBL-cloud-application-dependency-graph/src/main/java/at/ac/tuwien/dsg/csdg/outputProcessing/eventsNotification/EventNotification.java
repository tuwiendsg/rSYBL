/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import at.ac.tuwien.dsg.csdg.utils.Configuration;
import at.ac.tuwien.dsg.csdg.utils.DependencyGraphLogger;


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
    public void sendEvent(IEvent event) {
        if (Configuration.getMQEnabled()==true){
        	try{
        
         rabbitMQProducer.sendMessage(event);
        	}catch(Exception e){
        		DependencyGraphLogger.logger.error(e.getCause());
        	}
        }
    }
}
