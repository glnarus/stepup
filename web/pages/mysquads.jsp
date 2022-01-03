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
        <title>StepUp&trade; - My Squads</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>
    
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>${user.username}'s Step Up Squads</h1>
        <br>Use this page to keep up with your friends and family members in your own group. 
        <br>Write encouraging posts and share fitness activities!  
        <br><br>
        <h2>Squads you are invited to join</h2>
        <div class="scrollbox">
            <c:forEach var="invite" items="${myInvitations}">
                Invitation by <span class="emphasistext">${invite.ownerName}</span> to join <span class="emphasistext">${invite.squadName}</span>:&nbsp;&nbsp;
                <a href="<c:url value ='stepup?action=joinsquad&id=${invite.squadId}'/>">join</a> | 
                <a href="<c:url value ='stepup?action=removeinvite&id=${invite.squadId}'/>">decline</a>
                <br>
            </c:forEach>   
        </div>                     
        
        
        <h2>Squads you are in</h2>        
        <div class="scrollbox">
            <c:forEach var="squad" items="${joinedOrOwnedSquads}">                
                <c:choose>
                    <c:when test="${squad.isOwner == false}">
                        Member of <a href="<c:url value='stepup?action=squaddashboard&squadid=${squad.squadId}'/>">${squad.squadName}</a>, 
                        owned by: <a href="<c:url value='stepup?action=profile&profilefor=${squad.ownerName}'/>">${squad.ownerName}</a> <br>
                    </c:when>                                        
                    <c:otherwise>
                        Owner of <a href="<c:url value='stepup?action=squaddashboard&squadid=${squad.squadId}'/>">${squad.squadName}</a> | 
                                 <a href="<c:url value ='stepup?action=invitemembers&squadid=${squad.squadId}&squadname=${squad.squadName}'/>">Invite Others</a><br>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
        
        <h2><a href="<c:url value = 'stepup?action=createsquad' />">Create New Squad</a></h2>
        
  
        <p> <a href="<c:url value = 'stepup?action=home' />">Home</a> |                                  
            <a href="<c:url value ='stepup?action=profile&profilefor=${user}'/>">My Profile</a> |            
            <a href="<c:url value = 'stepup?action=logout'/>">Logout</a>
        </p>        
<%@ include file="footer.jspf"%>
    </body>    
</html>
