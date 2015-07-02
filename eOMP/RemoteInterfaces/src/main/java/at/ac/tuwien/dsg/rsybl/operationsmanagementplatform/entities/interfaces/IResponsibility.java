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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Georgiana
 */
public interface IResponsibility {

    public Long getId();

    public void setId(Long id);

    public String getResponsabilityType();

    public void setResponsabilityType(String responsabilityType);

    public ArrayList<String> getAssociatedMetrics();

    public void setAssociatedMetrics(ArrayList<String> associatedMetrics);

    public Set<IRole> getRoles();

    public void setRoles(Set<IRole> roles);

    public String getResponsibilityName();

    public ArrayList<String> getAssociatedMetricPatterns();

    public void setAssociatedMetricPatterns(ArrayList<String> associatedMetricPatterns);
}
