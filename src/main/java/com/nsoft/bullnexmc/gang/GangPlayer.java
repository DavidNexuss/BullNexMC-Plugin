package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;
import java.util.UUID;

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
public class GangPlayer implements DataField{
	
	@Override
	public String dataType() { return "players"; }
	
	private static int maxjoin = 5;
	private OfflinePlayer p;			  /** Reflaja un objto {@link OfflinePlayer} del jugador*/
	private Mafia mafia;				  /** Acceso a la mafia del jugador {@link Mafia}*/
	private int lvl;
	private int xp;
	private ArrayList<Float> pay = new ArrayList<>(); 
	
	private int joinCount = 0;
	
	private boolean masterBan = false;
	public GangPlayer(UUID uid,Mafia m,int lvl,int xp) {
		
		this.p = SpigotPlugin.plugin.getServer().getOfflinePlayer(uid);
		this.mafia = m;
		
		if(lvl == 0) lvl = 1;
		this.lvl = lvl;
		this.xp = xp;

		
	}
	
	@Override
	public void save(ConfigurationSection save) {
		
		save.set("level", lvl);
		save.set("xp", xp);
		save.set("isBanned", masterBan);
		save.set("join", joinCount);
	}
	
	public static GangPlayer create(ConfigurationSection sec,Mafia m) {
		
		GangPlayer a = new GangPlayer(Dictionary.getPlayerUUID(sec.getName()), 
						m, sec.getInt("level"), sec.getInt("xp"));
		a.joinCount = sec.getInt("join");
		if(sec.getBoolean("isBanned")) a.banProfile();
		return a;
	}
	void pay(float p) {
		
		pay.add(p);
	}
	
	public float Total() {
		
		float x = 0;
		
		for (Float float1 : pay) {
			
			x+= float1;
		}
		return x;
	}
	void clear() {
		
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
	void addXP(float nxp) {
		
		xp+= nxp;
		lvl = (int) (Math.sqrt(xp) + 1);
	}
	
	void setMafia(Mafia m) {
		
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
	 * Como método de seguridad se puede desactivar el perfil del jugador si este comete infracciones
	 * @return Devuelve true si el jugador esta conectado
	 */
	public boolean isConnected() { return p.isOnline() || isAble();}
	
	/**
	 * @return Devuelve el nombre del jugador
	 */
	@Override
	public String getName() {return p.getName();}

	/**
	 * Devuelve el nivel del jugador
	 * @return el nivel
	 */
	public int getLevel() { return lvl; }

	/**
	 * Devuelve la mafia de la que es participe, sinó esta en ninguna mafia devuelve null
	 * @return el objeto mafia, o null sinó esta en ninguna
	 */
	public Mafia getMafia() { return mafia; }

	/**
	 * Devuelve true si el jugador es partícipe en una mafia, false sinó lo es
	 * @return el estado del jugador
	 */
	public boolean isInMafia() {return mafia != null;}
	
	
	void tryJoin() {
		joinCount ++;
	}
	
	void resetJoinCount() {
		joinCount =0;
	}
	
	/**
	 * Establece el limite de veces que el jugador puede pedir ser de una mafia, 
	 * por defecto el máximo es de 5, si lo sobrepasa se le suspende el perfil {@link #banProfile()}
	 * 
	 * @param new_max_join El nuevo límite
	 */
	public void setMaxJoin(int new_max_join) {
		
		maxjoin = new_max_join;
	}
	private boolean isAble() {
		
		return (joinCount > maxjoin ? false : true ) || !masterBan;
	}
	
	/**
	 * Suspende el perfil de mafioso del jugador, para todo el plugin y todo lo referido a las mafias
	 * el jugador estará desconectado
	 */
	public void banProfile() {
		masterBan = true;
	}
	
	/**
	 * Desbanea el perfil {@link #banProfile()}
	 */
	public void unBanProfile() {
		masterBan = false;
	}
	
	/**
	 * Devuelve el status del jugador
	 * @return true si el perfil del jugador esta baneado
	 */
	public boolean isProfileBanned() {
		return masterBan;
	}

	/**
	 * Devuelve true si es administrador de una mafia y si esta conectado,
	 * se tiene en cuenta si su perfil esta suspeso
	 * @return
	 */
	public boolean isPromoted() {
		
		if(getMafia() != null) {
			
			return getMafia().isAdmin(this);
		}
		return false;
	}
}
