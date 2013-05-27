package org.generationcp.ibpworkbench.util;

public class TableItems {

	private String columnId;
	private Object rowId;
	private Integer type;
	private String Label;
	private Object value;
	 
	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public Object getRowId() {
		return rowId;
	}
	public void setRowId(Object rowId) {
		this.rowId = rowId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
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
