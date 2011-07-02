<%@ page import="crashcollector.Application" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
    </div>
    <% if (Application.getGoogleAnalyticsKey() != null) { %>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("<%= StringEscapeUtils.escapeJava(Application.getGoogleAnalyticsKey()) %>");
pageTracker._trackPageview();
} catch(err) {}</script>
<% } %>
  </body>
</html>
