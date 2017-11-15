package com.nsoft.bullnexmc;

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
import org.bukkit.plugin.EventExecutor;

import net.md_5.bungee.api.ChatColor;

public class DBall {

	public static Location[] Balls = new Location[7];
	public DBall() {
		
		
	}
	
	public static Location nearLocation(Location playerPos) {
		
		
		if(Balls[0] == null) {
			
			return playerPos;
		}
		Location near = Balls[0];
		int lenght = (Balls[0].getBlockX() - playerPos.getBlockX())^2
					+(Balls[0].getBlockY() - playerPos.getBlockY())^2
					+(Balls[0].getBlockZ() - playerPos.getBlockZ())^2;
		
		for (Location location : Balls) {
		
			int lenght2 = (location.getBlockX() - playerPos.getBlockX())^2
						 +(location.getBlockY() - playerPos.getBlockY())^2
						 +(location.getBlockZ() - playerPos.getBlockZ())^2;
			if(lenght2 < lenght) {
				
				near = location;
				lenght = lenght2;
			}
		}
		
		return near;
	}
	
	public static void resetCompass() {
		
		for (Player p : SpigotPlugin.plugin.getServer().getOnlinePlayers()) {
			
			p.setCompassTarget(nearLocation(p.getLocation()));
		}
	}
	public static void onBlockPlaced(Block block) {
		
		if(block.getType().equals(Material.DIAMOND_BLOCK)) {
			
			SpigotPlugin.plugin.getServer().broadcastMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.GOLD + "found block");
			
			Location[] balls2 = new Location[Balls.length + 1];
			System.arraycopy(Balls, 0, balls2, 0, Balls.length);
			balls2[Balls.length] = block.getLocation();
			
			resetCompass();
		}
	}
	public static void onConnection(Player player) {
		
		player.setCompassTarget(nearLocation(player.getLocation()));
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
			}
			
			int n = 0;
			try {
				
				n = Integer.parseInt(args[0]);
				
			} catch (NumberFormatException e) {
				
				SpigotPlugin.sendMessage(sender, "Debes introducir un numero!",2);
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
