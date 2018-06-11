package com.nsoft.bullnexmc.gang;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import com.nsoft.bullnexmc.SpigotPlugin;

public class BankUser {

	static HashMap<UUID, BankUser> BankUsersMap = new HashMap<>();
	static File BankFile;
	static FileConfiguration BankF;
	
	private static Location Withdraw;
	private static Location Deposit;
	private static float interest = 0.002f;
	
	private float bankmoney = 0;
	private Time lastOperation;
	private UUID playerUUID;
	
	public static void setNewWithdrawLocation(Location a) {
		
		Withdraw = new Location(a.getWorld(), a.getX(), a.getY(), a.getZ());
		BankF.set("WithdrawLOC", new Vector(a.getX(), a.getY(), a.getZ()));
	}
	
	public static void setNewDepositLocation(Location a) {
		
		Deposit = new Location(a.getWorld(), a.getX(), a.getY(), a.getZ());
		BankF.set("DepositLOC", new Vector(a.getX(), a.getY(), a.getZ()));
	}
	
	public static void setNewInterest(float interest) {
		
		BankUser.interest = interest;
		BankF.set("Interest", interest);
	}
	
	public BankUser(UUID uid,float bankmoney,Time lastoperation) {
		
		playerUUID = uid;
		this.bankmoney = bankmoney;
		this.lastOperation = lastoperation;
	}
	
	public void deposit(float deposit) {
		
		bankmoney = getBalanceWithInterest();
	}
	public float getBalance() {
		
		return bankmoney;
	}
	
	//TODO Time
	public float getBalanceWithInterest() {
		
		return getBalance() * (1 + interest);
	}
	
	public static void init() {
		
		BankFile = new File(SpigotPlugin.plugin.getDataFolder() , "bank.yml");
		
		boolean neew = false;
		if(!BankFile.exists()) {
			
			BankFile.getParentFile().mkdirs();
			SpigotPlugin.plugin.saveResource("bank.yml", false);
			neew = true;
		}
		
		BankF = new YamlConfiguration();
		
		if(neew) {
			

			BankF.set("WithdrawLOC", new Vector(0, 0, 0));
			BankF.set("DepositLOC", new Vector(0, 0, 0));
			BankF.set("Interest", 0.002);
			Map<UUID, List<?>> map = new HashMap<>();
			BankF.set("Bankusers", map);
		}
		try {
			
			BankF.load(BankFile);
			load();
		} catch (IOException | InvalidConfigurationException e) {
			
			e.printStackTrace();
		}
	}
	public static void save() {
		
		Map<UUID,List<?>> map = new HashMap<>();
		
		for (UUID uid : BankUsersMap.keySet()) {
			
			BankUser a = BankUsersMap.get(uid);
			
			ArrayList<Object> list = new ArrayList<>();
			list.add(uid);
			list.add(a.bankmoney);
			list.add(a.lastOperation);
			
			map.put(uid, list);
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
		float i = (float) BankF.getDouble("Interest");
		
		Withdraw = new Location(SpigotPlugin.plugin.getServer().getWorld("world"), w.getX(), w.getY(), w.getZ());
		Deposit = new Location(SpigotPlugin.plugin.getServer().getWorld("world"), d.getX(), d.getY(), d.getZ());
		interest = i;
		
		Map<UUID, List<?>> map = (Map<UUID, List<?>>) BankF.getMapList("Bankusers");
		
		for (UUID key : map.keySet()) {
			
			List a = map.get(key);
			UUID uid = (UUID) a.get(0);
			BankUsersMap.put(uid,new BankUser(uid,(float)a.get(1),(Time) a.get(2)));
		}
	}
	
}
