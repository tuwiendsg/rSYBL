package at.ac.tuwien.dsg.rsybl.controllercommunication;

/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184. * This work was partially supported by the European Commission in terms
 * of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;

import javax.xml.bind.JAXBContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class SYBLControlClient {

    protected String REST_API_URL = "http://localhost:8280/rSYBL/restWS";
    protected String compRules;

    public SYBLControlClient(String rsyblurl) {
        REST_API_URL = rsyblurl;
    }

    public void initialize() {
        try {
            String applicationID = "SCAN";
            String tosca = readFile("test.tosca", Charset.defaultCharset());
            String deployment = readFile("deployment_SCAN.xml", Charset.defaultCharset());
            String compositionRules = readFile("compositionRules.xml", Charset.defaultCharset());
            SYBLControlClient sYBLControlClient = new SYBLControlClient(REST_API_URL);
//            sYBLControlClient.prepareControl(applicationID);
//            sYBLControlClient.setApplicationDescription(applicationID, tosca);
//            sYBLControlClient.setApplicationDeployment(applicationID, deployment);
//            sYBLControlClient.startTest(applicationID);
//            sYBLControlClient.testElasticityCapability(applicationID, "cassandraSeedNode", "balance");

            sYBLControlClient.prepareControl(applicationID);

            sYBLControlClient.setApplicationDescription(applicationID, tosca);
            sYBLControlClient.setApplicationDeployment(applicationID, deployment);
            sYBLControlClient.setMetricsCompositionRules(applicationID, compositionRules);
            sYBLControlClient.startApplication(applicationID);

        } catch (IOException ex) {
            Logger.getLogger(SYBLControlClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void modifyAppDescription(String applicationID, String newAppDescription, String appDeployment, String effects) {
        stopApplication(applicationID);
        initialInstantiationLifecycle(applicationID, newAppDescription, appDeployment, effects, compRules);
    }

    public void initialInstantiationLifecycle(String applicationID, String appDescription, String appDeployment, String effects, String compRules) {
        this.compRules = compRules;
        prepareControl(applicationID);
        setApplicationDescription(applicationID, appDescription);
        setApplicationDeployment(applicationID, appDeployment);
        setElasticityCapabilitiesEffects(applicationID, effects);
        setMetricsCompositionRules(applicationID, compRules);
        startApplication(applicationID);
    }

    public void setApplicationDescription(String applicationID, String appDescription) {

        callPUT(appDescription, applicationID + "/description/tosca");

    }

    public void testElasticityCapability(String applicationID, String componentID, String elasticityCapability) {

        callPUT("", applicationID + "/" + componentID + "/testElasticityCapability/" + elasticityCapability);

    }

    public void startTest(String applicationID) {

        callPUT("", applicationID + "/startTEST");

    }

    public void testElasticityCapabilityWithPlugin(String applicationID, String componentID, String pluginID, String elasticityCapability) {

        callPUT("", applicationID + "/" + componentID + "/testElasticityCapability/" + pluginID + "/" + elasticityCapability);

    }

    public void setApplicationDeployment(String applicationID, String appDescription) {
        callPUT(appDescription, applicationID + "/deployment");

    }

    public void prepareControl(String id) {

        callPUT(id, id + "/prepareControl");

    }

    public void setMetricsCompositionRules(String id, String rules) {

        callPUT(rules, id + "/compositionRules");

    }

    private void callPUT(String body, String methodName) {

        Client c = Client.create();

        c.getProperties().put(
                ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        WebResource r = c.resource(REST_API_URL + "/" + methodName);
        String response = r.accept(
                MediaType.APPLICATION_XML_TYPE).
                header("Content-Type", "application/xml; charset=utf-8").
                header("Accept", "application/xml, multipart/related").
                put(String.class, body);
        System.out.println(response);
    }

    private void callDELETE(String body, String methodName) {

        Client c = Client.create();

        c.getProperties().put(
                ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        WebResource r = c.resource(REST_API_URL + "/" + methodName);
        String response = r.accept(
                MediaType.APPLICATION_XML_TYPE).
                header("Content-Type", "application/xml; charset=utf-8").
                header("Accept", "application/xml, multipart/related").
                delete(String.class, body);
        System.out.println(response);
    }

    private String callPOST(String body, String methodName) {
         Client c = Client.create();

        c.getProperties().put(
                ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        WebResource r = c.resource(REST_API_URL + "/" + methodName);
        String response = r.accept(
                MediaType.APPLICATION_XML_TYPE).
                header("Content-Type", "application/xml; charset=utf-8").
                header("Accept", "application/xml, multipart/related").
                post(String.class, body);
        return response;
        
            
    }
 private String callGET( String methodName) {
           Client c = Client.create();

        c.getProperties().put(
                ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        WebResource r = c.resource(REST_API_URL + "/" + methodName);
        String response = r.accept(
                MediaType.APPLICATION_XML_TYPE).
                header("Content-Type", "application/xml; charset=utf-8").
                header("Accept", "application/xml, multipart/related").
                get(String.class);
        return response;
    }
    public void startApplication(String applicationID) {
        callPUT("", applicationID + "/startControl");
    }

    public void stopApplication(String applicationID) {
        callPUT("", applicationID + "/stopControl");
    }

    public void removeApplicationFromControl(String applicationID) {
        callDELETE("", "managedService/" + applicationID);

    }

    public void setElasticityCapabilitiesEffects(String id, String effects) {

        callPUT(effects, id + "/elasticityCapabilitiesEffects");

    }

    public void undeployService(String id) {
        callDELETE("", id);
    }

    public void replaceCompositionRules(String id, String compositionRules) {
        callPOST(compositionRules, id + "/compositionRules");
    }

    public void replaceRequirements(String id, String requirements) {
        callPOST(requirements, id + "/description");
    }

    public void replaceEffects(String id, String effects) {
        callPOST(effects, id + "/elasticityCapabilitiesEffects");
    }

    public void startEnforcement(String id, String target, String capabilityID) {
        callPOST("", id + "/" + target + "/" + "/testElasticityCapability/" + capabilityID);

    }
    public String callGETMethod(String methodName, String reqType){
        try {
			
			// create HTTP Client
			HttpClient httpClient = HttpClientBuilder.create().build();
 
			// Create new getRequest with below mentioned URL
			HttpGet getRequest = new HttpGet(REST_API_URL+"/"+methodName);
 
			// Add additional header to getRequest which accepts application/xml data
			getRequest.addHeader("accept", reqType);
 
			// Execute your request and catch response
			HttpResponse response = httpClient.execute(getRequest);
 
			// Check for HTTP response code: 200 = success
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
 
			// Get-Capture Complete application/xml body response
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String o;
			System.out.println("============Output:============");
                        String output="";
			// Simply iterate through XML response and show on console.
			while ((o = br.readLine()) != null) {
				output+=o;
			}
                        return output;
 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "";
    }
    public String getServices(){
        return callGETMethod("elasticservices","text/plain");
                
    }
      public String getService(String id){
        return callGETMethod(id+"/description","application/xml");
                
    }
      public String getRequirements(String id){
          return callGETMethod(id+"/elasticityRequirements/plain","text/plain");
      }
    public void resumeControl(String id) {
        callPOST("", id + "/startControlOnExisting");

    }
}
