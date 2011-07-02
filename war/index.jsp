<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="crashcollector.*" %>
<%@ page import="crashcollector.data.*" %>
<%@ page import="com.google.appengine.api.users.*" %>
<%@ page import="javax.jdo.*" %>
<%@ page import="org.apache.commons.lang.*" %>
<jsp:include page="/templates/header.jsp" />
<%
PersistenceManager pm = Application.getPersistenceManager();
ProductRepository productRepository = new ProductRepository(pm);
VersionRepository versionRepository = new VersionRepository(pm);

try {
	List<Product> products = productRepository.getAll();
	Map<Long, Product> productMap = productRepository.convertToMap(products);
	List<Version> versions = versionRepository.getAll();

%>
<script>
function load_versions(product)
{
  var versionSelect = document.getElementById('version');

  while (versionSelect.options.length > 0)
    versionSelect.remove(0);

  add_version(versionSelect, product, '');

  <% for (Version version : versions) {
	     Product product = productMap.get(version.getProductId()); %>
    if (product == "<%= StringEscapeUtils.escapeJava(product.getLabel()) %>")
	  add_version(versionSelect, product, "<%= StringEscapeUtils.escapeJava(version.getLabel()) %>");
  <% } %>

  versionSelect.options[0].selected = true;
}

function add_version(versionSelect, product, version)
{
  versionSelect.options.add(new Option(version == '' ? 'All' : version, product + ':' + version));
}
</script>

<div class="span-24 last searchbar">

  <form action="reports.jsp" method="get">

    <table>
      <tr>
        <th>
          Product:
        </th>
        <th>&nbsp;</th>
        <th>
          Version:
        </th>
      </tr>
      <tr>
        <td>
          <select name="product" id="product" size="4" onchange="load_versions(this.value);">
          	<% if (products.size() > 0) { %>
	            <option value="" selected="selected">All</option>
            <% } %>
            <% for (Product product : products) { %>
	            <option value="<%= StringEscapeUtils.escapeHtml(product.getLabel()) %>"
	            <% if (products.size() == 1) { %> selected="selected"<% } %>>
	            <%= StringEscapeUtils.escapeHtml(product.getLabel()) %></option>
            <% } %>
          </select>
        </td>
        <td>&nbsp;&nbsp;&nbsp;</td>
        <td>
          <select name="version" id="version" size="4">
            <option value="" selected="selected">All</option>
          </select>
        </td>
        <td>&nbsp;&nbsp;&nbsp;</td>
        <td>
          <button onclick="document.forms[0].submit();">Find</button>
        </td>
      </tr>
    </table>

  </form>

</div>

<% if (products.size() == 1) { %>
<script>
load_versions(document.getElementById('product').value);
</script>
<% } %>

<%
} finally {
	pm.close();
}
%>
<jsp:include page="/templates/footer.jsp" />
