package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElasticityCapability")
public class ElasticityCapability {
	 @XmlAttribute(name = "Name")
		private String name="";
	 @XmlAttribute(name="PrimitiveOperationsSequence")
	 private String primitiveOperations;
	 @XmlAttribute(name = "Endpoint")
		private String script="";
	 @XmlAttribute(name = "Type")
		private String type=""; // REST/PluginMethod/Script
	 
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPrimitiveOperations() {
		return primitiveOperations;
	}
	public void setPrimitiveOperations(String primitiveOperations) {
		this.primitiveOperations = primitiveOperations;
	}
}
