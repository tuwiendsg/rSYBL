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
package at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.dryRun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import at.ac.tuwien.dsg.csdg.DependencyGraph;
import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.SimpleRelationship;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.enforcementPlugins.interfaces.EnforcementInterface;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.Configuration;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.utils.RuntimeLogger;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.MonitoringAPIInterface;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DryRunEnforcementAPI implements EnforcementInterface {

    private Node controlledService;
    private MonitoringAPIInterface monitoring;
    private List<String> parameters = new ArrayList<String>();
    private double MAX_VIOLATION_DEGREE=3.0;
    public DryRunEnforcementAPI(Node cloudService) {
        // TODO Auto-generated constructor stub
        controlledService = cloudService;
        readParameters();
    }
    public static void main (String[]args){
        DryRunEnforcementAPI aPI = new DryRunEnforcementAPI(null);
        aPI.readParameters();
    }
    public void readParameters(){
        JSONParser parser = new JSONParser();
		 
			try {
				InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream("config/resources.json");
				Object obj = parser.parse(new InputStreamReader(inputStream));
		 
				JSONObject jsonObject = (JSONObject) obj;
				
				for (Object p: jsonObject.keySet()){
				String pluginName = (String) p;
                                JSONObject plugin=(JSONObject) jsonObject.get(pluginName);
                                if (pluginName.toLowerCase().contains("dry")){
                                for (Object a:plugin.keySet()){
                                    String actionName= (String)a;
                                    JSONObject action=(JSONObject) plugin.get(actionName);
                                    JSONArray jSONArray = (JSONArray) action.get("parameters");
                                    for (int i=0;i<jSONArray.size();i++){
                                     parameters.add((String)jSONArray.get(i));
                                    }
                                    
                                }
                                }
                                }
                        }catch(Exception e){
                            RuntimeLogger.logger.info(e.getMessage());
                        }
    }
    public Node findNode(String id) {
        boolean found = false;
        if (!found) {
            if (id.equalsIgnoreCase(controlledService.getId())) {
                found = true;
                return controlledService;
            }
        }
        Node topology = controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);

        if (topology.getId().equalsIgnoreCase(id)) {
            found = true;
            return topology;
        }
        List<Node> topologies = new ArrayList<Node>();

        topologies.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));

        List<Node> componentsToExplore = new ArrayList<Node>();
        if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT) != null) {
            componentsToExplore.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
        }
        while (!found && !topologies.isEmpty()) {
            Node currentTopology = topologies.get(0);
            topologies.remove(0);
            if (currentTopology.getId().equalsIgnoreCase(id)) {
                found = true;
                return currentTopology;
            } else {
                //System.err.println(currentTopology.getId());
                if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY) != null && currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).size() > 0) {
                    topologies.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
                }
                if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT) != null && currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT).size() > 0) {
                    componentsToExplore.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
                }
            }
        }
        //		if (getCurrentReadLatency(arg0).isNaN())

        while (!found && !componentsToExplore.isEmpty()) {
            Node component = componentsToExplore.get(0);
            componentsToExplore.remove(0);
            if (component.getId().equalsIgnoreCase(id)) {
                found = true;
                return component;
            }
        }

        return null;
    }
    public boolean scaleOut(double violationDegree, Node arg0){
        boolean res =true;
        RuntimeLogger.logger.info("Scaling out ... " + arg0 + " " + arg0.getNodeType()+" violation degree "+violationDegree);

     //   monitoring.enforcingActionStarted("ScaleOut", arg0);

        Node o = arg0;



            res = scaleOutComponent(violationDegree,(Node) o);

       // monitoring.enforcingActionEnded("ScaleOut", arg0);
        return res;
        
    }
    public boolean scaleOut(Node arg0) {
        RuntimeLogger.logger.info("Scaling out ... " + arg0 + " " + arg0.getNodeType());

       // monitoring.enforcingActionStarted("ScaleOut", arg0);
        boolean res = true;
        Node o = arg0;

        if (o.getNodeType() == NodeType.CODE_REGION) {
            scaleOutComponent(findComponentOfCodeRegion(arg0));
        }

        //TODO : enable just ComponentTopology level 

        if (o.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
            //TODO: make it possible to scale a set of component topologies

            Node master = null;
            Node slave = null;
            ArrayList<Node> comps = (ArrayList<Node>) o.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT);


            for (Node comp : comps) {
                if (comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF) != null) {
                    master = comp;
                    slave = comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF).get(0);
                }
            }


            //TODO:add for more slaves
            List<String> slavesPrivateIps = new ArrayList<String>();
            List<String> masterPrivateIps = new ArrayList<String>();
            for (String ip : slave.getAssociatedIps()) {
                if (ip.split("\\.")[0].length() == 2) {
                    slavesPrivateIps.add(ip);
                }
            }
            for (String ip : master.getAssociatedIps()) {
                if (ip.split("\\.")[0].length() == 2) {
                    masterPrivateIps.add(ip);
                }
            }
            int nb = 1;
            if (masterPrivateIps.size() != 0) {
                nb = slavesPrivateIps.size() / masterPrivateIps.size();
            }
            //maybe we implement cluster-level actions
            //cloudsOpenStackConnection.scaleOutCluster(master, slave, nb,master.getAssociatedIps().get(0),controlledService);


        }

        if (o.getNodeType() == NodeType.SERVICE_UNIT) {
            res = scaleOutComponent((Node) o);
        }
        //monitoring.enforcingActionEnded("ScaleOut", arg0);
        return res;
    }

    private void loadDeploymentDescription() {
//		try {			
//			JAXBContext a = JAXBContext.newInstance( DeploymentDescription.class );
//			Unmarshaller u  = a.createUnmarshaller();
//			String deploymentDescriptionPath = at.ac.tuwien.dsg.sybl.monitorandenforcement.utils.Configuration.getDeploymentDescriptionPath();
//		//	RuntimeLogger.logger.info("Got here "+deploymentDescriptionPath);
//		//	RuntimeLogger.logger.info("Got here "+this.getClass().getClassLoader().getResourceAsStream(deploymentDescriptionPath));
//
//			if (deploymentDescriptionPath!=null)
//		     deploymentDescription = (DeploymentDescription) u.unmarshal( this.getClass().getClassLoader().getResourceAsStream(deploymentDescriptionPath)) ;
//
//			//RuntimeLogger.logger.info("Read deployment Descrption"+deploymentDescription.toString());
//
//		} catch (Exception e) {
//			RuntimeLogger.logger.error("Error in reading deployment description"+e.toString());
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
 private boolean scaleOutComponent(double violationDegree,Node o) {
     if (parameters.size()>0){
        DependencyGraph graph = new DependencyGraph();
        graph.setCloudService(controlledService);
        
        RuntimeLogger.logger.info("~~~~~~~~~~~~~~~~~~~~~~Image from which we create " + (String) o.getStaticInformation("DefaultImage"));
        String uuid = UUID.randomUUID().toString();
        Node node = new Node();
        Random r = new Random();
        String ip = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
        String vmType = "";
        if (violationDegree>MAX_VIOLATION_DEGREE) violationDegree=MAX_VIOLATION_DEGREE;
        
        int index = (int) (parameters.size()*violationDegree/MAX_VIOLATION_DEGREE)-1;
        if (index<0)index=0;
        
        if (!ip.equalsIgnoreCase("err") && !ip.equalsIgnoreCase("")) {
            if (o.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {

                Node artifact = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);
                node.getStaticInformation().put("UUID", uuid);
                node.getStaticInformation().put("IP", ip);
                node.setId(ip);
                node.setNodeType(NodeType.VIRTUAL_MACHINE);
                SimpleRelationship rel = new SimpleRelationship();
                rel.setSourceElement(artifact.getId());
                rel.setTargetElement(node.getId());
                rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                
                RuntimeLogger.logger.info("Adding to " + o.getId() + " vm with ip " + ip+" and of type "+parameters.get(index));

                artifact.addNode(node, rel);
            } else {
                node.getStaticInformation().put("UUID", uuid);
                node.getStaticInformation().put("IP", ip);
                node.setId(ip);
                node.setNodeType(NodeType.VIRTUAL_MACHINE);
                SimpleRelationship rel = new SimpleRelationship();
                rel.setSourceElement(o.getId());
                rel.setTargetElement(node.getId());
                rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                RuntimeLogger.logger.info("Adding to " + o.getId() + " vm with ip " + ip+" and of type "+parameters.get(index));

                o.addNode(node, rel);
            }
        }
        RuntimeLogger.logger.info("The controlled service is now " + controlledService.toString());
        
         monitoring.refreshServiceStructure(controlledService);
        return true;
        }else{
            return scaleOutComponent(o);
        }
       
    }
 
    private boolean scaleOutComponent(Node o) {
        DependencyGraph graph = new DependencyGraph();
        graph.setCloudService(controlledService);
        RuntimeLogger.logger.info("~~~~~~~~~~~~~~~~~~~~~~Image from which we create " + (String) o.getStaticInformation("DefaultImage"));
        String uuid = UUID.randomUUID().toString();
        Node node = new Node();
        Random r = new Random();
        String ip = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);


        if (!ip.equalsIgnoreCase("err") && !ip.equalsIgnoreCase("")) {
            if (o.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {

                Node artifact = node.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);
                node.getStaticInformation().put("UUID", uuid);
                node.getStaticInformation().put("IP", ip);
                node.setId(ip);
                node.setNodeType(NodeType.VIRTUAL_MACHINE);
                SimpleRelationship rel = new SimpleRelationship();
                rel.setSourceElement(artifact.getId());
                rel.setTargetElement(node.getId());
                rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                RuntimeLogger.logger.info("Adding to " + o.getId() + " vm with ip " + ip);

                artifact.addNode(node, rel);
            } else {
                node.getStaticInformation().put("UUID", uuid);
                node.getStaticInformation().put("IP", ip);
                node.setId(ip);
                node.setNodeType(NodeType.VIRTUAL_MACHINE);
                SimpleRelationship rel = new SimpleRelationship();
                rel.setSourceElement(o.getId());
                rel.setTargetElement(node.getId());
                rel.setType(RelationshipType.HOSTED_ON_RELATIONSHIP);
                RuntimeLogger.logger.info("Adding to " + o.getId() + " vm with ip " + ip);

                o.addNode(node, rel);
            }
        }
        RuntimeLogger.logger.info("The controlled service is now " + controlledService.toString());

        monitoring.refreshServiceStructure(controlledService);
        return true;
    }

    private void scaleInComponent(Node o) {

        DependencyGraph graph = new DependencyGraph();
        graph.setCloudService(controlledService);
        Node toBeScaled = graph.getNodeWithID(o.getId());
        Node toBeRemoved=null;
        if (toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT) != null && toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).size() > 0) {
            Node artifact = toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.ARTIFACT).get(0);
            if (artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER)!=null && artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).size()>0){
            Node container = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.CONTAINER).get(0);
              toBeRemoved = container.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
            RuntimeLogger.logger.info("Trying to remove  " + toBeRemoved.getId() + " From " + toBeScaled.getId());
            String cmd = "";
            String ip = toBeRemoved.getId();
            String uuid = (String) toBeRemoved.getStaticInformation().get("UUID");
            RuntimeLogger.logger.info("Removing server with UUID" + uuid);
            }else{
                 toBeRemoved = artifact.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
            RuntimeLogger.logger.info("Trying to remove  " + toBeRemoved.getId() + " From " + toBeScaled.getId());
            String cmd = "";
            String ip = toBeRemoved.getId();
            String uuid = (String) toBeRemoved.getStaticInformation().get("UUID");
            RuntimeLogger.logger.info("Removing server with UUID" + uuid);
            }

        } else {

             toBeRemoved = toBeScaled.getAllRelatedNodesOfType(RelationshipType.HOSTED_ON_RELATIONSHIP, NodeType.VIRTUAL_MACHINE).get(0);
            RuntimeLogger.logger.info("Trying to remove  " + toBeRemoved.getId() + " From " + toBeScaled.getId());
            String cmd = "";
            String ip = toBeRemoved.getId();
            String uuid = (String) toBeRemoved.getStaticInformation().get("UUID");
            RuntimeLogger.logger.info("Removing server with UUID" + uuid);
        }
        //flexiantActions.removeServer(uuid);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            RuntimeLogger.logger.info(e.getMessage());
        }

        toBeScaled.removeNode(toBeRemoved);

        monitoring.refreshServiceStructure(controlledService);
    }

    private void scaleInComponent(Node o, String IP) {

        DependencyGraph graph = new DependencyGraph();
        graph.setCloudService(controlledService);
        Node toBeScaled = graph.getNodeWithID(o.getId());
        Node toBeRemoved = graph.getNodeWithID(IP);
        RuntimeLogger.logger.info("Trying to remove  " + toBeRemoved.getId() + " From " + toBeScaled.getId());
        String cmd = "";
        String ip = IP;
        String uuid = (String) toBeRemoved.getStaticInformation().get("UUID");
        RuntimeLogger.logger.info("Removing server with UUID" + uuid);



        //flexiantActions.removeServer(uuid);
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            RuntimeLogger.logger.info(e.getMessage());
        }

        toBeScaled.removeNode(toBeRemoved);

        monitoring.refreshServiceStructure(controlledService);
    }

    public Node findControllerForComponent(Node c) {
        Node res = null;
        List<Node> componentTopologies = controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY);
        for (Node componentTopology : componentTopologies) {
            for (Node topology : componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)) {
                Node master = null;
                Node slave = null;
                ArrayList<Node> comps = (ArrayList<Node>) topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP);


                for (Node comp : comps) {
                    if (comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF) != null) {
                        master = comp;
                        slave = comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF).get(0);
                    }
                }
                if (slave.getId().equalsIgnoreCase(c.getId())) {
                    for (Node component : comps) {
                        if (component.getId().equalsIgnoreCase(master.getId())) {
                            res = component;
                        }
                    }
                }


                if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY) != null) {
                    for (Node top : topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)) {
                        Node master1 = null;
                        Node slave1 = null;
                        ArrayList<Node> comps1 = (ArrayList<Node>) topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP);


                        for (Node comp : comps) {
                            if (comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF) != null) {
                                master1 = comp;
                                slave1 = comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF).get(0);
                            }
                        }

                        if (slave1.getId().equalsIgnoreCase(c.getId())) {

                            List<Node> comps2 = top.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT);
                            for (Node component : comps2) {
                                if (component.getId().equalsIgnoreCase(master1.getId())) {
                                    res = component;
                                }
                            }
                        }
                    }
                }

            }
        }
        return res;
    }

    private Node findComponentOfCodeRegion(Node e) {
        boolean found = false;
        Node topology = controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);
        List<Node> topologies = new ArrayList<Node>();
        topologies.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));

        List<Node> componentsToExplore = new ArrayList<Node>();
        if (topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT) != null) {
            componentsToExplore.addAll(topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
        }
        while (!topologies.isEmpty()) {
            Node currentTopology = topologies.get(0);
            topologies.remove(0);
            if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).size() > 0) {
                topologies.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY));
            }
            if (currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT).size() > 0) {
                componentsToExplore.addAll(currentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT));
            }

        }

        while (!componentsToExplore.isEmpty()) {
            Node component = componentsToExplore.get(0);
            componentsToExplore.remove(0);
            for (Node codeRegion : component.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.CODE_REGION)) {
                if (codeRegion.getId().equalsIgnoreCase(e.getId())) {
                    return component;
                }
            }
        }

        return null;
    }

    private Node findParentNode(String entityID, Node controlledService) {
        if (controlledService.getId().equalsIgnoreCase(entityID)) {
            return null;
        }
        Node topology = controlledService.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY).get(0);

        if (topology.getId().equalsIgnoreCase(entityID)) {
            //targetedNode=topology;
            return controlledService;
        }
        for (Node componentTopology : topology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)) {
            if (componentTopology.getId().equalsIgnoreCase(entityID)) {
                //targetedNode=componentTopology;
                return topology;
            }
            for (Node subTopology : componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_TOPOLOGY)) {
                if (subTopology.getId().equalsIgnoreCase(entityID)) {
                    //targetedNode=subTopology;
                    return componentTopology;
                }
                for (Node comp : subTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)) {
                    if (comp.getId().equalsIgnoreCase(entityID)) {
                        //targetedNode=comp;
                        return subTopology;
                    }
                }
            }
            for (Node comp : componentTopology.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP, NodeType.SERVICE_UNIT)) {
                if (comp.getId().equalsIgnoreCase(entityID)) {
                    //targetedNode=comp;
                    return componentTopology;
                }
            }
        }
        return null;
    }

    public boolean scaleIn(Node arg0) {
        RuntimeLogger.logger.info("Scaling in..." + arg0.getId());

        if (arg0.getNodeType() == NodeType.CODE_REGION) {
            scaleIn(findComponentOfCodeRegion(arg0));
        }

        //TODO : enable just ComponentTopology level 
        if (arg0.getNodeType() == NodeType.SERVICE_UNIT) {
            scaleInComponent(arg0);
        }

        if (arg0.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
            ArrayList<Node> comps = (ArrayList<Node>) arg0.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP);
            Node master = null;
            Node slave = null;

            for (Node comp : comps) {
                if (comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF) != null) {
                    master = comp;
                    slave = comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF).get(0);
                }
            }

            for (Node component : comps) {
                if (component.getId().equalsIgnoreCase(master.getId())) {
                    master = component;
                }
                if (component.getId().equalsIgnoreCase(slave.getId())) {
                    slave = component;
                }

            }

            for (String ip : master.getAssociatedIps()) {
                if (ip.split("\\.")[0].length() == 2) {
                    //monitoring.enforcingActionStarted("ScaleIn",arg0 );

                    //flexiantActions.scaleInCluster(master, slave, ip,controlledService);
                    //monitoring.enforcingActionEnded("ScaleIn",arg0 );
                    break;
                }
                //scale in on the number of components of the topology
            }
        }
        return true;


    }

    public boolean scaleIn(Node arg0, String IP) {
        RuntimeLogger.logger.info("Scaling in..." + arg0.getId());

        if (arg0.getNodeType() == NodeType.CODE_REGION) {
            scaleIn(findComponentOfCodeRegion(arg0));
        }

        //TODO : enable just ComponentTopology level 
        if (arg0.getNodeType() == NodeType.SERVICE_UNIT) {
            scaleInComponent(arg0, IP);
        }

        if (arg0.getNodeType() == NodeType.SERVICE_TOPOLOGY) {
            ArrayList<Node> comps = (ArrayList<Node>) arg0.getAllRelatedNodesOfType(RelationshipType.COMPOSITION_RELATIONSHIP);
            Node master = null;
            Node slave = null;

            for (Node comp : comps) {
                if (comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF) != null) {
                    master = comp;
                    slave = comp.getAllRelatedNodesOfType(RelationshipType.MASTER_OF).get(0);
                }
            }

            for (Node component : comps) {
                if (component.getId().equalsIgnoreCase(master.getId())) {
                    master = component;
                }
                if (component.getId().equalsIgnoreCase(slave.getId())) {
                    slave = component;
                }

            }

            for (String ip : master.getAssociatedIps()) {
                if (ip.split("\\.")[0].length() == 2) {
                    //monitoring.enforcingActionStarted("ScaleIn",arg0 );

                    //flexiantActions.scaleInCluster(master, slave, ip,controlledService);
                    //monitoring.enforcingActionEnded("ScaleIn",arg0 );
                    break;
                }
                //scale in on the number of components of the topology
            }
        }



        return true;
    }

    public List<String> getElasticityCapabilities() {
        List<String> list = new ArrayList<String>();
        list.add("scaleOut");
        list.add("scaleIn");
        return list;
    }

    public List<String> getElasticityCapabilities(Node cloudService) {
        List<String> list = new ArrayList<String>();
        list.add("scaleOut");
        list.add("scaleIn");
        return list;
    }

    public void setControlledService(Node controlledService) {
        this.controlledService = controlledService;
    }

    public Node getControlledService() {
        // TODO Auto-generated method stub
        return controlledService;
    }

    @Override
    public void setMonitoringPlugin(MonitoringAPIInterface monitoring) {
        this.monitoring = monitoring;
    }

   
    @Override
    public boolean containsElasticityCapability(Node entity, String capability) {
        for (String cap : getElasticityCapabilities()) {
            if (cap.equalsIgnoreCase(capability)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void undeployService(Node serviceID) {
       
    }

    @Override
    public boolean scaleOut(Node toBeScaled, double violationDegree) {
        System.out.println("scaling out "+toBeScaled+" violation degree "+violationDegree);
        return true;
    }

    @Override
    public boolean scaleIn(Node toBeScaled, double violationDegree) {
        System.out.println("scaling in "+toBeScaled+" violation degree "+violationDegree);
        return true;
    }

    @Override
    public boolean enforceAction(Node serviceID, String actionName) {
        System.out.println("Enforcing action "+actionName);
        return true;
    }

  
}
