<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="crashcollector.*" %>
<%@ page import="crashcollector.data.*" %>
<%@ page import="com.google.appengine.api.users.*" %>
<%@ page import="javax.jdo.*" %>
<%@ page import="org.apache.commons.lang.*" %>
<jsp:include page="/templates/header.jsp" />
<%

Application.requireAdministrator();

PersistenceManager pm = Application.getPersistenceManager();
VersionRepository versionRepository = new VersionRepository(pm);
ProductRepository productRepository = new ProductRepository(pm);

try {
    String error = null;
    String action = request.getParameter("action");
    
    if (!StringUtils.isBlank(action)) {
	    if ("delete".equals(action)) {
	        Long id = Long.parseLong(request.getParameter("id"));
	        
	        if (versionRepository.getAllByProductId(id).size() > 0) {
	            error = "Product is in use";
	        } else {
	            productRepository.deleteById(id);
	        }
	    } else if ("create".equals(action)) {
	        String label = request.getParameter("label");
	        
	        if (StringUtils.isBlank(label)) {
	            error = "Product name is required";
	        } else {
	            pm.makePersistent(
	            	new Product(label, UUID.randomUUID().toString())
	            );
	        }
	    }
	    
	    if (error == null) {
		    response.sendRedirect("/products.jsp");
		    return;
	    }
    }

%>

<% if (error != null) { %>
<p class="error"><%= StringEscapeUtils.escapeHtml(error) %></p>
<% } %>

<table>
  <tr>
    <th>Product</th>
    <th>UUID</th>
    <th>&nbsp;</th>
  </tr>
  <% for (Product product : productRepository.getAll()) { %>
    <tr>
      <td><%= StringEscapeUtils.escapeHtml(product.getLabel()) %></td>
      <td><%= StringEscapeUtils.escapeHtml(product.getUuid()) %></td>
      <td><a href="/products.jsp?id=<%= product.getId() %>&amp;action=delete">Delete</a>
    </tr>
  <% } %>
</table>

<p><i>Add a new product</i></p>

<form action="/products.jsp?action=create" method="post">
  <table>
    <tr>
      <td>Product</td>
      <td><input type="text" name="label" /></td>
    </tr>
    <tr>
      <td>&nbsp;</td>
      <td><input type="submit" value="Create"/></td>
    </tr>
  </table>
</form>

<%
} finally {
	pm.close();
}
%>
<jsp:include page="/templates/footer.jsp" />
