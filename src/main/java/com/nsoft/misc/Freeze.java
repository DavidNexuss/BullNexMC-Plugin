package com.nsoft.misc;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.bullnexmc.gang.Dictionary;

public class Freeze extends SuperPower{

	static int r = 5;
	
	@Override
	public String getDescription() { return "Congela a tus enemigos"; }
	public Freeze(String name) {
		
		super(name);
	}
	
	public void freezeArea(Location l) {
		
		Block p = l.add(new Location(l.getWorld(), Math.random()*r*2 - r, Math.random()*2 - 1, Math.random()*r*2 - r)).getBlock();
		if(p.isLiquid())p.setType(Material.ICE);

	}
	public void freeze(Player p) {
		
		SpigotPlugin.plugin.getServer().getScheduler().runTaskAsynchronously(SpigotPlugin.plugin, ()->{
			
			try {
				
				int b = 0;
				int seconds = 5;
				while(b < seconds*1000/5) {
					
					SpigotPlugin.plugin.getServer().getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{freezeArea(p.getLocation());}, 1);
					b++;
					Thread.sleep(5);
				}
				
				setState(p, false);
			} catch (Exception e) {
				e.printStackTrace();
				setState(p, false);
			}
		});
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(!super.onCommand(sender, command, label, args)) return true;
		
		if(!getState(sender.getName())) {
			freeze(SpigotPlugin.plugin.getServer().getPlayer(Dictionary.getPlayerUUID(sender.getName())));
			setState(sender, true);
		}
		else {
			SpigotPlugin.sendMessage(sender, "Ya tienes el poder activado!");
		}
		
		return true;
	}

}
