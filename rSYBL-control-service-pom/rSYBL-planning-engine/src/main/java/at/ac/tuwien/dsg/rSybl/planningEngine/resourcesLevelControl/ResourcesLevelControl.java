/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. * This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
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
package at.ac.tuwien.dsg.rSybl.planningEngine.resourcesLevelControl;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Georgiana
 */
public class ResourcesLevelControl implements Runnable {
    
    private Timer t;
    private int REFRESH_PERIOD = 350000;
    private DependencyGraph dependencyGraph;
    private EnforcementAPIInterface enforcementAPI;
    private MonitoringAPIInterface monitoringAPI;
    private double stdDevThreshold = 23.0;
    
    private HashMap<String, HashMap<Double, HashMap<String, Double>>> expectedEffects = new HashMap<String, HashMap<Double, HashMap<String, Double>>>();
    private HashMap<String, HashMap<String, Double>> solution = new HashMap<String, HashMap<String, Double>>();
    
    public ResourcesLevelControl(EnforcementAPIInterface enforcementAPIInterface, MonitoringAPIInterface monitoringAPIInterface, DependencyGraph dependencyGraph) {
        enforcementAPI = enforcementAPIInterface;
        monitoringAPI = monitoringAPIInterface;
        this.dependencyGraph = dependencyGraph;
        REFRESH_PERIOD = Configuration.getRefreshPeriod();
        readResourceActionEffects();
    }
    
    public void start() {
        run();
    }
    
    public void stop() {
        boolean ok = false;
        while (!ok) {
            if (enforcementAPI.getPluginsExecutingActions().size() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(RuntimeLogger.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                ok = true;
            }
        }
        t.purge();
        t.cancel();
    }
    
    public void replaceDependencyGraph(DependencyGraph dependencyGraph) {
        this.dependencyGraph = dependencyGraph;
        
    }
    
    public void checkForOverusedResourcesAndScale() {
        List<Node> serviceUnits = dependencyGraph.getAllServiceUnits();
        for (Node serviceUnit : serviceUnits) {
            List<String> metrics = monitoringAPI.getAvailableMetrics(serviceUnit);
            double remainingCost = this.remainingCost(serviceUnit);
            for (String metric : metrics) {
                
                if (metric.toLowerCase().contains("usage") || metric.toLowerCase().contains("usedpercent")) {
                    try {
                        List<Node> associatedVMs = serviceUnit.getAllRelatedNodesOfType(Relationship.RelationshipType.HOSTED_ON_RELATIONSHIP, Node.NodeType.VIRTUAL_MACHINE);
                        double max = 0;
                        Node maxVm = null;
                        boolean foundSmaller = false;
                        HashMap<String, Double> currentValues = new HashMap<>();
                        double sum = 0;
                        for (Node vm : associatedVMs) {
                            try {
                                double vmMetric = monitoringAPI.getMetricValue(metric, vm);   
                                sum += vmMetric;
                                if (vmMetric > max) {
                                    max = vmMetric;
                                    maxVm = vm;
                                }
                                if (vmMetric < 50) {
                                    foundSmaller = true;
                                }
                                currentValues.put(vm.getId(), vmMetric);
                            } catch (Exception ex) {
                                PlanningLogger.logger.info("Error in resources control, when converting values for metrics "+ ex);
                            }
                        }
                        double val = sum/associatedVMs.size();
                        
                        if (maxVm != null && max > 90 && foundSmaller) {
                            searchAction(serviceUnit, maxVm, metric, val, remainingCost,currentValues);
                        } else {
                            double stdDev = computeStdDeviation(currentValues);
                            if (stdDev > stdDevThreshold) {
                                double maxDiff = -2;
                                Node vmToFix = null;
                                for (Node vm : associatedVMs) {
                                    double vmValue = currentValues.get(vm.getId());
                                    if (maxDiff < Math.abs(vmValue - val)) {
                                        maxDiff = Math.abs(vmValue - val);
                                        vmToFix = vm;
                                    }
                                }
                                if (vmToFix != null) {
                                    searchAction(serviceUnit, vmToFix, metric, stdDev, remainingCost,currentValues);
                                }
                            }
                        }
                    } catch (Exception ex) {
//                        ex.printStackTrace();
                        PlanningLogger.logger.info("Could not get metric " + metric + " for node " + serviceUnit + " ex " + ex.getLocalizedMessage());
                    }
                    
                }
            }
            executeGeneratedSolution(remainingCost);
        }
        
    }
    
    private void executeGeneratedSolution(double remainingCost) {
        for (String vmID : solution.keySet()) {
            if (solution.size() != 0) {
                
                Object[] parameters = new Object[2 * solution.get(vmID).size() + 1];
                int i = 0;
                for (String type : this.solution.get(vmID).keySet()) {
                    parameters[i] = type;
                    parameters[i + 1] = solution.get(vmID).get(type);
                    i += 2;
                }
                
                parameters[i] = remainingCost;
                
                enforcementAPI.enforceAction("scaleVertically", dependencyGraph.getNodeWithID(vmID), parameters);
            }
        }
        solution = new HashMap<>();
    }
    
    public double remainingCost(Node serviceUnit) {
        List<ElasticityRequirement> requirements = new ArrayList<>();
        Node serviceTopology = dependencyGraph.findParentNode(serviceUnit.getId());
        Node cloudService = dependencyGraph.findParentNode(serviceTopology.getId());
        
        requirements.addAll(serviceUnit.getElasticityRequirements());
        requirements.addAll(serviceTopology.getElasticityRequirements());
        requirements.addAll(cloudService.getElasticityRequirements());
        double minCost = 1000000;
        for (ElasticityRequirement elasticityRequirement : requirements) {
            Node unit = dependencyGraph.getNodeWithID(elasticityRequirement.getAnnotation().getEntityID());
            
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
            for (Constraint c : syblSpecification.getConstraint()) {
                BinaryRestriction restriction = c.getToEnforce().getBinaryRestriction().get(0).getBinaryRestrictions().get(0);
                if (restriction.getLeftHandSide().getMetric() != null && restriction.getLeftHandSide().getMetric().equalsIgnoreCase("cost")) {
                    try {
                        double currentCost = monitoringAPI.getMetricValue(restriction.getLeftHandSide().getMetric(), unit);
                        if (minCost > (Double.parseDouble(restriction.getRightHandSide().getNumber()) - currentCost)) {
                            minCost = Double.parseDouble(restriction.getRightHandSide().getNumber()) - currentCost;
                        }
                    } catch (Exception ex) {
                        RuntimeLogger.logger.info("Could not get metric " + restriction.getLeftHandSide().getMetric() + " for node " + unit + " ex " + ex.getLocalizedMessage());
                    }
                }
                if (restriction.getRightHandSide().getMetric() != null && restriction.getRightHandSide().getMetric().equalsIgnoreCase("cost")) {
                    try {
                        double currentCost = monitoringAPI.getMetricValue(restriction.getRightHandSide().getMetric(), unit);
                        if (minCost > (Double.parseDouble(restriction.getLeftHandSide().getNumber()) - currentCost)) {
                            minCost = Double.parseDouble(restriction.getLeftHandSide().getNumber()) - currentCost;
                        }
                    } catch (Exception ex) {
                        
                        RuntimeLogger.logger.info("Could not get metric " + restriction.getRightHandSide().getMetric() + " for node " + unit + " ex " + ex.getLocalizedMessage());
                    }
                }
            }
            
        }
        
        return minCost;
    }
    
    public boolean searchAction(Node serviceUnit, Node vm, String metric, double initialStdDev, double remainingCost,HashMap<String,Double> currentValues) {
        boolean ok = true;
        
        double minStdDev = initialStdDev;
        String resTypeMinStdDev = "";
        double resSizeMinStdDev = 10000;
        
        for (String resourceType : this.expectedEffects.keySet()) {
            for (double resourceSize : expectedEffects.get(resourceType).keySet()) {
                double valBefore = currentValues.get(vm.getId());
                if (expectedEffects.get(resourceType).get(resourceSize).get(metric) != null) {
                    currentValues.put(vm.getId(), valBefore + expectedEffects.get(resourceType).get(resourceSize).get(metric));
                    double newStdDev = computeStdDeviation(currentValues);
                    if (newStdDev < minStdDev) {
                        minStdDev = newStdDev;
                        resTypeMinStdDev = resourceType;
                        resSizeMinStdDev = resourceSize;
                    }
                    currentValues.put(vm.getId(), valBefore);
                }
            }
        }
        if (!resTypeMinStdDev.equalsIgnoreCase("")) {
            if (!solution.containsKey(vm.getId())) {
                solution.put(vm.getId(), new HashMap<String, Double>());
            }
            this.solution.get(vm.getId()).put(resTypeMinStdDev, resSizeMinStdDev);
        }
        return ok;
    }
    
    public void readResourceActionEffects() {
        JSONParser parser = new JSONParser();
        try {
            InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(Configuration.getResourcesEffectsPath());
            Object obj = parser.parse(new InputStreamReader(inputStream));
            
            JSONObject jsonObject = (JSONObject) obj;
            
            for (Object resourceType : jsonObject.keySet()) {
                expectedEffects.put((String) resourceType, new HashMap<Double, HashMap<String, Double>>());
                JSONObject object = (JSONObject) jsonObject.get(resourceType);
                
                for (Object resourceSize : object.keySet()) {
                    expectedEffects.get((String) resourceType).put(Double.parseDouble((String) resourceSize), new HashMap<String, Double>());
                    JSONObject metrics = (JSONObject) object.get(resourceSize);
                    for (Object metric : metrics.keySet()) {
                        double expectedEffect = (double) metrics.get(metric);
                        expectedEffects.get((String) resourceType).get(Double.parseDouble((String) resourceSize)).put((String) metric, expectedEffect);
                    }
                }
            }
        } catch (Exception e) {
            RuntimeLogger.logger.error("Error while reading resources effects");
        }
    }
    
    public double computeStdDeviation(HashMap<String, Double> currentValues) {
        double sum = 0;
        for (Double val : currentValues.values()) {
            sum += val;
        }
        double average = sum / currentValues.size();
        double sumOfSquares = 0;
        for (Double val : currentValues.values()) {
            sumOfSquares += Math.pow((val - average), 2);
        }
        sumOfSquares /= currentValues.size();
        
        return Math.sqrt(sumOfSquares);
    }
    
    @Override
    public void run() {
        t = new Timer();
        try {
            t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (dependencyGraph.isInControlState()) {
//                        try {
//                            Thread.sleep(REFRESH_PERIOD);
//                        } catch (InterruptedException e) {
//                            RuntimeLogger.logger.error(e.toString());
//                        }

                        Node cloudService = monitoringAPI.getControlledService();
                        
                        dependencyGraph.setCloudService(cloudService);
                        PlanningLogger.logger.info("Planning resource level control ");
                        checkForOverusedResourcesAndScale();
                    }
                }
            }, 2 * REFRESH_PERIOD, 2 * REFRESH_PERIOD);
        } catch (Exception e) {
            PlanningLogger.logger.info("Found error " + e.getMessage());
            
        }
    }
    
}
