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
package at.ac.tuwien.dsg.csdg.inputProcessing.multiLevelModel.abstractModelXML;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import at.ac.tuwien.dsg.csdg.elasticityInformation.elasticityRequirements.SYBLAnnotation;







@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public abstract class EntityXML implements Serializable {


	 /**
	 * 
	 */
	private static final long serialVersionUID = 9205136294649813484L;
	private SYBLAnnotation syblannotation;
		public SYBLAnnotation getAnnotation() {
			return syblannotation;
		}
		public EntityXML(){
			
		}
		public void setAnnotation(SYBLAnnotation ann){
			syblannotation=ann;
		}
		
		 @XmlAttribute(name = "id")
		private String id;
		 @XmlElement(name = "SYBLDirective")
			private SYBLAnnotationXML syblAnnotationXML;
		 public SYBLAnnotationXML getXMLAnnotation(){
			 return syblAnnotationXML;
		 }
		 public void setXMLAnnotation(SYBLAnnotationXML annotation){
			 syblAnnotationXML=annotation;
		 }
		public abstract List<String> getAssociatedIps();
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		
}
