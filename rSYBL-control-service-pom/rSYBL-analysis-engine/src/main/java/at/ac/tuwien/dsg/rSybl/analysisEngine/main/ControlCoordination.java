package at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.ResourceBundle.Control;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.tosca.TOSCAProcessing;
import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.AnalysisLogger;

public class ControlCoordination {
	private String currentControls="";
	private HashMap<String,ControlService> controls = new HashMap<String,ControlService>();
	public ControlCoordination(){
		
	}
	public void refreshApplicationDeploymentDescription(String deploymentNew){
		
	}
	public void prepareControl(String cloudServiceId){
		ControlService controlService=new ControlService();
		controls.put(cloudServiceId, controlService);
		currentControls=cloudServiceId;
	}
	public void setApplicationDeploymentDescription(String deployment){
		controls.get(currentControls).setApplicationDeployment(deployment);
	}
	public void setElasticityCapabilitiesEffects(String effects){
		AnalysisLogger.logger.info("Setting the effects for "+currentControls);
		controls.get(currentControls).setEffects(effects);
	}
	public void setMetricComposition(String composition){
		AnalysisLogger.logger.info("Setting the metric composition rules for "+currentControls);
		controls.get(currentControls).setMetricCompositionRules(composition);
	}
	
	public void setApplicationDescriptionInfo(String descriptionInfo){
		controls.get(currentControls).setApplicationDescriptionInfo(descriptionInfo);
	}
	public void startControl(String cloudServiceId){
		controls.get(cloudServiceId).start();
	}
	public void stopControl(String cloudServiceId){
		controls.get(cloudServiceId).stop();
	}
	
	public void processAnnotation(String serviceId,String entity,SYBLAnnotation annotation){
		try {
			controls.get(serviceId).processAnnotation(entity, annotation);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void replaceCloudServiceWithRequirements(String cloudServiceId,String cloudService){
		controls.get(cloudServiceId).replaceCloudServiceRequirements(cloudService);
	}
	public void replaceCloudServiceWithRequirements(String cloudService){
		controls.get(currentControls).replaceCloudServiceRequirements(cloudService);
	}
	public void replaceCompositionRules(String compositionRules){
		controls.get(currentControls).replaceCompositionRules(compositionRules);
	}
	public void replaceRequirements(String cloudServiceId,String requirements){
		controls.get(cloudServiceId).replaceElasticityRequirements(requirements);

	}
	public void replaceRequirements(String requirements){
		
		controls.get(currentControls).replaceElasticityRequirements(requirements);
	}
	public void setApplicationDescriptionInfoInternalModel(String applicationDescriptionXML, String elasticityRequirementsXML, String deploymentInfoXML){
		InputProcessing inputProcessing=new InputProcessing();
		DependencyGraph dependencyGraph=inputProcessing.loadDependencyGraphFromStrings(applicationDescriptionXML, elasticityRequirementsXML, deploymentInfoXML);
		ControlService controlService = new ControlService();
		controlService.setDependencyGraph(dependencyGraph);
		controls.put(dependencyGraph.getCloudService().getId(),controlService);
		
	}
	public void setAndStartToscaControl(String tosca){
		TOSCAProcessing inputProcessing=new TOSCAProcessing();
		DependencyGraph dependencyGraph=inputProcessing.toscaDescriptionToDependencyGraph(tosca);
		ControlService controlService = new ControlService();
		controlService.setDependencyGraph(dependencyGraph);
		controls.put(dependencyGraph.getCloudService().getId(),controlService);
		controls.get(dependencyGraph.getCloudService().getId()).start();
	}
}
