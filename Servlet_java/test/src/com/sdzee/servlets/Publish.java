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


@WebServlet("/Publish")
public class Publish extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public Publish() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		String name = request.getParameter("name");
		String state = request.getParameter("state");
		String ip = request.getParameter("ip");

		String type = request.getParameter("type");
		
		AllUsers.addToList(ip, name, type, state);
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
			String stateE = userE.state ;
			String nameE = userE.name ;	
			String typeE = userE.type ;
			
			out.println("<name>" + nameE + "</name>");
			out.println("<type>" + typeE + "</type>");
			out.println("<state>" + stateE + "</state>");
			out.println("<ip>" + ipE + "</ip>");
			out.println("</user>");
			
			
		}
		out.println("</users>");

	
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
