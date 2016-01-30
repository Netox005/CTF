package me.netux.ctf;

import me.netux.ctf.commands.CommandRegister;
import me.netux.ctf.configs.ConfigFile;
import me.netux.ctf.configs.Messages;
import me.netux.ctf.listeners.EventRegister;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.maps.Map;
import me.netux.ctf.utils.BroadcastManager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		ConfigFile f = new ConfigFile(this);
		f.saveDefault();
		
		Map.loadMaps(f.get().getString("cycle").split(", "));
		if(!isEnabled()) return;
		
		new EventRegister(this);
		new CommandRegister(this);
		Messages msgs = new Messages(this);
		
		BroadcastManager.startBroadcast();
		new Match(this).startCountdown();
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msgs.getMessage("debug,reload"));
			p.teleport(Map.getCycle().get(0).getInfo().getSpawn());
			p.setGameMode(GameMode.SPECTATOR);
			p.setPlayerListName("§7" + p.getName());
			p.setDisplayName("§7" + p.getName() + "§r");
		}
	}
	
}
