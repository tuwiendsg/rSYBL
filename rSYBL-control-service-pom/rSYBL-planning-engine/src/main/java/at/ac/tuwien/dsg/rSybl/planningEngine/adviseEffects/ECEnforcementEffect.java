/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.planningEngine.adviseEffects;

import at.ac.tuwien.dsg.csdg.DataElasticityDependency;
import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.LoadElasticityDependency;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitoring;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.ServicePartMonitor;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ComputeBehavior;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.ECPBehavioralModel;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.MyEntry;
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription.SYBLDescriptionParser;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class ECEnforcementEffect {

    private LinkedList<ContextRepresentation> withEnforcing = new LinkedList<ContextRepresentation>();
    private ElasticityCapability capability;
    private LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> measurements = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>();
    private DependencyGraph dependencyGraph;
    private double ACCEPTABLE_DISTANCE= 400;
    public double MAX_CONSTRAINTS = 10000;
    private MonitoringAPIInterface monitoringInterface;
    private Node cloudService;
    private ComputeBehavior behavior;
    public ECEnforcementEffect(ComputeBehavior behavior, Node cloudService, MonitoringAPIInterface monitoringAPIInterface, ElasticityCapability capability1) {
        dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        monitoringInterface = monitoringAPIInterface;
        this.cloudService = cloudService;
        capability = capability1;
        this.behavior = behavior;
        if (Configuration.getAcceptableDistance()>0)
            ACCEPTABLE_DISTANCE = Configuration.getAcceptableDistance();
        initializeContexts();
    }

    public double getImprovedStrategies(ContextRepresentation beforeContextRepresentation) {
        ContextEvaluation contextEvaluation = new ContextEvaluation();

        return contextEvaluation.countFixedStrategies(dependencyGraph, withEnforcing.get(withEnforcing.size() - 1), beforeContextRepresentation);
    }

    public double getFinalStateViolatedConstraints() {
        ContextEvaluation contextEvaluation = new ContextEvaluation();
        
        if (!withEnforcing.isEmpty() && withEnforcing.get(withEnforcing.size()-1).getDistance()<ACCEPTABLE_DISTANCE)
        return contextEvaluation.countViolatedConstraints(dependencyGraph, withEnforcing.get(withEnforcing.size() - 1));
        else
            return MAX_CONSTRAINTS;
    }

    public double overallViolatedConstraints() {
        double overallViolatedConstraints = 0.0;
        ContextEvaluation contextEvaluation = new ContextEvaluation();
        double avgDist=0.0;
        for (ContextRepresentation contextRepresentation : withEnforcing) {
            overallViolatedConstraints += contextEvaluation.countViolatedConstraints(dependencyGraph, contextRepresentation);
            avgDist+=contextRepresentation.getDistance();
        }
        
        if (withEnforcing.isEmpty()|| (avgDist/withEnforcing.size()>ACCEPTABLE_DISTANCE))
            return MAX_CONSTRAINTS;
        else
        return overallViolatedConstraints / withEnforcing.size();
    }

    private void initializeContexts() {

        LinkedHashMap<String, LinkedHashMap<String, Double>> metrics;
        
        LinkedHashMap<String, LinkedHashMap<String, MyEntry<Double, NDimensionalPoint>>> result = behavior.computeExpectedBehavior(capability);
        
//        for (String node:result.keySet()){
//            for (String metric: result.get(node).keySet()){
//                try {
//                    //double initialValue = monitoringInterface.getMetricValue(metric, dependencyGraph.getNodeWithID(node));
//                    //double initialPredicted= result.get(node).get(metric).getValues().get(0);
//                    LinkedList<Double> values=result.get(node).get(metric).getValue().getValues();
//                    for (int i= 0;i<values.size();i++){
//                        values.set(i, values.get(i));
//                    }
//                    result.get(node).get(metric).setValues(result.get(node).get(metric).getValue().getValues());
//                } catch (Exception ex) {
//                    Logger.getLogger(ECEnforcementEffect.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                
//            }
//        }
        for (int i = 0; i < ECPBehavioralModel.CHANGE_INTERVAL; i++) {
            metrics = new LinkedHashMap<>();
            double distance = 0.0;
            double nbDistances= 0;
            for (String node : result.keySet()) {
                if (!metrics.containsKey(node)) {
                    metrics.put(node, new LinkedHashMap<String, Double>());
                }
                for (String metric : result.get(node).keySet()) {
                    
                    metrics.get(node).put(metric, result.get(node).get(metric).getValue().getValues().get(i));
                    distance+= result.get(node).get(metric).getKey();
                    nbDistances++;
                }
            }
            if (nbDistances>0){
            ContextRepresentation contextRepresentation = new ContextRepresentation(cloudService, metrics);
            contextRepresentation.setDistance(distance/nbDistances);
            withEnforcing.add(contextRepresentation);
            
        }
        }
    }

    private List<String> findTargetedMetrics(Node entity) {
        ArrayList<String> metricsTargeted = new ArrayList<String>();

        SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();
        for (Relationship rel : dependencyGraph.getAllRelationshipsOfType(Relationship.RelationshipType.DATA)) {
            DataElasticityDependency dataElasticityDependency = (DataElasticityDependency) rel;
            if (entity.getId().equalsIgnoreCase(dataElasticityDependency.getSourceElement())) {

                if (!metricsTargeted.contains(dataElasticityDependency.getDataMeasurementSource())) {
                    metricsTargeted.add(dataElasticityDependency.getDataMeasurementSource());
                }



            }
            if (entity.getId().equalsIgnoreCase(dataElasticityDependency.getTargetElement())) {
                if (!metricsTargeted.contains(dataElasticityDependency.getDataMeasurementTarget())) {
                    metricsTargeted.add(dataElasticityDependency.getDataMeasurementTarget());
                }
            }
        }
        for (Relationship rel : dependencyGraph.getAllRelationshipsOfType(Relationship.RelationshipType.LOAD)) {
            LoadElasticityDependency loadElasticityDependency = (LoadElasticityDependency) rel;

            if (entity.getId().equalsIgnoreCase(loadElasticityDependency.getSourceElement())) {

                if (!metricsTargeted.contains(loadElasticityDependency.getSourceLoadMetric())) {
                    metricsTargeted.add(loadElasticityDependency.getSourceLoadMetric());
                }



            }
            if (entity.getId().equalsIgnoreCase(loadElasticityDependency.getTargetElement())) {
                if (!metricsTargeted.contains(loadElasticityDependency.getTargetLoadMetric())) {
                    metricsTargeted.add(loadElasticityDependency.getTargetLoadMetric());
                }
            }
        }
        for (ElasticityRequirement elasticityRequirement : entity.getElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elasticityRequirement.getAnnotation());
            for (Strategy strategy : syblSpecification.getStrategy()) {
                if (strategy.getToEnforce().getParameter() != null && metricsTargeted.contains(strategy.getToEnforce().getParameter())) {
                    metricsTargeted.add(strategy.getToEnforce().getParameter());
                }
                if (strategy.getCondition() != null) {
                    for (BinaryRestrictionsConjunction restrictions : strategy.getCondition().getBinaryRestriction()) {
                        for (BinaryRestriction restriction : restrictions.getBinaryRestrictions()) {
                            String right = restriction.getRightHandSide().getMetric();
                            String left = restriction.getLeftHandSide().getMetric();
                            if (right != null && !right.equalsIgnoreCase("")) {
                                if (!metricsTargeted.contains(right)) {
                                    metricsTargeted.add(right);
                                }
                            }

                            if (left != null && !left.equalsIgnoreCase("")) {
                                if (!metricsTargeted.contains(left)) {
                                    metricsTargeted.add(left);
                                }
                            }





                        }
                    }
                }
            }


            for (Monitoring monitoring : syblSpecification.getMonitoring()) {
                if (!metricsTargeted.contains(monitoring.getMonitor().getMetric())) {
                    metricsTargeted.add(monitoring.getMonitor().getMetric());
                }
            }

            for (Constraint constraint : syblSpecification.getConstraint()) {
                for (BinaryRestrictionsConjunction restrictions : constraint.getToEnforce().getBinaryRestriction()) {
                    for (BinaryRestriction restriction : restrictions.getBinaryRestrictions()) {
                        String right = restriction.getRightHandSide().getMetric();
                        String left = restriction.getLeftHandSide().getMetric();

                        if (right != null && !right.equalsIgnoreCase("")) {
                            if (!metricsTargeted.contains(right)) {
                                metricsTargeted.add(right);
                            }
                        }

                        if (left != null && !left.equalsIgnoreCase("")) {
                            if (!metricsTargeted.contains(left)) {
                                metricsTargeted.add(left);
                            }
                        }
                    }
                }
                if (constraint.getCondition() != null) {
                    for (BinaryRestrictionsConjunction restrictions : constraint.getCondition().getBinaryRestriction()) {
                        for (BinaryRestriction restriction : restrictions.getBinaryRestrictions()) {
                            String right = restriction.getRightHandSide().getMetric();
                            String left = restriction.getLeftHandSide().getMetric();
                            if (right != null && !right.equalsIgnoreCase("")) {
                                if (!metricsTargeted.contains(right)) {
                                    metricsTargeted.add(right);
                                }
                            }

                            if (left != null && !left.equalsIgnoreCase("")) {
                                if (!metricsTargeted.contains(left)) {
                                    metricsTargeted.add(left);
                                }
                            }
                        }
                    }
                }
            }
        }
        return metricsTargeted;
    }
}
