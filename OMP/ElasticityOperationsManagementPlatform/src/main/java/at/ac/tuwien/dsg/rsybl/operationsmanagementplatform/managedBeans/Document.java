/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

/**
 *
 * @author Georgiana
 */
 
import java.io.Serializable;
 
public class Document implements Serializable, Comparable<Document> {
 
    private String roleName;
     
    private String details;
     

     
    public Document(String name, String size) {
        this.roleName = name;
        this.details = size;
    }
 
    public String getRoleName() {
        return roleName;
    }
 
    public void setRoleName(String name) {
        this.roleName = name;
    }
 
    public String getDetails() {
        return details;
    }
 
    public void setDetails(String size) {
        this.details = size;
    }

 
    //Eclipse Generated hashCode and equals
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        return result;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Document other = (Document) obj;
        if (roleName == null) {
            if (other.roleName != null)
                return false;
        } else if (!roleName.equals(other.roleName))
            return false;
        if (details == null) {
            if (other.details != null)
                return false;
        } else if (!details.equals(other.details))
            return false;
   
        return true;
    }
 
    @Override
    public String toString() {
        return roleName;
    }
 
    public int compareTo(Document document) {
        return this.getRoleName().compareTo(document.getRoleName());
    }
}  
