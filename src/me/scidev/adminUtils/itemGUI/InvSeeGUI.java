package me.scidev.adminUtils.itemGUI;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.scidev.adminUtils.AdminUtils;
import me.scidev.adminUtils.commands.Freezer;
import me.scidev.adminUtils.text.Localizer;

public class InvSeeGUI extends BaseGUI {

	private Player viewer;
	private Player target;
	
	public InvSeeGUI(Player viewer, Player target) {
		super(5*9, Localizer.getLocalizedText("commands.invsee.inventoryName", new String[] {"player"}, new String[] {target.getName()}), viewer, true);
		this.target = target;
		this.viewer = viewer;
		updateViewerInventory();
	}

	@Override
	protected void initializeItems() {
		return;
	}

	@Override
	protected void onClick(InventoryClickEvent e) {
		Player player = (Player)e.getWhoClicked();
		if (player == viewer) {
			switch (e.getSlot()) {
			case 41:
			case 44:
				e.setCancelled(true);
				break;
			case 42:
				target.getInventory().clear();
				updateViewerInventory();
				e.setCancelled(true);
				break;
			case 43:
				e.setCancelled(true);
				if (Freezer.addFrozenPlayer(target))
					player.sendMessage(Localizer.getLocalizedText("commands.freeze.successFreeze", new String[] {"player"}, new String[] {target.getName()}));
				else 
					player.sendMessage(Localizer.getLocalizedText("commands.freeze.noChange"));
				break;
			default:
				updateTargetInventoryLater();
			}
		} else if (player == target && e.getClickedInventory() == (Inventory)target.getInventory())
			updateViewerInventoryLater();
	}
	@Override
	protected void onDrag(InventoryDragEvent e) {
		Player player = (Player)e.getWhoClicked();
		if (player == viewer) {
			updateTargetInventoryLater();
		} else if (player == target) 
			updateViewerInventoryLater();
	}
	@Override
	protected void onPickupItem(EntityPickupItemEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		if (player == target) updateViewerInventoryLater();
	}
	@Override
	protected void onDropItem(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if (player == target) updateViewerInventoryLater();
	}
	@Override
	protected void onSwapOffHand(PlayerSwapHandItemsEvent e) {
		Player player = e.getPlayer();
		if (player == target) updateViewerInventoryLater();
	}

	private void updateViewerInventoryLater() {
		BukkitRunnable task = new BukkitRunnable() { @Override public void run() { updateViewerInventory(); } };
		task.runTaskLater(AdminUtils.instance, 1);
	}
	private void updateViewerInventory() {
		inventory.clear();
		PlayerInventory targetInv = target.getInventory();
		for (int slot = 0; slot < targetInv.getSize(); slot++)
			inventory.setItem(slot, targetInv.getItem(slot));
		inventory.setItem(41, createGUIItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
		inventory.setItem(42, createGUIItem(Material.RED_STAINED_GLASS_PANE, 1, "Clear Inventory"));
		inventory.setItem(43, createGUIItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, "Freeze Player"));
		inventory.setItem(44, createGUIItem(Material.GRAY_STAINED_GLASS_PANE, 1, " "));
	}
	

	private void updateTargetInventoryLater() {
		BukkitRunnable task = new BukkitRunnable() { @Override public void run() { updateTargetInventory(); } };
		task.runTaskLater(AdminUtils.instance, 1);
	}
	private void updateTargetInventory() {
		PlayerInventory targetInv = target.getInventory();
		for (int slot = 0; slot < targetInv.getSize(); slot++)
			targetInv.setItem(slot, inventory.getItem(slot));
	}
	
}
