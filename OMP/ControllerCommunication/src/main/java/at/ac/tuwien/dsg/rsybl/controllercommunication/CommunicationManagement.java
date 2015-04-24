/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.controllercommunication;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.ActionPlanEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.CustomEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.IEvent;
import at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing.AccessOrganizationInfo;
import at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing.InitiateInteractions;
import at.ac.tuwien.dsg.rsybl.controllercommunication.interactionProcessing.InteractionProcessing;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Interaction;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Message;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.communicationModel.Role;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IMessage;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IResponsibility;
import at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.interfaces.IRole;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Georgiana
 */
public class CommunicationManagement implements Runnable{

    private InitiateInteractions initiateInteractions;
    private final int  THRESHOLD=5;
    private List<IResponsibility> responsibilities;
    private List<IRole> roles;
    private List<String> metrics;
    private List<String> metricsPatterns;
    private AccessOrganizationInfo accessOrganizationInfo;
    private HashMap<IRole, List<Interaction>> queuedInteractions = new HashMap<IRole, List<Interaction>>();
    private HashMap<String, List<IRole>> metricsToRoles = new HashMap<String, List<IRole>>();
    private HashMap<String, List<IRole>> metricPatternsToRoles = new HashMap<String, List<IRole>>();
    private Thread thisThread ;
    public CommunicationManagement() {
        QueueListenerrSYBL queueListenerSYBL = new QueueListenerrSYBL(this);
        initiateInteractions = new InitiateInteractions(this);
        initiateInteractions.startListeningToMessages();
        accessOrganizationInfo = new AccessOrganizationInfo();
        thisThread = new Thread(this);
        init();

    }
    public void run(){
        while (true){
            processInteractions();
            try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    private void processInteractions(){
        for (IRole iRole:queuedInteractions.keySet()){
            if (THRESHOLD*Math.log10(iRole.getAuthority())<=queuedInteractions.get(iRole).size()){
                for (Interaction interaction:queuedInteractions.get(iRole)){
                    initiateInteractions.initiateInteraction(interaction);
                }
                List<Interaction> interactions = new ArrayList<Interaction>();
                queuedInteractions.put(iRole, interactions);
            }
        }
    }
    private void init() {
        responsibilities = accessOrganizationInfo.getAllResponsibilities();
        roles = accessOrganizationInfo.getAllRoles();
        for (IRole role : roles) {
            for (IResponsibility iResponsibility : role.getResponsabilities()) {
                for (String metric : iResponsibility.getAssociatedMetrics()) {
                    if (!metrics.contains(metric)) {
                        metrics.add(metric);
                        List<IRole> tempRole = new ArrayList<IRole>();
                        tempRole.add(role);
                        metricsToRoles.put(metric, tempRole);
                    } else {
                        metricsToRoles.get(metric).add(role);
                    }
                }
                for (String metric : iResponsibility.getAssociatedMetricPatterns()) {
                    if (!metricsPatterns.contains(metric)) {
                        metricsPatterns.add(metric);
                        List<IRole> tempRole = new ArrayList<IRole>();
                        tempRole.add(role);
                        metricPatternsToRoles.put(metric, tempRole);
                    } else {
                        metricPatternsToRoles.get(metric).add(role);
                    }
                }
            }
        }
        thisThread.start();
    }

    private Message constructMessageFromActionPlan(ActionPlanEvent event, List<String> metrics) {
        Message message = new Message();
        String enforcement = "";
        int i = 0;
        for (Entry<String, String> e : event.getEffect()) {
            if (i != event.getEffect().size() - 1) {
                enforcement += e.getKey() + "." + e.getValue() + ", ";
            } else {
                enforcement += e.getKey() + "." + e.getValue();
            }
        }
        message.setActionEnforced(enforcement);
        String cause = "Constraints and respectively strategies violated: (i)";
        for (Constraint c : event.getConstraints()) {
            cause += SYBLDirectiveMappingFromXML.mapXMLConstraintToSYBLAnnotation(c);
        }
        cause += " (ii) ";
        for (Strategy s : event.getStrategies()) {
            cause += SYBLDirectiveMappingFromXML.mapFromXMLStrategyToSYBLAnnotation(s);
        }

        cause += ". You received this notification from EC due to the following metrics which are affected: ";
        for (String m : metrics) {
            if (i != event.getEffect().size() - 1) {
                enforcement += m + ", ";
            } else {
                enforcement += m;
            }
        }
        cause += ".";
        message.setCloudServiceId(event.getServiceId());
        message.setCause(cause);
        message.setTargetPartId(event.getServiceId());
        message.setId(UUID.randomUUID().toString());
        message.setMessageType(IMessage.MessageType.NOTIFICATION);
        return message;
    }

    private List<String> getMetricsFromConstraints(List<Constraint> constraints) {
        List<String> result = new ArrayList<String>();
        for (Constraint c : constraints) {
            for (BinaryRestrictionsConjunction binaryRestrictionConjunction : c.getToEnforce().getBinaryRestriction()) {
                for (BinaryRestriction binaryRestriction : binaryRestrictionConjunction.getBinaryRestrictions()) {
                    if (binaryRestriction.getLeftHandSide().getMetric() != null && !binaryRestriction.getLeftHandSide().getMetric().equalsIgnoreCase("")) {
                        if (!result.contains(binaryRestriction.getLeftHandSide().getMetric())) {
                            result.add(binaryRestriction.getLeftHandSide().getMetric());
                        }
                    }
                    if (binaryRestriction.getRightHandSide().getMetric() != null && !binaryRestriction.getRightHandSide().getMetric().equalsIgnoreCase("")) {
                        if (!result.contains(binaryRestriction.getRightHandSide().getMetric())) {
                            result.add(binaryRestriction.getRightHandSide().getMetric());
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<String> getMetricsFromStrategies(List<Strategy> strategies) {
        List<String> result = new ArrayList<String>();
        for (Strategy s : strategies) {
            for (BinaryRestrictionsConjunction binaryRestrictionConjunction : s.getCondition().getBinaryRestriction()) {
                for (BinaryRestriction binaryRestriction : binaryRestrictionConjunction.getBinaryRestrictions()) {
                    if (binaryRestriction.getLeftHandSide().getMetric() != null && !binaryRestriction.getLeftHandSide().getMetric().equalsIgnoreCase("")) {
                        if (!result.contains(binaryRestriction.getLeftHandSide().getMetric())) {
                            result.add(binaryRestriction.getLeftHandSide().getMetric());
                        }
                    }
                    if (binaryRestriction.getRightHandSide().getMetric() != null && !binaryRestriction.getRightHandSide().getMetric().equalsIgnoreCase("")) {
                        if (!result.contains(binaryRestriction.getRightHandSide().getMetric())) {
                            result.add(binaryRestriction.getRightHandSide().getMetric());
                        }
                    }
                }
            }
            if (s.getToEnforce().getParameter() != null && !s.getToEnforce().getParameter().equalsIgnoreCase("")) {
                result.add(s.getToEnforce().getParameter());
            }
        }
        return result;
    }

    public void processActionPlanEvent(ActionPlanEvent event) {
        List<String> metricsConstraints = getMetricsFromConstraints(event.getConstraints());
        List<String> metricsStrategies = getMetricsFromStrategies(event.getStrategies());
        List<String> currentMetrics = new ArrayList<String>();
        HashMap<String, String> metricPatternsEncountered = new HashMap<String, String>();
        for (String s : metricsConstraints) {
            if (!currentMetrics.contains(s) && (this.metrics.contains(s))) {
                currentMetrics.add(s);
            } else {
                if (!currentMetrics.contains(s)) {
                    for (String p : this.metricsPatterns) {
                        if (Pattern.compile(p).matcher(s).find()) {
                            metricPatternsEncountered.put(s, p);
                        }
                    }
                }
            }
        }
        for (String s : metricsStrategies) {
            if (!currentMetrics.contains(s) && (this.metrics.contains(s))) {
                currentMetrics.add(s);
            } else {
                if (!currentMetrics.contains(s)) {
                    for (String p : this.metricsPatterns) {
                        if (Pattern.compile(p).matcher(s).find()) {
                            metricPatternsEncountered.put(s, p);
                        }
                    }
                }
            }
        }
        HashMap<IRole, List<String>> currentRoles = new HashMap<IRole, List<String>>();
        for (String m : currentMetrics) {
            List<IRole> myRoles = this.metricsToRoles.get(m);
            myRoles.addAll(metricPatternsToRoles.get(m));
            for (IRole r : myRoles) {
                if (!currentRoles.containsKey(r)) {
                    ArrayList<String> ms = new ArrayList<String>();
                    currentRoles.put(r, ms);
                }
                currentRoles.get(r).add(m);
            }

        }
        Role role = new Role();
        role.setRoleName("EC");

        for (IRole r : currentRoles.keySet()) {
            Interaction interaction = new Interaction();
            interaction.setDialogId(UUID.randomUUID().toString());
            interaction.setInitiationDate(new Date());
            interaction.setInitiator(role);
            interaction.setReceiver(r);
            interaction.setMessage(constructMessageFromActionPlan(event, currentRoles.get(r)));
            interaction.setId(UUID.randomUUID().toString());
            if (!this.queuedInteractions.containsKey(r)) {
                List<Interaction> interactions = new ArrayList<Interaction>();
                queuedInteractions.put(r, interactions);
            }
            queuedInteractions.get(r).add(interaction);
        }

    }

    public void processCustomEvent(CustomEvent event) {
        Message message = new Message();
        if (event.getType() == IEvent.Type.ERROR) {
            message.setMessageType(IMessage.MessageType.EMERGENCY);
        }
        if (event.getType() == IEvent.Type.UNHEALTHY_SP) {
            message.setMessageType(IMessage.MessageType.WARNING);
        }
        message.setCloudServiceId(event.getCloudServiceID());
        message.setTargetPartId(event.getTarget());
        message.setDescription(event.getMessage());
        Role role = new Role();
        role.setRoleName("EC");
        for (IRole r : this.metricsToRoles.get("error")) {
            Interaction interaction = new Interaction();
            interaction.setDialogId(UUID.randomUUID().toString());
            interaction.setInitiationDate(new Date());
            interaction.setInitiator(role);
            interaction.setReceiver(r);
            interaction.setMessage(message);
            interaction.setId(UUID.randomUUID().toString());
            if (!this.queuedInteractions.containsKey(r)) {
                List<Interaction> interactions = new ArrayList<Interaction>();
                queuedInteractions.put(r, interactions);
            }
                        queuedInteractions.get(r).add(interaction);

        }

    }

    /**
     * @return the initiateInteractions
     */
    public InitiateInteractions getInitiateInteractions() {
        return initiateInteractions;
    }

    /**
     * @param initiateInteractions the initiateInteractions to set
     */
    public void setInitiateInteractions(InitiateInteractions initiateInteractions) {
        this.initiateInteractions = initiateInteractions;
    }
}
