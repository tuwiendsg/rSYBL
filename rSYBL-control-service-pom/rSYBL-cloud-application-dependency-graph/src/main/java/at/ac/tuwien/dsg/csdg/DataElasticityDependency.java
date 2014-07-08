package at.ac.tuwien.dsg.csdg;

public class DataElasticityDependency extends Relationship{
	private String dataMeasurementSource;
	private String dataMeasurementTarget;
	//Assumption: there is 1-1 dependency among these two. will also implement more complex things later on
	public String getDataMeasurementSource() {
		return dataMeasurementSource;
	}
	public void setDataMeasurementSource(String dataMeasurementSource) {
		this.dataMeasurementSource = dataMeasurementSource;
	}
	public String getDataMeasurementTarget() {
		return dataMeasurementTarget;
	}
	public void setDataMeasurementTarget(String dataMeasurementTarget) {
		this.dataMeasurementTarget = dataMeasurementTarget;
	}
}
