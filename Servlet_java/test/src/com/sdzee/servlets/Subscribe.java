package com.sdzee.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sdzee.beans.AllUsers;
import com.sdzee.beans.User;


@WebServlet("/Subscribe")
public class Subscribe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	
	

    public Subscribe() {
        super();
    }

    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String ip = request.getParameter("ip");

		
		AllUsers.addToList(ip, name, type, "offline");
		HashMap<String, User>  list  = AllUsers.getList();
		
		response.setContentType("text/xml");
		response.setCharacterEncoding( "UTF-8" );
		
		PrintWriter out;
		try {
			out = response.getWriter();

		
		
			out.println("<users>");
			
			for (Entry<String, User> e : list.entrySet()) {
				out.println("<user>");
	
				String ipE = e.getKey();
				User userE = e.getValue();
				String nameE = userE.name ;
				String typeE = userE.type ;
				out.println("<name>" + nameE + "</name>");
				out.println("<type>" + typeE + "</type>");
				out.println("<ip>" + ipE + "</ip>");

				out.println("</user>");

			
			}
		
			out.println("</users>");

		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
