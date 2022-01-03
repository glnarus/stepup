<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Create Squad</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>  
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>Create a new squad for your friends & family on StepUp&trade;</h1>
        <%@ include file="flash.jspf"%>  
        <form method="POST" action="<c:url value='stepup'/>">
            <input type="hidden" name="action" value="createsquad"/>
            <table id="formtable">
                <tr><td>New Squad Name: </td><td><input value="" type="text" name="newsquadname" placeholder="Enter in a cool squad name here" size = "33"/></td></tr>             
                <tr><td colspan="2"><input type="submit" value="Create Squad!"/></td></tr>
            </table>
        </form>
        <p> <a href="<c:url value = 'stepup?action=mysquads' />">My Squads</a> | 
            <a href="<c:url value = 'stepup?action=home' />">Home</a> |             
            <a href="<c:url value = 'stepup?action=logout'/>">Logout</a>
        </p>      
<%@ include file="footer.jspf"%>       
    </body>
</html>
