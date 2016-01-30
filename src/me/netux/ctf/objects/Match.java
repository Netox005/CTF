package me.netux.ctf.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.netux.ctf.utils.Color;
import me.netux.ctf.main;
import me.netux.ctf.configs.ConfigFile;
import me.netux.ctf.listeners.custom.PlayerInGameStatusChangeEvent;
import me.netux.ctf.listeners.custom.PlayerInGameStatusChangeEvent.Action;
import me.netux.ctf.objects.User.StatType;
import me.netux.ctf.objects.maps.Map;
import me.netux.ctf.objects.zones.Zone;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Match {

	public enum FinishReason { WINNER, TIME, FORCED }
	private enum StartFails { NOFLAGS, NOTEAMS, NOPLAYERS, NONE, UNKWNOWN };
	private ConfigurationSection config;
	private BukkitRunnable attempt;
	private BukkitRunnable anouncer;
	private BukkitRunnable cicler;
	public static Match instance;
	private int playing;
	private Map map;
	private Plugin p = Bukkit.getPluginManager().getPlugin("CaptureTheFlag");
	private Flag[] flags;
	private Team[] teams;
	private Kit[] kits;
	private Zone[] zones;
	private boolean started = false;
	private boolean ciclying = false;
	
	public Match() { }
	
	public Match(main plugin) {
		p = plugin;
		
		this.config = new ConfigFile().get().getConfigurationSection("timers");
		
		this.map = Map.getCycle().get(0);
		this.flags = map.getFlags();
		this.teams = map.getTeams();
		this.kits = map.getKits();
		this.zones = map.getZones();
		
		this.started = false;
		
		instance = this;
	}
	
	public void startCountdown() {
		final Integer[] toDisplay = new Integer[] { 60, 30, 10, 5, 4, 3, 2, 1 };
		final Integer[] flagEffect = new Integer[] { 4, 3, 2, 1 };
		
		cicler = new BukkitRunnable() { @Override public void run() { } };
		cicler.runTaskTimer(p, 0L, 20*60L);
		
		attempt = new BukkitRunnable() {
			int time = config.getInt("start");
			@Override
			public void run() {
				if(time == 0) {
					attemptToStart();
					time--;
					return;
				}
				if(time == -config.getInt("attempt")) time = 60;
				for(int i : toDisplay)
					if(i == time) Bukkit.broadcastMessage("§6[CTF] §7Game on §9" + map.getInfo().getName() + " §7starting in §a" + (time) + "§7 " + (time == 1 ? "second." : "seconds"));
				for(int i : flagEffect)
					if(i == time) for(Flag f : flags) f.setDamage(f.getDamage() - 1);
				time--;
			}
		};
		attempt.runTaskTimer(p, 0L, 20L);
	}
	
	public void attemptToStart() {
		boolean failed;
		String msg = "Couldnt start the Match."; 
		
		switch(checkToStart()) {
		default: failed = false; break;
		case NOFLAGS:
			failed = true;
			msg = "There should be at least §a1 §7Flag per Team to start the Match.";
			break;
		case NOPLAYERS:
			failed = true;
			msg = "There should be at least §a1 §7Player per Team to start the Match.";
			break;
		case NOTEAMS:
			failed = true;
			msg = "There are not any Team defined to start the Match.";
			break;
		case UNKWNOWN:
			failed = true;
			msg = "The Match coulnt start due to an unkwnown error.";
			break;
		}
		
		if(failed == true) {
			Bukkit.broadcastMessage("§c[CTF] §7" + msg);
			Bukkit.broadcastMessage("§c[CTF] §7Attempting again in §a20§7 seconds.");
			for(Flag f : flags) f.reset();
		} else forceStart();
	}
	
	@SuppressWarnings("deprecation")
	public void forceStart() {
		attempt.cancel();
		started = true;
		
		anouncer = new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage("§9[CTF] §7Currently playing §9" + map.getInfo().getName() + " §7by " + map.getInfo().getAuthors() + "§7.");
			}
		};
		anouncer.runTaskTimer(p, 20*60L, config.getInt("anouncer"));
		
		for(Flag f : flags) f.startSelfRegen();
		for(Player p : Bukkit.getOnlinePlayers()) {
			User g = User.getUser(p);
			if(g.getTeam() == null) continue;
			Bukkit.getPluginManager().callEvent(new PlayerInGameStatusChangeEvent(Action.JOIN, p, g.getTeam()));
		}
		
		Bukkit.broadcastMessage("§6[CTF] §7Game started!");
	}
	
	@SuppressWarnings("deprecation")
	public void finish(Team winner, FinishReason reason) {
		started = false;
		attempt.cancel();
		anouncer.cancel();
		Bukkit.broadcastMessage("§6[CTF] §7Game finished!");
		switch(reason) {
		case WINNER: 
			Bukkit.broadcastMessage("§6[CTF] §7Team " + Color.toChatColor(winner.getColor()) + winner.getName() + "§7 won!");
			break;
		case TIME:
			Bukkit.broadcastMessage("§6[CTF] §7Its a tie for out of time!");
			break;
		case FORCED:
			Bukkit.broadcastMessage("§6[CTF] §7Forced to finish by a Staff Member");
		}
		
		for(Flag f : flags) {
			if(f.isIdle()) f.removeIdleBlocks();
			f.stopSelfRegen();
			f.reset();
		}
		for(Entity e : map.getInfo().getWorld().getEntities()) if(e instanceof Item) e.remove();
		for(Player p : Bukkit.getOnlinePlayers()) {
			User g = User.getUser(p);
			g.setKit(null);
			if(g.isStoling()) g.setStoling(false);
			Bukkit.getPluginManager().callEvent(new PlayerInGameStatusChangeEvent(Action.LEAVE, p, null));
			
			p.sendMessage(new String[] {
				"§9[CTF] §7Final Match stats:",
				"       §7Captures: " + g.getStat(StatType.CAPTURES) + " | Steals: " + g.getStat(StatType.STEALS),
				"       §7Kills: " + g.getStat(StatType.KILLS) + " | Deaths: " + g.getStat(StatType.DEATHS)
			});
		}
		if(reason != FinishReason.FORCED)
			cicleCountdown(map.getID() + 1);
	}
	
	public void cicleCountdown(final int mapID) {
		if(started == true || ciclying == true) return;
		ciclying = true;
		
		final Integer[] toDisplay = new Integer[] { 30, 20, 10, 5, 4, 3, 2, 1 };
		
		cicler = new BukkitRunnable() {
			int time = config.getInt("cicler");
			@Override
			public void run() {
				if(time == 0) {
					forceCicle(mapID);
					time--;
					return;
				}
				for(int i : toDisplay)
					if(i == time) {
						int mapID2 = mapID;
						if(Map.getCycle().size() < (mapID + 1) || mapID < 0) mapID2 = 0;
						Bukkit.broadcastMessage("§6[CTF] §7Starting cycle to §9" + Map.getCycle().get(mapID2).getInfo().getName() + " §7in §a" + (time) + "§7 " + (time == 1 ? "second." : "seconds"));
					}
				time--;
			}
		};
		cicler.runTaskTimer(p, 0L, 20L);
	}
	
	@SuppressWarnings("deprecation")
	public void forceCicle(int mapID) {
		if(started == true) return;
		ciclying = false;
		attempt.cancel();
		cicler.cancel();
		for(Flag f : flags) f.reset();
		
		if(Map.getCycle().size() < (mapID + 1) || mapID < 0) mapID = 0;
		this.map = Map.getCycle().get(mapID);
		this.flags = map.getFlags();
		this.teams = map.getTeams();
		this.kits = map.getKits();
		this.zones = map.getZones();
		
		Bukkit.broadcastMessage("§6[CTF] §7Cycled to §9" + map.getInfo().getName() + " §7by " + map.getInfo().getAuthors() + "§7.");
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			User g = User.getUser(p);
			if(g.getTeam() != null) {
				g.getTeam().removePlayer(p);
				g.setTeam(null);
			}
			p.teleport(Map.getCycle().get(mapID).getInfo().getSpawn());
		}
		
		startCountdown();
	}
	
	private StartFails checkToStart() {
		if(playing <= teams.length - 1)
			return StartFails.NOPLAYERS;
		if(teams.length - 1 == 0)
			return StartFails.NOTEAMS;
		if(teams.length > flags.length)
			return StartFails.NOTEAMS;
		return StartFails.NONE;
	}
	
	public boolean teamsAreFull() {
		for(Team t : teams) if(!t.isFull()) return false;
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public List<Player> getSpectatorsAsList() {
		List<Player> toReturn = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers())
			if(User.getUser(p).getTeam() == null) toReturn.add(p);
		return toReturn;
	}
	
	public Player[] getSpectators() {
		return getSpectatorsAsList().toArray(new Player[getSpectatorsAsList().size()]);
	}
	
	public boolean isStarted() { return started; }
	public boolean isCiclying() { return ciclying; }
	public Flag[] getFlags() { return flags; }
	public Team[] getTeams() { return teams; }
	public Kit[] getKits() { return kits; }
	public Zone[] getZones() { return zones; }
	public List<Flag> getFlagList() { return Arrays.asList(flags); }
	public List<Team> getTeamList() { return Arrays.asList(teams); }
	public List<Kit> getKitList() { return Arrays.asList(kits); }
	public List<Zone> getZoneList() { return Arrays.asList(zones); }
	public void setPlayersPlaying(int amount) { this.playing = amount; }
	public int getPlayersPlaying() { return playing; }
	public Map getCurrentMap() { return map; }
}
