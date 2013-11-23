import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CloudServiceDirective;
import at.ac.tuwien.dsg.sybl.model.annotations.SYBL_CloudServiceDirective.AnnotType;


public class Main {

	/**
	 * @param args
	 */
	@SYBL_CloudServiceDirective(annotatedEntityID="CloudService",type=AnnotType.DURING,constraints="Co1:CONSTRAINT responseTime<3 ms")
	public static void main(String[] args) {

			AnnotatedClass annotatedClass = new AnnotatedClass();
			annotatedClass.sendActualizedData();
	}

}
