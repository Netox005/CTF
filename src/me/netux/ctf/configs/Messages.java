package me.netux.ctf.configs;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Messages {
	
	public static Messages instance;
	private FileConfiguration messages;
	
	public Messages(Plugin p) {
		this.messages = new MessagesFile(p).get();
		Messages.instance = this;
	}
	
    private ConfigurationSection getSection(String[] sections) {
    	ConfigurationSection currentSection = messages.getDefaultSection();
    	for(int i = 0; i < (sections.length - 1); i++) {
    		currentSection = currentSection.getConfigurationSection(sections[i]);
    	}
    	return currentSection;
    }
    
    public String getMessage(String sections) {
    	return getSection(sections.split(",")).getString(sections.split(",")[sections.split(",").length - 1]).replaceAll("&", "§");
    }
    
    public String getMessage(String sections, String[] toReplace, String[] replacement) {
    	String toReturn = getMessage(sections);
    	for(int i = 0; i < toReplace.length; i++)
    		toReturn = toReturn.replace(toReplace[i], replacement[i]);
    	return toReturn;
    }
    
    public String[] getMessages(String sections) {
    	List<String> toReturn = getSection(sections.split(",")).getStringList(sections.split(",")[sections.split(",").length - 1]);
    	for(int i = 0; i < toReturn.size(); i++) toReturn.set(i, toReturn.get(i).replace("&", "§"));
    	return toReturn.toArray(new String[toReturn.size()]);
    }
    
    public String[] getMessages(String sections, String[] toReplace, String[] replacement) {
    	String[] toReturn = getMessages(sections);
    	for(int rI = 0; rI < toReturn.length; rI++)
    		for(int i = 0; i < toReplace.length; i++)
    			toReturn[rI] = toReturn[rI].replace(toReplace[i], replacement[i]);
    	return toReturn;
    }
	
}
