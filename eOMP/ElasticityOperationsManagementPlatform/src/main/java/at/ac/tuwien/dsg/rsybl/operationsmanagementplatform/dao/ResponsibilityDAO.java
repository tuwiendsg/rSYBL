/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils.Constants;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgiana
 */
public class ResponsibilityDAO extends GenericHibernateDAO<IResponsibility> {

    public ResponsibilityDAO() {
        super(Responsibility.class);
    }

    public IResponsibility findResponsibilityByType(String type) {
        Criteria criteria = getSession().createCriteria(Responsibility.class);
        criteria.add(Restrictions.eq("responsabilityType", type));

        List result = criteria.list();

        List<IResponsibility> responsibilities = (List<IResponsibility>) result;
        if (responsibilities.size() > 0) {
            return responsibilities.get(0);
        }
        return null;
    }

}
