/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.*;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author Georgiana
 */
@ManagedBean(name="dialogBean")
@ViewScoped
public class DialogBean implements Serializable{
        @ManagedProperty(value = "#{userManagedBean}")
    private UserManagedBean userManagedBean;
        // dialog constructs
    private List<String> interactionTypes = new ArrayList<String>();
    private String selectedInteractionType ="";
    private List<String> myRoles = new ArrayList<String>();
    private String initiatorRole="";
    private List<String> receiverRoles = new ArrayList<String>();
    private String receiverRole="";
    private List<String> cloudServices =  new ArrayList<>();
    private String selectedCloudService="";
    private List<String> availableActions = new ArrayList<>();
    private String selectedAction="";
    private String parameterName ="";
    private String parameterValue="";  
    
    @PostConstruct
      public void initiateDialogConstants(){
        getInteractionTypes().add(IInteraction.InteractionType.EMERGENCY);
        getInteractionTypes().add(IInteraction.InteractionType.NOTIFICATION);
        getInteractionTypes().add(IInteraction.InteractionType.REQUEST);
        getInteractionTypes().add(IInteraction.InteractionType.WARNING);
        
        List<RoleDescription> roles =    getUserManagedBean().getUserRoles();
        for (RoleDescription description:roles){
            getMyRoles().add(description.getRoleName());
        }
        List<IRole> allRoles = getUserManagedBean().getAllRoles();
        for (IRole role:allRoles){
            this.getReceiverRoles().add(role.getRoleName());
        }
        this.setCloudServices(getUserManagedBean().getServices());
        availableActions.add(IMessage.RequestTypes.REPLACE_CUSTOM_METRICS);
        availableActions.add(IMessage.RequestTypes.REMOVE_SERVICE);
        availableActions.add(IMessage.RequestTypes.REPLACE_REQUIREMENTS);
        availableActions.add(IMessage.RequestTypes.PAUSE_CONTROL);
        availableActions.add(IMessage.RequestTypes.UNDEPLOY_SERVICE);
                availableActions.add(IMessage.RequestTypes.REPLACE_DESCRIPTION);

                availableActions.add(IMessage.RequestTypes.DELEGATE);
        
    }
      
       public List<String> getInteractionTypes() {
        return interactionTypes;
    }

    /**
     * @param interactionTypes the interactionTypes to set
     */
    public void setInteractionTypes(List<String> interactionTypes) {
        this.interactionTypes = interactionTypes;
    }

    /**
     * @return the selectedInteractionType
     */
    public String getSelectedInteractionType() {
        return selectedInteractionType;
    }

    /**
     * @param selectedInteractionType the selectedInteractionType to set
     */
    public void setSelectedInteractionType(String selectedInteractionType) {
        this.selectedInteractionType = selectedInteractionType;
    }
    public void onSelectedInteractionChange(AjaxBehaviorEvent actionEvent){
        
    }

    /**
     * @return the receiverRoles
     */
    public List<String> getReceiverRoles() {
        return receiverRoles;
    }

    /**
     * @param receiverRoles the receiverRoles to set
     */
    public void setReceiverRoles(List<String> receiverRoles) {
        this.receiverRoles = receiverRoles;
    }

    /**
     * @return the receiverRole
     */
    public String getReceiverRole() {
        return receiverRole;
    }

    /**
     * @param receiverRole the receiverRole to set
     */
    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    /**
     * @return the initiatorRole
     */
    public String getInitiatorRole() {
        return initiatorRole;
    }

    /**
     * @param initiatorRole the initiatorRole to set
     */
    public void setInitiatorRole(String initiatorRole) {
        this.initiatorRole = initiatorRole;
    }

    /**
     * @return the myRoles
     */
    public List<String> getMyRoles() {
        return myRoles;
    }

    /**
     * @param myRoles the myRoles to set
     */
    public void setMyRoles(List<String> myRoles) {
        this.myRoles = myRoles;
    }

    /**
     * @return the cloudServices
     */
    public List<String> getCloudServices() {
        return cloudServices;
    }

    /**
     * @param cloudServices the cloudServices to set
     */
    public void setCloudServices(List<String> cloudServices) {
        this.cloudServices = cloudServices;
    }

    /**
     * @return the selectedCloudService
     */
    public String getSelectedCloudService() {
        return selectedCloudService;
    }

    /**
     * @param selectedCloudService the selectedCloudService to set
     */
    public void setSelectedCloudService(String selectedCloudService) {
        this.selectedCloudService = selectedCloudService;
    }

    /**
     * @return the availableActions
     */
    public List<String> getAvailableActions() {
        return availableActions;
    }

    /**
     * @param availableActions the availableActions to set
     */
    public void setAvailableActions(List<String> availableActions) {
        this.availableActions = availableActions;
    }

    /**
     * @return the selectedAction
     */
    public String getSelectedAction() {
        return selectedAction;
    }

    /**
     * @param selectedAction the selectedAction to set
     */
    public void setSelectedAction(String selectedAction) {
        this.selectedAction = selectedAction;
    }
   public void onSelectedAction(AjaxBehaviorEvent actionEvent){
      switch(selectedAction){
          case IMessage.RequestTypes.REMOVE_SERVICE:
              break;
          case IMessage.RequestTypes.REPLACE_REQUIREMENTS:
              parameterName="Requirements Specification"; 
              String reqs ="";
              String returnedReq[] = getUserManagedBean().getRequirements(selectedCloudService);
              for (String s :returnedReq){
                  if (s!=null)
                  reqs+=s.split("-")[1]+"\n";
              }
              parameterValue=reqs;
              break;
          case IMessage.RequestTypes.REPLACE_CUSTOM_METRICS:
              break;
          case IMessage.RequestTypes.UNDEPLOY_SERVICE:
              break;
          case IMessage.RequestTypes.PAUSE_CONTROL:
              break;
           case IMessage.RequestTypes.REPLACE_DESCRIPTION:
               parameterName="Service description";
              break; 
          case IMessage.RequestTypes.DELEGATE:
               parameterName="Delegate";
               parameterValue="Please take care of this issue.";
              break;  
      }
   }

    /**
     * @return the parameterName
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName the parameterName to set
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * @return the parameterValue
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * @param parameterValue the parameterValue to set
     */
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    /**
     * @return the userManagedBean
     */
    public UserManagedBean getUserManagedBean() {
        return userManagedBean;
    }

    /**
     * @param userManagedBean the userManagedBean to set
     */
    public void setUserManagedBean(UserManagedBean userManagedBean) {
        this.userManagedBean = userManagedBean;
    }
   public void createNewInteraction(){
       userManagedBean.createNewInteraction(this.selectedCloudService,this.selectedInteractionType,this.initiatorRole,this.receiverRole, this.selectedAction,this.parameterValue);
   }
    
    
}
