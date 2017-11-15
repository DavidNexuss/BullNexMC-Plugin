package com.nsoft.bullnexmc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebeaninternal.api.SpiUpdate;

import net.md_5.bungee.api.ChatColor;

public class Admin {

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
