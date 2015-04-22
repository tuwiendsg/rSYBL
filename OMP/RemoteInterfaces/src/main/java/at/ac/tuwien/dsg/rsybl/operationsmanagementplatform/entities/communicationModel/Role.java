/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;


import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public class Role implements Serializable, IRole {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String roleName;

    private Set<Responsibility> responsabilities = new LinkedHashSet<Responsibility>();

    private int authority;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Role)) {
            return false;
        }
        Role other = (Role) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Role[ id=" + id + " , name= " + this.roleName + " authority=" + authority + ", responsibilities=" + responsabilities + " ]";
    }

    /**
     * @return the responsabilities
     */
    public Set<IResponsibility> getResponsabilities() {
        return (Set<IResponsibility>) (Set<?>) responsabilities;
    }

    /**
     * @param responsabilities the responsabilities to set
     */
    @Override
    public void setResponsabilities(Set<IResponsibility> responsabilities) {
        this.responsabilities = (Set<Responsibility>) (Set<?>) responsabilities;
    }

    public void addResponsability(IResponsibility responsability) {
        responsabilities.add((Responsibility) responsability);
    }

    /**
     * @return the authority
     */
    @Override
    public int getAuthority() {
        return authority;
    }

    /**
     * @param authority the authority to set
     */
    public void setAuthority(int authority) {
        this.authority = authority;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName the roleName to set
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

//    /**
//     * @return the currentUsers
//     */
//    public Set<User> getCurrentUsers() {
//        return currentUsers;
//    }
//
//    /**
//     * @param currentUsers the currentUsers to set
//     */
//    public void setCurrentUsers(Set<User> currentUsers) {
//        this.currentUsers = currentUsers;
//    }
//    
    /**
     * @return the currentUsers
     */
}
