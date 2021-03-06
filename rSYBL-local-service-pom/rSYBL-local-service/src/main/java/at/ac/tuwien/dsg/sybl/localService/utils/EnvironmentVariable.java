/** 
   Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184

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
package at.ac.tuwien.dsg.sybl.localService.utils;

import java.io.Serializable;

public class EnvironmentVariable implements Serializable{
	private String name;
	private Comparable var;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getVar() {
		return var;
	}

	public void setVar(Comparable var) {
		this.var = var;
	}

	public int hashCode() {
		return name.hashCode();

	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof EnvironmentVariable) {
			EnvironmentVariable environmentVariable = (EnvironmentVariable) o;
			if (name.equalsIgnoreCase(environmentVariable.getName()))
				return true;
			else
				return false;
		} else
			return o.equals(var);
	}


}
