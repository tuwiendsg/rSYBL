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




import java.rmi.RemoteException;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;

import at.ac.tuwien.dsg.cloudServiceDependencyGraph.elasticityInformation.elasticityRequirements.SYBLAnnotation;
import at.ac.tuwien.dsg.sybl.directives.SYBL_CodeRegionDirective;
import at.ac.tuwien.dsg.sybl.directives.SYBL_CodeRegionDirective.AnnotType;
import at.ac.tuwien.dsg.sybl.localCommunication.communicationInterface.LocalProcessingInterface;



public aspect ProcessingAspect_CodeRegion {
	LocalProcessingInterface stub;
	String processID="";
	//after():
	  //      @annotation(SYBL_CodeRegionDirective) && call(* *(..)){
	@After("execution(* *.*(..)) && @annotation(testAnnotation) ")
	public void afterAnnotationExecution( SYBL_CodeRegionDirective testAnnotation) {	
		stub=LocalMonitorConnection.stub;

		if (stub!=null){
			//stop monitoring
			//start in case it contains an after clause
		
			SYBL_CodeRegionDirective currentAnnotation = testAnnotation;
			if (currentAnnotation!=null&& (currentAnnotation.type()==AnnotType.AFTER)){
			SYBLAnnotation annotation = new SYBLAnnotation();
			annotation.setConstraints(currentAnnotation.constraints());
			annotation.setMonitoring(currentAnnotation.monitoring());
			annotation.setPriorities(currentAnnotation.priorities());
			annotation.setStrategies(currentAnnotation.strategies());
			annotation.setAnnotationType(SYBLAnnotation.AnnotationType.CODE_REGION);

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
       // @annotation(SYBL_CodeRegionDirective) && call(* *(..)){
	@Before("execution(* *.*(..)) && @annotation(testAnnotation) ")
	public void beforeAnnotationExecution( SYBL_CodeRegionDirective testAnnotation) {
		stub=LocalMonitorConnection.stub;

		if (stub!=null){
		
		//String name =joinPoint.getSignature().getName();
		
		SYBL_CodeRegionDirective currentAnnotation = testAnnotation;
		if (currentAnnotation!=null){
		SYBLAnnotation annotation = new SYBLAnnotation();
		annotation.setConstraints(currentAnnotation.constraints());
		annotation.setMonitoring(currentAnnotation.monitoring());
		annotation.setPriorities(currentAnnotation.priorities());
		annotation.setStrategies(currentAnnotation.strategies());
		annotation.setAnnotationType(SYBLAnnotation.AnnotationType.CODE_REGION);

		if (currentAnnotation.type()==AnnotType.BEFORE){
		try {
			stub.processAnnotationOnce(processID, "", annotation);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}else{
			try {
				stub.processAnnotationLoop(processID, "", annotation);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		}
		}
	}
		
}
