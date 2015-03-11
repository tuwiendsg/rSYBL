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
    public void sendEvent(Event event) {
        if (Configuration.getMQEnabled()==true){
        	try{
        	ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
             JAXBContext jaxbContext = JAXBContext.newInstance(Event.class);
             Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

             // output pretty printed
             jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

             jaxbMarshaller.marshal(event, byteArrayOutputStream);
         rabbitMQProducer.sendMessage(new String(byteArrayOutputStream.toByteArray(), "UTF-8"));
        	}catch(Exception e){
        		DependencyGraphLogger.logger.error(e.getCause());
        	}
        }
    }
}
