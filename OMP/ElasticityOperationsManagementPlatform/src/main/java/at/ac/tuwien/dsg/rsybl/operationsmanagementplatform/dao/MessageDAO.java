/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Message;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgiana
 */
public class MessageDAO extends GenericHibernateDAO<IMessage> {

    public MessageDAO() {
        super(Message.class);
    }
     public IMessage findByUUID(String uuid) {

        Criteria criteria = getSession().createCriteria(IDialog.class);
        criteria.setFetchMode(Message.class.getSimpleName(), FetchMode.JOIN).add(Restrictions.eq("uuid", uuid));

        List result = criteria.list();

        List<IMessage> roles = new ArrayList<IMessage>();
        for (Object o : result) {
            roles.add((IMessage) o);
        }
        if (roles.size() > 0) {
            return roles.get(0);
        }
        return null;
    } 
}
