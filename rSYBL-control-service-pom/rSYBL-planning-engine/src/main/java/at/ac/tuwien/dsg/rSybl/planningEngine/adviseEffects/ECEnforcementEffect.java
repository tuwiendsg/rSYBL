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
import at.ac.tuwien.dsg.rSybl.learningEngine.advise.kMeans.NDimensionalPoint;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription.SYBLDescriptionParser;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Georgiana
 */
public class ECEnforcementEffect {

    private List<ContextRepresentation> withEnforcing = new LinkedList<ContextRepresentation>();
    private ElasticityCapability capability;
    private LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>> measurements = new LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>();
    private DependencyGraph dependencyGraph;
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
        initializeContexts();
    }

    public double getImprovedStrategies(ContextRepresentation beforeContextRepresentation) {
        ContextEvaluation contextEvaluation = new ContextEvaluation();

        return contextEvaluation.countFixedStrategies(dependencyGraph, withEnforcing.get(withEnforcing.size() - 1), beforeContextRepresentation);
    }

    public double getFinalStateViolatedConstraints() {
        ContextEvaluation contextEvaluation = new ContextEvaluation();

        return contextEvaluation.countViolatedConstraints(dependencyGraph, withEnforcing.get(withEnforcing.size() - 1));
    }

    public double overallViolatedConstraints() {
        double overallViolatedConstraints = 0.0;
        ContextEvaluation contextEvaluation = new ContextEvaluation();
        for (ContextRepresentation contextRepresentation : withEnforcing) {
            overallViolatedConstraints += contextEvaluation.countViolatedConstraints(dependencyGraph, contextRepresentation);

        }
        return overallViolatedConstraints / withEnforcing.size();
    }

    private void initializeContexts() {

        LinkedHashMap<String, LinkedHashMap<String, Double>> metrics;


        LinkedHashMap<String, LinkedHashMap<String, NDimensionalPoint>> result = behavior.computeExpectedBehavior(capability);
        for (int i = 0; i < ECPBehavioralModel.CHANGE_INTERVAL; i++) {
            metrics = new LinkedHashMap<>();
            for (String node : result.keySet()) {
                if (!metrics.containsKey(node)) {
                    metrics.put(node, new LinkedHashMap<String, Double>());
                }
                for (String metric : result.get(node).keySet()) {
                    metrics.get(node).put(metric, result.get(node).get(metric).getValues().get(i));
                }
            }
            ContextRepresentation contextRepresentation = new ContextRepresentation(cloudService, metrics);
            withEnforcing.add(contextRepresentation);
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
