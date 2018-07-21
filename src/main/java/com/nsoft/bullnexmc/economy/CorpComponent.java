package com.nsoft.bullnexmc.economy;

import org.bukkit.configuration.ConfigurationSection;

public abstract class CorpComponent {

	private Corp corp;
	private ConfigurationSection section;
	
	public CorpComponent(Corp corp,ConfigurationSection section) {
		
		this.corp = corp;
		this.section = section;
	}
	
	public Corp getCorp() {
		return corp;
	}
	
	public ConfigurationSection getConfigurationSection() {
		return section;
	}
	
	abstract void load();
	final void saveToFile() {corp.saveCorp();}
}
