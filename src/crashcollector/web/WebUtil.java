package crashcollector.web;

import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import crashcollector.Application;
import crashcollector.Patterns;

public final class WebUtil {
	private WebUtil() {
	}
	
	public static String formatIssues(String issues) {
    	String bugUrl = Application.getBugUrl();
    	
    	if (StringUtils.isBlank(issues)) {
    		return "";
    	}
    	
    	if (!StringUtils.isBlank(bugUrl)) {
    		Matcher matcher = Patterns.issues.matcher(issues);
    		StringBuilder sb = new StringBuilder();
    		int lastEnd = 0;
    		
    		while (matcher.find()) {
    			sb.append(StringEscapeUtils.escapeHtml(issues.substring(lastEnd, matcher.start())));
    			
    			String issue = issues.substring(matcher.start(), matcher.end());
    			boolean strike = false;
    			
    			if (issue.substring(0, 1).equals("-")) {
    				strike = true;
    				issue = issue.substring(1);
    			}
    			
    			sb.append("<a href=\"");
    			sb.append(StringEscapeUtils.escapeHtml(String.format(bugUrl, issue)));
    			sb.append("\" target=\"_blank\">");
    			
    			if (strike) {
    				sb.append("<strike>");
    			}
    			
    			sb.append(issue);
    			
    			if (strike) {
    				sb.append("</strike>");
    			}
    			
    			sb.append("</a>");
    			
    			lastEnd = matcher.end();
    		}
    		
    		sb.append(StringEscapeUtils.escapeHtml(issues.substring(lastEnd, issues.length())));
    		
    		return sb.toString();
    	} else {
    		return StringEscapeUtils.escapeHtml(issues);
    	}
	}
	
	public static String getRequestUrl(HttpServletRequest req) {
	    String reqUrl = req.getRequestURL().toString();
	    String queryString = req.getQueryString();   // d=789
	    if (queryString != null) {
	        reqUrl += "?"+queryString;
	    }
	    return reqUrl;
	}
}
