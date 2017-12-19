package com.nsoft.bullnexmc.gang;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
	private int Balance;
	public ChatColor color;
	
	public ArrayList<GangPlayer> players;
	private ArrayList<GangPlayer> promoteds;
	
	public ArrayList<GangPlayer> aplyForGangs = new ArrayList<>();
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
					
					SendMafiaMessage(pl.getPlayer(), text[i]);
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
				
				SendMafiaMessage(pl.getPlayer(), "	  Tramitando cobro...");
				SendMafiaMessage(pl.getPlayer(), "---------------------------------");
			}
		}
		for (Point point : ownedPoints) {
			
			float pay = point.getBaseBalance();
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
				
				
				SendMafiaMessage(point.getOperator().getPlayer(), "Beneficios por ser operador de " + point.getName() + " recibes un " + ChatColor.GREEN + "30% " + ChatColor.DARK_PURPLE + "del total.");
				SendMafiaMessage(point.getOperator().getPlayer(), "Recibes " + ChatColor.GREEN + pay*.3f + eco.currencyNamePlural());
				
				point.getOperator().pay(pay*.3f);
				Gang.eco.depositPlayer(point.getOperator().getOfflinePlayer(), pay * .3f);
			
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
			
				total += pl.getLevel();
			}
			
			for (GangPlayer pl : players) {
				
				if(!pl.isConnected()) continue;
				
				float payI = rp * (pl.getLevel()/total);
				pl.pay(payI);
				Gang.eco.depositPlayer(pl.getOfflinePlayer(),payI);
				SendMafiaMessage(pl.getPlayer(), "Recibes un " + ChatColor.GREEN + "(" + (pl.getLevel()/total)*100 + "%)" +ChatColor.DARK_PURPLE + " del 40% a repartir",
						"Recibes un " + ChatColor.GREEN + (.4f * (pl.getLevel()/total))*100 + "% " + ChatColor.DARK_PURPLE + "recibes " + ChatColor.GREEN + payI + eco.currencyNamePlural());
				
			}
		}
		
		for (GangPlayer pl : players) {
			
			if(pl.isConnected()) {
				
				SendMafiaMessage(pl.getPlayer(), "Sumatorio de todos los pagos: ");
				String[] list = pl.retrieveList();
				
				for (String string : list) {
					SendMafiaMessage(pl.getPlayer(), "	" + string);
				}
				
				SendMafiaMessage(pl.getPlayer(), "Total " + pl.Total());
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
		
		if(p.getMafia() != null) {
			
			return false;
		}
		
		players.add(p);
		p.setMafia(this);
		
		if(anounce)announceJoint(p);
		
		return true;
	}
	
	public void broadcast(String string) {

		for (GangPlayer player : getPlayers()) {
			
			if(player.isConnected()) {
				
				player.getPlayer().sendMessage(ChatColor.GREEN + "[" + getName() + "] " + ChatColor.DARK_PURPLE + string);
			}
		}
	}
	public void announceJoint(GangPlayer p) {
		
		SpigotPlugin.BroadCast(ChatColor.GOLD + "[" + ChatColor.GREEN + "BullNex Mafias" + ChatColor.GOLD + "]" + ChatColor.DARK_PURPLE + " " +
							  p.getName() + " se ha unido a la mafia " + name);
	}
	/**
	 * Devuelve el nombre de la mafia
	 * @return el nombre
	 */
	public String getName() {return name;}
	
	public String getFancyName() {return ChatColor.GREEN + getName() + ChatColor.DARK_PURPLE;}
	
	public GangPlayer getAspirantGang(String name) {
		
		for (GangPlayer gang : aplyForGangs) {
			
			if(gang.getName().equals(name)) {
				
				return gang;
			}
		}
		
		return null;
	}
	public boolean removeOperator(GangPlayer player) {
		
		boolean isOperator = false;
		if(player.getMafia() != this) return false;
		
		for (Point p : getPoints()) {	
			if(p.getOperator() == player) {
				
				p.quitOperator();
				isOperator = true;
			}
		}
		return isOperator;
	}
	public boolean leaveMafia(GangPlayer p) { return leaveMafia(p, false);}
	
	public boolean leaveMafia(GangPlayer p,boolean force) {
		
		if(p.getMafia() != this || (!p.isConnected() && !force)) return false;
		
		getPlayers().remove(p);
		removeOperator(p);
		p.setMafia(null);
		
		return true;
		
	}
	//TODO: Ask schedule
	public boolean askJoin(GangPlayer p) {
		
		if(p.getMafia() != this || !p.isConnected() || aplyForGangs.contains(p)) return false;
		p.tryJoin();
		aplyForGangs.add(p);
		return true;
	}
	
	//TODO: La gente que no este conectada deberia poder ser aceptada?? linea 307
	/**
	 * Acepta la unión de un nuevo miembro a la mafia
	 * Devolvera false si:
	 * 
	 * 		Si no hay ningun jugador con el nombre especificado que este en la lista
	 * 		Si ese jugador, por error, ya este en una mafia, se borrara su nombre de la lista
	 * 
	 * @param personaName el nombre de la persona
	 * @return true si todo funciona bien
	 */
	public boolean acceptJoin(String personaName) {
		
		GangPlayer p = getAspirantGang(personaName);
		if(p == null) return false;
		
		if(p.isInMafia() || p.isProfileBanned()) {
			
			aplyForGangs.remove(p);
			return false;
		}
		
		addPlayer(p, true);
		
		return true;
	}
	
	public ArrayList<GangPlayer> getPromoteds() {
		
		return (ArrayList<GangPlayer>) promoteds.clone();
	}
	
	public boolean isAdmin(GangPlayer p) {
		
		boolean isPromoted = false;
		for (GangPlayer pl : promoteds) {
			
			if(p == pl) {
				
				isPromoted = true;
				break;
			}
		}
		
		return isPromoted && p.isConnected();
	}
	public void promote(GangPlayer player,GangPlayer promoted,boolean force) {
		
		if(player.getMafia() != this) return;
		if(force) {
			promoteds.add(player);
			return;
		}
		
		boolean ready = force;
		if(!ready) {
			
			for (GangPlayer p : promoteds) {
				
				if(p == promoted) {
					
					ready = true;
					break;
				}
			}
		}
		
		if(ready) {
			
			promoteds.add(player);
		}
	}
	public void buy(Good object,GangPlayer player) {
		
		if(player.isConnected() && object.isAvaible()) 
			
			if(getBalance() > object.getBaseBalance()) {
				
				object.own(this, player);
			}
		
	}
	
	public int getBalance() {return Balance;}
	public static enum Protocol{ALREADY_IN_MAFIA}
	
	
}
