package at.ac.tuwien.dsg.sybl.controlService.service;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;


public class MapFromSYBLAnnotationtoSyblAnnotation {


	public static SyblAnnotation annotationToAnnotation(SYBLAnnotation syblAnnotation){
		//System.out.println("Received as parameter " + syblAnnotation);
		//System.out.println("Object factory " +objectFactory);
		SyblAnnotation syblAnnotation1 = new SyblAnnotation();
		//System.err.println(syblAnnotation1);
		syblAnnotation1.setConstraints(syblAnnotation.getConstraints());
		syblAnnotation1.setEntityID(syblAnnotation.getEntityID());
		syblAnnotation1.setMonitoring(syblAnnotation.getMonitoring());
		syblAnnotation1.setPriorities(syblAnnotation.getPriorities());
		syblAnnotation1.setStrategies(syblAnnotation.getStrategies());
		
		switch (syblAnnotation.getAnnotationType()){
		case CLOUD_SERVICE : syblAnnotation1.setAnnotationType(AnnotationType.CLOUD_SERVICE);
		break;
		case CODE_REGION : syblAnnotation1.setAnnotationType(AnnotationType.CODE_REGION);
		break;
		case SERVICE_UNIT : syblAnnotation1.setAnnotationType(AnnotationType.SERVICE_UNIT);
		break;
		case SERVICE_TOPOLOGY : syblAnnotation1.setAnnotationType(AnnotationType.SERVICE_TOPOLOGY);
		break;
		
		}
		return syblAnnotation1;
		
	}
	
}
