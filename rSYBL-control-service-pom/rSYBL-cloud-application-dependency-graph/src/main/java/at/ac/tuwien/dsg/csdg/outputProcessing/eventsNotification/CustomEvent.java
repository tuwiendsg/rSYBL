/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

/**
 *
 * @author Georgiana
 */
public class CustomEvent implements IEvent{
    private Stage stage;
    private Type type ;
    private String cloudServiceID;
    private String target;
    private String message;
    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public Type getType() {
        return type;
    }

    
    @Override
    public String getServiceId() {
       return getCloudServiceID();
    }

    /**
     * @return the cloudServiceID
     */
    public String getCloudServiceID() {
        return cloudServiceID;
    }

    /**
     * @param cloudServiceID the cloudServiceID to set
     */
    public void setCloudServiceID(String cloudServiceID) {
        this.cloudServiceID = cloudServiceID;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
}
