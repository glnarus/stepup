/**
 * The front controller for the StepUp application.  All web requests pass
 * through this servlet, and it passes control around to other jsp's and
 * servlets through query strings and forwards.
*/
package com.myurlname.stepup;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String nextPage = null;
        if (action == null) {
            if (request.getAttribute("user") == null)
                action = "login";
            else
                action = "home";
        }
        switch (action) {
            case "login" :
                nextPage = login (request);
                break;

            case "register":
                nextPage = register (request);
                break;

            case "home":
                nextPage = home(request);
                break;

            case "logout" :
                nextPage = logout(request);
                break;

            default:
                if (request.getAttribute("user") != null)
                    nextPage = "home";
                else
                    nextPage = "login";
        }
        request.getRequestDispatcher(nextPage + ".jsp").forward(request,
                                                                response);
    }

    private String logout (HttpServletRequest request) {
        request.getSession().setAttribute("user", null);
        request.getSession().setAttribute("achievements", null);
        request.getSession().invalidate();
        return "login";
    }

    private String login(HttpServletRequest request) {
        //for GET requests, just return and allow JSP to display page
        if (request.getMethod().equals("GET")) return "login";
        //for POST requests, let's try and login now
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        LoginBean bean = new LoginBean(username, password);
        if (bean.validate()) {
            StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
            User user = db.authenticate(username, password);
            if (user == null) {
                String error = db.getLastError();
                request.setAttribute("flash",
                        (error == null? "No user/password combination found" :
                                                                      error));
                return "login";
            } else {
                request.getSession().setAttribute("user", user);
                //add the achievement log for this user to the session as well
                //since we are loading the home page next
                List<Achievement> achievements =
                                            db.getAchievementsByDate(username);
                request.getSession().setAttribute("achievements", achievements);
                return "home";
            }
        } else {
            request.setAttribute("flash",
                                 "Please follow username and password rules");
            return "login";
        }
    }

    private String register (HttpServletRequest request) {
        //for GET requests, just return and allow JSP to display
        //the register page
        if (request.getMethod().equals("GET")) return "register";
        //for POST requests, we need to validate the data, and if valid,
        //insert it.
        String username = request.getParameter("user");
        String password1 = request.getParameter("pass1");
        String password2 = request.getParameter("pass2");
        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String goal = request.getParameter("goal");
        String reward = request.getParameter("reward");
        String emailSubsribe = request.getParameter("emailsubscribe");
        String textSubscribe = request.getParameter("textsubscribe");
        RegistrationBean r = new RegistrationBean (username,
                                                   password1,
                                                   password2,
                                                   firstName,
                                                   lastName,
                                                   email,
                                                   phone,
                                                   goal,
                                                   reward,
                                                   emailSubsribe,
                                                   textSubscribe);
        Profile profile = new Profile (r);
        if (!profile.validateRegistration()) {
            request.setAttribute("flash", "Required information not entered correctly" + profile.getErrorMessage());
            request.setAttribute("bean", r);
            request.setAttribute("problems", profile.getErrorMessage());
            return "register";
        }
        //Data is valid, let's update/create the profile!
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        User user = db.register(profile);
        if (user == null) {
            request.setAttribute("flash",db.getLastError());
            request.setAttribute("bean", r);
            return "register";
        }
        //Everything registered fine, let's log in the user officially
        request.getSession().setAttribute("user", user);
        return "home";
    }

    private String home (HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        if (user == null) return "login";
        if (request.getMethod().equals("GET")) return "home";
        //assumes a POST with a logged in user (meaning an achievement logged)
        String activity = request.getParameter("activity");
        String intensity = request.getParameter("intensity");
        String minutes = request.getParameter("minutes");
        String notes = request.getParameter("notes");
        String dateActivity = request.getParameter("dateactivity");
        AchievementBean bean = new AchievementBean (activity, intensity,
                                                   minutes, notes, dateActivity);
        Achievement achievement = new Achievement (bean);
        if (!achievement.validate()) {
            request.setAttribute("flash", "Improper input for activity, try again");
            request.setAttribute("bean", bean);
            return "home";
        }
        achievement.setUser(user);
        //Everything is valid about this achievement, let's write it to the DB!
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        int achievementId = db.addAchievement (achievement);
        if (achievementId > -1) {
            achievement.setAchievementId(achievementId);
            List achievements = db.getAchievementsByDate(user.getUsername());
            if ((achievements != null) && (!achievements.isEmpty())) {
                Badge badge = BadgeCalculator.calculateBadge(achievements, new Date());
                int upRet = db.updateBadge(user.getUserId(), badge);
                if (upRet != -1)
                    user.setBadge(badge);
            }
            //Can be more efficient about the above by adding Achievement, in order
            //here instead of calling the dbase again.
            if (achievements == null)  {
                request.setAttribute ("flash",db.getLastError());
                request.setAttribute("bean", bean);
            }
            request.getSession().setAttribute("achievements", achievements);
            return "home";
        }
        else {
            request.setAttribute ("flash",db.getLastError());
            request.setAttribute("bean", bean);
            return "home";
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
