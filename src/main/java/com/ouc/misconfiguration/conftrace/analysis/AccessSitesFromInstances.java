package com.ouc.misconfiguration.conftrace.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ouc.misconfiguration.conftrace.element.ConfClassVariable;
import com.ouc.misconfiguration.conftrace.utils.Analyzer;

public class AccessSitesFromInstances {
	public List<ConfClassVariable> getVariablesOfConfClass(String fullClassName) throws XPathExpressionException {
		List<ConfClassVariable> list = new ArrayList<ConfClassVariable>();
		NodeList declNodes = Analyzer.getVariablesOfConfClass(fullClassName);
		for (int i = 0; i < declNodes.getLength(); i++) {
			String variableName = getConfClassName(declNodes.item(i));
			Node scope = getScopeOfConfClass(declNodes.item(i));
			list.add(new ConfClassVariable(variableName, scope));
		}
		return list;
	}

	public String getConfClassName(Node node) {
		List<Node> nameNode = Analyzer.getChildNodeByTagName(node, "name");
		if (nameNode.size() != 1)
			System.err.println("Incorrect declaration!");
		Node name = nameNode.iterator().next();
		return name.getTextContent();
	}

	public Node getScopeOfConfClass(Node node) {
		Node parentNode = node.getParentNode();
		Node grandParentNode = parentNode.getParentNode();
		Node scope = null;
		if (parentNode.getNodeName().equals("parameter") && grandParentNode.getNodeName().equals("parameter_list")) {

			return scope = grandParentNode.getParentNode();
		}
		scope = Analyzer.getScope(node);
		return scope;
	}

	public void getConfGetNodes(String fullClassName) throws XPathExpressionException {
		List<ConfClassVariable> list = getVariablesOfConfClass(fullClassName);
		Iterator<ConfClassVariable> iter = list.iterator();
		while (iter.hasNext()) {
			ConfClassVariable tmp = (ConfClassVariable) iter.next();
			Node scope = tmp.getScope();
			searchConfCallSites(tmp.getVariableName(), scope, fullClassName);

			if (scope.getParentNode().getNodeName().equals("class")
					|| scope.getParentNode().getNodeName().equals("interface")) {

				String className = Analyzer.getClassName(scope.getParentNode());
				System.out.println("has subClasses: " + className);
				GetSubClasses gSubClasses = new GetSubClasses();
				gSubClasses.getSubClassList(className);
				searchConfClassSitesOfSubClasses(tmp.getVariableName(), gSubClasses.getSubClassNodes(), fullClassName);
			}

		}
	}

	public void searchConfCallSites(String variableName, Node scope, String fullClassName) throws XPathExpressionException {
		Map<String, String> properties = AccessVariableSitesLocator.variableandProperties.get(fullClassName);
		Map<String, Set<String>> varandORPs = new HashMap<String, Set<String>>();
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			Set<String> locations = new HashSet<String>();
			String lineXpath = "//expr[./name/name[last()-1][text()='" + variableName + "']][./name/operator[text()='.']][./name/name[last()][text()='"
					+ entry.getKey() + "']]//name/name[last()]/@line";
			NodeList callSitesList = Analyzer.getNodeList(Analyzer.document, lineXpath);
			String fileNameXpath = "./ancestor::unit/@filename";
			String fileName = "";
			for (int i = 0; i < callSitesList.getLength(); i++) {
				fileName = Analyzer.getNode(callSitesList.item(i), fileNameXpath).getTextContent();
				locations.add(fileName + "@" + callSitesList.item(i).getTextContent());
			}
			varandORPs.put(entry.getKey(), locations);
		}
		AccessVariableSitesLocator.addMapKeyValue(fullClassName, varandORPs);
	}

	public void searchConfClassSitesOfSubClasses(String variableName, Set<Node> childrens, String fullClassName)
			throws XPathExpressionException {
		Iterator<Node> iter = childrens.iterator();
		while (iter.hasNext()) {
			Node node = iter.next();
			searchConfCallSites(variableName, node, fullClassName);
		}
	}
}
