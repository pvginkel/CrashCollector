package crashcollector.web;

import org.apache.commons.lang.StringUtils;

import crashcollector.data.Entity;

public abstract class Column<T extends Entity> {
	private String label;
	private String name;
	private boolean noWrap;
	private boolean sortable;
	
	public Column(String name, String label) {
		this(name, label, true);
	}
	
	public Column(String name, String label, boolean noWrap) {
		this.name = name;
		this.label = label;
		this.noWrap = noWrap;
		
		this.sortable = !StringUtils.isBlank(name);
	}
	
	public abstract String getValue(T entity);
	
	public String getName() {
		return name;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean getNoWrap() {
		return noWrap;
	}
	
	public boolean getSortable() {
		return sortable;
	}
	
	public String truncate(String value, int maxLength) {
		if (!StringUtils.isBlank(value)) {
			if (value.length() > maxLength) {
				return value.substring(0, maxLength - 3) + "...";
			}
		}
		
		return value;
	}
}
