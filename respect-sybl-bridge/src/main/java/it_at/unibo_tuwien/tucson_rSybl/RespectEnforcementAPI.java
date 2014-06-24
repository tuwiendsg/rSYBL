/**
 * Crated by ste on 17/giu/2014
 */
package it_at.unibo_tuwien.tucson_rSybl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import alice.logictuple.LogicTuple;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonAgentId;
import alice.tucson.api.TucsonMetaACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tucson.service.TucsonNodeService;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import at.ac.tuwien.dsg.csdg.Node;

/**
 * @author ste
 * 
 */
public class RespectEnforcementAPI {
    public static final String AID = "'$rSYBL-enforce'";
    public static final int MAX_TRIES = 10;
    public static final String MONITOR_METRICS_PATH =
     "it_at/unibo_tuwien/tucson_rSybl/scaleout.rsp";
    public static final long OP_TIMEOUT = 3000;
    public static final int TUCSON_PORT = 20504;
    private static final String HOST = "localhost";
    private static final String IN = "in";
    private static final String IN_S = "in_s";
    private static final int INSTALL_TIMEOUT = 30000;
    private static final String OUT = "out";
    private static final String OUT_S = "out_s";
    private static final String QUIT = "quit";
    private static final String RD = "rd";
    private static final String RD_S = "rd_s";
    private static final String SET_S = "set_s";
    private static final String TC_NAME = "'$rSYBL'";
    private EnhancedSynchACC acc;
    private TucsonAgentId aid;
    private boolean isTucsonServiceOn;
    private final Logger logger;
    // private String monitorMetricsPath;
    private final int port;
    private TucsonTupleCentreId tcid;
    private TucsonNodeService tns;

    /**
     * @param tucsonPort
     *            the listening port for TuCSoN service hosting ReSpecT tuple
     *            centre
     * @param agentName
     *            the name of the agent
     */
    public RespectEnforcementAPI(final int tucsonPort, final String agentName) {
        this.port = tucsonPort;
        this.isTucsonServiceOn = false;
        // this.monitorMetricsPath = RespectEnforcementAPI.MONITOR_METRICS_PATH;
        try {
            this.tcid = new TucsonTupleCentreId(RespectEnforcementAPI.TC_NAME,
                    RespectEnforcementAPI.HOST, String.valueOf(this.port));
            this.aid = new TucsonAgentId(agentName);
        } catch (TucsonInvalidTupleCentreIdException
                | TucsonInvalidAgentIdException e) {
            // cannot happen
            e.printStackTrace();
        }
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    /**
     * @param actionName
     *            the ReSpecT action to be carried out @code{actionName =
     *            primitive(args)}
     * @param opTimeout
     *            the maximum waiting time
     * @param e
     *            TODO understand if/when/how to use it
     * @return the result of the operation
     */
    public ITucsonOperation delegate(final String actionName,
            final long opTimeout, final Node e) {
        ITucsonOperation result = null;
        synchronized (this) {
            if (!this.isTucsonServiceOn) {
                this.isTucsonServiceOn = this.bootTucsonService();
            }
        }
        if (this.isTucsonServiceOn) {
            if (RespectEnforcementAPI.QUIT.equalsIgnoreCase(actionName)) {
                this.shutdownTucsonService();
            } else {
                final int i = actionName.indexOf("(");
                final String primitive = actionName.substring(0, i).trim();
                this.log("primitive = " + primitive);
                final String args = actionName.substring(i + 1,
                        actionName.length() - 1).trim();
                this.log("args = " + args);
                switch (primitive) {
                    case OUT:
                        result = this.doOut(args, opTimeout);
                        break;
                    case IN:
                        result = this.doIn(args, opTimeout);
                        break;
                    case RD:
                        result = this.doRd(args, opTimeout);
                        break;
                    case OUT_S:
                        result = this.doOutS(args, opTimeout);
                        break;
                    case IN_S:
                        result = this.doInS(args, opTimeout);
                        break;
                    case RD_S:
                        result = this.doRdS(args, opTimeout);
                        break;
                    case SET_S:
                        result = this.doSetS(args, opTimeout);
                        break;
                    default:
                        this.err("Unknown ReSpecT primitive");
                        break;
                }
            }
        }
        return result;
    }

    // /**
    // * @return the monitorMetricsPath
    // */
    // public String getMonitorMetricsPath() {
    // return this.monitorMetricsPath;
    // }
    //
    // /**
    // * @param monitorMetricsPath
    // * the monitorMetricsPath to set
    // */
    // public void setMonitorMetricsPath(final String monitorMetricsPath) {
    // this.monitorMetricsPath = monitorMetricsPath;
    // }
    /**
     * 
     */
    private boolean bootTucsonService() {
        boolean result = false;
        this.tns = new TucsonNodeService(this.port);
        this.tns.install();
        try {
            if (TucsonNodeService.isInstalled(this.port,
                    RespectEnforcementAPI.INSTALL_TIMEOUT)) {
                result = true;
                this.log("TuCSoN service succesfully installed on port: "
                        + this.port);
                this.acc = TucsonMetaACC.getContext(this.aid,
                        RespectEnforcementAPI.HOST, this.port);
            } else {
                this.err("TuCSoN service failed to install on port: "
                        + this.port);
            }
        } catch (final SocketTimeoutException e) {
            this.err("Cannot guarantee TuCSoN service installation on port: "
                    + this.port + " due to: " + e);
        } catch (final IOException e) {
            // unknown error
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param arg
     *            the String representation of the LogicTuple argument of the
     *            ReSpecT primitive to be carried out
     * @param opTimeout
     * @return the result of the ReSpecT primitive
     */
    private ITucsonOperation doIn(final String arg, final long opTimeout) {
        ITucsonOperation op = null;
        try {
            op = this.acc.in(this.tcid, LogicTuple.parse(arg), opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        } catch (final InvalidTupleException e) {
            this.err("Malformed tuple: " + arg);
        }
        return op;
    }

    /**
     * @param arg
     *            the String representation of the LogicTuple argument of the
     *            ReSpecT primitive to be carried out
     * @param opTimeout
     * @return the result of the ReSpecT primitive
     */
    private ITucsonOperation doInS(final String arg, final long opTimeout) {
        final String[] spec = this.parseSpec(arg);
        ITucsonOperation op = null;
        try {
            op = this.acc.inS(this.tcid, LogicTuple.parse(spec[0]),
                    LogicTuple.parse(spec[1]), LogicTuple.parse(spec[2]),
                    opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        } catch (final InvalidTupleException e) {
            this.err("Malformed tuple: " + arg);
        }
        return op;
    }

    /**
     * @param arg
     *            the String representation of the LogicTuple argument of the
     *            ReSpecT primitive to be carried out
     * @param opTimeout
     * @return the result of the ReSpecT primitive
     */
    private ITucsonOperation doOut(final String arg, final long opTimeout) {
        ITucsonOperation op = null;
        try {
            op = this.acc.out(this.tcid, LogicTuple.parse(arg), opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        } catch (final InvalidTupleException e) {
            this.err("Malformed tuple: " + arg);
        }
        return op;
    }

    /**
     * @param arg
     *            the String representation of a ReSpecT sepcification tuple
     * @code{e(), g(), b()}
     * @param opTimeout
     * @return the result of the ReSpecT primitive
     */
    private ITucsonOperation doOutS(final String arg, final long opTimeout) {
        final String[] spec = this.parseSpec(arg);
        ITucsonOperation op = null;
        try {
            op = this.acc.outS(this.tcid, LogicTuple.parse(spec[0]),
                    LogicTuple.parse(spec[1]), LogicTuple.parse(spec[2]),
                    opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        } catch (final InvalidTupleException e) {
            this.err("Malformed tuple: " + arg);
        }
        return op;
    }

    /**
     * @param arg
     *            the String representation of a ReSpecT sepcification tuple
     * @code{e(), g(), b()}
     * @param opTimeout
     * @return the result of the ReSpecT primitive
     */
    private ITucsonOperation doRd(final String arg, final long opTimeout) {
        ITucsonOperation op = null;
        try {
            op = this.acc.rd(this.tcid, LogicTuple.parse(arg), opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        } catch (final InvalidTupleException e) {
            this.err("Malformed tuple: " + arg);
        }
        return op;
    }

    /**
     * @param arg
     *            the String representation of a ReSpecT sepcification tuple
     * @code{e(), g(), b()}
     * @param opTimeout
     * @return the result of the ReSpecT primitive
     */
    private ITucsonOperation doRdS(final String arg, final long opTimeout) {
        final String[] spec = this.parseSpec(arg);
        ITucsonOperation op = null;
        try {
            op = this.acc.rdS(this.tcid, LogicTuple.parse(spec[0]),
                    LogicTuple.parse(spec[1]), LogicTuple.parse(spec[2]),
                    opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        } catch (final InvalidTupleException e) {
            this.err("Malformed tuple: " + arg);
        }
        return op;
    }

    /**
     * @param args
     * @param opTimeout
     * @return
     */
    private ITucsonOperation doSetS(final String arg, final long opTimeout) {
        ITucsonOperation op = null;
        try {
            op = this.acc.setS(this.tcid, arg, opTimeout);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            this.err("TuCSoN service unavailable: " + e);
        }
        return op;
    }

    /**
     * @param string
     */
    private void err(final String msg) {
        this.logger.log(Level.SEVERE, msg);
    }

    /**
     * @param string
     */
    private void log(final String msg) {
        this.logger.log(Level.INFO, msg);
    }

    // TODO parse as tuple then exploit knowledge that it always has three args
    private String[] parseSpec(final String spec) {
        String[] split = spec.split("\\), g\\(");
        final String event = split[0].substring(2).trim();
        split = split[1].split("\\), b\\(");
        final String guards = "(" + split[0].trim() + ")";
        final String body = "("
                + split[1].substring(0, split[1].length() - 1).trim() + ")";
        this.log("event = " + event + ", guards = " + guards + ", body = "
                + body);
        return new String[] { event, guards, body };
    }

    /**
     * @return shutdown always succeeds (TuCSoN flaw)
     */
    private boolean shutdownTucsonService() {
        this.tns.shutdown();
        return true;
    }
}
