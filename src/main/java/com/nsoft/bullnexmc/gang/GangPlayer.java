package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.nsoft.bullnexmc.SpigotPlugin;
import static com.nsoft.bullnexmc.gang.Gang.*;


/**
 * Clase que describe valores adicionales para cada jugador, como la mafia en la que esta, el nivel dentro de esta, la experiencia...
 * @author Usuari
 *
 */
public class GangPlayer implements Field{
	
	private OfflinePlayer p;			  /** Reflaja un objto {@link OfflinePlayer} del jugador*/
	private Mafia mafia;				  /** Acceso a la mafia del jugador {@link Mafia}*/
	private int lvl;
	private int xp;
	private ArrayList<Float> pay; 
	
	public GangPlayer(String pName,Mafia m,int lvl,int xp) {
		
		this(SpigotPlugin.plugin.getServer().getOfflinePlayer(pName),m);
		if(lvl == 0) lvl = 1;
		this.lvl = lvl;
		this.xp = xp;
		pay = new ArrayList<>();
		
	}
	
	@Override
	public void save(ConfigurationSection save) {
		
		save.set("name", getName());
		save.set("level", lvl);
		save.set("xp", xp);
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
		this.p = p;
		
	}
	public void setMafia(Mafia m) {
		
		mafia = m;
	}
	
	/**
	 * Devuelve el objeto {@link OfflinePlayer} que refleja este jugador
	 * @return el jugador
	 */
	public OfflinePlayer getOfflinePlayer() {return p;}
	
	/**
	 * Devuelve el objeto {@link Player} que refleja este jugador, si no esta conectado devuelve null
	 * @return el jugador, null si no esta conectado
	 */
	public Player getPlayer() {return getOfflinePlayer().getPlayer();}
	/**
	 * @return Devuelve true si el jugador esta conectado
	 */
	public boolean isConnected() { return p.isOnline(); }
	/**
	 * @return Devuelve el nombre del jugador
	 */
	public String getName() {return p.getName();}

	/**
	 * Devuelve el nivel del jugador
	 * @return el nivel
	 */
	public float getLevel() { return lvl; }

	/**
	 * Devuelve la mafia de la que es participe, sinó esta en ninguna mafia devuelve null
	 * @return el objeto mafia, o null sinó esta en ninguna
	 */
	public Mafia getMafia() { return mafia; }

}
