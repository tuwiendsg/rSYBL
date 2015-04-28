/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
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
@ApplicationScoped
public class UserManagedBean implements Serializable {
    
    @EJB(name = "SetupInitialDataSessionBean", beanInterface = ISetupInitialDataSessionBean.class, beanName = "SetupInitialDataSessionBean", lookup = "global/ElasticityOperationsManagementPlatform/SetupInitialDataSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean")
    ISetupInitialDataSessionBean iSetupInitialDataSessionBean;
    @EJB(name = "UserManagementBean", beanInterface = IUserManagementSessionBean.class, beanName = "UserManagementBean", lookup = "global/ElasticityOperationsManagementPlatform/UserManagementBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean")
    IUserManagementSessionBean iUserManagementSessionBean;
    
    @EJB(name = "InteractionManagementSessionBean", beanInterface = InteractionManagementSessionBean.class, beanName = "InteractionManagementSessionBean", lookup = "global/ElasticityOperationsManagementPlatform/InteractionManagementSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.InteractionManagementSessionBean")
    InteractionManagementSessionBean interactionManagementSessionBean;
    
    private String username;
    private String password;
    private List<String> userRoles = new ArrayList<>();
    private Set<IRole> iRoles = new HashSet<>();
    private boolean loggedIn = false;

    public UserManagedBean() {
        
    }
    
    @PostConstruct
    public void init() {
     
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
        }
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
                if (!iRole.getRoleName().equalsIgnoreCase("EC")) {
                    this.userRoles.add(iRole.getRoleName());
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
          ActionListener refreshListener = new ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                processEvents(e);
            }
            
        };
        interactionManagementSessionBean.addActionListener(refreshListener);
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
        
        loggedIn = false;
        this.iRoles = new HashSet<IRole>();
        this.userRoles = new ArrayList<String>();
        FacesMessage message = new FacesMessage("Logged out");
        FacesContext.getCurrentInstance().addMessage(null, message);
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "index.xhtml");
        
    }

    /**
     * @return the userRoles
     */
    public List<String> getUserRoles() {
        return userRoles;
    }

    /**
     * @param userRoles the userRoles to set
     */
    public void setUserRoles(List<String> userRoles) {
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
    
}
