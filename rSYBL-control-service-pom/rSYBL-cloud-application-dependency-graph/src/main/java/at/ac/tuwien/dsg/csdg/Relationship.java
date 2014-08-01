/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.csdg;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;

/**
 *
 * @author Georgiana
 */
public abstract class Relationship {
    public static enum RelationshipType{
		   COMPOSITION_RELATIONSHIP,HOSTED_ON_RELATIONSHIP, ASSOCIATED_AT_RUNTIME_RELATIONSHIP, RUNS_ON, MASTER_OF, PEER_OF,DATA,LOAD,INSTANTIATION,POLYNIMIAL_RELATIONSHIP;
		 }
    public abstract RelationshipType getType();
    public abstract ElasticityRequirement getRequirement();
}
