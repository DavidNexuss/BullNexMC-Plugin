package com.nsoft.bullnexmc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.avaje.ebean.enhance.asm.Type;

public class DeadFall {
	
	static boolean drop(Server server,float x,float y, float z) {
		
		x+= .5f;
		z+= .5f;
		
		
		World world = server.getWorld("world");
		
		SpigotPlugin.BroadCast("Falling at " + x + " " + z);
		Location block = new Location(world, x, y, z);
		int typeId = world.getBlockAt(block).getTypeId();
		
		if(typeId == 0) return true;
		
		world.spawnFallingBlock(block, typeId, (byte)0);
		world.getBlockAt(block).setTypeId(0);
		
		return true;
	}
	static class BoltCommand extends MyComandExecutor{

		public BoltCommand(String name) {
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
			
			if(args.length != 1) {
				
				SpigotPlugin.sendMessage(sender, "La cantidad de parametros es incorrecta, debes introducir un nombre", 2);
				return true;
				
			}		
				Player a = Bukkit.getPlayer(args[0]);
				if(a == null) {
					
					SpigotPlugin.sendMessage(sender, "No hay ningun jugador conectado con ese nombre!");
					return true;
				}
				
				World world = sender.getServer().getWorld("world");
				world.spawnEntity(a.getLocation(), EntityType.LIGHTNING);
			return true;
		}
		
	}
	static class DropCommand extends MyComandExecutor{

		public DropCommand(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			if(!super.onCommand(sender, command, label, args)) return true;
			if(!sender.isOp()) {
				
				SpigotPlugin.NotOPMessage(sender);
				return true;
			}
			if(args.length < 3) {
				
				SpigotPlugin.sendMessage(sender, "No introduciste suficientes parámetros!",2);
				return true;
			}
			
			if(args.length > 3) {
				
				SpigotPlugin.sendMessage(sender, "Introduciste demasiados parámetros",2);
				return true;
			}
			
			float x;
			float y;
			float z;
			
			try {
				
				x = Float.parseFloat(args[0]);
				y = Float.parseFloat(args[1]);
				z = Float.parseFloat(args[2]);
				
			} catch (NumberFormatException  e) {
				
				SpigotPlugin.sendMessage(sender, "Al menos uno de los argumentos no eran números!",2);
				return true;
				
			}
			
			return drop(sender.getServer(), x, y, z);
		}
		
	}

}
