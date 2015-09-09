/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184.  *
 * This work was partially supported by the European Commission in terms of the
 * CELAR FP7 project (FP7-ICT-2011-8 #317790).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
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

    public SyblControlWS() {
        controlCoordination = new ControlCoordination();
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test working";
    }

    @PUT
    @Path("/{id}/onDemandControl/unhealthy")
    @Consumes("plain/txt")
    public Response checkUnhealthyState(String servicePartID, @PathParam("id") String id) {
        try {
            controlCoordination.triggerHealthFixServicePart(servicePartID, servicePartID);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/processAnotation")
    @Consumes("application/xml")
    public Response processAnnotation(String serviceId, String entity, SYBLAnnotation annotation) {
        try {
            controlCoordination.processAnnotation(serviceId, entity, annotation);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/descriptionInternalModel")
    @Consumes("application/xml")
    public Response setApplicationDescriptionInfoInternalModel(String applicationDescriptionXML, String elasticityRequirementsXML, String deploymentInfoXML) {
        try {
            controlCoordination.setApplicationDescriptionInfoInternalModel(applicationDescriptionXML, elasticityRequirementsXML, deploymentInfoXML);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/description/tosca")
    @Consumes("application/xml")
    public Response setApplicationDescriptionTOSCA(@PathParam("id") String cloudServiceId, String celar) {
        try {
            controlCoordination.setApplicationDescriptionInfoTOSCA(celar, cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/startTEST")
    @Consumes("application/xml")
    public Response startTest(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.setTESTState(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/{componentID}/testElasticityCapability/{capabilityID}")
    @Consumes("application/xml")
    public Response startElasticityCapability(@PathParam("id") String cloudServiceId, @PathParam("componentID") String componentID, @PathParam("capabilityID") String capabilityID) {
        try {

            if (controlCoordination.testEnforcementCapability(cloudServiceId, capabilityID, componentID)) {
                return Response.ok().build();
            } else {

                return Response.status(ClientResponse.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/{componentID}/testElasticityCapability/{pluginID}/{capabilityID}")
    @Consumes("application/xml")
    public Response startElasticityCapabilityWithPlugin(@PathParam("id") String cloudServiceId, @PathParam("componentID") String componentID, @PathParam("pluginID") String pluginID, @PathParam("capabilityID") String capabilityID) {
        try {
            if (controlCoordination.testEnforcementCapabilityOnPlugin(cloudServiceId, pluginID, capabilityID, componentID)) {
                return Response.ok().build();
            } else {

                return Response.status(ClientResponse.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes("application/xml")
    public Response undeployService(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.undeployService(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @DELETE
    @Path("/managedService/{id}")
    @Consumes("application/xml")
    public Response removeServiceFromControl(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.removeService(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/description")
    @Consumes("application/xml")
    public Response setApplicationDescriptionInfo(@PathParam("id") String cloudServiceId, String celar) {
        try {
            controlCoordination.setApplicationDescriptionInfo(cloudServiceId, celar);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @GET
    @Path("/{id}/description")
    @Produces("application/xml")
    public String getApplicationDescriptionInfo(@PathParam("id") String cloudServiceId) {
        return controlCoordination.getApplicationDescriptionInfo(cloudServiceId);
    }

    @PUT
    @Path("/{id}/elasticityCapabilitiesEffects")
    @Consumes("application/json")
    public Response setElasticityCapabilitiesEffects(@PathParam("id") String cloudServiceId, String effects) {
        try {
            controlCoordination.setElasticityCapabilitiesEffects(effects);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/compositionRules")
    @Consumes("application/xml")
    public Response setMetricsComposition(@PathParam("id") String cloudServiceId, String composition) {
        try {
            controlCoordination.setMetricComposition(cloudServiceId, composition);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/deployment")
    @Consumes("application/xml")
    public Response setApplicationDeploymentInfoCELAR(@PathParam("id") String cloudServiceId, String celar) {
        try {
            controlCoordination.setApplicationDeploymentDescription(cloudServiceId, celar);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/{id}/deployment")
    @Consumes("application/xml")
    public Response setApplicationRefreshDeploymentInfo(@PathParam("id") String cloudServiceId, String description) {
        try {
            controlCoordination.refreshApplicationDeploymentDescription(description);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }

    }

    @PUT
    @Path("/{id}/prepareControl")
    @Consumes("application/xml")
    public Response prepareControl(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.prepareControl(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/startControl")
    @Consumes("application/xml")
    public Response startControl(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.startControl(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/startControlOnExisting")
    @Consumes("application/xml")
    public Response startControlOnExisting(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.startControlOnExisting(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @PUT
    @Path("/{id}/stopControl")
    @Consumes("application/xml")
    public Response stopControl(@PathParam("id") String cloudServiceId) {
        try {
            controlCoordination.stopControl(cloudServiceId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/{id}/description")
    @Consumes("application/xml")
    public Response replaceCloudService(@PathParam("id") String cloudServiceId, String cloudService) {
        try {
            controlCoordination.replaceCloudServiceWithRequirements(cloudServiceId, cloudService);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/{id}/compositionRules")
    @Consumes("application/xml")
    public Response replaceCompositionRules(@PathParam("id") String cloudServiceId, String composition) {
        try {
            controlCoordination.replaceCompositionRules(cloudServiceId, composition);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @POST
    @Path("/{id}/elasticityRequirements/xml")
    @Consumes("application/xml")
    public Response replaceRequirements(@PathParam("id") String cloudServiceId, String requirements) {
        try {
            controlCoordination.replaceRequirements(cloudServiceId, requirements);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @GET
    @Path("/{id}/elasticityRequirements/xml")
    @Produces("application/xml")
    public String getXMLRequirements(@PathParam("id") String cloudServiceId) {
        return controlCoordination.getRequirements(cloudServiceId);
    }

    @GET
    @Path("/{id}/elasticityRequirements/plain")
    @Produces("text/plain")
    public String getSYBLRequirements(@PathParam("id") String cloudServiceId) {
        return controlCoordination.getSimpleRequirements(cloudServiceId);
    }

    @POST
    @Path("/{id}/elasticityCapabilitiesEffects")
    @Consumes("application/json")
    public Response replaceEffects(@PathParam("id") String id, String effects) {
        try {
            controlCoordination.replaceEffects(id, effects);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/structuralData/json")
    public String getStructuralData(@PathParam("id") String id) {
        return controlCoordination.getJSONStructureOfService(id);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/elasticservices")
    public String getServices() {
        return controlCoordination.getServices();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("/{id}/replaceRequirements/plain")
    public Response replaceRequirementsString(@PathParam("id") String id, String requirement) {
        try {
            controlCoordination.replaceRequirementsString(id, requirement);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }

    public UriInfo getContext() {
        return context;
    }

    public Response setContext(UriInfo context) {
        try {
            this.context = context;
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e).build();
        }
    }
}
