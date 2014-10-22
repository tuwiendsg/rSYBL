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
import javax.ws.rs.POST;
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
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;


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
        @Path("/{id}/onDemandControl/unhealthy")
        @Consumes("plain/txt")
        public void checkUnhealthyState(String servicePartID,@PathParam("id")String id){
            controlCoordination.triggerHealthFixServicePart(servicePartID, servicePartID);
        }
	
	 @PUT
	 @Path("/processAnotation")
	 @Consumes("application/xml")
	public void processAnnotation(String serviceId,String entity,SYBLAnnotation annotation){
			controlCoordination.processAnnotation(serviceId,entity, annotation);
		
	}
	 @PUT
	 @Path("/descriptionInternalModel")
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
	 @Path("/{id}/description/tosca")
	 @Consumes("application/xml")
	public void setApplicationDescriptionTOSCA(@PathParam("id")String cloudServiceId,String celar){
		 controlCoordination.setApplicationDescriptionInfoTOSCA(celar,cloudServiceId);
	}
           @PUT
	 @Path("/{id}/startTEST")
	 @Consumes("application/xml")
	public Response startTest(@PathParam("id")String cloudServiceId){
                try{
		 controlCoordination.setTESTState(cloudServiceId);
                 return Response.ok().build();
                }catch(Exception e){
                    return Response.serverError().entity(e).build();
                }
	}
                 @PUT
	 @Path("/{id}/{componentID}/testElasticityCapability/{capabilityID}")
	 @Consumes("application/xml")
	public Response startElasticityCapability(@PathParam("id")String cloudServiceId,@PathParam("componentID")String componentID,@PathParam("capabilityID") String capabilityID){
                try{

		 if (controlCoordination.testEnforcementCapability(cloudServiceId,capabilityID,componentID)){
                 return Response.ok().build();}
                 else{ 

                     return Response.status(ClientResponse.Status.CONFLICT).build();
                 }
                }catch(Exception e){
                    return Response.serverError().entity(e).build();
                }
	}
                     @PUT
	 @Path("/{id}/{componentID}/testElasticityCapability/{pluginID}/{capabilityID}")
	 @Consumes("application/xml")
	public Response startElasticityCapabilityWithPlugin(@PathParam("id")String cloudServiceId,@PathParam("componentID")String componentID,@PathParam("pluginID")String pluginID,@PathParam("capabilityID") String capabilityID){
                try{
		 if (controlCoordination.testEnforcementCapabilityOnPlugin(cloudServiceId,pluginID,capabilityID,componentID)){
                 return Response.ok().build();}
                 else{ 

                     return Response.status(ClientResponse.Status.CONFLICT).build();
                 }
                }catch(Exception e){
                    return Response.serverError().entity(e).build();
                }
	}   
             @DELETE
	 @Path("/{id}")
	 @Consumes("application/xml")
	public void undeployService(@PathParam("id")String cloudServiceId){
		 controlCoordination.undeployService(cloudServiceId);
	}
             @DELETE
	 @Path("/managedService/{id}")
	 @Consumes("application/xml")
	public void removeServiceFromControl(@PathParam("id")String cloudServiceId){
		 controlCoordination.removeService(cloudServiceId);
	}  
	 @PUT
	 @Path("/{id}/description")
	 @Consumes("application/xml")
	public void setApplicationDescriptionInfo(@PathParam("id")String cloudServiceId,String celar){
		 controlCoordination.setApplicationDescriptionInfo(cloudServiceId,celar);
	}
          @GET
	 @Path("/{id}/description")
	 @Produces("application/xml")
	public String getApplicationDescriptionInfo(@PathParam("id")String cloudServiceId){
		 return controlCoordination.getApplicationDescriptionInfo(cloudServiceId);
	}
          
	 @PUT
	 @Path("/{id}/elasticityCapabilitiesEffects")
	 @Consumes("application/json")
	public void setElasticityCapabilitiesEffects(@PathParam("id")String cloudServiceId,String effects){
		 controlCoordination.setElasticityCapabilitiesEffects(effects);
	}
	 
	 @PUT
	 @Path("/{id}/compositionRules")
	 @Consumes("application/xml")
	public void setMetricsComposition(@PathParam("id")String cloudServiceId,String composition){
		 controlCoordination.setMetricComposition(cloudServiceId,composition);
	}
	 
	 @PUT
	 @Path("/{id}/deployment")
	 @Consumes("application/xml")
	public void setApplicationDeploymentInfoCELAR(@PathParam("id")String cloudServiceId,String celar){
		 controlCoordination.setApplicationDeploymentDescription(cloudServiceId,celar);

	} 
	 
	 
	 @POST
	 @Path("/{id}/deployment")
	 @Consumes("application/xml")
	public void setApplicationRefreshDeploymentInfo(@PathParam("id")String cloudServiceId,String description){
		 controlCoordination.refreshApplicationDeploymentDescription(description);

	} 
	 
	 @PUT
	 @Path("/{id}/prepareControl")
	 @Consumes("application/xml")
	public void prepareControl(@PathParam("id")String cloudServiceId){
		 controlCoordination.prepareControl(cloudServiceId);
	} 
	 @PUT
	 @Path("/{id}/startControl")
	 @Consumes("application/xml")
	public void startControl(@PathParam("id")String cloudServiceId){
		 controlCoordination.startControl(cloudServiceId);
	} 
	
	 @PUT
	 @Path("/{id}/stopControl")
	 @Consumes("application/xml")
	public void stopControl(@PathParam("id")String cloudServiceId){
		 controlCoordination.stopControl(cloudServiceId);
	}
	 
	 @POST
	 @Path("/{id}/description")
	 @Consumes("application/xml")
	public void replaceCloudService(@PathParam("id")String cloudServiceId,String cloudService){
		 controlCoordination.replaceCloudServiceWithRequirements(cloudServiceId, cloudService);
	}

	
	 @POST
	 @Path("/{id}/compositionRules")
	 @Consumes("application/xml")
	public void replaceCompositionRules(@PathParam("id")String cloudServiceId,String composition){
		 controlCoordination.replaceCompositionRules(cloudServiceId,composition);
	}
	 @POST
	 @Path("/{id}/elasticityRequirements/xml")
	 @Consumes("application/xml")
	public void replaceRequirements(@PathParam("id")String cloudServiceId,String requirements){
		controlCoordination.replaceRequirements(cloudServiceId, requirements); 
	}
         
         @GET
	 @Path("/{id}/elasticityRequirements/xml")
	 @Produces("application/xml")
	public String getXMLRequirements(@PathParam("id")String cloudServiceId){
		return controlCoordination.getRequirements(cloudServiceId); 
	}

          @POST
	 @Path("/{id}/elasticityCapabilitiesEffects")
	 @Consumes("application/json")
	public void replaceEffects(@PathParam("id") String id,String effects){
		 controlCoordination.replaceEffects(id,effects);
	}
         
        @GET
        @Produces(MediaType.APPLICATION_JSON)
	 @Path("/{id}/structuralData/json")
	public String getStructuralData(@PathParam("id") String id){
            return controlCoordination.getJSONStructureOfService(id);
	}
        @GET
        @Produces(MediaType.TEXT_PLAIN)
	 @Path("/elasticservices")
	public String getServices(){
            return controlCoordination.getServices();
	}
        
  
        @POST
        @Consumes(MediaType.TEXT_PLAIN)
	 @Path("/{id}/replaceRequirements/plain")
	public void replaceRequirementsString(@PathParam("id") String id,String requirement){
          controlCoordination.replaceRequirementsString(id,requirement);
	}
        
	public UriInfo getContext() {
		return context;
	}

	public void setContext(UriInfo context) {
		this.context = context;
	}
}
