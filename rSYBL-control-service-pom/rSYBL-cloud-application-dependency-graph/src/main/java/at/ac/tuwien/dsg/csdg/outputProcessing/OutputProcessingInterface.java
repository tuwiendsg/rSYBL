/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg.outputProcessing;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import java.util.HashMap;

/**
 *
 * @author Georgiana
 */
public interface OutputProcessingInterface {
    public void saveActionPlan(HashMap<Node,ElasticityCapability> actionplan);
}
