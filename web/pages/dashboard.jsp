<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Dashboard</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>StepUp&trade; Community Dashboard</h1>
        <%@ include file="flash.jspf"%>        
        <h2>Activity Stream</h2>
        <div class="scrollboxBig">
        <c:forEach var="achievement" items="${achievementsAll}">
            <a href="<c:url value='stepup?action=profile&profilefor=${achievement.user}'/>">
                                                        ${achievement.user}
            </a>&nbsp;&nbsp;${achievement.prettyPrintActivityDate}->
            ${achievement.activity}&nbsp;${achievement.minutes}&nbsp;minutes
            &nbsp;at&nbsp;${achievement.intensity}&nbsp;intensity
            <c:if test="${not empty achievement.notes}">
                        [<i>${achievement.notes}</i>]</c:if>
            <br>                                   
        </c:forEach>
        </div>

        <h2>Shout outs!</h2>
        <div class="scrollbox">
        <c:forEach var="post" items="${posts}">
            [${post.prettyPrintPostDate}]&nbsp;
            <a href="<c:url value='stepup?action=profile&profilefor=${post.username}' />">
                                                        ${post.username} 
            </a>->&nbsp;&nbsp;${post.content}<br>                                   
        </c:forEach>   
        </div>
        <form method="POST" action="<c:url value='stepup'/>">
            <input type="hidden" name="action" value="squaddashboard"/>
            <table>
                <tr><td><input type="text" size="190" maxlength="280" id="postinput" 
                               placeholder="What would you like to say?" 
                               name="content"/></td></tr>                
                <tr><td colspan="2"><input type="submit" id="roundinput" 
                                           value="Post it!"/></td></tr>
            </table>
        </form>        
        
        <p> <a href="<c:url value = 'stepup?action=mysquads' />">My Squads</a> |
            <a href="<c:url value='stepup?action=home'/>">Home</a> |
            <a href="<c:url value='stepup?action=profile&profilefor=${user}'/>">My Profile</a> |              
            <a href="<c:url value='stepup?action=logout'/>">Logout</a>
        </p>       
    <%@ include file="footer.jspf"%>    
    </body>
</html>
