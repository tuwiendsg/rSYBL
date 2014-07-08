package at.ac.tuwien.dsg.csdg;

public class LoadElasticityDependency extends Relationship{
	private String sourceLoadMetric;
	private String targetLoadMetric;
	public String getSourceLoadMetric() {
		return sourceLoadMetric;
	}
	public void setSourceLoadMetric(String sourceLoadMetric) {
		this.sourceLoadMetric = sourceLoadMetric;
	}
	public String getTargetLoadMetric() {
		return targetLoadMetric;
	}
	public void setTargetLoadMetric(String targetLoadMetric) {
		this.targetLoadMetric = targetLoadMetric;
	}
}
