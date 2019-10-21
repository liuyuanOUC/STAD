package com.ouc.misconfiguration.conftrace.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ouc.misconfiguration.conftrace.utils.Analyzer;

public class AccessVariableSitesLocator {

	public static Map<String, Map<String, String>> variableandProperties = new HashMap<String, Map<String, String>>(); //<class name, <variable, property>>
	public static Map<String, Map<String, Set<String>>> variableandLocation = new HashMap<String, Map<String, Set<String>>>();//<class name, <variable, ORPs>>, ORP:filename@line

	/*get variableandProperties*/
	public void getClassNameandProperties(Map<String, Node> classes) throws TransformerException {
		for (Entry<String, Node> entry : classes.entrySet()) {
			try {
				variableandProperties.put(entry.getKey(), getPropertyNames(entry.getValue()));
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
	}

	/*get variableandProperties of one class*/
	public Map<String, String> getPropertyNames(Node classNode) throws XPathExpressionException, TransformerException {
		Map<String, String> propertyNames = new HashMap<String, String>();

		String xpath = "./block/decl_stmt[.//call//literal[contains(text(), '.')]]";
		NodeList callSiteNodes = Analyzer.getNodeList(classNode, xpath);
		if (callSiteNodes != null) {
			int num = callSiteNodes.getLength();
			String varXpath = "./decl/name";
			String optXpath = ".//literal[contains(text(), '.')]";
			for (int i = 0; i < num; i++) {
				propertyNames.put(Analyzer.getNode(callSiteNodes.item(i), varXpath).getTextContent(),
						Analyzer.getNode(callSiteNodes.item(i), optXpath).getTextContent());
			}
		}
		return propertyNames;
	}

	/*get all sites*/
	public void getAllSites(Map<String, Node> classes) throws TransformerException {
		for (Entry<String, Node> entry : classes.entrySet()) {
			try {
				getSitesInClass(entry.getKey(), entry.getValue());
				getSitesReferredbyMethod(getMethodByReuturnType(entry.getKey()), entry.getKey());
				getSitesReferredbyInstanceClass(entry.getKey());
				getSitesReferredbyClass(entry.getKey());
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*first case: get access variable sites in class*/
	public void getSitesInClass(String className, Node classNode) throws XPathExpressionException {
		Map<String, String> properties = variableandProperties.get(className);
		String fileNameXpath = "./ancestor::unit/@filename";
		String fileName = Analyzer.getNode(classNode, fileNameXpath).getTextContent();
		Map<String, Set<String>> varandORPs = new HashMap<String, Set<String>>();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			Set<String> locations = new HashSet<String>();
			String lineXpath = ".//expr[./name[text()='" + entry.getKey() + "']]//name[text()='" + entry.getKey()
					+ "']/@line";
			NodeList callSiteNodes = Analyzer.getNodeList(classNode, lineXpath);
			for (int i = 0; i < callSiteNodes.getLength(); i++) {
				locations.add(fileName + "@" + callSiteNodes.item(i).getTextContent());
			}
			varandORPs.put(entry.getKey(), locations);
		}
		addMapKeyValue(className, varandORPs);
	}

	/*second case: get access variable sites referred by method*/
	public Set<String> getMethodByReuturnType(String returnType) throws XPathExpressionException {
		String xpath = ".//function_decl[./type//name[text()='" + returnType + "']]|.//function[./type/name[text()='"
				+ returnType + "']]";
		Set<String> fNames = new HashSet<String>();
		NodeList functions = Analyzer.getNodeList(Analyzer.document, xpath);
		for (int i = 0; i < functions.getLength(); i++) {
			List<Node> functionName = Analyzer.getChildNodeByTagName(functions.item(i), "name");
			Iterator<Node> iter = functionName.iterator();
			String fName = iter.next().getTextContent();
			fNames.add(fName);
		}
		return fNames;
	}

	public void getSitesReferredbyMethod(String methodName, String className) throws XPathExpressionException {
		Map<String, String> properties = variableandProperties.get(className);
		Map<String, Set<String>> varandORPs = new HashMap<String, Set<String>>();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			Set<String> locations = new HashSet<String>();
			String lineXpath = ".//expr[./call[.//name[text()='" + methodName + "'] and ./name[text()='"
					+ entry.getKey() + "']]/name[last()]/@line";
			NodeList callSitesList = Analyzer.getNodeList(Analyzer.document, lineXpath);
			String fileNameXpath = "./ancestor::unit/@filename";
			String fileName = "";
			for (int i = 0; i < callSitesList.getLength(); i++) {
				fileName = Analyzer.getNode(callSitesList.item(i), fileNameXpath).getTextContent();
				locations.add(fileName + "@" + callSitesList.item(i).getTextContent());
			}
			varandORPs.put(entry.getKey(), locations);
		}
		addMapKeyValue(className, varandORPs);
	}

	public void getSitesReferredbyMethod(Set<String> methodNames, String className) throws XPathExpressionException {
		Iterator<String> iter = methodNames.iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			getSitesReferredbyMethod(name, className);
		}
	}

	/*third case: get access variable sites referred by instances of class*/
	public void getSitesReferredbyInstanceClass(String className) throws XPathExpressionException {
		AccessSitesFromInstances accessSitesFromInstances = new AccessSitesFromInstances();
		accessSitesFromInstances.getConfGetNodes(className);
	}
	
	/*fourth case: get access variable sites referred by Class*/
	public void getSitesReferredbyClass(String className) throws XPathExpressionException {
		Map<String, String> properties = variableandProperties.get(className);
		Map<String, Set<String>> varandORPs = new HashMap<String, Set<String>>();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			Set<String> locations = new HashSet<String>();
			String lineXpath = "//expr[./name/name[last()-1][text()='" + className + "'] and ./name/name[last()][text()='"
					+ entry.getKey() + "']]/name/name[last()]/@line";
			NodeList callSitesList = Analyzer.getNodeList(Analyzer.document, lineXpath);
			String fileNameXpath = "./ancestor::unit/@filename";
			String fileName = "";
			for (int i = 0; i < callSitesList.getLength(); i++) {
				fileName = Analyzer.getNode(callSitesList.item(i), fileNameXpath).getTextContent();
				locations.add(fileName + "@" + callSitesList.item(i).getTextContent());
			}
			varandORPs.put(entry.getKey(), locations);
		}
		addMapKeyValue(className, varandORPs);
	}

	/*add map<key, map<key, value>>*/
	public static void addMapKeyValue(String className, Map<String, Set<String>> varandORPs) {
		if (variableandLocation.containsKey(className)) {
			if (varandORPs.size() != 0) {
				Map<String, Set<String>> varorps = new HashMap<String, Set<String>>();
				varorps = variableandLocation.get(className);
				for (Entry<String, Set<String>> entry : varandORPs.entrySet()) {
					if (varorps.containsKey(entry.getKey())) {
						if (entry.getValue().size() != 0) {
							Set<String> orps = new HashSet<String>();
							orps = varorps.get(entry.getKey());
							orps.addAll(entry.getValue());
							varorps.replace(entry.getKey(), orps);
						}
					}else {
						varorps.put(entry.getKey(), entry.getValue());
					}
				}
				variableandLocation.replace(className, varorps);
			}			
		}else {
			variableandLocation.put(className, varandORPs);
		}
	}

	/*getter and setter*/
	public static Map<String, Map<String, String>> getVariableandProperties() {
		return variableandProperties;
	}

	public static void setVariableandProperties(Map<String, Map<String, String>> variableandProperties) {
		AccessVariableSitesLocator.variableandProperties = variableandProperties;
	}

	public static Map<String, Map<String, Set<String>>> getVariableandLocation() {
		return variableandLocation;
	}

	public static void setVariableandLocation(Map<String, Map<String, Set<String>>> variableandLocation) {
		AccessVariableSitesLocator.variableandLocation = variableandLocation;
	}

}
