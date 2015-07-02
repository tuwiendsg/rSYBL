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

    String lastEvent = "";
    private static EventNotification eventNotification;
    private MQProducer rabbitMQProducer;

    private EventNotification() {
        if (Configuration.getMQEnabled() == true) {
            rabbitMQProducer = new MQProducer();
        }
    }

    public static EventNotification getEventNotification() {
        if (eventNotification == null) {
            eventNotification = new EventNotification();

        }
        return eventNotification;
    }

    public void clearAllEvents() {
        if (Configuration.getMQEnabled() == true) {
            try {

                rabbitMQProducer.clearAllEvents();
                rabbitMQProducer.initiateQueue();
            } catch (Exception e) {
                DependencyGraphLogger.logger.error(e.getCause());
            }
        }
    }

    public void sendEvent(IEvent event) {
        if (Configuration.getMQEnabled() == true) {
            try {

                rabbitMQProducer.sendMessage(event);
            } catch (Exception e) {
                DependencyGraphLogger.logger.error(e.getCause());
            }
        }
    }
}
