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
