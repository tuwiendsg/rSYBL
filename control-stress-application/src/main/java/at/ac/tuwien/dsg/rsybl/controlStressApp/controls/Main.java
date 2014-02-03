package at.ac.tuwien.dsg.rsybl.controlStressApp.controls;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLElasticityRequirementsDescription;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.InputProcessing;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML.CloudServiceXML;
import at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.deploymentDescription.DeploymentDescription;



public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		FlexiantActions flexiantActions = new FlexiantActions();
//		System.out.println(flexiantActions.createNewServer("DataNode_new",
//				"4cfa6d4c-dc3d-3e82-be53-a42ce47a13cd", 2, 4));
		InputProcessing inputProcessing = new InputProcessing();
		JAXBContext jc;
		CloudServiceXML applicationDescription = null;
		DeploymentDescription deploymentDescription = null;
		try {
			jc = JAXBContext.newInstance(CloudServiceXML.class);
		
		Unmarshaller u = jc.createUnmarshaller();

		 applicationDescription = (CloudServiceXML)  u.unmarshal(new File(Configuration
				.getApplicationDescription()));
		//applicationDescription = element.getValue();

		

		JAXBContext jc1 = JAXBContext.newInstance(DeploymentDescription.class);
		Unmarshaller u1 = jc1.createUnmarshaller();

		deploymentDescription = (DeploymentDescription) u1.unmarshal(new File(Configuration
				.getDeploymentDescription()));

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		DependencyGraph dependencyGraph = inputProcessing.loadDependencyGraphFromObjects(applicationDescription, new SYBLElasticityRequirementsDescription(),
						deploymentDescription);
		System.out.println(dependencyGraph.graphToString());
	
		RandomControlGeneration controlGeneration = new RandomControlGeneration(
				dependencyGraph);

	}

}
