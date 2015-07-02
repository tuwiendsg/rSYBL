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

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import org.apache.openjpa.persistence.jdbc.Unique;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author Georgiana
 */
@NamedQueries( {
    @NamedQuery(name = "selectDialogsOnInitiators", 
			query = "SELECT o FROM Dialog o "+
                                "JOIN o.interactions AS i "+
                                "JOIN i.initiator AS r " +
                                "WHERE r.roleName = :rolename"),
    @NamedQuery(name = "selectDialogsOnReceivers", 
			query = "SELECT o FROM Dialog o "+
                                "JOIN o.interactions AS i "+
                                "JOIN i.receiver AS r " +
                                "WHERE r.roleName = :rolename"),
     @NamedQuery(name = "selectDialogs", 
			query = "SELECT o FROM Dialog o "+
                                "JOIN o.interactions AS i "+
                                "JOIN i.receiver AS r " +
                                "JOIN i.initiator AS init "+
                                "WHERE r.roleName = :rolename OR init.roleName = :rolename"),
    @NamedQuery(name = "selectDialogsWithType", 
			query = "SELECT o FROM Dialog o "+
                                "JOIN o.interactions AS i "+
                                "JOIN i.receiver AS r " +
                                "JOIN i.initiator AS init "+
                                "WHERE (r.roleName = :rolename OR init.roleName = :rolename) "+
                                " AND i.type = :type")    

})
@Entity
public class Dialog implements IDialog, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Unique
    private String uuid;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Interaction> interactions = new HashSet<>();


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the interactions
     */
    public Set<IInteraction> getInteractions() {
        return (Set<IInteraction>) (Set<?>)interactions;
    }

    /**
     * @param interactions the interactions to set
     */
    @Override
    public void setInteractions(Set<IInteraction> interactions) {
        this.interactions = (Set<Interaction>) (Set<?>) interactions;
    }

  
    @Override
    public void addInteraction(IInteraction role){
        interactions.add((Interaction) role);
                }/**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
