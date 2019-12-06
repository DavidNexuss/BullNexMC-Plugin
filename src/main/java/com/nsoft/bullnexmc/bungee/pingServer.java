package com.nsoft.bullnexmc.bungee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.nsoft.bullnexmc.SpigotPlugin;

public class pingServer {

	static String url = "jdbc:mysql://remotemysql.com:3306/BqlOcNnU0u?useSSL=false";
	static String user = "BqlOcNnU0u";
	static String passwd = "DnrwMEbjYs";
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
