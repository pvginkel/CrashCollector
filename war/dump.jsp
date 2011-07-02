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
    
    Report report = reportRepository.getById(id);
    
    if (report == null) {
        %>Report not found<%
    } else {
    	String action = request.getParameter("action");
    	
    	if (!StringUtils.isBlank(action)) {
    		Application.requireAdministrator();
    		
    		if ("delete".equalsIgnoreCase(action)) {
    			Long crashId = report.getCrashId();
    			
    			pm.deletePersistent(report);
    			
    			Transaction tx = pm.currentTransaction();
    			
    			try {
    				tx.begin();
    				
    				Crash crash = crashRepository.getById(crashId);
    				crash.setHits(crash.getHits() - 1);
    				pm.makePersistent(crash);
    				
    				tx.commit();
    			} finally {
    				if (tx.isActive()) {
    					tx.rollback();
    				}
    			}
    		}
    		
    		response.sendRedirect(request.getParameter("referer"));
    		return;
    	}
    	
    	final Crash crash = crashRepository.getById(report.getCrashId());
    	final Product product = productRepository.getById(crash.getProductId());
    	final Version version = versionRepository.getById(crash.getVersionId());
    	
    	DetailsTable<Report> table = new DetailsTable<Report>();
    	
        table.addColumn(new Column<Report>("signature", "Signature") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(crash.getSignature());
        } });
        table.addColumn(new Column<Report>("uuid", "UUID") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getUuid());
        } });
        table.addColumn(new Column<Report>("created", "Created") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(Application.defaultDateTimeFormat.format(entity.getCreated()));
        } });
        table.addColumn(new Column<Report>("productId", "Product") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(product.getLabel());
        } });
        table.addColumn(new Column<Report>("versionId", "Version") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(version.getLabel());
        } });
        table.addColumn(new Column<Report>("os", "OS") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getOs());
        } });
        table.addColumn(new Column<Report>("osVersion", "OS Version") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getOsVersion());
        } });
        table.addColumn(new Column<Report>("cpu", "CPU") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getCpu());
        } });
        table.addColumn(new Column<Report>("cpuInfo", "CPU Info") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getCpuInfo());
        } });
        table.addColumn(new Column<Report>("reason", "Crash Reason") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getReason());
        } });
        table.addColumn(new Column<Report>("message", "Crash Message") { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getMessage());
        } });
        table.addColumn(new Column<Report>("comments", "User Comments", false) { public String getValue(Report entity) {
            return StringEscapeUtils.escapeHtml(entity.getComments().getValue());
        } });
        if (Application.isUserAdmin()) {
	        table.addColumn(new Column<Report>("email", "User E-Mail", false) { public String getValue(Report entity) {
	            return StringEscapeUtils.escapeHtml(entity.getEmail());
	        } });
	        table.addColumn(new Column<Report>("extra", "Extra Information", false) { public String getValue(Report entity) {
	            return StringEscapeUtils.escapeHtml(entity.getExtra());
	        } });
	        table.addColumn(new Column<Report>("comments", "User Host", false) { public String getValue(Report entity) {
	            return StringEscapeUtils.escapeHtml(entity.getHost());
	        } });
        }
%>

<h1><%= StringEscapeUtils.escapeHtml(product.getLabel() + " " + version.getLabel() + " [@ " + crash.getSignature() + "]") %></h1>

<%

table.render(report, request, out);

int i = 0;

%>
<h1>Stack Trace</h1>

<pre><%= StringEscapeUtils.escapeHtml(report.getStackTrace().getValue()) %></pre>

<%
    }
} finally {
    pm.close();
}
%>
<jsp:include page="/templates/footer.jsp" />
