/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.servlets;

import uk.ac.dundee.computing.aec.libs.*;


import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author andycobley
 */
@WebServlet(name = "Test", urlPatterns = {"/Test"})
public class Test extends HttpServlet {
     Cluster cluster=null;
     Session session=null;
     PreparedStatement SelectStatement;
     public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
        //cluster = Cluster.builder().addContactPoint("192.168.2.10").build(); //vagrant cassandra cluster
        try {
            cluster = Cluster.builder().addContactPoint("192.168.2.10").build(); //vagrant cassandra cluster
            session = cluster.connect();
        } catch (NoHostAvailableException et) {
            try {
                cluster = Cluster.builder().addContactPoint("127.0.0.1").build(); //localhost
                session = cluster.connect();
            } catch (NoHostAvailableException et1) {
                //can't get to a cassandra cluster bug out
                return;

            }
        }
        Keyspaces kp = new Keyspaces();
        kp.SetUpKeySpaces(cluster);
        SelectStatement=session.prepare("select * from mzMLKeyspace.mzMLTemp where name= ? ;");
 
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ResultSet rs = null;
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Test</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Test at " + request.getContextPath() + "</h1>");
            BoundStatement boundStatement = new BoundStatement(SelectStatement);
            String xmlFile = "561L1AIL00.mzML";
            rs = session.execute(boundStatement.bind(xmlFile));
            out.println("<ul>");
            if (!rs.isExhausted()) {
                Row rr = rs.one();
                String mzArray = rr.getString("mzArray");
                String intensityArray = rr.getString("intensityArray");
                String name = rr.getString("name");
                int count = rr.getInt("scan");
                out.println("<li>" + name + "&nbsp;" + count);
            }
            out.println("</ul>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
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
