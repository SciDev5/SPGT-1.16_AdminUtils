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

import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.BanList.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.text.Localizer;

public class BanCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.ban.error.syntax", alias));
			return true;
		}
		if (args.length >= 2) {
			if (!args[1].toLowerCase().matches("forever|[0-9]+(dy|hr|min|s)")) {
				sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.ban.error.syntax", alias));
				return true;
			}
		}
		
		Date expires = null;
		String reason = "Banned by an operator.";
		
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
			new BanByName(sender, args[0], reason, expires).start();
			return true;
		}
		
		if (expires != null) {
			String timeStr = BanCommand.banRemainingDurationString(expires);
			target.kickPlayer(Localizer.getLocalizedText("punishment.ban", new String[] {"duration","reason"}, new String[] {timeStr,reason}));
			sender.sendMessage(Localizer.getLocalizedText("commands.ban.success", new String[] {"player","reason","duration"}, new String[] {target.getName(),reason,timeStr}));
		} else {
			target.kickPlayer(Localizer.getLocalizedText("punishment.banPermanent", new String[] {"reason"}, new String[] {reason}));
			sender.sendMessage(Localizer.getLocalizedText("commands.ban.successForever", new String[] {"player","reason"}, new String[] {target.getName(),reason}));
		}
		BanCommand.broadcastBanMessage(sender,target,expires,reason);
		target.banPlayer(reason, expires);
			
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
	
	public static String banRemainingDurationString(Date expirationDate) {
		if (expirationDate == null) return "forever";
		long dt_ms = expirationDate.getTime()-System.currentTimeMillis();
		long dt_s = dt_ms / 1000l;
		long dt_min = dt_s / 60l; dt_s %= 60l;
		long dt_hr = dt_min / 60l; dt_min %= 60l;
		long dt_day = dt_hr / 24l; dt_hr %= 24l;
		if (dt_day > 0)
			return dt_day+" day"+(dt_day!=1l?"s":"")+", "+dt_hr+" hour"+(dt_hr!=1l?"s":"");
		else if (dt_hr > 0)
			return dt_hr+" hour"+(dt_hr!=1l?"s":"")+", "+dt_min+" min"+(dt_min!=1l?"s":"");
		else if (dt_min > 0)
			return dt_min+" min"+(dt_min!=1l?"s":"")+", "+dt_s+"s";
		else 
			return dt_s+"s";
	}
	public static void broadcastBanMessage(CommandSender admin, OfflinePlayer target, Date expirationDate, String reason) {
		if (!AdminUtils.instance.getConfig().getBoolean("punishmentBroadcast.ban", false)) return;
		if (expirationDate != null) {
			String timeStr = BanCommand.banRemainingDurationString(expirationDate);
			AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.ban.broadcastMessage", new String[] {"admin","player","duration","reason"}, new String[] {admin.getName(),target.getName(),timeStr,reason}));
		} else
			AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.ban.broadcastMessageForever", new String[] {"admin","player","reason"}, new String[] {admin.getName(),target.getName(),reason}));
	}
	
	private class BanByName extends Thread {
		String playerName;
		String reason;
		Date expires;
		CommandSender sender;
		public BanByName(CommandSender sender, String playerName, String reason, Date expires) {
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
						sender.sendMessage(Localizer.getLocalizedText("commands.ban.error.banNewbie"));
						return;
					}
					boolean isBanned = target.isBanned();
					if (isBanned) {
						BanEntry ban = Bukkit.getBanList(Type.NAME).getBanEntry(target.getName());
						if (ban != null) {
							Date expirationDate = ban.getExpiration();
							if (expirationDate != null)
								if (System.currentTimeMillis() > expirationDate.getTime())
									isBanned = false;
						}
					}
					if (isBanned) {
						sender.sendMessage(Localizer.getLocalizedText("commands.ban.error.alreadyBanned", new String[] {"player"}, new String[] {target.getName()}));
					} else {
						if (expires != null)
							target.banPlayer(reason, expires);
						else 
							target.banPlayer(reason);
						if (expires != null) {
							String timeStr = BanCommand.banRemainingDurationString(expires);
							sender.sendMessage(Localizer.getLocalizedText("commands.ban.success", new String[] {"player","reason","duration"}, new String[] {target.getName(),reason,timeStr}));
						} else
							sender.sendMessage(Localizer.getLocalizedText("commands.ban.successForever", new String[] {"player","reason"}, new String[] {target.getName(),reason}));
						BanCommand.broadcastBanMessage(sender,target,expires,reason);
					}
					
				} else {
					sender.sendMessage(Localizer.getLocalizedText("commands.ban.error.playerNotFound", new String[] {"player"}, new String[] {playerName}));
				}
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
