package at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.ResourceBundle.Control;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.tosca.TOSCAProcessing;

public class ControlCoordination {
	private String currentControls="";
	private HashMap<String,ControlService> controls = new HashMap<String,ControlService>();
	public ControlCoordination(){
		
	}
	public void prepareControl(String cloudServiceId){
		ControlService controlService=new ControlService();
		controls.put(cloudServiceId, controlService);
		currentControls=cloudServiceId;
	}
	public void setApplicationDeploymentDescription(String deployment){
		controls.get(currentControls).setApplicationDeploymentDescriptionInfoCELAR(deployment);
	}
	public void setApplicationDescriptionInfo(String descriptionInfo){
		controls.get(currentControls).setApplicationDescriptionInfoCELAR(descriptionInfo);
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
