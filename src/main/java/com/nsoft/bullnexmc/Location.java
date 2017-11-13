package com.nsoft.bullnexmc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Location extends MyComandExecutor {

	public Location(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if(!super.onCommand(sender, command, label, args)) return false;
		
		sender.sendMessage(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		return true;
	}

}
