package me.netux.ctf.commands;

import me.netux.ctf.main;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

public class CommandRegister {

	Plugin p = Bukkit.getPluginManager().getPlugin("CaptureTheFlag");
	
	public CommandRegister(main instance) {
		p = instance;
		register(new MatchCommands(), "join", "leave", "switch", "kit", "class");
		register(new GameCommand(), "game", "ctw", "ctf");
		register(new UserCommands(), "rotation", "rot", "stats", "channel", "ch", "toggle");
	}
	
	private void register(CommandExecutor executor, String... names) {
		for(String name : names) Bukkit.getPluginCommand(name).setExecutor(executor);
	}
	
}
