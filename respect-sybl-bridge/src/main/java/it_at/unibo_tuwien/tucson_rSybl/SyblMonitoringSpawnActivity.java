/**
 * Crated by ste on 18/giu/2014
 */
package it_at.unibo_tuwien.tucson_rSybl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import alice.logictuple.LogicTuple;
import alice.logictuple.Value;
import alice.logictuple.Var;
import alice.tucson.api.AbstractSpawnActivity;
import alice.tuplecentre.api.exceptions.InvalidOperationException;
import alice.tuplecentre.api.exceptions.InvalidTupleException;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.OfferedMonitoredMetrics;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;

/**
 * @author ste
 * 
 */
public class SyblMonitoringSpawnActivity extends AbstractSpawnActivity {
    /**
     * @author ste
     * 
     */
    private final static class RSyblWhitePages {
        /**
         * @param nodeId
         *            the id of the rSYBL node to retrieve
         * @return the reference to the rSYBL node retrieved
         */
        public static Node discover(final String nodeId) {
            // should query rSYBL to get the Node referred by nodeId
            final Node node = new Node();
            return node;
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see alice.tucson.api.AbstractSpawnActivity#doActivity()
     */
    @Override
    public void doActivity() {
        // which node to monitor?
        final LogicTuple sm = this.in(new LogicTuple("sampleMetrics", new Var(
                "NodeId")));
        String n = null;
        Node node = null;
        try {
            n = sm.getArg(0).toString();
            node = RSyblWhitePages.discover(n);
        } catch (final InvalidOperationException e) {
            // developer error
            e.printStackTrace();
            return;
        }
        // which monitoring time step?
        final LogicTuple mt = this.in(new LogicTuple("monitoringTime",
                new Value("T")));
        // which metrics to check?
        final List<LogicTuple> mtc = this.inAll(new LogicTuple("metricToCheck",
                new Value(n), new Var("M")));
        // get monitoring object
        final MonitoringInterface monitor = OfferedMonitoredMetrics
                .getInstance(node);
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
                metricValue = monitor.getMetricValue(metricName, node);
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
                this.out(LogicTuple.parse("metric(node('" + node.getId()
                        + "'), name(" + key + "), value("
                        + readMetrics.get(key) + "))"));
            } catch (final InvalidTupleException e) {
                // cannot happen
                e.printStackTrace();
            }
        }
    }
}
