package at.ac.tuwien.dsg.sybl.syblProcessingUnit.exceptions;

public class MeasurementNotAvailableException extends Exception {
	private String name;
	public MeasurementNotAvailableException(String name){
		super (name);
		this.name=name;
	}
	public String getMessage(){
		return name;
	}
}
