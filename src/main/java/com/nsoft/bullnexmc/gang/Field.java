package com.nsoft.bullnexmc.gang;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Interfaz para guardar un archivo en formato YML
 * @author DavidNexuss
 *
 */
public interface Field{
	/**
	 * Guarda el objeto
	 * @param save El objeto {@link ConfigurationSection} que refleja la sección del archivo YML donde se quiere guardar el objeto
	 */
	public void save(ConfigurationSection save);
}