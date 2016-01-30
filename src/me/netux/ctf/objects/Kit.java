package me.netux.ctf.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.netux.ctf.objects.maps.MapInfo;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Kit {

	private static List<Kit> kits = new ArrayList<>();
	private int ID;
	private String name;
	private String internalname;
	private String description;
	private MapInfo mapowner;
	private Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	private PotionEffect[] potions;
	
	public Kit(String name, String internalName, MapInfo mapOwner) {
		this.name = name;
		this.internalname = internalName;
		this.mapowner = mapOwner;
		this.ID = kits.size();
		kits.add(this);
		Bukkit.getLogger().info("[CTF] Generated Kit " + toString());
	}
	
	public Kit setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public Kit setItems(Map<Integer, ItemStack> items) {
		this.items = items;
		return this;
	}
	
	public Kit addItem(int slot, ItemStack item) {
		if(item != null)
			this.items.put(slot, item);
		return this;
	}
	
	public Kit setPotions(PotionEffect[] potions) {
		this.potions = potions;
		return this;
	}
	
	public Kit addPotion(PotionEffect potion) {
		if(potions == null) {
			this.potions = new PotionEffect[] { potion };
			return this;
		}
		
		List<PotionEffect> tempP = new ArrayList<>();
		tempP.addAll(Arrays.asList(potions));
		tempP.add(potion);
		this.potions = tempP.toArray(new PotionEffect[potions.length]);
		return this;
	}
	
	public void addLoadoutToPlayer(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[] { null, null, null, null });
		for(PotionEffect pot : player.getActivePotionEffects())
			player.removePotionEffect(pot.getType());
		
		for(Entry<Integer, ItemStack> en : getGear().entrySet()) {
			if(en.getValue() == null) continue;
			if(en.getKey() < 37) player.getInventory().setItem(en.getKey(), en.getValue());
			else {
				if(en.getKey() == 37) player.getInventory().setHelmet(en.getValue());
				if(en.getKey() == 38) player.getInventory().setChestplate(en.getValue());
				if(en.getKey() == 39) player.getInventory().setLeggings(en.getValue());
				if(en.getKey() == 40) player.getInventory().setBoots(en.getValue());
			}
		}
		if(getPotions() != null) player.addPotionEffects(Arrays.asList(getPotions()));
		
		player.updateInventory();
	}
	
	public String getName() { return name; }
	public String getInternalName() { return internalname; }
	public String getDescription() { return description; }
	public Map<Integer, ItemStack> getGear() { return items; }
	public ItemStack[] getItems() { return items.values().toArray(new ItemStack[items.values().size()]); }
	public PotionEffect[] getPotions() { return potions; }
	public MapInfo getMap() { return mapowner; }
	public static List<Kit> getKits() { return kits; }
	
	public static Kit getByNameAndMap(String name, MapInfo mapOwner) {
		for(Kit k : kits)
			if(k.getName().equalsIgnoreCase(name) && k.getMap().equals(mapOwner)) return k;
		return null;
	}
	
	public static Kit getByInternalNameAndMap(String name, MapInfo mapOwner) {
		for(Kit k : kits)
			if(k.getInternalName().equalsIgnoreCase(name) && k.getMap().equals(mapOwner)) return k;
		return null;
	}
	
	public String toString() {
		return "[ID #" + ID + "] [Name " + name + "]";
	}
}
