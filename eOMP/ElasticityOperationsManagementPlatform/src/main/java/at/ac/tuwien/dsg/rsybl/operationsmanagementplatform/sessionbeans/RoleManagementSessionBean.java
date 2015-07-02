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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.ResponsibilityDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.RoleDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.OMPLogger;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.TestData;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IRoleManagementSessionBean;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces.IRoleManagementBeanRemote;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ejb.Remote;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.SystemException;
import org.hibernate.Session;

/**
 *
 * @author Georgiana
 */
@Stateless
@Remote(IRoleManagementBeanRemote.class)
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN)
public class RoleManagementSessionBean implements IRoleManagementSessionBean,IRoleManagementBeanRemote{

    @Resource
    private javax.transaction.UserTransaction userTransaction;
    @PersistenceContext
    protected EntityManager em;

    protected RoleDAO roleDAO;
    protected ResponsibilityDAO responsibilitiesDAO;

    public RoleManagementSessionBean() {

    }

    @PostConstruct
    public void init() {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        responsibilitiesDAO = new ResponsibilityDAO();
        responsibilitiesDAO.setEntityManager(em);

    }

    @Override
    public void createRole(List<String> resp, String roleName, int authority) {

        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        responsibilitiesDAO = new ResponsibilityDAO();
        responsibilitiesDAO.setEntityManager(em);

        try {
            userTransaction.begin();
            Role role = new Role();
            Set<IResponsibility> responsibilities = new LinkedHashSet<IResponsibility>();
            for (String r : resp) {
                IResponsibility responsibility = responsibilitiesDAO.findResponsibilityByType(r);
                if (responsibility != null) {
                    responsibilities.add(em.merge(responsibility));
                }
            }
            role.setResponsabilities(responsibilities);
            em.persist(role);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed creating role ~~~~ " + e.getMessage());
        }

    }

    @Override
    public void clearRoleData() {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        responsibilitiesDAO = new ResponsibilityDAO();

        responsibilitiesDAO.setEntityManager(em);
        List<IRole> roles = roleDAO.findAll();
        try {
            userTransaction.begin();
            for (IRole r : roles) {
                em.remove(em.merge(r));
            }
            List<IResponsibility> responsibilitys = responsibilitiesDAO.findAll();
            for (IResponsibility re : responsibilitys) {
                em.remove(em.merge(re));
            }
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed clearing role data ~~~~ " + e.getMessage());
        }
    }

    @Override
    public void createResponsibility(String responsibilityType, ArrayList<String> metrics, ArrayList<String> metricPatterns) {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);
        responsibilitiesDAO = new ResponsibilityDAO();
        responsibilitiesDAO.setEntityManager(em);
        IResponsibility resp = responsibilitiesDAO.findResponsibilityByType(responsibilityType);

        try {

            if (resp == null) {
                userTransaction.begin();
                resp = new Responsibility();
                resp.setAssociatedMetricPatterns(metricPatterns);
                resp.setAssociatedMetrics(metrics);
                resp.setResponsabilityType(responsibilityType);
                em.persist(resp);
                em.flush();
                userTransaction.commit();
            }
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (Exception ex) {

            }
            OMPLogger.logger.info("failed creating responsibility ~~~~ " + e.getMessage());
        }
    }

    @Override
    public List<IResponsibility> findAllResponsibilities() {

        responsibilitiesDAO = new ResponsibilityDAO();
        responsibilitiesDAO.setEntityManager(em);
        return responsibilitiesDAO.findAll();
    }

    @Override
    public IResponsibility searchForResponsibilityOfType(String type) {
        responsibilitiesDAO = new ResponsibilityDAO();
        responsibilitiesDAO.setEntityManager(em);
        return responsibilitiesDAO.findResponsibilityByType(type);
    }

    @Override
    public IRole searchForRoleWithName(String name) {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);

        return roleDAO.findByRoleName(name);
    }

    @Override
    public List<IRole> findAllRoles() {
        roleDAO = new RoleDAO();
        roleDAO.setEntityManager(em);

        return roleDAO.findAll();
    }




}
