package me.netux.ctf.utils;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum Color {
	WHITE, RED, BLUE, ORANGE, PINK, GREEN, LIGHT_GREEN, CYAN, LIGHT_BLUE, LIGHT_GRAY, GRAY, MAGENTA, PURPLE, YELLOW, BLACK;
	
	public static DyeColor toDyeColor(Color color) {
		DyeColor result = null;
		switch(color) {
		case BLUE: result = DyeColor.BLUE; break;
		case CYAN: result = DyeColor.CYAN; break;
		case GRAY: result = DyeColor.GRAY; break;
		case GREEN: result = DyeColor.GREEN; break;
		case LIGHT_BLUE: result = DyeColor.LIGHT_BLUE; break;
		case LIGHT_GRAY: result = DyeColor.SILVER; break;
		case LIGHT_GREEN: result = DyeColor.LIME ;break;
		case MAGENTA: result = DyeColor.MAGENTA; break;
		case ORANGE: result = DyeColor.ORANGE; break;
		case PINK: result = DyeColor.PINK; break;
		case PURPLE: result = DyeColor.PURPLE; break;
		case RED: result = DyeColor.RED; break;
		case WHITE: result = DyeColor.WHITE; break;
		case YELLOW: result = DyeColor.YELLOW; break;
		case BLACK: result = DyeColor.BLACK; break;
		}
		return result;
	}
	
	public static ChatColor toChatColor(Color color) {
		ChatColor result = null;
		switch(color) {
		case BLUE: result = ChatColor.BLUE; break;
		case CYAN: result = ChatColor.DARK_AQUA; break;
		case GRAY: result = ChatColor.DARK_GRAY; break;
		case GREEN: result = ChatColor.DARK_GREEN; break;
		case LIGHT_BLUE: result = ChatColor.AQUA; break;
		case LIGHT_GRAY: result = ChatColor.GRAY; break;
		case LIGHT_GREEN: result = ChatColor.GREEN; ;break;
		case MAGENTA: result = ChatColor.LIGHT_PURPLE; break;
		case ORANGE: result = ChatColor.GOLD; break;
		case PINK: result = ChatColor.LIGHT_PURPLE; break;
		case PURPLE: result = ChatColor.DARK_PURPLE; break;
		case RED: result = ChatColor.RED; break;
		case WHITE: result = ChatColor.WHITE; break;
		case YELLOW: result = ChatColor.YELLOW; break;
		case BLACK: result = ChatColor.BLACK; break;
		}
		return result;
	}
	
	public static Color fromName(String name) {
		for(Color c : Color.values())
			if(c.toString().equalsIgnoreCase(name)) return c;
		return null;
	}
	
}
