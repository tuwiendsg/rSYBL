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

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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
    private boolean roleInteractionsNotification = false;
    private boolean roleInteractionsRequest = false;
    private boolean roleInteractionsWarning = false;

    private boolean roleInteractionsError = false;
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
        if (url.toLowerCase().contains("users") || url.toLowerCase().contains("responsibilities") || url.toLowerCase().contains("dialog")||url.toLowerCase().contains("statistics")) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true;");
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('myServicesW').show();");

    }

    public void activateRoleInteractions(String type) {
        if (type.equalsIgnoreCase("")){
            type="Notification";
        }
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getRequestURL().toString();
        if (url.toLowerCase().contains("users") || url.toLowerCase().contains("responsibilities") || url.toLowerCase().contains("dialog")||url.toLowerCase().contains("statistics")) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true;");
        }
        switch(type){
            case "Notification":
                setRoleInteractionsNotification(true);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('roleInteractionsNotificationW').show();");
                break;
            case "Error":
                setRoleInteractionsError(true);
                         context = RequestContext.getCurrentInstance();
        context.execute("PF('roleInteractionsErrorW').show();");
                break;
            case "Warning":
                         context = RequestContext.getCurrentInstance();
        context.execute("PF('roleInteractionsWarningW').show();");
                setRoleInteractionsWarning(true);
               break;
            case "Request":
                         context = RequestContext.getCurrentInstance();
        context.execute("PF('roleInteractionsRequestW').show();");
                setRoleInteractionsRequest(true);
                break;
        }


    }

    public void activateResponsibilities() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String url = request.getRequestURL().toString();
        if (url.toLowerCase().contains("users") || url.toLowerCase().contains("responsibilities") || url.toLowerCase().contains("dialog")||url.toLowerCase().contains("statistics")) {
            FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "loggedPageUser.xhtml?faces-redirect=true;");
        }
        this.myResponsibilities = true;
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
            case "roleInteractionsRequest":
                setRoleInteractionsRequest(false);
                break;
            
            case "roleInteractionsWarning":
                setRoleInteractionsWarning(false);
                break;
            case "roleInteractionsError":
                setRoleInteractionsError(false);
                break;
            case "roleInteractionsNotification":
                setRoleInteractionsNotification(false);
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

    /**
     * @return the roleInteractionsNotification
     */
    public boolean isRoleInteractionsNotification() {
        return roleInteractionsNotification;
    }

    /**
     * @param roleInteractionsNotification the roleInteractionsNotification to set
     */
    public void setRoleInteractionsNotification(boolean roleInteractionsNotification) {
        this.roleInteractionsNotification = roleInteractionsNotification;
    }

    /**
     * @return the roleInteractionsRequest
     */
    public boolean isRoleInteractionsRequest() {
        return roleInteractionsRequest;
    }

    /**
     * @param roleInteractionsRequest the roleInteractionsRequest to set
     */
    public void setRoleInteractionsRequest(boolean roleInteractionsRequest) {
        this.roleInteractionsRequest = roleInteractionsRequest;
    }

    /**
     * @return the roleInteractionsWarning
     */
    public boolean isRoleInteractionsWarning() {
        return roleInteractionsWarning;
    }

    /**
     * @param roleInteractionsWarning the roleInteractionsWarning to set
     */
    public void setRoleInteractionsWarning(boolean roleInteractionsWarning) {
        this.roleInteractionsWarning = roleInteractionsWarning;
    }

    /**
     * @return the roleInteractionsError
     */
    public boolean isRoleInteractionsError() {
        return roleInteractionsError;
    }

    /**
     * @param roleInteractionsError the roleInteractionsError to set
     */
    public void setRoleInteractionsError(boolean roleInteractionsError) {
        this.roleInteractionsError = roleInteractionsError;
    }
}
