package com.nsoft.bullnexmc;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebeaninternal.api.SpiUpdate;

import net.md_5.bungee.api.ChatColor;

public class Admin {

	static class RemovePlugin extends MyComandExecutor{

		public RemovePlugin(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args))return true;
			
			if(!sender.isOp()) {
				
				SpigotPlugin.NotOPMessage(sender);
				return true;
			}
			
			if(args.length != 1) {
				
				SpigotPlugin.sendMessage(sender, "Numero de argumentos incorrecto!",2);
				return true;
			}
			
			try {
			    
				new File("plugins/" + args[0] + ".jar").delete();
			} catch (Exception x) {
				
			    SpigotPlugin.sendMessage(sender, "No existe " + "plugins/" + args[0] + ".jar O hubo un error",2);
			    return true;
			}
			return true;
		}
	}
	static class Download extends MyComandExecutor{

		public Download(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			 if(!super.onCommand(sender, command, label, args)) return true;
			 
			 if(!sender.isOp()) {
				 
				 SpigotPlugin.NotOPMessage(sender);
				 return true;
			 }
			 
			 if(args.length != 2) {
				 
				 SpigotPlugin.sendMessage(sender, "El numero de parametros es incorrecto!");
			 }
			 
			 try {
				Update.downloadUsingStream(args[0], "plugins/" + args[1] + ".jar");
			}
			 catch (IOException e) {
				// TODO Auto-generated catch block
				SpigotPlugin.sendMessage(sender, e.getMessage());
				return true;
				
			}
			 return true;
		}
	}
	static class Spawn extends MyComandExecutor{

		public Spawn(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(sender instanceof Player) {
				
				Player p = (Player)sender;
				p.teleport(new Location(SpigotPlugin.plugin.getServer().getWorld("world"), -184.5f, 71, 392));
				SpigotPlugin.sendMessage(sender, "Te has teletransportado al spawn! ",1);
			}
			return true;
		}
	}
	static class BroadCast extends MyComandExecutor{

		public BroadCast(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!sender.isOp()) {
				
				SpigotPlugin.NotOPMessage(sender);
				return true;
			}
			
			if(args.length < 0 || args.length == 0) {
				
				SpigotPlugin.sendMessage(sender, "Debes escribir un mensaje!",2);
				return true;
			}
			
			String text = "";
			for (String string : args) {
				
				text += string + " ";
			}
			
			SpigotPlugin.BroadCast(text);
			return true;
		}
		
	}
}
