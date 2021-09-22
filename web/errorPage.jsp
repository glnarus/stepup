<%-- 
    Document   : errorPage
    Created on : Sep 20, 2021, 2:20:10 PM
    Author     : gabriel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@page isErrorPage="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Error</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>
    </head>
    <body>
        <h1>Our apologies, an unexpected error occurred</h1>
        <p><a href="<c:url value='stepup'/>">Return to StepUp</a></p>       
<%@ include file="footer.jspf"%>    
    </body>
</html>
