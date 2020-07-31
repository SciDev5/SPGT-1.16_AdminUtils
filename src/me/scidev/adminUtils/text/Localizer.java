package me.scidev.adminUtils.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.scidev.adminUtils.AdminUtils;

public class Localizer {

	private static String substitutionDelimiterBeginning = "<:";
	private static String substitutionDelimiterEnding = ":>";
	
	public static void setSubstitutionDelimiters(String beginning, String ending) {
		assert beginning != null : "substitution delimiter was null";
		assert ending != null : "substitution delimiter was null";
		assert beginning.trim().isEmpty() : "substitution delimiter was empty";
		assert ending.trim().isEmpty() : "substitution delimiter was empty";
		
		substitutionDelimiterBeginning = beginning.trim();
		substitutionDelimiterEnding = ending.trim();
	}
	
	public static String getLocalizedText(String configNodeAddress) {
		return Colorizer.colorize(AdminUtils.instance
				.getConfig().getConfigurationSection("i10n")
				.getString(configNodeAddress,"<I10N@"+configNodeAddress+">"));
	}
	public static String getLocalizedText(String configNodeAddress, String[] substitutionKeys, String[] substitutionValues) {
		assert substitutionKeys.length == substitutionValues.length : "substitution keys and values had different lengths";
		String textSection = getLocalizedText(configNodeAddress);
		for (int i = 0; i < substitutionKeys.length; i++)
			textSection = textSection.replaceAll(
				"([^\\\\]|^)" + Pattern.quote(substitutionDelimiterBeginning + substitutionKeys[i] + substitutionDelimiterEnding), 
				"$1"+Matcher.quoteReplacement(substitutionValues[i]));
		return textSection;
	}
	public static String getLocalizedCommandUsageText(String configNodeAddress, String alias) {
		return getLocalizedText(configNodeAddress, new String[] {"command"}, new String[] {alias});
	}
}
