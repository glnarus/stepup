/**
 * The front controller for the StepUp application.  All web requests pass
 * through this servlet, and it passes control around to other jsp's and
 * servlets through query strings and forwards.
*/
package com.myurlname.stepup;

import java.io.IOException;
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
            else {
                if (request.getSession().getAttribute("user") != null)
                    action = "upload";
                else
                    action = "login";
            }
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

            case "squaddashboard":
                nextPage = dashboard(request);
                break;

            case "upload":
                nextPage = uploadImage(request);
                break;

            case "follow":
                nextPage = follow(request);
                break;

            case "unfollow":
                nextPage = unfollow(request);
                break;

            case "image":
                sendImage(request, response);
                break;

            case "mysquads":
                nextPage = mysquads(request);
                break;

            case "joinsquad":
                nextPage = removeOrJoinSquad(request, true);
                break;

            case "createsquad":
                nextPage = createSquad (request);
                break;
                
            case "removeinvite":
                nextPage = removeOrJoinSquad(request, false);
                break;
                
            case "invitemembers" :
                nextPage = inviteMember(request);
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
        request.getRequestDispatcher("pages/" + nextPage + ".jsp").forward(request,
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
                request.setAttribute("flash", "User and/or password combination not recognized");
                return "login";
            } else {
                request.getSession().setAttribute("user", user);
                //add the achievement log for this user to the session as well
                //since we are loading the home page next
                List<Achievement> achievements =
                                            db.getAchievementsByDate(username,-1);
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
            request.setAttribute("flash", profile.getErrorMessage());
            request.setAttribute("bean", r);
            return "register";
        }
        //Data is valid, let's update/create the profile!
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        user = db.register(profile);
        if (user == null) {
            String message = db.getLastError().contains("duplicate key")?"Username already exists, please pick another"
                                                                        :"Unable to save new user, please check data and retry";
            flashDbError (request, db,message);       
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
        int squadId = -1;
        try {squadId = Integer.parseInt(request.getParameter("squadid"));
        } catch (Exception e) {}
        if (user == null)
            return "login";
        if (squadId == -1)
            return "home";
        
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        
        
        //get all the achievements for the activity board for everyone in the squad...
        List <Achievement> achievements = db.getAllAchievementsByDate(squadId);
        if (achievements == null) {
            flashDbError (request, db,"Unable to read all dashboard achievments, please retry");       
            return "dashboard";
        }
        request.getSession().setAttribute("achievementsAll", achievements);
        
        //now, get all the posts for the bulletin board made by the squad members...
        List <Post> posts = db.getSortedPostsByDate(squadId);
        if (posts == null) {
            flashDbError (request, db,"Unable to read all dashboard posts, please retry");    
            return "dashboard";
        }
        request.getSession().setAttribute("posts", posts);
        request.getSession().setAttribute("currentSquadId", new Integer(squadId)); //need to save
        //the squad ID this dashboard is showing at session level for future dashboard requests
        //everything went ok ready for JSP to display
        return "dashboard";
    }        

    private String submitPost (HttpServletRequest request) {
        //Currently, only GETs supported.
        User user = (User)request.getSession().getAttribute("user");
        int squadId = -1;
        try {squadId = (Integer) request.getSession().getAttribute("currentSquadId");
        } catch (Exception e) {}        
        if (user == null)
            return "login";
        if (squadId == -1) //this is an unexpected condition, so send them back to the dashboard
            return "dashboard";

        
        //NEED function here to ensure user is member of squad before posting
                
        
        
        //build the Post object
        Post post = new Post (request.getParameter("content"),
                              user.getUsername(), user.getUserId(), squadId);

        if (!post.isPostValid()) {
            request.setAttribute("flash", "Posts must be between 0 and " +
                                        Post.POST_MAX_LENGTH + " characters");
            return "dashboard";
        }
        //Post checks out ok and Post object translated all HTML/SQL control
        //characters, so let's update the db
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        
        //one final check here is to make sure the user belongs to the squad.  This should be
        //enforced through proper web app navigation, but we want to be robust here from a security
        //standpoint.
        if (!db.isUserInSquad(user.getUserId(), squadId)) {
                request.getSession().invalidate();
                return "login";
        }
        
        int postId = db.createPost(post);
        if (postId < 0) {
            flashDbError (request, db,"Unable to confirm post is saved, please retry");    
            return "dashboard";
        }
        //send email out if people are following this post
        sendOutUpdatesToFollowers(db, post);
        //pull all posts again (there may have been more posts added since we
        //have been working on this one)
        List <Post> posts = db.getSortedPostsByDate(squadId);

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
            flashDbError (request, db,"Problem looking up username for profile access, please retry");    
            return "profile";
        }
        Profile profile = db.getProfileFor(subject);
        if (profile == null) {
            flashDbError (request, db,"Problem looking up profile, please retry");    
            return "profile";
        }

        List achievements = db.getAchievementsByDate(subject.getUsername(), -1);
        if (achievements == null)  {
            flashDbError (request, db,"Problem looking up profile, please retry");    
            return "profile";
        }
        request.getSession().setAttribute("subjectachievements", achievements);
        if (user.getUserId() == subject.getUserId())
            user.setProfile(profile);
        else {
            //determine if this User is following the subject
            Boolean isFollowing = db.checkForFollowing(user.getUserId(), subject.getUserId());
            if (isFollowing == null) {
                flashDbError (request, db,"Problem looking up profile, please retry");    
                return "profile";
            }
            else if (isFollowing)
                request.getSession().setAttribute("following", "true");
            else
                request.getSession().setAttribute("following", null);

        }
        request.getSession().setAttribute("profile", profile);
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
            //we can pass this directly to the JSP because the only way to get
            //the edit profile is to first load profile, which attaches the profile attribute.
            if (request.getSession().getAttribute("profile") == null) {
                //This is an odd case where a user bookmarks the edit profile URL without going through My Profile first in the session
                //This is a corner case that may not be possible, but load their profile info for them here in this case to handle gracefully
                Profile p = db.getProfileFor(user);
                if (p == null)
                    flashDbError (request, db,"Problem loading profile values, please try logging out and logging back in"); 
                else
                    request.setAttribute("profile", p);
            }
            return "editprofile";
        }
        //Process the POST        
        String firstName = request.getParameter("fname");
        String lastName = request.getParameter("lname");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String goal = request.getParameter("goal");
        String reward = request.getParameter("reward");
        String emailSubsribe = request.getParameter("emailsubscribe");
        String textSubscribe = request.getParameter("textsubscribe");
        //we need to make sure everything is valid, so let's reuse the registration bean        
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
            request.setAttribute("flash", profile.getErrorMessage());
            request.setAttribute("bean", r);
            return "editprofile";
        }
        //data is ready to be sent to the database.  do NOT update the username
        //or password of course.
        User userEdited = db.updateProfile(user, profile);
        if (userEdited == null) {
            flashDbError (request, db,"Problem updating profile, please retry");    
            request.setAttribute("bean", r);
            return "editprofile";
        }
        request.getSession().setAttribute("profile", userEdited.getProfile());
        request.getSession().setAttribute("user", userEdited);
        request.getSession().setAttribute("subject", userEdited);
        request.setAttribute("bean", r);
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
                    flashDbError (request, db,"Problem removing achievement, please check and retry if necessary");    
                    return "editAchievement";
                }
                request.setAttribute("flash", "Achievement deleted successfully");
                //re-populate achievement list
                //Can be more efficient about the above by adding Achievement, in order
                //here instead of calling the dbase again.
                List achievements = db.getAchievementsByDate(user.getUsername(),-1);
                if (achievements == null)  {
                    flashDbError (request, db,"Problem looking up achievements, please retry");    
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
                flashDbError (request, db,"Problem updating achievement, please check and retry if necessary");    
                request.setAttribute("bean", bean);
                return "editAchievement";
            }
            //Can be more efficient about the above by adding Achievement, in order
            //here instead of calling the dbase again.
            List achievements = db.getAchievementsByDate(user.getUsername(),-1);
            if (achievements == null)  {
                flashDbError (request, db,"Problem looking up achievements, please retry");    
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
            List achievements = db.getAchievementsByDate(user.getUsername(),-1);
            if ((achievements != null) && (!achievements.isEmpty())) {
                Badge badge = BadgeCalculator.calculateBadge(achievements, new Date());
                int upRet = db.updateBadge(user.getUserId(), badge);
                if (upRet != -1)
                    user.setBadge(badge);
            }
            //Can be more efficient about the above by adding Achievement, in order
            //here for the six week instead of calling the dbase again.
            if (achievements == null)  {
                flashDbError (request, db,"Problem looking up achievements, please retry");    
                request.setAttribute("bean", bean);
            }
            request.getSession().setAttribute("achievements", achievements);
            List <Integer> SixWeeksScores = BadgeCalculator.getSixWeeksHistory(
                                                            achievements, null);
            Collections.reverse(SixWeeksScores);
            request.getSession().setAttribute("sixweeksscores",SixWeeksScores);
            sendOutUpdatesToFollowers(db,achievement);
            attachCurrentDate(request);
            return "home";
        }
        else {
            flashDbError (request, db,"Problem adding achievement, please check and retry if necessary");    
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
    Part filePart = null;
    ProfilePicDataAndType pic = null;
    try {
        filePart = request.getPart("pic");
        String filetype = filePart.getContentType();
        if (!filetype.contains("image")) {
            request.setAttribute("flash", "The uploaded file is not an image.");
            try {filePart.delete();} catch (Exception ignored) {}            
            return "uploadPic";
        }                
        int maxSize = (int)request.getServletContext().getAttribute("profilePicSize");
        int maxPixelsOnEdge = (int)request.getServletContext().getAttribute("profilePicMaxPixelCount");        
        pic = ProfilePictureFactory.getEnforcedSizePicture(filePart, 
                                                                        maxSize, 
                                                                        maxPixelsOnEdge);
        User user = (User)request.getSession().getAttribute("user");
        StepUpDAO db = (StepUpDAO)getServletContext().getAttribute("db");
        db.updateImage(user.getUserId(), pic.getContentType(), pic.getData());
        if (db.getLastError() != null) {
            flashDbError (request, db,"Problem updating profile image, please retry");     
            pic.close();
            try {filePart.delete();} catch (Exception ignored) {}            
            return "uploadPic";
        }
        Profile p = db.getProfileFor(user);
        if (db.getLastError() != null) {
            flashDbError (request, db,"Problem refreshing profile with new image, please retry"); 
            pic.close();
            try {filePart.delete();} catch (Exception ignored) {}            
            return "uploadPic";
        }
        user.setProfile(p);  
        request.getSession().setAttribute("profile", p);
    } catch (IOException | ServletException e) {
        request.setAttribute("flash", e.getMessage());
        if (pic != null) pic.close();
        try {filePart.delete();} catch (Exception ignored) {}
        return "uploadPic";    
    }    
    pic.close();
    try {filePart.delete();} catch (Exception e) {}
    return "profile";
    }
    
    private String mysquads (HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";        
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        List<SquadMembership> squadMemberships = db.getSquadMemberships(user.getUserId());
        if (squadMemberships == null) {
            flashDbError (request, db,"Problem looking up username for list of squad invitations, please retry");    
            return "mysquads";
        }
        //create a list of invitations (memberships where isInvited is true for this user),
        //a list of squads that the user owns, and a list of all squads the user is a member or owner
        //of
        List <SquadMembership> invites = new ArrayList<>();        
        List <SquadMembership> ownedSquads = new ArrayList<>();     
        List <SquadMembership> joinedOrOwnedSquads = new ArrayList<>();             
        for (SquadMembership m : squadMemberships) {
            if (m.getIsInvited())
                invites.add(m);
            else {
                joinedOrOwnedSquads.add(m);
                if (m.getIsOwner())
                    ownedSquads.add(m);
            }            
        }            
        request.getSession().setAttribute("myInvitations", invites);        
        request.getSession().setAttribute("joinedOrOwnedSquads", joinedOrOwnedSquads);        
        request.getSession().setAttribute("ownedSquads", ownedSquads);        
        return "mysquads";
    }    
    
    private String removeOrJoinSquad (HttpServletRequest request, boolean isJoin) {
        //Method will verify user is in session and is invited to squad
        //After join/remove complete, method will call mysquad method to display the mysquads page with updated results

        //isJoin = false:
        //Method will remove the session's user's invitation to the squad from the id parameter attached to the http request

        //isJoin = true: 
        //Method will add the session's user to the squad from the id parameter attached to the http request
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";        
        //get and verify the squad ID paramter that this user wants to join
        int squadId;
        try {squadId = Integer.parseInt(request.getParameter("id"));}
        catch (NumberFormatException nfe) {return "home";}
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        if (isJoin) {
            if (!db.joinSquad(user.getUserId(), squadId)) {
                flashDbError (request, db,"Problem adding user to squad, please retry");    
                return "mysquads";
            }
        }
        else {
            if (!db.removeUserFromSquad(user.getUserId(), squadId)) {
                flashDbError (request, db,"Problem removing user from squad, please retry");    
                return "mysquads";
            }                        
        }
        return mysquads (request);
    }        
         
    private String inviteMember (HttpServletRequest request) {
       
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";      
        if (request.getMethod().equals("GET")) {            
            int squadId;            
            try {squadId = Integer.parseInt(request.getParameter("squadid"));}
            catch (NumberFormatException nfe) {return "mysquads";}
            request.getSession().setAttribute("squadname", request.getParameter("squadname"));
            request.getSession().setAttribute("squadid", squadId);
            StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
            updateInvitedMemberList (db, request, squadId);
        }
        else if (request.getMethod().equals("POST")) {
            //this is a POST, check if the username requesting an invite exists, if so,
            //invite the user to the squad (DAO), update the 'invitationsSent' list
            int squadId = 0;
            String squadName = "";
            if (request.getSession().getAttribute("squadid") == null ||
                request.getSession().getAttribute("squadname")== null) //unexpected condition, go back to home
                return "home";
            else {
                squadId = (int)request.getSession().getAttribute("squadid");
                squadName = (String)request.getSession().getAttribute("squadname");
            }    
            String invitedUsername = request.getParameter("invitedusername");
            InvitedUser iu = new InvitedUser(invitedUsername, squadId);
            if (!iu.isInvitedUsernameValid()) {
                request.setAttribute("flash", "Improperly formatting username, please try again");
                return "invitemember";
            }
            StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
            if (db.inviteUser(iu) == -1) {
                request.setAttribute("flash","Unrecognized user to invite");
                flashDbError (request, db,"Problem inviting user, please check username and retry");  
                return "invitemember";
            }
            //Now update the list of invited members for the JSP to load
            updateInvitedMemberList (db, request, squadId);    
        }
        
        return "invitemember";
    }     
   
    private String createSquad (HttpServletRequest request) {
//       Just have a short JSP that users can either POST in their new squad name, 
//       and once created will automatically go to the invite others page
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";    
        if (request.getMethod().equals("GET")) return "createsquad";
        if (request.getMethod().equals("POST")) {
            //this is a POST, so the user wants to create a new squad.  Let's check if the 
            //proposed squad name is valid and unique, and if so, create it!          
            String newSquadName = (String)request.getParameter("newsquadname");
               
            NewSquadName nsn = new NewSquadName (newSquadName);
            if (!nsn.validate()) {
                request.setAttribute("flash", "Squad names must be: 5-50 characters composed of letters, numbers, and single spaces only");
                return "createsquad";
            }
            StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
            if (!db.createSquad(user.getUserId(), nsn.getNewSquadName())) {
                request.setAttribute("flash","Could not create new squad");
                flashDbError (request, db,"Problem creating new squad");  
                return "createsquad";                
            }                
            //attach squad ID to session scope
            int newSquadId = db.getSquadIdBySquadName(newSquadName);
                
            //Now update the session attributes to prepare for the inviteMember page
            request.getSession().setAttribute("squadname", newSquadName);
            request.getSession().setAttribute("squadid", newSquadId);
            updateInvitedMemberList (db, request, newSquadId);
        }
        
        return "invitemember";
    }     
    
    private boolean updateInvitedMemberList (StepUpDAO db, HttpServletRequest request, int squadId) {
        List<SquadMembership> squadMemberships = db.getAllSquadMembers(squadId);
        if (squadMemberships == null) {
            flashDbError (request, db,"Problem looking up username for list of squad members, please retry");    
            return false;
        }
        //create a list of invitations sent out for this squad (memberships where isInvited is true for this squad)        
        List <SquadMembership> invitationsSent = new ArrayList<>();
        for (SquadMembership m : squadMemberships) {
            if (m.getIsInvited())
                invitationsSent.add(m);
        }            
        request.getSession().setAttribute("invitationsSent", invitationsSent);  
        return true;
    }
    
    private String follow (HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";
        int toFollowId = 0;
        String toFollowIdString = request.getParameter("id");
        try {toFollowId = Integer.parseInt(toFollowIdString);}
        catch (NumberFormatException nfe) {
            request.setAttribute("flash","Unrecognized user to follow");
            return "profile";
        }
        //data inputs look good, so let's establish the follower relationship
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        int result = db.startFollowing(user.getUserId(), toFollowId);
        if (result < 0) {
            flashDbError (request, db,"Problem following user, please retry"); 
            return "profile";
        }
        request.getSession().setAttribute("following", "true");
        return "profile";
    }

    private String unfollow (HttpServletRequest request) {
        User user = (User)request.getSession().getAttribute("user");
        if (user == null)
            return "login";
        int toFollowId = 0;
        String toFollowIdString = request.getParameter("id");
        try {toFollowId = Integer.parseInt(toFollowIdString);}
        catch (NumberFormatException nfe) {
            request.setAttribute("flash","Unrecognized user to unfollow");
            return "profile";
        }
        //data inputs look good, so let's destroy the follower relationship
        StepUpDAO db = (StepUpDAO) getServletContext().getAttribute("db");
        int result = db.stopFollowing(user.getUserId(), toFollowId);
        if (result < 0) {
            flashDbError (request, db,"Problem unfollowing user, please retry"); 
            return "profile";
        }
        request.getSession().setAttribute("following", null);
        return "profile";
    }

    //userId is the user that this achievement
    private void sendOutUpdatesToFollowers(StepUpDAO db, Achievement ach) {
        List<String> emails = db.getFollowerEmails(ach.getUser().getUserId());
        if ((emails == null) || (emails.isEmpty())) return;
        for (String email : emails) {
            String contents = String.format(
                    "StepUp&trade; user %s recorded a new activity!<br><br>%s<br><br>"
                            + "<italic>Thank you from StepUp&trade;</italic><br><br><br><small>"
                            + "To stop these "
                            + "emails, <a href=\"http://localhost:8080/StepUp/\">login</a>"
                            +" and visit the profile page of those"
                            + " users you want to stop following and click the"
                            + " unfollow link.</small>",
                    ach.getUser().getUsername(),ach);
            String subject = "New activity for: " + ach.getUser().getUsername();
            SendEmail.generateAndSendEmail(email,subject, contents);

        }


    }

    //userId is the user that this achievement
    private void sendOutUpdatesToFollowers(StepUpDAO db, Post post) {
        List<String> emails = db.getFollowerEmails(post.getUserId());
        if ((emails == null) || (emails.isEmpty())) return;
        for (String email : emails) {
            String contents = String.format(
                    "StepUp&trade; user %s made a new post!<br><br>%s<br><br>"
                            + "<italic>Thank you from StepUp&trade;</italic><br><br><br><small>"
                            + "To stop these "
                            + "emails, <a href=\"http://localhost:8080/StepUp/\">login</a>"
                            +" and visit the profile page of those"
                            + " users you want to stop following and click the"
                            + " unfollow link.</small>",
                    post.getUsername(),post);
            String subject = "New post from : " + post.getUsername();
            SendEmail.generateAndSendEmail(email,subject, contents);

        }


    }
    
    private static void flashDbError (HttpServletRequest request, StepUpDAO db, String prefix) {
        if (db.getLastError()!= null) {
            request.setAttribute("flash",prefix);
            request.setAttribute("flashsmall",db.getLastError());        
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
