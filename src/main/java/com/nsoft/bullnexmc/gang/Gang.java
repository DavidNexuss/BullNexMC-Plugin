package com.nsoft.bullnexmc.gang;

import java.nio.channels.GatheringByteChannel;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebeaninternal.util.ValueUtil;
import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Gang {

	static Economy eco = null; 
	
	static ArrayList<Point> points;
	static ArrayList<GangPlayer> players;
	static ArrayList<Mafia> mafias;
	
	static FileConfiguration Data;
	
	static JavaPlugin plugin;
	
	static void init(FileConfiguration data, JavaPlugin p) {
		
		plugin = p;
		
		if(!setupEconomy()) {
			
			SpigotPlugin.BroadCast(ChatColor.RED + "No se pudo iniciar el sistema de bandas, no se encontó vault.");
			return;
		}
		
		Data = data;
		load(Data.getConfigurationSection("gang"));
		initCommands(p);
	}
	/**
	 * Devuelve una mafia por su nombre
	 * @param name el nombre de la mafia
	 * @return la mafia, si no hay ninguna mafia con este nombre, devuelve null
	 */
	public static Mafia getMafia(String name) {
		
		for (Mafia mafia : mafias) {
			if(mafia.getName().equals(name)) {
				return mafia;
			}
		}
		return null;
	}
	/**
	 * Transforma {@link OfflinePlayer} a {@link GangPlayer} si es posible, sinó crea un nuevo perfil
	 * @param Player el objeto {@link OfflinePlayer} que refleja el jugador deseado
	 * @return Su objeto {@link GangPlayer}
	 */
	public static GangPlayer getGangPlayer(OfflinePlayer player) {
		
		for (GangPlayer p : players) {
			if(p.getOfflinePlayer() == player) {
				return p;
			}
		}
		
		GangPlayer a = new GangPlayer(player.getName(), null, 1, 0);
		getPlayers().add(a);
		return a;
	}
	/**
	 * @see Gang#getGangPlayer(OfflinePlayer)
	 * @param player
	 * @return Su objeto {@link GangPlayer}
	 */
	public static GangPlayer getGangPlayer(Player player) {return getGangPlayer(plugin.getServer().getOfflinePlayer(player.getName()));}
	
	/**
	 * @see Gang#getGangPlayer(OfflinePlayer)
	 * @param player
	 * @return Su objeto {@link GangPlayer}
	 */
	public static GangPlayer getGangPlayer(String player_name) {return getGangPlayer(plugin.getServer().getOfflinePlayer(player_name));}
	
	
	
	//TODO: All commands!
	private static void initCommands(JavaPlugin p) {
		
		p.getCommand("mafia-ban").setExecutor(new MyComandExecutor("mafia-ban") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				// TODO Auto-generated method stub
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1 ) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.sendMessage(sender, "No tienes permiso para ejecutar este comando",2); return true;}
				
				getGangPlayer(args[0]).banProfile();
				return true;
			}
		});
		
		p.getCommand("mafia-unban").setExecutor(new MyComandExecutor("mafia-unban") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				// TODO Auto-generated method stub
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1 ) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.sendMessage(sender, "No tienes permiso para ejecutar este comando",2); return true;}
				
				getGangPlayer(args[0]).unBanProfile();
				return true;
			}
		});
		p.getCommand("mafia-create").setExecutor(new MyComandExecutor("mafia-create") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

				if(!super.onCommand(sender, command, label, args)) return true;
					
					if(!sender.isOp()) SpigotPlugin.NotOPMessage(sender); else {
						
						if(args.length != 2) { SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2);return true;}
						if(ChatColor.valueOf(args[1]) == null) {SpigotPlugin.sendMessage(sender, "Color invalido: " + args[1],2); return true;} //TODO: Check Enum.valueOf()
						
						Mafia m = new Mafia(args[0], ChatColor.valueOf(args[1]), 0);
					}
				
				return true;
			}
		});
		
		p.getCommand("mafia-join").setExecutor(new MyComandExecutor("mafia-join") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				GangPlayer p = getGangPlayer(sender.getName());

				getMafia(args[0]).askJoin(p);
				return true;
			}
		});
	}
	/**
	 * Carga las mafias
	 * @param section La sección del YML que guarda todo lo relacionado con las Mafias
	 */
	public static void load(ConfigurationSection section) {
		
		
	}
	/**
	 * Guarda todo los objtos relacionados con el plugin en un YML
	 */
	public static void save() {
		
		for (int i = 0; i < points.size(); i++) {
			
			points.get(i).save(Data.getConfigurationSection("points." + i));
		}
		for (int i = 0; i < players.size(); i++) {
	
			players.get(i).save(Data.getConfigurationSection("players." + i));
		}
		for (int i = 0; i < mafias.size(); i++) {
			
			mafias.get(i).save(Data.getConfigurationSection("mafias." + i));
		}
	}
	
	private static boolean setupEconomy() {
        if (SpigotPlugin.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = SpigotPlugin.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        eco = rsp.getProvider();
        return eco != null;
    }

	/**
	 * @return Todos los locales
	 */
	public static ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 * @return Todos los jugadores
	 */
	public static ArrayList<GangPlayer> getPlayers() {
		return players;
	}

	/**
	 * @return Todas las mafias
	 */
	public static ArrayList<Mafia> getMafias() {
		return mafias;
	}
	
	
}
