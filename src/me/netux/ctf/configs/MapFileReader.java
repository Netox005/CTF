package me.netux.ctf.configs;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import me.netux.ctf.objects.Flag;
import me.netux.ctf.objects.Kit;
import me.netux.ctf.objects.Team;
import me.netux.ctf.objects.maps.Map;
import me.netux.ctf.objects.maps.MapInfo;
import me.netux.ctf.objects.zones.Zone;
import me.netux.ctf.objects.zones.ZoneSetting.EnumZoneSetting;
import me.netux.ctf.utils.MapUtils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MapFileReader {

	public static MapInfo getNewMapInfo(MapFile mapFile) {
		ConfigurationSection section = mapFile.get().getConfigurationSection("info");
		String name = "Map" + Map.getMapsLoaded().size();
		World world = mapFile.getWorld();
		String[] authors = new String[] { "CaptureTheFlag" };
		Location spawn = new Location(world, 0.0, 50.0, 0.0, 0, 0);
		if(section.get("display-name") != null) name = section.getString("display-name");
		if(section.get("authors") != null) authors = section.getString("authors").split(", ");
		if(section.get("spectators-spawn") != null) spawn = MapUtils.stringLocation(world, section.getString("spectators-spawn").split(", "));
		
		MapInfo info = new MapInfo(name, world.getName(), authors, spawn);
		if(section.get("rain") != null) info.setRain(section.getBoolean("rain"));
		if(section.get("time-move") != null) info.setTimeMove(section.getBoolean("time"));
		return info;
	}
	
	public static Kit getNewKit(ConfigurationSection section, MapInfo mapInfo) {
		String name = "Kit" + Kit.getKits().size();
		if(section.get("display-name") != null) name = section.getString("display-name");
		
		Kit currentKit = new Kit(name, section.getName(), mapInfo);
		
		for(String s : section.getConfigurationSection("items").getKeys(false)) {
			ConfigurationSection itemSec = section.getConfigurationSection("items").getConfigurationSection(s);
			int slot;
			try {
				slot = Integer.parseInt(itemSec.getName());
			} catch(NumberFormatException e) {
				Bukkit.getLogger().severe("[CTF] Item slot " + itemSec.getName() + " cannot be resolved. Skipping...");
				continue;
			}
			currentKit.addItem(slot, getNewItemStack(itemSec, mapInfo));
		}
		
		if(section.get("potions") != null) {
			for(String s : section.getConfigurationSection("potions").getKeys(false)) {
				ConfigurationSection potionSec = section.getConfigurationSection("potions").getConfigurationSection(s);
				currentKit.addPotion(getPotionEffectFromSection(potionSec));
			}
		}
		
		return currentKit;
	}
	
	public static Team getNewTeam(ConfigurationSection section, MapInfo mapInfo) {
		String name = "Team" + Team.getTeams().size();
		me.netux.ctf.utils.Color color = me.netux.ctf.utils.Color.BLACK;
		int maxplayers = 16;
		if(section.get("display-name") != null) name = section.getString("display-name");
		if(section.get("color") != null) color = me.netux.ctf.utils.Color.fromName(section.getString("color"));
		if(section.get("max-players") != null) maxplayers = section.getInt("max-players");
		
		Team currentTeam = new Team(name, section.getName(), color, mapInfo);
		currentTeam.setMaxPlayers(maxplayers);
		
		if(section.getStringList("spawn").size() == 0)
			currentTeam.defineCapture(Zone.getByInternalNameAndMap(section.getString("spawn"), mapInfo));
		else {
			List<Zone> tempZones = new ArrayList<>();
			for(String s : section.getStringList("spawn")) {
				Zone currentZone = Zone.getByInternalNameAndMap(s, mapInfo);
				if(currentZone == null) {
					Bukkit.getLogger().severe("[CTF] Zone " + s + " defined on the spawn list not found. Skipping...");
					return null;
				} else tempZones.add(Zone.getByInternalNameAndMap(s, mapInfo));
			}
			currentTeam.defineSpawn(tempZones.toArray(new Zone[tempZones.size()]));
		}
		if(currentTeam.getSpawn() == null) {
			Bukkit.getLogger().severe("[CTF] Spawn point for Map " + mapInfo.getName() + " not found. Skipping...");
			return null;
		}
		
		if(section.getStringList("capture").size() == 0)
			currentTeam.defineCapture(Zone.getByInternalNameAndMap(section.getString("capture"), mapInfo));
		else {
			List<Zone> tempZones = new ArrayList<>();
			for(String s : section.getStringList("capture")) {
				Zone currentZone = Zone.getByInternalNameAndMap(s, mapInfo);
				if(currentZone == null) {
					Bukkit.getLogger().severe("[CTF] Zone " + s + " defined on the capture list not found. Skipping...");
					return null;
				} else tempZones.add(Zone.getByInternalNameAndMap(s, mapInfo));
			}
			currentTeam.defineCapture(tempZones.toArray(new Zone[tempZones.size()]));
		}
		
		if(section.getStringList("kits").size() == 1) {
			Kit currentKit = Kit.getByInternalNameAndMap(section.getStringList("kits").get(0), mapInfo);
			if(currentKit == null) {
				Bukkit.getLogger().severe("[CTF] Map " + mapInfo.getName() + " has no kits. Skipping...");
				return null;
			} else currentTeam.defineKit(currentKit);
		} else {
			List<Kit> tempK = new ArrayList<>();
			for(String s : section.getStringList("kits")) {
				Kit currentKit = Kit.getByInternalNameAndMap(s, mapInfo);
				if(currentKit == null) continue;
				tempK.add(currentKit);
			}
			if(tempK.size() == 0) {
				Bukkit.getLogger().severe("[CTF] Map " + mapInfo.getName() + " has no kits. Skipping...");
				return null;
			} else currentTeam.defineKits(tempK.toArray(new Kit[tempK.size()]));
		}
		
		if(section.getStringList("flags").size() == 1) {
			Flag currentFlag = Flag.getByInternalNameAndMap(section.getStringList("flags").get(0), mapInfo);
			if(currentFlag == null) {
				Bukkit.getLogger().severe("[CTF] Map " + mapInfo.getName() + " has no flags. Skipping...");
				return null;
			} else currentTeam.defineFlag(currentFlag);
		} else {
			List<Flag> tempF = new ArrayList<>();
			for(String s : section.getStringList("flags")) {
				Flag currentFlag = Flag.getByInternalNameAndMap(s, mapInfo);
				if(currentFlag == null) continue;
				tempF.add(currentFlag);
			}
			if(tempF.size() == 0) {
				Bukkit.getLogger().severe("[CTF] Map " + mapInfo.getName() + " has no flags. Skipping...");
				return null;
			} else currentTeam.defineFlags(tempF.toArray(new Flag[tempF.size()]));
		}
		return currentTeam;
	}
	
	public static Flag getNewFlag(ConfigurationSection section, MapInfo mapInfo) {
		String name = "Flag" + Flag.getFlags().size();
		Location location = new Location(mapInfo.getWorld(), 0.0, 50.0, 0.0, 0, 0);
		if(section.get("display-name") != null) name = section.getString("display-name");
		if(section.get("location") != null) location = MapUtils.stringLocation(mapInfo.getWorld(), section.getString("location").split(", "));
		else {
			Bukkit.getLogger().severe("[CTF] Flag " + section.getName() + " on Map " + mapInfo.getName() + " has no location.");
			return null;
		}
		
		Flag currentFlag = new Flag(location, name, section.getName(), mapInfo);
		return currentFlag;
	}
	
	public static PotionEffect getPotionEffectFromSection(ConfigurationSection section) {
		PotionEffectType type = PotionEffectType.getByName(section.getName());
		int duration = 300;
		int amplifier = 1;
		if(section.get("duration") != null) duration = section.getInt("duration");
		if(section.get("amplifier") != null) amplifier = section.getInt("amplifier") - 1;
		return new PotionEffect(type, duration, amplifier);
	}
	
	public static Zone getZoneFromSection(ConfigurationSection section, MapInfo mapInfo) {
		String name = "Zone" + Zone.getZones().size();
		Location min;
		Location max;
		if(section.get("display-name") != null) name = section.getString("display-name");
		if(section.get("min") == null || section.get("max") == null) {
			Bukkit.getLogger().severe("[CTF] Zone " + section.getName() + " has no minimum and/or maximum point.");
			return null;
		} else {
			min = MapUtils.stringLocation(mapInfo.getWorld(), section.getString("min").split(", "));
			max = MapUtils.stringLocation(mapInfo.getWorld(), section.getString("max").split(", "));
		}
		
		Zone currentZone = new Zone(name, section.getName(), min, max, mapInfo);
		if(section.getConfigurationSection("settings") != null) {
			for(String s : section.getConfigurationSection("settings").getKeys(false)) {
				ConfigurationSection settingSec = section.getConfigurationSection("settings").getConfigurationSection(s);
				EnumZoneSetting settingType;
				boolean allowed = false;
				String[] whom = null;
				try {
					settingType = EnumZoneSetting.valueOf(s.toUpperCase());
				} catch(Exception e) {
					Bukkit.getLogger().severe("[CTF] Zone " + section.getName() + " has the invalid setting" + s + ". Skipping...");
					continue;
				}
				
				if(settingSec.get("allowed") != null) allowed = settingSec.getBoolean("allowed");
				else Bukkit.getLogger().severe("[CTF] Zone " + section.getName() + " has not found an allowed tag. Guessing as false.");
				if(settingSec.get("whom") != null) whom = settingSec.getStringList("whom").toArray(new String[settingSec.getStringList("whom").size()]);
				currentZone.setSetting(settingType, allowed, whom);
			}
		}
		return currentZone;
	}
	
	public static ItemStack getNewItemStack(ConfigurationSection section, MapInfo mapInfo) {
		Material material;
		int amount = 1;
		short damage = 0;
		String name = null;
		List<String> lore = null;
		List<Entry<Enchantment, Integer>> enchantments = new ArrayList<>();
		ItemMeta meta = null;
		
		if(Material.valueOf(section.getString("type")) == null) {
			Bukkit.getLogger().severe("Item " + section.getName() + " on Map " + mapInfo.getName() + " has no type.");
			return null;
		}
		
		material = Material.valueOf(section.getString("type"));
		
		if(section.get("amount") != null) amount = section.getInt("amount");
		if(section.get("durability") != null) damage = (short) section.getInt("durability");
		if(section.get("name") != null) name = section.getString("name").replaceAll("&", "§");
		if(section.get("lore") != null) {
			lore = section.getStringList("lore");
			List<String> newLore = new ArrayList<>();
			if(lore.size() == 0) newLore.add(section.getString("lore").replaceAll("&", "§"));
			else for(int index = 0; index < lore.size(); index++) newLore.add(lore.get(index).replaceAll("&", "§"));
			lore = newLore;
		}
		
		ItemStack toReturn = new ItemStack(material, amount, damage);
		
		if(material.toString().startsWith("LEATHER_")) {
			LeatherArmorMeta lmeta = (LeatherArmorMeta) toReturn.getItemMeta();
			Color color = lmeta.getColor();
			if(section.get("color") != null) {
				String[] clrs = section.getString("color").split(", ");
				color = Color.fromRGB(Integer.parseInt(clrs[0]), Integer.parseInt(clrs[1]), Integer.parseInt(clrs[2]));
			}
			lmeta.setColor(color);
			meta = lmeta;
		} else if(material.equals(Material.POTION)) {
			PotionMeta pmeta = (PotionMeta) toReturn.getItemMeta();
			List<PotionEffect> peffects = new ArrayList<>();
			PotionEffectType main = PotionEffectType.ABSORPTION;
			try { main = PotionEffectType.getByName(section.getString("main-effect")); } catch(Exception e) {
				Bukkit.getLogger().severe("Item " + section.getName() + " on Map" + mapInfo.getName() + " has an invalid main effect.");
				return null;
			}
			if(section.get("effects") != null) {
				for(String s : section.getConfigurationSection("effects").getKeys(false)) {
					ConfigurationSection effectSec = section.getConfigurationSection("effects").getConfigurationSection(s);
					PotionEffectType type;
					int duration = 30;
					int amplifier = 1;
					
					try { type = PotionEffectType.getByName(effectSec.getString("type")); } catch(Exception e) {
						Bukkit.getLogger().severe("Item " + section.getName() + " on Map " + mapInfo.getName() + "has an invalid type of effect for " + effectSec.getName() + ".");
						continue;
					}
					if(effectSec.get("duration") != null) duration = effectSec.getInt("duration");
					if(effectSec.get("amplifier") != null) amplifier = effectSec.getInt("amplifier");
					peffects.add(new PotionEffect(type, duration, amplifier));
				}
			}
			pmeta.setMainEffect(main);
			for(PotionEffect pe : peffects) pmeta.addCustomEffect(pe, true);
			meta = pmeta;
		} else if(material.equals(Material.SKULL_ITEM)) {
			SkullMeta smeta = (SkullMeta) toReturn.getItemMeta();
			String ownername = "";
			if(section.get("owner") != null) ownername = section.getString("owner");
			smeta.setOwner(ownername);
			meta = smeta;
		} else meta = toReturn.getItemMeta();
		
		if(name != null) meta.setDisplayName(name);
		if(lore != null) meta.setLore(lore);
		
		if(section.get("enchantments") != null)
			for(String s : section.getConfigurationSection("enchantments").getKeys(false)) {
				ConfigurationSection enchantSec = section.getConfigurationSection("enchantments").getConfigurationSection(s);
				Enchantment ench;
				int level = 0;
				
				ench = Enchantment.getByName(enchantSec.getName().toUpperCase());
				if(ench == null) {
					Bukkit.getLogger().severe("Item " + section.getName() + " on Map " + mapInfo.getName() + " has an invalid enchantment type on " + enchantSec.getName() + ".");
					continue;
				}
				if(enchantSec.get("level") != null) level = enchantSec.getInt("level");
				
				enchantments.add(new AbstractMap.SimpleEntry<Enchantment, Integer>(ench, level));
			}
		
		for(Entry<Enchantment, Integer> e : enchantments) meta.addEnchant(e.getKey(), e.getValue(), true);
		
		toReturn.setItemMeta(meta);
		return toReturn;
	}
	
}
