package com.ouc.misconfiguration.conftrace.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ouc.misconfiguration.conftrace.utils.Analyzer;

public class GetSubClasses {
	private LinkedList<String> workList=new LinkedList<String>();
	private HashSet<String> classNames=new HashSet<String>();
	private HashSet classNodes=new HashSet<Node> ();
	private Map<String,Node> childClasses=new HashMap();
	
	public HashSet<String> getSubClassList(String className) throws XPathExpressionException{
		Set<String> confClassNames=getDirectSubClassNames(className);
		workList.addAll(confClassNames);
		
		while(!workList.isEmpty()){
			String tmp=workList.removeFirst();
			classNames.add(tmp);
			workList.addAll(getDirectSubClassNames(tmp));
		}
		
		return classNames;	
		
	}
	public Set<String> getDirectSubClassNames(String className) throws XPathExpressionException{
		Set<String> confClassNames=new HashSet<String>();
		NodeList list=Analyzer.getSubClasses(className);
		for(int i=0;i<list.getLength();i++){
			List<Node> confClassNodes=Analyzer.getChildNodeByTagName(list.item(i), "name");	
			String name=confClassNodes.iterator().next().getFirstChild().getTextContent();
			confClassNames.add(name);
			classNodes.add(list.item(i));
			childClasses.put(name, list.item(i));
		}
		return confClassNames;
	}
	public Set<Node> getSubClassNodes(){
		return classNodes;
	}
	public Map<String,Node> getMapClassNodes(){
		return childClasses;
	}
}
