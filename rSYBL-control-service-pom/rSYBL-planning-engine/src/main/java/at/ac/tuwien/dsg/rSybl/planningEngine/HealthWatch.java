/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.planningEngine;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapabilityInformation;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPI;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.MultipleEnforcementAPIs;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class HealthWatch {

    private MonitoringAPIInterface monitoringAPI;
    private EnforcementAPIInterface enforcementAPI;
    private DependencyGraph dependencyGraph;
    private ActionPlanEnforcement actionPlanEnforcement;
    private boolean ongoing = false;

    public HealthWatch(MonitoringAPIInterface aPIInterface, EnforcementAPIInterface enforcementAPIInterface, DependencyGraph dependencyGraph1) {
        this.monitoringAPI = aPIInterface;
        this.enforcementAPI = enforcementAPIInterface;
        this.dependencyGraph = dependencyGraph1;
        actionPlanEnforcement = new ActionPlanEnforcement(enforcementAPI);
    }
    
    public void restartNode(Node nodeToRestart, Node servicePartToFix) {

        List<ElasticityCapabilityInformation> caps = nodeToRestart.getElasticityCapabilitiesByKeyword("restart");
        for (ElasticityCapabilityInformation capability : caps) {
            if (!monitoringAPI.checkHealthy(servicePartToFix)) {
                actionPlanEnforcement.enforceElasticityCapabilityFromNode(capability, nodeToRestart, dependencyGraph);
            }
        }
    }

    public void triggerHealthFix(String servicePartID) {
        //Currently we try health fix through reboot - however we should provide mechanisms to specify different ways of fixing probable errors
        //we go top down, and try rebooting until fixed
        if (ongoing!=true){
        ongoing = true;
       
        Node servicePartToFix = dependencyGraph.getNodeWithID(servicePartID);
        if (servicePartToFix.getNodeType().equals(Node.NodeType.SERVICE_UNIT)) {
            List<Node> artifacts = servicePartToFix.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.ARTIFACT);
            if (artifacts != null) {
                for (Node a : artifacts) {
                    restartNode(a, servicePartToFix);
                }
                if (!monitoringAPI.checkHealthy(servicePartToFix)) {
                    for (Node a : artifacts) {
                        List<Node> containers = a.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.CONTAINER);
                        if (containers != null) {
                            for (Node c : containers) {
                                restartNode(c, servicePartToFix);
                            }
                            if (!monitoringAPI.checkHealthy(servicePartToFix)) {
                                for (Node c : containers) {
                                    List<Node> vms = c.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.VIRTUAL_MACHINE);
                                    for (Node vm : vms) {
                                        restartNode(vm, servicePartToFix);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                List<Node> vms = servicePartToFix.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.VIRTUAL_MACHINE);
                if (vms != null) {
                    for (Node a : vms) {
                        restartNode(a, servicePartToFix);
                    }
                }
            }
        }else{
            if (servicePartToFix.getNodeType().equals(Node.NodeType.VIRTUAL_MACHINE)){
                 restartNode(servicePartToFix, servicePartToFix);
            }
        }
        ongoing = false;
        }else{
            return;
        }
    }
}
