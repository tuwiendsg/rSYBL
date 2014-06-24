/**
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup
 * E184.
 * 
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

import it_at.unibo_tuwien.tucson_rSybl.RespectEnforcementAPI;
import it_at.unibo_tuwien.tucson_rSybl.SyblMonitoringAgent;
import it_at.unibo_tuwien.tucson_rSybl.SyblScaleOutAgent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.examples.utilities.Utils;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.OfferedEnforcementCapabilities;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin.MELA_API;

public class EnforcementAPI  {
    private static HashMap<Node, ArrayList<Float>> avgRunningTimes = new HashMap<Node, ArrayList<Float>>();
    private Node controlledService;
    private boolean executingControlAction = false;
    private MonitoringAPIInterface monitoringAPIInterface;
    private EnforcementInterface offeredCapabilities;
    public void setControlledService(Node controlledService,String className) {
		this.controlledService = controlledService;
		offeredCapabilities = OfferedEnforcementCapabilities.getInstance(className,this.controlledService);
	}
    public void refreshControlService(Node cloudService){
		   controlledService=cloudService;
	   }
    /*
     * s.mariani@unibo.it CODE begins
     */
    private final RespectEnforcementAPI respect;

    public EnforcementAPI() {
        /*
         * parameters could be easy configurable from rSYBL side, e.g. from file
         * or directives (within actionName!)
         */
        this.respect = new RespectEnforcementAPI(
                RespectEnforcementAPI.TUCSON_PORT, RespectEnforcementAPI.AID);
    }

    /*
     * s.mariani@unibo.it CODE ends
     */
    public void enforceAction(final String actionName, final Node e) {
        RuntimeLogger.logger
                .info("~~~~~~~~~~~Trying to execute action executingControlaction="
                        + this.executingControlAction);
        if (this.executingControlAction == false) {
            /*
             * s.mariani@unibo.it CODE begins
             */
            if (e != null) {
                RuntimeLogger.logger.info("Enforcing action " + actionName
                        + " on the node " + e + " ...");
            } else {
                RuntimeLogger.logger.info("Enforcing coordination action "
                        + actionName + " ...");
            }
            this.executingControlAction = true;
            String args;
            if (actionName.startsWith("scaleout")) {
                if ("scaleout".length() == actionName.length()) {
                    args = null;
                } else {
                    args = actionName.substring("scaleout(".length(),
                            actionName.length() - 1);
                }
                this.doCoordinatedScaleOut(args, e);
            } else if (actionName.startsWith("scalein")) {
                if ("scalein".length() == actionName.length()) {
                    args = null;
                } else {
                    args = actionName.substring("scalein(".length(),
                            actionName.length() - 1);
                }
                this.doCoordinatedScaleIn(args, e);
            } else if (actionName.startsWith("monitorMetrics")) {
                if ("monitorMetrics".length() == actionName.length()) {
                    args = null;
                } else {
                    args = actionName.substring("monitorMetrics(".length(),
                            actionName.length() - 1);
                }
                this.doCoordinatedMonitorMetrics(args, e);
            } else {
                this.respect.delegate(actionName,
                        RespectEnforcementAPI.OP_TIMEOUT, e);
            }
            this.executingControlAction = false;
            if (e != null) {
                RuntimeLogger.logger.info("Finished enforcing action "
                        + actionName + " on the node " + e + " ...");
            } else {
                RuntimeLogger.logger
                        .info("Finished enforcing coordination action "
                                + actionName + " ...");
            }
            /*
             * s.mariani@unibo.it CODE ends
             */
        }
    }

    /*
     * s.mariani@unibo.it CODE ends
     */
    // TODO depending on the protocol specified and the parameters, call the
    // capability = default parameter - Service Part ID
    public void enforceElasticityCapability(
            final ElasticityCapability capability, final Node e) {
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
                final OutputStream os = connection.getOutputStream();
                if (capability.getParameter().size() == 0) {
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setRequestProperty("Accept", "text/plain");
                    os.write(e.getId().getBytes());
                } else {
                    // tODO: add parameters here parameter=x
                }
                os.flush();
                os.close();
                final InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(errorStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Logger.getLogger(MELA_API.class.getName()).log(
                                Level.SEVERE, line);
                    }
                }
                final InputStream inputStream = connection.getInputStream();
                if (inputStream != null) {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Logger.getLogger(MELA_API.class.getName()).log(
                                Level.SEVERE, line);
                    }
                }
            } catch (final Exception ex) {
                Logger.getLogger(MELA_API.class.getName()).log(Level.SEVERE,
                        ex.getMessage(), e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        } else {
            if (capability.getCallType().toLowerCase().contains("plugin")) {
                this.offeredCapabilities.enforceAction(
                        capability.getEndpoint(), e);
            }
        }
    }

    
    public Node getControlledService() {
        // TODO Auto-generated method stub
        return this.offeredCapabilities.getControlledService();
    }

    public boolean isExecutingControlAction() {
        return this.executingControlAction;
    }

    
    public void scalein(final Node arg0) {
        RuntimeLogger.logger
                .info("~~~~~~~~~~~Trying to execute action executingControlaction="
                        + this.executingControlAction);
        if (this.executingControlAction == false) {
            if (arg0.getAllRelatedNodes().size() > 1) {
                this.executingControlAction = true;
                this.offeredCapabilities.scaleIn(arg0);
                final List<String> metrics = this.monitoringAPIInterface
                        .getAvailableMetrics(arg0);
                boolean checkIfMetrics = false;
                // monitoringAPIInterface.enforcingActionStarted("ScaleIn",
                // arg0);
                while (!checkIfMetrics) {
                    boolean myMetrics = true;
                    try {
                        Thread.sleep(10000);
                    } catch (final InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    RuntimeLogger.logger.info("Waiting for action....");
                    for (final String metricName : metrics) {
                        RuntimeLogger.logger.info("Metric "
                                + metricName
                                + " has value "
                                + this.monitoringAPIInterface.getMetricValue(
                                        metricName, arg0));
                        if (this.monitoringAPIInterface.getMetricValue(
                                metricName, arg0) == null
                                || this.monitoringAPIInterface.getMetricValue(
                                        metricName, arg0) <= 0) {
                            myMetrics = false;
                            RuntimeLogger.logger.info("~~~~Metric "
                                    + metricName + "smaller than 0");
                        }
                    }
                    checkIfMetrics = myMetrics;
                }
                try {
                    Thread.sleep(60000);
                } catch (final InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                this.executingControlAction = false;
                // monitoringAPIInterface.enforcingActionEnded("ScaleIn", arg0);
                RuntimeLogger.logger.info("Finished scaling in " + arg0.getId()
                        + " ...");
            } else {
                RuntimeLogger.logger.info("Number of nodes associated with "
                        + arg0.getAllRelatedNodes().size());
            }
        }
    }

    
    public void scaleout(final Node arg0) {
        RuntimeLogger.logger
                .info("~~~~~~~~~~~Trying to execute action executingControlaction="
                        + this.executingControlAction);
        if (this.executingControlAction == false && arg0 != null) {
            RuntimeLogger.logger.info("Scaling out " + arg0 + " ...");
            this.executingControlAction = true;
            this.offeredCapabilities.scaleOut(arg0);
            final List<String> metrics = this.monitoringAPIInterface
                    .getAvailableMetrics(arg0);
            // monitoringAPIInterface.enforcingActionStarted("ScaleOut", arg0);
            boolean checkIfMetrics = false;
            while (!checkIfMetrics) {
                boolean myMetrics = true;
                try {
                    Thread.sleep(10000);
                } catch (final InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                RuntimeLogger.logger.info("Waiting for action....");
                for (final String metricName : metrics) {
                    RuntimeLogger.logger.info("Metric "
                            + metricName
                            + " has value "
                            + this.monitoringAPIInterface.getMetricValue(
                                    metricName, arg0));
                    if (this.monitoringAPIInterface.getMetricValue(metricName,
                            arg0) == null
                            || this.monitoringAPIInterface.getMetricValue(
                                    metricName, arg0) <= 0) {
                        myMetrics = false;
                        RuntimeLogger.logger.info("~~~Metric " + metricName
                                + "smaller than 0");
                    }
                }
                checkIfMetrics = myMetrics;
            }
            try {
                Thread.sleep(60000);
            } catch (final InterruptedException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            this.executingControlAction = false;
            // monitoringAPIInterface.enforcingActionEnded("ScaleOut", arg0);
            RuntimeLogger.logger.info("Finished scaling out " + arg0.getId()
                    + " ...");
        } else {
            RuntimeLogger.logger.info(arg0);
        }
    }

    
    public void setControlledService(final Node controlledService) {
        this.controlledService = controlledService;
        this.offeredCapabilities = OfferedEnforcementCapabilities
                .getInstance(this.controlledService);
    }

    
    public void setMonitoringPlugin(
            final MonitoringAPIInterface monitoringInterface) {
        this.monitoringAPIInterface = monitoringInterface;
        this.offeredCapabilities.setMonitoringPlugin(monitoringInterface);
    }

    
    public void submitElasticityRequirements(
            final ArrayList<ElasticityRequirement> description) {
        // TODO Auto-generated method stub
    }

    /*
     * s.mariani@unibo.it CODE begins
     */
    /**
     * @param actionName
     * @param e
     *            the node to be monitored
     */
    private void doCoordinatedMonitorMetrics(final String actionName,
            final Node node) {
        try {
            // this.respect.setMonitorMetricsPath(actionName);
            if (actionName != null) {
                this.respect.delegate("set_s(" + Utils.fileToString(actionName)
                        + ")", Long.MAX_VALUE, node);
            }
            // this.respect
            // .setMonitorMetricsPath(RespectEnforcementAPI.MONITOR_METRICS_PATH);
            // this.respect
            // .delegate(
            // "out_s("
            // + Utils.fileToString(RespectEnforcementAPI.MONITOR_METRICS_PATH)
            // + ")", Long.MAX_VALUE, node);
            new SyblMonitoringAgent(SyblMonitoringAgent.AID,
                    RespectEnforcementAPI.TUCSON_PORT, node).go();
            this.respect.delegate("out(sampleMetrics('" + node.getId() + "'))",
                    Long.MAX_VALUE, node);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TucsonInvalidAgentIdException e) {
            // cannot happen
        }
    }

    /**
     * @param actionName
     * @param node
     *            the node to be scaled in
     */
    private void doCoordinatedScaleIn(final String actionName, final Node node) {
        // TODO Auto-generated method stub
    }

    /**
     * @param actionName
     * @param node
     *            the node to be scaled out
     * 
     */
    private void doCoordinatedScaleOut(final String actionName, final Node node) {
        RuntimeLogger.logger
                .info("~~~~~~~~~~~Trying to execute action executingControlaction="
                        + this.executingControlAction);
        if (this.executingControlAction == false && node != null) {
            RuntimeLogger.logger.info("Scaling out " + node + " ...");
            this.executingControlAction = true;
            // call CloudAPI for scaling
            // this.offeredCapabilities.scaleOut(node);
            // delegate scaling out to ReSpecT!
            try {
                if (actionName != null) {
                    this.respect.delegate(
                            "set_s(" + Utils.fileToString(RespectEnforcementAPI.MONITOR_METRICS_PATH) + ")",
                            Long.MAX_VALUE, node);
                }
                new SyblScaleOutAgent(SyblScaleOutAgent.AID,
                        RespectEnforcementAPI.TUCSON_PORT,
                        this.controlledService, node).go();
                this.respect.delegate(
                        "out(doScaleOut('" + node.getId() + "'))",
                        Long.MAX_VALUE, node);
            } catch (TucsonInvalidAgentIdException e) {
                // cannot happen
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            RuntimeLogger.logger.info("Waiting for action....");
            // delegate monitoring to ReSpecT!
            this.doCoordinatedMonitorMetrics(actionName, node);
            // ((MonitoringAPI) this.monitoringAPIInterface)
            // .delegateMetricsReadingToRespect(node);
            // coordinate with ReSpecT tc to get metrics!
            ITucsonOperation op = null;
            final List<ITucsonOperation> ops = new LinkedList<ITucsonOperation>();
            for (int i = 0; i < RespectEnforcementAPI.MAX_TRIES; i++) {
                op = this.respect.delegate("in(metric(node('" + node.getId()
                        + "'), name(M), value(V)))",
                        RespectEnforcementAPI.OP_TIMEOUT, node);
                if (op.isResultSuccess()) {
                    ops.add(op);
                }
            }
            // do something with metrics (e.g. log)...
            for (final ITucsonOperation o : ops) {
                try {
                    RuntimeLogger.logger.info("Metric "
                            + o.getLogicTupleResult().getArg(1).getArg(0)
                            + " has value "
                            + o.getLogicTupleResult().getArg(2).getArg(0));
                } catch (final InvalidOperationException e) {
                    // cannot happen
                    e.printStackTrace();
                }
            }
            // coordinate with ReSpecT tc for a successful scaling!
            op = this.respect.delegate("in(scaleOut('" + node.getId()
                    + "'), done(B))", RespectEnforcementAPI.OP_TIMEOUT, node);
            this.executingControlAction = false;
            if (op.isResultSuccess()) {
                try {
                    RuntimeLogger.logger.info("Finished scaling out "
                            + node.getId() + " : "
                            + op.getLogicTupleResult().getArg(1).getArg(0));
                } catch (InvalidOperationException e) {
                    // cannot happen
                }
            } else {
                RuntimeLogger.logger.info("Finished scaling out "
                        + node.getId() + " failed!");
            }
        } else {
            RuntimeLogger.logger.info(node);
        }
    }
}
