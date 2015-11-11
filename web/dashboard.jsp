<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp - Dashboard</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>
        <h1>StepUp Community Dashboard</h1>
        <h2 class="flash">${flash}</h2>
        <h2>Activity Stream</h2>
        <c:forEach var="achievement" items="${achievements}">
            <a href="stepup?action=profile&profilefor=${achievement.user}">
                                                        ${achievement.user} 
            </a>&nbsp;&nbsp;${achievement.prettyPrintActivityDate}->
            ${achievement.activity}&nbsp;${achievement.minutes}&nbsp;minutes
            &nbsp;at&nbsp;${achievement.intensity}&nbsp;intensity
            <c:if test="${not empty achievement.notes}">
                        [<i>${achievement.notes}</i>]</c:if>
            <br>                                   
        </c:forEach>
        <p> <a href="stepup?action=home">Home</a>
            <a href="stepup?action=profile&profilefor=${user}">My Profile</a> |              
            <a href="stepup?action=logout">Logout</a>
        </p>                
    </body>
</html>
