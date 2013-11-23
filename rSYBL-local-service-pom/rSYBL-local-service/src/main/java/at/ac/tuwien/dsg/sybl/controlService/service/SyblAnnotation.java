
package at.ac.tuwien.dsg.sybl.controlService.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for syblAnnotation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="syblAnnotation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="annotationType" type="{http://main.controlService.sybl.dsg.tuwien.ac.at/}annotationType" minOccurs="0"/>
 *         &lt;element name="constraints" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="entityID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="monitoring" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="priorities" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strategies" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syblAnnotation", propOrder = {
    "annotationType",
    "constraints",
    "entityID",
    "monitoring",
    "priorities",
    "strategies"
})
public class SyblAnnotation {

    protected AnnotationType annotationType;
    protected String constraints;
    protected String entityID;
    protected String monitoring;
    protected String priorities;
    protected String strategies;

    /**
     * Gets the value of the annotationType property.
     * 
     * @return
     *     possible object is
     *     {@link AnnotationType }
     *     
     */
    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    /**
     * Sets the value of the annotationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AnnotationType }
     *     
     */
    public void setAnnotationType(AnnotationType value) {
        this.annotationType = value;
    }

    /**
     * Gets the value of the constraints property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConstraints() {
        return constraints;
    }

    /**
     * Sets the value of the constraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConstraints(String value) {
        this.constraints = value;
    }

    /**
     * Gets the value of the entityID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityID() {
        return entityID;
    }

    /**
     * Sets the value of the entityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityID(String value) {
        this.entityID = value;
    }

    /**
     * Gets the value of the monitoring property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonitoring() {
        return monitoring;
    }

    /**
     * Sets the value of the monitoring property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonitoring(String value) {
        this.monitoring = value;
    }

    /**
     * Gets the value of the priorities property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriorities() {
        return priorities;
    }

    /**
     * Sets the value of the priorities property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriorities(String value) {
        this.priorities = value;
    }

    /**
     * Gets the value of the strategies property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrategies() {
        return strategies;
    }

    /**
     * Sets the value of the strategies property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrategies(String value) {
        this.strategies = value;
    }

}
