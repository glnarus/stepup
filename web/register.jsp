<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>StepUp&trade; - Register</title>
        <link rel="stylesheet" type="text/css" href="StepUp.css"/>        
    </head>
    <body>  
        <img id="imagelogo" src="images/logo_sm.jpg" align="left"/>        
        <h1>Register for StepUp&trade;</h1>
        <h2 class="flash">${flash}</h2>
        <form method="POST" action="stepup">
            <input type="hidden" name="action" value="register"/>
            <table id="formtable">
                <tr><td>Username:</td><td><input value="${bean.username}" type="text" name="user" placeholder="3 to 15 characters."/></td></tr>
                <tr><td>Password:</td><td><input type="password" name="pass1" placeholder="6 to 15 characters."></td></tr>
                <tr><td>Confirm password:</td><td><input type="password" name="pass2" placeholder="type again to confirm"/></td></tr>
                <tr><td>First Name:</td><td><input value="${bean.firstName}" type="text" name="fname"/></td></tr>
                <tr><td>Last Name:</td><td><input value="${bean.lastName}" type="text" name="lname"/></td></tr>
                <tr><td>Email (optional):</td><td><input value="${bean.email}" type="email" name="email"/></td></tr>
                <tr><td>Phone (optional):</td><td><input value="${bean.phone}" type="text" name="phone" placeholder="###-###-####"/></td></tr>
                <tr><td>Goal (optional):</td><td><input value="${bean.goal}" type="text" name="goal" placeholder="Running distance or time?  Weight loss? What is your goal?"/></td></tr>
                <tr><td>Reward (optional):</td><td><input value="${bean.reward}" type="text" name="reward" placeholder="How would you reward yourself when you meet your goal?"/></td></tr>
                <tr><td colspan="2"><input type="submit" value="Create Account!"/></td></tr>
            </table>
        </form>
        <p><a href="stepup?action=login">Login existing user</a>
        </p>     
        <div class="footer">
        Copyright &copy;2015 Gabriel Narus ACC Capstone Project                    
        </div>        
    </body>
</html>
