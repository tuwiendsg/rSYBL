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
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;

import java.util.ArrayList;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import java.util.List;

public interface EnforcementAPIInterface {

    public Node getControlledService();

    public List<String> getPluginsExecutingActions();

    public void setControlledService(Node controlledService);
    public void submitElasticityRequirements(ArrayList<ElasticityRequirement> description);
    
    public boolean scalein(Node arg0);
    public void undeployService(Node service);
    public void setMonitoringPlugin(MonitoringAPIInterface monitoringInterface);

    public boolean scaleout(Node arg0);
 public boolean scaleout(double violationDegree,Node arg0);
    public boolean enforceAction(String actionName, Node e);
public boolean enforceAction(double violationDegree,String actionName, Node e);
    public boolean enforceElasticityCapability(ElasticityCapability capability, Node e);

    public boolean scalein(String target, Node arg0);

    public boolean scaleout(String target, Node arg0);
 public boolean scaleout(double violationDegree,String target, Node arg0);
    public boolean enforceAction(String target, String actionName, Node e);
    public boolean enforceAction(double violationDegree, String target, String actionName, Node e);

    public boolean enforceElasticityCapability(String target, ElasticityCapability capability, Node e);

    public boolean enforceAction(String target, String actionName, Node node, Object[] parameters);
    
    public boolean enforceAction(double violationDegree, String target, String actionName, Node node, Object[] parameters);
}
