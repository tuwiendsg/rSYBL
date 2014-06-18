package at.ac.tuwien.dsg.rSybl.planningEngine;

import java.util.ArrayList;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityCapability;
import at.ac.tuwien.dsg.rSybl.cloudInteractionUnit.api.EnforcementAPIInterface;
import at.ac.tuwien.dsg.rSybl.planningEngine.PlanningGreedyAlgorithm.Pair;
import at.ac.tuwien.dsg.rSybl.planningEngine.staticData.ActionEffect;

public class ActionPlanEnforcement {
	EnforcementAPIInterface enforcementAPI = null;
	public ActionPlanEnforcement(EnforcementAPIInterface apiInterface){
		enforcementAPI=apiInterface;
	}
	public void enforceResult(ArrayList<Pair<ActionEffect, Integer>>  result){
		for (Pair<ActionEffect, Integer> actionEffect : result){
			boolean foundCapability=false;
			for (ElasticityCapability capability: actionEffect.getFirst().getTargetedEntity().getElasticityCapabilities()){
				if (capability.getName()!=null && !capability.getName().equalsIgnoreCase("")){
					if (capability.getEndpoint()!=null && !capability.getEndpoint().equalsIgnoreCase("")){
						foundCapability=true;
						enforcementAPI.enforceElasticityCapability(capability, actionEffect.getFirst().getTargetedEntity());
					}
				}
			}

			if (!foundCapability){
				if (actionEffect.getSecond()>0)
				for (int i=0;i<actionEffect.getSecond();i++){
					enforceAction(actionEffect.getFirst());
				}
			}
			
			}
	}
	public void enforceAction(ActionEffect actionEffect){
		String target="";
		String actionName=actionEffect.getActionName().toLowerCase();
		if (actionEffect.getActionName().contains(".")){
			 target = actionEffect.getActionName().split("\\.")[0].toLowerCase();
			 actionName=actionEffect.getActionName().split("\\.")[1].toLowerCase();
		}
	if (target.equalsIgnoreCase(""))
	{
		for (ElasticityCapability capability:actionEffect.getTargetedEntity().getElasticityCapabilities()){
			if (capability.getName().toLowerCase().contains(actionName.toLowerCase())){
				target = capability.getName().split("\\.")[0].toLowerCase();
			}
		}
	}
	if (target.equalsIgnoreCase("")){
		switch (actionEffect.getActionName().toLowerCase()){
		case "scaleout":
			
			enforcementAPI.scaleout(actionEffect.getTargetedEntity());
			break;
		case "scalein":
				enforcementAPI.scalein(actionEffect.getTargetedEntity());			
				break;
		default:
				enforcementAPI.enforceAction(actionEffect.getActionType(),actionEffect.getTargetedEntity());	
			break;
		}
	}
	else{
		switch (actionEffect.getActionName().split("\\.")[1].toLowerCase()){
		case "scaleout":
			enforcementAPI.scaleout(target,actionEffect.getTargetedEntity());
			break;
		case "scalein":
			enforcementAPI.scalein(target,actionEffect.getTargetedEntity());
				break;
		default:
			if (target.equalsIgnoreCase(""))
				enforcementAPI.enforceAction(target,actionEffect.getActionType(),actionEffect.getTargetedEntity());
				
			break;
		}
	}
	}
	public void enforceFoundActions(ContextRepresentation contextRepresentation){
		
		for (ActionEffect actionEffect:contextRepresentation.getActionsAssociatedToContext()){
			enforceAction(actionEffect);
		}
			
		}
	

}
