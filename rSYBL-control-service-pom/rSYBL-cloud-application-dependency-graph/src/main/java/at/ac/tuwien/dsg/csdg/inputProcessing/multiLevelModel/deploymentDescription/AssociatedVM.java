package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "AssociatedVM")
	public class AssociatedVM {
	    @XmlAttribute(name = "IP")
		private String ip="";

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

	 
}
