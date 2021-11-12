<%-- 
    Document   : home
    Created on : Oct 31, 2015, 5:08:52 PM
    Author     : gabriel
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Invite Members to Squad</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>
    
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>Invite Members to ${squadname}</h1>
        <br>Use this page to invite friends and family members to your group. 
        <br><br>
        <h2>Invitations already sent:</h2>
        <div class="scrollbox">
            <c:forEach var="invited" items="${invitationsSent}">                
                <a href="<c:url value='stepup?action=profile&profilefor=${invited.userName}' />">${invited.userName}</a> 
                <br>
            </c:forEach>   
        </div>                     
        
        
        <h2>Send invitation </h2> 
                <%@ include file="flash.jspf"%>   
          <form method="POST" action="<c:url value='stepup'/>">
            <input type="hidden" name="action" value="invitemembers"/>
            <table id="formtable">               
                <tr><td>Username to invite:</td><td><input id="roundinput" 
                                            value="" 
                                            type="text" size="30" maxlength="40" name="invitedusername"/></td></tr>             
                                
                <tr><td></td><td colspan="2"><input id="roundinput" type="submit" value="Send Invite!"/></td></tr>            
            </table>
          </form>
        
        
        <p> <a href="<c:url value = 'stepup?action=home' />">Home</a> |                                  
            <a href="<c:url value = 'stepup?action=mysquads' />">My Squads</a> |            
            <a href="<c:url value = 'stepup?action=logout'/>">Logout</a>
        </p>        
<%@ include file="footer.jspf"%>
    </body>    
</html>
