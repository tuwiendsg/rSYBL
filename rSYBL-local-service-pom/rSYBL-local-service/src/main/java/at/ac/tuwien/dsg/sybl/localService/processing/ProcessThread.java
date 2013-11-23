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
package at.ac.tuwien.dsg.sybl.localService.processing;

import java.util.HashMap;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;




public class ProcessThread {
	HashMap<String,SYBLProcessingThread> processThreads=new HashMap<String,SYBLProcessingThread>();
	private String pid;
	public ProcessThread(){
		
	}
	public void newAnnotationsLoop(String method, SYBLAnnotation  syblAnnotation){
		SYBLProcessingThread processingThread = new SYBLProcessingThread(syblAnnotation);
		processThreads.put(method, processingThread);
		processingThread.start();
	}
	public void newAnnotationsOnce(String method,SYBLAnnotation syblAnnotation){
		SYBLProcessingThread processingThread = new SYBLProcessingThread(syblAnnotation);
		processingThread.singleRun();
	}
	
	public void stopAnnotations(String method){
		if (processThreads.containsKey(method)){
		processThreads.get(method).stop();	
		}

	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}

	
}
