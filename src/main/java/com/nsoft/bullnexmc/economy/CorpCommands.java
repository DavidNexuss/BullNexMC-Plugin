package com.nsoft.bullnexmc.economy;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;
import com.nsoft.bullnexmc.economy.CorpTicket.TransactionObject;

public class CorpCommands {

	public static class TicketDebug extends MyComandExecutor {
		
		public TicketDebug(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			if(!super.onCommand(sender, command, label, args)) return false;
			
			Player p = (Player)sender;
			Corp corp = Corp.getCorp("NexCorp");
			CorpTicket t = new CorpTicket(corp, null,sender.getName(),BankUser.getCurrentDate());
			for (int i = 0; i < 15; i++) {
				
				TransactionObject o = new TransactionObject();
				o.m = Material.getMaterial(i);
				o.value = i*i / 2;
				o.amount = i;
				t.addItem(o,false);
			}
			
			p.getInventory().addItem(t.getAsBook());
			SpigotPlugin.sendMessage(sender, "Ejecutado correctamente");
			return true;
		}
	}
}
