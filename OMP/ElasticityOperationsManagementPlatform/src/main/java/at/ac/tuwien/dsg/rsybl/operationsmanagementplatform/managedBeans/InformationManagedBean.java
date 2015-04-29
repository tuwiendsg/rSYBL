/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;

/**
 *
 * @author Georgiana
 */
@ManagedBean(name = "informationManagedBean")
@ApplicationScoped
public class InformationManagedBean implements Serializable {

    private boolean platformPanel = true;
    private boolean eC = false;
    private boolean services = false;
    private boolean roles = false;
    private boolean processes = false;
    private boolean myServices = false;
    private boolean organizationRoles = false;
    private boolean roleInteractions = false;
    private boolean myResponsibilities = false;
    public InformationManagedBean() {

    }

    @PostConstruct
    public void init() {

    }

    /**
     * @return the platformPanel
     */
    public boolean getPlatformPanel() {
        return platformPanel;
    }

    /**
     * @param platformPanel the platformPanel to set
     */
    public void setPlatformPanel(boolean platformPanel) {
        this.platformPanel = platformPanel;
    }

    /**
     * @return the eC
     */
    public boolean geteC() {
        return eC;
    }

    /**
     * @param eC the eC to set
     */
    public void seteC(boolean eC) {
        this.eC = eC;
    }

    /**
     * @return the services
     */
    public boolean getServices() {
        return services;
    }

    /**
     * @param services the services to set
     */
    public void setServices(boolean services) {
        this.services = services;
    }

    /**
     * @return the roles
     */
    public boolean getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(boolean roles) {
        this.roles = roles;
    }

    /**
     * @return the processes
     */
    public boolean getProcesses() {
        return processes;
    }

    /**
     * @param processes the processes to set
     */
    public void setProcesses(boolean processes) {
        this.processes = processes;
    }

    public void activatePlatformPanel() {
        platformPanel = true;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('platformW').show();");

    }

    public void activateRolesPanel() {
        roles = true;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('rolesW').show();");

    }

    public void activateProcessesPanel() {
        processes = true;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('processesW').show();");

    }

    public void activateECPanel() {
        eC = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('ecW').show();");
    }

    public void activateServices() {
        services = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('servicesW').show();");

    }

    public void activateRoles() {
        services = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('orgRolesW').show();");

    }

    public void activateAssociatedServices() {
        this.myServices = true;
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getRequestURL().toString();
        if (url.toLowerCase().contains("users") || url.toLowerCase().contains("responsibilities")) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true;");
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('myServicesW').show();");

    }

    public void activateRoleInteractions() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getRequestURL().toString();
        if (url.toLowerCase().contains("users") || url.toLowerCase().contains("responsibilities")) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true;");
        }
        this.roleInteractions = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('roleInteractionsW').show();");

    }

    public void activateResponsibilities() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getRequestURL().toString();
        if (url.toLowerCase().contains("users") || url.toLowerCase().contains("responsibilities")) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true;");
        }
        this.roleInteractions = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('myResponsibilitiesW').show();");
    }

    public void onClose(CloseEvent event) {
        switch (event.getComponent().getId()) {
            case "platform":
                platformPanel = false;
                break;
            case "services":
                services = false;
                break;
            case "processes":
                processes = false;
                break;
            case "roles":
                roles = false;
                break;
            case "ec":
                eC = false;
                break;
            case "orgRoles":
                organizationRoles = false;
                break;
            case "myServices":
                setMyServices(false);
                break;
            case "roleInteractions":
                setRoleInteractions(false);
                break;
            case "myResponsibilities":
                myResponsibilities = false;
                break;
        }
    }

    /**
     * @return the organizationRoles
     */
    public boolean isOrganizationRoles() {
        return organizationRoles;
    }

    /**
     * @param organizationRoles the organizationRoles to set
     */
    public void setOrganizationRoles(boolean organizationRoles) {
        this.organizationRoles = organizationRoles;
    }

    /**
     * @return the myServices
     */
    public boolean isMyServices() {
        return myServices;
    }

    /**
     * @param myServices the myServices to set
     */
    public void setMyServices(boolean myServices) {
        this.myServices = myServices;
    }

    /**
     * @return the roleInteractions
     */
    public boolean isRoleInteractions() {
        return roleInteractions;
    }

    /**
     * @param roleInteractions the roleInteractions to set
     */
    public void setRoleInteractions(boolean roleInteractions) {
        this.roleInteractions = roleInteractions;
    }

    /**
     * @return the myResponsibilities
     */
    public boolean isMyResponsibilities() {
        return myResponsibilities;
    }

    /**
     * @param myResponsibilities the myResponsibilities to set
     */
    public void setMyResponsibilities(boolean myResponsibilities) {
        this.myResponsibilities = myResponsibilities;
    }
}
