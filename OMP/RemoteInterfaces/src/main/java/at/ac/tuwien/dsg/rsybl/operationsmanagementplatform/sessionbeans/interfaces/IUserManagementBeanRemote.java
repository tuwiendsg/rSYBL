/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.sessionbeans.interfaces;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author Georgiana
 */
@Remote
public interface IUserManagementBeanRemote {
    
    
    public List<String> findAllRemote();

}
