/**
 * Crated by ste on 19/giu/2014
 */
package it_at.unibo_tuwien.tucson_rSybl;

import java.util.HashMap;
import java.util.Map;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.NovaApiMetadata;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.jclouds.openstack.v2_0.domain.Resource;
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
import alice.tuplecentre.api.exceptions.OperationTimeOutException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;
import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudApiLowLevel.enforcementPlugins.openstack.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudApiLowLevel.enforcementPlugins.openstack.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.OfferedMonitoredMetrics;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.interfaces.MonitoringInterface;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author ste
 * 
 */
public class SyblScaleOutAgent extends AbstractTucsonAgent {
    public static final String AID = "'$rSYBL-scaleout'";
    public static final String HOST = "localhost";
    public static final String TC = "'$rSYBL'";

    /**
     * 
     */
    private static void bootLogging() {
        ImmutableSet.<Module> of(new SLF4JLoggingModule());
        RuntimeLogger.logger.info(Configuration.getCloudAPIType() + " "
                + Configuration.getCloudUser() + " "
                + Configuration.getCloudPassword() + " "
                + Configuration.getCloudAPIEndpoint());
    }

    /**
     * @param entity
     * @return
     */
    private static CreateServerOptions createOptions(final Node entity) {
        final CreateServerOptions createNodeOptions = new CreateServerOptions();
        final Map<String, String> nodeMetaData = new HashMap<String, String>();
        String metadata = "";
        if ("DataNodeServiceUnit".equalsIgnoreCase(entity.getId())) {
            metadata = "CASSANDRA_SEED_IP=10.99.0.44 \n CASSANDRA_RPC_PORT=9160 \n CASSANDRA_TCP_PORT=9161";
        } else {
            metadata = "LOAD_BALANCER_IP=10.99.0.39 \n CASSANDRA_SEED_NODE_IP=10.99.0.44";
        }
        nodeMetaData.put(metadata, "");
        createNodeOptions.metadata(nodeMetaData);
        createNodeOptions.userData(metadata.getBytes());
        createNodeOptions.keyPairName(Configuration.getCertificateName());
        return createNodeOptions;
    }

    // private final JCloudsOpenStackConnection jClouds;
    private NovaApi client;
    private final Node controlledService;
    private final MonitoringInterface monitor;
    private final Node node;
    private ServerApi serverApi;
    private TucsonTupleCentreId tid;
    private EnhancedSynchACC acc;

    /**
     * @param id
     *            the TuCSoN agent id
     * @param p
     *            the port where TuCSoN node service is listening
     * @param cs
     *            the controlled service
     * @param n
     *            the SYBL Node to scaleout
     * @throws TucsonInvalidAgentIdException
     *             if given id is not a valid TuCSoN agent id
     */
    public SyblScaleOutAgent(final String id, final int p, final Node cs,
            final Node n) throws TucsonInvalidAgentIdException {
        super(id, SyblScaleOutAgent.HOST, p);
        this.controlledService = cs;
        this.node = cs;
        try {
            this.tid = new TucsonTupleCentreId(SyblScaleOutAgent.TC,
                    super.myNode(), String.valueOf(super.myport()));
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // cannot happen
            e.printStackTrace();
        }
        // this.jClouds = new JCloudsOpenStackConnection(this.node);
        SyblScaleOutAgent.bootLogging();
        this.bootClient();
        this.bootServer();
        this.monitor = OfferedMonitoredMetrics.getInstance(this.node);
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tuplecentre
     * .core.AbstractTupleCentreOperation)
     */
    @Override
    public void operationCompleted(final AbstractTupleCentreOperation arg0) {
        // not used atm
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.AbstractTucsonAgent#operationCompleted(alice.tucson.
     * api.ITucsonOperation)
     */
    @Override
    public void operationCompleted(final ITucsonOperation arg0) {
        // not used atm
    }

    /**
     * 
     */
    private void bootClient() {
        final ComputeServiceContext context = ContextBuilder
                .newBuilder(Configuration.getCloudAPIType())
                .credentials(Configuration.getCloudUser(),
                        Configuration.getCloudPassword())
                .endpoint(Configuration.getCloudAPIEndpoint())
                .buildView(ComputeServiceContext.class);
        context.getComputeService();
        this.client = context.unwrap(NovaApiMetadata.CONTEXT_TOKEN).getApi();
    }

    /**
     * 
     */
    private void bootServer() {
        final String region = "myregion";
        this.serverApi = this.client.getServerApiForZone(region);
        for (final Resource flavor : this.client.getFlavorApiForZone(region)
                .list().concat()) {
            RuntimeLogger.logger.error(flavor.getId() + " " + flavor.getName());
        }
    }

    /**
     * @param entity
     * @return
     */
    private String getFlavorID(final Node entity) {
        String flavorID = "";
        for (final Resource flavor : this.client
                .getFlavorApiForZone("myregion").list().concat()) {
            if (((String) entity.getStaticInformation("DefaultFlavor"))
                    .equalsIgnoreCase(flavor.getName())) {
                flavorID = flavor.getId();
            }
        }
        return flavorID;
    }

    /**
     * Override to change scaling out policy
     * 
     * @param entity
     *            the node to scale
     * @param controller
     *            the controller node
     * @return the ip of the new node or @code{"ERR"} if scale out failed
     */
    // copy-paste from JCloudsOpenStackConnection
    protected String scaleOutAndWaitUntilNewServerBoots(final Node entity,
            final Node controller) {
        // preparatory operations
        final CreateServerOptions createNodeOptions = SyblScaleOutAgent
                .createOptions(entity);
        final String vmName = entity.getId();
        final String flavorID = this.getFlavorID(entity);
        final int tries;
        final int step;
        try {
            // how many tries?
            ITucsonOperation op = this.acc.in(this.tid, new LogicTuple(
                    "scaleOutTries", new Var("Tries")), Long.MAX_VALUE);
            tries = op.getLogicTupleResult().getArg(0).intValue();
            // which time step between re-tries?
            op = this.acc.in(this.tid, new LogicTuple("scaleOutTime", new Var(
                    "Time")), Long.MAX_VALUE);
            step = op.getLogicTupleResult().getArg(0).intValue();
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            super.say("TuCSoN operation failure: " + e);
            return "ERR";
        } catch (InvalidOperationException e) {
            // developer error
            e.printStackTrace();
            return "ERR";
        }
        ServerCreated cs = null;
        String res = "failure";
        for (int i = 0; i < tries; i++) {
            cs = this.serverApi.create(vmName,
                    (String) entity.getStaticInformation("DefaultImage"),
                    flavorID, createNodeOptions);
            // wait to become ACTIVE
            while (this.serverApi.get(cs.getId()).getStatus() != Server.Status.ACTIVE) {
                try {
                    Thread.sleep(step);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // check success
            if (this.serverApi.get(cs.getId()).getStatus() != Server.Status.ERROR) {
                RuntimeLogger.logger.info(this.serverApi.get(cs.getId())
                        .getStatus());
                res = "success";
                break;
            }
            this.serverApi.delete(cs.getId());
            RuntimeLogger.logger.error("Created with error new instance for "
                    + entity.getId() + " - deleting and retrying....");
        }
        String ip = "";
        if ("success".equals(res)) {
            final Server server = this.serverApi.get(cs.getId());
            ip = server.getAddresses().get("private").iterator().next()
                    .getAddr();
        } else {
            RuntimeLogger.logger.error("Error when scaling out");
            ip = "ERR";
        }
        // put outcome in shared ReSpecT tc
        try {
            this.acc.out(this.tid, new LogicTuple("scaleOut", new Value(
                    this.node.getId(), new Value("done", new Value(res)))),
                    Long.MAX_VALUE);
        } catch (TucsonOperationNotPossibleException | UnreachableNodeException
                | OperationTimeOutException e) {
            super.say("TuCSoN operation failure: " + e);
            ip = "ERR";
        }
        return ip;
    }

    /**
     * @param n
     */
    // copy-paste from EnforcementOpenstackAPI
    private void scaleOutComponent(final Node n) {
        final DependencyGraph graph = new DependencyGraph();
        graph.setCloudService(this.controlledService);
        final String ip = this.scaleOutAndWaitUntilNewServerBoots(n,
                graph.findParentNode(n.getId()));
        if (!ip.equalsIgnoreCase("err")) {
            final Node newNode = new Node();
            newNode.setId(ip);
            newNode.getStaticInformation().put("IP", ip);
            newNode.setNodeType(NodeType.VIRTUAL_MACHINE);
            final Relationship rel = new Relationship();
            rel.setSourceElement(n.getId());
            rel.setTargetElement(newNode.getId());
            rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
            RuntimeLogger.logger.info("Adding to " + n.getId() + " vm with ip "
                    + ip);
            n.addNode(newNode, rel);
        }
        RuntimeLogger.logger.info("The controlled service is now "
                + this.controlledService.toString());
        this.monitor.refreshServiceStructure(this.controlledService);
    }

    /**
     * Override to include other types of scaling targets
     */
    /*
     * (non-Javadoc)
     * @see alice.tucson.api.AbstractTucsonAgent#main()
     */
    @Override
    protected void main() {
        this.acc = super.getContext();
        this.monitor.notifyControlActionStarted("ScaleOut", this.node);
        RuntimeLogger.logger.info("Scaling out ... " + this.node + " "
                + this.node.getNodeType());
        // only scale out target supported atm
        if (this.node.getNodeType() == NodeType.SERVICE_UNIT) {
            this.scaleOutComponent(this.node);
        }
        this.monitor.notifyControlActionEnded("ScaleOut", this.node);
    }
}
