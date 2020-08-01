package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.punishment.Mutes;
import me.scidev.adminUtils.text.Localizer;

public class MuteIPCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.mute.error.syntax", alias));
			return true;
		}
		if (args.length >= 2) {
			if (!args[1].toLowerCase().matches("forever|[0-9]+(dy|hr|min|s)")) {
				sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.mute.error.syntax", alias));
				return true;
			}
		}
		
		Date expires = null;
		String reason = "Muted by an operator.";
		
		if (args.length >= 2) {
			if (args[1].toLowerCase().matches("[0-9]+(dy|hr|min|s)")) {
				Matcher matcher = Pattern.compile("([0-9]+)(dy|hr|min|s)").matcher(args[1]);
				matcher.find();
				long dt = Long.parseLong(matcher.group(1));
				switch (matcher.group(2)) {
				case "dy": dt *= 86400000l; break;
				case "hr": dt *= 3600000l; break;
				case "min": dt *= 60000l; break;
				case "s": dt *= 1000l; break;
				}
				expires = new Date(System.currentTimeMillis()+dt);
			}
		}
		
		if (args.length >= 3) {
			reason = "";
			for (int i = 2; i < args.length; i++)
				reason += args[i]+" ";
			reason = reason.trim();
		}
		
		Player target = AdminUtils.instance.getServer().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(Localizer.getLocalizedText("commands.mute.error.ipMuteOffline", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		if (expires != null) {
			String timeStr = BanCommand.banRemainingDurationString(expires);
			target.sendMessage(Localizer.getLocalizedText("punishment.muted", new String[] {"duration","reason"}, new String[] {timeStr,reason}));
			sender.sendMessage(Localizer.getLocalizedText("commands.mute.successIP", new String[] {"player","reason","duration"}, new String[] {target.getName(),reason,timeStr}));
		} else {
			target.sendMessage(Localizer.getLocalizedText("punishment.mutedPermanent", new String[] {"reason"}, new String[] {reason}));
			sender.sendMessage(Localizer.getLocalizedText("commands.mute.successIPForever", new String[] {"player","reason"}, new String[] {target.getName(),reason}));
		}
		MuteCommand.broadcastMuteMessage(sender,target,expires,reason);
		Mutes.mute(target.getAddress().getAddress().getHostAddress(), expires);
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) return null;
		List<String> list = new ArrayList<String>();
		switch (args.length) {
		case 2:
			if ("forever".startsWith(args[1]))
				list.add("forever");
			if (args[1].matches("[0-9]+[a-zA-Z]*")) try {
				long dt = Long.parseUnsignedLong(args[1].replaceFirst("^([0-9]+).*$", "$1"));
				if (args[1].matches("[0-9]+[dD]?[yY]?")) list.add(dt+"dy");
				if (args[1].matches("[0-9]+[hH]?[rR]?")) list.add(dt+"hr");
				if (args[1].matches("[0-9]+[mM]?[iI]?[nN]?")) list.add(dt+"min");
				if (args[1].matches("[0-9]+[sS]?")) list.add(dt+"s");
			} catch (NumberFormatException e) {}
			break;
		case 3:
			if (args[2].length() == 0) {
				list.add("Toxicity");
				list.add("Advertising");
				list.add("Breaking Server Rules");
				list.add("Filter Bypass");
			}
			break;
		}
		return list;
	}
}
