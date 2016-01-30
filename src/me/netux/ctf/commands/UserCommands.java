package me.netux.ctf.commands;

import me.netux.ctf.utils.Color;
import me.netux.ctf.configs.Messages;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.User;
import me.netux.ctf.objects.User.StatType;
import me.netux.ctf.objects.User.ToggleSetting;
import me.netux.ctf.objects.maps.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommands implements CommandExecutor {

	private Messages msgs;
	
	public UserCommands() {
		msgs = Messages.instance;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String slash, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		final Player p = (Player) sender;
		User g = User.getUser(p);
		
		if(cmd.getName().equalsIgnoreCase("rotation")) {
			p.sendMessage(msgs.getMessage("game,commands,rotation,first"));
			for(int index = 0; index < Map.getCycle().size(); index++) {
				Map m = Map.getCycle().get(index);
				if(Match.instance.getCurrentMap().equals(m))
					p.sendMessage(msgs.getMessage("game,commands,rotation,variables,current",
							new String[] { "{m:nu}", "{m:n}", "{m:a}" },
							new String[] { (index + 1) + "", m.getInfo().getName(), m.getInfo().getAuthors() }));
				else p.sendMessage(msgs.getMessage("game,commands,rotation,variables,other",
						new String[] { "{m:nu}", "{m:n}", "{m:a}" },
						new String[] { (index + 1) + "", m.getInfo().getName(), m.getInfo().getAuthors() }));
			}
		}
		if(cmd.getName().equalsIgnoreCase("stats")) {
			if(args.length == 0) {
				p.sendMessage(msgs.getMessages("game,commands,stats",
					new String[] { "{p:caps}", "{p:stls}", "{p:dths}", "{p:klls}", "{p:tcaps}", "{p:tstls}", "{p:tdths}", "{p:tklls}" },
					new String[] { g.getStat(StatType.CAPTURES) + "", g.getStat(StatType.STEALS) + "", g.getStat(StatType.DEATHS) + "", g.getStat(StatType.KILLS) + "",
						g.getStat(StatType.TOTALCAPTURES) + "", g.getStat(StatType.TOTALSTEALS) + "", g.getStat(StatType.TOTALDEATHS) + "", g.getStat(StatType.TOTALKILLS) + "" }));
				return true;
			}
			if(Bukkit.getPlayer(args[0]) == null) {
				p.sendMessage("§c[CTF] §7Player not found.");
				return true;
			}
			User gT = User.getUser(Bukkit.getPlayer(args[0]));
			p.sendMessage(msgs.getMessages("game,commands,stats",
					new String[] { "{p:caps}", "{p:stls}", "{p:dths}", "{p:klls}", "{p:tcaps}", "{p:tstls}", "{p:tdths}", "{p:tklls}" },
					new String[] { gT.getStat(StatType.CAPTURES) + "", gT.getStat(StatType.STEALS) + "", gT.getStat(StatType.DEATHS) + "", gT.getStat(StatType.KILLS) + "",
					gT.getStat(StatType.TOTALCAPTURES) + "", gT.getStat(StatType.TOTALSTEALS) + "", gT.getStat(StatType.TOTALDEATHS) + "", gT.getStat(StatType.TOTALKILLS) + "" }));
		}
		if(cmd.getName().equalsIgnoreCase("channels")) {
			g.setToggleSetting(ToggleSetting.TEAMCHAT, !g.getToggleSetting(ToggleSetting.TEAMCHAT));
			String tc = (String) (g.getTeam() == null ? "§7" : Color.toChatColor(g.getTeam().getColor()).toString());
			String c = (String) (g.getToggleSetting(ToggleSetting.TEAMCHAT) ? msgs.getMessage("game,commands,channel,team", new String[] { "{t:cc}" }, new String[] { tc }) : msgs.getMessage("game,commands,channel,global"));
			p.sendMessage(msgs.getMessage("game,commands,channel,switch") + c);
		}
		if(cmd.getName().equalsIgnoreCase("toggle")) {
			if(args.length == 0) {
				p.sendMessage("§c[CTF] §7Use /" + slash + " <toggle setting>");
				return true;
			}
			
			ToggleSetting setting = null;
			try { setting = ToggleSetting.valueOf(args[0].toUpperCase());
			} catch (Exception e) {
				StringBuilder settings = new StringBuilder();
				for(ToggleSetting t : ToggleSetting.values()) settings.append(t.toString().toLowerCase() + ", ");
				settings.replace(settings.length() - 2, settings.length(), "");
				p.sendMessage("§c[CTF] §7Invalid setting. Use " + settings.toString());
				return true;
			}
			
			g.setToggleSetting(setting, !g.getToggleSetting(setting));
			p.sendMessage("§6[CTF] §7Toggled " + setting.toString().toLowerCase() + " to " + (g.getToggleSetting(setting) ? "§atrue" : "§cfalse") + "§7.");
			return true;
		}
		return false;
	}

}
