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
import me.scidev.adminUtils.text.Localizer;

public class WarnCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length < 2) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.kick.error.syntax", alias));
			return true;
		}
		
		String reason = "";
		if (args.length >= 2) {
			for (int i = 1; i < args.length; i++)
				reason += args[i]+" ";
			reason = reason.trim();
		}
		
		Player target = AdminUtils.instance.getServer().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(Localizer.getLocalizedText("commands.warn.error.playerNotFound", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		target.sendMessage(Localizer.getLocalizedText("punishment.warn", new String[] {"reason"}, new String[] {reason}));
		sender.sendMessage(Localizer.getLocalizedText("commands.warn.success", new String[] {"player","reason"}, new String[] {target.getName(),reason}));
		
		WarnCommand.broadcastWarnMessage(sender,target,reason);
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) return null;
		List<String> list = new ArrayList<String>();
		switch (args.length) {
		case 2:
			if (args[1].length() == 0) {
				list.add("Unfair Advantage");
				list.add("Xray / Cavefinder");
				list.add("Duplicating");
				list.add("Toxicity");
				list.add("Lag Machine / Attempt to Damage Server");
				list.add("Advertising");
				list.add("Breaking Server Rules");
				list.add("Ban Evasion");
				list.add("Filter Bypass");
			}
			break;
		}
		return list;
	}
	public static void broadcastWarnMessage(CommandSender admin, OfflinePlayer target, String reason) {
		if (!AdminUtils.instance.getConfig().getBoolean("punishmentBroadcast.warn", false)) return;
		AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.warn.broadcastMessage", new String[] {"admin","player","reason"}, new String[] {admin.getName(),target.getName(),reason}));
	}
}
