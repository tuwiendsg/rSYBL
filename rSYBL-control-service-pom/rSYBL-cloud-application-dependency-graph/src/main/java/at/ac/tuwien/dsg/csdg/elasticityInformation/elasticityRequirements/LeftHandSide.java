package at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Metric">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class LeftHandSide {

    @XmlElement(name = "Metric", required = false)
    protected String metric;
    @XmlElement(name = "Number", required = false)
	private String number;

    /**
     * Gets the value of the metric property.
     * 
     * @return
     *     possible object is
     *     {@link SYBLSpecification.Strategy.Condition.BinaryRestriction.LeftHandSide.Metric }
     *     
     */
    public String getMetric() {
        return metric;
    }

    /**
     * Sets the value of the metric property.
     * 
     * @param value
     *     allowed object is
     *     {@link SYBLSpecification.Strategy.Condition.BinaryRestriction.LeftHandSide.Metric }
     *     
     */
    public void setMetric(String value) {
        this.metric = value;
    }

	public String getNumber() {
		return number;
	}

	public void setNumber(String value) {
		this.number = value;
	}
	public String toString(){
		if (number!=null&&number!="") return number;
		else return metric;
	}

}