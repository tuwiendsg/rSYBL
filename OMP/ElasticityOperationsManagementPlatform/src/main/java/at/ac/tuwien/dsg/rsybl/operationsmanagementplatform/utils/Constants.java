/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

}
