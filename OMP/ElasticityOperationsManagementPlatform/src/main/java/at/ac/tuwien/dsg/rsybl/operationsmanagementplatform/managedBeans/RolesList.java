/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Georgiana
 */
@ManagedBean(name = "roles")
@ApplicationScoped
public class RolesList implements Serializable{
   
    /**
     * @return the roles
     */
    public List<RoleDescription> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(List<RoleDescription> roles) {
        this.roles = roles;
    }

   
    private static String[] roleName;

    private static String[] description;
    private List<RoleDescription> roles;

    static {
        roleName = new String[10];
        roleName[0] = "Service Manager";
        roleName[1] = "Incident Analyst";
        roleName[2] = "Problem Owner/Manager";
        roleName[3] = "Test Manager";
        roleName[4] = "Configuration Librarian";
        roleName[5] = "IT Financial Manager/ IT Financial Administrator";
        roleName[6] = "Operations Manager";
        roleName[7] = "Systems Administrator";
        roleName[8] = "Systems Operator";
        roleName[9] = "Procurement Analyst";

        description = new String[10];
        description[0] = "responsible for Service Support and Service Delivery actions taken in order to meet IT requirements (responsible for user satisfaction). This role is interested in using elasticity for achieving best performance and quality at best possible cost.";
        description[1] = "provides support role to receive incidents which cannot be automatically resolved. From elasticity control perspective, to this role should be reported any detected incidents for which the controller has no solutions. This role is interested in the possible incidents which are discovered, or even introduced, while performing elasticity control processes. While when the service is deployed in a static environment, this role mainly interracts with the system administrator, and operations manager, for the case when the service is deployed in a cloud environment, it can receive valuable information from the elasticity controller.";
        description[2] = "responsible of reviewing problem trends. In the case of cloud service whose elasticiy is automatically controlled, this role should receive problems notifications from the elasticity controller. Moreover, the frequency of the notifications should differ with the gravity of the detected problems.";
        description[3] = "ensures proper testing for changes released into production. Whenever un-predictable behavior is detected by the cloud service elasticity controller, the test manager is notified for checking if it is the desired behavior.";
        description[4] = "responsible for maintaining up-to-date records of configuration items. When the possible runtime modifications that the elasticity controller is allowed to perform include configuration changes, the configuration librarian should be notified concerning the changes. Depending on the change frequency, it may be required that it receives an aggregated list of configurations performed.";
        description[5] = "monitors cost evolution and produces reports on IT assets and resources used by the service. When the analyzed service is deployed on a cloud environment, this role is interested in receiving the cost of hosting the service on possibly multiple cloud providers.";
        description[6] = "provide necessary services/resources in order to meet SLAs. When deployed in traditional servers (i.e., not cloud), the operations manager is in charge with managing all the roles which manage the infrastructure (e.g., Asset Administrator, Physical Site Engineer, Hardware Engineer). In the current case, when the service is deployed on the cloud and we use an elasticity controller, the mentioned roles are substituted by the elasticity controller, and the Operations Manager would need to interact alot with the elasticity controller.";
        description[7] = "administers infrastructure (servers, hosts, networking devices). The system administrator can use the elasticity controller to automate most of his/her tasks.";
        description[8] = "performs operational processes ensuring that services meet operational targets. The system operator can use the elasticity controller to automate most of his/her tasks.";
        description[9] = "contact for vendor suppliers. Is in charge with finding the vendors for the needed services, negotiating the costs and signing contracts. In cloud computing, part of these responsabilities are delegated to the elasticity controller. However, some responsabilities which involve humans and cannot be replaced by software (e.g., cloud providers do not offer re-negotiation APIs).";
    }

    @PostConstruct
    public void init() {
        setRoles(new ArrayList<RoleDescription>());
        for (int i = 0; i < 10; i++) {
            RoleDescription role = new RoleDescription();
            role.setRoleName(roleName[i]);
            role.setDescription(description[i]);
            roles.add(role);
        }

    }

  
}
