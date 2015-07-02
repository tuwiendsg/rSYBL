/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.governance;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.GovernanceScope;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 *
 * @author Georgiana
 */
public class GovOpsPlugin implements EnforcementInterface {

    private List<ElasticityCapability> elasticityCapabilities = new ArrayList<ElasticityCapability>();
    private Node controlledService;
    private MonitoringAPIInterface monitoring;
    private String REST_API_URL = "";

    public GovOpsPlugin() {

    }

    public GovOpsPlugin(Node service) {
        this.controlledService = service;
    }

    @Override
    public List<ElasticityCapability> getElasticityCapabilities() {
        return elasticityCapabilities;
    }

    @Override
    public void setControlledService(Node controlledService) {
        this.controlledService = controlledService;
        DependencyGraph dep = new DependencyGraph();
        dep.setCloudService(controlledService);
        elasticityCapabilities.addAll(dep.getAllElasticityCapabilities());
    }

    @Override
    public Node getControlledService() {
        return controlledService;
    }

    @Override
    public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
        this.monitoring = monitoring;

    }

    @Override
    public boolean containsElasticityCapability(Node entity, String capability) {
        for (ElasticityCapability cap : this.elasticityCapabilities) {
            if (cap.getName().equalsIgnoreCase(capability)) {
                return true;
            }
        }
        return false;
    }

    public String callPOSTMethod(String methodName, String reqType, String body) {
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(REST_API_URL + "/" + methodName);
            postRequest.addHeader("accept", reqType);
            HttpEntity entity = new ByteArrayEntity(body.getBytes("UTF-8"));
            postRequest.setEntity(entity);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                System.err.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + reqType + ":" + methodName);
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String o;
            System.out.println("============Output:============");
            String output = "";
            while ((o = br.readLine()) != null) {
                output += o;
            }
            return output;

        } catch (ClientProtocolException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    /*
     POST APIManager/governanceScope/setProcessProps/{procId}
     procId - process ID i.e. strategy ID
     requestBody - the parameters as JSON
     This is used to create a context for the strategy. This should be first invocation per strategy. After this you can invoke specific steps e.g., setProtocol(..)

     2. POST APIManager/governanceScope/invokeScope/{procId}/{query}/{capaId}/{method}
     procId - same as above 
     query - the QUERY provided in GOVERNANCE_SCOPE
     capaId and method are same as before (used to invoke a capability)
     requestBody - the parameters as JSON as we discussed
            
     */

    public boolean setProto(Node entity, String protocol, GovernanceScope governanceScope, String uncertainty) {
        System.err.println("Called method setProto with parameters protocol=" + protocol + " , uncertainty=" + uncertainty + " governanceScope=" + governanceScope);
        String strategyUncertainty = uncertainty.replace("=", ":").replace("AND", ",");
        String governanceUncertainty = governanceScope.getConsideringUncertainty().replace("=", ":").replace("AND", ",");
        String response = "";
        String response1 = "";
        try {
            response = callPOSTMethod("/governanceScope/setProcessProps/setProto", "application/json", governanceUncertainty);
            response1 = callPOSTMethod("/governanceScope/invokeScope/setProto" + "/" + governanceScope.getQuery() + "/" + "cChangeProto" + "/change?proto=" + protocol, "application/json", strategyUncertainty);
        } catch (Exception e) {
            RuntimeLogger.logger.error(e.getCause());
            return false;
        }
        System.out.println(response);
        System.out.println(response1);
        return true;
    }

    public boolean setProto(Node entity, String protocol, GovernanceScope governanceScope) {
        System.err.println("Called method setProto with parameters protocol=" + protocol + " governanceScope=" + governanceScope);
        String governanceUncertainty = governanceScope.getConsideringUncertainty().replace("=", ":").replace("AND", ",");
        String response = "";
        String response1 = "";
        try {
            response = callPOSTMethod("/governanceScope/setProcessProps/setProto", "application/json", governanceUncertainty);
            response1 = callPOSTMethod("/governanceScope/invokeScope/setProto" + "/" + governanceScope.getQuery() + "/" + "cChangeProto" + "/change?proto=" + protocol, "application/json", "");
        } catch (Exception e) {
            RuntimeLogger.logger.error(e.getCause());
            return false;
        }
        System.out.println(response);
        System.out.println(response1);
        return true;

    }

    public boolean updatePollRate(Node entity, String pollRate, GovernanceScope governanceScope, String uncertainty) {
        System.err.println("Called method update poll rate with parameters pollRate=" + pollRate + " , uncertainty=" + uncertainty + " governanceScope=" + governanceScope);
        String strategyUncertainty = uncertainty.replace("=", ":").replace("AND", ",");
        String governanceUncertainty = governanceScope.getConsideringUncertainty().replace("=", ":").replace("AND", ",");
        String response = "";
        String response1 = "";
        try {
            response = callPOSTMethod("/governanceScope/setProcessProps/setSensorRate", "application/json", governanceUncertainty);
            response1 = callPOSTMethod("/governanceScope/invokeScope/setSensorRate" + "/" + governanceScope.getQuery() + "/" + "cChangeSensorRate" + "/update?interval=" + pollRate, "application/json", strategyUncertainty);
        } catch (Exception e) {
            RuntimeLogger.logger.error(e.getCause());
            return false;
        }
        System.out.println(response);
        System.out.println(response1);
        return true;
    }

    public boolean updatePollRate(Node entity, String pollRate, GovernanceScope governanceScope) {
        System.err.println("Called method update poll rate with parameters pollRate=" + pollRate + " governanceScope=" + governanceScope);
        String governanceUncertainty = governanceScope.getConsideringUncertainty().replace("=", ":").replace("AND", ",");
        String response = "";
        String response1 = "";
        try {
            response = callPOSTMethod("/governanceScope/setProcessProps/setSensorRate", "application/json", governanceUncertainty);
            response1 = callPOSTMethod("/governanceScope/invokeScope/setSensorRate" + "/" + governanceScope.getQuery() + "/" + "cChangeSensorRate" + "/update?interval=" + pollRate, "application/json", "");
        } catch (Exception e) {
            RuntimeLogger.logger.error(e.getCause());
            return false;
        }
        System.out.println(response);
        System.out.println(response1);
        return true;
    }

}
