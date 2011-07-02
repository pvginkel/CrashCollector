<%@ page import="crashcollector.Application" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
   "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><%= StringEscapeUtils.escapeHtml(Application.getApplicationTitle()) %></title>
    <link rel="stylesheet" href="/stylesheets/blueprint/screen.css" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="/stylesheets/blueprint/plugins/buttons/screen.css" type="text/css" media="screen, projection">
    <link rel="stylesheet" href="/stylesheets/blueprint/print.css" type="text/css" media="print">
      <!--[if lt IE 8]><link rel="stylesheet" href="/stylesheets/blueprint/ie.css" type="text/css" media="screen, projection"><![endif]-->
    <link rel="stylesheet" href="/stylesheets/screen.css" type="text/css" media="screen, projection">
    <link rel="icon" type="image/x-icon" href="/favicon.ico" />
    <script>
    function verifyDelete()
    {
      return confirm('Are you sure?');
    }
    </script>
  </head>
  <body>
    <div class="container">
      <div class="last header">
        <div class="header-links">
          <% if (Application.getCurrentUser() != null) { %>
	        <% if (Application.getUserService().isUserAdmin()) { %>
		      <a href="products.jsp">Manage Products</a> |
            <% } %>
			<a href="<%= Application.getUserService().createLogoutURL(request.getRequestURI()) %>">Logout</a>
          <% } else { %>
            <a href="<%= Application.getUserService().createLoginURL(request.getRequestURI()) %>">Login</a>
          <% } %>
        </div>
        <img src="dialog-warning.png" />
        <a href="index.jsp"><%= StringEscapeUtils.escapeHtml(Application.getApplicationTitle()) %></a>
      </div>
