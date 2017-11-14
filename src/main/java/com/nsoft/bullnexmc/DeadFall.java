package com.nsoft.bullnexmc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.avaje.ebean.enhance.asm.Type;

public class DeadFall {
	
	static class DropCommand extends MyComandExecutor{

		public DropCommand(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			if(!super.onCommand(sender, command, label, args)) return true;
			if(!sender.isOp()) {
				
				SpigotPlugin.sendMessage(sender, "Solo los operadores pueden ejecutar este comando!", 2);
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
			
			x+= .5f;
			z+= .5f;
			
			
			World world = sender.getServer().getWorld("world");
			Location block = new Location(world, x, y, z);
			int typeId = world.getBlockAt(block).getTypeId();
			
			if(typeId == 0) return true;
			
			world.spawnFallingBlock(block, typeId, (byte)0);
			world.getBlockAt(block).setTypeId(0);
			
			return true;
		}
		
	}

}
