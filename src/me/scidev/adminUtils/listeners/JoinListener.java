package me.scidev.adminUtils.listeners;

import org.bukkit.Bukkit;

import java.util.Date;

import org.bukkit.BanEntry;
import org.bukkit.BanList.Type;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import me.scidev.adminUtils.commands.BanCommand;
import me.scidev.adminUtils.text.Localizer;

public class JoinListener implements Listener {
	
	@EventHandler
	public void on(PlayerLoginEvent e) {
		if (e.getPlayer().isBanned() || Bukkit.getBanList(Type.IP).getBanEntry(e.getAddress().getHostAddress()) != null) {
			BanEntry ban = null;
			ban = Bukkit.getBanList(Type.NAME).getBanEntry(e.getPlayer().getName());
			if (ban == null) ban = Bukkit.getBanList(Type.IP).getBanEntry(e.getAddress().getHostAddress());
			Date expirationDate = ban.getExpiration();
			if (expirationDate != null) {
				String timeStr = BanCommand.banRemainingDurationString(expirationDate);
				e.setKickMessage(Localizer.getLocalizedText("punishment.ban", new String[] {"duration","reason"}, new String[] {timeStr,ban.getReason()}));
				e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER);
			} else {
				e.setKickMessage(Localizer.getLocalizedText("punishment.banPermanent", new String[] {"reason"}, new String[] {ban.getReason()}));
				e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER);
			}
		}
	}
}
