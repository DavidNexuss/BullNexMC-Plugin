package com.nsoft.bullnexmc.economy;

import org.bukkit.configuration.ConfigurationSection;

public class CorpUser extends CorpComponent{

	private String name;
	
	public CorpUser(Corp corp,ConfigurationSection section,String name) {
		
		super(corp, section);
		this.name = name;
		
	}
	
	public String getName(){return name;}
	
	@Override
	void load() {
		
	}
}
