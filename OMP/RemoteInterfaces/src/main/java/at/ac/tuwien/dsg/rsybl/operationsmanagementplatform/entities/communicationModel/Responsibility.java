/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 *
 * @author Georgiana
 */
public class Responsibility implements Serializable, IResponsibility {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Set<Role> roles = new LinkedHashSet<Role>();

    private String responsabilityType; // responsability

    private ArrayList<String> associatedMetrics = new ArrayList<String>();
    private ArrayList<String> associatedMetricPatterns = new ArrayList<String>();
    private String responsibilityName;

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
        if (!(object instanceof Responsibility)) {
            return false;
        }
        Responsibility other = (Responsibility) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Responsability[ id=" + id + " ]";
    }

    /**
     * @return the responsabilityType
     */
    @Override
    public String getResponsabilityType() {
        return responsabilityType;
    }

    /**
     * @param responsabilityType the responsabilityType to set
     */
    @Override
    public void setResponsabilityType(String responsabilityType) {
        this.responsabilityType = responsabilityType;
    }

    /**
     * @return the associatedMetrics
     */
    @Override
    public ArrayList<String> getAssociatedMetrics() {
        return associatedMetrics;
    }

    /**
     * @param associatedMetrics the associatedMetrics to set
     */
    @Override
    public void setAssociatedMetrics(ArrayList<String> associatedMetrics) {
        this.associatedMetrics = associatedMetrics;
    }

    /**
     * @return the roles
     */
    public Set<IRole> getRoles() {
        return (Set<IRole>) (Set<?>) roles;
    }

    /**
     * @param roles the roles to set
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setRoles(Set<IRole> roles) {
        this.roles = (Set<Role>) (Set<?>) roles;
    }

    /**
     * @return the associatedMetricPatterns
     */
    @Override
    public ArrayList<String> getAssociatedMetricPatterns() {
        return associatedMetricPatterns;
    }

    /**
     * @param associatedMetricPatterns the associatedMetricPatterns to set
     */
    @Override
    public void setAssociatedMetricPatterns(ArrayList<String> associatedMetricPatterns) {
        this.associatedMetricPatterns = associatedMetricPatterns;
    }

    /**
     * @return the responsibilityName
     */
    public String getResponsibilityName() {
        return responsibilityName;
    }

    /**
     * @param responsibilityName the responsibilityName to set
     */
    public void setResponsibilityName(String responsibilityName) {
        this.responsibilityName = responsibilityName;
    }

}
