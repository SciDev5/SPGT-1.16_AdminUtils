package me.scidev.adminUtils.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import me.scidev.adminUtils.itemGUI.BaseGUI;

public class GUIListener implements Listener {
	private List<BaseGUI> guis = new ArrayList<>();
	
	public void registerGUI(BaseGUI gui) {
		if (!guis.contains(gui))
			guis.add(gui);
	}
	public void unregisterGUI(BaseGUI gui) {
		if (guis.contains(gui))
			guis.remove(gui);
	}
	@EventHandler
	public void on(InventoryClickEvent e) {
		for (BaseGUI gui : guis) gui.on(e);
	}
	@EventHandler
	public void on(InventoryDragEvent e) {
		for (BaseGUI gui : guis) gui.on(e);
	}
	@EventHandler
	public void on(InventoryCloseEvent e) {
		for (int i = guis.size()-1; i >= 0; i--) 
			guis.get(i).on(e);
	}
	@EventHandler
	public void on(EntityPickupItemEvent e) {
		for (BaseGUI gui : guis) gui.on(e);
	}
	@EventHandler
	public void on(PlayerDropItemEvent e) {
		for (BaseGUI gui : guis) gui.on(e);
	}
	@EventHandler
	public void on(PlayerSwapHandItemsEvent e) {
		for (BaseGUI gui : guis) gui.on(e);
	}
	
	
}
