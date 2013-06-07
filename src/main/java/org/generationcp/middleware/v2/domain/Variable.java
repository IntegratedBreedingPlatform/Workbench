package org.generationcp.middleware.v2.domain;

import org.generationcp.middleware.v2.util.Debug;

public class Variable  implements Comparable<Variable> {

	private VariableType variableType;
	
	private String value;

	public Variable() { }
	
	public Variable(VariableType variableType, String value) {
		this.variableType = variableType;
		this.value = value;
		if (variableType == null) throw new RuntimeException();
	}
	
	public Variable(VariableType variableType, Double value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Double.toString(value);
		}
		if (variableType == null) throw new RuntimeException();
	}

	public Variable(VariableType variableType, Integer value) {
		this.variableType = variableType;
		if (value != null) {
			this.value = Integer.toString(value);
		}
		if (variableType == null) throw new RuntimeException();
	}

	public VariableType getVariableType() {
		return variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
		if (variableType == null) throw new RuntimeException();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDisplayValue() {
		if (variableType.getStandardVariable().hasEnumerations()) {
			return variableType.getStandardVariable().findEnumerationById(Integer.parseInt(value)).getName();
		}
		return value;
	}

	public void print(int indent) {
		Debug.println(indent, "Variable: " );
		
		if (variableType == null) {
			Debug.println(indent + 3, "VariableType: null");
		}
		else {
		    Debug.println(indent + 3, "VariableType: " + variableType.getId() + " [" + variableType.getLocalName() + "]");
		}
		Debug.println(indent + 3, "Value: " + value);
	}
	
	public int hashCode() {
		return variableType.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Variable)) return false;
		Variable other = (Variable) obj;
		return other.getVariableType().equals(getVariableType()) &&
			   equals(other.getValue(), getValue());
	}
	
	private boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null) return false;
		if (s2 == null) return false;
		return s1.equals(s2);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Variable [variableType=");
		builder.append(variableType);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	// Sort in ascending order by rank
	public int compareTo(Variable compareValue) { 
		//System.out.println("This: " + this);
		//System.out.println("Compare Value: " + compareValue);
        int compareRank = ((Variable) compareValue).getVariableType().getRank(); 
        return Integer.valueOf(getVariableType().getRank()).compareTo(compareRank);
 	}
	
}
