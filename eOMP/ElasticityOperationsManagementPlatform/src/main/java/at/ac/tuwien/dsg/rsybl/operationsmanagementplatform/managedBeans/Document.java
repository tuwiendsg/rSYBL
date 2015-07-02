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
