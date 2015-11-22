<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp -- Profile for ${user}</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>     
    </head>
    <c:choose>
       <c:when test="${subject.badge.badgeLevel == 1}">
           <body background="images/grasshopper_large_bk.jpg">
       </c:when>
       <c:when test="${subject.badge.badgeLevel == 2}">
           <body background="images/rabbit_large_bk.jpg">
       </c:when>   
       <c:when test="${subject.badge.badgeLevel == 3}">
           <body background="images/kangaroo_large_bk.jpg">
       </c:when>               
       <c:otherwise>
           <body>
       </c:otherwise>
   </c:choose> 
        <h1>${subject}'s Profile</h1>
        <h2 class="flash">${flash}</h2>
        <p>Fitness Habit
            <c:choose>
               <c:when test="${subject.badge.badgeHabit == 1}">
                       <img src="images/bronze_star_home.jpg">
               </c:when>
               <c:when test="${subject.badge.badgeHabit == 2}">
                       <img src="images/silver_star_home.jpg">
               </c:when> 
               <c:when test="${subject.badge.badgeHabit == 3}"> 
                       <img src="images/gold_star_home.jpg">
               </c:when>           
               <c:otherwise>                   
               </c:otherwise>
           </c:choose>           
        
        </p>
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
