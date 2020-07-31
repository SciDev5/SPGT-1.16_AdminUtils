package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.itemGUI.InvSeeGUI;
import me.scidev.adminUtils.text.Localizer;

public class InvSeeCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Localizer.getLocalizedText("commands.invsee.error.isConsole"));
			return true;
		}
		if (args.length != 1) {
			sender.sendMessage(Localizer.getLocalizedCommandUsageText("commands.invsee.error.syntax", alias));
			return true;
		}
		
		Player player = (Player) sender;
		Player target = AdminUtils.instance.getServer().getPlayer(args[0]);
		if (target == null) {
			sender.sendMessage(Localizer.getLocalizedText("commands.invsee.error.playerNotFound", new String[] {"player"}, new String[] {args[0]}));
			return true;
		}
		
		new InvSeeGUI(player,target);
		sender.sendMessage(Localizer.getLocalizedText("commands.invsee.success", new String[] {"player"}, new String[] {target.getName()}));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) return null;
		List<String> list = new ArrayList<String>();
		return list;
	}

}
