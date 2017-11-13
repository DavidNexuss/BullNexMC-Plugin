package com.nsoft.bullnexmc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class MyComandExecutor implements CommandExecutor {

	public String commandname;
	public MyComandExecutor(String name) {
		
		commandname = name;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		
		if(commandname.equals(command.getName().toLowerCase()))
		return true;
		else
		return false;
	}
}
