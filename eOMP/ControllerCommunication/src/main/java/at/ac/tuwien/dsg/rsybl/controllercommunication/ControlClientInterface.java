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
