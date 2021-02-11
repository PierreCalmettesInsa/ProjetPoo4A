package com.sdzee.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.sdzee.beans.UserSettingForConnection;


@WebServlet("/OpenConnection")
public class OpenConnection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static HashMap<String, UserSettingForConnection> connection = new HashMap<String,UserSettingForConnection>() ;

    public OpenConnection() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String name = request.getParameter("myName");
		
		response.setContentType("text/xml");
		response.setCharacterEncoding( "UTF-8" );
		
		PrintWriter out;
		
		
		if (connection.containsKey(name)) {
			try {
				out = response.getWriter();
			
				out.println("<user>");
		
				UserSettingForConnection otherUser = connection.get(name);
				String otherUserName = otherUser.name ;
				String otherUserIp = otherUser.ip ;
				out.println("<name>" + otherUserName + "</name>");
				out.println("<ip>" +  otherUserIp + "</ip>" );
				
				out.println("</user>");
				
				connection.remove(name);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			
		} else {
			try {
				out = response.getWriter();
			
				out.println("<user>");
		
				out.println("<name>" + "" + "</name>");
				out.println("<ip>" +  "" + "</ip>" );
				
				out.println("</user>");
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		
		
		
		
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String user1 = request.getParameter("user1");
		String user2 = request.getParameter("user2");

		String ip = request.getParameter("ip");

		UserSettingForConnection user = new UserSettingForConnection(user1,ip);
		connection.put(user2, user);
		
		
	}

}
