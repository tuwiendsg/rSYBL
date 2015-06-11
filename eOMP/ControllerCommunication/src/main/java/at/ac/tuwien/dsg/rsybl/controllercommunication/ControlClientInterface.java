/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rsybl.controllercommunication;

/**
 *
 * @author Georgiana
 */
public interface ControlClientInterface {

    public void initialize() ;

    
    public void modifyAppDescription(String applicationID, String newAppDescription, String appDeployment, String effects) ;

    public void initialInstantiationLifecycle(String applicationID, String appDescription, String appDeployment, String effects, String compRules) ;

    public void setApplicationDescription(String applicationID, String appDescription) ;

    public void testElasticityCapability(String applicationID, String componentID, String elasticityCapability) ;
    public void startTest(String applicationID) ;
    public void testElasticityCapabilityWithPlugin(String applicationID, String componentID, String pluginID, String elasticityCapability);

    public void setApplicationDeployment(String applicationID, String appDescription) ;
    public void prepareControl(String id) ;

    public void setMetricsCompositionRules(String id, String rules) ;
    public void startApplication(String applicationID) ;
    public void stopApplication(String applicationID) ;

    public void removeApplicationFromControl(String applicationID) ;

    public void setElasticityCapabilitiesEffects(String id, String effects) ;
    public void undeployService(String id) ;
    public void replaceCompositionRules(String id, String compositionRules) ;

    public void replaceRequirements(String id, String requirements) ;

    public void replaceEffects(String id, String effects) ;

    public void startEnforcement(String id, String target, String capabilityID) ;


    public String getServices() ;

    public String getService(String id) ;

    public String getRequirements(String id) ;

    public void resumeControl(String id) ;
}
