package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.listeners.ChatListener;
import me.scidev.adminUtils.text.Localizer;

public class ChatCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.error.syntax", alias));
			return true;
		}
		switch (args[0]) {
		case "true":
			if (ChatListener.chatIsEnabled == true)
				sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.error.noChange", alias));
			else {
				AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.chat.broadcastOn", new String[] {"admin"}, new String[] {sender.getName()}));
				sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.successOn", alias));
				ChatListener.chatIsEnabled = true;
			}
			break;
		case "false":
			if (ChatListener.chatIsEnabled == false)
				sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.error.noChange", alias));
			else {
				AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.chat.broadcastOff", new String[] {"admin"}, new String[] {sender.getName()}));
				sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.successOff", alias));
				ChatListener.chatIsEnabled = false;
			}
			break;
		case "clear":
			for (int i = 0; i < 99; i++) 
				AdminUtils.instance.getServer().broadcastMessage(" ");
			AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.chat.broadcastClear", new String[] {"admin"}, new String[] {sender.getName()}));
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.successClear", alias));
			break;
		default:
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.chat.error.syntax", alias));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			if ("true".startsWith(args[0])) list.add("true");
			if ("false".startsWith(args[0])) list.add("false");
			if ("clear".startsWith(args[0])) list.add("clear");
		}
		return list;
	}

}
