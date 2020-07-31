package me.scidev.adminUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import me.scidev.adminUtils.commands.FreezeCommand;
import me.scidev.adminUtils.commands.Freezer;
import me.scidev.adminUtils.commands.InvSeeCommand;
import me.scidev.adminUtils.commands.UnfreezeCommand;
import me.scidev.adminUtils.itemGUI.GUIEventHandler;
import me.scidev.adminUtils.text.Colorizer;
import me.scidev.adminUtils.text.Localizer;

public class AdminUtils extends JavaPlugin {
	public static AdminUtils instance = null;
	public static Logger LOGGER = LogManager.getLogger("AdminUtils"); 
	
	public GUIEventHandler guiEventHandler;
	
	@Override
	public void onEnable() {
		instance = this;
		
		saveDefaultConfig();
		initTextUtils();
		
		registerEvents();
		registerCommands();
	}
	
	private void initTextUtils() {
		// Text colorization/stylization
		ConfigurationSection styleSection = getConfig().getConfigurationSection("i10n.style");
		if (styleSection != null) {
			String styleCharStr = styleSection.getString("stylechar", "");
			String hexColorMarker = styleSection.getString("hexcolor", "");
			if (styleCharStr.trim().length() == 1 && !hexColorMarker.trim().isEmpty())
				Colorizer.setStyleChars(styleCharStr.charAt(0), hexColorMarker);
			else
				logWarning("Test styling information missing, using defaults!");
		} else
			logWarning("Test styling information missing, using defaults!");
		// Text localization
		ConfigurationSection localizationSection = getConfig().getConfigurationSection("i10n.substitution");
		if (localizationSection != null) {
			String beginning = localizationSection.getString("beginning","");
			String ending = localizationSection.getString("ending","");
			if (!beginning.trim().isEmpty() && !ending.trim().isEmpty())
				Localizer.setSubstitutionDelimiters(beginning, ending);
			else
				logWarning("Text localization substitution delimiters missing, using defaults!");
		} else
			logWarning("Text localization substitution delimiters missing, using defaults!");
	}


	private void registerEvents() {
		this.guiEventHandler = new GUIEventHandler();
		getServer().getPluginManager().registerEvents(this.guiEventHandler, this);
		getServer().getPluginManager().registerEvents(new Freezer(), this);
	}

	public void registerCommands() {
		String permissionMessage = Localizer.getLocalizedText("noPermission");
		registerCommand("invsee", new InvSeeCommand(), permissionMessage);
		registerCommand("freeze", new FreezeCommand(), permissionMessage);
		registerCommand("unfreeze", new UnfreezeCommand(), permissionMessage);
	}
	
	public void registerCommand(String name, CommandExecutor executor, String permissionMessage) {
		PluginCommand cmd = getServer().getPluginCommand(name);
		cmd.setExecutor(executor);
		cmd.setPermissionMessage(permissionMessage);
	}
	
	public static void logInfo(String text) {
		LOGGER.log(Level.INFO, text);
	}
	private void logWarning(String text) {
		LOGGER.log(Level.WARN, text);
	}
}
