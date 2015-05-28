/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.test;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.ResponsibilityDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.RoleDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.List;
import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
//@Local
public class TestEntities {
//
//    EntityManager em;
//    EntityManagerFactory emf;
//
//    public TestEntities() {
//    }
//
//
//    @Before
//    public void init() throws Exception {
//        emf = Persistence.createEntityManagerFactory("OperationsManagementPlatform_PU");
//        em = emf.createEntityManager();
//
//    }
//
//   
//    
//
////    @Test
//    public void testEntities() {
//        // Store 1000 Point objects in the database:
//        em.getTransaction().begin();
//        for (int i = 0; i < 10; i++) {
//            Role p = new Role();
//            em.persist(p);
//        }
//        em.getTransaction().commit();
//
//        // Find the number of Point objects in the database:
//        Query q1 = em.createQuery("SELECT COUNT(p) FROM Role p");
//        System.out.println("Total Roles: " + q1.getSingleResult());
//
//        // Find the average X value:
////        Query q2 = em.createQuery("SELECT AVG(p.x) FROM Point p");
////        System.out.println("Average X: " + q2.getSingleResult());
//        // Retrieve all the Point objects from the database:
//        TypedQuery<Role> query
//                = em.createQuery("SELECT p FROM Role p", Role.class);
//        List<Role> results = query.getResultList();
//        for (Role p : results) {
//            System.out.println(p);
//        }
//    }
////    @Test
//    public void hibernateTest(){
//        em.getTransaction().begin();
//        for (int i = 0; i < 10; i++) {
//            Role p = new Role();
//            em.persist(p);
//        }
//        em.getTransaction().commit();
//
//        RoleDAO roleDAO = new RoleDAO();
//        roleDAO.setEntityManager(em);
//        List<IRole> roles=roleDAO.findAll();
//        for (IRole role:roles){
//            System.out.println(role.getId());
//        }
//    }
////    @Test
//    public void testRelationships(){
//          em.getTransaction().begin();
//        for (int i = 0; i < 10; i++) {
//            Role p = new Role();
//            Responsibility responsability = new Responsibility();
//            responsability.setResponsabilityType("Cost"+i);
//            p.addResponsability(responsability);
//            em.persist(p);
//            em.persist(responsability);
//        }
//        em.getTransaction().commit();
//        ResponsibilityDAO  responsibilityDAO = new ResponsibilityDAO();
//        responsibilityDAO.setEntityManager(em);
//        for (IResponsibility res:responsibilityDAO.findAll()){
//            System.out.println("Responsibility " +res.getResponsabilityType());
//        }
//    }
//    @After
//    public void tearDown() {
//        em.close();
//        emf.close();
//    }

}
