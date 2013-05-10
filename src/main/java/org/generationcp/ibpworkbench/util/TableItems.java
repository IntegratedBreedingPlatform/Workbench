package org.generationcp.ibpworkbench.util;

public class TableItems {

	private String columnId;
	private String rowId;
	private String type;
	private String Label;
	private Object value;
	
	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public String getRowId() {
		return rowId;
	}
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return Label;
	}
	public void setLabel(String label) {
		Label = label;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tableitems [columnId=");
        builder.append(columnId);
        builder.append(", rowId=");
        builder.append(rowId);
        builder.append(", type=");
        builder.append(type);
        builder.append(", Label=");
        builder.append(Label);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }
}
