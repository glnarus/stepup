<%-- 
    Document   : editAchievement
    Created on : Nov 28, 2015, 3:04:32 PM
    Author     : gabriel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; -- Edit Achievement</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>       
    </head>
    <body>
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>Edit <u>${user}</u>'s Achievement</h1>
        <%@ include file="flash.jspf"%>           
          <form method="POST" action="<c:url value='stepup'/>" id="achievementform">            
            <fieldset>
            <table id="formtable">
                <input type="hidden" name="action" value="editachievement"/>
                <input type="hidden" name="id" value="${achToEdit.achievementId}"/>       
                <tr><td>Activity:</td>
                    <td>
                    <select name="activity" form="achievementform" id="squareinput">
                    <c:forEach items="${applicationScope.activityNames}" var="activity">
                        <option value="${activity}" ${activity == bean.activity ? 'selected' : ''}>${activity}</option>
                    </c:forEach>
                    </select>                    
                </td></tr>
                <tr><td>Date Accomplished:</td>
                <c:choose>
                    <c:when test="${bean ne null}">
                        <td><input id="squareinput" value="${bean.dateActivity}"
                                   type="date"  name="dateactivity" </td></tr>
                    </c:when>
                    <c:otherwise>
                        <td><input id="squareinput" value="${todaysdate}" 
                                   type="date"  name="dateactivity"></td></tr>
                    </c:otherwise>
                </c:choose>                         
                    
                <tr><td>Duration (minutes):</td><td><input id="squareinput"
                                  value="${bean.minutes}" type="number" 
                                  name="minutes" 
                                  placeholder="how long in minutes?"/></td></tr>
                
               <tr><td>Intensity Level:</td>
                    <td>
                        <select id="squareinput" value="${bean.intensity}" 
                                name="intensity" form="achievementform">                        
                            <c:forEach items="${applicationScope.intensityNames}" var="intensity">
                                <option value="${intensity}" ${intensity == bean.intensity ? 'selected' : ''}>${intensity}</option>
                            </c:forEach>                                                               
                        </select>
                    </td></tr>                           
                <tr><td>Notes (optional):</td><td><input id="squareinput" 
                                               value="${bean.notes}" type="text" 
                                               name="notes"/></td></tr>                
                <tr><td colspan="2"><input id="roundinput" type="Submit" 
                                            value="Save Changes"/></td></tr>
                <tr><td colspan="2"><input id="roundinput" 
                                           type="reset" value="Cancel"/>             
            </table>
            </fieldset>
        </form>        
        <a href="stepup?action=editachievement&id=${achToEdit.achievementId}&delete=yes"><em>Delete Achievement</em></a>
        <p> <a href="<c:url value='stepup?action=home'/>">Home</a>
            <a href="<c:url value='stepup?action=dashboard'/>">Group Dashboard</a> |
            <a href="<c:url value='stepup?action=profile&profilefor=${user}'/>">View My Profile</a> |            
            <a href="<c:url value='stepup?action=logout'/>">Logout</a>
        </p>
<%@ include file="footer.jspf"%> 
    </body>
</html>