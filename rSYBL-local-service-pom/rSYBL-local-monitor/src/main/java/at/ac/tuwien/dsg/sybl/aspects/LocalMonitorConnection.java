package at.ac.tuwien.dsg.sybl.aspects;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import at.ac.tuwien.dsg.sybl.localCommunication.communicationInterface.LocalProcessingInterface;

public class LocalMonitorConnection {
	public static  LocalProcessingInterface stub;
	{
try {
			
		    Registry registry = LocateRegistry.getRegistry(3010);
		    stub = (LocalProcessingInterface) registry.lookup("rmiSYBL");

		} catch (Exception e) {
		    System.err.println("Client exception: " + e.toString());
		    e.printStackTrace();
		}
	}

}
