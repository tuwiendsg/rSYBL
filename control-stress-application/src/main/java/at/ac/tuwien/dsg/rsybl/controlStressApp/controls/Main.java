package at.ac.tuwien.dsg.rsybl.controlStressApp.controls;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FlexiantActions flexiantActions=new FlexiantActions();
		System.out.println(flexiantActions.createNewServer("DataNode_new","58fed54b-dbdb-3d53-80bb-5ab59ee067a7", 2, 4));
	}

}
