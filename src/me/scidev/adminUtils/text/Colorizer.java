package me.scidev.adminUtils.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class Colorizer {

	private static String hexColorMarker = "vx";
	private static char styleChar = '&';
	
	public static void setStyleChars(char styleChar, String hexColorMarker) {
		assert hexColorMarker != null : "hexColorMarker was null";
		assert hexColorMarker.trim().isEmpty() : "hexColorMarker was empty";
		
		Colorizer.hexColorMarker = hexColorMarker.trim();
		Colorizer.styleChar = styleChar;
	}
	
	public static String colorize(String textIn) {
		String hexColoredText = textIn;
		Matcher regexMatcher = Pattern.compile(Pattern.quote(hexColorMarker)+"([0-9a-fA-F]{6})").matcher(textIn);
		while (regexMatcher.find()) {
			String hexText = regexMatcher.group(1);
			hexColoredText = regexMatcher.replaceFirst(""+ChatColor.of("#"+hexText));
		}
		return ChatColor.translateAlternateColorCodes(styleChar, hexColoredText);
	}
}
