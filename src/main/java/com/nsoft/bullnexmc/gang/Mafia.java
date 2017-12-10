package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.hamcrest.core.Is;

import com.nsoft.bullnexmc.SpigotPlugin;
import static com.nsoft.bullnexmc.gang.Gang.*;

/**
 * 
 * Esta clase refleja una mafia
 * @author DavidNexuss
 *
 */
public class Mafia implements Field{
	
	public String name;
	public int Balance;
	public ChatColor color;
	public ArrayList<GangPlayer> players;
	public ArrayList<Point> ownedPoints;
	
	@Override
	public void save(ConfigurationSection save) {
		
		save.set("name", name);
		save.set("color", color.name());
		save.set("balance", Balance);
		
		String[] points = new String[ownedPoints.size()];
		
		for(int i = 0; i < ownedPoints.size(); i++) {
			
			points[i] = ownedPoints.get(i).getName();
		}
		
		save.set("points", points);
		
		String[] pls = new String[players.size()];
		
		for(int i = 0; i < players.size(); i++) {
			
			points[i] = players.get(i).getName();
		}
		
		save.set("players", pls);
	}
	public Mafia(String name,ChatColor col,int Balance) {
		
		this.name = name;
		this.color = col;
		this.Balance = Balance;
	}
	
	public boolean isIn(String name) {
		
		boolean isIn = false;
		for (GangPlayer pl : players) {
			
			if(pl.getName().equals(name)) {
				isIn = true;
				break;
			}
		}
		
		return isIn;
	}
	public GangPlayer getPlayer(String name) {
		
		for (GangPlayer p : players) {
			
			if(p.getName().equals(name))
				return p;
		}
		
		return null;
	}
	public void sendToAll(String ... text) {
		
		for (GangPlayer pl : players) {
			
			if(pl.isConnected()) {
				
				for (int i = 0; i < text.length; i++) {
					
					SendMafiaMessage(pl.p.getPlayer(), text[i]);
				}
				
			}
		}
	}
	public void SendMafiaMessage(CommandSender p,String ... text) {
		
		for (int i = 0; i < text.length; i++) {
			
			p.sendMessage(ChatColor.GREEN + "[Mafia " + name + "]" + ChatColor.DARK_PURPLE + text[i]);
		}
		
	}
	public void SendMafiaMessage(CommandSender p,String text) {
		
		p.sendMessage(ChatColor.GREEN + "[Mafia " + name + "]" + ChatColor.DARK_PURPLE + text);
	}
	public void PayAll() {
		
		for (GangPlayer pl : players) {
			
			if(pl.isConnected()) {
				
				SendMafiaMessage(pl.p.getPlayer(), "	  Tramitando cobro...");
				SendMafiaMessage(pl.p.getPlayer(), "---------------------------------");
			}
		}
		for (Point point : ownedPoints) {
			
			float pay = point.getBalance();
			String bonus= ChatColor.GRAY + "(0.00%)";
			
			sendToAll("Beneficios de " + point.getName() + ":",
					"-------------------------------------");
			
			float old = pay;
			if(point.getOperator().isConnected()) {
				
				
				pay*= 1 + point.getBonus();
				
				if(point.getBonus() > 0 ) {
					
					bonus= ChatColor.GREEN + "(" + (point.getBonus()*100) + "%)";
				}else {
					
					bonus= ChatColor.RED + "(" + (point.getBonus()*100) + "%)";
				}
				
				
				SendMafiaMessage(point.getOperator().p.getPlayer(), "Beneficios por ser operador de " + point.getName() + " recibes un " + ChatColor.GREEN + "30% " + ChatColor.DARK_PURPLE + "del total.");
				SendMafiaMessage(point.getOperator().p.getPlayer(), "Recibes " + ChatColor.GREEN + pay*.3f + eco.currencyNamePlural());
				
				point.getOperator().pay(pay*.3f);
				Gang.eco.depositPlayer(point.getOperator().p, pay * .3f);
			
			}
			
			sendToAll("Tramitando " + point.getName(),
					"Bonus por operaciones " + bonus, 
					"El operador actual es: " + point.getOperator().getName(),
					"Beneficios brutos: " + old + eco.currencyNamePlural(),
					"Beneficios netos: " + ChatColor.GREEN + pay + eco.currencyNamePlural());
			
			Balance += pay*.3f;
			float rp = pay*.4f;
			float total = 0;
			
			for (GangPlayer pl : players) {
			
				total += pl.lvl;
			}
			
			for (GangPlayer pl : players) {
				
				if(!pl.isConnected()) continue;
				
				float payI = rp * (pl.lvl/total);
				pl.pay(payI);
				Gang.eco.depositPlayer(pl.p,payI);
				SendMafiaMessage(pl.p.getPlayer(), "Recibes un " + ChatColor.GREEN + "(" + (pl.lvl/total)*100 + "%)" +ChatColor.DARK_PURPLE + " del 40% a repartir",
						"Recibes un " + ChatColor.GREEN + (.4f * (pl.lvl/total))*100 + "% " + ChatColor.DARK_PURPLE + "recibes " + ChatColor.GREEN + payI + eco.currencyNamePlural());
				
			}
		}
		
		for (GangPlayer pl : players) {
			
			if(pl.isConnected()) {
				
				SendMafiaMessage(pl.p.getPlayer(), "Sumatorio de todos los pagos: ");
				String[] list = pl.retrieveList();
				
				for (String string : list) {
					SendMafiaMessage(pl.p.getPlayer(), "	" + string);
				}
				
				SendMafiaMessage(pl.p.getPlayer(), "Total " + pl.Total());
				pl.clear();
			}
		}
		
	}
	
	public void addPoint(Point p,GangPlayer owner) {
		
		ownedPoints.add(p);
		p.own(this, owner);
	}
	public boolean addPlayer(GangPlayer p) {
		
		return addPlayer(p,false);
	}
	public boolean addPlayer(GangPlayer p,boolean anounce) {
		
		if(p.mafia != null) {
			
			return false;
		}
		
		players.add(p);
		p.setMafia(this);
		
		if(anounce)announceJoint(p);
		
		return true;
	}
	
	public void announceJoint(GangPlayer p) {
		
		SpigotPlugin.BroadCast(ChatColor.GOLD + "[" + ChatColor.GREEN + "BullNex Mafias" + ChatColor.GOLD + "]" + ChatColor.DARK_PURPLE + " " +
							  p.getName() + " se ha unido a la mafia " + name);
	}
}
