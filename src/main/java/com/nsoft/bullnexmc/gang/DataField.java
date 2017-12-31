package com.nsoft.bullnexmc.gang;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Interfaz para guardar un archivo en formato YML
 * @author DavidNexuss
 *
 */
public interface DataField{
	/**
	 * Guarda el objeto
	 * @param save El objeto {@link ConfigurationSection} que refleja la sección del archivo YML donde se quiere guardar el objeto
	 */
	//TODO: Remove this public modifier because scurity problems
	public void save(ConfigurationSection save);
	public String getName();
	public String dataType();
	
	public default void saveField(ConfigurationSection Data) {
		
		if(Data.getConfigurationSection(dataType() + "." + getName()) == null)
			Data.createSection(dataType() + "." + getName());
		
		save(Data.getConfigurationSection(dataType() + "." + getName()));
	}
}