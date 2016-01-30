package me.netux.ctf.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class MapUtils {

	public static Location stringLocation(World world, String[] from) {
		for(int i = 0; i < from.length; i++) {
			try {
				if(i < 4) Double.parseDouble(from[i]);
				else Float.parseFloat(from[i]);
			} catch(NumberFormatException e) {
				Bukkit.getLogger().severe("[CTF] " + from[i] + " is an invalid number.");
				return null;
			}
		}
		
		return new Location(world,
			Double.parseDouble(from[0]),
			Double.parseDouble(from[1]),
			Double.parseDouble(from[2]),
			Float.parseFloat(from[3]),
			Float.parseFloat(from[4])
		);
	}
	
	public static Location[] getSelection(Location min, Location max) {
		int minX = min.getBlockX() > max.getBlockX() ? max.getBlockX() : min.getBlockX();
		int minY = min.getBlockY() > max.getBlockY() ? max.getBlockY() : min.getBlockY();
		int minZ = min.getBlockZ() > max.getBlockZ() ? max.getBlockZ() : min.getBlockZ();
		int maxX = min.getBlockX() < max.getBlockX() ? max.getBlockX() : min.getBlockX();
		int maxY = min.getBlockY() < max.getBlockY() ? max.getBlockY() : min.getBlockY();
		int maxZ = min.getBlockZ() < max.getBlockZ() ? max.getBlockZ() : min.getBlockZ();
		
		List<Location> tempLocs = new ArrayList<>();
		for(int x = minX; x <= maxX; x++)
		for(int y = minY; y <= maxY; y++)
		for(int z = minZ; z <= maxZ; z++)
			tempLocs.add(new Location(min.getWorld(), x + 0.5, y + 0.1, z + 0.5, min.getYaw(), max.getPitch()));
		
		return tempLocs.toArray(new Location[tempLocs.size()]);
	}
	
}
