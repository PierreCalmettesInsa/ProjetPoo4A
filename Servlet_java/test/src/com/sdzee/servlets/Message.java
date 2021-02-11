package com.sdzee.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



@WebServlet("/Message")
public class Message extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected HashMap<String,HashMap<String,String>> messagesList = new HashMap<String,HashMap<String,String>>() ;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Message() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String user1 = request.getParameter("user1");
		String user2 = request.getParameter("user2");
		
		PrintWriter out;
		
		if (messagesList.containsKey(user1) && messagesList.get(user1).containsKey(user2)) {
			
				String message = messagesList.get(user1).get(user2);
				
				try {
					out = response.getWriter();
				
					out.println("<user>");
			
					out.println("<msg>" + message + "</msg>");
					
					out.println("</user>");
					
					messagesList.get(user1).remove(user2);
					
				} catch (IOException e) {

					e.printStackTrace();
				}

				
		} else {
			try {
				
				out = response.getWriter();
			
				out.println("<user>");
		
				out.println("<msg>" + "" + "</msg>");
				
				out.println("</user>");
									
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String user1 = request.getParameter("user1");
		String user2 = request.getParameter("user2");
		String msg = request.getParameter("msg");

		if (messagesList.containsKey(user2)) {
			messagesList.get(user2).put(user1, msg);
		}
		else {
			HashMap<String,String> messagesOfuser2 = new HashMap<String,String>();
			messagesOfuser2.put(user1, msg);
			messagesList.put(user2, messagesOfuser2);
		}

	}

}
