package at.ac.tuwien.dsg.rSybl.planningEngine;

public interface PlanningAlgorithmInterface extends Runnable{
	public void start();
	public void stop();
	public void setEffects(String effects);
}
