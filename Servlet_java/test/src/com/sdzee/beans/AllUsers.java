package com.sdzee.beans;

import java.util.HashMap;
import java.util.Map.Entry;

public class AllUsers {
	
	private static HashMap<String,User> usersList = new HashMap<String,User>();
	
	public static HashMap<String, User> getList(){
		return usersList ;
	}
	
	public static void addToList(String ip, String name, String type, String state) {
		User user = new User(name, type,  state);
		usersList.put(ip, user);
	}
	
	public static User getKeyByValue(HashMap<String,User> map, String name) {
	    for (Entry<String, User> entry : map.entrySet()) {
	        if (entry.getValue().name != null && entry.getValue().name.equals(name)) {
	        	User user = new User(entry.getKey(), entry.getValue().type, entry.getValue().state );
	            return user;
	        }
	    }
	    return null;
	}
	

	

}


