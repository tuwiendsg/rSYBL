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
import org.primefaces.model.diagram.connector.StraightConnector;
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
    
    private PanelGrid panelGridServices; //bound to the view
    @ManagedProperty("#{roles}")
    private RolesList rolesList;
    
    
    private TreeNode root;
    private TreeNode selectedNode;
    private Document selectedDocument;
     
    
    public TreeNode getTreeNode(String serviceID){
        CloudServiceXML cloudServiceXML = userManagedBean.getDescription(serviceID);
        if (cloudServiceXML!=null){
        HashMap<String,TreeNode> serviceTopologies = new HashMap<String,TreeNode>();
        TreeNode serviceDescriptionRoot = new DefaultTreeNode("Root", null);

        for (ServiceTopologyXML serviceTopologyXML:cloudServiceXML.getServiceTopologies()){
        TreeNode node0 = new DefaultTreeNode(serviceTopologyXML.getId(), serviceDescriptionRoot);
        serviceTopologies.put(serviceTopologyXML.getId(),node0);
        for (ServiceUnitXML serviceUnitXML:serviceTopologyXML.getServiceUnits()){
            TreeNode nodex = new DefaultTreeNode(serviceUnitXML.getId(),node0);
                   
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

    public List<RolesList.RoleDescription> getRoleNames() {
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

                Element el = new Element(role.getRoleName()+" ["+role.getAuthority()+"]", size / allRoles.size() * i + "em", 20 + "em");
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
       
    }
    private List<Tab> tabs = new ArrayList<Tab>();

    /**
     * @return the tabs
     */
    public List<Tab> getTabs() {

        return tabs;
    }

    /**
     * @param tabs the tabs to set
     */
    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
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

  

}
