<%-- 
    Document   : login
    Created on : Oct 31, 2015, 4:54:57 PM
    Author     : gabriel
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Login</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>
    </head>
    <body>
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>
        <h1>Log in to StepUp&trade;</h1>
        <%@ include file="flash.jspf"%>          
        <form method="POST" action="stepup">
            <input type="hidden" name="action" value="login"/>
            <table id="logintable">
                <tr><td>username:</td><td><input id="roundinput" type="text" 
                                                 name="username"/></td></tr>
                <tr><td>password:</td><td><input id="roundinput" type="password" 
                                                 name="password"/></td></tr>
                <tr><td colspan="2"><input id="roundinput" type="submit" 
                                           value="Log in!"/></td></tr>
            </table>
        </form>
        <p><a href="stepup?action=register">Register new user</a></p>
<%@ include file="footer.jspf"%>    
    </body>
</html>
