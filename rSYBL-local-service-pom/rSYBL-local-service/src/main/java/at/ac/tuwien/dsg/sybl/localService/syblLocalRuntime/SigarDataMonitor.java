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
package at.ac.tuwien.dsg.sybl.localService.syblLocalRuntime;




import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class SigarDataMonitor {

    private Sigar sigar;
    
    private float currentLoad = 0;
    private float cpuSize = 0;
    private float cpuSpeed = 0;
    private float nbCores = 0;
    
    private float currentMemUsage = 0;
    private float memSize = 0;
    
    private float currentDiskUsage = 0;
    private float diskSize = 0;

	public SigarDataMonitor() {
         sigar = new Sigar();
      
    }

    public double getCurrentCpuUsage()  {
    	
        float value = 0;
		try {
			value = (float) (sigar.getCpuPerc().getCombined());
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (value >= 0 && value <= 1) {
        	currentLoad = value*100;
            return currentLoad;
        } else {
            return currentLoad;
        }
        
    }

    public float getNbCores() throws SigarException{
    float value =  sigar.getCpuInfoList()[0].getTotalCores();
    if (value > 0) {
        nbCores = value;
        return value;
    } else {
        return nbCores;
    }

    }
    
    public float getCpuSize() throws SigarException{
    float value = sigar.getCpuInfoList()[0].getMhz();
    
    if (value > 0 ) {
        cpuSize = value;
        return value;
    } else {
        return cpuSize;
    }

    }
    public float getMemSize() throws SigarException{
        float value =  sigar.getMem().getTotal();
        if (value > 0) {
        	memSize = value;
            return value;
        } else {
            return memSize;
        }
    }
    public float getMemUsage() throws SigarException{
    	 float value = (float) (sigar.getMem().getUsedPercent());
         if (value > 0 && value < 100) {
        	 currentMemUsage = value;
             return currentMemUsage;
         } else {
             return currentMemUsage;
         }
         
    
    }
    public Float tryStuff() throws SigarException{
    	//System.out.println(sigar.getPid());
    	long pid = sigar.getPid();
    	System.out.println(sigar.getProcMem(pid).getSize()/(1024));
    	return (float) sigar.getProcCpu(pid).getPercent();
    }
    /**
	 * @param args
     * @throws SigarException 
	 */
	public static void main(String[] args) throws SigarException {
			new SigarDataMonitor().tryStuff();
	}
   
// can get reads and writes for the disk .. 

}
