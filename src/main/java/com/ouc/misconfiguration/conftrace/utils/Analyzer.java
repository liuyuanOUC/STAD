package com.ouc.misconfiguration.conftrace.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Analyzer {
	public static Document document = null;
	public static XPath xpath = null;
	public static HashMap<String, Node> allClasses = null;
	public static Set<String> allJavaName = new HashSet<String>();
	private static final Logger logger = LogManager.getLogger("Analyzer");

	public static XPath getXPath() {
		if (xpath == null) {
			XPathFactory factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
		}
		return xpath;
	}

	public static void loadXMLDoc(String xpath) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		document = docBuilder.parse(new File(xpath));
	}

	public static NodeList getNodeList(Node node, String path) throws XPathExpressionException {
		XPath xpath = getXPath();
		XPathExpression expression = xpath.compile(path);

		return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
	}

	public static Node getNode(Node node, String path) throws XPathExpressionException {
		XPath xpath = getXPath();
		XPathExpression expression = xpath.compile(path);

		return (Node) expression.evaluate(node, XPathConstants.NODE);
	}

	public static String nodeListToString(NodeList nodes) throws TransformerException {
		DOMSource source = new DOMSource();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		for (int i = 0; i < nodes.getLength(); ++i) {
			source.setNode(nodes.item(i));
			transformer.transform(source, result);
		}

		return writer.toString();
	}

	public static String nodeToString(Node node) throws TransformerException {
		DOMSource source = new DOMSource();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

		source.setNode(node);
		transformer.transform(source, result);

		return writer.toString();
	}

	public static NodeList getAllNameNodes(Node stmt) throws XPathExpressionException, TransformerException,
			ParserConfigurationException, SAXException, IOException {
		String xpath = ".//name[not(name)]";
		return Analyzer.getNodeList(stmt, xpath);

	}

	public static List<Node> toList(final NodeList list) {
		return new AbstractList<Node>() {
			public int size() {
				return list.getLength();
			}

			public Node get(int index) {
				Node item = list.item(index);
				if (item == null)
					throw new IndexOutOfBoundsException();
				return item;
			}
		};
	}

	public static List<String> toListStr(final NodeList list) {
		return new AbstractList<String>() {

			public int size() {
				return list.getLength();
			}

			public String get(int index) {
				Node item = list.item(index);
				if (item == null)
					throw new IndexOutOfBoundsException();
				return item.getTextContent();
			}
		};
	}

	public static List<String> toListStr2(final NodeList list) {
		List<String> liststr = new ArrayList<String>();
		int num = list.getLength();
		for (int i = 0; i < num; i++) {
			if (list.item(i) == null) {
				throw new IndexOutOfBoundsException();
			} else {
				liststr.add(list.item(i).getTextContent());
			}
		}
		return liststr;
	}

	public static Node[] convertToArray(NodeList list) {
		int length = list.getLength();
		Node[] copy = new Node[length];

		for (int n = 0; n < length; ++n)
			copy[n] = list.item(n);

		return copy;
	}

	/*get all sub classes of one class*/
	public static NodeList getSubClasses(String superClass) throws XPathExpressionException {
		String[] tmp = superClass.split("\\.");
		String packageName = tmp[0];
		for (int i = 1; i < tmp.length - 1; i++) {
			packageName = packageName + "." + tmp[i];
		}
		String className = tmp[tmp.length - 1];

		String xpath = "//class[./super/extends/name[text()='" + className + "']]";
		NodeList subClassNodes = Analyzer.getNodeList(document, xpath);
		return subClassNodes;
	}

	public static List<Node> getChildNodeByTagName(Node node, String tagName) {
		List<Node> set = new ArrayList<Node>();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(tagName))
				set.add(children.item(i));
		}
		return set;
	}

	public static String getClassName(Node classNode) {
		List<Node> nameNodes = getChildNodeByTagName(classNode, "name");
		Node nameNode = nameNodes.iterator().next();
		return nameNode.getFirstChild().getTextContent();
	}

	public static Node getScope(Node declStmt) {
		Node parent = null;
		Node temp = declStmt.getParentNode();
		while (!temp.getNodeName().equals("class") && !temp.getNodeName().equals("interface")) {
			String name = temp.getNodeName();
			if (name.equals("block") || name.equals("for") || name.equals("while")) {
				parent = temp;
				break;
			}
			temp = temp.getParentNode();
		}
		return parent;
	}

	public static NodeList getVariablesOfConfClass(String fullClassName) throws XPathExpressionException {
		String[] tmp = fullClassName.split("\\.");
		String packageName = tmp[0];
		for (int i = 1; i < tmp.length - 1; i++) {
			packageName = packageName + "." + tmp[i];
		}
		String className = tmp[tmp.length - 1];

		String xpath = "//decl[./type/name[text()='" + className + "']|./type/name/name[last()][text()='" + className
				+ "']]";
		NodeList variables = Analyzer.getNodeList(document, xpath);

		return variables;

	}

	public static HashMap<String, Node> getAllClasses(Document doc) throws XPathExpressionException {
		allClasses = new HashMap<String, Node>();
		String classpath = "//class/name|//interface/name";
		NodeList classpathNode = getNodeList(doc, classpath);
		for (int j = 0; j < classpathNode.getLength(); j++) {
			Node file = getFileNode(classpathNode.item(j));
			String fileName = file.getAttributes().getNamedItem("filename").getNodeValue();
			String packageName = extractPackageName(fileName);
			//Here only class name is extracted without class parameters, for instance, generic type class A<T>
			String fullClassName = packageName + classpathNode.item(j).getFirstChild().getTextContent();
			allClasses.put(fullClassName, classpathNode.item(j).getParentNode());

		}

		return allClasses;
	}

	public static HashMap<String, Node> getAllClasses() {
		if (allClasses != null)
			return allClasses;
		else
			return null;
	}

	public static Node getFileNode(Node stm) {
		Node parent = null;
		Node temp = stm.getParentNode();
		while (temp != null) {
			if (temp.getNodeName().equals("unit")) {
				parent = temp;
				break;
			}
			temp = temp.getParentNode();
		}
		return parent;
	}

	public static String extractPackageName(String fileName) {
		String packageName = "";
		String[] tmp = fileName.split("\\\\");
		for (int i = 2; i < tmp.length - 1; i++)
			packageName = packageName + tmp[i] + ".";
		return packageName;
	}

	public static Node getClassNode(String fullClassName) {
		//very important to format the class names before retrieving
		String className = Utils.removeSpaceLineBreaks(fullClassName);
		HashMap<String, Node> allClasses = getAllClasses();
		return allClasses.get(className);
	}

	/*get all class, interface and enum*/
	public static void getAllJavaName() throws XPathExpressionException {
		String classpath = "/unit/unit[@language='Java']/@filename";
		NodeList javaUnitNode = getNodeList(document, classpath);
		for (int i = 0; i < javaUnitNode.getLength(); i++) {
			allJavaName.add(javaUnitNode.item(i).getTextContent());
		}
	}

	/*print method*/
	public static void printMapMap(Map<String, Map<String, Set<String>>> map) {
		Set<String> keySet = map.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String key = it.next();
			Map<String, Set<String>> childMap = map.get(key);
			Set<Entry<String, Set<String>>> entrySet = childMap.entrySet();
			for (Iterator<Entry<String, Set<String>>> it2 = entrySet.iterator(); it2.hasNext();) {
				Map.Entry<String, Set<String>> me = it2.next();
				logger.info(key + "--" + me.getKey() + "--" + me.getValue().toString());
			}
		}
	}

	public static void printMapSet(Map<String, Map<String, String>> map) {
		Set<String> keySet = map.keySet();
		for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
			String key = it.next();
			Map<String, String> childMap = map.get(key);
			Set<Entry<String, String>> entrySet = childMap.entrySet();
			for (Iterator<Entry<String, String>> it2 = entrySet.iterator(); it2.hasNext();) {
				Map.Entry<String, String> me = it2.next();
				logger.info(key + "--" + me.getKey() + "--" + me.getValue().toString());
			}
		}
	}

	public static void printMap(Map<String, Set<String>> map) {
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			logger.info(entry.getKey() + "--" + entry.getValue());
		}
	}

	public static void printSet(Set<String> set) {
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String str = it.next();
			logger.info(str);
		}
	}
}
