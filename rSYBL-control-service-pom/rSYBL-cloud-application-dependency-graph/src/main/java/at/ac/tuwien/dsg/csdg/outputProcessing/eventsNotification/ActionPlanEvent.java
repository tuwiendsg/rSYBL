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

public class ActionPlanEvent implements Serializable, IEvent {

	private static final long serialVersionUID = -869510354040946630L;

	protected Stage stageType;
        private Type type;
	protected String serviceId;
        private List<Constraint> constraint = new ArrayList<Constraint>();
                private List<Strategy> strategy = new ArrayList<Strategy>();

	private List<Map.Entry<String,String>> effect = new ArrayList<Map.Entry<String,String>>();
	public ActionPlanEvent() {

	}

	public ActionPlanEvent(Stage type, String serviceId) {
		super();
		this.stageType = type;
		this.serviceId = serviceId;
	}


        
	public Stage getStage() {
		return stageType;
	}

	public void setStage(Stage type) {
		this.stageType = type;
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
        return constraint;
    }

    /**
     * @param cause the cause to set
     */
    public void setConstraints(List<Constraint> cause) {
        this.constraint = cause;
    }
    public void addConstraint(Constraint c){
        constraint.add(c);
    }
    
    public List<Strategy> getStrategies(){
        return strategy;
    }
    public void setStrategies(List<Strategy> str){
        strategy=str;
    }
    public void addStrategy(Strategy s){
        strategy.add(s);
    }
    /**
     * @return the effect
     */
    public List<Map.Entry<String,String>> getEffect() {
        return effect;
    }

    /**
     * @param effect the effect to set
     */
    public void setEffect(List<Map.Entry<String,String>> effect) {
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
