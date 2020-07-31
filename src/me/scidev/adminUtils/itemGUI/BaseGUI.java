package me.scidev.adminUtils.itemGUI;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.scidev.adminUtils.AdminUtils;

public abstract class BaseGUI {
	
	protected final Inventory inventory;
	
	private final boolean allowExternalEvents;
	
	public BaseGUI(int size, String title, HumanEntity viewer, boolean allowExternalEvents) {
		inventory = Bukkit.createInventory(null, size, title);
		initializeItems();
		this.allowExternalEvents = allowExternalEvents;
		register();
		viewer.openInventory(inventory);
	}
	public BaseGUI(int size, String title, HumanEntity viewer) {
		this(size,title,viewer,false);
	}

	protected abstract void initializeItems();
	
	protected static ItemStack createGUIItem(Material itemType, int count, String name, String... lore) {
		ItemStack item = new ItemStack(itemType, count);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	public final void on(InventoryEvent e) {
		if (e.getInventory() == this.inventory || allowExternalEvents) {
			if (e instanceof InventoryClickEvent) onClick((InventoryClickEvent) e);
			if (e instanceof InventoryDragEvent) onDrag((InventoryDragEvent) e);
			if (e instanceof InventoryCloseEvent) {
				unregister();
				onClose((InventoryCloseEvent) e);
			}
				
		}
	}
	public final void on(EntityPickupItemEvent e) {
		if (allowExternalEvents)
			if (e instanceof EntityPickupItemEvent) 
				onPickupItem((EntityPickupItemEvent) e);
	}
	public final void on(PlayerDropItemEvent e) {
		if (allowExternalEvents)
			if (e instanceof PlayerDropItemEvent) 
				onDropItem((PlayerDropItemEvent) e);
	}
	public final void on(PlayerSwapHandItemsEvent e) {
		if (allowExternalEvents)
			if (e instanceof PlayerSwapHandItemsEvent) 
				onSwapOffHand((PlayerSwapHandItemsEvent) e);
	}


	protected void onClick(InventoryClickEvent e) {}
	protected void onClose(InventoryCloseEvent e) {}
	protected void onDrag(InventoryDragEvent e) {}
	protected void onPickupItem(EntityPickupItemEvent e) {}
	protected void onDropItem(PlayerDropItemEvent e) {}
	protected void onMoveItem(InventoryMoveItemEvent e) {}
	protected void onSwapOffHand(PlayerSwapHandItemsEvent e) {}
	
	private void register() {
		AdminUtils.instance.guiEventHandler.registerGUI(this);
	}
	private void unregister() {
		AdminUtils.instance.guiEventHandler.unregisterGUI(this);
	}
	
}
