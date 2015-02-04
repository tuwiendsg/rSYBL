/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.rSybl.dataProcessingUnit.monitoringPlugins.melaPlugin;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.rSybl.dataProcessingUnit.api.model.MonitoringSnapshot;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Georgiana
 */
public class RecordedInfoProcessing {
    public static void main(String[] args){
        MELA_API3 mela_api3 = new MELA_API3();
        Node node = new Node();
        node.setId("ElasticIoTPlatform");
        mela_api3.setCurrentCloudService(node);
        List<MonitoringSnapshot> list= mela_api3.getAllMonitoringInformation();
        boolean created = false;
        for (MonitoringSnapshot monitoringSnapshot:list.subList(5,list.size()-1)){
          long currentTimestamp = monitoringSnapshot.getTimestamp();
          HashMap<String, String> actions=monitoringSnapshot.getOngoingActions();
          for (String servicePart:monitoringSnapshot.getServiceParts().keySet()){
              HashMap<String, Double> metrics=monitoringSnapshot.getServiceParts().get(servicePart).getMetrics();
              if (created==false){
                  
		try{
		 String fileName = "./monitoring/"+servicePart+".csv";
		 FileWriter fstream = new FileWriter(fileName);
		  String headers = "Time ,";
		  for (String metric :metrics.keySet())
			  headers+=metric+" ,";
		  headers +=" ongoingAction, targetedNodeID";
		  headers += "\n";
		  fstream.write(headers);
		  fstream.close();
		}catch(IOException ex){
			ex.printStackTrace();
		}
              }
              String fileName = "./monitoring/"+servicePart+".csv";
               FileWriter fstream=null;
                try {
                    fstream = new FileWriter(fileName,true);
                } catch (IOException ex) {
                    Logger.getLogger(RecordedInfoProcessing.class.getName()).log(Level.SEVERE, null, ex);
                }
				  String toWrite = currentTimestamp+",";
				  for (String metric :metrics.keySet())
			    toWrite+=metrics.get(metric) +",";
                            for (String action :actions.keySet()){
                                
				  toWrite+=action+","+actions.get(action)+",";
                            }
                try {
                    fstream.write(toWrite+'\n');
                      fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(RecordedInfoProcessing.class.getName()).log(Level.SEVERE, null, ex);
                }
                            
				
          }
          created=true;
        }
    }
}
