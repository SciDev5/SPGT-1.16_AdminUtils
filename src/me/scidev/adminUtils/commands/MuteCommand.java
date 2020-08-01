package me.scidev.adminUtils.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.punishment.Mutes;
import me.scidev.adminUtils.text.Localizer;

public class MuteCommand implements CommandExecutor, TabCompleter {

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
			new MuteByName(sender, args[0], reason, expires).start();
			return true;
		}
		
		if (expires != null) {
			String timeStr = BanCommand.banRemainingDurationString(expires);
			target.sendMessage(Localizer.getLocalizedText("punishment.muted", new String[] {"duration","reason"}, new String[] {timeStr,reason}));
			sender.sendMessage(Localizer.getLocalizedText("commands.mute.success", new String[] {"player","reason","duration"}, new String[] {target.getName(),reason,timeStr}));
		} else {
			target.sendMessage(Localizer.getLocalizedText("punishment.mutedPermanent", new String[] {"reason"}, new String[] {reason}));
			sender.sendMessage(Localizer.getLocalizedText("commands.mute.successForever", new String[] {"player","reason"}, new String[] {target.getName(),reason}));
		}
		MuteCommand.broadcastMuteMessage(sender,target,expires,reason);
		Mutes.mute(target, expires);
			
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
	
	public static void broadcastMuteMessage(CommandSender admin, OfflinePlayer target, Date expirationDate, String reason) {
		if (!AdminUtils.instance.getConfig().getBoolean("punishmentBroadcast.mute", false)) return;
		if (expirationDate != null) {
			String timeStr = BanCommand.banRemainingDurationString(expirationDate);
			AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.mute.broadcastMessage", new String[] {"admin","player","duration","reason"}, new String[] {admin.getName(),target.getName(),timeStr,reason}));
		} else
			AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.mute.broadcastMessageForever", new String[] {"admin","player","reason"}, new String[] {admin.getName(),target.getName(),reason}));
	}
	
	private class MuteByName extends Thread {
		String playerName;
		String reason;
		Date expires;
		CommandSender sender;
		public MuteByName(CommandSender sender, String playerName, String reason, Date expires) {
			this.playerName = playerName;
			this.expires = expires;
			this.reason = reason;
			this.sender = sender;
		}
		@Override
		public void run() {
			try {
				URL url = new URL("https", "api.mojang.com", "/users/profiles/minecraft/"+playerName);
				HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String inputLine;
				String response = "";
				while ((inputLine = in.readLine()) != null) {
					response += inputLine+"\n";
				}
				in.close();
				
				Matcher matcher = Pattern.compile(Pattern.quote("\"id\":\"")+"([0-9a-fA-F]{32})"+Pattern.quote("\"")).matcher(response);
				
				if (matcher.find()) {
					String uuidString = matcher.group(1);
					UUID uuid = UUID.fromString(uuidString.replaceFirst("^(.{8})(.{4})(.{4})(.{4})(.{12})$", "$1-$2-$3-$4-$5"));
					OfflinePlayer target = AdminUtils.instance.getServer().getOfflinePlayer(uuid);
					if (!target.hasPlayedBefore()) {
						sender.sendMessage(Localizer.getLocalizedText("commands.mute.error.banNewbie"));
						return;
					}
					boolean isBanned = Mutes.isMuted(target);
					if (isBanned) {
						sender.sendMessage(Localizer.getLocalizedText("commands.mute.error.alreadyMuted", new String[] {"player"}, new String[] {target.getName()}));
					} else {
						Mutes.mute(target, expires);
						if (expires != null) {
							String timeStr = BanCommand.banRemainingDurationString(expires);
							sender.sendMessage(Localizer.getLocalizedText("commands.mute.success", new String[] {"player","reason","duration"}, new String[] {target.getName(),reason,timeStr}));
						} else
							sender.sendMessage(Localizer.getLocalizedText("commands.mute.successForever", new String[] {"player","reason"}, new String[] {target.getName(),reason}));
						MuteCommand.broadcastMuteMessage(sender,target,expires,reason);
					}
					
				} else {
					sender.sendMessage(Localizer.getLocalizedText("commands.mute.error.playerNotFound", new String[] {"player"}, new String[] {playerName}));
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
