package me.scidev.adminUtils.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.punishment.Mutes;
import me.scidev.adminUtils.text.Localizer;

public class ChatListener implements Listener {

	public static boolean chatIsEnabled = true;
	
	private static List<Player> staff = new ArrayList<>();
	public static boolean staffLeave(Player player) {
		if (staff.contains(player)) {
			staff.remove(player);
			return true;
		}
		return false;
	}
	public static boolean staffJoin(Player player) {
		if (!staff.contains(player)) {
			staff.add(player);
			return true;
		}
		return false;
	}
	public static boolean staffToggle(Player player) {
		boolean staffChatting = staff.contains(player);
		if (!staffChatting)
			staff.add(player);
		else
			staff.remove(player);
		return !staffChatting;
	}
	
	public static void sendStaffMessage(CommandSender sender, String message) {
		String formattedMessage = String.format(Localizer.getLocalizedText("chatFormat.staffChat"),sender.getName(),message);
		for (Player recipient : staff)
			recipient.sendMessage(formattedMessage);
		AdminUtils.logInfo(formattedMessage);
	}
	

	
	@EventHandler
	public void on(AsyncPlayerChatEvent e) {
		if (
			!chatIsEnabled && !(
				e.getPlayer().hasPermission("adminutils.chat.talkWhenChatDisabled") 
				&& AdminUtils.instance.getConfig().getBoolean("staffCanTalkWhenChatMuted", true)
			)) 
		{
			e.getPlayer().sendMessage(Localizer.getLocalizedText("commands.chat.trySpeakGlobalMute"));
			e.setCancelled(true);
			return;
		}
		if (Mutes.isMuted(e.getPlayer()) || Mutes.isMuted(e.getPlayer().getAddress().getAddress().getHostAddress())) {
			e.getPlayer().sendMessage(Localizer.getLocalizedText("punishment.mutedTrySpeak"));
			e.setCancelled(true);
			return;
		}
		if (staff.contains(e.getPlayer())) {
			Set<Player> recipients = e.getRecipients();
			recipients.clear();
			for (Player staffMember : staff) recipients.add(staffMember);
			e.setFormat(Localizer.getLocalizedText("chatFormat.staffChat"));
		}
	}
	
	@EventHandler
	public void on(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if (staff.contains(player))
			ChatListener.staffLeave(player);
	}
}
