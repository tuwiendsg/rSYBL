/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.governance;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.GovernanceScope;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
/**
 *
 * @author Georgiana
 */
public class GovOpsPlugin implements EnforcementInterface{

	private List<ElasticityCapability> elasticityCapabilities = new ArrayList<ElasticityCapability>();
	private Node controlledService ;
	private MonitoringAPIInterface monitoring;
	public GovOpsPlugin(){
		
	}
        public GovOpsPlugin(Node service){
		this.controlledService=service;
	}
	@Override
	public List<ElasticityCapability> getElasticityCapabilities() {
		return elasticityCapabilities;
	}



	@Override
	public void setControlledService(Node controlledService) {
		this.controlledService=controlledService;
		DependencyGraph dep = new DependencyGraph();
		dep.setCloudService(controlledService);
		elasticityCapabilities.addAll(dep.getAllElasticityCapabilities());
	}

	@Override
	public Node getControlledService() {
		return controlledService;
	}

	@Override
	public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
		this.monitoring=monitoring;
		
	}

	@Override
	public boolean containsElasticityCapability(Node entity, String capability) {
		for (ElasticityCapability cap:this.elasticityCapabilities){
			if (cap.getName().equalsIgnoreCase(capability)){
				return true;
			}
		}
		return false;
	}

	public boolean setProto(Node entity, String protocol,GovernanceScope governanceScope,String uncertainty ){
		System.err.println("Called method setProto with parameters protocol="+protocol+" , uncertainty="+uncertainty+" governanceScope="+governanceScope);
		
		return false;
	}
	public boolean setProto(Node entity, String protocol,GovernanceScope governanceScope ){
		System.err.println("Called method setProto with parameters protocol="+protocol+" governanceScope="+governanceScope);
		
		return false;
	}
	public boolean updatePollRate(Node entity,String pollRate, GovernanceScope governanceScope, String uncertainty){
		System.err.println("Called method update poll rate with parameters pollRate="+pollRate+" , uncertainty="+uncertainty+" governanceScope="+governanceScope);

		return false;
	}
        public boolean updatePollRate(Node entity,String pollRate,  GovernanceScope governanceScope){
		System.err.println("Called method update poll rate with parameters pollRate="+pollRate+" governanceScope="+governanceScope);

		return false;
	}

    
}
