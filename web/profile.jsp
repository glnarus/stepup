<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp -- Profile for ${user}</title>
        <link rel="stylesheet" type="text/css" href="stepup.css"/>
    </head>
    <body>
        <h1>${subject}'s Profile</h1>
        <h2 class="flash">${flash}</h2>
        <p>Badge:&nbsp;${subject.badge.badgeHabitName}&nbsp;
                                          ${subject.badge.badgeLevelName}</p>
        <p>Name:&nbsp;${profile.firstName}&nbsp;${profile.lastName}</p>
        <p>Member since:&nbsp;${profile.prettyPrintJoinDate}</p>
        <p>Fitness Goal:&nbsp;${profile.goal}</p>
        <p>Incentive Reward(s):&nbsp;${profile.reward}</p>
        <c:choose>
            <c:when test="${user.username eq subject.username}">
                <p>Email:&nbsp;${profile.email}</p>
                <p>Phone number:&nbsp;${profile.phone}</p>        
                <p>Receive email notifications?&nbsp;${profile.emailSubscribe}</p>
                <p>Receive text notifications?&nbsp;${profile.textSubscribe}</p>                                
            </c:when>
            <c:otherwise>
                <p><small>Contact info fields are considered private.</small>
            </c:otherwise>
        </c:choose>        

        
        <p> <a href="stepup?action=home">Home</a>
            <a href="stepup?action=dashboard">Group Dashboard</a> |
            <a href="stepup?action=editprofile">Edit My Profile</a> |            
            <a href="stepup?action=logout">Logout</a>
        </p>
    </body>
</html>
