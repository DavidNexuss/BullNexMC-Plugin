package com.nsoft.bullnexmc.bungee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.nsoft.bullnexmc.SpigotPlugin;

public class SQLFactory {

	static String url = "jdbc:mysql://remotemysql.com:3306/dnlcatZsZQ?useSSL=false";
	static String user = "dnlcatZsZQ";
	static String passwd = "R4DkDMYmbg";
	static String name = "Local";
	static String ip = "localhost";
	
	public static void ping() {
		
		try {
			Connection conn = DriverManager.getConnection(url, user, passwd);
			conn.createStatement().executeUpdate("UPDATE servers SET last_connect = 240 WHERE Name='" + name + "';");
			SpigotPlugin.plugin.getLogger().log(Level.INFO, "Updated ping");
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpigotPlugin.plugin.getServer().getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{ping();}, 8200);
		
	}
	public static void updatePlayer(Player player) { 
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url,user,passwd);
			ResultSet set = conn.createStatement().executeQuery("SELECT * FROM player_list;");
			java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");  
			String strDate = dateFormat.format(date);  
			
			boolean send = true;
			while(set.next() && !send) {
				send = !set.getString("name").equals(player.getName());
			}
			
			if (send) {
				conn.createStatement().executeUpdate("UPDATE player_list SET last_connect='" + 
						strDate + "' WHERE name='" + player.getName() + "';");
			}else {
				conn.createStatement().executeUpdate(
				"INSERT INTO player_list(name,uuid,last_connect) VALUES('" + player.getName() + "','" 
				+ player.getUniqueId() + "','" + strDate + "');"
				);
			}
			
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void start() {
		
		ip = System.getenv("NGROKIP");
		
		if(System.getenv("SERVER_NAME") != null) name = System.getenv("SERVER_NAME");
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, user, passwd);
			ResultSet set = conn.createStatement().executeQuery("SELECT * FROM servers;");
			boolean send = true;
			while(set.next()) {
				send = !set.getString("Name").equals(name);
			}
			
			if(send) {
				
				SpigotPlugin.plugin.getLogger().log(Level.INFO, "Creating row for this server.");
				conn.createStatement().executeUpdate("INSERT INTO servers(Name,IP) VALUES('" + name + "','" + ip + "');");
			}
			conn.close();
	//		SpigotPlugin.plugin.getServer().getScheduler().runTaskLater(SpigotPlugin.plugin, ()->{ping();}, 1200);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
