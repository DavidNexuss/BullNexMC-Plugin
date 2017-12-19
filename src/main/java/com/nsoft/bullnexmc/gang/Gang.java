package com.nsoft.bullnexmc.gang;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;

import net.milkbowl.vault.economy.Economy;

public class Gang {

	static Economy eco = null; 
	
	static ArrayList<Good> objects;
	
		static ArrayList<Point> points;
	
	static ArrayList<GangPlayer> players;
	static ArrayList<Mafia> mafias;
	
	
	static FileConfiguration Data;
	
	static JavaPlugin plugin;
	
	public static void init(FileConfiguration data, JavaPlugin p) {
		
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
	
	public static Good getGood(String goodName) {
		
		for (Good good : objects) {
			if(good.getName().equals(goodName)) {		
				return good;
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
	 * @throws Exception si no existe ningun jugador con ese nombre
	 */
	public static GangPlayer getGangPlayer(String player_name) throws Exception {
		
		if(plugin.getServer().getOfflinePlayer(player_name) != null) {
			
			return getGangPlayer(plugin.getServer().getOfflinePlayer(player_name));
		}else
			throw Errors.userNotFoundException(player_name);
		
	}
	
	
	//TODO: All commands!
	private static void initCommands(JavaPlugin p) {
		
		p.getCommand("mafia-ban").setExecutor(new MyComandExecutor("mafia-ban") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1 ) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.sendMessage(sender, "No tienes permiso para ejecutar este comando",2); return true;}
				
				try 				{	getGangPlayer(args[0]).banProfile();	}
				catch (Exception e) {	Errors.handleException(sender, e); 		}
				
				return true;
			}
		});
		
		p.getCommand("mafia-unban").setExecutor(new MyComandExecutor("mafia-unban") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1 ) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.sendMessage(sender, "No tienes permiso para ejecutar este comando",2); return true;}
				
				try 				{	getGangPlayer(args[0]).unBanProfile();	}
				catch (Exception e) {	Errors.handleException(sender, e); 		}
				
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
				
				try {
					
					GangPlayer p = getGangPlayer(sender.getName());
					getMafia(args[0]).askJoin(p);
				
				} catch (Exception e) {
					
					Errors.handleException(sender, e);
					return true;
				}

				
				return true;
			}
		});
		
		p.getCommand("mafia-accept").setExecutor(new MyComandExecutor("mafia-accept") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				try {
					GangPlayer player = getGangPlayer(sender.getName());
					
					if(player.isConnected()) {
						
						if(player.isInMafia()) {
							
							Mafia m = player.getMafia();
							if(!m.acceptJoin(args[0]))
								SpigotPlugin.sendMessage(sender, "Hubo un error admitiendo al jugador: " +args[0]);
						}else
							SpigotPlugin.sendMessage(sender, "No formas parte de ninguna mafia!",2);
					}
					
				} catch (Exception e) { Errors.handleException(sender, e);}
				
				return true;
			}
		});
		
		p.getCommand("mafia-force-join").setExecutor(new MyComandExecutor("mafia-force-join") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 2) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
				try {
					GangPlayer player = getGangPlayer(args[0]);
					if(player.isInMafia()) player.getMafia().leaveMafia(player,true);
					
					getMafia(args[1]).addPlayer(player);
					
					if(args != null && args[2].equals("true")) {
						
						getMafia(args[1]).promote(player, null, true);
					}
				} catch (Exception e) { Errors.handleException(sender, e);}
				
				return true;
			}
		});
		
		p.getCommand("mafia-buy").setExecutor(new MyComandExecutor("mafia-buy") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				try {
					GangPlayer player = getGangPlayer(sender.getName());
					if(player.isInMafia() && player.isPromoted()) {
						
						Good a = getGood(args[0]);
						if(a != null) {
							
							if(a.isAvaible()) {
								
								if(a.getBuyPrice() < player.getMafia().getBalance()) {
									
									a.setFreeAndOwn(player.getMafia(), null);
								}else
									SpigotPlugin.sendMessage(sender, "Tu mafia no tiene suficiente dinero.");
									SpigotPlugin.sendMessage(sender, "Dinero de tu mafia " + player.getMafia().getBalance() + eco.currencyNamePlural());
									SpigotPlugin.sendMessage(sender, "Precio de " + player.getMafia().getName() + ": " + a.getBuyPrice() + eco.currencyNamePlural());
							}else
								SpigotPlugin.sendMessage(sender, a.getType() + " " + a.getName() + " no esta disponible.");
						}else
							SpigotPlugin.sendMessage(sender, "No existe ningún bien en el server con el nombre: " + args[0],2);
					}
					
				} catch (Exception e) { Errors.handleException(sender, e);}
				
				return true;
			}
		});
		
		p.getCommand("mafia-buy-self").setExecutor(new MyComandExecutor("mafia-buy-self") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				try {
					GangPlayer player = getGangPlayer(sender.getName());
					if(player.isInMafia() && player.isConnected()) {
						
						Good a = getGood(args[0]);
						if(a != null) {
							
							if(a.isAvaible()) {
								
								if(a.getBuyPrice() < eco.getBalance(player.getOfflinePlayer())) {
									
									a.setFreeAndOwn(player.getMafia(), player);
									eco.withdrawPlayer(player.getOfflinePlayer(), a.getBuyPrice());
								}else
									SpigotPlugin.sendMessage(sender, "No tienes suficiente dinero.");
									SpigotPlugin.sendMessage(sender, "Tu dinero: " + eco.getBalance(player.getOfflinePlayer())+ eco.currencyNamePlural());
									SpigotPlugin.sendMessage(sender, "Precio de " + player.getMafia().getName() + ": " + a.getBuyPrice() + eco.currencyNamePlural());
							}else
								SpigotPlugin.sendMessage(sender, a.getType() + " " + a.getName() + " no esta disponible.");
						}else
							SpigotPlugin.sendMessage(sender, "No existe ningún bien en el server con el nombre: " + args[0],2);
					}
					
				} catch (Exception e) { Errors.handleException(sender, e);}
				
				return true;
			}
		});
		
		//TODO: Local creation protocol
		p.getCommand("mafia-create-local").setExecutor(new MyComandExecutor("mafia-create-local") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 5) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
				//TODO: Correct creation:
				
				try {
					
					Point a = new Point(Integer.parseInt(args[1]), args[0], null, .2f, getGangPlayer(sender.getName()).getPlayer().getLocation());
					//TODO Normal protocol;
					points.add(a);
					objects.add(a);
					
				} catch (Exception e) {
					
					SpigotPlugin.sendMessage(sender, "Error en los parametros");
				}
				return true;
			}
		});
		
		p.getCommand("mafia-test-pay").setExecutor(new MyComandExecutor("mafia-create-local") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
				Mafia a = getMafia(args[0]);
				if(a != null) {
					
					a.PayAll();
				}
				return true;
			}
		});

		p.getCommand("mafia-good-list").setExecutor(new MyComandExecutor("mafia-good-list") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				for (Good good : objects) {
					
					if(good.isOwned()) SpigotPlugin.sendMessage(sender, ChatColor.GREEN + "[Gang List] " + ChatColor.DARK_PURPLE + good.getType() + ": " + good.getName() + " de la mafia: " + good.getMafiaName());
					else SpigotPlugin.sendMessage(sender, ChatColor.GREEN + "[Gang List] " + ChatColor.DARK_PURPLE + good.getType() + ": " + good.getName() + " no pertenece a ninguna mafia");
				}
				return true;
			}
		});
		
		p.getCommand("mafia-mafia-list").setExecutor(new MyComandExecutor("mafia-mafia-list") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				for (Mafia m : mafias) {
					
					SpigotPlugin.sendMessage(sender,ChatColor.GREEN + "[Gang List] " + m.getFancyName() + ", balance: " + m.getBalance() + eco.currencyNamePlural());
				}
				return true;
			}
		});
	}
	/**
	 * Carga las mafias
	 * @param section La sección del YML que guarda todo lo relacionado con las Mafias
	 */
	static void load(ConfigurationSection section) {
		
		
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
