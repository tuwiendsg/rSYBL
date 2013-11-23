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
package at.ac.tuwien.dsg.sybl.localService.processing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import at.ac.tuwien.dsg.sybl.localService.exceptions.ConstraintViolationException;
import at.ac.tuwien.dsg.sybl.localService.exceptions.MethodNotFoundException;
import at.ac.tuwien.dsg.sybl.localService.languageDescription.SYBLDescriptionParser;
import at.ac.tuwien.dsg.sybl.localService.syblLocalRuntime.SyblMonitorAndEnforceLocally;
import at.ac.tuwien.dsg.sybl.localService.utils.EnvironmentVariable;
import at.ac.tuwien.dsg.sybl.localService.utils.Rule;




public class Utils {
	ArrayList<MonitoringThread> monitoringThreads = new ArrayList<MonitoringThread>();
	
	HashMap<EnvironmentVariable, Comparable> monitoredVariables = new HashMap<EnvironmentVariable, Comparable>();
    public HashMap<String,Boolean> cons= new HashMap<String,Boolean>();
    SyblMonitorAndEnforceLocally syblAPI ;
	ArrayList<Rule> disabledRules = new ArrayList<Rule>();
	String monitoring= "";
	String constraints = "";
	String strategies ="";
	String priorities="";
	public Utils(String priorities, String monitoring, String constraints, String strategies,SyblMonitorAndEnforceLocally api){
		this.priorities=priorities;
		this.constraints=constraints;
		this.strategies = strategies;
		this.monitoring=monitoring;
		this.syblAPI=api;
	}
	public void clearDisabledRules(){
	 disabledRules.clear();
		
	}
	public void processSyblSpecifications(){
		if (!monitoring.equals("")) {
			System.out.println("=============================================");
			System.out.println("Monitoring " + monitoring);

			processMonitoring(monitoring);
			System.out.println();
			System.out.println();
		}

		if (!priorities.equals("")) {
			System.out.println("=============================================");
			System.out.println("Priorities " + priorities);
			ArrayList<Rule> rules = new ArrayList<Rule>();
			if (!monitoring.equals(""))
			for (String m : monitoring.split(";")) {
				String[] s = m.split(":");
				Rule r = new Rule();
				r.setName(eliminateSpaces(s[0]));
				r.setText(s[1]);
				rules.add(r);
			}
			if (!constraints.equals(""))
			for (String m : constraints.split(";")) {
				String[] s = m.split(":");
				Rule r = new Rule();
				r.setName(eliminateSpaces(s[0]));
				r.setText(s[1]);
				rules.add(r);
			}
			if (!strategies.equals(""))
			for (String m : strategies.split(";")) {
				String[] s = m.split(":");
				Rule r = new Rule();
				r.setName(eliminateSpaces(s[0]));
				r.setText(s[1]);
				rules.add(r);
			}

			processPriorities(priorities,rules);

			System.out.println();
			System.out.println();
		}

		
		try {
			if (!constraints.equalsIgnoreCase(""))
				System.out
						.println("=============================================");
			System.out.println("Constraints " + constraints);

			processConstraints(constraints);
			System.out.println();
			System.out.println();
		} catch (MethodNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!strategies.equals("")) {
			System.out.println("=============================================");
			System.out.println("Strategies " + strategies);
			processStrategies(strategies);
			System.out.println();
			System.out.println();
		}

	}
// ==========================processing code========================================//
public void processPriorities(String priorities,ArrayList<Rule> rules) {
	String[] s = priorities.split(";");
	for (String c : s) {
		String[] x = c.split(":");
		Rule r = new Rule();
		if (x.length > 1) {
			r.setName(x[0]);
			r.setText(x[1]);
		} else {
			r.setText(x[0]);
		}
		String smallerRule="";
		String greaterImpRule="";
		x = r.getText().split("<");
		if (x.length>1){
			 smallerRule =eliminateSpaces(x[0].split("[\\(]")[1].split("[\\)]")[0]);
				 greaterImpRule=eliminateSpaces(x[1].split("[\\(]")[1].split("[\\)]")[0]);
				System.out.println("Priority  "+smallerRule+" is smaller than of "+greaterImpRule);
		
		}else{
			x = r.getText().split(">");
			if (x.length>1){
				 smallerRule =eliminateSpaces(x[1].split("[\\(]")[1].split("[\\)]")[0]);
				 greaterImpRule=eliminateSpaces(x[0].split("[\\(]")[1].split("[\\)]")[0]);
					System.out.println("Priority "+smallerRule+" is smaller than of "+greaterImpRule);
				
			}
		}
		boolean disableLessImpRule = false;
		for (Rule rule : rules){
			if (rule.getName().equalsIgnoreCase(greaterImpRule)){
				String ruleText = rule.getText();
				if (ruleText.toLowerCase().contains(" when ")){
					String cond = ruleText.toLowerCase().split("when ")[1];
					try {
						if (evaluateCondition(cond)){
						System.out.println("Evaluating condition "+cond+" of the higher importance rule");
							disableLessImpRule=true;
						}
					} catch (MethodNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					if (ruleText.toLowerCase().contains("case")){
						String cond = ruleText.toLowerCase().split("case ")[1].split(":")[0];
						try {
							if (evaluateCondition(cond)){
							System.out.println("Evaluating condition "+cond+" of the higher importance rule");
								disableLessImpRule=true;
							}
						} catch (MethodNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else
					disableLessImpRule=true;
				}
			break;
			}
		}
		if (disableLessImpRule){
			for (Rule rule:rules){
				if (rule.getName().equalsIgnoreCase(smallerRule)){
					disabledRules.add(rule);
				}
			}
		}
	}
	for (Rule r:disabledRules){
		System.out.println("Disabled rule "+r.getName());
	}

}

public void processConstraints(String constraints)
		throws MethodNotFoundException {
	String[] s = constraints.split(";");
	for (String c : s) {
		String[] x = c.split(":");
		Rule r = new Rule();
		System.out.println("Constraint " + x[0] + " is " + x[1]);
		r.setName(eliminateSpaces(x[0]));
		r.setText(x[1]);
		if (!disabledRules.contains(r)){
		if (x[1].contains("WHEN "))
			try {
				processComplexConstraint(r);
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getMessage());
			}
		else if (x[1].contains("AND ") || x[1].contains("OR "))
			processCompositeConstraint(r);
		else
			try {
				processSimpleConstraint(r);
			} catch (ConstraintViolationException e) {
				// TODO Auto-generated catch block
				System.err.println(e.getMessage());
			}
	}else{
		this.cons.put(x[0], false);
		System.out.println(x[0]+" is not evaluated because other constraint of higher importance overrides it");
	}
		}

}

public void processMonitoring(String monitoring) {
	String[] s = monitoring.split(";");
	for (String c : s) {
		String[] x = c.split(":");
		System.out.println("Monitoring rule " + x[0] + " is " + x[1]);
		Rule r = new Rule();
		r.setName(x[0]);
		r.setText(x[1]);
		if (x[1].contains("WHEN "))
			try {
				processComplexMonitoringRule(r);
			} catch (MethodNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			processMonitoringRule(r);
	}

}

public void processStrategies(String strategies) {
	String[] s = strategies.split(";");
	for (String c : s) {
		String[] x = c.split(":");
		
		Rule r = new Rule();
		r.setName(x[0]);

		r.setText(c.substring(c.indexOf(":") + 1));
		System.out.println("Strategy " + x[0] + " is " + r.getText());
		if (r.getText().contains("WHERE "))
			processComplexStrategy(r);
		else
			processStrategy(r);
	}

}

public void processStrategy(Rule r) {
	if (r.getText().toLowerCase().contains("case")) {
		String s[] = r.getText().split(":");
		String condition = s[0].toLowerCase().split("case ")[1];
		try {
			if ((condition.contains("and") && evaluateCompositeCondition(condition))||(!condition.contains("and") &&evaluateCondition(condition)) ){
				if (s[1].contains("\\(")){
				String actionName = s[1].split("[(]")[0];
				String[] params = s[1].split("[(]")[1].split("[,\\)]");
				if (!params.equals("")){
				actionName = eliminateSpaces(actionName);

				try {
					Class partypes[] = new Class[params.length];

					Object[] parameters = new Object[params.length];
					for (int i = 0; i < params.length; i++) {

						try {
							parameters[i] = evaluateTerm(params[i]);
						} catch (MethodNotFoundException e) {
							parameters[i] = params[i];
						}
						partypes[i] = parameters[i].getClass();
					}

					Method actionMethod = SyblMonitorAndEnforceLocally.class.getMethod(
							actionName, partypes);

					actionMethod.invoke(syblAPI, parameters);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
				}else{
					String method = eliminateSpaces(s[1]);
					try {
						
						Method actionMethod = SyblMonitorAndEnforceLocally.class.getMethod(
								method);
								
						
						actionMethod.invoke(syblAPI);
					} catch (IllegalAccessException e) {
						System.out.println("Exception when calling"+method);
						// TODO Auto-generated catch block
					e.printStackTrace();
					
					} catch (SecurityException e) {
						System.out.println("Exception when calling"+method);
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						System.out.println("Exception when calling"+method);
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						System.out.println("Exception when calling"+method);
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						System.out.println("Exception when calling"+method);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				System.out.println("Condition not true " );
			}
		} catch (MethodNotFoundException e) {
			e.printStackTrace();
		}
	}
}

public void processComplexStrategy(Rule r) {

}

/************************** Monitoring processing *************************/
public void processMonitoringRule(Rule r) {

	if (r.getText().toLowerCase().contains("timestamp ")) {
		 String[] s = r.getText().toLowerCase().split("timestamp ");
		 float timestamp = Float.parseFloat(s[1].split(" ")[0]);
		 
		MonitoringThread t = new MonitoringThread(this,monitoredVariables,s[0], (long) timestamp);
		if (!monitoringThreads.contains(t)){
		t.start();
		monitoringThreads.add(t);
		}
	} else {
		try {
			processSimpleMonitoringRule(r.getText());
		} catch (MethodNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

public void processSimpleMonitoringRule(String monitoring)
		throws MethodNotFoundException {
	String[] s = monitoring.split(" ");
	String monitoredConcept = "";
	String variableName = "";
	for (int i = 0; i < s.length; i++) {
		if (s[i].equals("=")) {
			monitoredConcept = s[i + 1];
			variableName = s[i - 1];
			break;
		}
	}

	SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();
	String methodName = descriptionParser.getMethod(monitoredConcept);
	if (!methodName.equals("")) {

		Method method=null;
		try {
			method = SyblMonitorAndEnforceLocally.class.getMethod(methodName);
			Class variableType = method.getReturnType();
			Comparable newVar = null;
			switch (variableType.getName()) {
			case "java.lang.Float":
				newVar = new Float(0);
				break;
			case "java.lang.String":
				newVar = new String("");
				break;
			case "java.lang.Integer":
				newVar = new Integer(0);
				break;
			}

			EnvironmentVariable environmentVariable = new EnvironmentVariable();
			environmentVariable.setName(variableName);
			environmentVariable.setVar(newVar);
			System.out.println("Executing method "+methodName);
			Comparable res = (Comparable) method.invoke(syblAPI);
			//System.out.println("The monitored variable, " + variableName
			//		+ ", has the value " + res.toString());
			
			monitoredVariables.put(environmentVariable, res);

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
		//		System.out.println(method.getName()+" "+syblAPI.getCurrentCPUUsage());
		
			//System.out.println(e.getTargetException().toString());
			e.printStackTrace();
		}

	} else {
		throw new MethodNotFoundException("Method for " + monitoredConcept
				+ " was not found.");
	}
}

private String eliminateSpaces(String spaceFull) {
	String spaceFree = "";
	for (int i = 0; i < spaceFull.length(); i++) {
		if (spaceFull.charAt(i) != ' ') {
			spaceFree += spaceFull.charAt(i);
		}
	}

	return spaceFree;
}

public void processComplexMonitoringRule(Rule r)
		throws MethodNotFoundException {
	String[] s = r.getText().toLowerCase().split("when ");
	String monitoring = s[0].split("monitoring ")[1];
	String condition = s[1];
	// Process condition, if it holds process and enforce constraint
	if (evaluateCondition(condition)) {
		processSimpleMonitoringRule(monitoring);
	}
}

/**************** Constraints Processing *****************************/
public void processComplexConstraint(Rule constraint)
		throws MethodNotFoundException, ConstraintViolationException {
	String[] s = constraint.getText().toLowerCase().split("when ");
	String constr = s[0].split("constraint ")[1];
	String condition = s[1];
	// Process condition, if it holds process and enforce constraint
	if (evaluateCondition(condition)) {
		if (evaluateCondition(constr))
			System.out.println("CONSTRAINT " + constraint.getName()
					+ " is fulfilled.");
		else
			throw new ConstraintViolationException("CONSTRAINT "
					+ constraint.getName() + " is violated.");
	} else {
		System.out.println("CONSTRAINT " + constraint.getName()
				+ " is not evaluated because the condition " + condition
				+ " is not met.");
	}
}
public void processCompositeConstraint(Rule constraint)
		{
	
}
public boolean evaluateCompositeCondition (String compCond)throws MethodNotFoundException{
	if (compCond.toLowerCase().contains("and")){
	String [] s= compCond.toLowerCase().split("and");
	if (evaluateCondition(s[0]) && evaluateCondition(s[1])) return true;
	else return false;
	}
	return false;
}
public Comparable evaluateTerm(String term) throws MethodNotFoundException {
	SYBLDescriptionParser descriptionParser = new SYBLDescriptionParser();
	if ((term.toLowerCase().charAt(0) >= 'a')
			&& (term.toLowerCase().charAt(0) <= 'z')) {

		String methodName = descriptionParser.getMethod(term);// de luat
																// parametrii
		if (!methodName.equals("")) {
			try {
				Method method = SyblMonitorAndEnforceLocally.class.getMethod(methodName);
				return (Comparable) method.invoke(syblAPI);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			EnvironmentVariable myVar = null;

			for (EnvironmentVariable variable : monitoredVariables.keySet()) {
				if (variable.getName().equalsIgnoreCase(term)) {
					myVar = variable;
				}
			}
			if (myVar == null)
				throw new MethodNotFoundException("Variable "
						+ term + " not found!");
			return monitoredVariables.get(myVar);
		}

	} else {
		if ((term.toLowerCase().charAt(0) >= '0')
				&& (term.toLowerCase().charAt(0) <= '9')) {
			return Float.parseFloat(term);
		}
	}
	return null;
}

@SuppressWarnings("unchecked")
public boolean evaluateCondition(String condition)
		throws MethodNotFoundException {
	String[] s = condition.split(" ");
	if (condition.toLowerCase().contains("violated") || condition.toLowerCase().contains("fulfilled")){
	//System.out.println(condition.split("[(]")[0]);
	if(eliminateSpaces(condition.split("\\(")[0]).equalsIgnoreCase("violated")) {
		//Get constraint and check if it is violated
		String name = condition.split("[()]")[1];
	
		//System.out.println("The constraint is "+name+" "+constraints.get(name));
		if (cons.get(name)==null) return false;
		if (cons.get(name))return false;
		else return true;
	
	} 
	if(eliminateSpaces(condition.split("[(]")[0]).equalsIgnoreCase("fulfilled")) {
		//Get constraint and check if it is violated
		
		String name = condition.split("[()]")[1];
		//System.out.println("The constraint is "+name+" "+constraints.get(name));
		return cons.get(name);
	} 
	
	}else{
		if (condition.toLowerCase().contains("enabled")){
			Rule r = new Rule();
			r.setName(eliminateSpaces(condition.split("[()]")[1]));
			if(disabledRules.contains(r) )return false;
			else return true;
		}
		if (condition.toLowerCase().contains("disabled")){
			Rule r = new Rule();
			r.setName(eliminateSpaces(condition.split("[()]")[1]));
			if(disabledRules.contains(r))return true;
			else return false;
		}
	}
	switch (s[1]) {
	case ">":
		if ((evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) <= 0))
			return false;
		else
			return true;
	case "<":
		if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) >= 0)
			return false;
		else
			return true;
	case ">=":
		if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) < 0)
			return false;
		else
			return true;
	case "<=":
		if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) > 0)
			return false;
		else
			return true;
	case "==":
		if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) != 0)
			return false;
		else
			return true;
	case "!=":
		if (evaluateTerm(s[0]).compareTo(evaluateTerm(s[2])) == 0)
			return false;
		else
			return true;

	default:
		break;
	}

	return false;
}

public void processSimpleConstraint(Rule constraint)
		throws MethodNotFoundException, ConstraintViolationException {
	String s[] = constraint.getText().split("CONSTRAINT ");
	if (s[1].toLowerCase().contains("and"))
	{
		if (evaluateCompositeCondition(s[1])){
			cons.put(eliminateSpaces(constraint.getName().toLowerCase()), true);
			System.out.println("CONSTRAINT " + constraint.getName()
					+ " is fulfilled.");
			
		}
		else{
			cons.put(eliminateSpaces(constraint.getName().toLowerCase()), false);

			System.err.println("CONSTRAINT "
					+ constraint.getName() + " is violated.");
		}
	}
	if (evaluateCondition(s[1])){
		cons.put(eliminateSpaces(constraint.getName().toLowerCase()), true);
		System.out.println("CONSTRAINT " + constraint.getName()
				+ " is fulfilled.");
		
	}
	else{
		cons.put(eliminateSpaces(constraint.getName().toLowerCase()), false);

		System.err.println("CONSTRAINT "
				+ constraint.getName() + " is violated.");
	}
}
}
