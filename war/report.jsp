<%@page import="java.net.URLEncoder"%>
<%@page import="java.text.DateFormat"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="crashcollector.*" %>
<%@ page import="crashcollector.data.*" %>
<%@ page import="crashcollector.web.*" %>
<%@ page import="com.google.appengine.api.users.*" %>
<%@ page import="javax.jdo.*" %>
<%@ page import="org.apache.commons.lang.*" %>
<jsp:include page="/templates/header.jsp" />
<%

PersistenceManager pm = Application.getPersistenceManager();
CrashRepository crashRepository = new CrashRepository(pm);
ProductRepository productRepository = new ProductRepository(pm);
VersionRepository versionRepository = new VersionRepository(pm);
ReportRepository reportRepository = new ReportRepository(pm);

try {
	Long id = Long.parseLong(request.getParameter("id"));
	
	Crash crash = crashRepository.getById(id);
	
	if (crash == null) {
		%>Crash not found<%
	} else {
		String action = request.getParameter("action");
		
		if (!StringUtils.isBlank(action)) {
			Application.requireAdministrator();
			
			if ("delete".equalsIgnoreCase(action)) {
				pm.deletePersistentAll(reportRepository.getAllByCrashId(crash.getId()));
				pm.deletePersistent(crash);
			} else if ("updateBugs".equalsIgnoreCase(action)) {
				crash.setIssues(request.getParameter("bugs"));
				pm.makePersistent(crash);
			}
		
			response.sendRedirect(request.getParameter("referer"));
			return;
		}
		
		final Product product = productRepository.getById(crash.getProductId());
		final Version version = versionRepository.getById(crash.getVersionId());
		
	    List<Object> parameters = new ArrayList<Object>();
	    
	    parameters.add(id);
	    
	    TableRenderer<Report> table = new TableRenderer<Report>(Report.class, "crashId == :p1", parameters, "created desc");
	    
        table.addColumn(new Column<Report>("created", "Created") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(Application.defaultDateTimeFormat.format(entity.getCreated()));
        } });
        table.addColumn(new Column<Report>(null, "Product") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(product.getLabel());
        } });
        table.addColumn(new Column<Report>(null, "Version") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(version.getLabel());
        } });
        table.addColumn(new Column<Report>("os", "OS") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getOs());
        } });
        table.addColumn(new Column<Report>("cpu", "CPU") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getCpu());
        } });
        table.addColumn(new Column<Report>("reason", "Reason") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getReason());
        } });
        table.addColumn(new Column<Report>(null, "Comments", false) { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(truncate(entity.getComments().getValue(), 40));
        } });
	    
	    if (Application.isUserAdmin()) {
	        table.addAction(new Action("delete", "delete"));
	    }
	    
	    table.setDetailsUrl("dump.jsp");
%>

<script>
function editBugs()
{
    document.getElementById('bugs_non_edit').style.display = 'none';
    document.getElementById('bugs_edit').style.display = '';
}
</script>

<p>
<b>Crash Reports for <%= StringEscapeUtils.escapeHtml(crash.getSignature()) %></b><br/>
<a href="<%= StringEscapeUtils.escapeHtml(request.getParameter("referer")) %>">Back to overview</a>
</p>

<% if (!StringUtils.isBlank(crash.getIssues()) || Application.isUserAdmin()) { %>
  <table class="tight" id="bugs_non_edit">
    <tr>
      <td>Bugs:</td>
      <td>&nbsp;</td>
      <td>
        <% if (!StringUtils.isBlank(crash.getIssues())) { %>
          <%= WebUtil.formatIssues(crash.getIssues()) %>
        <% } else { %>
          (none)
        <% } %>
      </td>
      <% if (Application.isUserAdmin()) { %>
        <td>&nbsp;</td>
        <td><a href="#" onclick="editBugs();">edit</a></td>
      <% } %>
    </tr>
  </table>
  <% if (Application.isUserAdmin()) { %>
  <form id="bugs_edit" action="report.jsp?id=<%= crash.getId().toString() %>&amp;action=updateBugs&amp;referer=<%= URLEncoder.encode(WebUtil.getRequestUrl(request), "UTF-8") %>" method="post" style="display: none;">
    <table class="tight">
      <tr>
        <td>Bugs:</td>
        <td>&nbsp;</td>
        <td><input type="text" name="bugs" value="<%= crash.getIssues() == null ? "" : StringEscapeUtils.escapeHtml(crash.getIssues()) %>"/></td>
        <td>&nbsp;</td>
        <td><button onclick="document.forms[0].submit();">Update</button></td>
      </tr>
    </table>
  </form>
  <% } %>
<% } %>


<%
	    table.render(pm, request, out);
	}
} finally {
    pm.close();
}
%>
<jsp:include page="/templates/footer.jsp" />
