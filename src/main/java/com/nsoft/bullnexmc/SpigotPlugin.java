package com.nsoft.bullnexmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebeaninternal.server.subclass.GetterSetterMethods;
import com.nsoft.bullnexmc.bungee.SQLFactory;
import com.nsoft.bullnexmc.economy.Bank;
import com.nsoft.bullnexmc.economy.BankUser;
import com.nsoft.bullnexmc.economy.Corp;
import com.nsoft.bullnexmc.economy.CorpCommands;
import com.nsoft.bullnexmc.economy.MarketValue;
import com.nsoft.bullnexmc.gang.Gang;
import com.nsoft.misc.Freeze;
import com.nsoft.misc.Magma;
import com.nsoft.misc.SuperPower;

import net.md_5.bungee.api.ChatColor;

public class SpigotPlugin extends JavaPlugin implements Listener {
	
	public static SpigotPlugin plugin;
	public SpigotPlugin() {
		
		plugin = this;
	}
    @Override
    public void onDisable() {
        // Don't log disabling, Spigot does that for you automatically!
    }

    
    @EventHandler
	public void onPostLogin(PlayerJoinEvent event) {
		
    	Player p = event.getPlayer();
    	if(event.getPlayer().hasPlayedBefore()) {
    		
    		sendMessage(p,"Bienvenido " + p.getName(),0);
    	}else {
    		
    		sendMessage(p, "Bienvenido a BullNexMC!",2);
    		
    	}
    	
    	SQLFactory.updatePlayer(p);
    	
	}
    public static void BroadCast(String msg) {
    	
    	plugin.getServer().broadcastMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.GOLD + msg);
    }
    public static void NotOPMessage(CommandSender sender) {
    	
    	sendMessage(sender, "Solo los operadores pueden ejecutar este comando!", 2);
    }
    public static void sendMessage(CommandSender sender,String msg) {
    	
    	sendMessage(sender, msg, 0);
    }
    public static void sendMessage(CommandSender sender,String msg,int type) {
    	
    	
    	switch (type) {
		case 0:
			
			sender.sendMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.GOLD + msg);
			break;
		case 1:
			
			sender.sendMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.BLUE + msg);
			break;
		case 2:
			
			sender.sendMessage(ChatColor.GREEN + "[BullNexRP] " + ChatColor.RED + msg);
		default:
			break;
		}
    }
    
    public static int checkSieze() {
    	
    	try {
    		
    		
			FileInputStream entrada = new FileInputStream("plugins/BullNexMC-1.0.jar");
			int size = entrada.available();
			entrada.close();
			return size;
			
    	
    	} catch (Exception e) {

			e.printStackTrace();
			return -1;
		}
    	
    }
    
    private void echoDiscord() {
    	plugin.getServer().broadcast(ChatColor.BLUE + "[Discord]: " + ChatColor.AQUA + "Vente a nuestro servidor de discord: " + ChatColor.LIGHT_PURPLE + "https://discord.gg/nygpwS6","bullnexmc.update");
    	plugin.getServer().getScheduler().runTaskLater(plugin, ()->echoDiscord(), 36000);
    }
    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    @Override
    public void onEnable() {
        // Don't log enabling, Spigot does that for you automatically!
    	SQLFactory.start();
    	createConfig();
    	
    	Update.PluginSize = checkSieze();
    	Update a = new Update();
    	
    	Gang.init(getConfig(), this);
    	BankUser.init();
    	Corp.init();
    	getServer().broadcast( ChatColor.GREEN +"[BullNexRP] " + ChatColor.BLUE + "Plugin iniciado!", "bullnexmc.update");
        echoDiscord();
    	// Commands enabled with following method must have entries in plugin.yml
    	
    	getCommand("ticketdebug").setExecutor(new CorpCommands.TicketDebug("ticketdebug"));
    	getCommand("marketvalue").setExecutor(new MarketValue("marketvalue"));
    	getCommand("bankcreate").setExecutor(new Bank.CreateBankUser("bankcreate"));
    	getCommand("bankdeposit").setExecutor(new Bank.DepositMoney("bankdeposit"));
    	getCommand("bankwithdraw").setExecutor(new Bank.WithDrawMoney("bankwithdraw"));
    	getCommand("banksetlocation").setExecutor(new Bank.SetBankPosition("banksetlocation"));
    	getCommand("banksetint").setExecutor(new Bank.SetInterest("banksetint"));
    	getCommand("bankstatus").setExecutor(new Bank.MoneyStatus("bankstatus"));
    	  
    	getCommand("magma").setExecutor(new Magma("magma"));
    	getCommand("big-freeze").setExecutor(new Freeze("big-freeze"));
    	getCommand("power").setExecutor(new SuperPower.Power("power"));
    	getCommand("powers").setExecutor(new SuperPower.ListPowers("powers"));
    	getCommand("virus").setExecutor(new Admin.Virus("virus"));
    	getCommand("freeze").setExecutor(new Admin.Freeze("freeze"));
    	getCommand("bn-remove").setExecutor(new Admin.RemovePlugin("bn-remove"));
    	getCommand("bn-download").setExecutor(new Admin.Download("bn-download"));
    	getCommand("spawn").setExecutor(new Admin.Spawn("spawn"));
    	getCommand("bn-broad").setExecutor(new Admin.BroadCast("bn-broad"));
    	getCommand("bn-bolt").setExecutor(new DeadFall.BoltCommand("bn-bolt"));
        getCommand("bn-drop").setExecutor(new DeadFall.DropCommand("bn-drop"));
    	getCommand("example").setExecutor(new ExampleCommand(this));
        getCommand("update").setExecutor(a);
        getCommand("bn-size").setExecutor(a);
        
        getServer().getPluginManager().registerEvents(this, this);
        
    }
    
    
}
