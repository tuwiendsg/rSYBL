/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package at.ac.tuwien.dsg.sybl.localService.main;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.jclouds.encryption.internal.Base64.InputStream;

import at.ac.tuwien.dsg.sybl.localCommunication.communicationInterface.LocalProcessingInterface;
import at.ac.tuwien.dsg.sybl.localService.utils.Configuration;



public class RMIServer {
	public static void main(String args[]){ 
	    try {
	    	
	            //System.setSecurityManager(new SecurityManager());
	           
	    
	    	LocalProcessing obj = new LocalProcessing();
	    	LocalProcessingInterface stub = (LocalProcessingInterface) UnicastRemoteObject.exportObject((Remote) obj, 0);
	    	
	    	 // Bind the remote object's stub in the registry
	    	Registry registry = null;
		    registry = LocateRegistry.createRegistry(Configuration.getRMIPort());
			registry.rebind(Configuration.getRMIName(), stub);
		} catch ( RemoteException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
