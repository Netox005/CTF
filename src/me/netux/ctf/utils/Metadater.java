package me.netux.ctf.utils;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class Metadater {

	private Player player;
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("CaptureTheFlag");
	
	public Metadater(Player player) {
		this.player = player;
	}
	
	public void set(String key, Object value) { player.setMetadata(key, new FixedMetadataValue(plugin, value)); }
	public MetadataValue get(String key) {
		List<MetadataValue> result = player.getMetadata(key);
		return (result.isEmpty() ? null : result.get(0)); 
	}
	public MetadataValue[] gets(String key) { return player.getMetadata(key).toArray(new MetadataValue[player.getMetadata(key).size()]); }
	
	public static void set(Player player, String key, Object value) { player.setMetadata(key, new FixedMetadataValue(plugin, value)); }
	public static MetadataValue get(Player player, String key) {  List<MetadataValue> result = player.getMetadata(key); return (result.isEmpty() ? null : result.get(0)); }
	public static MetadataValue[] gets(Player player, String key) { return player.getMetadata(key).toArray(new MetadataValue[player.getMetadata(key).size()]); }
	
}
