package com.nsoft.bullnexmc;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlugin extends JavaPlugin {
	
	public static SpigotPlugin plugin;
	public SpigotPlugin() {
		
		plugin = this;
	}
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    @Override
    public void onEnable() {
        // Don't log enabling, Spigot does that for you automatically!

        // Commands enabled with following method must have entries in plugin.yml
        getCommand("example").setExecutor(new ExampleCommand(this));
        getCommand("update").setExecutor(new Update("update"));
    }
}
