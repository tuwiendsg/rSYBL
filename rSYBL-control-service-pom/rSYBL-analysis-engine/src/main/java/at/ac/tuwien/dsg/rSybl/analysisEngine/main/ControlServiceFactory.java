package at.ac.tuwien.dsg.rSybl.analysisEngine.main;

import at.ac.tuwien.dsg.rSybl.analysisEngine.utils.AnalysisLogger;


public class ControlServiceFactory {
	private static ControlService controlService;
	public static ControlService getControlServiceInstance(String id){

		if (controlService==null){
			controlService=new ControlService(id);
		}
		
		return controlService;
	}
}
