package com.nsoft.bullnexmc.economy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.bullnexmc.economy.Bank.DepositMoney;

public class Corp {

	private String name;
	private CorpDeposit deposit;
	private ArrayList<CorpVault> vaults;
	private HashMap<String, CorpUser> users;
	
	private ConfigurationSection corpSection;
	private ConfigurationSection userSection;
	private ConfigurationSection vaultSection;
	private ConfigurationSection depositSection;
	
	private static HashMap<String, Corp> corps = new HashMap<>();
	
	private static File CorpsFile;
	private static FileConfiguration CorpsF;
	private static ConfigurationSection corporations;
	
	public Corp(String name,ConfigurationSection section) {
		
		this.name = name;
		this.corpSection = section;
		
		
		userSection = section.contains("users") ? section.getConfigurationSection("users") : section.createSection("users");
		vaultSection = section.contains("vaults") ? section.getConfigurationSection("vaults") : section.createSection("vaults");
		depositSection = section.contains("deposit") ? section.getConfigurationSection("deposit") : section.createSection("deposit");
		
		createDeposit();
		
		initUsers();
		initVaults();
	}
	
	private void initVaults() {
		
		for (String vault_name : vaultSection.getKeys(false)) {
			
			CorpVault vault = new CorpVault(this, vaultSection.getConfigurationSection(vault_name), null);
			vault.load();
			vaults.add(vault);
		}
	}
	private void initUsers() {
		
		for (String user_name : userSection.getKeys(false)) {
			
			CorpUser corp = new CorpUser(this, userSection.getConfigurationSection(user_name), user_name);
			corp.load();
			setUser(corp);
		}
	}
	private void createDeposit() {
		
		deposit = new CorpDeposit(this, depositSection);
		deposit.load();
	}
	
	public CorpDeposit getDeposit() {
		
		return deposit;
	}
	public CorpUser getUser(String name) {
		
		return users.get(name);
	}
	
	public boolean addUser(String name) {
		
		return setUser(new CorpUser(this,userSection.createSection(name),name));
	}
	
	private boolean setUser(CorpUser user) {
		
		if(users.containsKey(user.getName())) return false;
		users.put(user.getName(), user);
		saveCorp();
		return true;
	}
	
	public String getName() {return name;}
	public final void saveCorp() {	
		saveFile();
	}
	
	public static Corp getCorp(String name) {
		
		if(corps.containsKey(name)) return corps.get(name);
		
		return createCorporation(name);
	}
	public static Corp createCorporation(String name) {
		
		if(corps.containsKey(name)) return null;
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
		
		CorpsF = new YamlConfiguration();
		if (neew) baseInit();
		
		loadFile();
		load();
	}
	
	private static void baseInit() {
		
		CorpsF.createSection("corps");
		try {
			CorpsF.save(CorpsFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		for (String corps_names: corporations.getKeys(false)) {
			
			ConfigurationSection corp = corporations.getConfigurationSection(corps_names);
			corps.put(corps_names, new Corp(corps_names, corp));
		}
	}
}
