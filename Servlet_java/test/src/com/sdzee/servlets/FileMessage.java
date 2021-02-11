package com.sdzee.servlets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jar.FileMessageForServlet;




@WebServlet("/FileMessage")
public class  FileMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	protected HashMap<String,HashMap<String,FileMessageForServlet>> messagesList = new HashMap<String,HashMap<String,FileMessageForServlet>>() ;
       
    public FileMessage() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String user1 = request.getParameter("user1");
		String user2 = request.getParameter("user2");
		
		
		if (messagesList.containsKey(user1) && messagesList.get(user1).containsKey(user2)) {
			
				FileMessageForServlet message = messagesList.get(user1).get(user2);
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream out = null;
				
				byte[] objetBytes = null ;
				
				out = new ObjectOutputStream(bos);   
				out.writeObject(message);
				out.flush();
				objetBytes = bos.toByteArray();
				
				
				
				try {
	
					ServletOutputStream os = response.getOutputStream();
					os.write(objetBytes);
					os.flush();
					os.close();
										
					messagesList.get(user1).remove(user2);
					
				} catch (IOException e) {

					e.printStackTrace();
				}

				
		} else {
			try {
				response.sendError(403);
									
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			ObjectInputStream inObj = new ObjectInputStream(request.getInputStream());
			FileMessageForServlet myFile = (FileMessageForServlet)inObj.readObject();
			
			String user1 = myFile.getuser1();
			String user2 = myFile.getuser2();
		
		
			if (messagesList.containsKey(user2)) {
				messagesList.get(user2).put(user1, myFile);
			}
			else {
				HashMap<String,FileMessageForServlet> messagesOfuser2 = new HashMap<String,FileMessageForServlet>();
				messagesOfuser2.put(user1, myFile);
				messagesList.put(user2, messagesOfuser2);
			}
	
	        
			PrintWriter out;
			
			response.setContentType("text/xml");
			response.setCharacterEncoding( "UTF-8" );
	
			out = response.getWriter();
	
			//verification de l'objet
			out.println("<user1>" + user1 + "</user1>");
			out.println("<user2>" + user2 + "</user2>");
				
			
		} catch (ClassNotFoundException e) {
			
			StringWriter outString = new StringWriter();
			
			PrintWriter out = new PrintWriter(outString);
			
			e.printStackTrace(out);
			
			response.getWriter().print(outString.toString());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	

	}
	
	
	
}
