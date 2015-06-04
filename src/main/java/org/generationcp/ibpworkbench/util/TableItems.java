
package org.generationcp.ibpworkbench.util;

public class TableItems {

	private String columnId;
	private Object rowId;
	private Integer type;
	private String Label;
	private Object value;

	public String getColumnId() {
		return this.columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public Object getRowId() {
		return this.rowId;
	}

	public void setRowId(Object rowId) {
		this.rowId = rowId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getLabel() {
		return this.Label;
	}

	public void setLabel(String label) {
		this.Label = label;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tableitems [columnId=");
		builder.append(this.columnId);
		builder.append(", rowId=");
		builder.append(this.rowId);
		builder.append(", type=");
		builder.append(this.type);
		builder.append(", Label=");
		builder.append(this.Label);
		builder.append(", value=");
		builder.append(this.value);
		builder.append("]");
		return builder.toString();
	}
}
