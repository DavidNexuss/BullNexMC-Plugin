package com.nsoft.bullnexmc.gang;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.misc.Profile;

public class Dictionary implements Listener {

	private static HashMap<String, Profile> Offlines = new HashMap<>();
	private static boolean changes = false;
	private static Thread AutoSave;
	
	static {
		
		AutoSave = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					
					while(true) {
						
						save(SpigotPlugin.plugin.getConfig().getConfigurationSection("dic"));
						Thread.sleep(3000);
					}
				} catch (Exception e) {
					
					SpigotPlugin.BroadCast(ChatColor.RED + "El proceso de autoguardado del diccionario se ha suspendido!");
				}
			}
		});
	}
	
	public static void init() {
		
		load(SpigotPlugin.plugin.getConfig().getConfigurationSection("dic"));
		AutoSave.start();
		System.err.println("Executed");
		SpigotPlugin.plugin.getServer().getPluginManager().registerEvents(new Dictionary(), SpigotPlugin.plugin);
	}
	public static void load(ConfigurationSection inf) {
		
		if(inf == null)return;
		
		for (String key : inf.getKeys(false)) {
			
			Profile p = new Profile();
			Offlines.put(key, (Profile) inf.get(key));
		}
	}
	
	public static void save(ConfigurationSection inf) {
		
		if(!changes) return;
		
		if(inf == null) {
			
			SpigotPlugin.plugin.getConfig().createSection("dic");
			return;
		}
		for (String key : Offlines.keySet()) {
			
			inf.set(key, Offlines.get(key));
		}
		
		SpigotPlugin.plugin.saveConfig();
		changes = false;
	}
	
	public static UUID getPlayerUUID(String name) {
		
		return Offlines.get(name).uuid;	
	}
	
	public static Profile getProfile(String name) {
		
		return Offlines.get(name);
	}
	
	//TODO: PLAYER LOGIN !!!!!!!!!
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if(!Offlines.containsKey(event.getPlayer().getName())) {
			
			Profile p = new Profile();
			p.uuid = event.getPlayer().getUniqueId();
			Offlines.put(event.getPlayer().getName(), p);
			changes = true;
		}
		
	}
}
