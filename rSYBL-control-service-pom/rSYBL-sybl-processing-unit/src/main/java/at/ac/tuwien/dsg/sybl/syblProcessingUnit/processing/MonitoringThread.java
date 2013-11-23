/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed SystemsGroup E184.               
   
   This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790).
 
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

/**
 *  Author : Georgiana Copil - e.copil@dsg.tuwien.ac.at
 */

package at.ac.tuwien.dsg.sybl.syblProcessingUnit.processing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.sybl.syblProcessingUnit.exceptions.MethodNotFoundException;
import at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils.EnvironmentVariable;







public class MonitoringThread  implements Runnable  {
		private String monitoring;
		long timestamp;
		Thread t;
		Utils utils;
		HashMap<EnvironmentVariable, Comparable> monitoredVariables ;
		//private boolean ok = true;
		MonitoringThread(Utils utils,HashMap<EnvironmentVariable, Comparable> monitoredVariables, String monitoring, long timestamp) {
			// Create a new, second thread
			this.monitoredVariables = monitoredVariables;
			this.setMonitoring(monitoring);
			this.timestamp = timestamp;
			this.utils= utils;
			t = new Thread (this);
			
		}

		
		public boolean equals(Object o){
			MonitoringThread m = (MonitoringThread) o;
			if (m.getMonitoring().equals(monitoring))return true;
			else return false;
		}
		public void run() {
			
			while (true) {

				try {
					utils.processSimpleMonitoringRule(getMonitoring());
				} catch (MethodNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					// this.wait((long) timestamp);

					Thread.sleep(timestamp);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}
		}

		public void stop() {
			//ok = false;
			t.stop();
		}
		public void start(){
			t.start();
		}


		String getMonitoring() {
			return monitoring;
		}


		void setMonitoring(String monitoring) {
			this.monitoring = monitoring;
		}
	
	
	class Logging implements Runnable{
		Thread t ;
		public final int loggingTime = 5000;
		String header= "";
		ArrayList<String> vars = new ArrayList<String>();
		boolean headerWritten = false;
		BufferedWriter bufferedWriter = null;
		public Logging(BufferedWriter bufferedWriter){
			t = new Thread(this);
			this.bufferedWriter = bufferedWriter;
		}
		public void stop(){
			try {
				bufferedWriter.write(header);
				bufferedWriter.newLine();
				for (String s : vars){
					bufferedWriter.write(s);
					bufferedWriter.newLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bufferedWriter.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t.stop();
		}
		public void start(){
			t.start();
		}
		@Override
		public void run() {
			
			while (true){
				if (!headerWritten){
					String h= "";
					for (EnvironmentVariable m: monitoredVariables.keySet()){
						 h +=m.getName()+","; 
						
					}
					header = h;
				}
				String current = "";
				for (EnvironmentVariable m: monitoredVariables.keySet()){
					current +=monitoredVariables.get(m)+","; 
				}
				
				
				if (!current.equalsIgnoreCase(""))
				vars.add(current);
				try {
					Thread.sleep(loggingTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					try {
						bufferedWriter.write(header);
						bufferedWriter.newLine();
						for (String s : vars){
							bufferedWriter.write(s);
							bufferedWriter.newLine();
						}
					} catch (IOException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					try {
						bufferedWriter.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						bufferedWriter.close();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					e.printStackTrace();
				}
			}
		}
		
	}
}
