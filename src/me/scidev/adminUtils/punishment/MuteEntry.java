package me.scidev.adminUtils.punishment;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class MuteEntry {

	private OfflinePlayer target;
	private String targetIp;
	private Date expires;
	public MuteEntry(OfflinePlayer target, Date expires) {
		this.target = target;
		this.expires = expires;
	}
	public MuteEntry(String targetIp, Date expires) {
		this.targetIp = targetIp;
		this.expires = expires;
	}
	public MuteEntry(String packedInfo) {
		if (packedInfo.length() == 0) {
			this.target = null;
			this.expires = null;
			return;
		}
		Matcher matcherPlayer = Pattern.compile("<([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}):(NULL|[0-9]+)>").matcher(packedInfo);
		Matcher matcherIP = Pattern.compile(
				"<((?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + 
				"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + 
				"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + 
				"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)):(NULL|[0-9]+)>").matcher(packedInfo);
		if (matcherPlayer.find()) {
			UUID uuid = UUID.fromString(matcherPlayer.group(1));
			this.target = Bukkit.getOfflinePlayer(uuid);
			if (!matcherPlayer.group(2).equals("NULL"))
				this.expires = new Date(Long.parseLong(matcherPlayer.group(2)));
			else 
				this.expires = null;
		} else if (matcherIP.find()) {
			this.targetIp = matcherIP.group(1);
			if (!matcherIP.group(2).equals("NULL"))
				this.expires = new Date(Long.parseLong(matcherIP.group(2)));
			else 
				this.expires = null;
		} else {
			this.target = null;
			this.expires = null;
		}
	}
	public String toString() {
		if (this.target == null && this.targetIp != null)
			return "<"+targetIp+":"+(expires == null ? "NULL" : expires.getTime())+">";
		else if (this.target != null && this.targetIp == null)
			return "<"+target.getUniqueId().toString()+":"+(expires == null ? "NULL" : expires.getTime())+">";
		else 
			return "<INVALID>";
	}
	public boolean isMuted(OfflinePlayer player) {
		if (this.expires != null)
			if (System.currentTimeMillis() > this.expires.getTime())
				return false;
		return isPlayer(player);
	}
	public boolean isNameMuted(String playerName) {
		if (this.expires != null)
			if (System.currentTimeMillis() > this.expires.getTime())
				return false;
		return isPlayerName(playerName);
	}
	public boolean isMuted(String ip) {
		if (this.expires != null)
			if (System.currentTimeMillis() > this.expires.getTime())
				return false;
		return isIp(ip);
	}
	public boolean isPlayer(OfflinePlayer player) {
		return player == target;
	}
	public boolean isPlayerName(String playerName) {
		return playerName.equalsIgnoreCase(this.target.getName());
	}
	public boolean isIp(String ip) {
		return ip.equalsIgnoreCase(targetIp);
	}
	public boolean valid(List<MuteEntry> muted) {
		if (this.expires != null)
			if (System.currentTimeMillis() > this.expires.getTime())
				return false;
		if (this.target == null && this.targetIp != null) {
			for (MuteEntry other : muted)
				if (other.isIp(targetIp))
					return false;
			return true;
		} else if (this.target != null && this.targetIp == null) {
			if (!this.target.hasPlayedBefore())
				return false;
			for (MuteEntry other : muted)
				if (other.isPlayer(target))
					return false;
			return true;
		} else return false;
	}

	public String getTargetName() {
		if (this.target == null) return null;
		return this.target.getName();
	}
	public String getTargetIP() {
		return this.targetIp;
	}
}
