package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AssociatedVM")
	public class AssociatedVM {
	    @XmlAttribute(name = "IP")
		private String ip="";
	    @XmlAttribute(name="UUID")
	    private String uuid="";
                 @XmlElement(name = "ElasticityCapability")
        private List<ElasticityCapability> elasticityCapabilities=new ArrayList<ElasticityCapability>();
		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

    /**
     * @return the elasticityCapabilities
     */
    public List<ElasticityCapability> getElasticityCapabilities() {
        return elasticityCapabilities;
    }

    /**
     * @param elasticityCapabilities the elasticityCapabilities to set
     */
    public void setElasticityCapabilities(List<ElasticityCapability> elasticityCapabilities) {
        this.setElasticityCapabilities(elasticityCapabilities);
    }


	 
}
