package me.netux.ctf.commands;

import java.util.Random;

import me.netux.ctf.configs.ConfigFile;
import me.netux.ctf.configs.MessagesFile;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.Match.FinishReason;
import me.netux.ctf.objects.maps.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String slash, String[] args) {
		if(!sender.isOp()) { /* TODO: Remove Only OP permission. Add 'ctw.admin' permission */
			sender.sendMessage("§c[CTF] §7You must be an Operator to use this command.");
			return true;
		}
		if(args.length == 0) {
			sender.sendMessage("§c[CTF] §7Use /" + slash + " <subcommand/help>");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("start")) {
			if(Match.instance.isStarted()) {
				sender.sendMessage("§c[CTF] §7Match already started.");
				return true;
			}
			if(args.length == 1) Match.instance.forceStart();
			else {
				try { Boolean.parseBoolean(args[1]); } catch(Exception e) {
					sender.sendMessage("§c[CTF] §7Use true or false.");
					return true;
				}
				boolean force = Boolean.parseBoolean(args[1]);
				if(force == true) Match.instance.forceStart();
				else Match.instance.attemptToStart();
			}
		} else if(args[0].equalsIgnoreCase("finish")) {
			if(!Match.instance.isStarted()) {
				sender.sendMessage("§c[CTF] §7Match not started.");
				return true;
			}
			Match.instance.finish(null, FinishReason.FORCED);
		} else if(args[0].equalsIgnoreCase("rot")) {
			if(Match.instance.isStarted()) Match.instance.finish(null, FinishReason.FORCED);
			if(args.length == 1 || args[1].equalsIgnoreCase("next")) {
				Match.instance.forceCicle(Match.instance.getCurrentMap().getID() + 1);
				return true;
			}
			if(args[1].equalsIgnoreCase("last")) Match.instance.forceCicle(Match.instance.getCurrentMap().getID() - 1);
			else if(args[1].equalsIgnoreCase("random")) Match.instance.forceCicle(new Random().nextInt(Map.getCycle().size()));
			else if(args[1].equalsIgnoreCase("add")) {
				if(args.length == 2) {
					sender.sendMessage("§c[CTF] §7Use /" + slash + " add <map name>");
					return true;
				}
				sender.sendMessage("§c[CTF] §7SoonTM");
			} else if(args[1].equalsIgnoreCase("remove")) {
				if(args.length == 2) {
					sender.sendMessage("§c[CTF] §7Use /" + slash + " remove <map name>");
					return true;
				}
				sender.sendMessage("§c[CTF] §7SoonTM");
			} else if(args[1].equalsIgnoreCase("reload")) {
				if(args.length == 2) {
					sender.sendMessage("§c[CTF] §7Use /" + slash + " reload <map name>");
					return true;
				}
				sender.sendMessage("§c[CTF] §7SoonTM");
			} else {
				try {
					Match.instance.forceCicle(Integer.parseInt(args[1]) - 1);
				} catch(NumberFormatException e) {
					sender.sendMessage("§6[CTF] §cUse next, last, random or the name of the map.");
				}
			}
		} else if(args[0].equalsIgnoreCase("reloadfile")) {
			if(args.length == 1) {
				sender.sendMessage("§6[CTF] §cUse config or messages");
				return true;
			}
			if(args[1].equalsIgnoreCase("config")) {
				new ConfigFile().reload();
				sender.sendMessage("§6[CTF] §7ConfigFile reloaded!");
			} else if(args[1].equalsIgnoreCase("messages")) {
				new MessagesFile().reload();
				sender.sendMessage("§6[CTF] §7MessagesFile reloaded!");
			} else sender.sendMessage("§6[CTW] §cUse config or messages");
		} else {
			sender.sendMessage(new String[] {
				"§6[CTF] §fGame Command Help:",
				"     - /" + slash + " start [force] | Start or ForceStart the game.",
				"     - /" + slash + " finish | ForceFinish the game",
				"     - /" + slash + " rot [random/next/last/map ID/add/remove/reload] ... | ForceCicle to a random/the next/the named map.",
				"     - /" + slash + " reloadfile [config/messages] | Reload the Config/Messages file."
			});
		}
		/* TODO: Make more Admin Commands */
		return true;
	}

}
