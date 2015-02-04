/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.planningEngine.adviseEffects;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.BinaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Condition;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Constraint;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLSpecification;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.Strategy;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.UnaryRestriction;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.UnaryRestrictionsConjunction;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.SYBLDirectiveMappingFromXML;
import at.ac.tuwien.dsg.rSybl.planningEngine.adviseEffects.ContextRepresentation;
import at.ac.tuwien.dsg.rSybl.planningEngine.MonitoredEntity;
import at.ac.tuwien.dsg.rSybl.planningEngine.utils.PlanningLogger;

/**
 *
 * @author Georgiana
 */
public class ContextEvaluation {
      public String getViolatedConstraints(DependencyGraph dependencyGraph , ContextRepresentation currentContextRepresentation) {
        String constr = "";
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());

            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
            String monitoredEntity = syblSpecification.getComponentId();

            for (Constraint constraint : syblSpecification.getConstraint()) {

                if (evaluateCondition(dependencyGraph,constraint.getCondition(), monitoredEntity,currentContextRepresentation) && !evaluateCondition(dependencyGraph,constraint.getToEnforce(), monitoredEntity,currentContextRepresentation)) {
                    constr += constraint.getId() + " ";
                }

            }
        }
        return constr;
    }

    public String getImprovedStrategies(DependencyGraph dependencyGraph, ContextRepresentation currentContextRepresentation, ContextRepresentation previousContextRepresentation, String strategiesNeedingToBe) {
        String str = "";
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
            String monitoredEntity = syblSpecification.getComponentId();
            if (monitoredEntity == null) {
                PlanningLogger.logger.info("Not finding monitored entity " + monitoredEntity + " " + syblSpecification.getComponentId());
            }
            for (Strategy strategy : syblSpecification.getStrategy()) {
                Condition condition = strategy.getCondition();
                if (strategiesNeedingToBe.contains(strategy.getId())) {
                    if (evaluateCondition(dependencyGraph,condition, monitoredEntity,currentContextRepresentation)) {
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize") || strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                            if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")) {
                                //PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                                if (currentContextRepresentation.getMetricValue(monitoredEntity,strategy.getToEnforce().getParameter()) > previousContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                    str += strategy.getId() + " ";
                                }
                            }
                            if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                                //	PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                                if (currentContextRepresentation.getMetricValue(monitoredEntity,strategy.getToEnforce().getParameter()) < previousContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                    str += strategy.getId() + " ";
                                }
                            }
                        }
                    }
                }
            }
        }
        return str;
    }

    private double quantifyBinaryRestriction(BinaryRestriction binaryRestriction, String monitoredEntity, ContextRepresentation contextRepresentation) {
        double fulfilled = 0.0;
        Double currentLeftValue = 0.0;
        Double currentRightValue = 0.0;
        if (binaryRestriction.getLeftHandSide().getMetric() != null) {
            String metric = binaryRestriction.getLeftHandSide().getMetric();
            //PlanningLogger.logger.info(monitoredEntity+" "+metric);
            currentLeftValue = contextRepresentation.getMetricValue(monitoredEntity, metric);
            if (binaryRestriction.getRightHandSide().getNumber()!=null){
            currentRightValue = Double.parseDouble(binaryRestriction.getRightHandSide().getNumber());
            }else{
                 if (binaryRestriction.getRightHandSide().getMetric()!=null && !binaryRestriction.getRightHandSide().getMetric().equalsIgnoreCase("")){
                    if (binaryRestriction.getRightHandSide().getMetric().contains(".")){
                        String [] s = binaryRestriction.getRightHandSide().getMetric().split(".");
                        currentRightValue=contextRepresentation.getMetricValue(s[0], s[1]); 
                    }else{
                        currentRightValue=contextRepresentation.getMetricValue(monitoredEntity, binaryRestriction.getRightHandSide().getMetric());    
                    }
                    
                }
            }
            
        } else if (binaryRestriction.getRightHandSide().getMetric() != null) {
            
            String metric = binaryRestriction.getRightHandSide().getMetric();
            if (metric!=null) {
                currentRightValue = contextRepresentation.getMetricValue(monitoredEntity, metric);
            }
            //System.out.println("Current value for metric is  "+ currentRightValue);
            if (binaryRestriction.getLeftHandSide().getNumber()!=null){
            currentLeftValue = Double.parseDouble(binaryRestriction.getLeftHandSide().getNumber());
            }else{
                 if (binaryRestriction.getLeftHandSide().getMetric()!=null && !binaryRestriction.getLeftHandSide().getMetric().equalsIgnoreCase("")){
                    if (binaryRestriction.getLeftHandSide().getMetric().contains(".")){
                        String [] s = binaryRestriction.getLeftHandSide().getMetric().split(".");
                        currentLeftValue=contextRepresentation.getMetricValue(s[0], s[1]); 
                    }else{
                        currentLeftValue=contextRepresentation.getMetricValue(monitoredEntity, binaryRestriction.getLeftHandSide().getMetric());    
                    }
                    
                }
            }
            
        }
        switch (binaryRestriction.getType()) {
            case "lessThan":
                if (currentLeftValue >= currentRightValue) {
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);
                }
                break;
            case "greaterThan":
                if (currentLeftValue <= currentRightValue) {
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);

                }
                break;
            case "lessThanOrEqual":
                if (currentLeftValue > currentRightValue) {
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);

                }
                break;
            case "greaterThanOrEqual":
                if (currentLeftValue < currentRightValue) {
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);
                }
                break;
            case "differentThan":
                if (currentLeftValue == currentRightValue) {
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);
                }
                break;
            case "equals":
                if (currentLeftValue != currentRightValue) {
                    //System.out.println("Violated constraint "+constraint.getId());
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);
                }
                break;
            default:
                if (currentLeftValue >= currentRightValue) {
                    //System.out.println("Violated constraint "+constraint.getId());
                    fulfilled = Math.abs((currentLeftValue - currentRightValue) / currentRightValue);
                }
                break;
        }
        return fulfilled;
    }

    public boolean evaluateBinaryRestriction(BinaryRestriction binaryRestriction, String node, ContextRepresentation currentContextRepresentation) {
        boolean fulfilled = true;
        Double currentLeftValue = 0.0;
        Double currentRightValue = 0.0;
        if (binaryRestriction.getLeftHandSide().getMetric() != null) {
            String metric = binaryRestriction.getLeftHandSide().getMetric();
            //PlanningLogger.logger.info(monitoredEntity+" "+metric);
            currentLeftValue = currentContextRepresentation.getMetricValue(node,metric);
            if (currentLeftValue < 0) {
                
                    currentLeftValue = 0.0;
                
            }
            try{
            if (binaryRestriction.getRightHandSide().getNumber()!=null){
                currentRightValue = Double.parseDouble(binaryRestriction.getRightHandSide().getNumber());
            }else{
                if (binaryRestriction.getRightHandSide().getMetric()!=null && !binaryRestriction.getRightHandSide().getMetric().equalsIgnoreCase("")){
                    if (binaryRestriction.getRightHandSide().getMetric().contains(".")){
                        String [] s = binaryRestriction.getRightHandSide().getMetric().split(".");
                        currentRightValue=currentContextRepresentation.getMetricValue(s[0], s[1]); 
                    }else{
                        currentRightValue=currentContextRepresentation.getMetricValue(node, binaryRestriction.getRightHandSide().getMetric());    
                    }
                    
                }
            }
            }catch(Exception e){
                
                currentRightValue=0.0d;
                PlanningLogger.logger.error("Error in ContextEvaluation class, evaluateBinaryRestriction method, when trying to convert left hand side.");
            }
        } else if (binaryRestriction.getRightHandSide().getMetric() != null) {
            try{
            String metric = binaryRestriction.getRightHandSide().getMetric();
            currentRightValue = currentContextRepresentation.getMetricValue(node,metric);;
            //System.out.println("Current value for metric is  "+ currentRightValue);
            if (binaryRestriction.getLeftHandSide().getNumber()!=null){
            currentLeftValue = Double.parseDouble(binaryRestriction.getLeftHandSide().getNumber());
            }else{
                if (binaryRestriction.getLeftHandSide().getMetric()!=null && !binaryRestriction.getLeftHandSide().getMetric().equalsIgnoreCase("")){
                    if (binaryRestriction.getLeftHandSide().getMetric().contains(".")){
                        String [] s = binaryRestriction.getLeftHandSide().getMetric().split(".");
                        currentRightValue=currentContextRepresentation.getMetricValue(s[0], s[1]); 
                    }else{
                        currentRightValue=currentContextRepresentation.getMetricValue(node, binaryRestriction.getLeftHandSide().getMetric());    
                    }
                    
                }
            }
            }catch(Exception e ){
                PlanningLogger.logger.error("Error in ContextEvaluation class, evaluateBinaryRestriction method, when trying to convert right hand side.");
            }
        }
        switch (binaryRestriction.getType()) {
            case "lessThan":
                if (currentLeftValue >= currentRightValue) {
                    fulfilled = false;
                }
                break;
            case "greaterThan":
                if (currentLeftValue <= currentRightValue) {
                    fulfilled = false;

                }
                break;
            case "lessThanOrEqual":
                if (currentLeftValue > currentRightValue) {
                    fulfilled = false;

                }
                break;
            case "greaterThanOrEqual":
                if (currentLeftValue < currentRightValue) {
                    fulfilled = false;
                }
                break;
            case "differentThan":
                if (currentLeftValue == currentRightValue) {
                    fulfilled = false;
                }
                break;
            case "equals":
                if (currentLeftValue != currentRightValue) {
                    //System.out.println("Violated constraint "+constraint.getId());
                    fulfilled = false;
                }
                break;
            default:
                if (currentLeftValue >= currentRightValue) {
                    //System.out.println("Violated constraint "+constraint.getId());
                    fulfilled = false;
                }
                break;
        }
        return fulfilled;
    }

    public int countFixedStrategies(DependencyGraph dependencyGraph, ContextRepresentation currentContextRepresentation,ContextRepresentation previousContextRepresentation, String strategiesThatNeedToBeImproved) {
        int nbFixedStrategies = 0;
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
            String monitoredEntity = syblSpecification.getComponentId();
            if (monitoredEntity == null) {
                PlanningLogger.logger.info("Not finding monitored entity " + monitoredEntity + " " + syblSpecification.getComponentId());
            }
            for (Strategy strategy : syblSpecification.getStrategy()) {
                Condition condition = strategy.getCondition();

                if (strategiesThatNeedToBeImproved.contains(strategy.getId()) && evaluateCondition(dependencyGraph,condition, monitoredEntity,currentContextRepresentation)) {
                    if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize") || strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")) {
                            //PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                            if (currentContextRepresentation.getMetricValue( monitoredEntity,strategy.getToEnforce().getParameter()) > previousContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                nbFixedStrategies += 1;
                            }
                        }
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                            //	PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                            if (currentContextRepresentation.getMetricValue(monitoredEntity,strategy.getToEnforce().getParameter()) < previousContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                nbFixedStrategies += 1;
                            }
                        }
                    }
                }
            }
        }
        return nbFixedStrategies;
    }

    public int countFixedStrategies(DependencyGraph dependencyGraph,ContextRepresentation currentContextRepresentation,ContextRepresentation previousContextRepresentation) {
        int nbFixedStrategies = 0;
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
            String monitoredEntity = syblSpecification.getComponentId();
            if (monitoredEntity == null) {
                PlanningLogger.logger.info("Not finding monitored entity " + monitoredEntity + " " + syblSpecification.getComponentId());
            }
            for (Strategy strategy : syblSpecification.getStrategy()) {
                Condition condition = strategy.getCondition();

                if (evaluateCondition(dependencyGraph,condition, monitoredEntity,currentContextRepresentation)) {
                    if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize") || strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("maximize")) {
                            //PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                            if (currentContextRepresentation.getMetricValue(monitoredEntity,strategy.getToEnforce().getParameter()) > previousContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                nbFixedStrategies += 1;
                                System.out.println("Improved strategy "+ strategy.getCondition() +"-"+strategy.getToEnforce());

                            }
                        }
                        if (strategy.getToEnforce().getActionName().toLowerCase().contains("minimize")) {
                            //	PlanningLogger.logger.info("Current value for "+ strategy.getToEnforce().getParameter()+" is "+ monitoredEntity.getMonitoredValue(strategy.getToEnforce().getParameter())+" .Previous value was "+previousContextRepresentation.getValueForMetric(monitoredEntity,strategy.getToEnforce().getParameter()));

                            if (currentContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter()) < previousContextRepresentation.getMetricValue(monitoredEntity, strategy.getToEnforce().getParameter())) {
                                nbFixedStrategies += 1;
                                System.out.println("Improved strategy "+ strategy.getCondition() +"-"+strategy.getToEnforce());
                                        
                            }
                        }
                    }
                }
            }
        }
        return nbFixedStrategies;
    }

    public boolean evaluateCondition(DependencyGraph dependencyGraph,Condition c, String monitoredEntity, ContextRepresentation contextRepresentation) {
        if (c == null) {
            return true;
        }

       
        boolean oneEvaluatedToTrueFound = false;

        for (BinaryRestrictionsConjunction restrictions : c.getBinaryRestriction()) {
            boolean value = true;
            for (BinaryRestriction binaryRestriction : restrictions.getBinaryRestrictions()) {
                if (!evaluateBinaryRestriction(binaryRestriction, monitoredEntity, contextRepresentation)) {
                    value = false;
                }
            }

            if (value == true) {
                oneEvaluatedToTrueFound = true;
            }
        }

        for (UnaryRestrictionsConjunction restrictions : c.getUnaryRestrictions()) {
            boolean value = true;
            for (UnaryRestriction unaryRestriction : restrictions.getUnaryRestrictions()) {
                if (!evaluateUnaryRestriction(dependencyGraph,unaryRestriction, monitoredEntity,contextRepresentation)) {
                    value = false;
                }
            }

            if (value == true) {
                oneEvaluatedToTrueFound = true;
            }
        }

        if (oneEvaluatedToTrueFound) {
            return true;
        } else {
            return false;
        }

    }
    public boolean evaluateUnaryRestriction(DependencyGraph dependencyGraph,UnaryRestriction unaryRestriction, String monitoredEntity, ContextRepresentation contextRepresentation) {
        if (unaryRestriction.getReferenceTo().getFunction().equalsIgnoreCase("fulfilled")) {
            if (getViolatedConstraints(dependencyGraph,contextRepresentation).contains(unaryRestriction.getReferenceTo().getName())) {
                return false;
            } else {
                return true;
            }
        } else {
            if (getViolatedConstraints(dependencyGraph,contextRepresentation).contains(unaryRestriction.getReferenceTo().getName())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public double quantifyCondition(DependencyGraph dependencyGraph,Condition c, String monitoredEntity, ContextRepresentation contextRepresentation) {
        if (c == null) {
            return 0;
        }

        if (monitoredEntity == null) {
            PlanningLogger.logger.info("Monitored entity is null ");
            return 0;
        }
        double evaluatedConstraints = 0;
        double binaryRestrictionNb = 0;
        for (BinaryRestrictionsConjunction restrictions : c.getBinaryRestriction()) {

            for (BinaryRestriction binaryRestriction : restrictions.getBinaryRestrictions()) {
                if (!evaluateBinaryRestriction(binaryRestriction, monitoredEntity,contextRepresentation)) {
                    evaluatedConstraints += quantifyBinaryRestriction(binaryRestriction, monitoredEntity,contextRepresentation);

                }
                binaryRestrictionNb++;
            }

        }

        for (UnaryRestrictionsConjunction restrictions : c.getUnaryRestrictions()) {

            for (UnaryRestriction unaryRestriction : restrictions.getUnaryRestrictions()) {
                if (!evaluateUnaryRestriction(dependencyGraph,unaryRestriction, monitoredEntity,contextRepresentation)) {
                    evaluatedConstraints += quantifyUnaryRestriction(dependencyGraph,unaryRestriction, contextRepresentation);
                }
                binaryRestrictionNb++;
            }


        }

        return evaluatedConstraints;

    }

    public double quantifyUnaryRestriction(DependencyGraph dependencyGraph,UnaryRestriction unaryRestriction,ContextRepresentation contextRepresentation) {
        if (unaryRestriction.getReferenceTo().getFunction().equalsIgnoreCase("fulfilled")) {
            if (getViolatedConstraints(dependencyGraph,contextRepresentation).contains(unaryRestriction.getReferenceTo().getName())) {
                return 1;
            } else {
                return 0;
            }
        } else {
            if (getViolatedConstraints(dependencyGraph,contextRepresentation).contains(unaryRestriction.getReferenceTo().getName())) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    


    public int countViolatedConstraints(DependencyGraph dependencyGraph, ContextRepresentation contextRepresentation) {
        int numberofViolatedConstraints = 0;
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
            String monitoredEntity = syblSpecification.getComponentId();
            if (monitoredEntity == null) {
                PlanningLogger.logger.info("Not finding monitored entity " + monitoredEntity + " " + syblSpecification.getComponentId());
            }

            for (Constraint constraint : syblSpecification.getConstraint()) {
                if (evaluateCondition(dependencyGraph,constraint.getCondition(), monitoredEntity,contextRepresentation) && !evaluateCondition(dependencyGraph,constraint.getToEnforce(), monitoredEntity,contextRepresentation)) {
                    numberofViolatedConstraints = numberofViolatedConstraints + 1;
                }

            }
        }
        //PlanningLogger.logger.info("Number of violated constraints"+ numberofViolatedConstraints);
        return numberofViolatedConstraints;
    }

    public int evaluateViolationPercentage(DependencyGraph dependencyGraph, ContextRepresentation contextRepresentation) {
        int numberofViolatedConstraints = 0;
        int nbConstraints = 0;
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
            String monitoredEntity = syblSpecification.getComponentId();
            if (monitoredEntity == null) {
                PlanningLogger.logger.info("Not finding monitored entity " + monitoredEntity + " " + syblSpecification.getComponentId());
            }

            for (Constraint constraint : syblSpecification.getConstraint()) {
                if (evaluateCondition(dependencyGraph,constraint.getCondition(), monitoredEntity,contextRepresentation) && !evaluateCondition(dependencyGraph,constraint.getToEnforce(), monitoredEntity,contextRepresentation)) {
                    numberofViolatedConstraints = numberofViolatedConstraints + 1;
                }
                nbConstraints += 1;
            }
        }
        //PlanningLogger.logger.info("Number of violated constraints"+ numberofViolatedConstraints);
        return numberofViolatedConstraints / nbConstraints;
    }

    public double evaluateViolationDegree(DependencyGraph dependencyGraph, ContextRepresentation currentContextRepresentation) {
        int numberofViolatedConstraints = 0;
        int nbConstraints = 0;
        double violationDegree = 0.0;
        for (ElasticityRequirement elReq : dependencyGraph.getAllElasticityRequirements()) {
            SYBLSpecification syblSpecification = SYBLDirectiveMappingFromXML.mapFromSYBLAnnotation(elReq.getAnnotation());
            //System.out.println("Searching for monitored entity "+syblSpecification.getComponentId());
                String monitoredEntity  = syblSpecification.getComponentId();

            for (Constraint constraint : syblSpecification.getConstraint()) {
                
                if (evaluateCondition(dependencyGraph,constraint.getCondition(), monitoredEntity,currentContextRepresentation) && !evaluateCondition(dependencyGraph,constraint.getToEnforce(), monitoredEntity,currentContextRepresentation)) {
                    numberofViolatedConstraints = numberofViolatedConstraints + 1;
                    for (int i = 0; i < constraint.getToEnforce().getBinaryRestriction().size(); i++) {
                        BinaryRestrictionsConjunction binaryRestrictionConjunction = constraint.getToEnforce().getBinaryRestriction().get(i);
                        for (BinaryRestriction binaryRestriction : binaryRestrictionConjunction.getBinaryRestrictions()) {
                            if (binaryRestriction.getLeftHandSide().getMetric() != null && !binaryRestriction.getLeftHandSide().getMetric().equalsIgnoreCase("")) {
                                String metric = binaryRestriction.getLeftHandSide().getMetric();
                                double val = currentContextRepresentation.getMetricValue(monitoredEntity,metric);
                                double desiredVal = Double.parseDouble(binaryRestriction.getRightHandSide().getNumber());
                                switch (binaryRestriction.getType()) {
                                    case "lessThan":
                                        if (val >= desiredVal) {
                                            violationDegree += (val - desiredVal) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "greaterThan":
                                        if (val <= desiredVal) {
                                            violationDegree += (desiredVal - val) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "lessThanOrEqual":
                                        if (val > desiredVal) {
                                            violationDegree += (val - desiredVal) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "greaterThanOrEqual":
                                        if (val < desiredVal) {
                                            violationDegree += (desiredVal - val) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "differentThan":
                                        if (val == desiredVal) {
                                            violationDegree += 1;
                                        }
                                        ;
                                        break;
                                    case "equals":
                                        if (val != desiredVal) {
                                            violationDegree += 1;
                                        }
                                        ;
                                        break;
                                    default:
                                        if (val >= desiredVal) {
                                            violationDegree += (val - desiredVal) / desiredVal;
                                        }
                                        ;
                                        break;
                                }

                            } else {
                                String metric = binaryRestriction.getRightHandSide().getMetric();
                                double val = currentContextRepresentation.getMetricValue(monitoredEntity, metric);
                                double desiredVal = Double.parseDouble(binaryRestriction.getLeftHandSide().getNumber());
                                switch (binaryRestriction.getType()) {
                                    case "lessThan":
                                        if (val >= desiredVal) {
                                            violationDegree += (val - desiredVal) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "greaterThan":
                                        if (val <= desiredVal) {
                                            violationDegree += (desiredVal - val) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "lessThanOrEqual":
                                        if (val > desiredVal) {
                                            violationDegree += (val - desiredVal) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "greaterThanOrEqual":
                                        if (val < desiredVal) {
                                            violationDegree += (desiredVal - val) / desiredVal;
                                        }
                                        ;
                                        break;
                                    case "differentThan":
                                        if (val == desiredVal) {
                                            violationDegree += 1;
                                        }
                                        ;
                                        break;
                                    case "equals":
                                        if (val != desiredVal) {
                                            violationDegree += 1;
                                        }
                                        ;
                                        break;
                                    default:
                                        if (val >= desiredVal) {
                                            violationDegree += (val - desiredVal) / desiredVal;
                                        }
                                        ;
                                        break;
                                }
                            }
                        }
                    }

                }
                nbConstraints += 1;
            }
        }
        //PlanningLogger.logger.info("Number of violated constraints"+ numberofViolatedConstraints);
        return violationDegree;
    }
}
