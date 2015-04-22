/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.mapper;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public class MapToCommunicationObjects {
    public static Role mapFromRole(IRole irole){
        Role role = new Role();
        role.setAuthority(irole.getAuthority());
        role.setRoleName(irole.getRoleName());
        Set<IResponsibility> resp = irole.getResponsabilities();
        for (IResponsibility r:resp){
            role.addResponsability(mapFromResponsibility(r));
        }
        return role;
    }
    public static Responsibility mapFromResponsibility(IResponsibility iResponsibility){
        Responsibility resp = new Responsibility();
        resp.setAssociatedMetricPatterns(iResponsibility.getAssociatedMetricPatterns());
        resp.setAssociatedMetrics(iResponsibility.getAssociatedMetrics());
        resp.setResponsabilityType(iResponsibility.getResponsabilityType());
        resp.setResponsibilityName(iResponsibility.getResponsibilityName());
        return resp;
    }
}
