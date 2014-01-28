package at.ac.tuwien.dsg.rsybl.controlStressApp.controls;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FlexiantActions flexiantActions=new FlexiantActions();
		System.out.println(flexiantActions.createNewServer("DataNode_new","c569fa37-a8fa-384a-8ab1-365efb157105	", 2, 4));
	}

}
