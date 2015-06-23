package at.ac.tuwien.dsg.sybl.syblProcessingUnit.processing;

/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184.  *
 * This work was partially supported by the European Commission in terms of the
 * CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.GovernanceScope;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.CustomEvent;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.EventNotification;
import at.ac.tuwien.dsg.csdg.outputProcessing.eventsNotification.IEvent;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.exceptions.ConstraintViolationException;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.exceptions.MeasurementNotAvailableException;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.exceptions.MethodNotFoundException;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription.SYBLDescriptionParser;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.EnvironmentVariable;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.Rule;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.SYBLDirectivesEnforcementLogger;

public class Utils {

    private ArrayList<MonitoringThread> monitoringThreads = new ArrayList<MonitoringThread>();

    private HashMap<EnvironmentVariable, Comparable> monitoredVariables = new HashMap<EnvironmentVariable, Comparable>();
    public static HashMap<String, Boolean> cons = new HashMap<String, Boolean>();
    private MonitoringAPIInterface monitoringAPI;
    private EnforcementAPIInterface enforcementAPI;
    private ArrayList<Rule> disabledRules = new ArrayList<Rule>();
    private String monitoring = "";
    private String constraints = "";
    private String strategies = "";
    private String priorities = "";
    private String notifications = "";
    private String governanceScopes = "";
    private Node currentEntity;
    private DependencyGraph dependencyGraph;
    private HashMap<String,GovernanceScope> governanceScopesExecuted = new HashMap<String,GovernanceScope>();

    public Utils(Node currentEntity, String notifications, String priorities, String monitoring, String constraints, String strategies,String governanceScopes, MonitoringAPIInterface monitoringAPI, EnforcementAPIInterface enforcementAPI, DependencyGraph dependencyGraph) {
        this.currentEntity = currentEntity;
        this.priorities = priorities;
        this.notifications = notifications;
        this.constraints = constraints;
        this.strategies = strategies;
        this.monitoring = monitoring;
        this.monitoringAPI = monitoringAPI;
        this.enforcementAPI = enforcementAPI;
        this.governanceScopes = governanceScopes;
        this.dependencyGraph = dependencyGraph;
    }
    private boolean enforcingAction = false;

    public void clearDisabledRules() {
        disabledRules.clear();

    }

    public void processSyblSpecifications() {
        if (!monitoring.equalsIgnoreCase("")) {
            //SYBLDirectivesEnforcementLogger.logger.info("=============================================");
            SYBLDirectivesEnforcementLogger.logger.info("Monitoring requirements are: " + monitoring);

            processMonitoring(monitoring);
        }
        if (notifications != null && !notifications.equalsIgnoreCase("")) {
            //SYBLDirectivesEnforcementLogger.logger.info("=============================================");
            SYBLDirectivesEnforcementLogger.logger.info("Notification requirements are: " + notifications);

            processNotifications(notifications);
        }

        try {
            if (!constraints.equalsIgnoreCase("")) {
                //	SYBLDirectivesEnforcementLogger.logger.info("=============================================");
                SYBLDirectivesEnforcementLogger.logger.info("Constraints are: " + constraints);

                processConstraints(constraints);
            }

        } catch (Exception e) {
            SYBLDirectivesEnforcementLogger.logger.error("Utils,Processing constraints" + e.toString());
        }

        if (!strategies.equals("")) {
            //SYBLDirectivesEnforcementLogger.logger.info("=============================================");
            SYBLDirectivesEnforcementLogger.logger.info("Strategies are: " + strategies);
            processStrategies(strategies);
        }
        if (!priorities.equalsIgnoreCase("")) {
            //SYBLDirectivesEnforcementLogger.logger.info("=============================================");
            SYBLDirectivesEnforcementLogger.logger.info("Priorities set by the user are: " + priorities);
            ArrayList<Rule> rules = new ArrayList<Rule>();
            if (!monitoring.equals("")) {
                for (String m : monitoring.split(";")) {
                    String[] s = m.split(":");
                    Rule r = new Rule();
                    r.setName(eliminateSpaces(s[0]));
                    r.setText(s[1]);
                    rules.add(r);
                }
            }
            if (!constraints.equalsIgnoreCase("")) {
                for (String m : constraints.split(";")) {
                    String[] s = m.split(":");
                    Rule r = new Rule();
                    r.setName(eliminateSpaces(s[0]));
                    r.setText(s[1]);
                    rules.add(r);
                }
            }
            if (!strategies.equals("")) {
                for (String m : strategies.split(";")) {
                    String[] s = m.split(":");
                    Rule r = new Rule();
                    r.setName(eliminateSpaces(s[0]));
                    r.setText(s[1]);
                    rules.add(r);
                }
            }

            processPriorities(priorities, rules);

        }

    }
    
// ==========================processing code========================================//
    public void processPriorities(String priorities, ArrayList<Rule> rules) {
        String[] s = priorities.split(";");
        for (String c : s) {
            String[] x = c.split(":");
            Rule r = new Rule();
            if (x.length > 1) {
                r.setName(x[0]);
                r.setText(x[1]);
            } else {
                r.setText(x[0]);
            }
            String smallerRule = "";
            String greaterImpRule = "";
            x = r.getText().split("<");
            if (x.length > 1) {
                smallerRule = eliminateSpaces(x[0].split("[\\(]")[1].split("[\\)]")[0]);
                greaterImpRule = eliminateSpaces(x[1].split("[\\(]")[1].split("[\\)]")[0]);
                SYBLDirectivesEnforcementLogger.logger.info("Priority  " + smallerRule + " is smaller than of " + greaterImpRule);

            } else {
                x = r.getText().split(">");
                if (x.length > 1) {
                    smallerRule = eliminateSpaces(x[1].split("[\\(]")[1].split("[\\)]")[0]);
                    greaterImpRule = eliminateSpaces(x[0].split("[\\(]")[1].split("[\\)]")[0]);
                    SYBLDirectivesEnforcementLogger.logger.info("Priority " + smallerRule + " is smaller than of " + greaterImpRule);

                }
            }
            boolean disableLessImpRule = false;
            for (Rule rule : rules) {
                if (rule.getName().equalsIgnoreCase(greaterImpRule)) {
                    String ruleText = rule.getText();
                    if (ruleText.contains(" WHEN ")) {
                        String cond = ruleText.split("WHEN ")[1];
                        try {
                            if (evaluateCondition(cond)) {
                                //SYBLDirectivesEnforcementLogger.logger.info("Evaluating condition "+cond+" of the higher importance rule");
                                disableLessImpRule = true;
                            }
                        } catch (Exception e) {
						//SYBLDirectivesEnforcementLogger.logger.error("In evaluating condition "+e.toString());

						// TODO Auto-generated catch block
                            //	e.printStackTrace();
                        }
                    } else {
                        if (ruleText.contains("CASE")) {
                            String cond = ruleText.split("CASE ")[1].split(":")[0];
                            try {
                                if (evaluateCondition(cond)) {
                                    //SYBLDirectivesEnforcementLogger.logger.info("Evaluating condition "+cond+" of the higher importance rule");
                                    disableLessImpRule = true;
                                }
                            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (MeasurementNotAvailableException e) {
                                SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());
                            }

                        } else {
                            disableLessImpRule = true;
                        }
                    }
                    break;
                }
            }
            if (disableLessImpRule) {
                for (Rule rule : rules) {
                    if (rule.getName().equalsIgnoreCase(smallerRule)) {
                        disabledRules.add(rule);
                    }
                }
            }
        }
        for (Rule r : disabledRules) {
            SYBLDirectivesEnforcementLogger.logger.info("Disabled rule " + r.getName());
        }

    }
    public void processGovernanceScopes(){   
        if (governanceScopes!=null && !governanceScopes.equalsIgnoreCase("")){
        String [] govScopes = this.governanceScopes.split(";");
        for (String governanceScope: govScopes){
             GovernanceScope gov = new GovernanceScope();
        gov.setId(governanceScope.split(":")[0]);
        String governanceRule = governanceScope.split("GOVERNANCE_SCOPE ")[1];
        if (governanceRule.contains("QUERY")){
            if (governanceRule.contains("CONSIDERING_UNCERTAINTY")){
                String[] res = governanceRule.split("QUERY")[1].split(" CONSIDERING_UNCERTAINTY");
                gov.setQuery(res[0]);
                gov.setConsideringUncertainty(res[1]);
            }
            
        }
        governanceScopesExecuted.put(gov.getId(), gov);
        }
        }
    }
    public void processConstraints(String constraints)
            throws MethodNotFoundException {
        String[] s = constraints.split(";");
        for (String c : s) {
            if (!eliminateSpaces(c).equalsIgnoreCase("")) {
                String[] x = c.split(":");

                Rule r = new Rule();

		//SYBLDirectivesEnforcementLogger.logger.info("Constraint " + x[0] + " has the following body: " + x[1]);
                r.setName(eliminateSpaces(x[0]));
                r.setText(x[1]);
                if (!disabledRules.contains(r)) {
                    if (x[1].contains("WHEN ")) {
                        try {
                            try {
                                processComplexConstraint(r);
                            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } catch (ConstraintViolationException e) {
                            // TODO Auto-generated catch block
                            SYBLDirectivesEnforcementLogger.logger.info(e.getMessage());
                        }
                    } else if (x[1].contains("AND ") || x[1].contains("OR ")) {
                        processCompositeConstraint(r);
                    } else {
                        try {
                            try {
                                processSimpleConstraint(r);
                            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } catch (ConstraintViolationException e) {
                            // TODO Auto-generated catch block
                            SYBLDirectivesEnforcementLogger.logger.info(e.getMessage());
                        }
                    }

                } else {
                    this.cons.put(eliminateSpaces(x[0]), false);
                    SYBLDirectivesEnforcementLogger.logger.info(x[0] + " is not evaluated because other constraint of higher importance overrides it");
                }
            }
        }
//	for ( String current:cons.keySet()){
//		SYBLDirectivesEnforcementLogger.logger.info("The constraint  "+current+" "+cons.get(current));
//
//	}
    }

    public void processNotifications(String notifications) {
        String[] st = notifications.split(";");
        for (String c : st) {
            String[] x = c.split(":");

            Rule r = new Rule();
            r.setName(x[0]);

            r.setText(c.substring(c.indexOf(":") + 1));

            if (r.getText().contains("WHEN")) {
                String role = "";
                int in = 0;
                for (String sx : st) {
                    if (sx.equalsIgnoreCase("notification")) {
                        int ii = in + 1;
                        while (!st[ii].equalsIgnoreCase("when") && ii < st.length) {
                            role += st[ii];
                            ii++;
                        }

                    }
                    in++;
                }

                String s[] = r.getText().split(":");
                //SYBLDirectivesEnforcementLogger.logger.info(r.getText());
                String condition = s[0].split("WHEN ")[1];
                try {
                    try {
                        if ((condition.contains("AND") && evaluateCompositeCondition(condition)) || (!condition.contains("AND") && evaluateCondition(condition))) {
                            doNotification(s[1], role);
                        } else {
                            SYBLDirectivesEnforcementLogger.logger.info("Condition not true for strategy " + r.getName());
                        }
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (MeasurementNotAvailableException e) {
                        SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());
                    }
                } catch (MethodNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
            	if (r.getText().contains("NOTIFY")){
                String s[] = r.getText().split("NOTIFY ");
                String[] actions = s[0].split(";");
                
                for (String action : actions) {
                    doEnforcementWithPrimitives(action, r.getName(), SYBLDirectiveMappingFromXML.mapFromSYBLAnnotationToXMLStrategy(r.getName() + ":" + r.getText()));
                }
            	}
            }

        }
    }

    public void doNotification(String role, String message) {
        EventNotification eventNotification = EventNotification.getEventNotification();
        CustomEvent customEvent = new CustomEvent();
        customEvent.setCloudServiceID(this.dependencyGraph.getCloudService().getId());
        customEvent.setType(IEvent.Type.NOTIFICATION);
        customEvent.setTarget(role);
        customEvent.setMessage(message);
        eventNotification.sendEvent(customEvent);
    }

    public void processMonitoring(String monitoring) {
        String[] s = monitoring.split(";");
        for (String c : s) {
            String[] x = c.split(":");
            //SYBLDirectivesEnforcementLogger.logger.info("Monitoring requirement " + x[0] + " has the following body: " + x[1]);
            Rule r = new Rule();
            r.setName(x[0]);
            r.setText(x[1]);
            if (x[1].contains("WHEN ")) {
                try {
                    try {
                        processComplexMonitoringRule(r);
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (MethodNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                processMonitoringRule(r);
            }
        }

    }

    public void processStrategies(String strategies) {
//SYBLDirectivesEnforcementLogger.logger.info("Processing strategies : " +strategies);
        String[] s = strategies.split(";");
        for (String c : s) {
            String[] x = c.split(":");

            Rule r = new Rule();
            r.setName(x[0]);

            r.setText(c.substring(c.indexOf(":") + 1));
            //SYBLDirectivesEnforcementLogger.logger.info("Strategy requirement " + x[0] + " has the following body: " + r.getText());
            if (r.getText().contains("WHERE ")) {
                processComplexStrategy(r);
            } else {
                processStrategy(r);
            }
        }

    }

    public void doEnforcementWithPrimitives(String enf, String strategyName, Strategy s) {

        if (!enf.contains("minimize") && !enf.contains("maximize")) {
            if (enf.contains("(")) {
                String actionName = enf.split("[(]")[0];

                String parameter = eliminateSpaces(enf.split("[(]")[1].split("[)]")[0]);
                Node entity = dependencyGraph.getNodeWithID(parameter);
                ElasticityCapabilityEnforcement capabilityEnforcement = new ElasticityCapabilityEnforcement(enforcementAPI);
                capabilityEnforcement.enforceActionGivenPrimitives(actionName, entity, dependencyGraph, null, s);
            } else {
                String actionName = eliminateSpaces(enf);

                ElasticityCapabilityEnforcement capabilityEnforcement = new ElasticityCapabilityEnforcement(enforcementAPI);
                capabilityEnforcement.enforceActionGivenPrimitives(actionName, currentEntity, dependencyGraph, null, s);

            }
        }
    }
   public void doEnforcementWithPrimitives(String enf, String strategyName, Strategy s,GovernanceScope scope,String uncertainty) {

        if (!enf.contains("minimize") && !enf.contains("maximize")) {
            if (enf.contains("(")) {
                String [] enfs = enf.split(",");
                for (String enf1:enfs){
                String actionName = enf1.split("[(]")[0];
                
                String parameter = eliminateSpaces(enf1.split("[(]")[1].split("[)]")[0]);
                
                Node entity = dependencyGraph.getNodeWithID(parameter);
                ElasticityCapabilityEnforcement capabilityEnforcement = new ElasticityCapabilityEnforcement(enforcementAPI);
                if (entity==null){
                    
                   capabilityEnforcement.enforceActionGivenPrimitives(actionName, currentEntity, dependencyGraph, null, s,parameter,scope,uncertainty);

                }else{
                
                capabilityEnforcement.enforceActionGivenPrimitives(actionName, entity, dependencyGraph, null, s,"",scope,uncertainty);
                }
                }
            } else {
                String actionName = eliminateSpaces(enf);

                ElasticityCapabilityEnforcement capabilityEnforcement = new ElasticityCapabilityEnforcement(enforcementAPI);
                capabilityEnforcement.enforceActionGivenPrimitives(actionName, currentEntity, dependencyGraph, null, s,"",scope,uncertainty);

            }
        }
    }
    public void processStrategy(Rule r) {
        
        if (r.getText().contains("CASE")) {
            String s[] = r.getText().split(":");
            //SYBLDirectivesEnforcementLogger.logger.info(r.getText());
            String condition = s[0].split("CASE ")[1];
            try {
                try {
                    if ((condition.contains("AND") && evaluateCompositeCondition(condition)) || (!condition.contains("AND") && evaluateCondition(condition))) {
                        
                    	if (r.getText().contains("FOR ") && r.getText().contains("CONSIDERING_UNCERTAINTY")){
                            String [] afterFor = r.getText().split("FOR ")[1].split(" CONSIDERING_UNCERTAINTY");
                            
                    		doEnforcementWithPrimitives(s[1].split(" FOR")[0],r.getName(),SYBLDirectiveMappingFromXML.mapFromSYBLAnnotationToXMLStrategy(r.getName() + ":" + r.getText()),governanceScopesExecuted.get(afterFor[0]), afterFor[1]);
           
                        }
                    	else{
                    		if (r.getText().contains("FOR")){
                    			doEnforcementWithPrimitives(s[1].split(" FOR")[0],r.getName(),SYBLDirectiveMappingFromXML.mapFromSYBLAnnotationToXMLStrategy(r.getName() + ":" + r.getText()),governanceScopesExecuted.get(r.getText().split("FOR ")[1].split(";")[0]),null);
                    	           
                    		}else{
                    		if (r.getText().contains("CONSIDERING_UNCERTAINTY")){
                    			doEnforcementWithPrimitives(s[1].split(" CONSIDERING_UNCERTAINTY")[0],r.getName(),SYBLDirectiveMappingFromXML.mapFromSYBLAnnotationToXMLStrategy(r.getName() + ":" + r.getText()),null,r.getText().split("CONSIDERING_UNCERTAINTY ")[1].split(";")[0]);
                    	           
                    		}else{
                    		doEnforcementWithPrimitives(s[1], r.getName(), SYBLDirectiveMappingFromXML.mapFromSYBLAnnotationToXMLStrategy(r.getName() + ":" + r.getText()));
                                }
                                }
                                }
                        } else {
                        SYBLDirectivesEnforcementLogger.logger.info("Condition not true for strategy " + r.getName());
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MeasurementNotAvailableException e) {
                    SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());
                }
            } catch (MethodNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String s[] = r.getText().split("STRATEGY ");
            String[] actions = s[0].split(";");

            for (String action : actions) {
                doEnforcementWithPrimitives(action, r.getName(), SYBLDirectiveMappingFromXML.mapFromSYBLAnnotationToXMLStrategy(r.getName() + ":" + r.getText()));
            }

        }

    }

    public void processComplexStrategy(Rule r) {
        SYBLDirectivesEnforcementLogger.logger.error("!!!!!!!!!!!!!!!Not implemented for processing complex strategies");
    }

    /**
     * ************************ Monitoring processing ************************
     */
    public void processMonitoringRule(Rule r) {

        if (r.getText().contains("TIMESTAMP ")) {
            String[] s = r.getText().split("TIMESTAMP ");
            float timestamp = Float.parseFloat(s[1].split(" ")[0]);

            MonitoringThread t = new MonitoringThread(this, monitoredVariables, s[0], (long) timestamp);
            if (!monitoringThreads.contains(t)) {
                t.start();
                monitoringThreads.add(t);
            }
        } else {
            try {
                processSimpleMonitoringRule(r.getText());
            } catch (MethodNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void processSimpleMonitoringRule(String monitoring)
            throws MethodNotFoundException {
        String[] s = monitoring.split(" ");
        String monitoredConcept = "";
        String variableName = "";
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("=")) {
                monitoredConcept = s[i + 1];
                variableName = s[i - 1];
                break;
            }
        }

        SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();
        String methodName = descriptionParser.getMethod(monitoredConcept);
        if (!methodName.equals("")) {

            Method method = null;
            try {
                Class partypes[] = new Class[1];

                Object[] parameters = new Object[1];
                parameters[0] = currentEntity;
                partypes[0] = Node.class;

                method = MonitoringAPIInterface.class.getMethod(methodName, partypes);
                Class variableType = method.getReturnType();
                Comparable newVar = null;
                switch (variableType.getName()) {
                    case "java.lang.Float":
                        newVar = new Float(0);
                        break;
                    case "java.lang.String":
                        newVar = new String("");
                        break;
                    case "java.lang.Integer":
                        newVar = new Integer(0);
                        break;
                }

                EnvironmentVariable environmentVariable = new EnvironmentVariable();
                environmentVariable.setName(variableName);
                environmentVariable.setVar(newVar);
			//SYBLDirectivesEnforcementLogger.logger.info("Executing method "+methodName);

                Comparable res = (Comparable) method.invoke(monitoringAPI, parameters);
			//SYBLDirectivesEnforcementLogger.logger.info("The monitored variable, " + variableName
                //		+ ", has the value " + res.toString());

                monitoredVariables.put(environmentVariable, res);

            } catch (NoSuchMethodException | SecurityException | IllegalAccessException e) {
                // TODO Auto-generated catch block

                SYBLDirectivesEnforcementLogger.logger.error("In monitoring rule processing" + e.toString());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                SYBLDirectivesEnforcementLogger.logger.error(e.toString());
            } catch (InvocationTargetException e) {
			// TODO Auto-generated catch block

                //SYBLDirectivesEnforcementLogger.logger.info(e.getTargetException().toString());
                e.printStackTrace();
            }

        } else {
            throw new MethodNotFoundException("Method for " + monitoredConcept
                    + " was not found.");
        }
    }

    private String eliminateSpaces(String spaceFull) {
        String spaceFree = "";
        for (int i = 0; i < spaceFull.length(); i++) {
            if (spaceFull.charAt(i) != ' ') {
                spaceFree += spaceFull.charAt(i);
            }
        }

        return spaceFree;
    }

    public void processComplexMonitoringRule(Rule r)
            throws MethodNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String[] s = r.getText().split("WHEN ");
        String monitoring = s[0].split("MONITORING ")[1];
        String condition = s[1];
        // Process condition, if it holds process and enforce constraint
        try {
            if (evaluateCondition(condition)) {
                processSimpleMonitoringRule(monitoring);
            }
        } catch (Exception e) {
            SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());
        }
    }

    /**
     * ************** Constraints Processing
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException ****************************
     */
    public void processComplexConstraint(Rule constraint)
            throws MethodNotFoundException, ConstraintViolationException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String[] s = constraint.getText().split("WHEN ");
        String constr = s[0].split("CONSTRAINT ")[1];
        String condition = s[1];
        // Process condition, if it holds process and enforce constraint
        try {
            if (evaluateCondition(condition)) {
                if (evaluateCondition(constr)) {
                    SYBLDirectivesEnforcementLogger.logger.info("CONSTRAINT " + constraint.getName()
                            + " is fulfilled.");
                } else {
                    throw new ConstraintViolationException("CONSTRAINT "
                            + constraint.getName() + " is violated.");
                }
            } else {
                SYBLDirectivesEnforcementLogger.logger.info("CONSTRAINT " + constraint.getName()
                        + " is not evaluated because the condition " + condition
                        + " is not met.");
            }
        } catch (MeasurementNotAvailableException e) {
            SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());
        }
    }

    public void processCompositeConstraint(Rule constraint) {

    }

    public boolean evaluateCompositeCondition(String compCond) throws MethodNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (compCond.contains("AND")) {
            String[] s = compCond.split("AND ");
        //TODO: implement multiple complex combinations of and and or
            //SYBLDirectivesEnforcementLogger.logger.info("Condition "+s[0]+" is "+evaluateCondition(s[0]));
            //SYBLDirectivesEnforcementLogger.logger.info("Condition "+s[1]+" is "+evaluateCondition(s[1]));
            try {
                if (evaluateCondition(s[0]) && evaluateCondition(s[1])) {
                    return true;
                } else {
                    return false;
                }
            } catch (MeasurementNotAvailableException e) {
                SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());
            }
        }
        return false;
    }

    public Comparable evaluateTerm(String term) throws MeasurementNotAvailableException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Double result = 0.0;
        SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();

        if ((term.charAt(0) >= 'a')
                && (term.charAt(0) <= 'z')) {

            String methodName = descriptionParser.getMethod(term);
            if (!methodName.equals("")) {

                Class partypes[] = new Class[1];

                Object[] parameters = new Object[1];
                parameters[0] = currentEntity;
                partypes[0] = Node.class;

                Method method = MonitoringAPIInterface.class.getMethod(methodName, partypes);
                result = (Double) method.invoke(monitoringAPI, parameters);

            } else {

                EnvironmentVariable myVar = null;

                for (EnvironmentVariable variable : monitoredVariables.keySet()) {
                    if (variable.getName().equalsIgnoreCase(term)) {
                        myVar = variable;
                    }
                }
                if (myVar == null) {
                    try {
                        result = (Double) monitoringAPI.getMetricValue(term, currentEntity);
                    } catch (Exception e) {
                        throw new MeasurementNotAvailableException(term + " not found");
                    }

                } else {
                    result = (Double) monitoredVariables.get(myVar);
                }

            }

        } else {
            if ((term.charAt(0) >= '0')
                    && (term.charAt(0) <= '9')) {
                result = Double.parseDouble(term);
            }
        }
//	SYBLDirectivesEnforcementLogger.logger.info("We evaluate term "+term+" discovered in the requirement. Its value is "+result+ " for node "+currentEntity.getId());
        return result;
    }

    @SuppressWarnings("unchecked")
    public boolean evaluateCondition(String condition) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MeasurementNotAvailableException {
        String[] s = condition.split(" ");
        if (condition.toLowerCase().contains("violated") || condition.toLowerCase().contains("fulfilled")) {
            //SYBLDirectivesEnforcementLogger.logger.info(condition.split("[(]")[0]);
            if (eliminateSpaces(condition.split("\\(")[0]).equalsIgnoreCase("violated")) {
                //Get constraint and check if it is violated
                String name = condition.split("[()]")[1];

                //SYBLDirectivesEnforcementLogger.logger.info("The constraint is "+name+" "+constraints.get(name));
                if (cons.get(eliminateSpaces(name.toLowerCase())) == null) {
                    return false;
                }
                if (cons.get(eliminateSpaces(name.toLowerCase()))) {
                    return false;
                } else {
                    return true;
                }

            }
            if (eliminateSpaces(condition.split("[(]")[0]).equalsIgnoreCase("fulfilled")) {
		//Get constraint and check if it is violated

                String name = condition.split("[()]")[1];
//		for ( String current:cons.keySet()){
//			SYBLDirectivesEnforcementLogger.logger.info("The constraint  "+current+" "+cons.get(current));
//
//		}
                //SYBLDirectivesEnforcementLogger.logger.info("The constraint is "+eliminateSpaces(name.toLowerCase())+" "+cons.get(eliminateSpaces(name.toLowerCase())));
                if (!cons.containsKey(eliminateSpaces(name.toLowerCase()))) {
                    return true;
                } else {
                    return cons.get(eliminateSpaces(name.toLowerCase()));
                }
            }

        } else {
            if (condition.toLowerCase().contains("enabled")) {
                Rule r = new Rule();
                r.setName(eliminateSpaces(condition.split("[()]")[1]));
                if (disabledRules.contains(r)) {
                    return false;
                } else {
                    return true;
                }
            }
            if (condition.toLowerCase().contains("disabled")) {
                Rule r = new Rule();
                r.setName(eliminateSpaces(condition.split("[()]")[1]));
                if (disabledRules.contains(r)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        //SYBLDirectivesEnforcementLogger.logger.info("Evaluating condition " +s[1]+" and terms "+s[0]+" and "+s[2]);
//	if (s[1].equalsIgnoreCase("<")){
//		if (((Double)evaluateTerm(s[0]))<((Double)evaluateTerm(s[2])))return true;
//		else return false;
//	}
        try {
            switch (s[1]) {
                case ">":
                    if ((evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) <= 0)) {
                        return false;
                    } else {
                        return true;
                    }
                case "<":
                    if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) >= 0) {
                        return false;
                    } else {
                        return true;
                    }
                case ">=":
                    if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) < 0) {
                        return false;
                    } else {
                        return true;
                    }
                case "<=":
                    if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) > 0) {
                        return false;
                    } else {
                        return true;
                    }
                case "==":
                    if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) != 0) {
                        return false;
                    } else {
                        return true;
                    }
                case "!=":
                    if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) == 0) {
                        return false;
                    } else {
                        return true;
                    }

                default:
                    break;
            }
        } catch (MeasurementNotAvailableException availableException) {
            throw availableException;
        }

        return false;
    }

    public void processSimpleConstraint(Rule constraint)
            throws MethodNotFoundException, ConstraintViolationException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String s[] = constraint.getText().split("CONSTRAINT ");
        if (s[1].contains("AND")) {
            if (evaluateCompositeCondition(s[1])) {
                cons.put(eliminateSpaces(constraint.getName().toLowerCase()), true);
                SYBLDirectivesEnforcementLogger.logger.info("CONSTRAINT " + constraint.getName()
                        + " is fulfilled.");

            } else {
                cons.put(eliminateSpaces(constraint.getName().toLowerCase()), false);

                SYBLDirectivesEnforcementLogger.logger.info("CONSTRAINT "
                        + constraint.getName() + " is violated.");
            }
        }
        try {
            if (evaluateCondition(s[1])) {
                cons.put(eliminateSpaces(constraint.getName().toLowerCase()), true);
                SYBLDirectivesEnforcementLogger.logger.info("CONSTRAINT " + constraint.getName()
                        + " is fulfilled.");

            } else {
                cons.put(eliminateSpaces(constraint.getName().toLowerCase()), false);

                SYBLDirectivesEnforcementLogger.logger.info("CONSTRAINT "
                        + constraint.getName() + " is violated.");
            }
        } catch (MeasurementNotAvailableException e) {
            SYBLDirectivesEnforcementLogger.logger.error(e.getMessage());

        }
    }

    public boolean isEnforcingAction() {
        return enforcingAction;
    }

    public void setEnforcingAction(boolean enforcingAction) {
        this.enforcingAction = enforcingAction;
    }

    /**
     * @return the governanceScopes
     */
    public String getGovernanceScopes() {
        return governanceScopes;
    }

    /**
     * @param governanceScopes the governanceScopes to set
     */
    public void setGovernanceScopes(String governanceScopes) {
        this.governanceScopes = governanceScopes;
    }
}
