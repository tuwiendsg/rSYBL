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
    String REPLACE_DESCRIPTION="ReplaceServiceDescription";
    String DELEGATE="Delegate";
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
