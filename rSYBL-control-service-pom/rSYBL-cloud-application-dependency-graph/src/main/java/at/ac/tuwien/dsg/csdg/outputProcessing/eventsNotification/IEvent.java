/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author Georgiana
 */
@XmlSeeAlso({ ActionPlanEvent.class, ActionEvent.class, CustomEvent.class })
@XmlAccessorType(XmlAccessType.FIELD)
public interface IEvent extends Serializable{
    public enum Stage {
		START, FINISHED, FAILED
	}
	public enum Type {
		NOTIFICATION, ERROR, UNHEALTHY_SP,ELASTICITY_CONTROL
	}
    public Stage getStage();
    public Type getType();
    

	public String getServiceId() ;

        
}
