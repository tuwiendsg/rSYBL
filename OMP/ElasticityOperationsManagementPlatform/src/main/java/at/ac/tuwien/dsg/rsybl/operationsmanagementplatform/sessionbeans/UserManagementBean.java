/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.RoleDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.UserDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IUserManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.User;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.OMPLogger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.SystemException;

/**
 *
 * @author Georgiana
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class UserManagementBean implements IUserManagementSessionBean {

    @PersistenceContext
    protected EntityManager em;

    @Resource
    private javax.transaction.UserTransaction userTransaction;

    RoleDAO roleDAO;
    UserDAO userDAO;

    @PostConstruct
    public void init() {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        userDAO = new UserDAO();
        userDAO.setEntityManager(em);
    }

    @Override
    public void clearData() {
        try {
            userTransaction.begin();
            List<IUser> roles = userDAO.findAll();
            for (IUser r : roles) {
                em.remove(em.merge(r));
            }
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed clearing user data ~~~~ " + e.getMessage());
        }
    }

    @Override
    public void createUser(String username, String password) {
        try {
            userTransaction.begin();
            User user = new User();
            user.setPassword(password);
            user.setUsername(username);
            em.persist(user);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed creating user ~~~~ " + e.getMessage());
        }

    }

    @Override
    public void addRole(Long roleID, String username) {
        userDAO = new UserDAO();
        userDAO.setEntityManager(em);
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        Role role = (Role) roleDAO.findById(roleID);
        User user = (User) userDAO.findByUsername(username);
        if (user != null) {
            System.err.println(role.getId() + "-" + user.getId());
            try {
                userTransaction.begin();
                User newUser = em.merge(user);
                newUser.addRole(em.merge(role));
                em.persist(newUser);
                em.flush();
                userTransaction.commit();
            } catch (Exception e) {
                try {
                    userTransaction.rollback();
                } catch (Exception ex) {

                }
                OMPLogger.logger.info("failed adding role ~~~~ " + e.getMessage());
            }
        }

    }

    @Override
    public IUser searchForUserByUsername(String username) {
        userDAO = new UserDAO();
        userDAO.setEntityManager(em);
        return userDAO.findByUsername(username);
    }

    @Override
    public boolean login(String username, String password) {
        userDAO = new UserDAO();
        userDAO.setEntityManager(em);

        if (userDAO.findByUsername(username) != null && userDAO.findByUsername(username).getPassword().equals(password)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<IUser> findAll() {
        userDAO = new UserDAO();
        userDAO.setEntityManager(em);
        return userDAO.findAll();
    }
}
