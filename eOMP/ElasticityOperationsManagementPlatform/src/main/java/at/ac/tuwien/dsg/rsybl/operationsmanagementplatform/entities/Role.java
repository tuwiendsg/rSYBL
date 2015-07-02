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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.Constants;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.authorityvalidator.Authority;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

/**
 *
 * @author Georgiana
 */
@Entity
public class Role implements Serializable, IRole {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = {
        @JoinColumn( name = Constants.I_Role, referencedColumnName = "ID")},
            inverseJoinColumns = {
                @JoinColumn(name = Constants.I_Responsibility, referencedColumnName = "ID")})
    private Set<Responsibility> responsabilities = new LinkedHashSet<Responsibility>();

    @Authority(min = 0, max = 10)
    private int authority;

    @ManyToMany(mappedBy = "roles")
    private Set<User> currentUsers = new LinkedHashSet<>();

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
    public Set<User> getCurrentUsers() {
        return currentUsers;
    }

    /**
     * @param currentUsers the currentUsers to set
     */
    public void setCurrentUsers(Set<User> currentUsers) {
        this.currentUsers = currentUsers;
    }
}
