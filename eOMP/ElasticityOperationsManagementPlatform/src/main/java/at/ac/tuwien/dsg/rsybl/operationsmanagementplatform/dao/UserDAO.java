/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Responsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.User;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Example;

/**
 *
 * @author Georgiana
 */
public class UserDAO extends GenericHibernateDAO<IUser> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String type) {
        User res = new User();
        res.setUsername(type);

        List<User> users = this.getSession().createCriteria(User.class)
                .setFetchMode(Role.class.getSimpleName(), FetchMode.JOIN).add(Example.create(res)).list();
        List<IUser> us = new ArrayList<IUser>();
        for (Object o : users) {
            us.add((IUser) o);
        }
        if (us.size() > 0) {
            return (User) us.get(0);
        }
        return null;
    }

}
