package me.netux.ctf.objects.maps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.netux.ctf.configs.ConfigFile;
import me.netux.ctf.configs.MapFile;
import me.netux.ctf.configs.MapFileReader;
import me.netux.ctf.objects.Flag;
import me.netux.ctf.objects.Kit;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.Team;
import me.netux.ctf.objects.zones.Zone;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Map {
	
	private static final List<Map> cycle = new ArrayList<>();
	private static final List<Map> maps = new ArrayList<>();
	private MapInfo info;
	private Team[] teams;
	private Flag[] flags;
	private Kit[] kits;
	private Zone[] zones;
	private MapFile mapfile;
	private int ID;
	
	public Map(MapFile fromMapFile) {
		FileConfiguration fromConfig = fromMapFile.get();
		this.mapfile = fromMapFile;
		this.info = MapFileReader.getNewMapInfo(mapfile);
		
		List<Kit> tempKits = new ArrayList<>();
		List<Team> tempTeams = new ArrayList<>();
		List<Flag> tempFlags = new ArrayList<>();
		List<Zone> tempZones = new ArrayList<>();
		
		for(String zoneOnConfig : fromConfig.getConfigurationSection("zones").getKeys(false)) {
			ConfigurationSection zoneSection = fromConfig.getConfigurationSection("zones").getConfigurationSection(zoneOnConfig);
			Zone currentZone = MapFileReader.getZoneFromSection(zoneSection, this.info);
			if(currentZone == null) continue;
			tempZones.add(currentZone);
		}
		
		for(String kitOnConfig : fromConfig.getConfigurationSection("kits").getKeys(false)) {
			ConfigurationSection kitSection = fromConfig.getConfigurationSection("kits").getConfigurationSection(kitOnConfig);
			Kit currentKit = MapFileReader.getNewKit(kitSection, this.info);
			if(currentKit == null) continue;
			tempKits.add(currentKit);
		}
		
		for(String flagOnConfig : fromConfig.getConfigurationSection("flags").getKeys(false)) {
			ConfigurationSection flagSection = fromConfig.getConfigurationSection("flags").getConfigurationSection(flagOnConfig);
			Flag currentFlag = MapFileReader.getNewFlag(flagSection, this.info);
			if(currentFlag == null) continue;
			tempFlags.add(currentFlag);
		}
		
		for(String teamOnConfig : fromConfig.getConfigurationSection("teams").getKeys(false)) {
			ConfigurationSection teamSection = fromConfig.getConfigurationSection("teams").getConfigurationSection(teamOnConfig);
			Team currentTeam = MapFileReader.getNewTeam(teamSection, this.info);
			if(currentTeam == null) continue;
			tempTeams.add(currentTeam);
		}
		
		
		if(tempKits.size() == 0) {
			Bukkit.getLogger().severe("[CTF] Map " + info.getName() + " has no kits. Skipping...");
			throw new NullPointerException("MAP WITH NO KITS");
		}
		if(tempFlags.size() == 0) {
			Bukkit.getLogger().severe("[CTF] Map " + info.getName() + " has no flags. Skipping...");
			throw new NullPointerException("MAP WITH NO FLAGS");
		}
		if(tempTeams.size() == 0) {
			Bukkit.getLogger().severe("[CTF] Map " + info.getName() + " has no teams. Skipping...");
			throw new NullPointerException("MAP WITH NO TEAMS");
		}
		if(tempZones.size() == 0) {
			Bukkit.getLogger().severe("[CTF] Map " + info.getName() + " has no zones. Skipping...");
			throw new NullPointerException("MAP WITH NO ZONES");
		}
		
		this.zones = tempZones.toArray(new Zone[tempZones.size()]);
		this.kits = tempKits.toArray(new Kit[tempKits.size()]);
		this.flags = tempFlags.toArray(new Flag[tempFlags.size()]);
		this.teams = tempTeams.toArray(new Team[tempTeams.size()]);
		this.ID = maps.size();
		
		maps.add(this);
		cycle.add(this);
	}
	
	public MapInfo getInfo() {
		return info;
	}
	
	public Team[] getTeams() {
		return teams;
	}
	
	public Flag[] getFlags() {
		return flags;
	}
	
	public Kit[] getKits() {
		return kits;
	}
	
	public Zone[] getZones() {
		return zones;
	}
	
	public MapFile getMapFile() {
		return mapfile;
	}
	
	public int getID() {
		return ID;
	}
	
	public static List<Map> getMapsLoaded() {
		return maps;
	}
	
	public static List<Map> getCycle() {
		return cycle;
	}
	
	public static void addCycle(Map map) {
		if(Match.instance.getCurrentMap().equals(map)) return;
		if(!cycle.contains(map)) cycle.add(map);
		updateCycle();
	}
	
	public static void removeCycle(Map map) {
		if(Match.instance.getCurrentMap().equals(map)) return;
		if(cycle.contains(map)) cycle.remove(map);
		updateCycle();
	}
	
	public static void loadMaps(String[] fromWorldsName) {
		for(String n : fromWorldsName) {
			Bukkit.getLogger().info("[CTF] ===========----- Loading map " + n + "-----===========");
			if(!new File(Bukkit.getWorldContainer(), n).exists()) {
				Bukkit.getLogger().warning("[CTF] World " + n + " doesnt exist on the Worlds Folder, skipping it...");
				Bukkit.getLogger().info("[CTF] ===========----- Load of " + n + " canceled -----===========");
				continue;
			}
			WorldCreator wc = new WorldCreator(n);
			World w = wc.createWorld();
			MapFile mapFile = new MapFile(w);
			if(mapFile.get() == null) {
				Bukkit.getLogger().severe("[CTF] MapFile for " + n + " not found. Skipping...");
				Bukkit.getLogger().info("[CTF] ===========----- Load of " + n + " canceled -----===========");
				continue;
			}
			try {
				new Map(mapFile);
			} catch(NullPointerException e) {
				Bukkit.getLogger().info("[CTF] ===========----- Load of " + n + " canceled -----===========");
				continue;
			}
			Bukkit.getLogger().info("[CTF] ===========----- Loaded map " + n + "-----===========");
		}
		if(cycle.size() == 0 || cycle == null) {
			Bukkit.getLogger().severe("[CTF] They are no maps to load on Cycle. Disabling...");
			Plugin ctf = Bukkit.getPluginManager().getPlugin("CaptureTheFlag");
			Bukkit.getScheduler().cancelTasks(ctf);
			Bukkit.getPluginManager().disablePlugin(ctf);
			Bukkit.getScheduler().cancelTasks(ctf);
			return;
		}
		Bukkit.getLogger().info("[CTF] Loaded all Maps.");
	}
	
	public static Map getByName(String name) {
		for(Map m : maps)
			if(m.getInfo().getName().equalsIgnoreCase(name)) return m;
		return null;
	}
	
	public static Map getByWorldName(String name) {
		for(Map m : maps)
			if(m.getInfo().getWorldName().equalsIgnoreCase(name)) return m;
		return null;
	}
	
	public String toString() {
		return "[ID #" + ID + "] [SpectsSpawn " + info.getSpawn() + "] [TeamsNum " + teams.length + "] [FlagsNum " + flags.length + "] [World " + getInfo().getWorldName() + "] [Name " + info.getName() + "]";
	}
	
	private static void updateCycle() {
		ConfigFile f = new ConfigFile();
		StringBuilder newCycle = new StringBuilder();
		for(Map m : cycle) newCycle.append(m.getInfo().getWorldName() + ", ");
		newCycle.replace(newCycle.length() - 3, newCycle.length() - 1, "");
		f.get().set("cycle", newCycle.toString().trim());
		f.save();
		f.reload();
	}
	
}
