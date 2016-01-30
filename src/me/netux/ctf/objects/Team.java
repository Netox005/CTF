package me.netux.ctf.objects;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.UUID;

import me.netux.ctf.utils.Color;
import me.netux.ctf.listeners.custom.PlayerInGameStatusChangeEvent;
import me.netux.ctf.listeners.custom.PlayerInGameStatusChangeEvent.Action;
import me.netux.ctf.objects.maps.MapInfo;
import me.netux.ctf.objects.zones.Zone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Team {

	private static List<Entry<Team, String>> teams = new ArrayList<>();
	private List<UUID> players;
	private String name;
	private String internalname;
	private Color color;
	private int ID;
	private int maxplayers;
	private MapInfo mapinfo;
	
	private Flag[] flags;
	private Kit[] kits;
	private Zone[] spawnZones;
	private Zone[] captureZones;
	
	public Team(String name, String internalName, Color color, MapInfo map) {
		this.name = name;
		this.internalname = internalName;
		this.color = color;
		this.players = new ArrayList<>();
		this.mapinfo = map;
		this.ID = teams.size();
		teams.add(new AbstractMap.SimpleEntry<Team, String>(this, name));
		
		Bukkit.getLogger().info("[CTF] Generating Team " + toString());
	}
	
	public void setMaxPlayers(int maxPlayers) {
		this.maxplayers = maxPlayers;
	}
	
	public Location getSpawnRandom() {
		Zone pickedZone = spawnZones[new Random().nextInt(spawnZones.length)];
		return pickedZone.getSection()[new Random().nextInt(pickedZone.getSection().length)];
	}
	
	public Kit[] getKits() {
		return kits;
	}
	
	public int getMaxPlayers() {
		return maxplayers;
	}
	
	public void defineSpawn(Zone zone) {
		defineSpawn(new Zone[] { zone });
	}
	
	public void defineSpawn(Zone[] zones) {
		this.spawnZones = zones;
	}
	
	public void defineFlag(Flag flag) {
		flag.setTeam(this);
		defineFlags(new Flag[] { flag });
	}
	
	public void defineFlags(Flag[] flags) {
		for(Flag f : flags) f.setTeam(this);
		this.flags = flags;
	}
	
	public void defineKit(Kit kit) {
		this.kits = new Kit[] { kit };
	}
	
	public void defineKits(Kit[] kits) {
		this.kits = kits;
	}
	
	public void defineCapture(Zone zone) {
		defineCapture(new Zone[] { zone });
	}
	
	public void defineCapture(Zone[] zones) {
		this.captureZones = zones;
	}
	
	public MapInfo getMap() {
		return mapinfo;
	}
	
	public Zone[] getSpawn() {
		return spawnZones;
	}
	
	public Flag[] getFlags() {
		return flags;
	}
	
	public Zone[] getCapture() {
		return captureZones;
	}
	
	public String getInternalName() {
		return internalname;
	}
	
	public boolean areAllFlagsStolen() {
		for(Flag f : flags) if(!f.isCaptured()) return false;
		return true;
	}
	
	public boolean isFull() {
		return players.size() == maxplayers;
	}
	
	public List<UUID> getPlayersUUID() {
		return players;
	}
	
	public List<Player> getPlayers() {
		List<Player> toReturn = new ArrayList<>();
		for(UUID uuid : players) toReturn.add(Bukkit.getPlayer(uuid));
		return toReturn;
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getID() {
		return ID;
	}
	
	public void addPlayer(Player player) {
		if(players.size() + 1 >= maxplayers) return;
		if(players.contains(player.getUniqueId())) return;
		players.add(player.getUniqueId());
		Match.instance.setPlayersPlaying(Match.instance.getPlayersPlaying() + 1);
		Bukkit.getPluginManager().callEvent(new PlayerInGameStatusChangeEvent(Action.JOIN, player, this));
	}
	
	public void removePlayer(Player player) {
		if(!players.contains(player.getUniqueId())) return;
		players.remove(player.getUniqueId());
		Match.instance.setPlayersPlaying(Match.instance.getPlayersPlaying() - 1);
		Bukkit.getPluginManager().callEvent(new PlayerInGameStatusChangeEvent(Action.LEAVE, player, this));
	}
	
	public boolean hasPlayer(Player player) {
		if(players.contains(player.getUniqueId())) return true;
		return false;
	}
	
	public static Team getByName(String name) {
		for(Entry<Team, String> t : teams) {
			if(t.getValue().equalsIgnoreCase(name)) return t.getKey();
		}
		return null;
	}
	
	public static Team getByInternalNameAndMap(String name, MapInfo mapOwner) {
		for(Entry<Team, String> t : teams) {
			if(t.getValue().equalsIgnoreCase(name)) return t.getKey();
		}
		return null;
	}
	
	public static Team getByNameAndMap(String name, MapInfo mapOwner) {
		for(Entry<Team, String> t : teams) {
			if(t.getValue().equalsIgnoreCase(name) && t.getKey().getMap().equals(mapOwner)) return t.getKey();
		}
		return null;
	}
	
	@Deprecated
	public static Team getPlayerTeam(Player player) {
		for(Entry<Team, String> e : teams)
			if(e.getKey().hasPlayer(player)) return e.getKey();
		return null;
	}
	
	public static List<Entry<Team, String>> getTeams() {
		return teams;
	}
	
	public String toString() {
		return "[ID #" + ID + "] [Name " + name + "] [Color " + color.toString() + "] [MaxPlayers " + maxplayers + "]";
	}
	
}
