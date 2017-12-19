package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Representa un bien material ya sea un local {@link Point}
 * @author DavidNexuss
 * @param 
 */
public abstract class Good implements Field {

	private String name;
	private float pay;
	
	private static ArrayList<String> names = new ArrayList<>();
	private Mafia m;
	private GangPlayer operator;
	
	/**
	 * Constructor para crear un nuevo bien
	 * @param name El nombre del objeto
	 * @param pay Su coste o sus beneficios
	 */
	public Good(String name,float pay) {
		
		if(!isNameAvaible(name)) throw new IllegalArgumentException();
		this.name = name;
		this.pay = pay;
	}
	
	private boolean isNameAvaible(String name) {
		
		return !names.contains(name); 
	}
	/**
	 * Devuelve el valor del balance de base, es una función final que no se puede sobreescribir.
	 * Para modificar su valor se tiene que sobreescribir {@link #getFinalBalance() getFinalBalance}
	 * @return el balance
	 */
	public final float getBaseBalance() {return pay;};
	
	/**
	 * Devuelve el nombre del objeto, fancy
	 * @return el nombre
	 */
	public String getFancyName() {return ChatColor.GREEN + name + ChatColor.DARK_PURPLE;};
	
	/**
	 * Devuelve el nombre del objeto
	 * @return el nombre
	 */
	public final String getBaseName() {return name;};
	
	/**
	 * Devuelve el nombre del objeto
	 * @return el nombre
	 */
	public String getMafiaName() {
		if(getMafia() != null)
			return ChatColor.GREEN + getMafia().name + ChatColor.DARK_PURPLE;
		return "";
	};
	
	
	/**
	 * Devuelve el balance neto/final del objeto, sean beneficios o coste
	 * Se puede sobreescribir según las necesidades
	 * @return el balance final
	 */
	public float getFinalBalance() {return getBaseBalance();}
	
	@Override
	public void save(ConfigurationSection save) {
		
		save.set("name", name);
		save.set("pay", pay);
		save.set("operator", operator.getName());
	}
	
	/**
	 * Devuelve el balance en un String con formato, por ejemplo:
	 * Si el balance es de 100 la funcion devolvera:
	 * 100[la moneda del servidor ej BNDs] -> 100BNDs
	 * Si el valor es negativo se devolvera el texto de color rojo, si es positivo verde
	 * @return el balance con formato.
	 */
	public String getChatBalance() {
		
		if(pay > 0) 
			
			return "" + ChatColor.GREEN + getFinalBalance() + Gang.eco.currencyNamePlural();
		else
			return "" + ChatColor.RED + getFinalBalance() + Gang.eco.currencyNamePlural();
	}
	
	/**
	 * Obtener punto
	 * @param m La mafia
	 * @param p El jugador
	 */
	public void own(Mafia m,GangPlayer p) {
		
		this.m = m;
		this.operator = p;
		
		getMafia().broadcast(getType() + " " + getFancyName() + " ha sido comprado/a por tu mafia");
		
	}
	
	public void setFree() {
		
		if(m != null) {
			
			getMafia().broadcast(getType() + " " + getFancyName() + " ha sido liberado!");
			this.m = null;
			this.operator = null;
		}
		
	}
	
	public void setFreeAndOwn(Mafia m,GangPlayer p) {
		
		setFree();
		own(m, p);
		
	}
	/**
	 * @return the mafia
	 */
	public Mafia getMafia() {
		return m;
	}
	
	/**
	 * @return true si este local pertenece ya pertenece a una mafia
	 */
	public final boolean isOwned() {return getMafia() != null;}

	public void setOperator(GangPlayer player) {
		
		if(getMafia().isIn(player.getName())) {
			
			this.operator = player;
		}
	}
	
	public abstract String getType();
	/**
	 * @return the operator
	 */
	public GangPlayer getOperator() {
		return operator;
	}
	
	public void quitOperator() {
		
		operator = null;
		getMafia().broadcast("El operador de " + getFancyName() + " ha sido revocado de sus funciones!");
	}
	/**
	 * @return
	 */
	public boolean isAvaible() {
		
		return !isOwned();
	};
	
	public float getBuyPrice() { return Math.abs(pay)*100;}
}
