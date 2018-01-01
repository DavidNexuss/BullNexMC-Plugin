package com.nsoft.misc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.bullnexmc.gang.Dictionary;

public class Magma extends SuperPower {

	public Magma(String name) {
		super(name);
		
	}

	@Override
	public String getDescription() {
		
		return "Crea unas columnas de magma";
	}
	
	public void magma(Block p) {
		
		if(p.getType().equals(Material.GLOWSTONE)) return;
		final Material type =p.getType();
		p.setType(Material.GLOWSTONE);
		SpigotPlugin.plugin.getServer().getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{
			
			p.setType(type);
		}, 1000);
	}
	public void createColumn(World w,int x,int z,int y) {
		
		SpigotPlugin.plugin.getServer().getScheduler().runTask(SpigotPlugin.plugin, ()->{
			
			int ny = y;
			double grades = Math.atan(z/x);
			while(ny > 200) {
				
				ny++;
				grades +=10;
				magma(new Location(w, x + 5*Math.cos(Math.toRadians(grades)), y, z + 5*Math.sin(Math.toRadians(grades))).getBlock());
				System.out.println(x + 5*Math.cos(Math.toRadians(grades)) + " " + z + 5*Math.sin(Math.toRadians(grades)));
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
	public void buildTower(Player p) {
		
		SpigotPlugin.plugin.getServer().getScheduler().runTask(SpigotPlugin.plugin, ()->{
			
			createColumn(p.getWorld(),p.getLocation().getBlockX() + 5, p.getLocation().getBlockZ(),p.getLocation().getBlockY());
			createColumn(p.getWorld(),p.getLocation().getBlockX() - 5, p.getLocation().getBlockZ(),p.getLocation().getBlockY());
			createColumn(p.getWorld(),p.getLocation().getBlockX(), p.getLocation().getBlockZ() + 5,p.getLocation().getBlockY());
			createColumn(p.getWorld(),p.getLocation().getBlockX(), p.getLocation().getBlockZ() - 5,p.getLocation().getBlockY());
		});
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if(!super.onCommand(sender, command, label, args)) return true;
		SpigotPlugin.sendMessage(sender, "Aun no funciona este comando!");
		//buildTower(SpigotPlugin.plugin.getServer().getPlayer(Dictionary.getPlayerUUID(sender.getName())));
		return true;
	}

}
