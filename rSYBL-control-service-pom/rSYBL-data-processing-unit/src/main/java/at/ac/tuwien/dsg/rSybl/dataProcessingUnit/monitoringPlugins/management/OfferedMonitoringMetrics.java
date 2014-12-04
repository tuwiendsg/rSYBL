/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. This work was partially supported by the European Commission in terms
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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.management;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;
import java.util.HashMap;

public class OfferedMonitoringMetrics implements MonitoringInterface {

    ManageMonitoringPlugins manageMonitoringPlugins = new ManageMonitoringPlugins();
    Node cloudService;

    public OfferedMonitoringMetrics(Node cloudService) {
        this.cloudService = cloudService;
    }

    public void callSpecializedMethod(Node method, Node parameter) {
    }

    public Double getCpuUsage(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getCpuUsage")) {
                result = methods.get(method).getCpuUsage(string);
            }
        }

        return result;
    }

    public Double getMemoryAvailable(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getMemoryAvailable")) {
                result = methods.get(method).getMemoryAvailable(string);
            }
        }

        return result;
    }

    public Double getMemorySize(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getMemorySize")) {
                result = methods.get(method).getMemorySize(string);
            }
        }

        return result;
    }

    public Double getMemoryUsage(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getMemoryUsage")) {
                result = methods.get(method).getMemoryUsage(string);
            }
        }

        return result;
    }

    public Double getDiskSize(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getDiskSize")) {
                result = methods.get(method).getDiskSize(string);
            }
        }

        return result;
    }

    public Double getDiskAvailable(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getDiskAvailable")) {
                result = methods.get(method).getDiskAvailable(string);
            }
        }

        return result;
    }

    public Double getDiskUsage(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getDiskUsage")) {
                result = methods.get(method).getDiskUsage(string);
            }
        }

        return result;
    }

    public Double getCPUSpeed(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getCPUSpeed")) {
                result = methods.get(method).getCPUSpeed(string);
            }
        }

        return result;
    }

    public Double getPkts(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getPkts")) {
                result = methods.get(method).getPkts(string);
            }
        }

        return result;
    }

    public Double getPktsIn(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getPktsIn")) {
                result = methods.get(method).getPktsIn(string);
            }
        }

        return result;
    }

    public Double getPktsOut(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getPktsOut")) {
                result = methods.get(method).getPktsOut(string);
            }
        }

        return result;
    }

    public Double getReadLatency(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getReadLatency")) {
                result = methods.get(method).getReadLatency(string);
            }
        }

        return result;
    }

    public Double getWriteLatency(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getWriteLatency")) {
                result = methods.get(method).getWriteLatency(string);
            }
        }

        return result;
    }

    public Double getReadCount(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getReadCount")) {
                result = methods.get(method).getReadCount(string);
            }
        }

        return result;
    }

    public Double getWriteCount(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getWriteCount")) {
                result = methods.get(method).getWriteCount(string);
            }
        }

        return result;
    }

    public Double getCostPerHour(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {

            if (method.getName().equalsIgnoreCase("getCostPerHour")) {
                result = methods.get(method).getCostPerHour(string);
            }
        }

        return result;
    }

    public Double getTotalCostSoFar(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getTotalCostSoFar")) {
                result = methods.get(method).getTotalCostSoFar(string);
            }
        }

        return result;
    }

    public List<String> getAvailableMetrics(Node node) {
        List<String> metricsAvailable = new ArrayList<String>();
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getAvailableMetrics")) {
                metricsAvailable = methods.get(method).getAvailableMetrics(node);
            }
        }
        return metricsAvailable;
    }

    public Double getMetricValue(String metricName, Node node) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getMetricValue")) {
                result = methods.get(method).getMetricValue(metricName, node);
            }
        }

        return result;
    }

    public void notifyControlActionStarted(String actionName, Node e) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("notifyControlActionStarted")) {
                methods.get(method).notifyControlActionStarted(actionName, e);
            }
        }


    }

    public void notifyControlActionEnded(String actionName, Node e) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("notifyControlActionEnded")) {
                methods.get(method).notifyControlActionEnded(actionName, e);
            }
        }
    }

    @Override
    public void submitServiceConfiguration(Node cloudService) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("submitServiceConfiguration")) {
                methods.get(method).submitServiceConfiguration(cloudService);
            }
        }

    }

    @Override
    public void submitCompositionRules(String composition) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("submitCompositionRules") && (method.getParameterTypes().length > 0)) {
                RuntimeLogger.logger.info("Setting composition rules on MELA from WS");
                methods.get(method).submitCompositionRules(composition);
            }
        }
    }

    @Override
    public void submitCompositionRules() {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("submitCompositionRules") && (method.getParameterTypes().length == 0)) {
                RuntimeLogger.logger.info("Setting composition rules on MELA from file");
                methods.get(method).submitCompositionRules();
            }
        }
    }

    @Override
    public void submitElasticityRequirements(
            ArrayList<ElasticityRequirement> description) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("submitElasticityRequirements")) {
                methods.get(method).submitElasticityRequirements(description);
            }
        }
    }

    @Override
    public Double getNumberInstances(Node string) {
        Double result = 0.0;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getNumberInstances")) {
                result = methods.get(method).getNumberInstances(string);
            }
        }
        return result;
    }

    @Override
    public void refreshServiceStructure(Node node) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("refreshServiceStructure")) {
                methods.get(method).refreshServiceStructure(node);
            }
        }
    }

    @Override
    public List<String> getOngoingActionID() {
        List<String> result = new ArrayList<String>();
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getNumberInstances")) {
                result = methods.get(method).getOngoingActionID();
            }
        }
        return result;

    }

    @Override
    public List<String> getOngoingActionNodeID() {
        List<String> result = new ArrayList<String>();
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getNumberInstances")) {
                result = methods.get(method).getOngoingActionNodeID();
            }
        }
        return result;
    }

    @Override
    public boolean checkIfMetricsValid(Node node) {
        boolean result = true;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("checkIfMetricsValid")) {
                result = methods.get(method).checkIfMetricsValid(node);
            }
        }
        return result;
    }

    @Override
    public void sendMessageToAnalysisService(String message) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("sendMessageToAnalysisService")) {
                methods.get(method).sendMessageToAnalysisService(message);
            }
        }
    }

    @Override
    public void sendControlIncapacityMessage(String message, List<ElasticityRequirement> cause) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("sendControlIncapacityMessage")) {
                methods.get(method).sendControlIncapacityMessage(message, cause);
            }
        }
    }

    @Override
    public void removeService(Node cloudService) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("removeService")) {
                methods.get(method).removeService(cloudService);
            }
        }
    }

    @Override
    public HashMap<String, ArrayList<Double>> getAllMonitoringInformation(Node n) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getAllMonitoringInformation")) {
                return methods.get(method).getAllMonitoringInformation(n);
            }
        }
        return new HashMap<String, ArrayList<Double>>();
    }

    @Override
    public HashMap<String, ArrayList<Double>> getAllMonitoringInformationOnPeriod(Node n, long time) {
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("getAllMonitoringInformationOnPeriod")) {
                return methods.get(method).getAllMonitoringInformationOnPeriod(n,time);
            }
        }
        return new HashMap<String, ArrayList<Double>>();
    }

    @Override
    public boolean isHealthy() {
        boolean result = true;
        Map<Method, MonitoringInterface> methods = manageMonitoringPlugins.getMethods(manageMonitoringPlugins.getAllPlugins(cloudService));
        for (Method method : methods.keySet()) {
            if (method.getName().equalsIgnoreCase("isHealthy")) {
                result = methods.get(method).isHealthy();
            }
        }
        return result;
    }
}
