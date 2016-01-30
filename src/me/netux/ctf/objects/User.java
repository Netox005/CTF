package me.netux.ctf.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.netux.ctf.objects.zones.Zone;
import me.netux.ctf.utils.Metadater;

import org.bukkit.entity.Player;

public class User {

	public enum StatType { CAPTURES, STEALS, DEATHS, KILLS, KARMA, TOTALCAPTURES, TOTALSTEALS, TOTALDEATHS, TOTALKILLS, TOTALKARMA }
	public enum ToggleSetting { KITSELECTIONSUICIDE, SCOREBOARD, TEAMCHAT }
	public enum KitSelectionWay { RANDOM, RESPAWN, SELECTED };
	private static Map<Player, User> players = new HashMap<>();
	public Metadater metadater;
	private Object[] zoneSettings;
	private Team team;
	private Kit kit;
	private Zone zone;
	private int[] stats;
	private boolean[] toggles;
	
	public User(Player player) {
		if(!players.containsKey(player)) {
			this.metadater = new Metadater(player);
			this.team = null;
			this.kit = null;
			this.zone = null;
			
			this.stats = new int[StatType.values().length];
			for(int i = 0; i < stats.length; i++) stats[i] = 0;
			
			this.toggles = new boolean[ToggleSetting.values().length];
			for(int i = 0; i < toggles.length; i++) toggles[i] = false;
			
			metadater.set("leavingFromMatch", false);
			metadater.set("isStoling", false);
			
			players.put(player, this);
		}
	}
	
	public Player asPlayer() {
		for(Entry<Player, User> e : players.entrySet())
			if(e.getValue() == this) return e.getKey();
		return null;
	}
	
	public static User getUser(Player player) {
		if(players.get(player) == null) return new User(player);
		return players.get(player);
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Kit getKit() {
		return kit;
	}
	
	public void setKit(Kit kit) {
		if(toggles[0] && Match.instance.isStarted()) asPlayer().setHealth(0.0);
		
		this.kit = kit;
	}
	
	public Zone getZone() {
		return zone;
	}
	
	public Object[] getZoneCurrentSettings() {
		return zoneSettings;
	}
	
	public void setZone(Zone zone) {
		this.zone = zone;
		this.zoneSettings = zone == null ? null : zone.getFlags();
	}
	
	public void resetMatchStats() {
		for(int i = 0; i < 5; i++) stats[i] = 0;
	}

	public int getStat(StatType type) {
		return stats[type.ordinal()];
	}
	
	public void setStat(StatType type, int newStat) {
		stats[type.ordinal()] = newStat;
	}
	
	public void addToStat(StatType type) {
		stats[type.ordinal()]++;
		stats[type.ordinal() + 5]++;
	}
	
	public boolean getToggleSetting(ToggleSetting setting) {
		return toggles[setting.ordinal()];
	}
	
	public void setToggleSetting(ToggleSetting setting, boolean newSetting) {
		toggles[setting.ordinal()] = newSetting;
	}
	
	public void setLeavingFromMatch(boolean leavingFromMatch) {
		metadater.set("leavingFromMatch", leavingFromMatch);
	}
	
	public boolean isLeavingFromMatch() {
		return metadater.get("leavingFromMatch").asBoolean();
	}
	
	public boolean isStoling() {
		return metadater.get("isStoling").asBoolean();
	}

	public void setStoling(boolean isStoling) {
		metadater.set("isStoling", isStoling);
	}
	
}
