package com.nsoft.bullnexmc;

import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.avaje.ebeaninternal.util.ValueUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Gang {

	public static Economy eco = null;
	public HashMap<Player, GangPlayer> gangs;
	public ArrayList<Mafia> mafias;
	public FileConfiguration Data;
	
	public Gang(FileConfiguration data) {
		
		if(!setupEconomy()) {
			
			SpigotPlugin.BroadCast(ChatColor.RED + "No se pudo iniciar el sistema de bandas, no se encontó vault.");
			return;
		}
		
		Data = data;
		ConfigurationSection mafias = Data.getConfigurationSection("mafias");
		ConfigurationSection players = Data.getConfigurationSection("mafias");
		
		for (String key : mafias.getKeys(false)) {
			
			Mafia a = new Mafia(Data.getString("mafias." + key + ".name"), ChatColor.valueOf("mafias." + key + ".color"),
					Data.getInt("mafias." + key + ".balance"));
			this.mafias.add(a);
			
			for (String keyP : mafias.getConfigurationSection(".players").getKeys(false)) {
				
				GangPlayer pl = new GangPlayer(mafias.getString(key + ".players." + keyP + "name"), a,
						mafias.getInt(key + ".players." + keyP + "level"),
						mafias.getInt(key + ".players." + keyP + "xp"));
				
				a.addPlayer(pl);
				
				
			}
			
			for (String keyP : mafias.getConfigurationSection(".points").getKeys(false)) {
				

				Point p = new Point((float)mafias.getDouble(key + ".points." + keyP + "pay"), 
									mafias.getString(key + ".points." + keyP + "name"),
									mafias.getString(key + ".points." + keyP + "display"), 
									(float)mafias.getDouble(key + ".points." + keyP + "bonus"));
				
				a.addPoint(p,a.getPlayer(mafias.getString(key + ".points." + keyP + "owner")));
			}
			
		}
	}
	
	public void save() {
		
		
	}
	private boolean setupEconomy() {
        if (SpigotPlugin.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = SpigotPlugin.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }
	static class Mafia{
		
		public String name;
		public int Balance;
		public ChatColor color;
		public ArrayList<GangPlayer> players;
		public ArrayList<Point> ownedPoints;
		
		public Mafia(String name,ChatColor col,int Balance) {
			
			this.name = name;
			this.color = col;
			this.Balance = Balance;
		}
		
		public GangPlayer getPlayer(String name) {
			
			for (GangPlayer p : players) {
				
				if(p.name.equals(name))
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
				
				float pay = point.pay();
				String bonus= ChatColor.GRAY + "(0.00%)";
				
				sendToAll("Beneficios de " + point.name + ":",
						"-------------------------------------");
				if(point.operator.isConnected()) {
					
					float old = pay;
					pay*= 1 + point.bonus;
					
					if(point.bonus > 0 ) {
						
						bonus= ChatColor.GREEN + "(" + (point.bonus*100) + "%)";
					}else {
						
						bonus= ChatColor.RED + "(" + (point.bonus*100) + "%)";
					}
					
					sendToAll("Tramitando " + point.name,
							"Bonus por operaciones " + bonus, 
							"El operador actual es: " + point.operator.name,
							"Beneficios brutos: " + old + eco.currencyNamePlural(),
							"Beneficios netos: " + ChatColor.GREEN + pay + eco.currencyNamePlural());
					SendMafiaMessage(point.operator.p.getPlayer(), "Beneficios por ser operador de " + point.name + " recibes un " + ChatColor.GREEN + "30% " + ChatColor.DARK_PURPLE + "del total.");
					SendMafiaMessage(point.operator.p.getPlayer(), "Recibes " + ChatColor.GREEN + pay*.3f + eco.currencyNamePlural());
					
					point.operator.pay(pay*.3f);
					Gang.eco.depositPlayer(point.operator.p, pay * .3f);
				
				}
				
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
								  p.name + " se ha unido a la mafia " + name);
		}
	}
	
	static class Point{
		
		static final float range = 0.2f;
		float defaultPay;
		String name;
		Material displayItem;
		float bonus;
		Mafia m;
		GangPlayer operator;
		
		public Point(float defaultPay, String name, String displayItem, float bonus) {
			super();
			this.defaultPay = defaultPay;
			this.name = name;
			this.displayItem = Material.getMaterial(displayItem);
			this.bonus = bonus;
		}
		
		public void own(Mafia m,GangPlayer p) {
			
			this.m = m;
			this.operator = p;
			
		}

		public void aplyDefaultAddition() {aplyAddition(0.02f);}
		public void aplyAddition(float add) {
			
			if(bonus < range)
				bonus += add;
		}
		public void aplyDefaultSubstract() {aplySubstract(0.005f);}
		public void aplySubstract(float sub) {
			
			if(bonus > -range)
			bonus -= sub;
			
		}
		public void setOperator(GangPlayer p) {
			
			this.operator = p;
		}
		public float pay() {
			
			return defaultPay* bonus;
		}
		
	}
	static class GangPlayer{
		
		String name;
		OfflinePlayer p;
		Mafia mafia;
		int lvl;
		int xp;
		ArrayList<Float> pay;
		public GangPlayer(String pName,Mafia m,int lvl,int xp) {
			
			this(SpigotPlugin.plugin.getServer().getOfflinePlayer(pName),m);
			if(lvl == 0) lvl = 1;
			this.lvl = lvl;
			this.xp = xp;
			pay = new ArrayList<>();
			
		}
		
		public void pay(float p) {
			
			pay.add(p);
		}
		
		public float Total() {
			
			float x = 0;
			
			for (Float float1 : pay) {
				
				x+= float1;
			}
			
			return x;
		}
		public void clear() {
			
			pay.clear();
		}
		public String[] retrieveList() {
			
			String[] list = new String[pay.size()];
					
			for (int i = 0; i < list.length; i++) {
				
				if(pay.get(i) > 0) {
					
					list[i] = ChatColor.GREEN + "" + pay.get(i) + eco.currencyNamePlural();
				}else {
					
					list[i] = ChatColor.RED + "" + pay.get(i) + eco.currencyNamePlural();
				}
				
			}
			
			return list;
		}
		public void addXP(float nxp) {
			
			xp+= nxp;
			lvl = (int) (Math.sqrt(xp) + 1);
		}
		public GangPlayer(OfflinePlayer p, Mafia m) {
			
			
			this.mafia = m;
			name = p.getName();
			this.p = p;
			
		}
		
		public void setMafia(Mafia m) {
			
			mafia = m;
		}
		
		public boolean isConnected() { return p.isOnline(); }
	}
}
