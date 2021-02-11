package com.sdzee.beans;

import java.util.HashMap;

public class AllUsers {
	
	private static HashMap<String,User> usersList = new HashMap<String,User>();
	
	public static HashMap<String, User> getList(){
		return usersList ;
	}
	
	public static void addToList(String ip, String name, String type, String state) {
		User user = new User(name, type,  state);
		usersList.put(ip, user);
	}
	

		
	

}


