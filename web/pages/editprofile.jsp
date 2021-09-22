<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; -- Edit ${user}'s Profile</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>       
    </head>
    <body>
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1><u>${user}</u>'s Profile</h1>
        <%@ include file="flash.jspf"%>     
          <form method="POST" action="<c:url value='stepup'/>">
            <input type="hidden" name="action" value="editprofile"/>
            <table id="formtable">               
                <tr><td>First Name:</td><td><input id="roundinput" 
                                            value="${bean.firstName}" 
                                            type="text" name="fname"/></td></tr>
                <tr><td>Last Name:</td><td><input id="roundinput" 
                                         value="${bean.lastName}" type="text" 
                                         name="lname"/></td></tr>
                <tr><td>Email (optional):</td><td><input id="roundinput" 
                                             value="${bean.email}" type="email" 
                                             name="email"/></td></tr>
                <tr><td>Phone (optional):</td><td><input id="roundinput" 
                                          value="${bean.phone}" type="text" 
                                          name="phone" 
                                          placeholder="###-###-####"/></td></tr>
                <tr><td>Goal (optional):</td><td><input id="roundinput" 
                                               value="${bean.goal}" type="text" 
                                               name="goal" 
                                               placeholder="Running distance or time?  Weight loss? What is your goal?"/></td></tr>
                <tr><td>Reward (optional):</td><td><input id="roundinput" 
                                           value="${bean.reward}" type="text" 
                                           name="reward" placeholder="How would you reward yourself when you meet your goal?"/></td></tr>
                <tr><td>Profile picture:</td>
                    <td>
                    <c:choose>
                        <c:when test="${user.profile.imageType ne null}">
                            <img id="profilePic" src="stepup?action=image&for=${user}"/>
                        </c:when>
                        <c:otherwise>
                            <img id="profilePic" src="images/default_icon_sm.png" align="left"/>
                        </c:otherwise>
                    </c:choose>                                                    
                    </td>    
                <tr><td></td>
                    <td>
                        <a href="<c:url value='stepup?action=upload'/>">Upload a new picture</a>                        
                    </td></tr>                
                <tr><td colspan="2"><input id="roundinput" 
                                           type="reset" value="Cancel"/>
                                           &nbsp;&nbsp;&nbsp;
                                           <input id="roundinput" 
                                              type="submit" 
                                              value="Save Changes"/></td></tr>
            </table>
          </form>
                        <p> <a href="<c:url value='stepup?action=home'/>">Home</a>
            <a href="<c:url value='stepup?action=dashboard">Group Dashboard'/></a> |
            <a href="<c:url value='stepup?action=profile&profilefor=${user}'/>">View My Profile</a> |            
            <a href="<c:url value='stepup?action=logout'/>">Logout</a>
        </p>
<%@ include file="footer.jspf"%>     
    </body>
</html>
