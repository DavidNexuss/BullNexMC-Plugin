package com.nsoft.bullnexmc.economy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class CorpTicket extends CorpComponent{

	public static class TransactionObject{
		
		private ConfigurationSection section;
		
		Material m;
		int amount;
		int value;
		
		private void setConfigurationSection(ConfigurationSection section) {
			
			this.section = section;
		}
		
		private void save() {
			
			section.set("amount", amount);
			section.set("value", value);
		}
	}
	public static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
	
	private String clientname;
	private Date date;
	
	private HashMap<String,TransactionObject> transactions = new HashMap<>();
	private ArrayList<TransactionObject> transactionList = new ArrayList<>();
	
	public CorpTicket(Corp corp, ConfigurationSection section,String client,Date date) {
		super(corp, section);
		clientname = client;
		this.date = date;
	}
	
	public boolean addItem(TransactionObject t) {
		return addItem(t,true);
	}
	public boolean addItem(TransactionObject t,boolean save) {
		
		if(transactions.containsKey(t.m.name())) return false;
		
		transactions.put(t.m.name(), t);
		transactionList.add(t);
		if(save)t.save();
		return true;
	}
	@Override
	void load() {
		
		for (String key : getConfigurationSection().getKeys(false)) {
			
			TransactionObject t = new TransactionObject();
			
			t.m = Material.getMaterial(key);
			t.setConfigurationSection(getConfigurationSection().getConfigurationSection(key));
			
			t.amount = t.section.getInt("amount");
			t.value  = t.section.getInt("value" );
			
			transactions.put(key, t);
			transactionList.add(t);
		}
	}
	
	public ItemStack getAsBook() {
		
		ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
		bookMeta.setTitle("Ticket de compra de " + getCorp().getName());
		bookMeta.setAuthor(getCorp().getName());
		
		String header = "Este es un ticket de compra, un micro contrato reducido que refleja la siguiente transacción: \n";
		header += "El señor/a " + ChatColor.GREEN + clientname + ChatColor.RESET + " en la fecha " + ChatColor.GOLD + format.format(date) + ChatColor.RESET + "\n";
		header += "a la empresa " + ChatColor.LIGHT_PURPLE + getCorp().getName() + ChatColor.RESET + " .\n";
		header += "A continuación una lista de los elementos comprados";
		
		bookMeta.addPage(header);
		String list = "";
		int i =0;
		int val = 0;
		for (TransactionObject t : transactionList) {
			
			list += "#" + ChatColor.DARK_GREEN + t.m.name() + ChatColor.RESET;
			list += ">" + ChatColor.GOLD + "(" + ChatColor.BLUE + t.amount + ChatColor.RESET + ",";
			list += "" + ChatColor.DARK_RED + t.value + "B" + ChatColor.GOLD + ")" + ChatColor.RESET;
			list += "\n";
			i++;
			if(i > 7) {
				
				bookMeta.addPage(list);
				list = "";
				i = 0;
			}
			
			val += t.value;
		}
		
		if(!list.equals("")) bookMeta.addPage(list);
		bookMeta.addPage("Con un coste total de: " + ChatColor.DARK_RED + val + "BNDs");
		writtenBook.setItemMeta(bookMeta);
		
		return writtenBook;
	}
}
