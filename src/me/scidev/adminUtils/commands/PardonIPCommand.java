package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.text.Localizer;

public class PardonIPCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.unban.error.syntax", alias));
			return true;
		}
		
		boolean isBanned = AdminUtils.instance.getServer().getBanList(Type.IP).isBanned(args[0]);
		if (!isBanned) {
			sender.sendMessage(Localizer.getLocalizedText("commands.unban.error.playerNotFound", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		String target = AdminUtils.instance.getServer().getBanList(Type.IP).getBanEntry(args[0]).getTarget();
		AdminUtils.instance.getServer().getBanList(Type.IP).pardon(args[0]);
		sender.sendMessage(Localizer.getLocalizedText("commands.unban.success", new String[] {"player"}, new String[] {target}));
		
		PardonCommand.broadcastUnbanMessage(sender,"a player's IP");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) 
			for (BanEntry ban : AdminUtils.instance.getServer().getBanList(Type.IP).getBanEntries())
				if (ban.getTarget().startsWith(args[0]))
					list.add(ban.getTarget());
		return list;
	}
}
