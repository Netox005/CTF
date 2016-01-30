package me.netux.ctf.listeners;

import me.netux.ctf.main;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventRegister {
	
	Plugin p = Bukkit.getPluginManager().getPlugin("CaptureTheFlag");
	
	public EventRegister(main instance) {
		p = instance;
		register(new FlagListener());
		register(new MatchListener());
		register(new PlayerListener());
		register(new ZoneListener());
	}
	
	private void register(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, p);
	}
	
}
