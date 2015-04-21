/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.ResponsibilityDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.RoleDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.UserDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.User;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.ISetupInitialDataSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.OMPLogger;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.TestData;
import java.util.List;
import java.util.Set;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Georgiana
 */
@Singleton
@LocalBean
@Startup
@TransactionManagement(TransactionManagementType.BEAN)
public class SetupInitialDataSessionBean implements ISetupInitialDataSessionBean {

    @PersistenceContext
    protected EntityManager em;

    @Resource
    private javax.transaction.UserTransaction userTransaction;

    protected RoleDAO roleDAO;
    protected UserDAO userDAO;

    @PostConstruct
    public void init() {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        userDAO = new UserDAO();
        userDAO.setEntityManager(em);
        populateWithITILRoles();
        initializeUsers();
    }

    @Override
    public void populateWithITILRoles() {
        try {
            userTransaction.begin();
            TestData testData = new TestData(em);
            testData.createInitialRoles();
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed populating with ITIL roles ~~~~ " + e.getMessage());
        }

    }

    @Override
    public void initializeUsers() {
        try {
            userTransaction.begin();
            User user = new User();
            user.setUsername("admin");
            user.setPassword("admin");
            user.setName("SysAdmin");
            IRole role = (IRole) em.merge(roleDAO.findByRoleName("Systems Administrator"));

//            role.setRoleName("Systems Administrator");
            user.addRole(role);
            em.persist(user);
            em.flush();
            User georgiana = new User();
            georgiana.setUsername("georgiana");
            georgiana.setPassword("georgiana");
            georgiana.setName("Georgiana Copil");
            for (IRole irole : roleDAO.findAll()) {
                georgiana.addRole(em.merge(irole));
            }
            em.persist(georgiana);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            OMPLogger.logger.info("failed populating with users ~~~~ " + e.getMessage());
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed populating with ITIL roles ~~~~ " + e.getMessage());
        }
    }

    public List<IRole> findAllRoles() {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        return roleDAO.findAll();
    }

    public List<IResponsibility> findAllResponsibilities() {
        ResponsibilityDAO responsibilityDAO = new ResponsibilityDAO();
        responsibilityDAO.setEntityManager(em);
        return responsibilityDAO.findAll();
    }
}