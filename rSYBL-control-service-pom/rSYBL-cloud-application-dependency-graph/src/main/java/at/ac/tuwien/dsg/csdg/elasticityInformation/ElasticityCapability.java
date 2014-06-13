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

package at.ac.tuwien.dsg.csdg.elasticityInformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ElasticityCapability{
	  public ElasticityCapability(){
	      	
      }
      private String value;
      private String name;
      private String type;
      private String apiMethod;
      private String script;
      private List<String> parameters;
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getApiMethod() {
			return apiMethod;
		}
		public void setApiMethod(String apiMethod) {
			this.apiMethod = apiMethod;
		}
		public List<String> getParameter() {
			return parameters;
		}
		public void setParameter(List<String> parameters) {
			this.parameters = parameters;
		}
		public void addParameter(String parameter) {
			if (parameters==null){
				parameters=new ArrayList<String>();
			}
			parameters.add(parameter);
		}
		public String getEndpoint() {
			return script;
		}
		public void setEndpoint(String script) {
			this.script = script;
		}
		public String getCallType() {
			return type;
		}
		public void setCallType(String type) {
			this.type = type;
		}

}
