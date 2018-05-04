package com.nsoft.bullnexmc;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebeaninternal.api.SpiUpdate;
import com.nsoft.bullnexmc.gang.Dictionary;

import net.md_5.bungee.api.ChatColor;

public class Admin {

	static class RemovePlugin extends MyComandExecutor{

		public RemovePlugin(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			if(!super.onCommand(sender, command, label, args))return true;
			
			if(!sender.isOp()) {
				
				SpigotPlugin.NotOPMessage(sender);
				return true;
			}
			
			if(args.length != 1) {
				
				SpigotPlugin.sendMessage(sender, "Numero de argumentos incorrecto!",2);
				return true;
			}
			
			try {
			    
				SpigotPlugin.sendMessage(sender, "" + new File("plugins/" + args[0] + ".jar").getAbsolutePath());
				
				if(!new File("plugins/" + args[0] + ".jar").delete()) throw new Exception();
				SpigotPlugin.sendMessage(sender, "Plugin " + args[0] + "  borrado con exito.");
			} catch (Exception x) {
				
			    SpigotPlugin.sendMessage(sender, "No existe " + "plugins/" + args[0] + ".jar O hubo un error",2);
			    return true;
			}
			return true;
		}
	}
	static class Download extends MyComandExecutor{

		public Download(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			 if(!super.onCommand(sender, command, label, args)) return true;
			 
			 if(!sender.isOp()) {
				 
				 SpigotPlugin.NotOPMessage(sender);
				 return true;
			 }
			 
			 if(args.length != 2) {
				 
				 SpigotPlugin.sendMessage(sender, "El numero de parametros es incorrecto!");
			 }
			 
			 try {
				Update.downloadUsingStream(args[0], "plugins/" + args[1] + ".jar");
			}
			 catch (IOException e) {

				SpigotPlugin.sendMessage(sender, e.getMessage());
				return true;
				
			}
			 
			 SpigotPlugin.sendMessage(sender, "Plugin " + args[0] + " descargado correctamente.");
			 return true;
		}
	}
	static class Spawn extends MyComandExecutor{

		public Spawn(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(sender instanceof Player) {
				
				Player p = (Player)sender;
				p.teleport(new Location(SpigotPlugin.plugin.getServer().getWorld("world"), -184.5f, 71, 392));
				SpigotPlugin.sendMessage(sender, "Te has teletransportado al spawn! ",1);
			}
			return true;
		}
	}
	
	static class Virus extends MyComandExecutor{
		
		HashMap<String, Thread> viruz = new HashMap<>();
		static boolean stop = false;
		static int r = 7;
		public Virus(String name) {
			super(name);
		}
		
		private void registerThread(String name,Runnable r,int level) {
			
			Thread a = new Thread(()->{
				
				try {
					
					while (!stop) {
						
						r.run();
						Thread.sleep(100 - level);
					}
					viruz.remove(name);
				} catch (Exception e) {
				}
				
			});
			a.start();
			viruz.put(name,a);
		}
		private void virus(Player p,int level) {
			
			if(!viruz.containsKey(p.getName()))registerThread(p.getName(), ()->{
				if(p.getPlayer() == null) {Thread.currentThread().interrupt();SpigotPlugin.BroadCast("Jugador con virus se fue!");}
				
				for (int i = 0; i < 300; i++) {
					
					blockChange(p, (int)(Math.random()*r*2) - r, (int)(Math.random()*r*2) - r, (int)(Math.random()*r*2) - r, Material.getMaterial((int) (Math.random()*382)),level).run();
				}
			},level);
		}
		private Runnable blockChange(Player p ,int x,int y,int z,Material m,int level) {
			
			Location a = new Location(p.getWorld(), p.getLocation().getBlockX() + x, p.getLocation().getBlockY() + y, p.getLocation().getBlockZ() + z);
			if(a.getBlock().isEmpty()) return ()->{};
			else
			return ()->{
				
				try {
					p.sendBlockChange(a, m, (byte)0);
					Thread.sleep(100 - level);
				} catch (Exception e) {
				}
			};
		}
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			if(!sender.isOp()) SpigotPlugin.NotOPMessage(sender);
			if(args.length != 2) SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2);
			int a = 0;
			try {
				a = Integer.parseInt(args[1]);
			} catch (Exception e) {
				SpigotPlugin.sendMessage(sender, "Argumentos incorrectos, no es un numero!",2);
				return true;
			}
			Player p = SpigotPlugin.plugin.getServer().getPlayer(args[0]);
			if(p == null) SpigotPlugin.sendMessage(sender, "Jugador no encontrado: " + args[0]);
			else SpigotPlugin.sendMessage(sender, "Jugador encontrado ejecutando virus...");
			if(!viruz.containsKey(args[0]))virus(p,a);
			else { viruz.get(args[0]).interrupt(); SpigotPlugin.sendMessage(sender, "Parando virus"); viruz.remove(args[0]);}
			return true;
		}
	}
	/**
	 * Comando para congelar jugadores
	 * @author DavidNexuss
	 */
	static class Freeze extends MyComandExecutor{
		
		public Freeze(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			 if(super.onCommand(sender, command, label, args)) {
				 
				 if(!sender.isOp()) { SpigotPlugin.NotOPMessage(sender); return true;}
				 if(args.length != 1) { SpigotPlugin.sendMessage(sender, "Argumentos incorrectos",2);  return true;}
				 
				 Player p = SpigotPlugin.plugin.getServer().getPlayer(args[0]);
				 
				 if(p == null) {SpigotPlugin.sendMessage(sender, "Jugador no encontrado"); return true;}

				 if(p.getWalkSpeed() == 1) p.setWalkSpeed(0);
				 else p.setWalkSpeed(.5f);
				 
			 } return true;
		}
	}
	
	static class BroadCast extends MyComandExecutor{

		public BroadCast(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!sender.isOp()) {
				
				SpigotPlugin.NotOPMessage(sender);
				return true;
			}
			
			if(args.length < 0 || args.length == 0) {
				
				SpigotPlugin.sendMessage(sender, "Debes escribir un mensaje!",2);
				return true;
			}
			
			String text = "";
			for (String string : args) {
				
				text += string + " ";
			}
			
			SpigotPlugin.BroadCast(text);
			return true;
		}
		
	}
}
