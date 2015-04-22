/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

/**
 *
 * @author Georgiana
 */
@Local
public interface IRoleManagementSessionBean {

    public List<IRole> findAllRoles();

    public List<IResponsibility> findAllResponsibilities();

    public void createRole(List<String> resp, String roleName, int authority);

    public void createResponsibility(String responsibilityType, ArrayList<String> metrics, ArrayList<String> metricPatterns);

    public IResponsibility searchForResponsibilityOfType(String type);

    public void clearRoleData();

    public IRole searchForRoleWithName(String name);

}
