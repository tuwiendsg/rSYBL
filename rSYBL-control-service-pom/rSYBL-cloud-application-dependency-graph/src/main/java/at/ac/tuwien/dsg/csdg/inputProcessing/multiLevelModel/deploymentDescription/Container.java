package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Container")
public class Container {
@XmlAttribute(name = "Id")
    private String id = "";
    @XmlAttribute(name = "Name")
    private String name = "";
    @XmlAttribute(name = "Path")
    private String path = "";
    @XmlElement(name = "AssociatedVM")
    private AssociatedVM vm = new AssociatedVM();

    public String getName() {
        return name;
    }

    public void setName(String ip) {
        this.name = ip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String uuid) {
        this.path = uuid;
    }

    /**
     * @return the vm
     */
    public AssociatedVM getVm() {
        return vm;
    }

    /**
     * @param vm the vm to set
     */
    public void setVm(AssociatedVM vm) {
        this.vm = vm;
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
}
