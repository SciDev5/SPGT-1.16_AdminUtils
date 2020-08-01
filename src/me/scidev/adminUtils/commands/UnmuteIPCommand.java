package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import me.scidev.adminUtils.punishment.MuteEntry;
import me.scidev.adminUtils.punishment.Mutes;
import me.scidev.adminUtils.text.Localizer;

public class UnmuteIPCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.unmute.error.syntax", alias));
			return true;
		}

		boolean isBanned = Mutes.isMuted(args[0]);
		if (!isBanned) {
			sender.sendMessage(Localizer.getLocalizedText("commands.unmute.error.playerNotFound", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		Mutes.unmute(args[0]);
		
		sender.sendMessage(Localizer.getLocalizedText("commands.unmute.successIP", new String[] {"player"}, new String[] {args[0]}));
		UnmuteCommand.broadcastUnmuteMessage(sender,"a player's IP");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) 
			for (MuteEntry mute : Mutes.getMutes())
				if (mute.getTargetIP() != null)
					if (mute.getTargetIP().startsWith(args[0]))
						list.add(mute.getTargetIP());
		return list;
	}
}
