package com.ouc.misconfiguration.conftrace.element;

import org.w3c.dom.Node;

public class ConfClassVariable {
	private String variableName;
	private Node scope;
	
	
	public ConfClassVariable(String variableName, Node scope) {
		super();
		this.variableName = variableName;
		this.scope = scope;
	}
	
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public Node getScope() {
		return scope;
	}
	public void setScope(Node scope) {
		this.scope = scope;
	}
}
