<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Profile Picture</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>Upload a Profile Picture for ${user}'s StepUp&trade; Account</h1>
        <%@ include file="flash.jspf"%>  

        <form method="POST" action="<c:url value='stepup'/>" enctype="multipart/form-data">
            <input type="hidden" name="action" value="upload"/>
            <table id="formtable">
                <tr><td>Current Pic:</td><td><label for="pic">Select a new picture (20MB max size):</label></td></tr>
                <tr><td rowspan="2">
                        <c:choose>
                            <c:when test="${user.profile.imageType ne null}">
                                <img id="profilePic" src="stepup?action=image&for=${user.username}"/>
                            </c:when>
                            <c:otherwise>
                                <img id="profilePic" src="images/default_icon_sm.png"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><input type="file" name="pic" id="pic"/></td>
                </tr>
                <tr><td><input type="submit" value="Upload Picture"/></td></tr>
            </table>
        </form>
        <p> <a href="<c:url value='stepup?action=home'/>">Home</a>
            <a href="<c:url value='stepup?action=dashboard'/>">Group Dashboard</a> |
            <a href="<c:url value='stepup?action=editprofile'/>">Edit My Profile</a> |            
            <a href="<c:url value='stepup?action=logout'/>">Logout</a>
        </p>
        
<%@ include file="footer.jspf"%>     
    </body>
</html>