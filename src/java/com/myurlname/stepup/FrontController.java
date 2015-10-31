/**
 * The front controller for the StepUp application.  All web requests pass
 * through this servlet, and it passes control around to other jsp's and 
 * servlets through query strings and forwards.
*/
package com.myurlname.stepup;

import java.io.IOException;
import java.io.PrintWriter;
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
                
            case "home":
                nextPage = "home";
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
                return home(request);
            }
        } else {
            request.setAttribute("flash", 
                                 "Please follow username and password rules");
            return "login";
        }
    }    
    
    private String home (HttpServletRequest request) {
        //the only requests supported are GET requests at this time,
        //treat all other requests as GET for now..
        if (request.getSession().getAttribute("user") == null) return "login";
        

        //TODO - pull all the HOME info for the user and return it here
        
        return "home";
        
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
