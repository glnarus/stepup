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
        <title>StepUp - Home</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>
        <h1>Welcome back ${user.username}!</h1>
        <h2>Badge: ${user.badge.badgeHabitName}&nbsp;${user.badge.badgeLevelName}
        <h2>Enter a new achievement</h2>
        <h2 class="flash">${flash}</h2>
        <form method="POST" action="stepup" id="achievementform">
            <input type="hidden" name="action" value="home"/>
            <select value="${bean.activity}" name="activity" form="achievementform">
                <option value="Ball Sports">Ball Sports</option>
                <option value="Bicycling">Bicycling</option>
                <option value="Boxing">Boxing</option>
                <option value="Calisthenics">Calisthenics</option>
                <option value="Climbing">Climbing</option>
                <option value="Dance">Dance</option>
                <option value="Hiking">Hiking</option>
                <option value="Misc Cardio">Misc Cardio</option>
                <option value="Running">Running</option>
                <option value="Swimming">Swimming</option>
                <option value="Walking">Walking</option>
                <option value="Weights">Weights</option>
                <option value="Yoga">Yoga</option>                                                                                    
            </select>            
            <table id="formtable">
                <tr><td>Date Accomplished:</td><td><input value="${bean.dateActivity}" type="date" name="dateactivity" placeholder="MM/DD/YYYY"></td></tr>
                <tr><td>Duration (minutes):</td><td><input value="${bean.minutes}" type="number" name="minutes" placeholder="how long in minutes?"/></td></tr>
                <tr><td><select value="${bean.intensity}" name="intensity" form="achievementform">
                            <option value="Light">Light</option>
                            <option value="Moderate">Moderate</option>
                            <option value="Hard">Hard</option>
                            <option value="Strenuous">Strenuous</option>
                        </select>                    
                </td></tr>
                <tr><td>Notes (optional):</td><td><input value="${bean.notes}" type="text" name="notes"/></td></tr>                
                <tr><td colspan="2"><input type="submit" value="Log it!"/></td></tr>
            </table>
        </form>
        <p> <a href="stepup?action=dashboard">Group Dashboard</a> |
            <a href="stepup?action=profile&profilefor=${user}">My Profile</a> |            
            <a href="stepup?action=logout">Logout</a>
        </p>
        <h2> Achievement Log </h2>
            <c:forEach var="achievement" items="${achievements}">
                ${achievement.user}: ${achievement}<br>
            </c:forEach>        
    </body>    
</html>
