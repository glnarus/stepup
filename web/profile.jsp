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
        <h1>${user}'s Profile</h1>
        <h2 class="flash">${flash}</h2>
        <p>Badge:&nbsp;${user.badge.badgeHabitName}&nbsp;${user.badge.badgeLevelName}</p>
        <p>Name:&nbsp;${user.profile.firstName}&nbsp;${user.profile.lastName}</p>
        <p>Member since:&nbsp;${user.profile.prettyPrintJoinDate}</p>
        <p>Email:&nbsp;${user.profile.email}</p>
        <p>Phone number:&nbsp;${user.profile.phone}</p>
        <p>Fitness Goal:&nbsp;${user.profile.goal}</p>
        <p>Incentive Reward(s):&nbsp;${user.profile.reward}</p>
        <p>Receive email notifications?&nbsp;${user.profile.emailSubscribe}</p>
        <p>Receive text notifications?&nbsp;${user.profile.textSubscribe}</p>
        
        <p> <a href="stepup?action=home">Home</a>
            <a href="stepup?action=dashboard">Group Dashboard</a> |
            <a href="stepup?action=editprofile">Edit My Profile</a> |            
            <a href="stepup?action=logout">Logout</a>
        </p>
    </body>
</html>
