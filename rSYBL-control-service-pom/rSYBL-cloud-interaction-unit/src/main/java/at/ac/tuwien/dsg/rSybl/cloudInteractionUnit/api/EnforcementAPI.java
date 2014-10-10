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
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.OfferedEnforcementCapabilities;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.MELA_API;


public class EnforcementAPI {

    private static HashMap<Node, ArrayList<Float>> avgRunningTimes = new HashMap<Node, ArrayList<Float>>();
    private boolean executingControlAction = false;
    private MonitoringAPIInterface monitoringAPIInterface;
    private Node controlledService;
    private EnforcementInterface offeredCapabilities;
    private String className;

    public EnforcementAPI() {
    }

    public boolean containsElasticityCapability(Node controlledService, String capability) {
        return offeredCapabilities.containsElasticityCapability(controlledService, capability);
    }

    public void setControlledService(Node controlledService, String className) {
        this.className = className;
        this.controlledService = controlledService;
        offeredCapabilities = OfferedEnforcementCapabilities.getInstance(
                className, this.controlledService);
    }

    public void setControlledService(Node controlledService) {
        this.controlledService = controlledService;
        offeredCapabilities = OfferedEnforcementCapabilities
                .getInstance(this.controlledService);
    }

    public void refreshControlService(Node cloudService) {
        controlledService = cloudService;
    }

    public boolean isExecutingControlAction() {
        return executingControlAction;
    }

    public boolean enforceAction(double violationDegree, String target, String actionName, Node node, Object[] parameters) {
        Method foundMethod = null;
        boolean res = false;
        try {
            for (Method method : Class.forName(className).getMethods()) {
                if (method.getName().toLowerCase()
                        .contains(actionName.toLowerCase())) {
                    foundMethod = method;

                }
            }

            if (foundMethod != null) {
                Class[] partypes = new Class[parameters.length + 2];
                Object[] myParameters = new Object[parameters.length + 2];
                partypes[0] = Double.class;
                myParameters[0] = violationDegree;

                partypes[1] = Node.class;
                myParameters[1] = node;
                int i = 2;
                for (Object o : parameters) {
                    partypes[i] = o.getClass();
                    myParameters[i] = o;
                    i += 1;

                }

                Method actionMethod;

                try {
                    actionMethod = Class.forName(className).getMethod(
                            foundMethod.getName(), partypes);

                    res = (boolean) actionMethod.invoke(offeredCapabilities,
                            myParameters);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    res = false;
                }

            } else {
                res = false;
                RuntimeLogger.logger.info("------------Method not found:> "
                        + foundMethod + " on " + target + " " + node
                        + " params " + parameters.length);

            }
            List<String> metrics = monitoringAPIInterface
                    .getAvailableMetrics(node);
            boolean checkIfMetrics = false;
            // monitoringAPIInterface.enforcingActionStarted("ScaleIn", arg0);
            while (!checkIfMetrics) {
                boolean myMetrics = true;
                RuntimeLogger.logger.info("Waiting for action....");
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                for (String metricName : metrics) {
                    try {
                        RuntimeLogger.logger.info("Metric "
                                + metricName
                                + " has value "
                                + monitoringAPIInterface.getMetricValue(
                                metricName, node));

                        if (monitoringAPIInterface.getMetricValue(metricName,
                                node) == null
                                || monitoringAPIInterface.getMetricValue(
                                metricName, node) < 0) {
                            myMetrics = false;
                            RuntimeLogger.logger.info("~~~~Metric "
                                    + metricName + "smaller than 0");
                        }
                    } catch (Exception e) {
                        myMetrics = false;
                        RuntimeLogger.logger.info("~~~~Metric " + metricName
                                + "not valid");

                    }

                }
                checkIfMetrics = myMetrics;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }

        } catch (SecurityException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            res = false;
        }

        return res;
    }

    public boolean enforceAction(String target, String actionName, Node node,
            Object[] parameters) {
        Method foundMethod = null;
        boolean res = false;
        try {
            for (Method method : Class.forName(className).getMethods()) {
                if (method.getName().toLowerCase()
                        .contains(actionName.toLowerCase())) {
                    foundMethod = method;

                }
            }

            if (foundMethod != null) {
                Class[] partypes = new Class[parameters.length + 1];
                Object[] myParameters = new Object[parameters.length + 1];
                partypes[0] = Node.class;
                myParameters[0] = node;
                int i = 1;
                for (Object o : parameters) {
                    partypes[i] = o.getClass();
                    myParameters[i] = o;
                    i += 1;

                }

                Method actionMethod;

                try {
                    actionMethod = Class.forName(className).getMethod(
                            foundMethod.getName(), partypes);

                    res = (boolean) actionMethod.invoke(offeredCapabilities,
                            myParameters);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    res = false;
                }

            } else {
                res = false;
                RuntimeLogger.logger.info("------------Method not found:> "
                        + foundMethod + " on " + target + " " + node
                        + " params " + parameters.length);

            }
            List<String> metrics = monitoringAPIInterface
                    .getAvailableMetrics(node);
            boolean checkIfMetrics = false;
            // monitoringAPIInterface.enforcingActionStarted("ScaleIn", arg0);
            while (!checkIfMetrics) {
                boolean myMetrics = true;
                RuntimeLogger.logger.info("Waiting for action....");
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                for (String metricName : metrics) {
                    try {
                        RuntimeLogger.logger.info("Metric "
                                + metricName
                                + " has value "
                                + monitoringAPIInterface.getMetricValue(
                                metricName, node));

                        if (monitoringAPIInterface.getMetricValue(metricName,
                                node) == null
                                || monitoringAPIInterface.getMetricValue(
                                metricName, node) < 0) {
                            myMetrics = false;
                            RuntimeLogger.logger.info("~~~~Metric "
                                    + metricName + "smaller than 0");
                        }
                    } catch (Exception e) {
                        myMetrics = false;
                        RuntimeLogger.logger.info("~~~~Metric " + metricName
                                + "not valid");

                    }

                }
                checkIfMetrics = myMetrics;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }

        } catch (SecurityException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            res = false;
        }

        return res;
    }

    public boolean scalein(Node arg0) {
        boolean res = false;
        if (arg0.getAllRelatedNodes().size() > 1) {


            res = offeredCapabilities.scaleIn(arg0);
            List<String> metrics = monitoringAPIInterface
                    .getAvailableMetrics(arg0);
            boolean checkIfMetrics = false;
            // monitoringAPIInterface.enforcingActionStarted("ScaleIn", arg0);
            while (!checkIfMetrics) {
                boolean myMetrics = true;
                RuntimeLogger.logger.info("Waiting for action....");
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                for (String metricName : metrics) {
                    try {
                        RuntimeLogger.logger.info("Metric "
                                + metricName
                                + " has value "
                                + monitoringAPIInterface.getMetricValue(
                                metricName, arg0));

                        if (monitoringAPIInterface.getMetricValue(metricName,
                                arg0) == null
                                || monitoringAPIInterface.getMetricValue(
                                metricName, arg0) < 0) {
                            myMetrics = false;
                            RuntimeLogger.logger.info("~~~~Metric "
                                    + metricName + "smaller than 0");
                        }
                    } catch (Exception e) {
                        myMetrics = false;
                        RuntimeLogger.logger.info("~~~~Metric " + metricName
                                + "not valid");

                    }

                }
                checkIfMetrics = myMetrics;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }

            // monitoringAPIInterface.enforcingActionEnded("ScaleIn", arg0);
            RuntimeLogger.logger.info("Finished scaling in " + arg0.getId()
                    + " ...");

        } else {
            res = false;
            RuntimeLogger.logger.info("Number of nodes associated with "
                    + arg0.getAllRelatedNodes().size());
        }
        return res;
    }

    public boolean scaleout(Node arg0) {
        boolean res = true;
        res = offeredCapabilities.scaleOut(arg0);
        List<String> metrics = monitoringAPIInterface.getAvailableMetrics(arg0);
        // monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
        boolean checkIfMetrics = false;
        while (!checkIfMetrics) {
            boolean myMetrics = true;

            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            RuntimeLogger.logger.info("Waiting for action....");
            for (String metricName : metrics) {
                try {
                    RuntimeLogger.logger.info("Metric "
                            + metricName
                            + " has value "
                            + monitoringAPIInterface.getMetricValue(metricName,
                            arg0));
                    if (monitoringAPIInterface.getMetricValue(metricName, arg0) == null
                            || monitoringAPIInterface.getMetricValue(
                            metricName, arg0) < 0) {
                        myMetrics = false;
                        RuntimeLogger.logger.info("~~~Metric " + metricName
                                + "smaller than 0");
                    }
                } catch (Exception e) {
                    RuntimeLogger.logger.info("~~~Metric " + metricName
                            + "does not have a valid value");

                }

            }
            checkIfMetrics = myMetrics;

            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();

            }
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();

        }
        // monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
        RuntimeLogger.logger.info("Finished scaling out " + arg0.getId()
                + " ...");
        return res;
    }

    public boolean scaleout(double violationDegree, Node arg0) {
        boolean res = true;
        res = offeredCapabilities.scaleOut(violationDegree, arg0);
        List<String> metrics = monitoringAPIInterface.getAvailableMetrics(arg0);
        // monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
        boolean checkIfMetrics = false;
        while (!checkIfMetrics) {
            boolean myMetrics = true;

            try {
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            RuntimeLogger.logger.info("Waiting for action....");
            for (String metricName : metrics) {
                try {
                    RuntimeLogger.logger.info("Metric "
                            + metricName
                            + " has value "
                            + monitoringAPIInterface.getMetricValue(metricName,
                            arg0));
                    if (monitoringAPIInterface.getMetricValue(metricName, arg0) == null
                            || monitoringAPIInterface.getMetricValue(
                            metricName, arg0) < 0) {
                        myMetrics = false;
                        RuntimeLogger.logger.info("~~~Metric " + metricName
                                + "smaller than 0");
                    }
                } catch (Exception e) {
                    RuntimeLogger.logger.info("~~~Metric " + metricName
                            + "does not have a valid value");

                }

            }
            checkIfMetrics = myMetrics;

            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();

            }
        }
        try {
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();

        }
        // monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
        RuntimeLogger.logger.info("Finished scaling out " + arg0.getId()
                + " ...");
        return res;
    }

    public boolean enforceAction(String actionName, Node e) {


        Method foundMethod = null;
        boolean res = false;
        try {
            for (Method method : Class.forName(className).getMethods()) {
                if (method.getName().toLowerCase()
                        .equalsIgnoreCase(actionName.toLowerCase())) {
                    foundMethod = method;

                }
            }

            if (foundMethod != null) {
                Class[] partypes = new Class[1];
                Object[] myParameters = new Object[1];
                partypes[0] = Node.class;
                myParameters[0] = e;
                int i = 1;


                Method actionMethod;

                try {
                    actionMethod = Class.forName(className).getMethod(
                            foundMethod.getName(), partypes);

                    res = (boolean) actionMethod.invoke(offeredCapabilities,
                            myParameters);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    res = false;
                }
                List<String> metrics = monitoringAPIInterface
                        .getAvailableMetrics(e);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

                boolean checkIfMetrics = false;
                while (!checkIfMetrics) {
                    boolean myMetrics = true;
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    RuntimeLogger.logger.info("Waiting for action....");

                    for (String metricName : metrics) {
                        try {
                            RuntimeLogger.logger.info("Metric "
                                    + metricName
                                    + " has value "
                                    + monitoringAPIInterface.getMetricValue(
                                    metricName, e));

                            if (monitoringAPIInterface.getMetricValue(metricName,
                                    e) == null
                                    || monitoringAPIInterface.getMetricValue(
                                    metricName, e) < 0) {
                                myMetrics = false;
                                RuntimeLogger.logger.info("~~~~Metric "
                                        + metricName + "smaller than 0");
                            }
                        } catch (Exception ex) {
                            myMetrics = false;
                            RuntimeLogger.logger.info("~~~~Metric " + metricName
                                    + "not valid");

                        }

                    }
                    checkIfMetrics = myMetrics;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }

                }
            } else {
                res = false;


            }
        } catch (SecurityException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            res = false;
        }

        return res;



    }

    public boolean enforceAction(double violationDegree, String actionName, Node e) {


        Method foundMethod = null;
        boolean res = false;
        try {
            for (Method method : Class.forName(className).getMethods()) {
                if (method.getName().toLowerCase()
                        .equalsIgnoreCase(actionName.toLowerCase())) {
                    foundMethod = method;

                }
            }

            if (foundMethod != null) {
                Class[] partypes = new Class[2];
                Object[] myParameters = new Object[2];
                partypes[0] = Double.class;
                myParameters[0] = violationDegree;

                partypes[1] = Node.class;
                myParameters[1] = e;
                int i = 2;


                Method actionMethod;

                try {
                    actionMethod = Class.forName(className).getMethod(
                            foundMethod.getName(), partypes);

                    res = (boolean) actionMethod.invoke(offeredCapabilities,
                            myParameters);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    res = false;
                }
                List<String> metrics = monitoringAPIInterface
                        .getAvailableMetrics(e);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

                boolean checkIfMetrics = false;
                while (!checkIfMetrics) {
                    boolean myMetrics = true;
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    RuntimeLogger.logger.info("Waiting for action....");

                    for (String metricName : metrics) {
                        try {
                            RuntimeLogger.logger.info("Metric "
                                    + metricName
                                    + " has value "
                                    + monitoringAPIInterface.getMetricValue(
                                    metricName, e));

                            if (monitoringAPIInterface.getMetricValue(metricName,
                                    e) == null
                                    || monitoringAPIInterface.getMetricValue(
                                    metricName, e) < 0) {
                                myMetrics = false;
                                RuntimeLogger.logger.info("~~~~Metric "
                                        + metricName + "smaller than 0");
                            }
                        } catch (Exception ex) {
                            myMetrics = false;
                            RuntimeLogger.logger.info("~~~~Metric " + metricName
                                    + "not valid");

                        }

                    }
                    checkIfMetrics = myMetrics;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }

                }
            } else {
                res = false;


            }
        } catch (SecurityException | ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            res = false;
        }

        return res;



    }

    public void setMonitoringPlugin(MonitoringAPIInterface monitoringInterface) {
        monitoringAPIInterface = monitoringInterface;
        offeredCapabilities.setMonitoringPlugin(monitoringInterface);
    }

    public void undeployService(Node service) {
        offeredCapabilities.undeployService(service);
    }

    public Node getControlledService() {
        // TODO Auto-generated method stub
        return offeredCapabilities.getControlledService();
    }

    public void submitElasticityRequirements(
            ArrayList<ElasticityRequirement> description) {
        // TODO Auto-generated method stub
    }

    // TODO depending on the protocol specified and the parameters, call the
    // capability = default parameter - Service Part ID
    public boolean enforceElasticityCapability(ElasticityCapability capability,
            Node e) {
        boolean res = false;
        if (e != null) {
            RuntimeLogger.logger.info("Enforcing capability " + capability.getApiMethod()
                    + " ...");
            if (capability.getCallType().toLowerCase().contains("rest")) {
                URL url = null;
                HttpURLConnection connection = null;
                try {

                    url = new URL(capability.getEndpoint());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setInstanceFollowRedirects(false);
                    if (capability.getCallType().toLowerCase().contains("post")) {
                        connection.setRequestMethod("POST");
                    } else {
                        connection.setRequestMethod("PUT");
                    }

                    // write message body
                    OutputStream os = connection.getOutputStream();

                    if (capability.getParameter().size() == 0) {
                        connection.setRequestProperty("Content-Type",
                                "text/plain");
                        connection.setRequestProperty("Accept", "text/plain");
                        os.write(e.getId().getBytes());
                    } else {
                        // tODO: add parameters here parameter=x
                    }
                    os.flush();
                    os.close();
                    res = true;
                    InputStream errorStream = connection.getErrorStream();
                    if (errorStream != null) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(errorStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Logger.getLogger(MELA_API.class.getName()).log(
                                    Level.SEVERE, line);
                            res = false;
                        }
                    }

                    InputStream inputStream = connection.getInputStream();
                    if (inputStream != null) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Logger.getLogger(MELA_API.class.getName()).log(
                                    Level.SEVERE, line);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MELA_API.class.getName()).log(
                            Level.SEVERE, ex.getMessage(), e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            } else {
                if (capability.getCallType().toLowerCase().contains("plugin")) {
                    res = offeredCapabilities.enforceAction(
                            capability.getEndpoint(), e);
                }
            }
            RuntimeLogger.logger.info("Finished enforcing action "
                    + capability.getName() + " on the node " + e + " ...");
        }
        return res;
    }

    public void setExecutingControlAction(boolean executingControlAction) {
        this.executingControlAction = executingControlAction;
    }
}
