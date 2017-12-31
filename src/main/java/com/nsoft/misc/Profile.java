package com.nsoft.misc;

import java.util.HashMap;
import java.util.UUID;

public class Profile {

	public UUID uuid;
	HashMap<String, Boolean> permissions = new HashMap<>();
	HashMap<String, Boolean> states = new HashMap<>();
	public Profile() {
		
	}
	
	public boolean getState(String p) {
		
		if(states.containsKey(p)) {
			return states.get(p);
		}else return false;
	}
	
	public void setState(String p,boolean s) {
		
		states.put(p, s);
	}
	public boolean hasPermission(String p) {
		
		if(permissions.containsKey(p)) {
			return permissions.get(p);
		}else return false;
	}
	
	public void setPermission(String p,boolean state) {
		
		permissions.put(p, state);
	}
}
