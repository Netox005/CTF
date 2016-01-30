package me.netux.ctf.objects.zones;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import me.netux.ctf.objects.maps.MapInfo;
import me.netux.ctf.objects.zones.ZoneSetting.EnumZoneSetting;
import me.netux.ctf.utils.MapUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Zone {
	
	private static List<Zone> zones = new ArrayList<>();
	private int ID;
	private String name;
	private String internalName;
	private Location min;
	private Location max;
	private MapInfo mapInfo;
	private Location[] locations;
	private ZoneSetting[] settings;
	
	public Zone(String name, String internalName, Location minimumPoint, Location maximumPoint, MapInfo mapInfo) {
		this.name = name;
		this.internalName = internalName;
		this.mapInfo = mapInfo;
		this.min = minimumPoint;
		this.max = maximumPoint;
		this.locations = MapUtils.getSelection(minimumPoint, maximumPoint);
		this.settings = new ZoneSetting[4];
		this.ID = zones.size();
		zones.add(this);
		Bukkit.getLogger().info("[CTF] Generated Zone " + toString());
	}
	
	public Location[] getSectionIgnoreHeadRotation() {
		Location[] result = locations.clone();
		for(int i = 0; i < result.length; i++)
			result[i] = new Location(result[i].getWorld(), result[i].getBlockX(), result[i].getBlockY(), result[i].getBlockZ(), 0, 0);
		return result;
	}
	
	public SimpleEntry<Boolean, String[]> getSetting(EnumZoneSetting type) {
		return new AbstractMap.SimpleEntry<Boolean, String[]>(settings[type.ordinal()].isAllowed(), settings[type.ordinal()].getWhomList());
	}
	
	public int getID() { return ID; }
	public String getName() { return name; }
	public String getInternalName() { return internalName; }
	public Location[] getSection() { return locations; }
	public Location getMaximumPoint() { return max; }
	public Location getMinimumPoint() { return min; }
	public ZoneSetting[] getFlags() { return settings; }
	public MapInfo getMap() { return mapInfo; }
	public void setSetting(EnumZoneSetting type, boolean allowed, String[] whom) { settings[type.ordinal()] = new ZoneSetting(type, allowed, whom); }
	public static List<Zone> getZones() { return zones; }
	
	public static Zone getByNameAndMap(String name, MapInfo mapOwner) {
		for(Zone z : zones)
			if(z.getName().equalsIgnoreCase(name) && z.getMap().equals(mapOwner)) return z;
		return null;
	}
	
	public static Zone getByInternalNameAndMap(String name, MapInfo mapOwner) {
		for(Zone z : zones)
			if(z.getInternalName().equalsIgnoreCase(name) && z.getMap().equals(mapOwner)) return z;
		return null;
	}

	public String toString() {
		return "[ID #" + ID + "] [Name " + name + "]";
	}
	
}
