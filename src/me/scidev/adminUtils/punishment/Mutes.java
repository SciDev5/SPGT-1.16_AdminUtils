package me.scidev.adminUtils.punishment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.bukkit.OfflinePlayer;

import me.scidev.adminUtils.AdminUtils;

public class Mutes {

	private static List<MuteEntry> muted = new ArrayList<>();
	public static boolean unmute(OfflinePlayer player) {
		MuteEntry toRemove = null;
		for (MuteEntry mute : muted)
			if (mute.isMuted(player)) {
				toRemove = mute;
				break;
			}
		if (toRemove != null) {
			muted.remove(toRemove);
			return true;
		}
		return false;
	}
	public static boolean unmuteName(String playerName) {
		MuteEntry toRemove = null;
		for (MuteEntry mute : muted)
			if (mute.isNameMuted(playerName)) {
				toRemove = mute;
				break;
			}
		if (toRemove != null) {
			muted.remove(toRemove);
			return true;
		}
		return false;
	}
	public static boolean mute(OfflinePlayer player, Date expires) {
		if (!isMuted(player)) {
			MuteEntry mute = new MuteEntry(player, expires);
			muted.add(mute);
			return true;
		}
		return false;
	}

	public static boolean unmute(String ip) {
		MuteEntry toRemove = null;
		for (MuteEntry mute : muted)
			if (mute.isMuted(ip)) {
				toRemove = mute;
				break;
			}
		if (toRemove != null) {
			muted.remove(toRemove);
			return true;
		}
		return false;
	}
	public static boolean mute(String ip, Date expires) {
		if (!isMuted(ip)) {
			MuteEntry mute = new MuteEntry(ip, expires);
			muted.add(mute);
			return true;
		}
		return false;
	}
	public static boolean isMuted(OfflinePlayer player) {
		MuteEntry toRemove = null;
		for (MuteEntry mute : muted)
			if (mute.isPlayer(player)) {
				if (mute.isMuted(player))
					return true;
				else {
					toRemove = mute;
					break;
				}
			}
		if (toRemove != null) muted.remove(toRemove);
		return false;
	}
	public static boolean isNameMuted(String player) {
		MuteEntry toRemove = null;
		for (MuteEntry mute : muted)
			if (mute.isPlayerName(player)) {
				if (mute.isNameMuted(player))
					return true;
				else {
					toRemove = mute;
					break;
				}
			}
		if (toRemove != null) muted.remove(toRemove);
		return false;
	}
	public static boolean isMuted(String ip) {
		MuteEntry toRemove = null;
		for (MuteEntry mute : muted)
			if (mute.isIp(ip)) {
				if (mute.isMuted(ip))
					return true;
				else {
					toRemove = mute;
					break;
				}
			}
		if (toRemove != null) muted.remove(toRemove);
		return false;
	}
	public static List<MuteEntry> getMutes() {
		return muted;
	}
	
	public static void save() {
		try {
			File saveFile = new File(AdminUtils.instance.getDataFolder(),"muted.txt");
			if(saveFile.exists()) saveFile.delete();
			saveFile.createNewFile();
			FileWriter writer = new FileWriter(saveFile);
			for (MuteEntry mute : muted)
				writer.write(mute.toString()+"\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void load() {
		try {
			File saveFile = new File(AdminUtils.instance.getDataFolder(),"muted.txt");
			Scanner reader = new Scanner(saveFile);
			while(reader.hasNextLine()) {
				MuteEntry mute = new MuteEntry(reader.nextLine());
				if (mute.valid(muted))
					muted.add(mute);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
