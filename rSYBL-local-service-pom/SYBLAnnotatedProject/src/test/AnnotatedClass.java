package test;

import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CloudServiceDirective;
import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CodeRegionDirective;
import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CodeRegionDirective.AnnotType;
import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_ServiceUnitDirective;


public class AnnotatedClass {
	 @SYBL_CloudServiceDirective(annotatedEntityID="CloudService",
	    		 					constraints="Co1:CONSTRAINT time < 10 ms; Co2:CONSTRAINT cpu.numberCores != 0", 
	    		 				  monitoring ="Mo1:MONITORING averageCost = cost.average TIMESTAMP 3 ms ; Mo2: MONITORING currentCpu = cpu.usage;Mo3:MONITORING time = runningTime.elapsed",
	    		 				  strategies="St1:STRATEGY CASE averageCost > 70 : scaleDown")
	     public void sendActualizedData(){
	    	 		ComputationalIntensive computationalIntensive = new ComputationalIntensive();
	    	 		Data data = computationalIntensive.refreshAllData();
	                new Communication().sendToAllClients(data);
	     }
	     
	     @SYBL_CodeRegionDirective(annotatedEntityID="methoddoEndOfTheMonthComputations",constraints="Co1:CONSTRAINT 98.5 <= availability.current;" +
	     		"Co2:CONSTRAINT availability.current >= 99.5 WHEN cost.average > 200 ;" +
	     		"Co3:CONSTRAINT cost.total < 500 ; Co4:CONSTRAINT cpu.size > 400 WHEN  Cost.Instant > 50;" +
	     		"Co5:CONSTRAINT cpu.size > 200",
	    		 priorities="Priority(Co1)<Priority(Co2);Priority(Co5)<Priority(Co4)", 
	    		 monitoring ="Mo1:MONITORING currentAvailability = availability.current TIMESTAMP 2 ms WHEN cost.instant < 50")
	     public void doEndOfTheMonthComputations(){
	            ComputationalIntensive computationalIntensive = new ComputationalIntensive();
	            computationalIntensive.computeMonthlyStatistics();
	     }
	 	
		 @SYBL_CodeRegionDirective(annotatedEntityID="methodaa",type=AnnotType.DURING,
				 constraints="Co1:CONSTRAINT cpuUsageData< 65;" +
						 "Co2:CONSTRAINT cpuUsageData > 30; " +
						 "Co3:CONSTRAINT cpuUsageData < 85 WHEN cost > 90", 
				  monitoring ="Mo1:MONITORING cost = cost.average TIMESTAMP 3 ms;" +
				  		"Mo2: MONITORING cpuUsageData = cpu.usage.datasource;" +
				  		"Mo3: MONITORING memUsage = memory.usage.datasource;" +
				  		"Mo4: MONITORING cpuUsage = cpu.usage",
				  strategies="St1:STRATEGY CASE Violated(Co2): scaleInDataSource; " +
				  		"St2:STRATEGY CASE Enabled(Co1) AND Violated(Co1): scaleOutDataSource",
				  priorities="Priority(Co3) > Priority(Co1)")
		 public void aa(){
			 
		 }
		 
		 @SYBL_ServiceUnitDirective(annotatedEntityID="test",
				 constraints="Co1:CONSTRAINT cpuUsage < 65;" +
						 "Co2:CONSTRAINT cpuUsage > 30; " +
						 "Co3:CONSTRAINT cpuUsage < 85 WHEN cost > 70", 
				  monitoring ="Mo1:MONITORING cost = cost.instant;" +
				  		"Mo2: MONITORING cpuUsage = cpu.usage;",
				  strategies="St1:STRATEGY CASE Violated(Co2): scaleIn; " +
				  		"St2:STRATEGY CASE Enabled(Co1) AND Violated(Co1): scaleOut;" +
				  		"St3:STRATEGY CASE Enabled(Co3) AND Violated(Co3): scaleOut;",
				  priorities="Priority(Co3) > Priority(Co1)")
		 public void test(){
			 
		 }
}


