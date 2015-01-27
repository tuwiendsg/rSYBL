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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import java.util.HashMap;

public interface MonitoringAPIInterface {

    public void setCompositionRules(String compositionRules);

    public void removeService(Node service);

    public void setCompositionRules();

    public List<MonitoringSnapshot> getAllMonitoringInformation();

    public List<MonitoringSnapshot> getAllMonitoringInformationOnPeriod(long count);

    public void refreshCompositionRules();

    public boolean isHealthy();

    public Double getCurrentCPUSize(Node e) throws Exception;

    public List<MonitoringSnapshot> getAllMonitoringInformationFromTimestamp(long timestamp);

    public Double getCostPerHour(Node e) throws Exception;

    public Double getCurrentRAMSize(Node e) throws Exception;

    public Double getCurrentMemUsage(Node e) throws Exception;

    public Double getTotalCostSoFar(Node e) throws Exception;

    public Node getControlledService();

    public void setControlledService(Node controlledService);
    public void controlExistingCloudService(Node controlledService);
    public Double getCurrentReadLatency(Node e) throws Exception;

    public Double getCurrentReadCount(Node e) throws Exception;

    public Double getCurrentWriteLatency(Node e) throws Exception;

    public Double getCurrentWriteCount(Node e) throws Exception;

    public Double getMetricValue(String metricName, Node e) throws Exception;

    public Double getCurrentCPUUsage(Node e) throws Exception;

    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description);

    public Double getCurrentHDDSize(Node e) throws Exception;

    public void scaleinstarted(Node arg0);

    public void scaleinended(Node arg0);

    public void scaleoutstarted(Node arg0);

    public void scaleoutended(Node arg0);

    public Double getCurrentLatency(Node arg0) throws Exception;

    public Double getCurrentOperationCount(Node arg0) throws Exception;

    public Double getCurrentHDDUsage(Node e) throws Exception;

    public void enforcingActionStarted(String actionName, Node e);

    public void enforcingActionEnded(String actionName, Node e);

    public Double getNumberInstances(Node e) throws Exception;

    public void refreshServiceStructure(Node cloudService);

    public List<String> getAvailableMetrics(Node node);

    public List<String> getOngoingActionID();

    public List<String> getOngoingActionNodeID();

    public boolean checkIfMetricsValid(Node node);

    public boolean checkHealthy(Node node);

    public void sendMessageToAnalysisService(String message);

    public void sendControlIncapacityMessage(String message, List<ElasticityRequirement> cause);
}
