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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.InteractionManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.SetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.UserManagementBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean;
import static com.sun.faces.facelets.util.Path.context;
import static java.awt.SystemColor.menu;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;

import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;

/**
 *
 * @author Georgiana
 */
@ManagedBean(name = "userManagedBean")
@SessionScoped
public class UserManagedBean implements Serializable,ActionListener{
    
    @EJB(name = "SetupInitialDataSessionBean", beanInterface = ISetupInitialDataSessionBean.class, beanName = "SetupInitialDataSessionBean", lookup = "global/ElasticityOperationsManagementPlatform/SetupInitialDataSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean")
    ISetupInitialDataSessionBean iSetupInitialDataSessionBean;
    @EJB(name = "UserManagementBean", beanInterface = IUserManagementSessionBean.class, beanName = "UserManagementBean", lookup = "global/ElasticityOperationsManagementPlatform/UserManagementBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean")
    IUserManagementSessionBean iUserManagementSessionBean;
    
    @EJB(name = "InteractionManagementSessionBean", beanInterface = InteractionManagementSessionBean.class, beanName = "InteractionManagementSessionBean", lookup = "global/ElasticityOperationsManagementPlatform/InteractionManagementSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.InteractionManagementSessionBean")
    InteractionManagementSessionBean interactionManagementSessionBean;
    private String selectedInteraction;
    private String username;
    private String password;
    private List<RoleDescription> userRoles = new ArrayList<>();
    private Set<IRole> iRoles = new HashSet<>();
    private boolean loggedIn = false;
    private List<String> services = new ArrayList<>();
    private String selectedService = "";

    public UserManagedBean() {
        
    }
    
    @PostConstruct
    public void init() {
        interactionManagementSessionBean.addActionListener(this);
        interactionManagementSessionBean.getServices();
    }
 
    public void processEvents(java.awt.event.ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase("NEW INTERACTION")) {            
            if (RequestContext.getCurrentInstance() != null) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('bar').show();");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(UserManagedBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                context.execute("PF('bar').hide();");
                     
            }
            FacesMessage message = new FacesMessage("Please refresh");
        FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    
   
    public List<IDialog> getDialogsAssociatedWithCurrentUsername(){
        
        return interactionManagementSessionBean.getDialogsForRoles(iRoles);
    }
    public String[] getRequirements(String serviceID){
        return interactionManagementSessionBean.getElasticityRequirements("", serviceID);
    }
     public CloudServiceXML getDescription(String serviceID){
        return interactionManagementSessionBean.getServiceDescription("", serviceID);
    }
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public List<IInteraction> getAllInteractionsForUserAsReceiver(String rolename) {
        return interactionManagementSessionBean.findAllInteractionsForReceiver(rolename);
        
    }
    
    public List<IInteraction> getAllInteractionsForUserAsInitiator(String rolename) {
        return interactionManagementSessionBean.findAllInteractionsForInitiator(rolename);
    }
    
    public List<IDialog> getAllDialogsForUserAsReceiver(String rolename) {
        return interactionManagementSessionBean.findAllDialogsForReceiver(rolename);
        
    }
    public List<IDialog> getAllDialogsForInteractionType(String rolename, String selectedInteractionType){
            return interactionManagementSessionBean.findAllDialogsForRoleWithType(rolename, selectedInteractionType);

    }
    public List<IDialog> getAllDialogsForUserAsInitiator(String rolename) {
        return interactionManagementSessionBean.findAllDialogsForInitiator(rolename);
    }

    public IDialog getDialogForInteraction(String dialogID) {
        return interactionManagementSessionBean.getDialog(dialogID);
    }
    
    public void login(ActionEvent event) {
        
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = null;
        Logger.getLogger(UserManagedBean.class.getName()).log(Level.INFO, "!!!! Loggedin " + loggedIn);
        
        if (iUserManagementSessionBean.login(username, password)) {
            setLoggedIn(true);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
            this.iRoles = iUserManagementSessionBean.searchForUserByUsername(username).getRoles();
            for (IRole iRole : iRoles) {
                if (!iRole.getRoleName().equalsIgnoreCase("Elasticity Controller")) {
                    RoleDescription r = new RoleDescription();
                      r.setRoleName(iRole.getRoleName());
                    this.userRoles.add(r);
                }
            }
        } else {
            setLoggedIn(false);
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials");
        }
//         Logger.getLogger(UserManagedBean.class.getName()).log(Level.INFO, "!!!! Loggedin "+loggedIn);

        FacesContext.getCurrentInstance().addMessage(null, message);
        context.addCallbackParam("loggedIn", isLoggedIn());
//         Logger.getLogger(UserManagedBean.class.getName()).log(Level.INFO, "!!!! Loggedin "+loggedIn);

//         Logger.getLogger(UserManagedBean.class.getName()).log(Level.INFO, "!!!! Loggedin "+loggedIn);
        if (loggedIn) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true");
            
        }

         
    }
    
    public List<IUser> getAllUsers() {
        
        return iUserManagementSessionBean.findAll();
    }

    public List<IUser> getAllUsersWithoutAdmin() {
        List<IUser> users = new ArrayList<IUser>();
        List<IUser> temp = iUserManagementSessionBean.findAll();
        for (IUser r : temp) {
            if (!r.getUsername().equalsIgnoreCase("admin")) {
                users.add(r);
            }
        }
        return users;
    }

    public List<IRole> getAllRoles() {
        return iSetupInitialDataSessionBean.findAllRoles();
        
    }
    
    public List<IResponsibility> getAllResponsibilities() {
        return iSetupInitialDataSessionBean.findAllResponsibilities();
    }
    
    public void logout() {
        username = "";
        password = "";
        this.userRoles=new ArrayList<RoleDescription>();
        loggedIn = false;
        this.iRoles = new HashSet<IRole>();
        this.userRoles = new ArrayList<RoleDescription>();
        FacesMessage message = new FacesMessage("Logged out");
        FacesContext.getCurrentInstance().addMessage(null, message);
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "index.xhtml");
        
    }

    /**
     * @return the userRoles
     */
    public List<RoleDescription> getUserRoles() {
        return userRoles;
    }

    /**
     * @param userRoles the userRoles to set
     */
    public void setUserRoles(List<RoleDescription> userRoles) {
        this.userRoles = userRoles;
    }

    /**
     * @return the iRoles
     */
    public Set<IRole> getiRoles() {
        return iRoles;
    }

    /**
     * @param iRoles the iRoles to set
     */
    public void setiRoles(Set<IRole> iRoles) {
        this.iRoles = iRoles;
    }

    /**
     * @return the loggedIn
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @param loggedIn the loggedIn to set
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * @return the services
     */
    public List<String> getServices() {
             return interactionManagementSessionBean.getServices();
    }

    /**
     * @param services the services to set
     */
    public void setServices(List<String> services) {
        this.services = services;
    }

    /**
     * @return the selectedService
     */
    public String getSelectedService() {
        return selectedService;
    }

    /**
     * @param selectedService the selectedService to set
     */
    public void setSelectedService(String selectedService) {
        this.selectedService = selectedService;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
       this.processEvents(e);
    }

    /**
     * @return the selectedInteraction
     */
    public String getSelectedInteraction() {
        return selectedInteraction;
    }

    /**
     * @param selectedInteraction the selectedInteraction to set
     */
    public void setSelectedInteraction(String selectedInteraction) {
        this.selectedInteraction = selectedInteraction;
    }
    
    public void createNewInteraction(String cloudService, String interactionType, String initiator, String receiver, String action, String description){
        interactionManagementSessionBean.initiateInteraction(selectedInteraction,cloudService,  interactionType,  initiator,  receiver,  action,  description);
    }
    public Date findEarliestDate(){
        return interactionManagementSessionBean.findEarliestDate();
    }
//  public void refreshSelectedInteraction(String interactionUUID){
//     
//      this.selectedInteraction=interactionUUID;
//  }
  /**
     * @return the interactionTypes
     */
   
   
}
