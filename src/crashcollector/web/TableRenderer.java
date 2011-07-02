package crashcollector.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import javax.jdo.*;
import javax.servlet.http.*;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import crashcollector.data.Entity;

public class TableRenderer<T extends Entity> {
	
	private Class<T> entityClass;
	private String where;
	private List<Object> parameters;
	private String order;
	private List<Column<T>> columns;
	private List<Action> actions;
	private String detailsUrl;
	private String orderColumn;
	private String orderDirection;
	
	public TableRenderer(Class<T> entityClass, String where, List<Object> parameters, String order) {
		this.entityClass = entityClass;
		this.where = where;
		this.parameters = parameters;
		this.order = order;
		
		columns = new ArrayList<Column<T>>();
		actions = new ArrayList<Action>();
	}
	
	public void addColumn(Column<T> column) {
		columns.add(column);
	}
	
	public void addAction(Action action) {
		actions.add(action);
	}
	
	public void setDetailsUrl(String detailsUrl) {
		this.detailsUrl = detailsUrl;
	}
	
	public String getDetailsUrl() {
		return detailsUrl;
	}
	
	public void render(PersistenceManager pm, HttpServletRequest req, JspWriter out) throws IOException {
		String tableOrderBy = req.getParameter("table_orderby");
		
		if (!StringUtils.isBlank(tableOrderBy)) {
			parseOrderBy(tableOrderBy);
		}
		
		if (orderColumn == null) {
			parseOrderBy(order);
		}
		
		out.print("<table class=\"table\">");
		
		renderHeaders(req, out);
		renderColumns(pm, req, out);
		
		out.print("</table>");
	}

	private void renderHeaders(HttpServletRequest req, JspWriter out) throws IOException {
		out.print("<tr>");
		
		for (Column<T> column : columns) {
			String cssClass;
			String newOrder;
			
			if (column.getSortable()) {
				if (column.getName().equalsIgnoreCase(orderColumn)) {
					cssClass = "sort-" + column.getName().toLowerCase();
					newOrder = orderDirection.equalsIgnoreCase("asc") ? "desc" : "asc";
				} else {
					cssClass = "sort";
					newOrder = "asc";
				}
			} else {
				cssClass = "";
				newOrder = null;
			}
			
			out.print("<th class=\"");
			out.print(cssClass);
			out.print("\">");
			
			if (column.getSortable()) {
				Map<String, String> parameters = new HashMap<String, String>();
				
				parameters.put("table_orderby", column.getName() + " " + newOrder);
				
				String link = buildLink(req, parameters);
				
				out.print("<a href=\"");
				out.print(StringEscapeUtils.escapeHtml(link));
				out.print("\">");
			}
			
			out.print(StringEscapeUtils.escapeHtml(column.getLabel()));
			
			if (column.getSortable()) {
				out.print("</a>");
			}
			
			out.print("</th>");
		}
		
		if (!actions.isEmpty()) {
			out.print("<th>&nbsp;</th>");
		}
		
		out.print("</tr>");
	}

	@SuppressWarnings("rawtypes")
	private String buildLink(HttpServletRequest req, Map<String, String> parameters) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		String requestUrl = WebUtil.getRequestUrl(req);
		int pos = requestUrl.indexOf("?");
		
		if (pos != -1) {
			requestUrl = requestUrl.substring(0, pos);
		}
		
		sb.append(requestUrl);
		
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		
		for (Enumeration e = req.getParameterNames(); e.hasMoreElements(); ) {
			String name = (String)e.nextElement();
			
			if (!parameters.containsKey(name)) {
				parameters.put(name, req.getParameter(name));
			}
		}
		
		if (parameters.size() > 0) {
			sb.append("?");
			
			boolean hadOne = false;
			
			for (String key : parameters.keySet()) {
				if (hadOne) {
					sb.append("&");
				} else {
					hadOne = true;
				}
				
				sb.append(URLEncoder.encode(key, "UTF-8"));
				sb.append("=");
				sb.append(URLEncoder.encode(parameters.get(key), "UTF-8"));
			}
		}
		
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private void renderColumns(PersistenceManager pm, HttpServletRequest req, JspWriter out) throws IOException {
		Query query = pm.newQuery(entityClass);
		
		if (!StringUtils.isBlank(where)) {
			query.setFilter(where);
		}
		
		if (orderColumn != null) {
			query.setOrdering(orderColumn + " " + orderDirection);
		}
		
		List<T> entities;
		
		if (parameters != null && parameters.size() > 0) {
			entities = (List<T>)query.executeWithArray(parameters);
		} else {
			entities = (List<T>)query.execute();
		}
		
		for (T entity : entities) {
			renderColumn(req, out, entity);
		}
	}

	private void renderColumn(HttpServletRequest req, JspWriter out, T entity) throws IOException {
		out.print("<tr>");
		
		String link =
			detailsUrl +
			"?id=" + entity.getId().toString() +
			"&referer=" + URLEncoder.encode(buildLink(req, null), "UTF-8");
		
		for (Column<T> column : columns) {
			out.print("<td");
			
			if (column.getNoWrap()) {
				out.print(" nowrap=\"nowrap\"");
			}
			
			out.print("><a href=\"");
			out.print(StringEscapeUtils.escapeHtml(link));
			out.print("\">");
			out.print(column.getValue(entity));
			out.print("</a></td>");
		}
		
		if (actions.size() > 0) {
			boolean hadOne = false;
			
			out.print("<td nowrap=\"nowrap\">");
			
			for (Action action : actions) {
				if (hadOne) {
					out.print(" | ");
				} else {
					hadOne = true;
				}
				
				out.print("<a href=\"");
				out.print(StringEscapeUtils.escapeHtml(link + "&action=" + action.getAction()));
				out.print("\"");
				
				if (action.getAction().equals("delete")) {
					out.print(" onclick=\"return (verifyDelete());\"");
				}
				
				out.print(">");
				
				out.print(StringEscapeUtils.escapeHtml(action.getLabel()));
				out.print("</a>");
			}
			
			out.print("</td>");
		}
		
		out.print("</tr>");
	}

	private void parseOrderBy(String order) {
		orderColumn = null;
		orderDirection = null;
		
		if (!StringUtils.isBlank(order)) {
			String[] parts = order.split(" ", 2);
			
			String columnLabel = parts[0];
			Column<T> column = getColumn(columnLabel);
			
			if (column != null) {
				orderColumn = columnLabel;
				orderDirection = "asc";
				
				if (parts.length == 2) {
					String direction = parts[1];
					
					if (
						"asc".equalsIgnoreCase(direction) ||
						"desc".equalsIgnoreCase(direction)
					) {
						orderDirection = direction;
					}
				}
			}
		}
	}

	private Column<T> getColumn(String column) {
		for (Column<T> item : columns) {
			if (!StringUtils.isBlank(item.getName()) && item.getName().equals(column)) {
				return item;
			}
		}
		
		return null;
	}
}
