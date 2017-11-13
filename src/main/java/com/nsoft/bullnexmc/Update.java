package com.nsoft.bullnexmc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class Update implements CommandExecutor{

	public static final String UrlName = "https://www.dropbox.com/s/ewhr7utlktw6txb/BullNexMC-1.0.jar?dl=1";
	public static int PluginSize;
	public static byte[] buffer;

	public Update() {
		// TODO Auto-generated constructor stub
	}
	public boolean sendSize(CommandSender sender) {
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		float size = ((float)PluginSize)/1024f;
		
		SpigotPlugin.sendMessage(sender, "El tamaño del plugin es de: " + ChatColor.RED + df.format(size) + "KB");
		return true;
	}
	public boolean update(CommandSender sender) {
		
		if(!sender.isOp()) {
			
			SpigotPlugin.sendMessage(sender, "No tienes permiso para usar este comando!",2);
			return true;
		}
		
		SpigotPlugin.sendMessage(sender, "El servidor va a buscar actualizaciones...",0);
		
		try {
			
			URL url = new URL(UrlName);
			BufferedInputStream input = new BufferedInputStream(url.openStream());
			
			if(input.available() == PluginSize) {
				
				SpigotPlugin.sendMessage(sender, "No hay actualizaciones disponibles!",2);
			}else {
				
				SpigotPlugin.sendMessage(sender, "Actualización disponible! procediendo a descargarla",0);
				buffer = new byte[input.available()];
				int b = -1;
				int n = 0;
				
				do {
					
					b = input.read();
					if(b != -1) {
						
						buffer[n] = (byte)b;
						n++;
					}
				} while (b != -1);
				
				SpigotPlugin.sendMessage(sender, "Actualización descargada, instalando...",1);
				FileOutputStream salida = new FileOutputStream("plugins/BullNexMC-1.0.jar");
				salida.write(buffer);
				salida.flush();
				salida.close();
				
				SpigotPlugin.sendMessage(sender, "Actualización instalada, reinicia el server con /reload",1);
				
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			SpigotPlugin.sendMessage(sender, e.getMessage(),2);
			return true;
		}
			
		return true;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		
		String Command = command.getName().toLowerCase();
		if(Command.equals("update")) return update(sender);
		if(Command.equals("bn-size")) return sendSize(sender);
		
		return true;
	}

}
