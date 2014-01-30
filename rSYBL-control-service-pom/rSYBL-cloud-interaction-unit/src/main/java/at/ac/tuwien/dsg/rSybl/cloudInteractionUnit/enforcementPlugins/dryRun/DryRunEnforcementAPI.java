/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */

package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.dryRun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;

public class DryRunEnforcementAPI implements EnforcementInterface{
	private Node controlledService;
	private EnforcementDryRun dryRun;
	public DryRunEnforcementAPI(Node cloudService){
		this.controlledService=cloudService;
		dryRun = new EnforcementDryRun();
	}
	@Override
	public void scaleOut(Node toBeScaled) {

		dryRun.enforceAction("Scale Out for "+toBeScaled.getId());
	}
	@Override
	public void scaleIn(Node toBeScaled) {
		dryRun.enforceAction("Scale In for "+toBeScaled.getId());
		
	}
	@Override
	public List<String> getElasticityCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void enforceAction(String actionName, Node entity) {
		dryRun.enforceAction("Action "+actionName+" for "+entity.getId());

		
	}
	@Override
	public void setControlledService(Node controlledService) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Node getControlledService() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
		// TODO Auto-generated method stub
		
	}





}
