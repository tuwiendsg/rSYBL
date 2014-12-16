package at.ac.tuwien.dsg.rSybl.planningEngine;

import at.ac.tuwien.dsg.csdg.DependencyGraph;

public interface PlanningAlgorithmInterface extends Runnable{
	public void start();
	public void stop();
	public void setEffects(String effects);
        public void replaceDependencyGraph(DependencyGraph dependencyGraph);
        
}
