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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Georgiana
 */
 public class RoleDescription implements Serializable{

        private String roleName;
        private String description;
        
        private List<IDialog> selectedDialogs;
        public RoleDescription() {

        }

        /**
         * @return the roleName
         */
        public String getRoleName() {
            return roleName;
        }

        /**
         * @param roleName the roleName to set
         */
        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

    /**
     * @return the selectedDialogs
     */
    public List<IDialog> getSelectedDialogs() {
        return selectedDialogs;
    }

    /**
     * @param selectedDialogs the selectedDialogs to set
     */
    public void setSelectedDialogs(List<IDialog> selectedDialogs) {
        this.selectedDialogs = selectedDialogs;
    }

    }