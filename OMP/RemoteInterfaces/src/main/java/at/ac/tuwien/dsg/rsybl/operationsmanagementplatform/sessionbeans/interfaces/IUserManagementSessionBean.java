/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;

/**
 *
 * @author Georgiana
 */
@Local
public interface IUserManagementSessionBean {

    public IUser searchForUserByUsername(String username);

    public void createUser(String username, String password);

    public void addRole(Long roleID, String username);

    public List<IUser> findAll();

    public void clearData();

    public boolean login(String username, String password);
}
