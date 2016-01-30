package me.netux.ctf.utils;

import java.util.Random;

import me.netux.ctf.configs.ConfigFile;
import me.netux.ctf.configs.Messages;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BroadcastManager {

	private static BukkitRunnable broadcast;
	
	public static void startBroadcast() {
		broadcast = new BukkitRunnable() {
			int lastIndex = 0;
			@Override
			public void run() {
				Messages msgs = Messages.instance;
				String[] messages = msgs.getMessages("game,broadcast,messages");
				if(msgs.getMessage("game,broadcast,change-status").equals("ORDER")) {
					if(messages.length < lastIndex + 1) lastIndex = 0;
					Bukkit.broadcastMessage(messages[lastIndex]);
					lastIndex++;
				} else Bukkit.broadcastMessage(messages[new Random().nextInt(messages.length - 1)]);
			}
		};
		int time = new ConfigFile().get().getConfigurationSection("timers").getInt("broadcaster");
		broadcast.runTaskTimer(Bukkit.getPluginManager().getPlugin("CaptureTheFlag"), time + 0L, time + 0L);
	}
	
	public static void stopBroadcast() { broadcast.cancel(); }
	
}
