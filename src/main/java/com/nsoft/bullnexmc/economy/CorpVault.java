package com.nsoft.bullnexmc.economy;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.material.Sign;
import org.bukkit.configuration.ConfigurationSection;

public class CorpVault {

	private Sign vaultSign;
	private Chest vaultChest;
	private Location location;
	
	private ConfigurationSection section;
	public CorpVault(Location l,ConfigurationSection section) {
		
		this.section = section;
		location = l;
		loadVault();
	}
	
	private void loadVault() {
		
		if(location.getBlock() instanceof Sign) {
			
			System.err.println("CorpVault initialized with a wrong location, not a sign!!");
			throw new IllegalArgumentException("Illegal location!");
		}
		
		vaultSign = (Sign) location.getBlock();
		
		if(location.getBlock().getRelative(vaultSign.getAttachedFace()).getType() != Material.CHEST) {
			
			System.err.println("CorpVault initialized with a wrong location, not a chest!!");
			throw new IllegalArgumentException("Illegal location!");
		}
		
		vaultChest = (Chest) location.getBlock().getRelative(vaultSign.getAttachedFace());
	}
}
