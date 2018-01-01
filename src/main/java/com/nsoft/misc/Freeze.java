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
		
		final int x = (int) Math.ceil((Math.random()*r*2 - r));
		final int y = (int) Math.ceil((Math.random()*2 - 1)) -1;
		final int z = (int) Math.ceil((Math.random()*r*2 - r));
		
		Block p = l.add(new Location(l.getWorld(), x, y, z)).getBlock();

		if(p.isLiquid()) {
			
			if(p.getType().equals(Material.LAVA) || p.getType().equals(Material.STATIONARY_LAVA)) freezeBlock(p,true);
			else freezeBlock(p, false);
		}
		else if(!p.isEmpty() && p.getLightLevel() < 2) {
			
			int a = (int) (Math.random()*100);
			if(a==0)freezeBlock(p,false);
		}

	}
	
	public void freezeBlock(Block p,boolean packed) {

		if(p.getType().equals(Material.ICE) || p.getType().equals(Material.PACKED_ICE)) return ;
		final Material c = p.getType();
		if (packed) p.setType(Material.PACKED_ICE);
		else p.setType(Material.ICE);
		SpigotPlugin.plugin.getServer().getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{
			p.setType(c);
		}, 1000);
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
