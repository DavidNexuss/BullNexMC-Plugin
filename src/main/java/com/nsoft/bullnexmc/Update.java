package com.nsoft.bullnexmc;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

	//TODO: Add a command executor to change web for update
	public Update() {

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
				
				SpigotPlugin.sendMessage(sender, "Actualización disponible! procediendo a descargarla y instalarla...",0);

				try {
					
					downloadUsingStream(UrlName, "plugins/BullNexMC-1.0.jar");
					
				} catch (IOException e) {
					
					SpigotPlugin.sendMessage(sender, "Ha ocurrido un error!: " + e.getMessage(),2);
					return true;
				}
				SpigotPlugin.sendMessage(sender, "Actualización instalada, reinicia el server con /reload",1);
				
				return true;
			}
		} catch (Exception e) {

			SpigotPlugin.sendMessage(sender, e.getMessage(),2);
			return true;
		}
			
		return true;
	}
	
	public static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		String Command = command.getName().toLowerCase();
		if(Command.equals("update")) return update(sender);
		if(Command.equals("bn-size")) return sendSize(sender);
		
		return true;
	}

}
