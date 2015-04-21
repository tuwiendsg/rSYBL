/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.ResponsibilityDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Georgiana
 */
public class TestData {

    protected EntityManager em;

    public TestData(EntityManager em) {
        this.em = em;
    }

    protected void createITILResponsibilities() {

        Responsibility cost = new Responsibility();
        ArrayList<String> associatedMetrics1 = new ArrayList<String>();
        associatedMetrics1.add("cost");
        associatedMetrics1.add("price");
        ArrayList<String> associatedMetricsPatterns1 = new ArrayList<String>();
        associatedMetricsPatterns1.add("*cost*");
        associatedMetricsPatterns1.add("*price*");
        cost.setAssociatedMetricPatterns(associatedMetricsPatterns1);
        cost.setAssociatedMetrics(associatedMetrics1);
        cost.setResponsabilityType("cost");
        cost.setResponsibilityName("Cost Elasticity");

        Responsibility quality = new Responsibility();
        ArrayList<String> associatedMetrics2 = new ArrayList<String>();
        associatedMetrics2.add("quality");
        associatedMetrics2.add("performance");
        associatedMetrics2.add("time");
        ArrayList<String> associatedMetricsPatterns2 = new ArrayList<String>();
        associatedMetricsPatterns2.add("*quality*");
        associatedMetricsPatterns2.add("*performance*");
        associatedMetricsPatterns2.add("*time*");
        quality.setAssociatedMetricPatterns(associatedMetricsPatterns2);
        quality.setAssociatedMetrics(associatedMetrics2);
        quality.setResponsabilityType("quality");
        quality.setResponsibilityName("Quality Elasticity");

        Responsibility resources = new Responsibility();
        ArrayList<String> associatedMetrics3 = new ArrayList<String>();
        associatedMetrics3.add("cpu");
        associatedMetrics3.add("memory");
        associatedMetrics3.add("disk");
        ArrayList<String> associatedMetricsPatterns3 = new ArrayList<String>();
        associatedMetricsPatterns3.add("*cpu*");
        associatedMetricsPatterns3.add("*mem*");
        associatedMetricsPatterns3.add("*disk*");
        resources.setAssociatedMetricPatterns(associatedMetricsPatterns3);
        resources.setAssociatedMetrics(associatedMetrics3);
        resources.setResponsabilityType("resources");
        resources.setResponsibilityName("Resources Elasticity");

        Responsibility error = new Responsibility();
        ArrayList<String> associatedMetrics4 = new ArrayList<String>();
        associatedMetrics4.add("error");
        associatedMetrics4.add("incident");
        ArrayList<String> associatedMetricsPatterns4 = new ArrayList<String>();
        associatedMetricsPatterns4.add("*error*");
        error.setAssociatedMetricPatterns(associatedMetricsPatterns4);
        error.setAssociatedMetrics(associatedMetrics4);
        error.setResponsabilityType("error");
        error.setResponsibilityName("Error Management");

        Responsibility configuration = new Responsibility();
        configuration.setResponsabilityType("configuration");
        configuration.setResponsibilityName("Configuration Management");

        Responsibility analytics = new Responsibility();
        ArrayList<String> associatedMetrics5 = new ArrayList<String>();
        associatedMetrics5.add("cpu");
        associatedMetrics5.add("memory");
        associatedMetrics5.add("disk");
        associatedMetrics5.add("error");
        associatedMetrics5.add("incident");
        associatedMetrics5.add("quality");
        associatedMetrics5.add("performance");
        associatedMetrics5.add("time");
        associatedMetrics5.add("cost");
        associatedMetrics5.add("price");
        ArrayList<String> associatedMetricsPatterns5 = new ArrayList<String>();
        associatedMetricsPatterns5.add("*cpu*");
        associatedMetricsPatterns5.add("*mem*");
        associatedMetricsPatterns5.add("*disk*");
        associatedMetricsPatterns5.add("*cost*");
        associatedMetricsPatterns5.add("*price*");
        associatedMetricsPatterns5.add("*quality*");
        associatedMetricsPatterns5.add("*performance*");
        associatedMetricsPatterns5.add("*time*");
        associatedMetricsPatterns5.add("*error*");
        analytics.setAssociatedMetricPatterns(associatedMetricsPatterns5);
        analytics.setAssociatedMetrics(associatedMetrics5);
        analytics.setResponsabilityType("analytics");
        analytics.setResponsibilityName("Service Behavior Analytics");

        em.persist(quality);
        em.persist(resources);
        em.persist(cost);
        em.persist(error);
        em.persist(analytics);
        em.persist(configuration);

    }

    protected void createITILRoles() {

        ResponsibilityDAO responsibilityDAO = new ResponsibilityDAO();
        responsibilityDAO.setEntityManager(em);

        Role serviceManager = new Role();
        serviceManager.setRoleName("Service Manager");
        serviceManager.addResponsability(responsibilityDAO.findResponsibilityByType("cost"));
        serviceManager.addResponsability(responsibilityDAO.findResponsibilityByType("quality"));
        serviceManager.addResponsability(responsibilityDAO.findResponsibilityByType("error"));
        serviceManager.addResponsability(responsibilityDAO.findResponsibilityByType("analytics"));
        serviceManager.setAuthority(10);
        em.persist(serviceManager);

        Role incidentAnalyst = new Role();
        incidentAnalyst.setRoleName("Incident Analyst");
        incidentAnalyst.addResponsability(responsibilityDAO.findResponsibilityByType("error"));
        incidentAnalyst.setAuthority(8);
        em.persist(incidentAnalyst);

        Role configurationLibrarian = new Role();
        configurationLibrarian.setRoleName("Configuration Librarian");
        configurationLibrarian.addResponsability(responsibilityDAO.findResponsibilityByType("configuration"));
        configurationLibrarian.setAuthority(5);
        em.persist(configurationLibrarian);

        Role itFinancialManager = new Role();
        itFinancialManager.setRoleName("IT Financial Manager");
        itFinancialManager.addResponsability(responsibilityDAO.findResponsibilityByType("cost"));
        itFinancialManager.addResponsability(responsibilityDAO.findResponsibilityByType("quality"));
        itFinancialManager.setAuthority(9);
        em.persist(itFinancialManager);

        Role operationsManager = new Role();
        operationsManager.setRoleName("Operations Manager");
        operationsManager.addResponsability(responsibilityDAO.findResponsibilityByType("cost"));
        operationsManager.addResponsability(responsibilityDAO.findResponsibilityByType("quality"));
        operationsManager.addResponsability(responsibilityDAO.findResponsibilityByType("resources"));
        operationsManager.addResponsability(responsibilityDAO.findResponsibilityByType("error"));
        operationsManager.addResponsability(responsibilityDAO.findResponsibilityByType("configuration"));
        operationsManager.setAuthority(7);
        em.persist(operationsManager);

        Role systemsAdministrator = new Role();
        systemsAdministrator.setRoleName("Systems Administrator");
        systemsAdministrator.addResponsability(responsibilityDAO.findResponsibilityByType("resources"));
        systemsAdministrator.addResponsability(responsibilityDAO.findResponsibilityByType("error"));
        systemsAdministrator.addResponsability(responsibilityDAO.findResponsibilityByType("configuration"));
        systemsAdministrator.setAuthority(6);
        em.persist(systemsAdministrator);

        Role procurementAnalysis = new Role();
        procurementAnalysis.setRoleName("Procurement Analysis");
        procurementAnalysis.addResponsability(responsibilityDAO.findResponsibilityByType("resources"));
        procurementAnalysis.addResponsability(responsibilityDAO.findResponsibilityByType("cost"));
        procurementAnalysis.setAuthority(6);
        em.persist(procurementAnalysis);

        Role systemsOperator = new Role();
        systemsOperator.setRoleName("Systems Operator");
        systemsOperator.addResponsability(responsibilityDAO.findResponsibilityByType("resources"));
        systemsOperator.addResponsability(responsibilityDAO.findResponsibilityByType("configuration"));
        systemsOperator.setAuthority(3);
        em.persist(systemsOperator);

    }

    public void createInitialRoles() {
        createITILResponsibilities();
        createITILRoles();
    }
}