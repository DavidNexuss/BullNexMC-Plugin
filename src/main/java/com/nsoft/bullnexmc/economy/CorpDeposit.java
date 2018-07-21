package com.nsoft.bullnexmc.economy;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class CorpDeposit extends CorpComponent{

	private HashMap<Material, Integer> deposit = new HashMap<>();
	
	public CorpDeposit(Corp corp,ConfigurationSection section) {
		
		super(corp, section);
	}
	
	public int getAmount(Material mat) {
		
		return deposit.get(mat);
	}
	public void addItem(Material mat) {
		
		addAmount(mat, 1);
	}
	public void subItem(Material mat) {
		
		addAmount(mat, -1);
	}
	public void addAmount(Material mat, int amount) {
		
		setAmount(mat, getAmount(mat) + amount);
	}
	public void setAmount(Material mat,int amount) {
		
		deposit.put(mat, amount);
		saveDeposit();
	}
	
	
	void saveDeposit() {
		
		for (Entry<Material, Integer> e: deposit.entrySet()) {
			
			getConfigurationSection().set(e.getKey().name(), e.getValue());
		}
		
		saveToFile();
	}
	
	@Override
	void load() {
		
		for (String mat : getConfigurationSection().getKeys(false)) {
			
			deposit.put(Material.getMaterial(mat), getConfigurationSection().getInt(mat));
		}
	}
}
