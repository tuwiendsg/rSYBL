package at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.ResourceBundle.Control;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.tosca.TOSCAProcessing;
import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.AnalysisLogger;
import org.springframework.web.util.HtmlUtils;

public class ControlCoordination {

    private String currentControls = "";
    private HashMap<String, ControlService> controls = new HashMap<String, ControlService>();

    public ControlCoordination() {
    }

    public void refreshApplicationDeploymentDescription(String deploymentNew) {
    }

    public void triggerHealthFixServicePart(String servicePartID, String serviceID) {
        controls.get(serviceID).triggerHealthFix(servicePartID);
    }
    public void removeService(String cloudServiceId){
        if (!cloudServiceId.equalsIgnoreCase("") && controls.containsKey(cloudServiceId)){
           controls.get(cloudServiceId).removeFromMonitoring();
        controls.get(cloudServiceId).stop();
        
        controls.remove(cloudServiceId);
        
        }
    }
       
    public void undeployService(String cloudServiceId){
        if (!cloudServiceId.equalsIgnoreCase("") && controls.containsKey(cloudServiceId)){
            controls.get(cloudServiceId).undeployService();
        controls.get(cloudServiceId).stop();
        
        controls.remove(cloudServiceId);
        
        }
    }
    public void prepareControl(String cloudServiceId) {
        ControlService controlService = new ControlService();
        controls.put(cloudServiceId, controlService);
        currentControls = cloudServiceId;
    }

    public void replaceRequirements(String cloudServiceID, String newReqs) {
        controls.get(cloudServiceID).replaceElasticityRequirements(newReqs);
    }

    public void setApplicationDeploymentDescription(String deployment) {
        controls.get(currentControls).setApplicationDeployment(deployment);
    }

    public void setApplicationDeploymentDescription(String currentControls, String deployment) {
        controls.get(currentControls).setApplicationDeployment(deployment);
    }

    public void setElasticityCapabilitiesEffects(String effects) {
        //AnalysisLogger.logger.info("Setting the effects for "+currentControls);
        controls.get(currentControls).setEffects(effects);
    }

    public void setElasticityCapabilitiesEffects(String currentControls, String effects) {
        //AnalysisLogger.logger.info("Setting the effects for "+currentControls);
        controls.get(currentControls).setEffects(effects);
    }

    public void setMetricComposition(String currentControls, String composition) {
        //AnalysisLogger.logger.info("Setting the metric composition rules for "+currentControls);
        controls.get(currentControls).setMetricCompositionRules(composition);
    }

    public void setMetricComposition(String composition) {
        //AnalysisLogger.logger.info("Setting the metric composition rules for "+currentControls);
        controls.get(currentControls).setMetricCompositionRules(composition);
    }

    public void setApplicationDescriptionInfo(String descriptionInfo) {
        controls.get(currentControls).setApplicationDescriptionInfo(descriptionInfo);
    }

    public void setApplicationDescriptionInfo(String currentControls, String descriptionInfo) {
        controls.get(currentControls).setApplicationDescriptionInfo(descriptionInfo);
    }

    public void startControl(String cloudServiceId) {
        controls.get(cloudServiceId).start();
    }

    public void stopControl(String cloudServiceId) {
        controls.get(cloudServiceId).stop();
    }

    public void processAnnotation(String serviceId, String entity, SYBLAnnotation annotation) {
        try {
            controls.get(serviceId).processAnnotation(entity, annotation);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void replaceCloudServiceWithRequirements(String cloudServiceId, String cloudService) {
        controls.get(cloudServiceId).replaceCloudServiceRequirements(cloudService);
    }

    public void replaceCloudServiceWithRequirements(String cloudService) {
        controls.get(currentControls).replaceCloudServiceRequirements(cloudService);
    }

    public void replaceEffects(String id, String effects) {
        controls.get(currentControls).replaceEffects(effects);
    }

    public void replaceEffects(String effects) {
        controls.get(currentControls).replaceEffects(effects);
    }

    public void replaceCompositionRules(String compositionRules) {
        controls.get(currentControls).replaceCompositionRules(compositionRules);
    }

    public void replaceCompositionRules(String currentControls, String compositionRules) {
        controls.get(currentControls).replaceCompositionRules(compositionRules);
    }

    public void replaceRequirementsString(String cloudServiceId, String requirements) {
        requirements.replaceAll("<", "&lt;");
        requirements.replaceAll(">", "&gt;");
        controls.get(cloudServiceId).replaceRequirements(requirements);

    }
    public String getRequirements(String cloudServiceId){
        if (controls.containsKey(cloudServiceId))
        return controls.get(cloudServiceId).getXMLRequirements();
        else return "";
    }
    public void replaceRequirements(String requirements) {
        
        controls.get(currentControls).replaceElasticityRequirements(requirements);
    }

    public void setApplicationDescriptionInfoInternalModel(String applicationDescriptionXML, String elasticityRequirementsXML, String deploymentInfoXML) {
        InputProcessing inputProcessing = new InputProcessing();
        DependencyGraph dependencyGraph = inputProcessing.loadDependencyGraphFromStrings(applicationDescriptionXML, elasticityRequirementsXML, deploymentInfoXML);
        ControlService controlService = new ControlService();
        controlService.setDependencyGraph(dependencyGraph);
        controls.put(dependencyGraph.getCloudService().getId(), controlService);

    }

    public void setAndStartToscaControl(String tosca) {
        TOSCAProcessing inputProcessing = new TOSCAProcessing();
        DependencyGraph dependencyGraph = inputProcessing.toscaDescriptionToDependencyGraph(tosca);
        ControlService controlService = new ControlService();
        controlService.setDependencyGraph(dependencyGraph);
        controls.put(dependencyGraph.getCloudService().getId(), controlService);
        controls.get(dependencyGraph.getCloudService().getId()).start();
    }
 public boolean testEnforcementCapability(String serviceName, String enforcementName, String componentID){
      return controls.get(serviceName).testEnforcementCapability(enforcementName, componentID);
    }
    public boolean testEnforcementCapabilityOnPlugin(String serviceName,String target,String enforcementName, String componentID){
      return controls.get(serviceName).testEnforcementCapabilityOnPlugin(target,enforcementName, componentID);
    }
    public boolean setTESTState(String serviceName){
        if (controls.containsKey(serviceName)){
            controls.get(serviceName).setStateTEST();
            return true;
          }
        else
        {
            return false;
        }
    }
    public String getJSONStructureOfService(String id) {
        if (controls.containsKey(id)){
        return controls.get(id).getJSONStructureOfService();
        }else{
            return "";
        }
    }

    public String getApplicationDescriptionInfo(String id) {
        if (!id.equalsIgnoreCase("") && controls.size()>0)
            return controls.get(id).getApplicationDescriptionInfo();
        else return "";
    }
    public void setApplicationDescriptionInfoTOSCA(String tosca, String serviceID){
        
        controls.get(serviceID).setApplicationDescriptionInfoTOSCABased(tosca);
    }
    public String getServices() {
        String services = "";
        for (String serv : controls.keySet()) {
            services += serv + ",";
        }
        return services;
    }
}
