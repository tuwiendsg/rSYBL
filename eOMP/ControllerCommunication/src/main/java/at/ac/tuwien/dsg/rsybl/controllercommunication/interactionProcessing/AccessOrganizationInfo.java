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
