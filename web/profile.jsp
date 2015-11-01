<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp -- Profile for ${subject}</title>
        <link rel="stylesheet" type="text/css" href="styles/main.css"/>
    </head>
    <body>
        <img src="images/hubbub.png"/><br/>
        <h1>Hubbub Profile for <a href="main?action=wall&for=${subject}">${subject}</a></h1>
        <h2 class="flash">${flash}</h2>
        <p><a href="main">Back to the Timeline</a> |
            <a href="main?action=wall&for=${subject}">Back to ${subject}'s Wall</a> |
            <a href="main?action=post">Post Something</a> |
            <a href="main?action=logout">Log me out</a>
        <p>
        <div class="postdiv">
            <c:choose>
                <c:when test="${profile.imageType ne null}">
                    <img src="main?action=image&for=${subject}"/>
                </c:when>
                <c:otherwise>
                    <img src="images/domo.jpg"/>
                </c:otherwise>
            </c:choose>
        <c:choose>
            <c:when test="${profile.biography ne null}">
                ${profile.biography}             
            </c:when>
            <c:otherwise>
                <strong>${subject}</strong> is a very private person with no biography on file.
            </c:otherwise>
        </c:choose>
        </div>
        </p>
        <h3 class="footer">
            Copyright 2015 www.austincc.edu and www.bytecaffeine.com
        </h3>
    </body>
</html>
