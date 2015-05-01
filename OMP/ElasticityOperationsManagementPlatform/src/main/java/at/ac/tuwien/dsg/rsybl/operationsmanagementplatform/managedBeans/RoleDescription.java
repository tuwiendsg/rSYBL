/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import java.util.List;

/**
 *
 * @author Georgiana
 */
 public class RoleDescription {

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