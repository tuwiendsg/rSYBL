/**
 * Crated by ste on 19/giu/2014
 */
package it_at.unibo_tuwien.tucson_rSybl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.EnhancedSynchACC;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.OfferedMonitoredMetrics;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;

/**
 * @author ste
 * 
 */
public class SyblMonitoringAgent extends AbstractTucsonAgent {
    private Node node;
    public static final String AID = "'$rSYBL-monitor'";
    public static final String HOST = "localhost";
    public static final String TC = "'$rSYBL'";
    private TucsonTupleCentreId tid;

    /**
     * @param id
     *            the TuCSoN agent id
     * @param p
     *            the port where TuCSoN node service is listening
     * @param n
     *            the SYBL Node to monitor
     * @throws TucsonInvalidAgentIdException
     *             if given id is not a valid TuCSoN agent id
     */
    public SyblMonitoringAgent(final String id, final int p, final Node n)
            throws TucsonInvalidAgentIdException {
        super(id, SyblMonitoringAgent.HOST, p);
        this.node = n;
        try {
            this.tid = new TucsonTupleCentreId(SyblMonitoringAgent.TC,
                    super.myNode(), String.valueOf(super.myport()));
        } catch (TucsonInvalidTupleCentreIdException e) {
            // cannot happen
            e.printStackTrace();
        }
    }

    /**
     * Override to change monitoring policy
     */
    /*
     * (non-Javadoc)
     * @see alice.tucson.api.AbstractTucsonAgent#main()
     */
    @Override
    protected void main() {
        final EnhancedSynchACC acc = super.getContext();
        ITucsonOperation op = null;
        LogicTuple mt = null;
        List<LogicTuple> mtc = null;
        try {
            // which monitoring time step?
            op = acc.in(this.tid, new LogicTuple("monitoringTime", new Value(
                    "T")), Long.MAX_VALUE);
            mt = op.getLogicTupleResult();
            // which metrics to check?
            op = acc.inAll(this.tid, new LogicTuple("metricToCheck", new Value(
                    this.node.getId()), new Var("M")), Long.MAX_VALUE);
            mtc = op.getLogicTupleListResult();
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            super.say("TuCSoN operation failure: " + e);
            return;
        }
        // get monitoring object
        final MonitoringInterface monitor = OfferedMonitoredMetrics
                .getInstance(this.node);
        String metricName = null;
        Double metricValue = null;
        final Map<String, Double> readMetrics = new HashMap<String, Double>();
        boolean success = false;
        // until all metrics have been read, try to read them
        while (!success) {
            success = true;
            // for each chosen metric
            for (final LogicTuple metric : mtc) {
                try {
                    metricName = metric.getArg(1).toString();
                } catch (final InvalidOperationException e) {
                    // developer error
                    e.printStackTrace();
                    return;
                }
                // sample metric
                metricValue = monitor.getMetricValue(metricName, this.node);
                // consider only valid metrics
                if (metricValue != null && metricValue.doubleValue() > 0) {
                    readMetrics.put(metricName, metricValue);
                } else {
                    success = false;
                }
            }
            // if some sample failed, retry
            if (!success) {
                try {
                    Thread.sleep(mt.getArg(0).intValue());
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                } catch (final InvalidOperationException e) {
                    // developer error
                    e.printStackTrace();
                    return;
                }
            }
        }
        // put metrics in shared ReSpecT tc
        for (final String key : readMetrics.keySet()) {
            try {
                acc.out(this.tid,
                        LogicTuple.parse("metric(node('" + this.node.getId()
                                + "'), name(" + key + "), value("
                                + readMetrics.get(key) + "))"), Long.MAX_VALUE);
            } catch (final InvalidTupleException
                    | TucsonOperationNotPossibleException
                    | UnreachableNodeException | OperationTimeOutException e) {
                super.say("TuCSoN operation failure: " + e);
                return;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tuplecentre
     * .core.AbstractTupleCentreOperation)
     */
    @Override
    public void operationCompleted(AbstractTupleCentreOperation arg0) {
        // not used atm
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tucson.
     * api.ITucsonOperation)
     */
    @Override
    public void operationCompleted(ITucsonOperation arg0) {
        // not used atm
    }
}
