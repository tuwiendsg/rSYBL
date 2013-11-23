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
package at.ac.tuwien.dsg.sybl.aspects;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.localCommunication.communicationInterface.LocalProcessingInterface;
import at.ac.tuwien.dsg.sybl.directives.SYBL_ServiceUnitDirective;
import at.ac.tuwien.dsg.sybl.directives.SYBL_ServiceUnitDirective.AnnotType;



public aspect ProcessingAspect_ServiceUnit {
	LocalProcessingInterface stub;
	String processID="";
//	{
//		
//		  String cmd = new String( "echo $PPID" ) ;  
//          System.out.println( "client command: '" + cmd + "'" ) ;  
//          try{
//                  Process myProcess = Runtime.getRuntime().exec( cmd ) ;  
//                  BufferedReader stdout = new BufferedReader( new InputStreamReader( myProcess.getInputStream() ) ) ;  
//                  String line = stdout.readLine() ;  
//                  while ( line != null )  
//                  {  
//                          System.out.println( line ) ;  
//                          line = stdout.readLine() ;  
//                  }  
//                
//                  int pid = myProcess.waitFor() ; 
//                  
//                  System.out.println("PPID"+pid);
//                  processID=""+pid;
//          }catch(Exception e){
//        	  e.printStackTrace();
//          }
//          
//		
//	}
//	
	//after():
	   //     @annotation(SYBL_ComponentDirective) && call(* *(..)){
	@After("execution(* *.*(..)) && @annotation(testAnnotation) ")
	public void afterAnnotationExecution( SYBL_ServiceUnitDirective testAnnotation) {	
		stub=LocalMonitorConnection.stub;

		if (stub!=null){
			//stop monitoring
			//start in case it contains an after clause
			SYBL_ServiceUnitDirective currentAnnotation = testAnnotation;
			if (currentAnnotation!=null&& (currentAnnotation.type()==AnnotType.AFTER)){
			SYBLAnnotation annotation = new SYBLAnnotation();
			annotation.setConstraints(currentAnnotation.constraints());
			annotation.setMonitoring(currentAnnotation.monitoring());
			annotation.setPriorities(currentAnnotation.priorities());
			annotation.setStrategies(currentAnnotation.strategies());
			try {
				stub.processAnnotationOnce(processID, "", annotation);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
			try {
				stub.stopAnnotation(processID, "");
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
	}

	//before():
     //   @annotation(SYBL_ComponentDirective) && call(* *(..)){
	@Before("execution(* *.*(..)) && @annotation(testAnnotation) ")
	public void beforeAnnotationExecution(JoinPoint joinPoint, SYBL_ServiceUnitDirective testAnnotation) {	
		stub=LocalMonitorConnection.stub;

		if (stub!=null){
			String name =joinPoint.getSignature().getName();
		SYBL_ServiceUnitDirective currentAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(
				SYBL_ServiceUnitDirective.class);
		if (currentAnnotation!=null){
		SYBLAnnotation annotation = new SYBLAnnotation();
		annotation.setConstraints(currentAnnotation.constraints());
		annotation.setMonitoring(currentAnnotation.monitoring());
		annotation.setPriorities(currentAnnotation.priorities());
		annotation.setStrategies(currentAnnotation.strategies());
		if (currentAnnotation.type()==AnnotType.BEFORE){
		try {
			stub.processAnnotationOnce(processID, name, annotation);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}else{
			try {
				stub.processAnnotationLoop(processID, name, annotation);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		}
		}
	}
}
