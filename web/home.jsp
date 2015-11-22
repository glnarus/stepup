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
    
    <c:choose>
       <c:when test="${user.badge.badgeLevel == 1}">
           <body background="images/grasshopper_large_bk.jpg">
       </c:when>
       <c:when test="${user.badge.badgeLevel == 2}">
           <body background="images/rabbit_large_bk.jpg">
       </c:when>   
       <c:when test="${user.badge.badgeLevel == 3}">
           <body background="images/kangaroo_large_bk.jpg">
       </c:when>               
       <c:otherwise>
           <body>
       </c:otherwise>
   </c:choose>     
    
        
        <h1>Welcome back <u>${user.username}</u>!</h1>
 
            <c:choose>
               <c:when test="${user.badge.badgeHabit == 1}">
                   <h4>Fitness Habit 
                       <img src="images/bronze_star_home.jpg"></h4>
               </c:when>
               <c:when test="${user.badge.badgeHabit == 2}">
                   <h4>Fitness Habit 
                       <img src="images/silver_star_home.jpg"></h4>
               </c:when> 
               <c:when test="${user.badge.badgeHabit == 3}">
                   <h4>Fitness Habit 
                       <img src="images/gold_star_home.jpg"></h4>
               </c:when>           
               <c:otherwise>                   
               </c:otherwise>
           </c:choose>            
        <h2>Enter a new achievement</h2>
        <h2 class="flash">${flash}</h2>
        <form method="POST" action="stepup" id="achievementform">     
            <fieldset>
            <table id="formtable">
                <td><input type="hidden" name="action" value="home"/>
                <tr><td>Activity:</td>                    
                    <td><select value="${bean.activity}" id="squareinput"
                                    name="activity" form="achievementform">
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
                    <td><select id="squareinput" value="${bean.intensity}" 
                                name="intensity" form="achievementform">
                            <option value="Light">Light</option>
                            <option value="Moderate">Moderate</option>
                            <option value="Hard">Hard</option>
                            <option value="Strenuous">Strenuous</option>                                                                                
                        </select>
                    </td></tr>                           
                <tr><td>Notes (optional):</td><td><input id="squareinput" 
                                               value="${bean.notes}" type="text" 
                                               name="notes"/></td></tr>                
                <tr><td colspan="2"><input id="roundinput" type="submit" 
                                            value="Log it!"/></td></tr>
            </table>
            </fieldset>
        </form>
        <p> <a href="stepup?action=dashboard">Group Dashboard</a> |
            <a href="stepup?action=profile&profilefor=${user}">My Profile</a> |            
            <a href="stepup?action=logout">Logout</a>
        </p>
        <h2> Achievement Log </h2>
        <div class="scrollbox">
            <c:forEach var="achievement" items="${achievements}">
                ${achievement.user}: ${achievement}<br>
            </c:forEach>   
        </div>                
            <h4>Star Progress Last 6 Weeks</h4>
            <c:forEach var="score" items="${sixweeksscores}">
                <c:choose>
                    <c:when test="${score == 0}">
                        <img src="images/bar3_level0.png" style="width:10px;height:20px;">
                    </c:when>
                    <c:when test="${score == 1}">
                        <img src="images/bar3_level1.png" style="width:10px;height:20px;">
                    </c:when>                        
                    <c:when test="${score == 2}">
                        <img src="images/bar3_level2.png" style="width:10px;height:20px;">
                    </c:when>     
                    <c:when test="${score == 3}">
                        <img src="images/bar3_level3.png" style="width:10px;height:20px;">
                    </c:when>                            
                    <c:otherwise>
                    </c:otherwise>
                </c:choose>                  
            </c:forEach>  
        <div id="footer">
        Copyright &copy;2015 Gabriel Narus ACC Capstone Project                    
        </div>
    </body>    
</html>
