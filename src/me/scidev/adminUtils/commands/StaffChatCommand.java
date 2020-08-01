package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.listeners.ChatListener;
import me.scidev.adminUtils.text.Localizer;

public class StaffChatCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		boolean isConsole = !(sender instanceof Player);
		if (args.length == 0) {
			if (isConsole) {
				sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.error.isConsole"));
				return true;
			}
			if (ChatListener.staffToggle((Player) sender))
				sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.successEnable"));
			else 
				sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.successDisable"));
			return true;
		} else if (args.length == 1) {
			if (isConsole && (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("false"))) {
				sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.error.isConsole"));
				return true;
			}
			if (args[0].equalsIgnoreCase("true")) {
				if(ChatListener.staffJoin((Player) sender))
					sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.successEnable"));
				else 
					sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.error.noChange"));
				return true;
			}
			if (args[0].equalsIgnoreCase("false")) {
				if (ChatListener.staffLeave((Player) sender))
					sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.successDisable"));
				else 
					sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.error.noChange"));
				return true;
			}
		}
		
		if (!isConsole) {
			if (ChatListener.staffJoin((Player) sender))
				sender.sendMessage(Localizer.getLocalizedText("commands.staffchat.successEnable"));
		}
		
		String message = String.join(" ", args);
		ChatListener.sendStaffMessage(sender, message);
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			if ("true".startsWith(args[0])) list.add("true");
			if ("false".startsWith(args[0])) list.add("false");
		}
		return list;
	}
	public static void broadcastKickMessage(CommandSender admin, OfflinePlayer target, String reason) {
		if (!AdminUtils.instance.getConfig().getBoolean("punishmentBroadcast.kick", false)) return;
		AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.kick.broadcastMessage", new String[] {"admin","player","reason"}, new String[] {admin.getName(),target.getName(),reason}));
	}
}
