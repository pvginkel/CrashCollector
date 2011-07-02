package crashcollector.web;

import java.io.IOException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;

import crashcollector.data.Entity;

public class DetailsTable<T extends Entity> {
	private List<Column<T>> columns;
	
	public DetailsTable() {
		columns = new ArrayList<Column<T>>();
	}
	
	public void addColumn(Column<T> column) {
		columns.add(column);
	}
	
	public void render(T entity, HttpServletRequest req, JspWriter out) throws IOException {
		out.print("<table class=\"details\">");
		
		int i = 0;
		
		for (Column<T> column : columns) {
			String cssClass = i++ % 2 == 0 ? "odd" : "even";
			
			out.print("<tr class=\"");
			out.print(cssClass);
			out.print("\"><th nowrap=\"nowrap\">");
			out.print(StringEscapeUtils.escapeHtml(column.getLabel()));
			out.print("</th><td>");
			
			String value = StringEscapeUtils.escapeHtml(column.getValue(entity));
			
			if (value != null) {
				value = value.replaceAll("\r?\n", "<br/>");
			}
			
			out.print(value);
			out.print("</td></tr>");
		}
		
		out.print("</table>");
	}
}
