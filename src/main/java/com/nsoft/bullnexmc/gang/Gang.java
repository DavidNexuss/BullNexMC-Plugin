package com.nsoft.bullnexmc.gang;

import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;

import net.milkbowl.vault.economy.Economy;

public class Gang {

	static Economy eco = null; 
	
	static ArrayList<GangPlayer> players = new ArrayList<>();
	static ArrayList<Mafia> mafias = new ArrayList<>();
	
	
	static FileConfiguration Data;
	
	static JavaPlugin plugin;
	
	public static void init(FileConfiguration data, JavaPlugin p) {
		
		plugin = p;
		
		if(!setupEconomy()) {
			
			SpigotPlugin.BroadCast(ChatColor.RED + "No se pudo iniciar el sistema de bandas, no se encontó vault.");
			return;
		}else
			SpigotPlugin.BroadCast(ChatColor.YELLOW + "Vault encontrado!");
		
		Data = data;
		load(Data.getConfigurationSection("gang"));
		initCommands(p);
		Dictionary.init();
		Mafia.payThread.start();
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
		
		for (Good good : Good.goods) {
			if(good.getBaseName().equals(goodName)) {		
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
			
			if(p.getOfflinePlayer().getUniqueId().toString().equals(player.getUniqueId().toString())) {
				
				return p;
			}
		}
		
		GangPlayer a = new GangPlayer(player.getUniqueId(), null, 1, 0);
		players.add(a);
		return a;
	}
	/**
	 * @see Gang#getGangPlayer(OfflinePlayer)
	 * @param player
	 * @return Su objeto {@link GangPlayer}
	 */
	public static GangPlayer getGangPlayer(Player player) {
		return getGangPlayer(plugin.getServer().getOfflinePlayer(player.getUniqueId()));
	}
	
	/**
	 * @see Gang#getGangPlayer(OfflinePlayer)
	 * @param player
	 * @return Su objeto {@link GangPlayer}
	 * @throws Exception si no existe ningun jugador con ese nombre
	 */
	public static GangPlayer getGangPlayer(String player_name){
		

		if(plugin.getServer().getOfflinePlayer(Dictionary.getPlayerUUID(player_name)) != null) {
			
			return getGangPlayer(plugin.getServer().getOfflinePlayer(Dictionary.getPlayerUUID(player_name)));
		}else
			return null;
		
	}
	
	
	//TODO: All commands!
	private static void initCommands(JavaPlugin p) {
		
		p.getCommand("mafia-ban").setExecutor(new MyComandExecutor("mafia-ban") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1 ) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.sendMessage(sender, "No tienes permiso para ejecutar este comando",2); return true;}
				
				try 				{	getGangPlayer(args[0]).banProfile();	SpigotPlugin.sendMessage(sender, "Perfil suspendido.");}
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
				
				try 				{	getGangPlayer(args[0]).unBanProfile();	SpigotPlugin.sendMessage(sender, "Perfil desbaneado");}
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
						
						
						ChatColor c = ChatColor.BLACK;
						try {
							
							c = ChatColor.valueOf(args[1]);
						} catch (Exception e) {
							
							SpigotPlugin.sendMessage(sender, "Este color no existe! " + args[1]);
							return true;
						}
						
						Mafia m = new Mafia(args[0], c, 0);
						mafias.add(m);
						SpigotPlugin.sendMessage(sender, "Mafia creada con éxito!");
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
					SpigotPlugin.sendMessage(sender, "Solicitud enviada");
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
					if(player == null) SpigotPlugin.sendMessage(sender, "Jugador == null",2);
					if(player.isConnected()) {
						
						if(player.isInMafia()) {
							
							Mafia m = player.getMafia();
							if(!m.acceptJoin(args[0]))
								SpigotPlugin.sendMessage(sender, "Hubo un error admitiendo al jugador: " +args[0]);
							else
								SpigotPlugin.sendMessage(sender, "JUgador admitido.");
						}else
							SpigotPlugin.sendMessage(sender, "No formas parte de ninguna mafia!",2);
					}
					
				} catch (Exception e) { Errors.handleException(sender, e);}
				
				return true;
			}
		});
		p.getCommand("mafia").setExecutor(new MyComandExecutor("mafia") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				GangPlayer p = getGangPlayer(sender.getName());
				if(p.isInMafia()) {
					
					SpigotPlugin.sendMessage(sender, "Tu mafia es: " + p.getMafia().getFancyName());
					if(p.isPromoted()) {
						SpigotPlugin.sendMessage(sender, "Eres administrador de tu mafia");
					}
					Mafia m = p.getMafia();
					m.SendMafiaMessage(sender,ChatColor.GOLD +  "Lista de jugadores:");
					
					for (GangPlayer gang : p.getMafia().players) {
						
						if(gang.isPromoted() && gang.isConnected())
							m.SendMafiaMessage(sender, "   -" + ChatColor.GOLD + gang.getName() + ChatColor.DARK_PURPLE + ", nivel: " + gang.getLevel());
						else if(gang.isConnected())
							m.SendMafiaMessage(sender, "   -" + ChatColor.GREEN + gang.getName() + ChatColor.DARK_PURPLE + ", nivel: " + gang.getLevel());
						else
							m.SendMafiaMessage(sender, "   -" + ChatColor.GRAY + gang.getName() + ChatColor.DARK_PURPLE + ", nivel: " + gang.getLevel());
					}
					
					ArrayList<Good> goods = Good.ownedGood(m);
					if(goods.size() != 0) {
						
						m.SendMafiaMessage(sender, ChatColor.GOLD + "Lista de bienes:");
						for (Good good : goods) {
							
							
							if(good instanceof Locable) {
								
								Locable l = (Locable)good;
								Location l2 = p.getPlayer().getLocation();
								
								int dist = (int) Math.sqrt(		
										(l.getX() - l2.getBlockX())^2 + 
										(l.getY() - l2.getBlockY())^2 + 
										(l.getZ() - l2.getBlockZ())^2		);
								
								m.SendMafiaMessage(sender, "   -" + good.getType() + ": " + good.getFancyName() + ", " + good.getChatBalance() + ", " + 
								ChatColor.GOLD + dist + " bloques");
								
							}else {
								
								m.SendMafiaMessage(sender, "   -" + good.getType() + ": " + good.getFancyName() + ", " + good.getChatBalance());
								
							}
						}
					}
				}else {
					
					SpigotPlugin.sendMessage(sender, "No estas en ninguna mafia.",2);
				}
				return true;
			}
		});
		p.getCommand("mafia-force-join").setExecutor(new MyComandExecutor("mafia-force-join") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 3) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
			
					GangPlayer player;
					player = getGangPlayer(args[0]);
					if(player == null) SpigotPlugin.sendMessage(sender, "Jugador == null",2);
					if(player.isInMafia()) player.getMafia().leaveMafia(player,true);
					if(getMafia(args[1]) == null)SpigotPlugin.sendMessage(sender, "Mafia == null",2);
					getMafia(args[1]).addPlayer(player);
					
					if(args[2].equals("true")) {
						
						getMafia(args[1]).promote(player, null, true);
					}
					
					SpigotPlugin.sendMessage(sender, "Jugador unido.");
				
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
									SpigotPlugin.sendMessage(sender, "Bien comprado");
								}else
									SpigotPlugin.sendMessage(sender, "Tu mafia no tiene suficiente dinero.");
									SpigotPlugin.sendMessage(sender, "Dinero de tu mafia " + player.getMafia().getBalance() + eco.currencyNamePlural());
									SpigotPlugin.sendMessage(sender, "Precio de " + player.getMafia().getName() + ": " + a.getBuyPrice() + eco.currencyNamePlural());
							}else
								SpigotPlugin.sendMessage(sender, a.getType() + " " + a.getFancyName() + " no esta disponible.");
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
									SpigotPlugin.sendMessage(sender, "Bien comprado.");
									SpigotPlugin.sendMessage(sender, "Te quedan: " + eco.getBalance(player.getOfflinePlayer())  + eco.currencyNamePlural());
								}else {
									SpigotPlugin.sendMessage(sender, "No tienes suficiente dinero.");
									SpigotPlugin.sendMessage(sender, "Tu dinero: " + eco.getBalance(player.getOfflinePlayer())+ eco.currencyNamePlural());
									SpigotPlugin.sendMessage(sender, "Precio de " + a.getFancyName() + ": " + a.getBuyPrice() + eco.currencyNamePlural());
								}
							}else
								SpigotPlugin.sendMessage(sender, a.getType() + " " + a.getFancyName() + " no esta disponible.");
						}else
							SpigotPlugin.sendMessage(sender, "No existe ningún bien en el server con el nombre: " + args[0],2);
					}else {
						
						SpigotPlugin.sendMessage(sender, "No formas parte de ninguna mafia",2);
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
					
					new Point(Integer.parseInt(args[1]), args[0], "DIAMOND_SWORD", .2f, getGangPlayer(sender.getName()).getPlayer().getLocation());
					
					
					SpigotPlugin.sendMessage(sender, "Local creado.");
				
				return true;
			}
		});
		
		p.getCommand("mafia-test-pay").setExecutor(new MyComandExecutor("mafia-test-pay") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) {SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2); return true;}
				
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
				Mafia a = getMafia(args[0]);
				if(a != null) {
					
					a.PayAll();
					SpigotPlugin.sendMessage(sender, "Paga dev realizada.");
				}
				return true;
			}
		});

		p.getCommand("mafia-good-list").setExecutor(new MyComandExecutor("mafia-good-list") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				for (Good good : Good.goods) {
					
					if(good.isOwned()) SpigotPlugin.sendMessage(sender, ChatColor.BLUE + "[Gang List] " + ChatColor.DARK_PURPLE + good.getType() + ": " + good.getFancyName() + " de la mafia: " + good.getMafiaName());
					else SpigotPlugin.sendMessage(sender, ChatColor.BLUE + "[Gang List] " + ChatColor.DARK_PURPLE + good.getType() + ": " + good.getFancyName() + " no pertenece a ninguna mafia, " + good.getBuyPrice());
				}
				
				if(Good.goods.size() == 0) SpigotPlugin.sendMessage(sender, "No hay bienes.");
				return true;
			}
		});
		
		p.getCommand("mafia-force-save").setExecutor(new MyComandExecutor("mafia-force-save") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
				save();
				SpigotPlugin.sendMessage(sender, "Configuración guardada");
				return true;
			}
		});
		
		p.getCommand("mafia-mafia-list").setExecutor(new MyComandExecutor("mafia-mafia-list") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				for (Mafia m : mafias) {
					
					SpigotPlugin.sendMessage(sender,ChatColor.BLUE + "[Gang List] " + m.getFancyName() + ", balance: " + (int)m.getBalance() +" " +eco.currencyNamePlural());
				}
				
				if(mafias.size() == 0) SpigotPlugin.sendMessage(sender, "No hay mafias.");
				return true;
			}
		});
		
		p.getCommand("mafia-promote").setExecutor(new MyComandExecutor("mafia-promote") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2);
				
				GangPlayer p = getGangPlayer(sender.getName());
				
				if(!p.isConnected()) { SpigotPlugin.sendMessage(sender, "Tienes el perfil suspendido!",2); return true;}
				
				if(p.isInMafia()) {
					
					if(p.isPromoted()) {
						
						GangPlayer o = p.getMafia().getPlayer(args[0]);
						
						if(o != null) {
							
							p.getMafia().promote(o, p, true);
							SpigotPlugin.sendMessage(sender, "El jugador " + args[0] + " ha sido puestoo como admin de la mafia");
						}else {
							
							SpigotPlugin.sendMessage(sender, "No existe ningun jugador con el nombre: " + args[0],2);
						}
					}else {
						
						SpigotPlugin.sendMessage(sender, "No tienes nivel suficiente para realizar esta operación!",2);
					}
				}else {
					
					SpigotPlugin.sendMessage(sender, "No eres miembro de ninguna mafia!",2);
				}
				return true;
			}
		});
		
		p.getCommand("mafia-force-promote").setExecutor(new MyComandExecutor("mafia-force-promote") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2);
				
				GangPlayer p = getGangPlayer(args[0]);
				
				if(!p.isConnected()) { SpigotPlugin.sendMessage(sender, "Tienes el perfil suspendido!",2); return true;}

				if(p.isInMafia()) {
					
					if(!p.isPromoted()) {
						
						p.getMafia().promote(p, null, true);
						SpigotPlugin.sendMessage(sender, "El jugador " + args[0] + " ha sido puestoo como admin de la mafia");
					}else {
						
						SpigotPlugin.sendMessage(sender, "Este jugador ya es admin",2);
					}
				}else {
					
					SpigotPlugin.sendMessage(sender, "Este jugador no esta en ninguna mafia.",2);
				}
				return true;
			}
		});
		p.getCommand("mafia-demote").setExecutor(new MyComandExecutor("mafia-demote") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(args.length != 1) SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2);
				
				GangPlayer p = getGangPlayer(sender.getName());
				
				if(!p.isConnected()) { SpigotPlugin.sendMessage(sender, "Tienes el perfil suspendido!",2); return true;}
				
				if(p.isInMafia()) {
					
					if(p.isPromoted()) {
						
						GangPlayer o = p.getMafia().getPlayer(args[0]);
						
						if(o != null) {
							
							p.getMafia().demote(o, p, true);
							SpigotPlugin.sendMessage(sender, "Se han revocado las funciones de administrador al jugador: " + args[0]);
						}else {
							
							SpigotPlugin.sendMessage(sender, "No existe ningun jugador con el nombre: " + args[0],2);
						}
					}else {
						
						SpigotPlugin.sendMessage(sender, "No tienes nivel suficiente para realizar esta operación!",2);
					}
				}else {
					
					SpigotPlugin.sendMessage(sender, "No eres miembro de ninguna mafia!",2);
				}
				return true;
			}
		});
		
		p.getCommand("mafia-local-fake").setExecutor(new MyComandExecutor("mafia-local-fake") {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
				if(!super.onCommand(sender, command, label, args)) return true;
				
				if(!sender.isOp()) {SpigotPlugin.NotOPMessage(sender); return true;}
				
				Point.createFakeStructure(getGangPlayer(sender.getName()).getPlayer().getLocation(), getGangPlayer(sender.getName()).getPlayer());
				return true;
			}
		});
	}
	/**
	 * Carga las mafias
	 * @param section La sección del YML que guarda todo lo relacionado con las Mafias
	 */
	static void load(ConfigurationSection section) {
		
		//TODO: Load procedure
		for (String a : section.getKeys(false)) {
			
		}
	}
	/**
	 * Guarda todo los objetos relacionados con el plugin en un YML
	 */
	public static void save() {
		
		for (int i = 0; i < Good.goods.size(); i++) {
			
			if(Data.getConfigurationSection("good." + i) == null) 	
				Data.createSection("good." + i);
			
			Good.goods.get(i).save(Data.getConfigurationSection("good." + i));
		}
		for (int i = 0; i < players.size(); i++) {
	
			if(Data.getConfigurationSection("players." + i) == null) 	
				Data.createSection("players." + i);
			
			players.get(i).save(Data.getConfigurationSection("players." + i));
		}
		for (int i = 0; i < mafias.size(); i++) {
			
			if(Data.getConfigurationSection("mafias." + i) == null) 	
				Data.createSection("mafias." + i);
			
			mafias.get(i).save(Data.getConfigurationSection("mafias." + i));
		}
		
		plugin.saveConfig();
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
	public static ArrayList<Point> getPointsClone() {
		return (ArrayList<Point>) Point.points.clone();
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
