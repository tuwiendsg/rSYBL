package at.ac.tuwien.dsg.sybl.syblProcessingUnit.processing;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitive;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitiveDependency;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ElasticityPrimitivesDescription;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.primitives.ServiceElasticityPrimitives;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.ActionPlanEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.EventNotification;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.IEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.IEvent.Stage;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;
import java.util.AbstractMap;

public class ElasticityCapabilityEnforcement {

    EnforcementAPIInterface enforcementAPI = null;
    ElasticityPrimitivesDescription primitivesDescription = null;
    EventNotification eventNotification = EventNotification.getEventNotification();

    public ElasticityCapabilityEnforcement(EnforcementAPIInterface apiInterface) {
        enforcementAPI = apiInterface;
        try {
            InputProcessing inputProcessing = new InputProcessing();
            primitivesDescription = inputProcessing.loadElasticityPrimitivesDescriptionFromFile();
        } catch (Exception e) {
            SYBLDirectivesEnforcementLogger.logger.error("Failed to load enabled primitives, working with default case");
        }
    }

    public boolean checkECPossible(ElasticityCapability ec) {
        List<String> execTargets = enforcementAPI.getPluginsExecutingActions();
        for (String target : getTargetsOfPrimitives(ec)) {
            if (execTargets.contains(target)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getTargetsOfPrimitives(ElasticityCapability elasticityCapability) {
        List<String> targets = new ArrayList<String>();
        String[] primitives = elasticityCapability
                .getPrimitiveOperations().split(";");
        for (String primitive : primitives) {
            String actionName = primitive;

            if (actionName.contains(".")) {
                targets.add(actionName.split("\\.")[0]);
                actionName = actionName.split("\\.")[1];
            }
        }


        return targets;
    }

    public void enforceActionGivenPrimitives(
            String actionName, Node target, DependencyGraph dependencyGraph, Constraint c, Strategy s) {
        IEvent.Stage stage = null;
        boolean started = false;
        if (!dependencyGraph.isInControlState()) {
            SYBLDirectivesEnforcementLogger.logger.info("Not enforcing action due to breakpoint ");
            return;
        } else {
            SYBLDirectivesEnforcementLogger.logger.info("Starting enforcing action" + actionName + " on target " + target + ".");
        }
        for (ElasticityCapability elasticityCapability : target.getElasticityCapabilities()) {
            if (elasticityCapability.getName().trim().equalsIgnoreCase(actionName.trim()) && checkECPossible(elasticityCapability)) {
                if (!elasticityCapability.getName().toLowerCase().contains("scalein") || (elasticityCapability.getName().toLowerCase().contains("scalein") && target.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).size() > 1)) {
                    ActionPlanEvent actionPlanEvent = new ActionPlanEvent();
                    actionPlanEvent.setServiceId(dependencyGraph.getCloudService().getId());
                    actionPlanEvent.addEffect(new AbstractMap.SimpleEntry<>(elasticityCapability.getName().toLowerCase(), target.getId()));
                    if (c!=null){
                    actionPlanEvent.addConstraint(c);}
                    if (s!=null){
                    actionPlanEvent.addStrategy(s);}
                    actionPlanEvent.setType(IEvent.Type.ELASTICITY_CONTROL);
                    actionPlanEvent.setStage(Stage.START);
                    EventNotification en = EventNotification.getEventNotification();
                    en.sendEvent(actionPlanEvent);
                    started = true;
                    String[] primitives = elasticityCapability
                            .getPrimitiveOperations().split(";");
                    for (int i = 0; i < primitives.length; i++) {

                        if (!enforcePrimitive(primitivesDescription, primitives[i],
                                target, dependencyGraph)) {
                            SYBLDirectivesEnforcementLogger.logger.info("Failed Enforcing " + primitives[i] + ", cancelling the entire elasticity capability ");
                            stage = IEvent.Stage.FAILED;
                            break;
                        } else {

                            SYBLDirectivesEnforcementLogger.logger.info("Successfully enforced " + primitives[i] + ", continuing with capability ");
                        }
                    }
                    break;
                }
            }
        }
        if (started) {
            if (stage == null) {
                stage = IEvent.Stage.FINISHED;
            }
            ActionPlanEvent actionPlanEvent = new ActionPlanEvent();
            actionPlanEvent.setServiceId(dependencyGraph.getCloudService().getId());
          //  actionPlanEvent.addEffect(new AbstractMap.SimpleEntry<String, String>(elasticityCapability.getName().toLowerCase(), target.getId()));
            if (c!=null){
            actionPlanEvent.addConstraint(c);}
            if (s!=null){
            actionPlanEvent.addStrategy(s);}
            actionPlanEvent.setType(IEvent.Type.ELASTICITY_CONTROL);
            actionPlanEvent.setStage(stage);
            EventNotification en = EventNotification.getEventNotification();
            en.sendEvent(actionPlanEvent);
        }
    }

    public Object parseParameter(String param, Node node,
            DependencyGraph dependencyGraph, String IP, String UUID) {
        String result = param;

        if (!param.contains(" ") && !param.contains(".")
                && !param.contains("$")) {
            if (param.equalsIgnoreCase("IP")) {
                return IP;
            }
            if (param.equalsIgnoreCase("UUID")) {
                return UUID;
            }
        } else {

            if (param.contains("{") && param.contains(".")) {

                result = param;
                String REGEX_IP = "(\\{[A-Za-z0-9]+\\}\\.IP)";

                Pattern p = Pattern.compile(REGEX_IP, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(param); // get a matcher object
                int nbFound = m.groupCount();

                int i = 0;
                while (m.find()) {
                    String value = m.group();

                    String nodeId = value.substring(1, value.indexOf('.') - 1);
                    Node searchNode = null;
                    for (Node myNode : dependencyGraph.getAllServiceUnits()) {
                        if (myNode.getId().toLowerCase()
                                .contains(nodeId.toLowerCase())) {
                            searchNode = myNode;
                            break;
                        }
                    }
                    if (searchNode == null) {
                        for (Node myNode : dependencyGraph.getAllServiceUnits()) {
                            if (myNode.getId().toLowerCase()
                                    .contains(nodeId.toLowerCase())) {
                                searchNode = myNode;
                                break;
                            }
                        }
                    }
                    String newRegex = "(\\{" + nodeId + "\\}\\.IP)";
                    Pattern p1 = Pattern.compile(newRegex);
                    Matcher m1 = p1.matcher(result); // get a matcher object
                    if (searchNode.getNodeType() == NodeType.VIRTUAL_MACHINE) {
                        String ip = (String) searchNode.getStaticInformation().get("IP");
                        if (ip == null || ip.equalsIgnoreCase("")) {
                            ip = searchNode.getId();
                        }
                        result = m1.replaceAll(ip);
                    } else {
                        Node artifact = null;
                        Node container = null;
                        Node accessToVM = searchNode;

                        if (searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {
                            artifact = searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);

                            if (artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER) != null && artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).size() > 0) {

                                container = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).get(0);
                            }
                        }
                        if (artifact != null || container != null) {

                            if (container == null) {
                                accessToVM = artifact;
                            } else {
                                accessToVM = container;
                            }
                        }
                        Node search = accessToVM.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
                        String ip = (String) search.getStaticInformation().get("IP");
                        if (ip == null || ip.equalsIgnoreCase("")) {
                            ip = search.getId();
                        }
                        result = m1.replaceAll(ip);
                    }

                    i++;

                }

                param = result;
                REGEX_IP = "(\\{[A-Za-z0-9]+\\}\\.UUID)";

                p = Pattern.compile(REGEX_IP, Pattern.CASE_INSENSITIVE);
                m = p.matcher(param); // get a matcher object
                nbFound = m.groupCount();

                i = 0;
                while (m.find()) {
                    String value = m.group();

                    String nodeId = value.substring(1, value.indexOf('.') - 1);
                    Node searchNode = null;
                    for (Node myNode : dependencyGraph.getAllServiceUnits()) {
                        if (myNode.getId().toLowerCase()
                                .contains(nodeId.toLowerCase())) {
                            searchNode = myNode;
                            break;
                        }
                    }
                    if (searchNode == null) {
                        for (Node myNode : dependencyGraph.getAllServiceUnits()) {
                            if (myNode.getId().toLowerCase()
                                    .contains(nodeId.toLowerCase())) {
                                searchNode = myNode;
                                break;
                            }
                        }
                    }

                    String newRegex = "(\\{" + nodeId + "\\}\\.UUID)";
                    Pattern p1 = Pattern.compile(newRegex);
                    Matcher m1 = p1.matcher(result); // get a matcher object

                    if (searchNode.getNodeType() == NodeType.VIRTUAL_MACHINE) {
                        String ip = (String) searchNode.getStaticInformation().get("UUID");
                        if (ip == null || ip.equalsIgnoreCase("")) {
                            ip = searchNode.getId();
                        }
                        result = m1.replaceAll(ip);
                    } else {
                        Node artifact = null;
                        Node container = null;
                        Node accessToVM = searchNode;

                        if (searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {
                            artifact = searchNode.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);

                            if (artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER) != null && artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).size() > 0) {

                                container = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).get(0);
                            }
                        }
                        if (artifact != null || container != null) {

                            if (container == null) {
                                accessToVM = artifact;
                            } else {
                                accessToVM = container;
                            }
                        }
                        Node search = accessToVM.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
                        String ip = (String) search.getStaticInformation().get("UUID");
                        if (ip == null || ip.equalsIgnoreCase("")) {
                            ip = search.getId();
                        }
                        result = m1.replaceAll(ip);
                    }

                    i++;

                }

                param = result;

            }
            if (param.contains("IP") || param.contains("UUID")) {
                String REGEX = "IP";
                Pattern p = Pattern.compile(REGEX);
                Matcher m = p.matcher(param); // get a matcher object
                param = m.replaceAll(IP);
                REGEX = "UUID";
                p = Pattern.compile(REGEX);
                m = p.matcher(param); // get a matcher object
                return m.replaceAll(UUID);
            }

        }
        return param;

    }

    public boolean enforcePrimitive(
            ElasticityPrimitivesDescription primitivesDescription,
            String primitive, Node node, DependencyGraph dependencyGraph) {
        String target = "";
        String actionName = primitive;
        SYBLDirectivesEnforcementLogger.logger.info("Trying to enforce primitive " + actionName);
        if (actionName.contains(".")) {
            target = actionName.split("\\.")[0];
            actionName = actionName.split("\\.")[1];
        }
        if (target.equalsIgnoreCase("")) {
            for (ElasticityCapability capability : node
                    .getElasticityCapabilities()) {
                if (capability.getName().toLowerCase().contains(actionName)) {
                    if (capability.getName().toLowerCase().contains(".")) {
                        target = capability.getName().split("\\.")[0].toLowerCase();
                    }
                }
            }
        }
        if (target.equalsIgnoreCase("")) {
            switch (actionName.toLowerCase()) {
                case "scaleout":
                    SYBLDirectivesEnforcementLogger.logger.info("Calling Scale out from sybl parser ");

                    return enforcementAPI.scaleout(node);

                case "scalein":
                    SYBLDirectivesEnforcementLogger.logger.info("Calling Scale in from sybl parser ");

                    return enforcementAPI.scalein(node);
                default:
                    return enforcementAPI.enforceAction(actionName, node);
            }
        } else {
            boolean res = true;
            for (ServiceElasticityPrimitives serviceElasticityPrimitives : primitivesDescription
                    .getElasticityPrimitives()) {
                if (serviceElasticityPrimitives.getId()
                        .equalsIgnoreCase(target)) {

                    for (ElasticityPrimitive elasticityPrimitive : serviceElasticityPrimitives
                            .getElasticityPrimitives()) {
                        if (elasticityPrimitive.getId().equalsIgnoreCase(
                                actionName)) {

                            if (elasticityPrimitive.getParameters()
                                    .equalsIgnoreCase("")) {
                                boolean x = enforcementAPI.enforceAction(target,
                                        actionName, node);
                                if (x == false) {
                                    res = false;
                                }
                            } else {
                                List<ElasticityPrimitiveDependency> elasticityPrimitiveDependencies = elasticityPrimitive
                                        .getPrimitiveDependencies();
                                List<ElasticityPrimitiveDependency> beforePrimitives = new ArrayList<ElasticityPrimitiveDependency>();

                                List<ElasticityPrimitiveDependency> afterPrimitives = new ArrayList<ElasticityPrimitiveDependency>();
                                for (ElasticityPrimitiveDependency dependency : elasticityPrimitiveDependencies) {
                                    if (dependency.getDependencyType().equalsIgnoreCase("BEFORE")) {
                                        beforePrimitives.add(dependency);
                                    } else {
                                        afterPrimitives.add(dependency);

                                    }
                                    //TODO:
                                }
                                Object[] parameters = new Object[elasticityPrimitive
                                        .getParameters().split(",").length];
                                Object transferredParameters;
                                String ip = "";
                                String uuid = "";
                                if (node.getNodeType() == NodeType.VIRTUAL_MACHINE) {
                                    ip = (String) node.getStaticInformation()
                                            .get("IP");
                                    if (ip == null || ip.equalsIgnoreCase("")) {
                                        ip = node.getId();
                                    }
                                    uuid = (String) node.getStaticInformation()
                                            .get("UUID");
                                    if (uuid == null || uuid.equalsIgnoreCase("")) {
                                        uuid = node.getId();
                                    }
                                } else {
                                    if (node.getNodeType() == NodeType.SERVICE_UNIT) {
                                        Node artifact = null;
                                        Node container = null;
                                        Node accessToVM = node;

                                        if (node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {
                                            artifact = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);

                                            if (artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER) != null && artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).size() > 0) {

                                                container = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).get(0);
                                            }
                                        }
                                        if (artifact != null || container != null) {

                                            if (container == null) {
                                                accessToVM = artifact;
                                            } else {
                                                accessToVM = container;
                                            }
                                        }
                                        Node newNode = accessToVM.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
                                        ip = (String) newNode.getStaticInformation()
                                                .get("IP");
                                        if (ip == null || ip.equalsIgnoreCase("")) {
                                            ip = newNode.getId();
                                        }
                                        uuid = (String) newNode.getStaticInformation()
                                                .get("UUID");
                                        if (uuid == null || uuid.equalsIgnoreCase("")) {
                                            uuid = newNode.getId();
                                        }
                                    }
                                }
                                int i = 0;
                                for (String parameter : elasticityPrimitive
                                        .getParameters().split(",")) {
                                    parameter.trim();
                                    parameters[i] = parseParameter(parameter,
                                            node, dependencyGraph, ip, uuid);
                                    i += 1;
                                }
                                for (ElasticityPrimitiveDependency elasticityPrimitiveDependency : beforePrimitives) {
                                    // check before primitives
                                    boolean x = enforcePrimitive(primitivesDescription,
                                            elasticityPrimitiveDependency
                                            .getPrimitiveID(), node,
                                            dependencyGraph);
                                    if (!x) {
                                        res = false;
                                    }

                                }

                                String methodName = elasticityPrimitive
                                        .getMethodName();

                                if (methodName.equalsIgnoreCase("")) {
                                    if (!target.equalsIgnoreCase("")) {
                                        boolean x = enforcementAPI.enforceAction(target,
                                                actionName, node, parameters);
                                        if (!x) {
                                            res = false;
                                        }
                                    } else {
                                        boolean x = enforcementAPI.enforceAction(
                                                actionName, node, parameters);
                                        if (!x) {
                                            res = false;
                                        }
                                    }
                                } else {
                                    if (!target.equalsIgnoreCase("")) {
                                        boolean x = enforcementAPI.enforceAction(target,
                                                methodName, node, parameters);
                                        if (!x) {
                                            res = false;
                                        }
                                    } else {
                                        boolean x = enforcementAPI.enforceAction(
                                                methodName, node, parameters);
                                        if (!x) {
                                            res = false;
                                        }
                                    }
                                }
                                for (ElasticityPrimitiveDependency elasticityPrimitiveDependency : afterPrimitives) {
                                    // check before primitives
                                    boolean x = enforcePrimitive(primitivesDescription,
                                            elasticityPrimitiveDependency
                                            .getPrimitiveID(), node,
                                            dependencyGraph);
                                    if (!x) {
                                        res = false;
                                    }
                                }

                            }
                            break;
                        }
                    }
                    break;
                }

            }
            //SYBLDirectivesEnforcementLogger.logger.info("Enforcing primitive success="+res);
            return res;
//			switch (actionName) {
//			case "scaleout":
//				SYBLDirectivesEnforcementLogger.logger.info("Calling Scale out from planning ");
//
//				enforcementAPI.scaleout(target, node);
//				break;
//			case "scalein":
//				SYBLDirectivesEnforcementLogger.logger.info("Calling Scale in from planning ");
//
//				enforcementAPI.scalein(target, node);
//				break;
//			default:
//				if (target.equalsIgnoreCase("")) {
//					enforcementAPI.enforceAction(target, actionName, node);
//				}
//
//				break;
//			}
        }
    }
}
