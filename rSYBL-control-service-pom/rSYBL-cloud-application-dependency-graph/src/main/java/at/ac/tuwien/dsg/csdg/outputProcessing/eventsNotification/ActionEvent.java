package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ActionEvent implements IEvent {

	private static final long serialVersionUID = -6697588252201342448L;

	protected Stage stage;
	protected String serviceId;
	protected String targetId; // e.g. the id of a UNIT
	protected String actionId;
	private Type type;




	
	public ActionEvent() {

	}

	public ActionEvent(Stage stage, String serviceId, String targetId, String actionId, Type type) {
		super();
		this.stage = stage;
		this.serviceId = serviceId;
		this.targetId = targetId;
		this.actionId = actionId;
		this.type = type;
	}

	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}


    @Override
    public String getServiceId() {
        return serviceId;
    }

   

}
