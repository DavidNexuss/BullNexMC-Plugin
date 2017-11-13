package com.nsoft.bullnexmc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Update extends MyComandExecutor{

	public static final String UrlName = "http://bullnexrp.herokuapp.com/mc/BullNexMC-1.0.jar";
	public static int PluginSize;
	public Update(String name) {
		super(name);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		
		if(!super.onCommand(sender, command, label, args)) return false;

		sender.sendMessage("[BullNexMC] El servidor va a buscar actualizaciones...");
		
		try {
			
			URL url = new URL(UrlName);
			BufferedInputStream input = new BufferedInputStream(url.openStream());
			
			if(input.available() == PluginSize) {
				
				sender.sendMessage("[BullNexMC] No hay actualizaciones disponibles!");
			}else {
				
				sender.sendMessage("[BullNexMC] Actualización disponible!");
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

}
