package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.ElasticityCapability;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ActionPlanEvent implements Serializable, IEvent {

	private static final long serialVersionUID = -869510354040946630L;

	protected Stage stage;
        private Type type;
	protected String serviceId;
        private ArrayList<Constraint> constraints = new ArrayList<Constraint>();
                private ArrayList<Strategy> strategies = new ArrayList<Strategy>();

	private ArrayList<Map.Entry<String,String>> effect = new ArrayList<Map.Entry<String,String>>();
	public ActionPlanEvent() {

	}

	public ActionPlanEvent(Stage type, String serviceId) {
		super();
		this.stage = type;
		this.serviceId = serviceId;
	}


        
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage type) {
		this.stage = type;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

    /**
     * @return the cause
     */
    public List<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * @param cause the cause to set
     */
    public void setConstraints(ArrayList<Constraint> cause) {
        this.constraints = cause;
    }
    public void addConstraint(Constraint c){
        constraints.add(c);
    }
    
    public ArrayList<Strategy> getStrategies(){
        return strategies;
    }
    public void setStrategies(ArrayList<Strategy> str){
        strategies=str;
    }
    public void addStrategy(Strategy s){
        strategies.add(s);
    }
    /**
     * @return the effect
     */
    public ArrayList<Map.Entry<String,String>> getEffect() {
        return effect;
    }

    /**
     * @param effect the effect to set
     */
    public void setEffect(ArrayList<Map.Entry<String,String>> effect) {
        this.effect = effect;
    }
    public void addEffect( Map.Entry<String,String> actionTarget){
        effect.add(actionTarget);
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }


}
