/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataBeanRemote;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementBeanRemote;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean;
import java.util.List;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

/**
 *
 * @author Georgiana
 */
public class AccessOrganizationInfo {

    ISetupInitialDataBeanRemote iSetupInitialDataBeanRemote;
    IUserManagementBeanRemote iUserManagementBeanRemote;

    public AccessOrganizationInfo() throws NamingException {
        Properties p = new Properties();
        p.put("java.naming.factory.initial", "org.apache.openejb.client.RemoteInitialContextFactory");
        p.put("java.naming.provider.url", "http://127.0.0.1:8080/tomee/ejb");

        InitialContext ctx = new InitialContext(p);

        Object obj = ctx.lookup("java:global/ElasticityOperationsManagementPlatform/SetupInitialDataSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataBeanRemote");
        Object newObj = ctx.lookup("java:global/ElasticityOperationsManagementPlatform/UserManagementBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementBeanRemote");
        iSetupInitialDataBeanRemote
                = (ISetupInitialDataBeanRemote) PortableRemoteObject.narrow(obj, ISetupInitialDataBeanRemote.class);
        iUserManagementBeanRemote = (IUserManagementBeanRemote) PortableRemoteObject.narrow(newObj, IUserManagementBeanRemote.class);

    }

    public List<IResponsibility> getAllResponsibilities() {
        return iSetupInitialDataBeanRemote.findAllResponsibilitiesRemote();
    }

    public List<IRole> getAllRoles() {
        return iSetupInitialDataBeanRemote.findAllRolesRemote();
    }

}
