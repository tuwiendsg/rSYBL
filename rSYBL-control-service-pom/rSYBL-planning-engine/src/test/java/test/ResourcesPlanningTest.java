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
package test;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.MultipleEnforcementAPIs;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPI;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.resourcesLevelControl.ResourcesLevelControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Georgiana
 */
public class ResourcesPlanningTest {

    DependencyGraph dependencyGraph;
    MonitoringAPIInterface monitoringAPI = new MonitoringAPI();
    EnforcementAPIInterface enforcementAPI = new MultipleEnforcementAPIs();
    ResourcesLevelControl planning ;
    public ResourcesPlanningTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        InputProcessing inputProc = new InputProcessing();
	dependencyGraph=inputProc.loadDependencyGraphFromFile();
        enforcementAPI.setControlledService(dependencyGraph.getCloudService());
        monitoringAPI.setControlledService(dependencyGraph.getCloudService());
        
        
        planning = new ResourcesLevelControl(enforcementAPI, monitoringAPI, dependencyGraph);

    }
//    @Test
    public void testReadingEffectsFromFile(){
        planning.readResourceActionEffects();
    }
    @After
    public void tearDown() {
    }

}
