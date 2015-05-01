/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.managedBeans;

import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceTopologyXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.ServiceUnitXML;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IDialog;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IInteraction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IUser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.component.datalist.DataList;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.diagram.Connection;
import org.primefaces.model.diagram.DefaultDiagramModel;
import org.primefaces.model.diagram.DiagramModel;
import org.primefaces.model.diagram.Element;
import org.primefaces.model.diagram.connector.FlowChartConnector;
import org.primefaces.model.diagram.endpoint.BlankEndPoint;
import org.primefaces.model.diagram.endpoint.DotEndPoint;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.endpoint.EndPointAnchor;
import org.primefaces.model.diagram.overlay.ArrowOverlay;
import org.primefaces.model.diagram.overlay.LabelOverlay;

/**
 *
 * @author Georgiana
 */
@ManagedBean
@RequestScoped
public class UserComponentsBean {

    @ManagedProperty(value = "#{userManagedBean}")
    private UserManagedBean userManagedBean;
    private DefaultDiagramModel diagram;
    private DefaultDiagramModel service;
    private DefaultDiagramModel dialogsDiagram;
    private PanelGrid panelGridServices; //bound to the view
    @ManagedProperty("#{roles}")
    private RolesList rolesList;

    private TreeNode root;
    private TreeNode selectedNode;
    private Document selectedDocument;
    public TreeNode getTreeNode(String serviceID) {
        CloudServiceXML cloudServiceXML = userManagedBean.getDescription(serviceID);
        if (cloudServiceXML != null) {
            HashMap<String, TreeNode> serviceTopologies = new HashMap<String, TreeNode>();
            TreeNode serviceDescriptionRoot = new DefaultTreeNode("Root", null);

            for (ServiceTopologyXML serviceTopologyXML : cloudServiceXML.getServiceTopologies()) {
                TreeNode node0 = new DefaultTreeNode(serviceTopologyXML.getId(), serviceDescriptionRoot);
                serviceTopologies.put(serviceTopologyXML.getId(), node0);
                for (ServiceUnitXML serviceUnitXML : serviceTopologyXML.getServiceUnits()) {
                    TreeNode nodex = new DefaultTreeNode(serviceUnitXML.getId(), node0);

                }
            }
            return serviceDescriptionRoot;

        }
        return null;
    }

    public void initTable() {
        createDocuments();
    }

    public TreeNode getRoot() {
        return root;
    }

    public List<RoleDescription> getRoleNames() {
        return rolesList.getRoles();
    }

 
    public TreeNode createDocuments() {
        root = new DefaultTreeNode(new Document("Responsibility/Roles ", "Details"), null);
        selectedNode = root;
        Set<IRole> associatedRoles = userManagedBean.getiRoles();

        for (IRole role : associatedRoles) {
            Set<IResponsibility> responsibilities = role.getResponsabilities();
            String responsibilityDetail = ". Responsibilities:";
            for (IResponsibility resp : responsibilities) {
                responsibilityDetail += " " + resp.getResponsibilityName();
            }
            TreeNode roleNode = new DefaultTreeNode(new Document(role.getRoleName(), "Authority " + role.getAuthority() + " out of 10"
                    + responsibilityDetail + "."), root);
            for (IResponsibility responsibility : responsibilities) {
                String metrics = "";
                for (String m : responsibility.getAssociatedMetrics()) {
                    metrics += " " + m;
                }
                TreeNode responsibilityNode = new DefaultTreeNode(new Document(responsibility.getResponsibilityName(),
                        " Responsibility " + responsibility.getResponsibilityName() + ", with focus on " + responsibility.getResponsabilityType() + " and interest in the following metrics: " + metrics + "."), roleNode);
            }
        }

        return root;
    }

    public Document getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        if (selectedNode != null) {
            this.selectedDocument = (Document) selectedNode.getData();
        }

    }

    public UserComponentsBean() {

    }

    public DefaultDiagramModel showDialogs() {
        if (userManagedBean.isLoggedIn()) {
            List<IDialog> dialogUUIDs = new ArrayList<IDialog>();
            List<RoleDescription> roleDescriptions = userManagedBean.getUserRoles();
           
            for (RoleDescription roleDescription:roleDescriptions){
                List<IDialog> selDial= roleDescription.getSelectedDialogs();
                if (selDial!=null){
                for (IDialog dialog:selDial){
                    if (!dialogUUIDs.contains(dialog)){
                        dialogUUIDs.add(dialog);
                    }
                }
                }
            }
            dialogsDiagram = new DefaultDiagramModel();
            if (!dialogUUIDs.isEmpty()){
            dialogsDiagram.setMaxConnections(-1);
            FlowChartConnector connector = new FlowChartConnector();
            connector.setPaintStyle("{strokeStyle:'#C7B097',lineWidth:3}");
            dialogsDiagram.setDefaultConnector(connector);
            EndPointAnchor[] anchors_BOTTOM = {EndPointAnchor.TOP_RIGHT, EndPointAnchor.BOTTOM_RIGHT, EndPointAnchor.LEFT, EndPointAnchor.BOTTOM_LEFT, EndPointAnchor.TOP_LEFT, EndPointAnchor.CONTINUOUS, EndPointAnchor.ASSIGN};
            EndPointAnchor[] anchors_TOP = {EndPointAnchor.TOP_LEFT, EndPointAnchor.BOTTOM_LEFT, EndPointAnchor.RIGHT, EndPointAnchor.BOTTOM_RIGHT, EndPointAnchor.BOTTOM_LEFT, EndPointAnchor.CONTINUOUS, EndPointAnchor.ASSIGN};
            dialogsDiagram.setDefaultConnector(connector);
            int size = 50;
            int dialogIndex = 0;
            for (IDialog dialog : dialogUUIDs) {
                Set<IInteraction> interactions = dialog.getInteractions();
                int index = 0;
                List<IRole> roles = new ArrayList<IRole>();
                HashMap<String, Element> elementRoles = new HashMap<String, Element>();
                Element start = new Element("Dialog ID: " + dialog.getUuid(), "20em", (3 + size / dialogUUIDs.size() * dialogIndex) + "em");
                start.addEndPoint(new BlankEndPoint(EndPointAnchor.BOTTOM));
                start.setStyleClass("dialog-name");
                dialogsDiagram.addElement(start);
                String elements = "";
                if (interactions.size() <= 4) {
                    elements = "ui-diagram-bigger";
                }
                if (interactions.size() > 4) {
                    elements = "ui-diagram-biggest";
                }
                int i = 0;
                for (IInteraction iInteraction : interactions) {
                    if (iInteraction.getMessage()!=null){
                    IRole initiator = iInteraction.getInitiator();
                    IRole receiver = iInteraction.getReceiver();
                    Element elementInitiator;
                    Element elementReceiver;
//                    
                    if (!roles.contains(initiator)) {
                        roles.add(initiator);
                        elementInitiator = new Element(initiator.getRoleName(), (50 * i) + "em", (10 + size / dialogUUIDs.size() * dialogIndex) + "em");
                        elementInitiator.addEndPoint(new DotEndPoint(anchors_BOTTOM[index]));
                        if (!elements.equalsIgnoreCase("")) {
                            elementInitiator.setStyleClass(elements);
                        }
                        elementRoles.put(iInteraction.getInitiator().getRoleName(), elementInitiator);
                        dialogsDiagram.addElement(elementInitiator);
                        i++;
                    } else {
                        elementInitiator = elementRoles.get(initiator.getRoleName());
                        elementInitiator.addEndPoint(new DotEndPoint(anchors_BOTTOM[index]));
                    }
                    if (!roles.contains(receiver)) {
                        roles.add(receiver);
                        elementReceiver = new Element(receiver.getRoleName(), (50 * i) + "em", (10 + size / dialogUUIDs.size() * dialogIndex) + "em");
                        elementReceiver.addEndPoint(new DotEndPoint(anchors_TOP[index]));
                        if (!elements.equalsIgnoreCase("")) {
                            elementReceiver.setStyleClass(elements);
                        }
                        elementRoles.put(iInteraction.getReceiver().getRoleName(), elementReceiver);
                        i++;
                        dialogsDiagram.addElement(elementReceiver);

                    } else {
                        elementReceiver = elementRoles.get(receiver.getRoleName());
                        elementReceiver.addEndPoint(new DotEndPoint(anchors_TOP[index]));
                    }
                    List<String> interMessage = new ArrayList<String>();
                    interMessage.add(iInteraction.getType());
                    if (iInteraction.getMessage().getCause()!= null && !iInteraction.getMessage().getCause().equalsIgnoreCase("")) {
                        interMessage.add("Cause "+iInteraction.getMessage().getCause());
                    }
                    if (iInteraction.getMessage().getActionEnforced() != null && !iInteraction.getMessage().getActionEnforced().equalsIgnoreCase("")) {
                        interMessage.add("Action "+iInteraction.getMessage().getActionEnforced());
                    }
                    if (iInteraction.getMessage().getDescription() != null && !iInteraction.getMessage().getDescription().equalsIgnoreCase("")) {
                        interMessage.add("Descr "+iInteraction.getMessage().getDescription());
                    }
                    dialogsDiagram.connect(createConnection(elementInitiator.getEndPoints().get(elementInitiator.getEndPoints().size() - 1), elementReceiver.getEndPoints().get(elementReceiver.getEndPoints().size() - 1), interMessage));
                    index++;
                }
                }
                dialogIndex++;
            }
            }
        }
        return dialogsDiagram;
    }

    private Connection createConnection(EndPoint from, EndPoint to, List<String> labels) {
        Connection conn = new Connection(from, to);
        conn.getOverlays().add(new ArrowOverlay(20, 20, 1, 1));
        int i = 0;
        for (String label : labels) {
            if (label != null) {
//            conn.getOverlays().add(new ArrowOverlay(20*(i+1), 20, 1, 1));
                conn.getOverlays().add(new LabelOverlay(label, "flow-label", 0.35 * (i + 1)));
            }
            i++;
        }
        return conn;
    }

    public void initRoles() {
        if (userManagedBean.isLoggedIn()) {
            diagram = new DefaultDiagramModel();
            diagram.setMaxConnections(-1);
            int size = 100;
            int userIndex = 0;
            HashMap<String, Element> roles = new HashMap<String, Element>();
            HashMap<String, Element> responsibilities = new HashMap<String, Element>();
            List<IRole> allRoles = userManagedBean.getAllRoles();
            List<IResponsibility> allResponsibilities = userManagedBean.getAllResponsibilities();

            int i = 0;

            for (IRole role : allRoles) {

                Element el = new Element(role.getRoleName() + " [" + role.getAuthority() + "]", size / allRoles.size() * i + "em", 20 + "em");
                el.addEndPoint(new DotEndPoint(EndPointAnchor.TOP));
                el.addEndPoint(new DotEndPoint(EndPointAnchor.BOTTOM));
                roles.put(role.getRoleName(), el);
                i++;

                diagram.addElement(el);

            }
            i = 0;
            for (IResponsibility resp : allResponsibilities) {

                Element el = new Element(resp.getResponsibilityName(), size / allResponsibilities.size() * i + "em", 40 + "em");
                el.addEndPoint(new DotEndPoint(EndPointAnchor.TOP));
                el.setStyleClass("ui-diagram-responsibility");
                responsibilities.put(resp.getResponsabilityType(), el);

                i++;
                diagram.addElement(el);
            }
            List<IUser> myUsers = userManagedBean.getAllUsersWithoutAdmin();

            for (IUser user : myUsers) {
                Set<IRole> myRoles = user.getRoles();

                Element us = new Element(user.getName(), (size / myUsers.size()) * (userIndex) + "em", 5 + "em");

                us.addEndPoint(new DotEndPoint(EndPointAnchor.BOTTOM));
                if (user.getUsername().equalsIgnoreCase(userManagedBean.getUsername())) {
                    us.setStyleClass("ui-diagram-current-user");

                } else {
                    us.setStyleClass("ui-diagram-user");
                }
                userIndex++;

                diagram.addElement(us);
                for (IRole r : myRoles) {
                    // diagram.addElement(roles.get(r.getRoleName()));
                    diagram.connect(new Connection(us.getEndPoints().get(0), roles.get(r.getRoleName()).getEndPoints().get(0)));
                    Set<IResponsibility> myResponsibilitys = r.getResponsabilities();
                    for (IResponsibility re : myResponsibilitys) {
                        //diagram.addElement(responsibilities.get(re.getResponsabilityType()));
                        diagram.connect(new Connection(roles.get(r.getRoleName()).getEndPoints().get(1), responsibilities.get(re.getResponsabilityType()).getEndPoints().get(0)));
                    }
                }

            }

        }

    }

    public DiagramModel getModel() {
        return getDiagram();
    }

    public void initServices() {
        if (userManagedBean.isLoggedIn()) {
            panelGridServices = new PanelGrid();
        }
    }

    @PostConstruct
    public void init() {
        initRoles();
        initServices();
        initTable();
        showDialogs();
    }
   

    /**
     * @return the userManagedBean
     */
    public UserManagedBean getUserManagedBean() {
        return userManagedBean;
    }

    /**
     * @param userManagedBean the userManagedBean to set
     */
    public void setUserManagedBean(UserManagedBean userManagedBean) {
        this.userManagedBean = userManagedBean;
    }

    /**
     * @return the panelGridServices
     */
    public PanelGrid getPanelGridServices() {
        return panelGridServices;
    }

    /**
     * @param panelGridServices the panelGridServices to set
     */
    public void setPanelGridServices(PanelGrid panelGridServices) {
        this.panelGridServices = panelGridServices;
    }

    public List<IInteraction> getAllInteractionsForUserAsReceiver(String rolename) {
        return userManagedBean.getAllInteractionsForUserAsReceiver(rolename);

    }

    public List<IInteraction> getAllInteractionsForUserAsInitiator(String rolename) {
        return userManagedBean.getAllInteractionsForUserAsInitiator(rolename);
    }

    public IDialog getDialogForInteraction(String dialogID) {
        return userManagedBean.getDialogForInteraction(dialogID);
    }

    public List<IDialog> getAllDialogsForUserAsReceiver(String rolename) {
        return userManagedBean.getAllDialogsForUserAsReceiver(rolename);

    }

    public List<IDialog> getAllDialogsForUserAsInitiator(String rolename) {
        return userManagedBean.getAllDialogsForUserAsInitiator(rolename);
    }

    /**
     * @return the diagram
     */
    public DefaultDiagramModel getDiagram() {
        return diagram;
    }

    /**
     * @param diagram the diagram to set
     */
    public void setDiagram(DefaultDiagramModel diagram) {
        this.diagram = diagram;
    }

    /**
     * @return the selectedNode
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * @return the rolesList
     */
    public RolesList getRolesList() {
        return rolesList;
    }

    /**
     * @param rolesList the rolesList to set
     */
    public void setRolesList(RolesList rolesList) {
        this.rolesList = rolesList;
    }

    /**
     * @return the service
     */
    public DefaultDiagramModel getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(DefaultDiagramModel service) {
        this.service = service;
    }

    /**
     * @return the dialogsDiagram
     */
    public DefaultDiagramModel getDialogsDiagram() {
        return dialogsDiagram;
    }

    /**
     * @param dialogsDiagram the dialogsDiagram to set
     */
    public void setDialogsDiagram(DefaultDiagramModel dialogsDiagram) {
        this.dialogsDiagram = dialogsDiagram;
    }

    public List<IDialog> getAllDialogsForInteractionTypes(String r,String interactionType){       
       
        return userManagedBean.getAllDialogsForInteractionType(r,interactionType);
    }
    

   
}
