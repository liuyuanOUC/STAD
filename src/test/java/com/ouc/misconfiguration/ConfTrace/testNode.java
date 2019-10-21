package com.ouc.misconfiguration.ConfTrace;

import java.io.IOException;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ouc.misconfiguration.conftrace.utils.Analyzer;

public class testNode {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException, TransformerException {

		/*String CLASSPATH = "//class[./name[text()='SetUtils']]"; ///or: unit[1]/unit[8]/class[1]
		String STATEMENT = "//class[./name[text()='SetUtils']]//function[./name[text()='iterableToSet']]"
				+ "//decl_stmt[./decl/name[text()='set']]";
		String dpath = "D:\\wkspace\\seed1\\chord - back.xml";		
		String xpath = STATEMENT;
		Analyzer.loadXMLDoc(dpath);
		NodeList nodes = Analyzer.getNodeList(Analyzer.document, xpath);

		int num = nodes.item(0).getChildNodes().item(0).getChildNodes().getLength();
		for (int i = 0; i < num; i++) {
			System.out.println(nodeToString(nodes.item(0).getChildNodes().item(0).getChildNodes().item(i)));
			System.out.println(nodes.item(0).getChildNodes().item(0).getChildNodes().item(i).getNodeType());
			System.out.println(nodes.item(0).getChildNodes().item(0).getChildNodes().item(i).getNodeName());
			System.out.println(nodes.item(0).getChildNodes().item(0).getChildNodes().item(i).getNodeValue());
			System.out.println("=====");
		}*/

		String dpath = "D:\\wkspace\\seed1\\testcase-01.xml"; 
		String xpath = "//expr_stmt[position[@line='1']]";
		Analyzer.loadXMLDoc(dpath);
		//Node testStmt = Analyzer.getNode(Analyzer.document, xpath);
		//System.out.println(nodeToString(testStmt));
		NodeList allNameNodes = Analyzer.getAllNameNodes(Analyzer.getNode(Analyzer.document, xpath));
		System.out.println(nodeListToString(allNameNodes));
//		for (int i = 0; i < 5; i ++) {
//			System.out.println(allNameNodes.item(i).getChildNodes().getLength());
//		}
		
//		String test="callsitee_optionName.getCallsite(x.y()).getA(r).getB();";
//		Pattern mypattern = Pattern.compile("[a-zA-Z_$][a-zA-Z_$0-9]*");
//		Matcher mymatcher = mypattern.matcher(test);    
//		while (mymatcher.find()) {
//		    String find = mymatcher.group(0) ;
//		    System.out.println("variable:" + find);
//		}

	}
	
	
	
	//XML nodes pretty print
	private static String nodeListToString(NodeList nodes) throws TransformerException {
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
	
	private static String nodeToString(Node node) throws TransformerException {
	    DOMSource source = new DOMSource();
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    Transformer transformer = TransformerFactory.newInstance().newTransformer();
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    
	    source.setNode(node);
	    transformer.transform(source, result);

	    return writer.toString();
	}
	
	//NodeList转List<Node>
	public static List<Node> list(final NodeList list) {
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
}
