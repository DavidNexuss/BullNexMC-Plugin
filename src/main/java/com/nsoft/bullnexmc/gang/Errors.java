package com.nsoft.bullnexmc.gang;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
/**
 * Clase para gestionar errores del plugin
 * @author DavidNexuss
 */
public class Errors {

	//TODO: Think about public access of this methods
	
	/**
	 * Devuelve una excepción genérica con un mensaje que explica que el jugador con nombre pName no existe
	 * @param pName el nombre del jugador 
	 * @return la Excepción
	 */
	static Exception userNotFoundException(String pName) {
		
		return new Exception("Jugador " + pName + " no encontrado");
	}
	
	/**
	 * Envia a un ejecutor de comando, la consola o un jugador, un código de error
	 * @param cmdSender la consola o jugador
	 * @param e la excepción
	 */
	static void handleException(CommandSender cmdSender, Exception e) {
		
		cmdSender.sendMessage(getErrorMessage(e.getMessage()));
	}
	
	static String getErrorMessage(String error) {
		
		return ChatColor.GREEN + "[BullNex Mafias] " + ChatColor.RED + error;
	}
}
