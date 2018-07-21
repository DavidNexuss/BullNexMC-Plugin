package com.nsoft.bullnexmc.economy;

import org.bukkit.configuration.ConfigurationSection;

public class CorpUser {

	private String name;
	private ConfigurationSection section;
	
	public CorpUser(String name,ConfigurationSection section) {
		
		this.name = name;
		this.section = section;
		
	}
	
	public String getName(){return name;}
}
