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
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.utils;

/**
 *
 * @author Georgiana
 */
public class Constants {
    /* TYPES (CLASSES) */

    public static final String T_Responsibility = "Responsibility";
    public static final String T_Role = "Role";
    public static final String T_ThirdPartyRole = "ThirdPartyRole";

    /*COLUMNS (properties) */
    public static final String C_ResponsibilityType = "responsibility_type";
    public static final String C_RoleName = "role_name";

    /* IDs (FOREIGN KEYS) */
    public static final String I_Responsibility = "responsibility_id";
    public static final String I_Role = "role_id";
    public static final String I_User = "user_id";
    public static final String I_ThirdPartyRole = "thirdpartyrole_id";

    /* ASSOCIATION NAMES (FOR QUERIES) */
    public static final String A_Responsibility = "responsibility";
    public static final String A_Role = "role";
    public static final String A_Interaction = "interaction";
    

}
