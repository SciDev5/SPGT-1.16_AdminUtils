package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.punishment.MuteEntry;
import me.scidev.adminUtils.punishment.Mutes;
import me.scidev.adminUtils.text.Localizer;

public class UnmuteCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.unmute.error.syntax", alias));
			return true;
		}
		
		boolean isBanned = Mutes.isNameMuted(args[0]);
		if (!isBanned) {
			sender.sendMessage(Localizer.getLocalizedText("commands.unmute.error.playerNotFound", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		Mutes.unmuteName(args[0]);
		Player target = AdminUtils.instance.getServer().getPlayer(args[0]);
		if (target != null)
			target.sendMessage(Localizer.getLocalizedText("punishment.unmuted"));
		sender.sendMessage(Localizer.getLocalizedText("commands.unmute.success", new String[] {"player"}, new String[] {target==null?args[0]:target.getName()}));
		UnmuteCommand.broadcastUnmuteMessage(sender,target==null?args[0]:target.getName());
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) 
			for (MuteEntry mute : Mutes.getMutes())
				if (mute.getTargetName() != null)
					if (mute.getTargetName().startsWith(args[0]))
						list.add(mute.getTargetName());
		return list;
	}
	public static void broadcastUnmuteMessage(CommandSender admin, String target) {
		if (!AdminUtils.instance.getConfig().getBoolean("punishmentBroadcast.unmute", false)) return;
		AdminUtils.instance.getServer().broadcastMessage(Localizer.getLocalizedText("commands.unmute.broadcastMessage", new String[] {"admin","player"}, new String[] {admin.getName(),target}));
	}
}
