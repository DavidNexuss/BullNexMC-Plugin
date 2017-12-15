package com.nsoft.bullnexmc.gang;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * 
 * Describe un local en el mundo
 * @author DavidNexuss
 *
 */
public class Point extends Good{
	
	static final float range = 0.2f;
	private Material displayItem;
	private float bonus;
	
	private Location loc;
	
	private Mafia m;
	private GangPlayer operator;
	
	public Point(float defaultPay, String name, String displayItem, float bonus,Location loc) {
		
		super(name,defaultPay);
		this.displayItem = Material.getMaterial(displayItem);
		this.bonus = bonus;
		this.loc = loc;
		
	}
	
	@Override
	public void save(ConfigurationSection save) {
		
		super.save(save);
		save.set("display", displayItem.name());
		save.set("bonus", bonus);
		save.set("operator", operator.getName());
		save.set("location.x", loc.getBlockX());
		save.set("location.y", loc.getBlockY());
		save.set("location.z", loc.getBlockZ());
	}
	/**
	 * Crea la estructura virtual del local
	 * @param p El cliente que recibira la actualizacion
	 */
	public void sendPacket(Player p) {
		
		if(isOwned()) { 
			
			if(getMafia().isIn(p.getName()))  //Your mafia owns this local
				
				createStructure(p, Case.YOURS);
			else  //Another mafia owns this local
				
				createStructure(p, Case.ENEMY);
			
		}else  //Nobody owns this local
			
			createStructure(p, Case.YOURS);

	}
	private enum Case{YOURS,ENEMY,NOBODY}
	
	//TODO: Finish this infernal method
	private void createStructure(Player p,Case c) {
		
		GangPlayer pl = Gang.getGangPlayer(p);
		
		if(c == Case.NOBODY) {
			
			
			p.sendBlockChange(getLocation(), Material.WOOL, (byte) 1);
		}
		else if(c == Case.ENEMY) {}
		else if(c == Case.YOURS) {}
	}
	/**
	 * Obtener punto
	 * @param m La mafia
	 * @param p El jugador
	 */
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
		
		if(bonus < 0)
			bonus = 0;
	}
	public void setOperator(GangPlayer p) {
		
		this.operator = p;
	}

	/**
	 * @return the displayItem
	 */
	public Material getDisplayItem() {
		return displayItem;
	}

	/**
	 * @return the bonus
	 */
	public float getBonus() {
		return bonus;
	}

	/**
	 * @return the Location
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * @return the mafia
	 */
	public Mafia getMafia() {
		return m;
	}

	/**
	 * @return the operator
	 */
	public GangPlayer getOperator() {
		return operator;
	}
	
	public void quitOperator() {
		
		operator = null;
	}
	
	public boolean isAvaible() {
		
		return getMafia() == null && getOperator() == null;
	}
	@Override
	public float getFinalBalance() {
		
		if(operator.isConnected())
			return super.getFinalBalance()*bonus;
		else
			return super.getFinalBalance();
	}
	/**
	 * @return true si este local pertenece ya pertenece a una mafia
	 */
	public boolean isOwned() {return m == null;}

	
}