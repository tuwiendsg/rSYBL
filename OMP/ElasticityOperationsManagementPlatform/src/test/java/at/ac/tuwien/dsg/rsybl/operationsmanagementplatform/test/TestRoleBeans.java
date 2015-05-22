/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.test;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IRoleManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Georgiana
 */
public class TestRoleBeans {

    protected static Context ctx;
    protected static EJBContainer ejbContainer;

    @Resource
    protected UserTransaction userTransaction;

    @PersistenceContext
    protected EntityManager em;
    @EJB
    protected IRoleManagementSessionBean roleManagementSessionBean;
    @EJB
    protected ISetupInitialDataSessionBean setupInitialDataSessionBean;

    public TestRoleBeans() {
    }

//    @BeforeClass
    public static void startContainer() throws Exception {

        System.setProperty("openejb.validation.output.level", "VERBOSE");
        System.setProperty("openejb.jpa.auto-scan", "true");
        System.setProperty("openejb.embedded.initialcontext.close", "DESTROY");

        Properties p = new Properties();
        p.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.openejb.client.LocalInitialContextFactory");

        // define data-source
        p.put("omp", "new://Resource?type=DataSource");
        p.put("omp.JdbcDriver", "org.h2.Driver");
        p.put("omp.JdbcUrl",
                "jdbc:h2:tcp://localhost/~/ompData;AUTO_SERVER=TRUE;MVCC=true");
        p.put("omp.UserName", "");
        p.put("omp.Password", "");
        p.put("omp.JtaManaged", "false");

        ejbContainer = EJBContainer.createEJBContainer(p);

        ctx = ejbContainer.getContext();
    }

//    @Before
    public void init() throws Exception {
        ctx.bind("inject", this);

//        roleManagementSessionBean = (IRoleManagementSessionBean) ctx.lookup("java:global/OperationsManagementPlatform/RoleManagementSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IRoleManagementSessionBean");
//        setupInitialDataSessionBean = (ISetupInitialDataSessionBean) ctx.lookup("java:global/OperationsManagementPlatform/SetupInitialDataSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean");
    }

//    @AfterClass
    public static void closeContainer() {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
            }
        }

        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

//    @Test
    public void testDefaultData() {
        try {
//            setupInitialDataSessionBean.populateWithITILRoles();
//            setupInitialDataSessionBean.initializeUsers();
            List<IRole> roles = roleManagementSessionBean.findAllRoles();
            for (IRole r : roles) {
                System.out.println("Current role "+r.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

//    @After
    public void release() {
        try {

            roleManagementSessionBean.clearRoleData();
  setupInitialDataSessionBean.populateWithITILRoles();
            setupInitialDataSessionBean.initializeUsers();
          
            ctx.unbind("inject");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

//    @Test
    public void testRoleManagementBean() {
        try {

            List<IRole> roles = roleManagementSessionBean.findAllRoles();
            System.err.println(roles.size());
            assertEquals(9, roles.size());
            roleManagementSessionBean.createResponsibility("cost", new ArrayList<String>(), new ArrayList<String>());
            assertNotNull(roleManagementSessionBean.searchForResponsibilityOfType("cost"));
            List<String> responsibilities = new ArrayList<String>();
            responsibilities.add("cost");
            roleManagementSessionBean.createRole(responsibilities, "sysAdmin", 1);
            roles = roleManagementSessionBean.findAllRoles();
            for (IRole r : roles) {
                System.out.println("Current role "+r.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();;
            fail(e.getMessage());
        }

    }

//    @AfterClass
    public static void tearDown() {
        ejbContainer.close();

    }

}
