package com.nsoft.bullnexmc.economy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import org.apache.commons.io.IOUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.bullnexmc.gang.Gang;

import net.md_5.bungee.api.ChatColor;

public class MarketValue extends MyComandExecutor{

	static File MarketFile;
	static FileConfiguration Market;

	public MarketValue(String name) {
		super(name);
		
		try {
			init();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void init() throws FileNotFoundException, IOException, InvalidConfigurationException {
	
		InputStream input = getClass().getResourceAsStream("/market.yml");
		MarketFile = File.createTempFile("market", ".yml");
		FileOutputStream out = new FileOutputStream(MarketFile);
		//IOUtils.copy(input, out);
		
		byte[] buffer = new byte[1024];
		int len= input.read(buffer);
		while (len != -1){
			out.write(buffer, 0, len);
			len = input.read(buffer);
		}

		Market = new YamlConfiguration();
		Market.load(MarketFile);
		
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!super.onCommand(sender, command, label, args)) return false;

		Player p = (Player)sender;
		String s = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType().name();
		SpigotPlugin.sendMessage(sender, Bank.B + " Para el objeto: " + s);
		
		int val= Market.getInt(s);
		if(val == 0) { SpigotPlugin.sendMessage(sender, " Lo sentimos, aun no esta implementado el precio de este objeto"); return true;}
		SpigotPlugin.sendMessage(sender, Bank.B + " Su precio de mercado es de: " + ChatColor.GREEN + val + Gang.eco.currencyNamePlural());
		return true;
	}
}
