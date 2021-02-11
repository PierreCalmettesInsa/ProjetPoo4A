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


@WebServlet("/Notify")
public class Notify extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public Notify() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		HashMap<String, User>  list  = AllUsers.getList();
		
		response.setContentType("text/xml");
		response.setCharacterEncoding( "UTF-8" );
		
		PrintWriter out;
		try {
			out = response.getWriter();
		out.println("<users>");
		
		for (Entry<String, User> e : list.entrySet()) {
			out.println("<user>");

			User userE = e.getValue();
			String ipE = e.getKey();
			String nameE = userE.name ;	
			String type = userE.type ;
			out.println("<name>" + nameE + "</name>");
			out.println("<type>" + type + "</type>");
			out.println("<state>" + userE.state + "</state>");
			out.println("<ip>" + ipE + "</ip>");

			out.println("</user>");
		}
		
		out.println("</users>");

		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
