package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.salsa.common;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.salsa.EnforcementSALSAAPI;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.MELA_API;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.utils.RuntimeLogger;

import javax.ws.rs.core.UriBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class DefaultSalsaClient {

    private String REST_API_URL = "http://128.130.172.215:8080/salsa-engine/rest/services";
    private Node service;

    public DefaultSalsaClient(Node cloudService) {
        REST_API_URL = Configuration.getEnforcementServiceURL();
        service = cloudService;
    }
public void undeployService(Node serviceID){
    URL url = null;
        HttpURLConnection connection = null;
        boolean notConnected = true;
        while (notConnected) {
            try {
                RuntimeLogger.logger.info("Trying to connect to Salsa ...");
                url = new URL(REST_API_URL + "/" + serviceID.getId());
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/xml");
                //connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Accept", "text/plain");
                //write message body
            

                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        java.util.logging.Logger.getLogger(EnforcementSALSAAPI.class.getName()).log(java.util.logging.Level.SEVERE, line);
                    }
                }

                InputStream inputStream = connection.getInputStream();
                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        java.util.logging.Logger.getLogger(EnforcementSALSAAPI.class.getName()).log(java.util.logging.Level.SEVERE, line);
                    }
                }

                notConnected = false;
            } catch (Exception e) {
                //Logger.getLogger(MELA_API.class.getName()).log(Level.WARNING, "Trying to connect to MELA - failing ... . Retrying later");
                RuntimeLogger.logger.error("Failing to delete service" + e.getMessage());
                
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
}
    public boolean scaleIn(String toScale) {
        URL url = null;
        HttpURLConnection connection = null;
        try {

            url = new URL(REST_API_URL + "/" + service.getId() + "/vmnodes/" + toScale + "/scalein");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            //write message body
            OutputStream os = connection.getOutputStream();

            os.flush();
            os.close();

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.error(line);
                    return false;
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.info(line);
                   
                }
                
            }

        } catch (Exception e) {
            RuntimeLogger.logger.info(e.getMessage());
            return false;
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
        return true;
    }

    public String scaleOut(String toScale) {
        URL url = null;
        HttpURLConnection connection = null;
        try {

            url = new URL(REST_API_URL + "/" + service.getId() + "/nodes/" + toScale + "/scaleout");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Accept", "application/xml");

            //write message body
            OutputStream os = connection.getOutputStream();

            os.flush();
            os.close();

            InputStream errorStream = connection.getErrorStream();
            if (errorStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.error(line);
                }
            }

            InputStream inputStream = connection.getInputStream();
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    RuntimeLogger.logger.info(line);
                    return line;
                }
                
            }

        } catch (Exception e) {
            RuntimeLogger.logger.info(e.getMessage());
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }
 
}
