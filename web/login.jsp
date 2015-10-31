<%-- 
    Document   : login
    Created on : Oct 31, 2015, 4:54:57 PM
    Author     : gabriel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp - Login</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>
    </head>
    <body>
        <h1>Log in to StepUp&trade;</h1>
        <h2 class="flash">${flash}</h2>
        <form method="POST" action="stepup">
            <input type="hidden" name="action" value="login"/>
            <table id="formtable">
                <tr><td>username:</td><td><input type="text" 
                                                 name="username"/></td></tr>
                <tr><td>password:</td><td><input type="password" 
                                                 name="password"/></td></tr>
                <tr><td colspan="2"><input type="submit" 
                                           value="Log in!"/></td></tr>
            </table>
        </form>
    </body>
</html>
