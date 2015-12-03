/**
 * The front controller for the StepUp application.  All web requests pass
 * through this servlet, and it passes control around to other jsp's and
 * servlets through query strings and forwards.
*/
package com.myurlname.stepup;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class FrontController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String nextPage = null;
        if (action == null) {
            if (request.getMethod().equals("GET")) {
                if (request.getSession().getAttribute("user") == null)
                    action = "login";
                else
                    action = "home";
            }
            else
                action = "upload";
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

            case "editachievement":
                nextPage = editAchievement(request);
                break;

            case "profile":
                nextPage = profile(request);
                break;

            case "editprofile":
                nextPage = editProfile (request);
                break;

            case "dashboard":
                nextPage = dashboard(request);
                break;

            case "upload":
                nextPage = uploadImage(request);
                break;

            case "image":
                sendImage(request, response);
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
                List <Integer> SixWeeksScores = BadgeCalculator.getSixWeeksHistory(
                                                                achievements, null);
                Collections.reverse(SixWeeksScores);
                request.getSession().setAttribute("sixweeksscores",SixWeeksScores);
                //attach the current date as default
                attachCurrentDate(request);
                return "home";
            }
        } else {
            request.setAttribute("flash",
                                 "Please follow username and password rules");
            return "login";
        }
    }

    private void attachCurrentDate (HttpServletRequest request) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        request.getSession().setAttribute("todaysdate", sdf.format(new Date()));
    }

    private String register (HttpServletRequest request) {
        //for GET requests, just return and allow JSP to display
        //the register page
        if (request.getMethod().equals("GET")) return "register";
        //for POST requests, we need to validate the data, and if valid,
        //insert it.
        User user = (User)request.getSession().getAttribute("user");
        if (user != null) //if already logged in, kill the session first
            request.getSession().invalidate();
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
        user = db.register(profile);
        if (user == null) {
            request.setAttribute("flash",db.getLastError());
            request.setAttribute("bean", r);
            return "register";
        }
        //Everything registered fine, let's log in the user officially
        request.getSession().setAttribute("user", user);
        attachCurrentDate(request);
        return "home";
    }

    private String dashboard (HttpServletRequest request) {
        if (request.getMethod().equals("POST")) {
            return submitPost (request);
        }
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";

        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        //get all the achievements for the activity board...
        List <Achievement> achievements = db.getAllAchievementsByDate();
        if (achievements == null) {
            request.setAttribute("flash",db.getLastError());
            return "dashboard";
        }
        request.getSession().setAttribute("achievementsAll", achievements);
        //get all the posts for the bulletin board...
        List <Post> posts = db.getSortedPostsByDate();
        if (posts == null) {
            request.setAttribute("flash", db.getLastError());
            return "dashboard";
        }
        request.getSession().setAttribute("posts", posts);
        //everything went ok ready for JSP to display
        return "dashboard";
    }

    private String submitPost (HttpServletRequest request) {
        //Currently, only GETs supported.
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";
        if (request.getSession().getAttribute("achievements") == null) {
            //a POST without previously viewing the dashboard is weird, if a user
            //does this weird thing somehow, send them home
            return "home";
        }
        //build the Post object
        Post post = new Post (request.getParameter("content"),
                              user.getUsername(), user.getUserId());

        if (!post.isPostValid()) {
            request.setAttribute("flash", "Posts must be between 0 and " +
                                        Post.POST_MAX_LENGTH + " characters");
            return "dashboard";
        }
        //Post checks out ok and Post object translated all HTML/SQL control
        //characters, so let's update the db
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        int postId = db.createPost(post);
        if (postId < 0) {
            request.setAttribute("flash",db.getLastError());
            return "dashboard";
        }
        //pull all posts again (there may have been more posts added since we
        //have been working on this one)
        List <Post> posts = db.getSortedPostsByDate();

        request.getSession().setAttribute("posts", posts);
        //everything went ok ready for JSP to display
        return "dashboard";
    }

    private String profile (HttpServletRequest request) {
        //for GET requests, make sure the user is logged in and
        //the profile object is created and attached to the user object
        //then return and allow profile.jsp to display the data
        //We do this regardless of GET or POST (editing profile uses a differnt
        //action word)
        User user = (User)request.getSession().getAttribute("user");
        String profileFor = request.getParameter("profilefor");
        if (user == null)
            return "login";
        else if (profileFor == null)
            return "home";

        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        User subject = db.getUserByUserName(profileFor);
        if (subject == null) {
            request.setAttribute("flash",db.getLastError());
            return "profile";
        }
        Profile profile = db.getProfileFor(subject);
        if (profile == null) {
            request.setAttribute("flash",db.getLastError());
            return "profile";
        }

        List achievements = db.getAchievementsByDate(subject.getUsername());
        if (achievements == null)  {
            request.setAttribute ("flash",db.getLastError());
            return "profile";
        }
        request.getSession().setAttribute("subjectachievements", achievements);
        if (user.getUserId() == subject.getUserId())
            user.setProfile(profile);
        request.getSession().setAttribute("profile", profile);
        //change line below to use "for" attribute's user object
        //add a reduced Profile object to the profilefor user and have
        //the .jsp use if/tests on how to write the page (profilefor vs user mismatch is the condition)
        request.getSession().setAttribute("subject", subject);

        //everything went ok, and now subject has the profile data,
        //ready for JSP to display
        return "profile";
    }

    private String editProfile (HttpServletRequest request) {
        //this method handles both GETs and POSTS.  GETs for
        //loading the editprofile.jsp page and POSTs for actually
        //modifying the user's profile.
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        if (request.getMethod().equals("GET")) {
            Profile profile = db.getProfileFor(user);
            if (profile == null) {
                request.setAttribute("flash",db.getLastError());
                return "editprofile";
            }
            RegistrationBean r = new RegistrationBean ("not_editable",
                                                   "not_editable",
                                                   "not_editable",
                                                   profile.getFirstName(),
                                                   profile.getLastName(),
                                                   profile.getEmail(),
                                                   profile.getPhone(),
                                                   profile.getGoal(),
                                                   profile.getReward(),
                                                   profile.getEmailSubscribe(),
                                                   profile.getTextSubscribe());
            request.setAttribute("bean", r);
            user.setProfile(profile);
            return "editprofile";
        }
        //Process the POST; we need to make sure everything is valid, so
        //let's reuse the registration bean
        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String goal = request.getParameter("goal");
        String reward = request.getParameter("reward");
        String emailSubsribe = request.getParameter("emailsubscribe");
        String textSubscribe = request.getParameter("textsubscribe");
        RegistrationBean r = new RegistrationBean ("not_editable",
                                                   "not_editable",
                                                   "not_editable",
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
            return "editprofile";
        }
        //data is ready to be sent to the database.  do NOT update the username
        //or password of course.
        User userEdited = db.updateProfile(user, profile);
        if (userEdited == null) {
            request.setAttribute("flash", db.getLastError());
            request.setAttribute("bean", r);
            return "editprofile";
        }
        request.getSession().setAttribute("profile", userEdited.getProfile());
        request.getSession().setAttribute("user", userEdited);
        request.getSession().setAttribute("subject", userEdited);
        return "profile";
    }

    private String editAchievement (HttpServletRequest request) {
        //If the achievement exists and the user is logged in, create an
        //achievement bean and attach it to the session.
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";
        int achievementId;
        try {achievementId = Integer.parseInt(request.getParameter("id"));}
        catch (NumberFormatException nfe) {return "home";}
        //Now see if we can pull this achievement from the session's achievements
        //list
        if (request.getMethod().equals("GET")) {
            Achievement achToEdit = (Achievement)request.getSession().
                                                    getAttribute("achToEdit");
            if ((achToEdit == null) ||
                              (achToEdit.getAchievementId() != achievementId)){
                List <Achievement> achList = (List)request.getSession().
                                                       getAttribute("achievements");
                if (achList == null) return "home";
                for (Achievement ach: achList){
                    if (ach.getAchievementId() == achievementId) {
                        achToEdit = ach;
                        request.getSession().setAttribute("achToEdit", ach);
                        break;
                    }
                }
                if (achToEdit == null)
                    return "home";
            }
            if (request.getParameter("delete") != null) {
                //remove the achievement
                StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
                if (db.removeAchievement(achToEdit) == null) {
                    request.setAttribute("flash", db.getLastError());
                    return "editAchievement";
                }
                request.setAttribute("flash", "Achievement deleted successfully");
                //re-populate achievement list
                //Can be more efficient about the above by adding Achievement, in order
                //here instead of calling the dbase again.
                List achievements = db.getAchievementsByDate(user.getUsername());
                if (achievements == null)  {
                    request.setAttribute ("flash",db.getLastError());
                    return "home";
                }
                request.getSession().setAttribute("achievements", achievements);
                List <Integer> SixWeeksScores = BadgeCalculator.getSixWeeksHistory(
                                                                achievements, null);
                Collections.reverse(SixWeeksScores);
                request.getSession().setAttribute("sixweeksscores",SixWeeksScores);
                attachCurrentDate(request);
                return "home";
            }
            AchievementBean bean = new AchievementBean(
                   achToEdit.getActivity().toString(),
                   achToEdit.getIntensity().toString(),
                    String.valueOf(achToEdit.getMinutes()),
                   achToEdit.getNotes(),achToEdit.getPrettyPrintActivityDate());
            request.setAttribute("bean", bean);
            return "editAchievement";
        }
        else if (request.getMethod().equals("POST")) {
            //this is a POST, make a new Achievement, keeping same ID, but
            //overwriting other fields.
            Achievement achToEdit = (Achievement)request.getSession().
                                                    getAttribute("achToEdit");
            if (achToEdit == null)
                return "home";

            StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
            //Process the POST; we need to make sure everything is valid, so
            //re-use the achievementbean
            String activity = request.getParameter("activity");
            String intensity = request.getParameter("intensity");
            String minutes = request.getParameter("minutes");
            String notes = request.getParameter("notes");
            String dateActivity = request.getParameter("dateactivity");
            AchievementBean bean = new AchievementBean (activity, intensity,
                minutes, notes, dateActivity, achToEdit.getPrettyPrintRecordedDate());

            Achievement modAch = new Achievement (bean);
            //above line will also set an updated score
            modAch.setAchievementId(achToEdit.getAchievementId());
            if (!modAch.validate()) {
                request.setAttribute("flash", "Improper input for achievement, try again");
                request.setAttribute("bean", bean);
                return "editAchievement";
            }

            //data is ready to be sent to the database.  Do NOT update the
            //Achievement ID or date recorded
            Achievement updatedAch = db.updateAchievement(modAch);
            if (updatedAch == null) {
                request.setAttribute("flash", db.getLastError());
                request.setAttribute("bean", bean);
                return "editAchievement";
            }
            //Can be more efficient about the above by adding Achievement, in order
            //here instead of calling the dbase again.
            List achievements = db.getAchievementsByDate(user.getUsername());
            if (achievements == null)  {
                request.setAttribute ("flash",db.getLastError());
                request.getSession().removeAttribute("achievements");
                return "home";
            }
            request.getSession().setAttribute("achievements", achievements);
            List <Integer> SixWeeksScores = BadgeCalculator.getSixWeeksHistory(
                                                            achievements, null);
            Collections.reverse(SixWeeksScores);
            request.getSession().setAttribute("sixweeksscores",SixWeeksScores);
            attachCurrentDate(request);
            return "home";
        }
        return "home";
    }

    private String home (HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        if (user == null) return "login";
        if (request.getMethod().equals("GET")) {
            //attach the default date if it is needed
            attachCurrentDate(request);
            return "home";
        }
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
            List <Integer> SixWeeksScores = BadgeCalculator.getSixWeeksHistory(
                                                            achievements, null);
            Collections.reverse(SixWeeksScores);
            request.getSession().setAttribute("sixweeksscores",SixWeeksScores);
            attachCurrentDate(request);
            return "home";
        }
        else {
            request.setAttribute ("flash",db.getLastError());
            request.setAttribute("bean", bean);
            return "home";
        }

    }

    private void sendImage(HttpServletRequest request, HttpServletResponse response) {
        String subject = request.getParameter("for");
        StepUpDAO db = (StepUpDAO)getServletContext().getAttribute("db");
        User imageUser = db.getUserByUserName(subject);
        if (imageUser == null) {
            request.setAttribute("flash","Problem reading the profile picture");
            return;
        }
        Profile profile = db.getProfileFor(imageUser);
        String pictype = profile.getImageType();
        byte[] picdata = profile.getImageData();
        if (picdata == null || pictype == null) {
            response.setStatus(404);
            return;
        }
        try {
            response.setContentType(pictype);
            response.getOutputStream().write(picdata);
        } catch (IOException ioe) {
            request.setAttribute("flash", ioe.getMessage());
        }
    }

    private String uploadImage(HttpServletRequest request) {
    if (request.getMethod().equals("GET")) return "uploadPic";
    try {
        final Part filePart = request.getPart("pic");
        String filename = filePart.getSubmittedFileName();
        String filetype = filePart.getContentType();
        if (!filetype.contains("image")) {
            request.setAttribute("flash", "The uploaded file is not an image.");
            return "uploadPic";
        }
        InputStream data = filePart.getInputStream();
        User user = (User)request.getSession().getAttribute("user");
        StepUpDAO db = (StepUpDAO)getServletContext().getAttribute("db");
        db.updateImage(user.getUserId(), filetype, data);
        if (db.getLastError() != null) {
            request.setAttribute("flash", db.getLastError());
            return "uploadPic";
        }
        Profile p = db.getProfileFor(user);
        if (db.getLastError() != null) {
            request.setAttribute("flash", db.getLastError());
            return "uploadPic";
        }
        user.setProfile(p);
    } catch (IOException | ServletException e) {
        request.setAttribute("flash", e.getMessage());
    }
    return "uploadPic";
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
