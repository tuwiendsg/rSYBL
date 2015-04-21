/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.SetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.UserManagementBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans.UserManagedBean;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.faces.bean.ManagedBean;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;

public class DataAccess {

    @EJB(beanInterface = ISetupInitialDataSessionBean.class)
    SetupInitialDataSessionBean iSetupInitialDataSessionBean;
    @EJB(beanInterface = IUserManagementSessionBean.class)
    UserManagementBean iUserManagementSessionBean;
    @Resource
    protected UserTransaction userTransaction;

    @PersistenceContext
    protected EntityManager em;
    protected static Context ctx;
    protected static EJBContainer ejbContainer;
    protected static DataAccess dataAccess;

    private DataAccess() {

    }

    public static DataAccess getDataAccess() {
        if (dataAccess == null) {
            dataAccess = new DataAccess();
        }
        return dataAccess;
    }

    @PostConstruct
    public void init() {

//        System.setProperty("openejb.validation.output.level", "VERBOSE");
//        System.setProperty("openejb.jpa.auto-scan", "true");
//        System.setProperty("openejb.embedded.initialcontext.close", "DESTROY");
//
//        Properties p = new Properties();
//        p.put(Context.INITIAL_CONTEXT_FACTORY,
//                "org.apache.openejb.client.LocalInitialContextFactory");
//
//        // define data-source
//        p.put("omp", "new://Resource?type=DataSource");
//        p.put("omp.JdbcDriver", "org.h2.Driver");
//        p.put("omp.JdbcUrl",
//                "jdbc:h2:tcp://localhost/~/ompData;AUTO_SERVER=TRUE;MVCC=true");
//        p.put("omp.UserName", "");
//        p.put("omp.Password", "");
////        p.put("omp.JtaManaged", "true");
//
//        ejbContainer = EJBContainer.createEJBContainer(p);
//        
//        ctx = ejbContainer.getContext();
//        try {
//            ctx.bind("inject", this);
        iSetupInitialDataSessionBean.populateWithITILRoles();
        iSetupInitialDataSessionBean.initializeUsers();
//        } catch (NamingException ex) {
//            ex.printStackTrace();
//        }
    }

    @PreDestroy
    public void destroy() {
//        if (ctx != null) {
//            try {
//                ctx.close();
//            } catch (NamingException e) {
//            }
//        }
//
//        if (ejbContainer != null) {
//            ejbContainer.close();
//        }
    }

    public boolean login(String username, String password) {
        return iUserManagementSessionBean.login(username, password);
    }
}
