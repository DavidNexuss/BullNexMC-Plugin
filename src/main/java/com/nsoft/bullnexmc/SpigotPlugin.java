package com.nsoft.bullnexmc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SpigotPlugin extends JavaPlugin {
	
	public static SpigotPlugin plugin;
	public SpigotPlugin() {
		
		plugin = this;
	}
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    public static void sendMessage(CommandSender sender,String msg) {
    	
    	sendMessage(sender, msg, 0);
    }
    public static void sendMessage(CommandSender sender,String msg,int type) {
    	
    	
    	switch (type) {
		case 0:
			
			sender.sendMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.GOLD + msg);
			break;
		case 1:
			
			sender.sendMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.BLUE + msg);
			break;
		case 2:
			
			sender.sendMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.RED + msg);
		default:
			break;
		}
    }
    
    public static int checkSieze() {
    	
    	try {
    		
    		
			FileInputStream entrada = new FileInputStream("plugins/BullNexMC-1.0.jar");
			int size = entrada.available();
			entrada.close();
			return size;
			
    	
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
    	
    }
    @Override
    public void onEnable() {
        // Don't log enabling, Spigot does that for you automatically!
    	
    	Update.PluginSize = checkSieze();
    	Update a = new Update();
    	getServer().broadcast( ChatColor.GREEN +"[BullNexRP] " + ChatColor.BLUE + "Plugin iniciado!", "bullnexmc.update");
        // Commands enabled with following method must have entries in plugin.yml
        getCommand("bn-drop").setExecutor(new DeadFall.DropCommand("bn-drop"));
    	getCommand("example").setExecutor(new ExampleCommand(this));
        getCommand("update").setExecutor(a);
        getCommand("bn-size").setExecutor(a);
        getCommand("location").setExecutor(new Location("location"));
        getCommand("writefile").setExecutor(new WriteFile("writefile"));
        
    }
}
