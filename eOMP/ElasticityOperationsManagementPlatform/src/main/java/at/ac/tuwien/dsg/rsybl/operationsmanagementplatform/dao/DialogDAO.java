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