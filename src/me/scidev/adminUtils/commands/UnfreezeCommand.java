package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.listeners.FreezeListener;
import me.scidev.adminUtils.text.Localizer;

public class UnfreezeCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.freeze.error.syntax", alias));
			return true;
		}
		
		Player target = AdminUtils.instance.getServer().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(Localizer.getLocalizedText("commands.freeze.error.playerNotFound", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		if (FreezeListener.removeFrozenPlayer(target)) {
			target.sendMessage(Localizer.getLocalizedText("punishment.unfreeze"));
			sender.sendMessage(Localizer.getLocalizedText("commands.freeze.successUnfreeze", new String[] {"player"}, new String[] {target.getName()}));
		} else 
			sender.sendMessage(Localizer.getLocalizedText("commands.freeze.noChange"));
			
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) return null;
		List<String> list = new ArrayList<String>();
		return list;
	}

}
