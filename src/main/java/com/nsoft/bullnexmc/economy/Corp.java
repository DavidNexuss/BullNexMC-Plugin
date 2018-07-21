package com.nsoft.bullnexmc.economy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.nsoft.bullnexmc.SpigotPlugin;

public class Corp {

	private String name;
	private ArrayList<CorpVault> vaults;
	private HashMap<String, CorpUser> users;
	
	private ConfigurationSection corpSection;
	private ConfigurationSection userSection;
	private ConfigurationSection vaultSection;
	
	private static ArrayList<Corp> corps = new ArrayList<>();
	
	private static File CorpsFile;
	private static FileConfiguration CorpsF;
	private static ConfigurationSection corporations;
	
	public Corp(String name,ConfigurationSection section) {
		this.name = name;
		this.corpSection = section;
		userSection = section.createSection("users");
		vaultSection = section.createSection("vaults");
	}
	
	public CorpUser getUser(String name) {
		
		return users.get(name);
	}
	public boolean addUser(String name) {
		
		if(users.containsKey(name)) return false;
		users.put(name, new CorpUser(name,userSection.createSection(name)));
		saveFile();
		return true;
	}
	
	public static Corp createCorporation(String name) {
		
		if(corporations.contains(name)) return null;
		return new Corp(name, corporations.createSection(name));
	}
	public static void init() {
		
		CorpsFile = new File(SpigotPlugin.plugin.getDataFolder(),"corps.yml");
		
		if(!CorpsFile.exists()) {
			
			try {
				CorpsFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		boolean neew = CorpsFile.length() == 0;
		
		if (neew) baseInit();
		
		loadFile();
		load();
	}
	
	private static void baseInit() {
		
		CorpsF.createSection("corps");
	}
	
	private static void loadFile() {
		
		try {
			CorpsF.load(CorpsFile);
			corporations = CorpsF.getConfigurationSection("corps");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static void saveFile() {
		
		try {
			CorpsF.save(CorpsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void load() {
		
		
	}
}
