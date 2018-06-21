package com.nsoft.bullnexmc.gang;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import com.nsoft.bullnexmc.SpigotPlugin;

public class BankUser {

	static HashMap<String, BankUser> BankUsersMap = new HashMap<>();
	static File BankFile;
	static FileConfiguration BankF;
	
	private static Location Withdraw = SpigotPlugin.plugin.getServer().getWorld("world").getSpawnLocation();
	private static Location Deposit = SpigotPlugin.plugin.getServer().getWorld("world").getSpawnLocation();
	private static Location Create = SpigotPlugin.plugin.getServer().getWorld("world").getSpawnLocation();
	
	private static float interest = 0.002f;
	
	private float bankmoney = 0;
	private Date lastOperation;
	private String playerName;
	
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	
	public static Location getWithdrawLocation() {return Withdraw;}
	public static void setNewWithdrawLocation(Location a) {
		
		Withdraw = new Location(a.getWorld(), a.getX(), a.getY(), a.getZ());
		BankF.set("WithdrawLOC", new Vector(a.getX(), a.getY(), a.getZ()));
		save();
	}
	
	public static Location getDepositLocation() {return Deposit;}
	public static void setNewDepositLocation(Location a) {
		
		Deposit = new Location(a.getWorld(), a.getX(), a.getY(), a.getZ());
		BankF.set("DepositLOC", new Vector(a.getX(), a.getY(), a.getZ()));
		save();
	}
	
	public static Location getCreationLocation() {return Create;}
	public static void setNewCreationLocation(Location a) {
		
		Create = new Location(a.getWorld(), a.getX(), a.getY(), a.getZ());
		BankF.set("CreateLOC", new Vector(a.getX(), a.getY(), a.getZ()));
		save();
	}

	public static float getInterest() {return interest;}
	public static void setNewInterest(float interest) {
		
		BankUser.interest = interest;
		BankF.set("Interest", interest);
		save();
	}
	
	public static Date getCurrentDate() {
		
		Calendar p = Calendar.getInstance();
		return new Date(p.get(Calendar.YEAR)- 1900, p.get(Calendar.MONTH), 
				p.get(Calendar.DAY_OF_MONTH), p.get(Calendar.HOUR_OF_DAY), p.get(Calendar.MINUTE), p.get(Calendar.SECOND));
	}
	//--------OBJECT-------------
	public BankUser(String name,float bankmoney,Date lastoperation) {
		
		playerName = name;
		this.bankmoney = bankmoney;
		this.lastOperation = lastoperation;
	}
	
	public void deposit(float deposit) {
		
		bankmoney = getBalanceWithInterest();
		bankmoney += deposit;
		lastOperation = getCurrentDate();
		save();
	}
	
	public void withdraw(float wth) {
		
		bankmoney = getBalanceWithInterest();
		bankmoney -= wth;
		lastOperation = getCurrentDate();
		save();
	}
	public float getBalance() {
		
		return bankmoney;
	}
	
	//TODO Time
	public float getBalanceWithInterest() {
		
		return (float) (getBalance()* Math.pow(Math.E, interest* elapsed()/60f));
	}
	
	public float elapsed() {
		
		if(lastOperation == null)return 0;
		
		Calendar current = Calendar.getInstance();
		
		float melapsed = 0;
		
		float cyea = current.get(Calendar.YEAR);
		float lyea = lastOperation.getYear() + 1900;
		
		melapsed += 12*30*24*60*(cyea - lyea);
		
		float cmon = current.get(Calendar.MONTH);
		float lmon = lastOperation.getMonth();
		
		melapsed += 30*24*60*(cmon - lmon);
		
		float cday = current.get(Calendar.DAY_OF_MONTH);
		float lday = lastOperation.getDate();
		
		melapsed += 24*60*(cday - lday);
		
		float chour = current.get(Calendar.HOUR_OF_DAY);
		float dhour = lastOperation.getHours();
		
		melapsed += 60*(chour - dhour);
		
		float cminut = current.get(Calendar.MINUTE);
		float dminut = lastOperation.getMinutes();
		
		melapsed += (cminut - dminut);
		
		return melapsed;
	}
	
	public Date getLastOperation() {return lastOperation;}
	public static void init() {
		
		BankFile = new File(SpigotPlugin.plugin.getDataFolder() , "bank.yml");
		
		if(!BankFile.exists()) {
			
			try {
				BankFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		boolean neew = false;
		
		neew = BankFile.length() == 0;
		
		System.out.println("NEEEW  " + neew);
		BankF = new YamlConfiguration();
		
		if(neew) {
			

			BankF.set("WithdrawLOC", new Vector(0, 0, 0));
			BankF.set("DepositLOC", new Vector(0, 0, 0));
			BankF.set("CreateLOC", new Vector(0, 0, 0));
			BankF.set("Interest", 0.002);
			BankF.createSection("Bankusers");
			save();
		}
		try {
			
			BankF.load(BankFile);
			load();
		} catch (IOException | InvalidConfigurationException e) {
			
			e.printStackTrace();
		}
	}
	public static void save() {
		
		ConfigurationSection banks = BankF.createSection("Bankusers");
		
		for (String name : BankUsersMap.keySet()) {
			
			ConfigurationSection p = banks.createSection(name);
			p.set("balance", BankUsersMap.get(name).bankmoney);
			p.set("last", format.format(BankUsersMap.get(name).lastOperation));
			
		}
		try {
			BankF.save(BankFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void load() {
		
		Vector w = BankF.getVector("WithdrawLOC");
		Vector d = BankF.getVector("DepositLOC");
		Vector c = BankF.getVector("CreateLOC");
		float i = (float) BankF.getDouble("Interest");
		
		Withdraw = new Location(SpigotPlugin.plugin.getServer().getWorld("world"), w.getX(), w.getY(), w.getZ());
		Deposit = new Location(SpigotPlugin.plugin.getServer().getWorld("world"), d.getX(), d.getY(), d.getZ());
		Create = new Location(SpigotPlugin.plugin.getServer().getWorld("world"), c.getX(), c.getY(), c.getZ());
		
		interest = i;
		
		ConfigurationSection banks = BankF.getConfigurationSection("Bankusers");
		
		for(String name : banks.getKeys(false)) {
		     
			ConfigurationSection pl = banks.getConfigurationSection(name);
			
			try {
				Date dat = format.parse(pl.getString("last"));
				BankUser p = new BankUser(name, (float)pl.getDouble("balance"), dat);
				
				BankUsersMap.put(name, p);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	public String getLastOperationAsString() {
		
		return format.format(lastOperation);

	}
	
}
