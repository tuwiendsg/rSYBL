/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public interface IMessage {
       
    public interface RequestTypes extends Serializable{
    String PREPARE_CONTROL = "PrepareControl";
    String SEND_SERVICE_DESCRIPTION = "SendServiceDescription";
    String SEND_DEPLOYMENT_DESCRIPTION = "SendDeploymentDescription";
    String SEND_CUSTOM_METRICS ="SendCustomMetrics";
    String START_CONTROL="StartControl";
    String START_TEST ="StartTest";
    String START_ENFORCEMENT="StartEnforcement";
    String REPLACE_CUSTOM_METRICS = "ReplaceRules";
    String UNDEPLOY_SERVICE= "UndeployService";
    String REMOVE_SERVICE="RemoveService";
    String REPLACE_REQUIREMENTS ="ReplaceRequirements";
    String PAUSE_CONTROL="PauseControl";
    String RESUME_CONTROL="ResumeControl";
    String GET_SERVICES="GetServices";
    String GET_SERVICE="GetService";
    String GET_REQUIREMENTS="GetRequirements";
    }
     public String getCloudServiceId() ;


    public void setCloudServiceId(String cloudServiceId) ;


    public Set<Double> getValues();


    public void setValues(Set<Double> values) ;
    
    public String getDescription() ;
    
    public void setDescription(String description);
 
    public String getTargetPartId() ;


    public void setTargetPartId(String targetPartId) ;
    public Long getId() ;
    public void setId(Long id);

 
 
    public String getPriority();


    public void setPriority(String priority) ;

 
    public String getCause() ;
 
    public void setCause(String cause) ;

 
    public IInteraction getInteraction();
    
    public void setInteraction(IInteraction interaction);
    
    public String getActionEnforced();
    
    public void setActionEnforced(String actionEnforced) ;
       public String getUuid();
    public void setUuid(String uuid);

}
