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
public interface IRole {

    public Long getId();

    public void setId(Long id);

    public Set<IResponsibility> getResponsabilities();

    public void setResponsabilities(Set<IResponsibility> responsabilities);

    public void addResponsability(IResponsibility responsability);

    public int getAuthority();

    public void setAuthority(int authority);

    public String getRoleName();

    public void setRoleName(String roleName);

}
