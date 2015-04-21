/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.SetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.UserManagementBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean;
import static com.sun.faces.facelets.util.Path.context;
import static java.awt.SystemColor.menu;
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
@SessionScoped
public class UserManagedBean implements Serializable {

    @EJB(name = "SetupInitialDataSessionBean", beanName = "SetupInitialDataSessionBean", lookup = "global/ElasticityOperationsManagementPlatform/SetupInitialDataSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean")
    ISetupInitialDataSessionBean iSetupInitialDataSessionBean;
    @EJB(name = "UserManagementBean", beanName = "UserManagementBean", lookup = "name=global/ElasticityOperationsManagementPlatform/UserManagementBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean")
    IUserManagementSessionBean iUserManagementSessionBean;
    DataAccess access;
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

    public void login(ActionEvent event) {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = null;
        Logger.getLogger(UserManagedBean.class.getName()).log(Level.INFO, "!!!! Loggedin " + loggedIn);

        if (iUserManagementSessionBean.login(username, password)) {
            setLoggedIn(true);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);
            this.iRoles = iUserManagementSessionBean.searchForUserByUsername(username).getRoles();
            for (IRole iRole : iRoles) {
                this.userRoles.add(iRole.getRoleName());
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