/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Dialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgiana
 */
public class DialogDAO extends GenericHibernateDAO<IDialog> {

    public DialogDAO() {
        super(Dialog.class);
    }
    public IDialog findByUUID(String uuid) {

        Criteria criteria = getSession().createCriteria(IDialog.class);
        criteria.setFetchMode(Dialog.class.getSimpleName(), FetchMode.JOIN).add(Restrictions.eq("uuid", uuid));

        List result = criteria.list();

        List<IDialog> roles = new ArrayList<IDialog>();
        for (Object o : result) {
            roles.add((IDialog) o);
        }
        if (roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    }
}