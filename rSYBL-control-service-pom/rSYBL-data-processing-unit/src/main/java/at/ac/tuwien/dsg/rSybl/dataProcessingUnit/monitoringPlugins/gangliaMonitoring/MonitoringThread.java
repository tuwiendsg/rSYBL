/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.                 This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).

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
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.gangliaMonitoring;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

import at.ac.tuwien.dsg.csdg.Node;



public class MonitoringThread implements Runnable{
	Thread t;
	Node entity;
	BufferedWriter out;
	MonitoringGangliaAPI api;
	FileWriter fstream;
	String fileName ;
	public MonitoringThread(Node e,MonitoringGangliaAPI apiGanglia){
		api=apiGanglia;
		
		entity = e;
		//Create file for current entity
		Date date = new Date();
		try{
			 fileName = "./monitoring/"+e.getId()+"_"+date.getHours()+"_"+date.getMinutes()+".csv";
		  fstream = new FileWriter(fileName);
		   
		  fstream.write("time, cpuUsage, memUsage, diskUsage, latency, cost \n");
		  fstream.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		  t = new Thread(this);
	}

	
	public void run() {
		while (true){
			try {
				  fstream = new FileWriter(fileName,true);
				  Date date = new Date();
				  if (!api.isExecutingControlAction())
				  fstream.write(date.getHours()+"_"+date.getMinutes()+","+api.getCpuUsage(entity)+","+api.getMemoryUsage(entity)+","+api.getDiskUsage(entity)+","+((double)api.getReadLatency(entity)+api.getWriteLatency(entity))+","+api.getCostPerHour(entity)+"\n");
				  else
					  fstream.write(date.getHours()+"_"+date.getMinutes()+","+0+","+0+","+0+","+0+","+0+","+"Executing action... "+"\n");
				  
				  fstream.close();
				  Thread.sleep(5000);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void start(){
		
		t.start();
		
	}
	public void stop(){
		t.stop();

		try {
			fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
