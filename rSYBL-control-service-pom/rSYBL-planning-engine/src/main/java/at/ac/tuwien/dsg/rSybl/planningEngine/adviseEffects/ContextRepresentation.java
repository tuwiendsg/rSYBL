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
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Monitoring;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;

import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.languageDescription.SYBLDescriptionParser;
import java.lang.Double;
import java.lang.String;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Georgiana
 */
public class ContextRepresentation {


    private LinkedHashMap<String, LinkedHashMap<String, Double>> metricValues;

    public ContextRepresentation(Node cloudService, LinkedHashMap<String, LinkedHashMap<String, Double>> values) {
        DependencyGraph dependencyGraph = new DependencyGraph();
        dependencyGraph.setCloudService(cloudService);
        for (String node : values.keySet()) {
            if (!metricValues.containsKey(node)) {
                metricValues.put(node, new LinkedHashMap<String, Double>());
            }
            for (String metric : values.get(node).keySet()) {

                metricValues.get(node).put(metric, values.get(node).get(metric));
            }
        }
    }

    public double getMetricValue(String node, String metric) {
        return metricValues.get(node).get(metric);
    }
    
    public void setMetricValue(String node, String metric,Double value){
        if (!metricValues.containsKey(node)){
            metricValues.put(node, new LinkedHashMap<String,Double>());
        }
        metricValues.get(node).put(metric, value);
    }

}
