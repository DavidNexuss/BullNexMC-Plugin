package com.nsoft.bullnexmc.gang;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nsoft.bullnexmc.MyComandExecutor;
import com.nsoft.bullnexmc.SpigotPlugin;

public class Bank {

	
	public static String B = ChatColor.BLUE + "[BANCO]" + ChatColor.WHITE;
	
	public static class SetInterest extends MyComandExecutor{
		
		public SetInterest(String name) {
			
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!sender.isOp()) { SpigotPlugin.NotOPMessage(sender); return true;}
			
			if(args.length != 1) { SpigotPlugin.sendMessage(sender, "Parámetros incorrectos",2); return true;}
			
			float inte = Float.parseFloat(args[0]);
			float old = BankUser.getInterest();
			
			BankUser.setNewInterest(inte/100f);
			
			SpigotPlugin.sendMessage(sender, "Interes cambiado: " + ChatColor.RED + old*100 + "%"
					+ ChatColor.YELLOW + " -> " + ChatColor.GREEN + inte + "%");
			
			
			return true;
		}
	}
	public static class SetBankPosition extends MyComandExecutor{
		
		public SetBankPosition(String name) {
			
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!sender.isOp()) { SpigotPlugin.NotOPMessage(sender); return true;}
			if(!(sender instanceof Player)) SpigotPlugin.sendMessage(sender, "Solo disponible para jugadores",2);
			Player p = (Player)sender;
			
			if(args.length != 1) SpigotPlugin.sendMessage(sender, "Argumentos insuficientes",2);
			
			switch (args[0]) {
			case "deposit":
				
				BankUser.setNewDepositLocation(p.getLocation());
				SpigotPlugin.sendMessage(sender, "Establecida nueva posicion de deposit: " + ChatColor.GREEN + p.getLocation().toString());
				break;

			case "withdraw": 
				
				BankUser.setNewWithdrawLocation(p.getLocation());
				SpigotPlugin.sendMessage(sender, "Establecida nueva posicion de withdraw: " + ChatColor.GREEN + p.getLocation().toString());	
				break;
			case "create":
				
				BankUser.setNewCreationLocation(p.getLocation());
				SpigotPlugin.sendMessage(sender, "Establecida nueva posicion de create: " + ChatColor.GREEN + p.getLocation().toString());	
				break;
			default:
				
				SpigotPlugin.sendMessage(sender, "Parametro incorrecto: " + args[0]);
				break;
			}
			return true;
		}
	}
	public static class CreateBankUser extends MyComandExecutor{

		public CreateBankUser(String name) {
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!(sender instanceof Player)) SpigotPlugin.sendMessage(sender, "Solo disponible para jugadores");
			Player p = (Player)sender;
			
			if(BankUser.BankUsersMap.containsKey(p.getUniqueId())) {
				
				sender.sendMessage(B + "Ya tienes una cuenta en el banco.");
				return true;
			}else {
				
				double r = p.getLocation().distanceSquared(BankUser.getCreationLocation());
				
				if(r < 9) {
					
					sender.sendMessage(B + "Gracias por crear una cuenta en nuestro banco.");
					BankUser.BankUsersMap.put(p.getUniqueId(), new BankUser(p.getUniqueId(), 0, BankUser.getCurrentDate()));
					BankUser.save();
				
				}else {
					
					SpigotPlugin.sendMessage(sender, "No puedes realizar esta operacion aqui");
				}
				
				return true;
			}
		}
		
	}
	
	public static class MoneyStatus extends MyComandExecutor{
		
		public MoneyStatus(String name) {
			
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!(sender instanceof Player)) SpigotPlugin.sendMessage(sender, "Solo disponible para jugadores");
			Player p = (Player)sender;
			
			if(!BankUser.BankUsersMap.containsKey(p.getUniqueId())) {
				
				SpigotPlugin.sendMessage(sender, "No tienes ninguna cuenta bancaria.");
				return true;
			}else {
				
				double r1 = p.getLocation().distanceSquared(BankUser.getDepositLocation());
				double r2 = p.getLocation().distanceSquared(BankUser.getCreationLocation());
				double r3 = p.getLocation().distanceSquared(BankUser.getWithdrawLocation());
				
				if(r1 < 9 || r2 < 9 || r3 < 9) {
					
					BankUser a = BankUser.BankUsersMap.get(p.getUniqueId());
					
					float money = a.getBalance();
					float moneyI = a.getBalanceWithInterest();
					String lastOp = a.getLastOperationAsString();
					float elapsed = a.elapsed();
					
					String c = Gang.eco.currencyNamePlural();
					sender.sendMessage(B + "Actualmente tienes " + ChatColor.YELLOW + money + c + ChatColor.WHITE + " en tu cuenta");
					sender.sendMessage(B + "Aplicando un " + ChatColor.GREEN + BankUser.getInterest()*100 + "%" + ChatColor.WHITE + "de interes durante " + ChatColor.YELLOW + elapsed/60f + ChatColor.WHITE + "h desde tu ultima operacion"
							+ " el: " + ChatColor.YELLOW + lastOp + ChatColor.WHITE + " obtienes un deposito de " + ChatColor.GREEN +  moneyI + c);
					sender.sendMessage(B + "Aumentaste tu patrimonio en " + ChatColor.GREEN + (moneyI - money) + c);
				
					
				}else {
					
					SpigotPlugin.sendMessage(sender, "No puedes realizar esta operacion aqui");
				}
				
				return true;
			}
		}
	}
	public static class DepositMoney extends MyComandExecutor{
		
		public DepositMoney(String name) {
			
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			// TODO Auto-generated method stub
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!(sender instanceof Player)) SpigotPlugin.sendMessage(sender, "Solo disponible para jugadores");
			Player p = (Player)sender;
			if(!BankUser.BankUsersMap.containsKey(p.getUniqueId())) {
				
				SpigotPlugin.sendMessage(sender, "No tienes ninguna cuenta bancaria.");
				return true;
			}else {
				
				if(args.length != 1) {
					
					SpigotPlugin.sendMessage(sender, "Argumentos incorrectos", 2);
					return true;
				}
				double r = p.getLocation().distanceSquared(BankUser.getDepositLocation());
				
				if(r < 9) {
					
					float dep = Float.parseFloat(args[0]);
					float bank = BankUser.BankUsersMap.get(p.getUniqueId()).getBalanceWithInterest();
					
					if(dep > Gang.eco.getBalance(p)) {
						
						sender.sendMessage(B + "Lo sentimos, no tienes suficiente para depositar en tu cuenta");
						sender.sendMessage(B + "Te faltan: " + ChatColor.RED +  (dep - Gang.eco.getBalance(p)) + Gang.eco.currencyNamePlural());
					}else {
						
						BankUser.BankUsersMap.get(p.getUniqueId()).deposit(dep);
						Gang.eco.withdrawPlayer(p, dep);
						sender.sendMessage(B + "Depositaste " + ChatColor.GREEN + dep + Gang.eco.currencyNamePlural() + ChatColor.WHITE + " en tu cuenta");
						sender.sendMessage(B + "Ahora tienes un saldo total de " + ChatColor.YELLOW + BankUser.BankUsersMap.get(p.getUniqueId()).getBalanceWithInterest() + Gang.eco.currencyNamePlural());
					}
					
				}else {
					
					SpigotPlugin.sendMessage(sender, "No puedes realizar esta operacion aqui");
				}
			return true;
		}
	}
	}
	public static class WithDrawMoney extends MyComandExecutor{
		
		public WithDrawMoney(String name) {
			
			super(name);
		}
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if(!super.onCommand(sender, command, label, args)) return true;
			
			if(!(sender instanceof Player)) SpigotPlugin.sendMessage(sender, "Solo disponible para jugadores");
			Player p = (Player)sender;
			
			if(!BankUser.BankUsersMap.containsKey(p.getUniqueId())) {
				
				SpigotPlugin.sendMessage(sender, "No tienes ninguna cuenta bancaria.");
				return true;
			}else {
				
				if(args.length != 1) {
					
					SpigotPlugin.sendMessage(sender, "Argumentos incorrectos", 2);
					return true;
				}
				double r = p.getLocation().distanceSquared(BankUser.getWithdrawLocation());
				
				if(r < 9) {
					
					float wth = Float.parseFloat(args[0]);
					float bank = BankUser.BankUsersMap.get(p.getUniqueId()).getBalanceWithInterest();
					
					if(wth > bank) {
						
						sender.sendMessage(B + "Lo sentimos, no tienes dinero suficiente en tu cuenta");
						sender.sendMessage(B + "Te faltan: " + ChatColor.RED + (wth - bank) + Gang.eco.currencyNamePlural());
					}else {
						
						BankUser.BankUsersMap.get(p.getUniqueId()).withdraw(wth);
						Gang.eco.depositPlayer(p, wth);
						sender.sendMessage(B + "Retiraste "+  ChatColor.GREEN + wth + Gang.eco.currencyNamePlural() + " de tu cuenta");
						sender.sendMessage(B + "Ahora tienes un saldo total de "+ ChatColor.YELLOW + BankUser.BankUsersMap.get(p.getUniqueId()).getBalanceWithInterest() + Gang.eco.currencyNamePlural());
					}
					
				}else {
					
					SpigotPlugin.sendMessage(sender, "No puedes realizar esta operacion aqui");
				}
				
				return true;
			}
			
		}
	}
}
	

