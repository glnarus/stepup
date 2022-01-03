<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; -- Profile for ${user}</title>
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
               <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>          
        <h1><u>${subject}</u>'s Profile</h1>        
        <%@ include file="flash.jspf"%>  
        <c:choose>
            <c:when test="${profile.imageType ne null}">
                <img id="profilePic" src="stepup?action=image&for=${subject}"/>
            </c:when>
            <c:otherwise>
                <img id="profilePic" src="images/default_icon_sm.png" align="left"/>
            </c:otherwise>
        </c:choose>             
        <p>Fitness Habit
            <c:choose>
               <c:when test="${subject.badge.badgeHabit == 1}">
                       <img src="images/bronze_star_home.png">
               </c:when>
               <c:when test="${subject.badge.badgeHabit == 2}">
                       <img src="images/silver_star_home.png">
               </c:when> 
               <c:when test="${subject.badge.badgeHabit == 3}"> 
                       <img src="images/gold_star_home.png">
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
            </c:when>
            <c:otherwise>
                <p><small>Contact info fields are considered private.</small>
                <c:choose>
                    <c:when test="${following == null}">
                        <p><a href="<c:url value ='stepup?action=follow&id=${subject.userId}'/>">Follow ${subject}'s fitness activity</a>
                    </c:when>
                    <c:otherwise>
                        <p><a href="<c:url value='stepup?action=unfollow&id=${subject.userId}'/>">Turn off following ${subject}</a>
                    </c:otherwise>
                </c:choose>
                
            </c:otherwise>
        </c:choose>                          
        <h2> Achievement Log </h2>
        <div class="scrollbox">
            <c:forEach var="achievement" items="${subjectachievements}">
                ${achievement.user}: ${achievement}<br>
            </c:forEach>   
        </div>                         

        
        <p> <a href="<c:url value='stepup?action=home'/>">Home</a>
            <a href="<c:url value='stepup?action=mysquads'/>">My Squads</a> |
            <a href="<c:url value='stepup?action=editprofile'/>">Edit My Profile</a> |            
            <a href="<c:url value='stepup?action=logout'/>">Logout</a>
        </p>
<%@ include file="footer.jspf"%>      
    </body>
</html>
