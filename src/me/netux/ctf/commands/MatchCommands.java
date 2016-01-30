package me.netux.ctf.commands;

import java.util.Random;

import me.netux.ctf.utils.Color;
import me.netux.ctf.configs.Messages;
import me.netux.ctf.objects.Kit;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.Team;
import me.netux.ctf.objects.User;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchCommands implements CommandExecutor {

	private Messages msgs;
	
	public MatchCommands() {
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
		
		if(slash.equalsIgnoreCase("join")) {
			if(Match.instance.teamsAreFull()) {
				p.sendMessage(msgs.getMessage("game,commands,join,full-teams"));
				return true;
			}
			if(Match.instance.isCiclying()) {
				p.sendMessage(msgs.getMessage("game,commands,join,is-cycling"));
				return true;
			}
			if(g.getTeam() != null) {
				p.sendMessage(msgs.getMessage("game,commands,join,already-team",
					new String[] { "{t:cc}", "{t:n}" },
					new String[] { Color.toChatColor(g.getTeam().getColor()).toString(), g.getTeam().getName() }));
				return true;
			}
			if(args.length == 0) {
			    int small = Integer.MAX_VALUE;
			    int index = 0;
			    for(int i = 0; i < Match.instance.getTeams().length; i++) {
			    	if(Match.instance.getTeams()[i].getPlayers().size() < small) {
			    		small = Match.instance.getTeams()[i].getPlayers().size();
			    		index = i;
			    	}
			    }
			    Team teamToJoin = Match.instance.getTeams().clone()[index];
				teamToJoin.addPlayer(p);
				p.sendMessage(msgs.getMessage("game,commands,join,success",
					new String[] { "{t:cc}", "{t:n}" },
					new String[] { Color.toChatColor(teamToJoin.getColor()).toString(), teamToJoin.getName() }));
				return true;
			}
			
			if(Team.getByName(args[0]) == null || !Match.instance.getTeamList().contains(Team.getByNameAndMap(args[0], Match.instance.getCurrentMap().getInfo()))) {
				p.sendMessage(msgs.getMessage("game,commands,join,no-team", new String[] { "{a:0}" }, new String[] { args[0] }));
				return true;
			}
			Team teamToJoin = Team.getByNameAndMap(args[0], Match.instance.getCurrentMap().getInfo());
			teamToJoin.addPlayer(p);
			p.sendMessage(msgs.getMessage("game,commands,join,success",
				new String[] { "{t:cc}", "{t:n}" },
				new String[] { Color.toChatColor(teamToJoin.getColor()).toString(), teamToJoin.getName() }));
		}
		if(slash.equalsIgnoreCase("leave")) {
			if(g.getTeam() == null) {
				p.sendMessage(msgs.getMessage("game,commands,leave,no-team"));
				return true;
			}
			final Team oldTeam = g.getTeam();
			if(!Match.instance.isStarted()) {
				g.setLeavingFromMatch(false);
				oldTeam.removePlayer(p);
				p.sendMessage(msgs.getMessage("game,commands,leave,success",
					new String[] { "{ot:cc}", "{ot:n}" },
					new String[] { Color.toChatColor(oldTeam.getColor()).toString(), oldTeam.getName() }));
				return true;
			}
			p.sendMessage(msgs.getMessage("game,commands,leave,timer,start", new String[] { "{s}" }, new String[] { "5" }));
			g.setLeavingFromMatch(true);
			BukkitRunnable leavingTimer = new BukkitRunnable() {
				User g = User.getUser(p);
				int time = 5;
				@Override
				public void run() {
					if(!g.isLeavingFromMatch()) {
						p.sendMessage(msgs.getMessage("game,commands,leave,timer,failed"));
						this.cancel();
						return;
					}
					if(time == 0 && g.isLeavingFromMatch()) {
						this.cancel();
						oldTeam.removePlayer(p);
						p.sendMessage(msgs.getMessage("game,commands,leave,success",
							new String[] { "{ot:cc}", "{ot:n}" },
							new String[] { Color.toChatColor(oldTeam.getColor()).toString(), oldTeam.getName() }));
					}
					time--;
				}
			};
			leavingTimer.runTaskTimer(Bukkit.getPluginManager().getPlugin("CaptureTheFlag"), 0L, 20L);
		}
		if(slash.equalsIgnoreCase("switch")) {
			if(Match.instance.isCiclying()) {
				p.sendMessage(msgs.getMessage("game,commands,join,is-cycling"));
				return true;
			}
			final Team oldTeam = g.getTeam();
			if(oldTeam == null) {
				p.sendMessage(msgs.getMessage("game,commands,switch,no-team"));
				return true;
			}
			if(!Match.instance.isStarted()) {
				oldTeam.removePlayer(p);
				Team newTeam = null;
				
				int teamSize = Match.instance.getTeams().length - 1;
				int mapID = Match.instance.getCurrentMap().getID();
				
				if(teamSize < (oldTeam.getID() - mapID * 2) + 1) newTeam = Match.instance.getTeams()[0];
				else newTeam = Match.instance.getTeams()[(oldTeam.getID() - mapID * 2) + 1];
				
				newTeam.addPlayer(p);
				p.sendMessage(msgs.getMessage("game,commands,switch,success",
					new String[] { "{ot:cc}", "{ot:n}", "{nt:cc}", "{nt:n}" },
					new String[] { Color.toChatColor(oldTeam.getColor()).toString(), oldTeam.getName(),
								   Color.toChatColor(newTeam.getColor()).toString(), newTeam.getName() }));
				return true;
			}
			p.sendMessage(msgs.getMessage("game,commands,switch,timer,start", new String[] { "{s}" }, new String[] { "5" }));
			g.setLeavingFromMatch(true);
			BukkitRunnable leavingTimer = new BukkitRunnable() {
				User g = User.getUser(p);
				int time = 5;
				@Override
				public void run() {
					if(!g.isLeavingFromMatch()) {
						p.sendMessage(msgs.getMessage("game,commands,switch,timer,failed"));
						this.cancel();
						return;
					}
					if(time == 0 && g.isLeavingFromMatch()) {
						this.cancel();
						g.setLeavingFromMatch(false);
						oldTeam.removePlayer(p);
						Team newTeam = null;
						
						int teamSize = Match.instance.getTeams().length - 1;
						int mapID = Match.instance.getCurrentMap().getID();
						
						if(teamSize < (oldTeam.getID() - mapID * 2) + 1) newTeam = Match.instance.getTeams()[0];
						else newTeam = Match.instance.getTeams()[(oldTeam.getID() - mapID * 2) + 1];
						
						newTeam.addPlayer(p);
						p.sendMessage(msgs.getMessage("game,commands,switch,success",
							new String[] { "{ot:cc}", "{ot:n}", "{nt:cc}", "{nt:n}" },
							new String[] { Color.toChatColor(oldTeam.getColor()).toString(), oldTeam.getName(),
										   Color.toChatColor(newTeam.getColor()).toString(), newTeam.getName() }));
					}
					time--;
				}
			};
			leavingTimer.runTaskTimer(Bukkit.getPluginManager().getPlugin("CaptureTheFlag"), 0L, 20L);
		}
		if(slash.equalsIgnoreCase("kit") || slash.equalsIgnoreCase("class")) {
			/* TODO: Add KitCommand to messages.yml */
			if(g.getTeam() == null) {
				p.sendMessage("§c[CTF] §7Join a team before selecting a " + (slash.equalsIgnoreCase("kit") ? "Kit" : "Class"));
				return true;
			}
			if(g.getTeam().getKits().length == 1) {
				p.sendMessage("§c[CTF] §7This map has no " + (slash.equalsIgnoreCase("kit") ? "Kits" : "Classes") + ".");
				return true;
			}
			if(args.length == 0) {
				/* TODO: Create Kit selector menu */
				StringBuilder kitsList = new StringBuilder();
				for(Kit k : g.getTeam().getKits())
					kitsList.append("§b" + k.getName() + "§7, ");
				kitsList.replace(kitsList.length() - 2, kitsList.length() - 1, "");
				p.sendMessage(new String[] {
					"§9[CTF] §7" + (slash.equalsIgnoreCase("kit") ? "Kits " : "Classes ") + "avaliables:",
					kitsList.toString().trim(),
					"        §7Use /" + slash + " <@random/" + slash + " name> to pick a " + (slash.equalsIgnoreCase("kit") ? "Kit" : "Class")
				});
				return true;
			}
			Kit selected;
			if(args[0].equalsIgnoreCase("@random")) {
				selected = g.getTeam().getKits()[new Random().nextInt(g.getTeam().getKits().length)];
				g.setKit(selected);
				p.sendMessage(new String[] {
					"§9[CTF] §7The dice has roll, picked " + slash + " §b" + selected.getName() + "§7.",
					"        §7You will respawn with that " + (slash.equalsIgnoreCase("kit") ? "Kit" : "Class") + "."
				});
				return true;
			}
			if(Kit.getByNameAndMap(args[0], Match.instance.getCurrentMap().getInfo()) == null) {
				p.sendMessage("§c[CTF] §7Invalid " + (slash.equalsIgnoreCase("kit") ? "Kit" : "Class") + " " + args[0]);
				return true;
			}
			selected = Kit.getByNameAndMap(args[0], Match.instance.getCurrentMap().getInfo());
			g.setKit(selected);
			p.sendMessage(new String[] {
				"§9[CTF] §7Selected " + slash + " §b" + selected.getName() + "§7.",
				"        §7You will respawn with that " + (slash.equalsIgnoreCase("kit") ? "Kit" : "Class") + "."
			});
		}
		return true;
	}

	
	
}
