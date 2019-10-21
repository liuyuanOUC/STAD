package com.ouc.misconfiguration.conftrace.trace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ouc.misconfiguration.conftrace.utils.*;
import com.ouc.misconfiguration.conftrace.analysis.AccessVariableSitesLocator;
import com.ouc.misconfiguration.conftrace.analysis.GetSubClasses;
import com.ouc.misconfiguration.conftrace.constant.*;

public class ConfTrace {
	private static final Logger logger = LogManager.getLogger("ConfTrace");
	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
	IOException, XPathExpressionException, TransformerException {
		logger.info("Trace Start");
		long startT = System.currentTimeMillis();
		Analyzer.loadXMLDoc(Constants.dpath);
		
		/*//unit node
		String unitPath = "//unit[@filename='chord-src-2.1\\src\\chord\\program\\RTA.java']";
		Node unitNode = Analyzer.getNode(Analyzer.document, unitPath);
		//statement node
		String stmtPath = ".//*[position[@line='514']]";
		Node stmtNode = Analyzer.getNode(unitNode, stmtPath);
		//name nodes
		String namePath = ".//name[not(name)]";
		NodeList allNameNodeList = Analyzer.getNodeList(stmtNode, namePath);
		//function node
		String functionPath = "./ancestor::function";
		Node funNode = Analyzer.getNode(stmtNode, functionPath);
		//declared name nodes
		String declNamePath = ".//decl/name";
		NodeList declNameNodeList = Analyzer.getNodeList(funNode, declNamePath);
		//declared field nodes
		String declFieldNamePath = "./class/block/decl_stmt/decl/name";
		NodeList declFieldNameNodeList = Analyzer.getNodeList(unitNode, declFieldNamePath);
		
		List<String> tmpallNameList = Analyzer.toListStr(allNameNodeList);
		List<String> tmpdeclNameList = Analyzer.toListStr(declNameNodeList);
		List<String> tmpFieldNameList = Analyzer.toListStr(declFieldNameNodeList);		
		List<String> allLocalNameList = new ArrayList<String>(); //local variables + parameters
		List<String> allFieldNameList = new ArrayList<String>(); //class variables + instance variables
		List<String> declNameList = new ArrayList<String>();
		List<String> fieldNameList = new ArrayList<String>();
		
		//to use "retainAll" method
		allLocalNameList.addAll(tmpallNameList);
		allFieldNameList.addAll(tmpallNameList);
		declNameList.addAll(tmpdeclNameList);
		fieldNameList.addAll(tmpFieldNameList);
		
		//intersection
		allLocalNameList.retainAll(declNameList);
		allFieldNameList.retainAll(fieldNameList);
		System.out.println(allLocalNameList);
		System.out.println(allFieldNameList);
		
		//String initPath = ".//decl_stmt[position[@line<'518'] and decl/name[text()='c']]/decl/init";
		//NodeList initNodeList = Analyzer.getNodeList(funNode, initPath);
		//System.out.println(Analyzer.nodeListToString(initNodeList));
		logger.info("Time cost: " + (System.currentTimeMillis() - startT)/(double)1000 + " s");*/
		
		/*main workflow*/
		/*step1: locating and identifying ORP*/
		/*1.1 extracting all classes*/
		/*Analyzer.getAllClasses(Analyzer.document);
		Analyzer.getAllJavaName();*/
		//Analyzer.printSet(Analyzer.allJavaName); //print
		/*1.2 locating all sub classes of the conf class*/
		/*String mainConfClass = "Config";
		String mainFullConfClass = "chord.project.Config";
		GetSubClasses getAllSubClasses = new GetSubClasses();
		getAllSubClasses.getSubClassList(mainConfClass);
		Map<String,Node> confClasses = getAllSubClasses.getMapClassNodes();
		confClasses.put(mainConfClass, Analyzer.getClassNode(mainFullConfClass));*/
		/*1.3 identifying all configuration option names*/
		/*AccessVariableSitesLocator accessVariableSitesLocator = new AccessVariableSitesLocator();
		accessVariableSitesLocator.getClassNameandProperties(confClasses);*/
		//Analyzer.printMapSet(AccessVariableSitesLocator.variableandProperties); //print
		/*locating all read points of configuration options
		accessVariableSitesLocator.getAllSites(confClasses);*/
		//Analyzer.printMapMap(AccessVariableSitesLocator.variableandLocation); //print
		logger.info("Time cost: " + (System.currentTimeMillis() - startT)/(double)1000 + " s");
	}
}
