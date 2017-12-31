package com.nsoft.misc;

import java.util.ArrayList;

import javax.lang.model.element.Element;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.bullnexmc.gang.Dictionary;

public abstract class SuperPower extends MyComandExecutor{

	private static ArrayList<SuperPower> superPowers = new ArrayList<>();
	public SuperPower(String name) {
		super(name);
		superPowers.add(this);
	}
	
	public void setState(CommandSender sender,boolean state) {
		
		Dictionary.getProfile(sender.getName()).states.put(getName(), state);
		if(state) {
			
			SpigotPlugin.sendMessage(sender, "Poder " + getName() + " activado!");
		}else {
			
			SpigotPlugin.sendMessage(sender, "Poder " + getName() + " desactivado");
		}
	}
	
	public boolean getState(String name) {
		
		return Dictionary.getProfile(name).getState(getName());
	}
	public static boolean exists(String name) {
		
		for (SuperPower superPower : superPowers) {
			
			if(superPower.getName().equals(name))return true;
		}
		return false;
	}
	
	public static void list(CommandSender sender) {
		
		for (SuperPower superPower : superPowers) {
			
			SpigotPlugin.sendMessage(sender, superPower.getName());
			SpigotPlugin.sendMessage(sender, "       +" + superPower.getDescription());
		}
	}
	
	public abstract String getDescription();
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if(!super.onCommand(sender, command, label, args))return true;
		if(Dictionary.getProfile(sender.getName()).hasPermission(getName()))return true;
		return false;
	}
	
	public static class Power extends MyComandExecutor{
		
		public Power(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			if(!super.onCommand(sender, command, label, args)) return true;
			if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
			if(args.length != 3) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos!",2); return true;}
			if(!exists(args[1])) {SpigotPlugin.sendMessage(sender, "Este superpoder no existe! " + args[0],2); list(sender); return true;}
			
			boolean state = false;
			
			try {
				
				state = Boolean.parseBoolean(args[2]);
			} catch (Exception e) {
			}
			Dictionary.getProfile(args[0]).setPermission(args[1], state);
			if(state)SpigotPlugin.sendMessage(SpigotPlugin.plugin.getServer().getPlayer(Dictionary.getPlayerUUID(sender.getName())),"Te han dado el superpoder: " + args[1]);
			SpigotPlugin.sendMessage(sender, "Permiso cambiado con exito");
			return true;
		}
	}
	
	public static class ListPowers extends MyComandExecutor{
		
		public ListPowers(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			list(sender);
			return true;
		}
	}
}
