/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces;

import java.util.Set;

/**
 *
 * @author Georgiana
 */
public interface IUser {

    public Long getId();

    public void setId(Long id);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public Set<IRole> getRoles();

    public void setRoles(Set<IRole> roles);

    public void addRole(IRole role);

    public String getName();
}
