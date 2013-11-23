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

package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.openstack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

//import at.ac.tuwien.dsg.sybl.model.deploymentDescription.DeploymentDescription;
//import at.ac.tuwien.dsg.sybl.model.deploymentDescription.DeploymentUnit;



public class DefaultDataWritting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DefaultDataWritting().writeDefaultDeploymentData();
	}
	public void writeDefaultDeploymentData(){
//		   JAXBContext jc;
//		   try{
//			   jc = JAXBContext.newInstance(DeploymentDescription.class);
//			   Marshaller m = jc.createMarshaller();
//			   DeploymentDescription deploymentDescription = new DeploymentDescription();
//			   deploymentDescription.setCloudServiceID("CloudService");
//			   
//			   List<DeploymentUnit> deploymentUnits = new ArrayList<DeploymentUnit>();
//			   DeploymentUnit cassandraNode = new DeploymentUnit();
//			   cassandraNode.setServiceUnitID("CassandraNode");
//			   cassandraNode.setDefaultImage("1d8c91dc-b15c-4bfb-a733-6dbf6fa55b4e");
//			   cassandraNode.setDefaultFlavor("m1.tiny");
//			   DeploymentUnit cassandraController = new DeploymentUnit();
//			   cassandraController.setServiceUnitID("CassandraController");
//			   cassandraController.setDefaultImage("e2f10d3a-f471-4716-8f63-de7e1a425f0c");
//			   cassandraController.setDefaultFlavor("m1.tiny");
//			   DeploymentUnit webService = new DeploymentUnit();
//			   webService.setServiceUnitID("WebService");
//			   webService.setDefaultImage("fb6e50ab-f2c3-4089-8cd2-0cbfaba2d6b4");
//			   webService.setDefaultFlavor("m1.tiny");
//			   DeploymentUnit haProxy = new DeploymentUnit();
//			   haProxy.setServiceUnitID("LoadBalancer");
//			   haProxy.setDefaultImage("0f344e97-76ef-43ae-b868-6fce6bed0f77");
//			   haProxy.setDefaultFlavor("m1.tiny");
//			  deploymentUnits.add(haProxy);
//			  deploymentUnits.add(webService);
//			  deploymentUnits.add(cassandraController);
//			  deploymentUnits.add(cassandraNode);   
			   	  
//			   deploymentDescription.setDeployments(deploymentUnits);
//			   m.marshal(deploymentDescription, new File("newDeploymentDescription.xml"));
//				 
//		   }catch (JAXBException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}

}
