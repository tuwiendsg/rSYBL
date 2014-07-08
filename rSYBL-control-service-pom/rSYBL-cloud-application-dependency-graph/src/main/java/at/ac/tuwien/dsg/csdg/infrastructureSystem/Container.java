package at.ac.tuwien.dsg.csdg.infrastructureSystem;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.csdg.Node;

public class Container extends Node{
	private List<Artifact> artifacts = new ArrayList<Artifact>();
	private List<Container> containers = new ArrayList<Container>();
	public List<Artifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<Artifact> artifacts) {
		this.artifacts = artifacts;
	}
	public void addArtifact(Artifact artifact){
		artifacts.add(artifact);
	}
	
	public void removeArtifact(Artifact artifact){
		artifacts.remove(artifact);
	}

	public List<Container> getContainers() {
		return containers;
	}

	public void setContainers(List<Container> containers) {
		this.containers = containers;
	}
	public void addContainer(Container container){
		containers.add(container);
	}
	

}
