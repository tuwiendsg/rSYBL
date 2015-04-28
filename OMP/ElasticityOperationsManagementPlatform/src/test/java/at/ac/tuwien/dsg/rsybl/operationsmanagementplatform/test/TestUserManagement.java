/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.test;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IRoleManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class TestUserManagement {

    protected static Context ctx;
    protected static EJBContainer ejbContainer;

    @Resource
    protected UserTransaction userTransaction;

    @PersistenceContext
    protected EntityManager em;
    @EJB
    protected IUserManagementSessionBean userManagementSessionBean;
    @EJB
    protected IRoleManagementSessionBean roleManagementSessionBean;
    @EJB
    protected ISetupInitialDataSessionBean setupInitialDataSessionBean;

    public TestUserManagement() {
    }

    @BeforeClass
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
        p.put("omp.JtaManaged", "true");

        ejbContainer = EJBContainer.createEJBContainer(p);

        ctx = ejbContainer.getContext();
    }

    @After
    public void clean() {
        userManagementSessionBean.clearData();
        roleManagementSessionBean.clearRoleData();

    }

    @Before
    public void init() throws Exception {
        ctx.bind("inject", this);
//        roleManagementSessionBean = (IRoleManagementSessionBean) ctx.lookup("java:global/OperationsManagementPlatform/RoleManagementSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IRoleManagementSessionBean");
//        setupInitialDataSessionBean = (ISetupInitialDataSessionBean) ctx.lookup("java:global/OperationsManagementPlatform/SetupInitialDataSessionBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean");
//
//        userManagementSessionBean = (IUserManagementSessionBean) ctx.lookup("java:global/OperationsManagementPlatform/UserManagementBean!at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean");

    }

    @Test
    public void testUsername() {
        setupInitialDataSessionBean.populateWithITILRoles();

        userManagementSessionBean.createUser("gigi", "lala");
        List<IRole> roles = roleManagementSessionBean.findAllRoles();
        for (IRole r : roles) {
            System.out.println(r.getId() + " role name " + r.getRoleName());
        }
        IRole role = roleManagementSessionBean.searchForRoleWithName("Systems Operator");
        userManagementSessionBean.addRole(role.getId(), "gigi");
        assertNotNull(userManagementSessionBean.searchForUserByUsername("gigi"));
    }

    @Test
    public void checkIfUsersExist() {
        setupInitialDataSessionBean.populateWithITILRoles();
        setupInitialDataSessionBean.initializeUsers();
        assertNotNull(userManagementSessionBean.searchForUserByUsername("admin"));
        assertNotNull(userManagementSessionBean.searchForUserByUsername("georgiana"));

    }

    @AfterClass
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
