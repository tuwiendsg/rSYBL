/** 
   Copyright 2013 Technische Universität Wien (TUW), Distributed Systems Group E184

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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.controlService.service.ControlService;
import at.ac.tuwien.dsg.sybl.controlService.service.MapFromSYBLAnnotationtoSyblAnnotation;
import at.ac.tuwien.dsg.sybl.controlService.service.SyblControlWebService;
import at.ac.tuwien.dsg.sybl.localCommunication.communicationInterface.LocalProcessingInterface;
import at.ac.tuwien.dsg.sybl.localService.processing.ProcessThread;






public class LocalProcessing implements Serializable,LocalProcessingInterface{
	HashMap<String, ProcessThread> processes = new HashMap<String,ProcessThread>();
	ControlService apiService_Service = new ControlService();
	SyblControlWebService syblCoordinator=apiService_Service.getSyblControlWebServicePort();
	public LocalProcessing(){
		
		try {
			SecurityManager securityManager = new SecurityManager();
		    System.setSecurityManager(securityManager);
		    Registry registry = LocateRegistry.getRegistry(3030);
		    
		    		   
		} catch (Exception e) {
		    System.err.println("Client exception SYBLService: " + e.toString());
		    e.printStackTrace();
		}
	}
	
	public void processAnnotationOnce(String processID, String methodName, SYBLAnnotation annotation) {
		InetAddress addr=null;
		System.out.println("Processing annotation"+annotation.toString());
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		syblCoordinator.processAnnotation(annotation.getEntityID(), MapFromSYBLAnnotationtoSyblAnnotation.annotationToAnnotation(annotation));

//TODO : add local processing
	}
	
	public void processAnnotationLoop(String processID, String methodName,
			SYBLAnnotation annotation) {
		System.out.println("Processing annotaiton"+annotation.toString());

		if (processes.containsKey(processID)){
			ProcessThread processThread = processes.get(processID);
			processThread.newAnnotationsLoop(methodName, annotation);
		}else{
			ProcessThread processThread = new ProcessThread();
			processThread.setPid(processID);
			processThread.newAnnotationsLoop(methodName, annotation);
			processes.put(processID, processThread);
		}
		
	}
	
	public void stopAnnotation(String processID, String methodName) {
		processes.get(processID).stopAnnotations(methodName);
		
	}
	
}
