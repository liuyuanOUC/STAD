package com.ouc.misconfiguration.conftrace.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {
	public static String removeArgs(String str){
		String[] s=str.split("<");
		return s[0];
	}
	
	public static String getLastPart(String str){
		String[] s=str.split("\\.");
		return s[s.length-1];
	}
	public static String getFirstPart(String str){
		String[] s=str.split("\\.");
		return s[0];
	}
	public static List<Node> removeWhileSpaceNode(NodeList list){
		ArrayList<Node> arrayList=new ArrayList<Node>();
		for(int i=0;i<list.getLength();i++){
			System.out.println("::"+list.item(i).getTextContent());
			if(list.item(i).getNodeType()==Node.ELEMENT_NODE){
				arrayList.add(list.item(i));
			}
		}
		return arrayList;
	}
	public static String removeSpaceLineBreaks(String s){
		return s.replaceAll("\\r\\n|\\r|\\n|\\s", "");
	}
}
