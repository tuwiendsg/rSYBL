/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Georgiana
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GovernanceScope", propOrder = {
    "query","consideringUncertainty",
   "id"
})
public class GovernanceScope {
    @XmlAttribute(name="Query")
    private String query="";
   @XmlAttribute(name = "Id")
    private String id;
   @XmlAttribute(name="ConsideringUncertainty")
   private String consideringUncertainty;

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

   

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the consideringUncertainty
     */
    public String getConsideringUncertainty() {
        return consideringUncertainty;
    }

    /**
     * @param consideringUncertainty the consideringUncertainty to set
     */
    public void setConsideringUncertainty(String consideringUncertainty) {
        this.consideringUncertainty = consideringUncertainty;
    }


}
