package at.ac.tuwien.dsg.sybl.syblProcessingUnit.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.ac.tuwien.dsg.csdg.Node;
import at.ac.tuwien.dsg.csdg.Node.NodeType;
import at.ac.tuwien.dsg.csdg.Relationship.RelationshipType;

public class TryParsing {
	public static void tryParsingIP(String param){
		
		String result=param;
		String REGEX_IP = "(\\{[A-Za-z0-9]+\\}\\.IP)";

		Pattern p = Pattern.compile(REGEX_IP,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(param); // get a matcher object
		int nbFound = m.groupCount();
		System.out.println("found "+nbFound);

		int i=0;
		while (m.find()) {
			String value = m.group();		
			System.out.println("found: "+value);	

			String nodeId = value.substring(1, value.indexOf('.')-1);
			System.out.println(nodeId);
			String newRegex= "(\\{"+nodeId+"\\}\\.IP)";
			Pattern p1 = Pattern.compile(newRegex);
			Matcher m1 = p1.matcher(result); // get a matcher object
				String newip="243242"+i;
				
				result=m1.replaceAll(newip);
		i++;
				
		}
		System.out.println("AAAAAAAAAAAAAAAAA"+result);	

	}
	public static void main(String[] args){
		tryParsingIP("{DataController}.IP IP {LALA}.IP");
	}
}
