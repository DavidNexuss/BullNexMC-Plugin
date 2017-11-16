package com.nsoft.bullnexmc;

import java.util.ArrayList;

import javax.persistence.PostLoad;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;

import net.md_5.bungee.api.ChatColor;

public class DBall {

	public static Block[] balls = new Block[2];
	public DBall() {
		
		
	}
	
	public static Location nearLocation(Location playerPos) {
		
		
		if(balls.length == 0) {
			
			return playerPos;
		}
		Location near = balls[0].getLocation();
		int lenght = (balls[0].getLocation().getBlockX() - playerPos.getBlockX())^2
					+(balls[0].getLocation().getBlockY() - playerPos.getBlockY())^2
					+(balls[0].getLocation().getBlockZ() - playerPos.getBlockZ())^2;
		
		for (Block b: balls) {
		
			if(b.getType().equals(Material.DIAMOND_BLOCK)) {
				
				Location location = b.getLocation();
				int lenght2 = (location.getBlockX() - playerPos.getBlockX())^2
							 +(location.getBlockY() - playerPos.getBlockY())^2
							 +(location.getBlockZ() - playerPos.getBlockZ())^2;
				if(lenght2 < lenght) {
					
					near = location;
					lenght = lenght2;
				}
			}
			
		}
		
		return near;
	}
	
	public static void resetCompassForPlayer(Player p) {
		
		p.setCompassTarget(nearLocation(p.getLocation()));
	}
	public static void resetCompass() {
		
		for (Player p : SpigotPlugin.plugin.getServer().getOnlinePlayers()) {
			
			p.setCompassTarget(nearLocation(p.getLocation()));
		}
	}
	public static void onBlockPlaced(Block block) {
		
		if(block.getType().equals(Material.DIAMOND_BLOCK)) {
			
			SpigotPlugin.plugin.getServer().broadcastMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.GOLD + "found block");
			
			Block[] balls2 = new Block[balls.length + 1];
			System.arraycopy(balls, 0, balls2, 0, balls.length);
			balls2[balls.length] = block;
			
			resetCompass();
		}
	}
	public static void onConnection(Player player) {
		
		player.setCompassTarget(nearLocation(player.getLocation()));
	}
	
	static class radar extends MyComandExecutor{

		public radar(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			
				
				SpigotPlugin.sendMessage(sender, "Este comando aun no funciona");
				return true;
			/*if(!(sender instanceof Player)) {
				
				SpigotPlugin.sendMessage(sender,"Este comando solo lo pueden usar jugadores",2);
				return true;
			}
			
			Player p = (Player)sender;
			
			if(p.getItemInHand().getType().equals(Material.COMPASS)) {
				
				SpigotPlugin.sendMessage(sender,"Coges tu brújula y pulsas el boton de buscar");
				resetCompassForPlayer(p);
				
				return true;
			}else {
				
				SpigotPlugin.sendMessage(sender,"Debes tener una brújula en la mano", 2);
				return true;
			}*/
			
		}
		
		
	}
	static class BetaDragonBalls extends MyComandExecutor{

		public BetaDragonBalls(String name) {
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
				
				SpigotPlugin.sendMessage(sender, "Este comando necesita un argumento", 2);
				return true;
			}
			
			int n = 0;
			try {
				
				n = Integer.parseInt(args[0]);
				
			} catch (NumberFormatException e) {
				
				SpigotPlugin.sendMessage(sender, "Debes introducir un numero!",2);
				return true;
			}
			
			World world = SpigotPlugin.plugin.getServer().getWorld("world");
			float size = (float) world.getWorldBorder().getSize();
			Location center = world.getWorldBorder().getCenter();
			
			for (int i = 1; i < n; i++) {
				
				int xi =  (int) (Math.random()*1000 + center.getBlockX() - 500);
				int zi =  (int) (Math.random()*1000 + center.getBlockZ() - 500);
				
				float x = ((float)xi) + .5f;
				float z = ((float)zi) + .5f;
				
				
				/*SpigotPlugin.sendMessage(sender, "Block at: " + x + " " + z,2);
				SpigotPlugin.sendMessage(sender, "Maxima distancia de " + size*2/3,2);*/
				world.getBlockAt(xi, 200, zi).setType(Material.DIAMOND_BLOCK);
				world.spawnFallingBlock(new Location(world, x, 200, z), 57, (byte)0);	
				
			}
			
			SpigotPlugin.sendMessage(sender,"Bolas del dragon generadas! en total: " + n);
			
			SpigotPlugin.BroadCast("Bloques de diamantes diamantes caen del cielo!");
			return true;
		}
	}
}
