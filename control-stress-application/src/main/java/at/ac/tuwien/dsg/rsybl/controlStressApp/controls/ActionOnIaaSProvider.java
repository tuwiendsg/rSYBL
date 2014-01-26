package at.ac.tuwien.dsg.rsybl.controlStressApp.controls;



public abstract class ActionOnIaaSProvider {
	public abstract String createNewServer(String imageUUID, int cpu, int mem);
}
