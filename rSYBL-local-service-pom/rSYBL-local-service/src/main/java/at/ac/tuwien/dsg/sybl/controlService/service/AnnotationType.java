
package at.ac.tuwien.dsg.sybl.controlService.service;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for annotationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="annotationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CLOUD_SERVICE"/>
 *     &lt;enumeration value="SERVICE_UNIT"/>
 *     &lt;enumeration value="SERVICE_TOPOLOGY"/>
 *     &lt;enumeration value="CODE_REGION"/>
 *     &lt;enumeration value="RELATIONSHIP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "annotationType")
@XmlEnum
public enum AnnotationType {

    CLOUD_SERVICE,
    SERVICE_UNIT,
    SERVICE_TOPOLOGY,
    CODE_REGION,
    RELATIONSHIP;

    public String value() {
        return name();
    }

    public static AnnotationType fromValue(String v) {
        return valueOf(v);
    }

}
