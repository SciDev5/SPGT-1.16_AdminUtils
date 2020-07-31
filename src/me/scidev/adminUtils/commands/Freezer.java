package me.scidev.adminUtils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

public class Freezer implements Listener {
	
	private static List<Player> frozenPlayers = new ArrayList<Player>();

	public static boolean addFrozenPlayer(Player player) {
		if (!frozenPlayers.contains(player)) {
			frozenPlayers.add(player);
			return true;
		} else
			return false;
	}
	public static boolean removeFrozenPlayer(Player player) {
		if (frozenPlayers.contains(player)) {
			frozenPlayers.remove(player);
			return true;
		} else
			return false;
	}

	private boolean shouldBlock(PlayerEvent e) {
		return frozenPlayers.contains(e.getPlayer());
	}
	private boolean shouldBlock(Player player) {
		return frozenPlayers.contains(player);
	}
	
	// EVENTS TO BLOCK

	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerMoveEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerDropItemEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerAttemptPickupItemEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockPlaceEvent e) {
		if (shouldBlock(e.getPlayer())) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(BlockBreakEvent e) {
		if (shouldBlock(e.getPlayer())) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerInteractEntityEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerTeleportEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void on(PlayerJumpEvent e) {
		if (shouldBlock(e)) e.setCancelled(true);
	}
}
