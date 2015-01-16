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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import java.util.HashMap;

public interface MonitoringInterface {

    public List<MonitoringSnapshot> getAllMonitoringInformation();

    public List<MonitoringSnapshot> getAllMonitoringInformationOnPeriod(long time);

    public Double getCpuUsage(Node node);

    public void removeService(Node service);

    public Double getMemoryAvailable(Node node);

    public boolean isHealthy();

    public Double getMemorySize(Node node);

    public Double getMemoryUsage(Node node);

    public Double getDiskSize(Node node);

    public Double getDiskAvailable(Node node);

    public Double getDiskUsage(Node node);

    public List<MonitoringSnapshot> getAllMonitoringInformationFromTimestamp(long timestamp);

    public Double getCPUSpeed(Node node);

    public Double getPkts(Node node);

    public Double getPktsIn(Node node);

    public Double getPktsOut(Node node);

    public Double getReadLatency(Node node);

    public Double getWriteLatency(Node node);

    public Double getReadCount(Node node);

    public Double getCostPerHour(Node node);

    public Double getWriteCount(Node node);

    public Double getTotalCostSoFar(Node node);

    public List<String> getAvailableMetrics(Node node);

    public void submitServiceConfiguration(Node node);

    public void submitCompositionRules(String composition);

    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description);

    public void notifyControlActionStarted(String actionName, Node node);

    public void notifyControlActionEnded(String actionName, Node node);

    public Double getMetricValue(String metricName, Node node);

    public Double getNumberInstances(Node node);

    public void refreshServiceStructure(Node node);

    public List<String> getOngoingActionID();

    public List<String> getOngoingActionNodeID();

    public void submitCompositionRules();

    public boolean checkIfMetricsValid(Node node);

    public void sendMessageToAnalysisService(String message);

    public void sendControlIncapacityMessage(String message, List<ElasticityRequirement> cause);
}
