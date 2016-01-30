package me.netux.ctf.listeners;

import me.netux.ctf.listeners.custom.PlayerChangeBlockEvent;
import me.netux.ctf.objects.maps.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MatchListener implements Listener {
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		if(Map.getByWorldName(e.getWorld().getName()) == null) return;
		if(Map.getByWorldName(e.getWorld().getName()).getInfo().getRain() == false)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onEventActivation(PlayerMoveEvent e) {
		Location to = new Location(e.getTo().getWorld(), e.getTo().getBlockX(), e.getTo().getBlockY(), e.getTo().getBlockZ());
		Location from = new Location(e.getFrom().getWorld(), e.getFrom().getBlockX(), e.getFrom().getBlockY(), e.getFrom().getBlockZ());
		
		if(from.equals(to)) return;
		
		Bukkit.getPluginManager().callEvent(new PlayerChangeBlockEvent(e));
	}
	
}
