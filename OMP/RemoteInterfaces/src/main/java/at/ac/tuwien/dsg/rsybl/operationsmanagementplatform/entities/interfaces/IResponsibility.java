/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public interface IResponsibility {

    public Long getId();

    public void setId(Long id);

    public String getResponsabilityType();

    public void setResponsabilityType(String responsabilityType);

    public ArrayList<String> getAssociatedMetrics();

    public void setAssociatedMetrics(ArrayList<String> associatedMetrics);

    public Set<IRole> getRoles();

    public void setRoles(Set<IRole> roles);

    public String getResponsibilityName();

    public ArrayList<String> getAssociatedMetricPatterns();

    public void setAssociatedMetricPatterns(ArrayList<String> associatedMetricPatterns);
}
