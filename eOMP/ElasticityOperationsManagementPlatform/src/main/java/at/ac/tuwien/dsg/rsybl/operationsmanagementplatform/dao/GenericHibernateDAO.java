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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.dao;
//
///**
// *
// * @author Georgiana
// */
//

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

public abstract class GenericHibernateDAO<T> {

    protected Class<? extends T> persistentClass;
    protected EntityManager em;

    public GenericHibernateDAO(Class<? extends T> c) {
        this.persistentClass = c;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    protected EntityManager getEntityManager() {
        if (em == null) {
            throw new IllegalStateException(
                    "EntityManager has not been set on DAO before usage");
        }
        return em;
    }

    // TODO add HINT to ass1-description
    protected Session getSession() {
        return getEntityManager().unwrap(Session.class);
    }

    public Class<? extends T> getPersistentClass() {
        return persistentClass;
    }

    @SuppressWarnings("all")
    public T findById(Long id) {
        return (T) getSession().load(getPersistentClass(), id);
    }

    public List<T> findAll() {
        return findByCriteria();
    }

    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }

    @SuppressWarnings("all")
    protected List<T> findByCriteria(Criterion... criterion) {
        List<T> result = new LinkedList<T>();
        Criteria crit = getSession().createCriteria(getPersistentClass());
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        for (Object o : crit.list()) {
            result.add((T) o);
        }
        return result;
    }

}
