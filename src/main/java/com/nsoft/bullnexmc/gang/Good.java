package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.nsoft.bullnexmc.SpigotPlugin;

import net.milkbowl.vault.economy.Economy;

/**
 * Representa un bien material ya sea un local {@link Point}
 * @author DavidNexuss
 * @param 
 */
public abstract class Good implements DataField{

	@Override
	public String dataType() {return "good";}
	
	private String name;
	private int pay;
	private boolean linked = false;
	private static ArrayList<String> names = new ArrayList<>();
	
	private Mafia m;
	private GangPlayer operator;
	
	static ArrayList<Good> goods = new ArrayList<>();
	
	/**
	 * Constructor para crear un nuevo bien
	 * @param name El nombre del objeto
	 * @param pay Su coste o sus beneficios
	 */
	public Good(String name,int pay) {
		
		if(!isNameAvaible(name)) throw new IllegalArgumentException();
		this.name = name;
		this.pay = pay;
		goods.add(this);

	}
	
	//TODO: Check if that works
	public static <T> ArrayList<T> ownedGood(Mafia m,Class<T> c){
		
		ArrayList<T> ngoods = new ArrayList<>();
		for (Good good : goods) {
			
			if(good.m == m) {

				try {
					T v = (T)good;
					ngoods.add(v);
				} catch (Exception e) {
					
					continue;
				}
			}
				
		}	
		return ngoods;
	}

	public static ArrayList<Good> ownedGood(Mafia m){
		
		return (ArrayList<Good>) ownedGood(m,Good.class);
	}
	private boolean isNameAvaible(String name) {
		
		return !names.contains(name); 
	}
	/**
	 * Devuelve el valor del balance de base, es una función final que no se puede sobreescribir.
	 * Para modificar su valor se tiene que sobreescribir {@link #getFinalBalance() getFinalBalance}
	 * @return el balance
	 */
	public final int getBaseBalance() {return pay;};
	
	/**
	 * Devuelve el nombre del objeto, fancy
	 * @return el nombre
	 */
	public String getFancyName() {return ChatColor.GREEN + name + ChatColor.DARK_PURPLE;};
	
	/**
	 * Devuelve el nombre del objeto
	 * @return el nombre
	 */
	@Override
	public final String getName() { return name;}
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
	public int getFinalBalance() {return getBaseBalance();}
	
	@Override
	public void save(ConfigurationSection save) {
		
		save.set("type", getClass().getName());
		save.set("pay", pay);
		save.set("operator", getOperatorName());
	}
	
	public void link() {
	
		if(linked) return;
		own(getOperator().getMafia(), getOperator());
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
			
			return "" + ChatColor.GREEN + (int)getFinalBalance() + Gang.eco.currencyNamePlural();
		else
			return "" + ChatColor.RED + (int)getFinalBalance() + Gang.eco.currencyNamePlural();
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
	
	public String getOperatorName() {
		
		if(getOperator() == null) return null;
		else return getOperator().getName();
	}
	/**
	 * @return
	 */
	public boolean isAvaible() {
		
		return !isOwned();
	};
	
	public float getBuyPrice() { return Math.abs(pay)*20;}
	
	public void destroy() {
		
		goods.remove(this);
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		SpigotPlugin.BroadCast(getType() + ": " + getFancyName() + " ha sido eliminado.");
		super.finalize();
	}
}
