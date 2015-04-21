///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao.GenericHibernateDAO;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.TypedValue;

/**
 *
 * @author Georgiana
 */
public class RoleDAO extends GenericHibernateDAO<IRole> {

    public RoleDAO() {
        super(Role.class);
    }

    public IRole findByRoleName(String name) {

        Criteria criteria = getSession().createCriteria(Role.class);
        criteria.setFetchMode(Role.class.getSimpleName(), FetchMode.JOIN).add(Restrictions.eq("roleName", name));

        List result = criteria.list();

        List<IRole> roles = new ArrayList<IRole>();
        for (Object o : result) {
            roles.add((IRole) o);
        }
        if (roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    }

}
