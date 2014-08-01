package at.ac.tuwien.dsg.csdg;

import at.ac.tuwien.dsg.csdg.elasticityInformation.ElasticityRequirement;

public class InstantiationElasticityDependency extends SimpleRelationship{
	public enum TypesOfInstantiationDependency {SIMPLE_INSTANTIATION_DEPENDENCY, REQUIREMENT_BASED_INSTANTIATION_DEPENDENCY};
	private TypesOfInstantiationDependency instantiationDependencyType=TypesOfInstantiationDependency.SIMPLE_INSTANTIATION_DEPENDENCY;
	private String focusMetric;

	public TypesOfInstantiationDependency getInstantiationDependencyType() {
		return instantiationDependencyType;
	}

	public void setInstantiationDependencyType(
			TypesOfInstantiationDependency instantiationDependencyType) {
		this.instantiationDependencyType = instantiationDependencyType;
	}

	public String getFocusMetric() {
		return focusMetric;
	}

	public void setFocusMetric(String focusMetric) {
		this.focusMetric = focusMetric;
	}
	
	
	
}
