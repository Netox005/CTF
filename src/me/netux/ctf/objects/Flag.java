package me.netux.ctf.objects;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import me.netux.ctf.utils.Color;
import me.netux.ctf.objects.User.StatType;
import me.netux.ctf.objects.maps.MapInfo;
import me.netux.ctf.utils.FlagUtils;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftBanner;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Flag {

	public enum FlagState {
		STOLEN, IDLE, ONBASE, CAPTURED;
		public String toString() {
			switch (this) {
				case STOLEN: return "Stolen";
				case IDLE: return "Idle";
				case ONBASE: return "On Base";
				case CAPTURED: return "Captured";
			}
			return null;
		}
	}
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("CaptureTheFlag");
	private static List<Entry<Flag, Location>> flags = new ArrayList<>();
	private BukkitRunnable repair;
	private MapInfo mapowner;
	private int ID;
	private Location loc;
	private int damage;
	private String name;
	private String internalname;
	private FlagState state;
	private Location idleLocation;
	private Item idleItem;
	private boolean dontRegen;
	private UUID stoler;
	private Team team;
	private Color color;
	private boolean invulnerable;
	private BlockState[] idleAfterBlocks;
	
	public Flag(Location location, String name, String internalName, MapInfo mapOwner) {
		for(Entry<Flag, Location> entry : flags) {
			if(location.getBlock().getLocation().equals(entry.getValue())) {
				Bukkit.getLogger().info("[CTF] Attempted to create a Flag when a Flag is already made on that location.");
				return;
			}
		}
		if(location.getBlock() == null) location.getBlock().setType(Material.PISTON_EXTENSION);
		this.loc = location.getBlock().getLocation();
		this.internalname = internalName;
		this.mapowner = mapOwner;
		this.name = name;
		this.damage = 4;
		this.state = FlagState.ONBASE;
		this.dontRegen = false;
		this.invulnerable = false;
		this.ID = flags.size();
		flags.add(new AbstractMap.SimpleEntry<Flag, Location>(this, loc));
		
		if(!location.getBlock().getType().equals(Material.STANDING_BANNER)) location.getBlock().setType(Material.STANDING_BANNER);
		CraftBanner banner = new CraftBanner(loc.getBlock());
		banner.setBaseColor(DyeColor.WHITE); banner.setPatterns(Arrays.asList(new Pattern[] { }));
		banner.setRawData((byte) FlagUtils.getDirection((int) location.getYaw()));
		banner.update(true);
		
		Bukkit.getLogger().info("[CTF] Created Flag " + toString());
	}
	
	public void remove() {
		for(int index = 0; index < flags.size(); index++) {
			if(flags.get(index).getKey().equals(this)) {
				flags.remove(index);
				loc.getBlock().setType(Material.AIR);
				Bukkit.getLogger().info("[CTF] Removed Flag " + toString());
				return;
			}
		}
	}
	
	public void startSelfRegen() {
		if(team == null) return;
		repair = new BukkitRunnable() {
			@Override
			public void run() {
				if(dontRegen == true) { dontRegen = false; return; }
				if(isUnaccessible()) return;
				
				setDamageRepresentation(damage);
				if(damage > 0) damage = damage - 1;
			}
		};
		repair.runTaskTimer(plugin, 0L, 20L);
	}
	
	public void stopSelfRegen() {
		repair.cancel();
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
		setDamageRepresentation(damage);
	}
	
	public void setTeam(Team team) {
		this.team = team;
		this.color = team.getColor();
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public MapInfo getMap() {
		return mapowner;
	}
	
	public String getInternalName() {
		return internalname;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getID() {
		return ID;
	}
	
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public void doDamage() {
		if(damage == 5) { this.state = FlagState.STOLEN; return; }
		this.damage = this.damage + 1;
		setDamageRepresentation(this.damage);
		dontRegen = true;
		invulnerable = true;
	}
	
	public void reset() {
		if(Match.instance.isStarted()) stopSelfRegen();
		this.damage = 0;
		this.state = FlagState.ONBASE;
		setDamageRepresentation(damage);
	}
	
	public FlagState getState() {
		return state;
	}
	
	public boolean isStolen() {
		return state == FlagState.STOLEN;
	}
	
	public UUID getLastStolersUUID() {
		return stoler;
	}
	
	public Player getLastStoler() {
		return Bukkit.getPlayer(stoler);
	}
	
	public void setStolen(Player who) {
		this.state = FlagState.STOLEN;
		this.stoler = who.getUniqueId();
		User g = User.getUser(who);
		g.setStoling(true);
		g.addToStat(StatType.STEALS);
		who.getInventory().setHelmet(FlagUtils.asItem(this));
		who.updateInventory();
	}
	
	public boolean isIdle() {
		return state == FlagState.IDLE;
	}
	
	public void setIdle(Location idleLocation) {
		this.state = FlagState.IDLE;
		this.idleLocation = idleLocation;
		this.idleAfterBlocks = FlagUtils.getAndSetIdleAfterBlocks(this);
		
		User.getUser(Bukkit.getOfflinePlayer(stoler).getPlayer()).setStoling(false);
	}
	
	@SuppressWarnings("deprecation")
	public void removeIdleBlocks() {
		for(int i = 0; i < idleAfterBlocks.length; i++) {
			idleAfterBlocks[i].getBlock().setType(idleAfterBlocks[i].getType());
			idleAfterBlocks[i].getBlock().setData(idleAfterBlocks[i].getData().getData());
		}
	}
	
	public Location getLastIdleLocation() {
		return idleLocation;
	}
	
	public Item getIdleItem() {
		return idleItem;
	}
	
	public boolean isCaptured() {
		return state == FlagState.CAPTURED;
	}
	
	public void setCaptured() {
		this.state = FlagState.CAPTURED;
		
		User g = User.getUser(Bukkit.getOfflinePlayer(stoler).getPlayer());
		g.setStoling(false);
		g.addToStat(StatType.CAPTURES);
		if(g.asPlayer().isOnline() && g.getKit().getGear().containsKey(37)) {
			g.asPlayer().getInventory().setHelmet(g.getKit().getGear().get(37));
		}
	}
	
	public boolean isOnBase() {
		return state == FlagState.ONBASE;
	}
	
	public void setOnBase() {
		this.state = FlagState.ONBASE;
		this.damage = 0;
		if(idleItem != null) idleItem.remove();
	}
	
	public boolean isUnaccessible() {
		if(state == FlagState.CAPTURED || state == FlagState.IDLE || state == FlagState.STOLEN) return true;
		return false;
	}
	
	public static List<Entry<Flag,Location>> getFlags() {
		return flags;
	}
	
	public static Flag getByInternalNameAndMap(String name, MapInfo mapOwner) {
		for(Entry<Flag, Location> e : flags) {
			if(e.getKey().getInternalName().equalsIgnoreCase(name) && e.getKey().getMap().equals(mapOwner)) return e.getKey();
		}
		return null;
	}
	
	public static Flag getByLocation(Location location) {
		Flag f = null;
		for(Entry<Flag, Location> entries : flags) {
			if(entries.getValue().equals(location)) {
				f = entries.getKey();
				break;
			}
		}
		return f;
	}
	
	public static void removeByID(int ID) {
		for(int index = 0; index < flags.size(); index++) {
			if(flags.get(index).getKey().getID() == ID) {
				flags.remove(index);
				Bukkit.getLogger().info("§6[CTW] §5Removed Flag " + flags.get(index).getKey().toString());
				return;
			}
		}
	}
	
	public static void removeByLocation(Location location) {
		for(int index = 0; index < flags.size(); index++) {
			if(flags.get(index).getValue().equals(location.getBlock().getLocation())) {
				flags.remove(index);
				Bukkit.getLogger().info("§6[CTF] §5Removed Flag " + flags.get(index).getKey().toString());
				return;
			}
		}
	}
	
	public static void remove(Flag flag) {
		for(int index = 0; index < flags.size(); index++) {
			if(flags.get(index).getKey().equals(flag)) {
				flags.remove(index);
				Bukkit.getLogger().info("§6[CTF] §5Removed Flag " + flags.get(index).getKey().toString());
				return;
			}
		}
	}
	
	private void setDamageRepresentation(int damage) {
		CraftBanner banner = new CraftBanner(loc.getBlock());
		switch(damage) {
		case 0:
			banner.setBaseColor(Color.toDyeColor(color));
			banner.setPatterns(Arrays.asList(new Pattern[] { }));
			break;
		case 1:
			banner.setBaseColor(Color.toDyeColor(color));
			banner.setPatterns(Arrays.asList(new Pattern[] { new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP) }));
			break;
		case 2:
			banner.setBaseColor(DyeColor.WHITE);
			banner.setPatterns(Arrays.asList(new Pattern[] { new Pattern(Color.toDyeColor(color), PatternType.HALF_HORIZONTAL_MIRROR) }));
			break;
		case 3:
			banner.setBaseColor(DyeColor.WHITE);
			banner.setPatterns(Arrays.asList(new Pattern[] { new Pattern(Color.toDyeColor(color), PatternType.STRIPE_BOTTOM) }));
			break;
		case 4:
			banner.setBaseColor(DyeColor.WHITE);
			banner.setPatterns(Arrays.asList(new Pattern[] { }));
			break;
		case 5:
			User g = User.getUser(Bukkit.getOfflinePlayer(stoler).getPlayer());
			DyeColor stolerTeam = Color.toDyeColor(g.getTeam().getColor());
			banner.setBaseColor(DyeColor.WHITE);
			banner.setPatterns(Arrays.asList(new Pattern[] { new Pattern(stolerTeam, PatternType.CIRCLE_MIDDLE) }));
			break;
		}
		banner.update(true);
	}
	
	public String toString() {
		return "[ID #" + ID + "] [Name " + name + "] [Location " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "]";
	}
	
}
