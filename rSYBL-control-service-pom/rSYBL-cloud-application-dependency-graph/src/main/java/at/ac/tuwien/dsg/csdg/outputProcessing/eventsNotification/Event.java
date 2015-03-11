package at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="Event", namespace="")
public class Event {
	public static class Types {
		public static String NOTIFICATION = "NOTIFICATION";
		public static String ERROR ="ERROR";
		public static String UNHEALTHY_SP = "UNHEALTHY_SP";

	    private final String name;       

	    private Types(String s) {
	        name = s;
	    }

	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }

	}

	 @XmlAttribute(name = "type")
		private String type; // one from the Types in the enum above
	 @XmlAttribute(name="cause")
	 private List<String> causes;
	 @XmlAttribute(name="effect")
	 private List<String> effects;
	public List<String> getCauses() {
		return causes;
	}
	public void setCauses(List<String> causes) {
		this.causes = causes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void addType(String cause){
		if (causes==null){
			causes = new ArrayList<String>();
			
		}
		causes.add(cause);
	}
	public void addEffect(String effect){
		if (effects ==null){
			effects = new ArrayList<String>();
		}
		effects.add(effect);
	}
	public List<String> getEffects() {
		return effects;
	}
	public void setEffects(List<String> effects) {
		this.effects = effects;
	}
	 
	 
	 

}
