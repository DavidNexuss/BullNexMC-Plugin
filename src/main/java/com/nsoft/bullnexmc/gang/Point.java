package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;

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
	
	@Override public String getType() {	return "El Local";	}
	
	static final float range = 0.2f;
	private Material displayItem;
	private float bonus;
	
	private Location loc;
	
	static ArrayList<Point> points = new ArrayList<>();
	
	public Point(float defaultPay, String name, String displayItem, float bonus,Location loc) {
		
		super(name,defaultPay);
		this.displayItem = Material.getMaterial(displayItem);
		this.bonus = bonus;
		this.loc = loc;
		points.add(this);
		
	}
	
	@Override
	public void save(ConfigurationSection save) {
		
		super.save(save);
		save.set("display", displayItem.name());
		save.set("bonus", bonus);
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

	@Override
	public float getFinalBalance() {
		
		if(getOperator().isConnected())
			return super.getFinalBalance()*bonus;
		else
			return super.getFinalBalance();
	}

	@Override
	public boolean isAvaible() {
		
		return super.isAvaible() && getOperator() == null;
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

	@Override
	public void own(Mafia m, GangPlayer p) {
		// TODO Auto-generated method stub
		super.own(m, p);
		m.ownedPoints.add(this);
	}
}