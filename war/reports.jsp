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
ProductRepository productRepository = new ProductRepository(pm);
VersionRepository versionRepository = new VersionRepository(pm);

try {
	String versionString = request.getParameter("version");
	
	if (versionString == null) {
		versionString = "";
	}
	
	String[] parts = versionString.split(":", 2);
	
	Product product = productRepository.getByLabel(parts[0]);
	Version version = null;
	
	if (product != null && parts.length > 1) {
		version = versionRepository.getByProductIdAndLabel(product.getId(), parts[1]);
	}
	
	String where = null;
	List<Object> parameters = new ArrayList<Object>();
	
	if (version != null) {
		where = "versionId == :p1";
		parameters.add(version.getId());
	} else if (product != null) {
		where = "productId == :p1";
		parameters.add(product.getId());
	}
	
	TableRenderer<Crash> table = new TableRenderer<Crash>(Crash.class, where, parameters, "hits desc");
	
	table.addColumn(new Column<Crash>("hits", "Hits") { public String getValue(Crash entity) {
		return entity.getHits().toString();
	} });
    table.addColumn(new Column<Crash>("signature", "Signature") { public String getValue(Crash entity) {
    	return StringEscapeUtils.escapeHtml(truncate(entity.getSignature(), 120));
   	} });
    table.addColumn(new Column<Crash>(null, "Bugs") { public String getValue(Crash entity) {
    	return WebUtil.formatIssues(entity.getIssues());
    } });
	
    if (Application.isUserAdmin()) {
		table.addAction(new Action("delete", "delete"));
	}
	
	table.setDetailsUrl("report.jsp");
	
	table.render(pm, request, out);
	
} finally {
    pm.close();
}
%>
<jsp:include page="/templates/footer.jsp" />

