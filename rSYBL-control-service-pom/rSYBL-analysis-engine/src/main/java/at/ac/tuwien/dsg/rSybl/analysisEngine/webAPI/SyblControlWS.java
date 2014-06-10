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


package at.ac.tuwien.dsg.rSybl.analysisEngine.webAPI;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.resource.Singleton;


import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.rSybl.analysisEngine.main.ControlCoordination;
import at.ac.tuwien.dsg.rSybl.analysisEngine.main.ControlService;
import at.ac.tuwien.dsg.rSybl.analysisEngine.main.ControlServiceFactory;
import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.AnalysisLogger;


@Singleton
@Path("/")
public class SyblControlWS {
        @Context
        private UriInfo context;
	private ControlCoordination controlCoordination;
	
	public SyblControlWS(){
		controlCoordination=new ControlCoordination();
	}
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test(){
		return "Test working";
	}
	
	 @PUT
	 @Path("/processAnotation")
	 @Consumes("application/xml")
	public void processAnnotation(String serviceId,String entity,SYBLAnnotation annotation){
			controlCoordination.processAnnotation(serviceId,entity, annotation);
		
	}
	 @PUT
	 @Path("/serviceDescriptionInternalModel")
	 @Consumes("application/xml")
	public void setApplicationDescriptionInfoInternalModel(String applicationDescriptionXML, String elasticityRequirementsXML, String deploymentInfoXML){
		 controlCoordination.setApplicationDescriptionInfoInternalModel(applicationDescriptionXML, elasticityRequirementsXML, deploymentInfoXML);
	}
	 
	 @PUT
	 @Path("/TOSCADescriptionAndStartControl")
	 @Consumes("application/xml")
	public void setTOSCAAndStartControl(String tosca){
		 controlCoordination.setAndStartToscaControl(tosca);
		 
	}

	 @PUT
	 @Path("/serviceDescription")
	 @Consumes("application/xml")
	public void setApplicationDescriptionInfo(String celar){
		 controlCoordination.setApplicationDescriptionInfo(celar);
	}
	 @PUT
	 @Path("/elasticityCapabilitiesEffects")
	 @Consumes("application/xml")
	public void setElasticityCapabilitiesEffects(String effects){
		 controlCoordination.setElasticityCapabilitiesEffects(effects);
	}
	 
	 @PUT
	 @Path("/metricsCompositionRules")
	 @Consumes("application/xml")
	public void setMetricsComposition(String composition){
		 controlCoordination.setMetricComposition(composition);
	}
	 
	 @PUT
	 @Path("/serviceDeployment")
	 @Consumes("application/xml")
	public void setApplicationDeploymentInfoCELAR(String celar){
		 controlCoordination.setApplicationDeploymentDescription(celar);

	} 
	 @PUT
	 @Path("/prepareControl")
	 @Consumes("application/xml")
	public void prepareControl(String cloudServiceId){
		 controlCoordination.prepareControl(cloudServiceId);
	} 
	 @PUT
	 @Path("/startControl")
	 @Consumes("application/xml")
	public void startControl(String cloudServiceId){
		 controlCoordination.startControl(cloudServiceId);
	} 
	
	 @PUT
	 @Path("/stopControl")
	 @Consumes("application/xml")
	public void stopControl(String cloudServiceId){
		 controlCoordination.stopControl(cloudServiceId);
	}
	 
	

	public UriInfo getContext() {
		return context;
	}

	public void setContext(UriInfo context) {
		this.context = context;
	}
}
