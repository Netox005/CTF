package me.netux.ctf.listeners;

import java.util.Map.Entry;

import me.netux.ctf.objects.Flag;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.User;
import me.netux.ctf.objects.Match.FinishReason;
import me.netux.ctf.objects.zones.Zone;
import me.netux.ctf.objects.Team;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlagListener implements Listener {
	
	@EventHandler
	public void onFlagInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.STANDING_BANNER) return;
		User g = User.getUser(e.getPlayer());
		
		Flag currentFlag = Flag.getByLocation(e.getClickedBlock().getLocation());
		if(currentFlag != null) {
			e.setCancelled(true);
			if(!Match.instance.isStarted()) return;
			if(g.getTeam() == null) return;
			if(!currentFlag.getTeam().hasPlayer(e.getPlayer())) {
				if(g.isStoling()) {
					e.getPlayer().sendMessage("§c[CTW] §7You are already carring a Flag!");
					return;
				}
				if(currentFlag.getDamage() == 4) {
					if(currentFlag.isCaptured()) {
						e.getPlayer().sendMessage("§6[CTF] §5" + currentFlag.getTeam().getName() + "'s " + currentFlag.getName() + " already captured!");
						return;
					}
					if(currentFlag.isStolen()) {
						e.getPlayer().sendMessage("§6[CTF] §5" + currentFlag.getTeam().getName() + "'s " + currentFlag.getName() + " already stolen by " + currentFlag.getLastStoler().getName() + "!");
						return;
					}
					currentFlag.setStolen(e.getPlayer());
					currentFlag.doDamage();
					e.getPlayer().sendMessage("§6[CTF] §5" + currentFlag.getTeam().getName() + "'s " + currentFlag.getName() + " stolen!");
				} else currentFlag.doDamage();
			}
		}
	}
	
	@EventHandler
	public void onFlagDrops(PlayerDeathEvent e) { onPlayerDropsFlag(e.getEntity()); }
	
	@EventHandler
	public void onFlagDrops(PlayerQuitEvent e) { onPlayerDropsFlag(e.getPlayer()); }
	
	private void onPlayerDropsFlag(Player player) {
		for(final Entry<Flag, Location> f : Flag.getFlags()) {
			if(f.getKey().isStolen() && f.getKey().getLastStoler().equals(player) && !f.getKey().isCaptured()) {
				if(player.getLocation().getBlockY() < 3) {
					f.getKey().setOnBase();
					User.getUser(Bukkit.getOfflinePlayer(f.getKey().getLastStolersUUID()).getPlayer()).setStoling(false);
					Bukkit.broadcastMessage("§6[CTF] §5" + f.getKey().getTeam().getName() + "'s " + f.getKey().getName() + " has been §orecovered§5 to its base.");
					return;
				}
				if(player.getLocation().getBlock().getType() == Material.LADDER || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
					Block cB = player.getLocation().getBlock();
					boolean negative = true;
					for(int y = cB.getY(); y > 0; y--) {
						cB = cB.getRelative(BlockFace.DOWN);
						if(cB.getType() != Material.LADDER && cB.getType() != Material.AIR) {
							negative = false;
							f.getKey().setIdle(cB.getLocation().add(0.0, 1.0, 0.0));
							Bukkit.broadcastMessage("§6[CTF] §5" + player.getName() + " dropped " + f.getKey().getTeam().getName() + "'s " + f.getKey().getName() + " at " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
							return;
						}
					}
					if(negative) {
						f.getKey().setOnBase();
						User.getUser(Bukkit.getOfflinePlayer(f.getKey().getLastStolersUUID()).getPlayer()).setStoling(false);
						Bukkit.broadcastMessage("§6[CTF] §5" + f.getKey().getTeam().getName() + "'s " + f.getKey().getName() + " has been §orecovered§5 to its base.");
						return;
					}
				}
				
				f.getKey().setIdle(player.getLocation());
				Bukkit.broadcastMessage("§6[CTF] §5" + player.getName() + " dropped " + f.getKey().getTeam().getName() + "'s " + f.getKey().getName() + " at " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
			}
		}
	}
	
	@EventHandler
	public void onFlagPickup(PlayerMoveEvent e) {
		Location to = new Location(e.getTo().getWorld(), e.getTo().getBlockX(), e.getTo().getBlockY(), e.getTo().getBlockZ());
		Location from = new Location(e.getFrom().getWorld(), e.getFrom().getBlockX(), e.getFrom().getBlockY(), e.getFrom().getBlockZ());
		if(equals(to, from)) return;
		
		for(Entry<Flag, Location> f : Flag.getFlags()) {
			if(f.getKey().isIdle() && equals(to, f.getKey().getLastIdleLocation())) {
				f.getKey().removeIdleBlocks();
				if(!f.getKey().getTeam().hasPlayer(e.getPlayer())) {
					f.getKey().setStolen(e.getPlayer());
					Bukkit.broadcastMessage("§6[CTF] §5" + f.getKey().getTeam().getName() + "'s " + f.getKey().getName() + " has been §lrestolen §5by " + e.getPlayer().getName());
				} else {
					f.getKey().setOnBase();
					Bukkit.broadcastMessage("§6[CTF] §5" + f.getKey().getTeam().getName() + "'s " + f.getKey().getName() + " has been §orecovered§5 to its base.");
				}
			}
		}
	}
	
	@EventHandler
	public void onFlagCapture(PlayerMoveEvent e) {
		Location to = new Location(e.getTo().getWorld(), e.getTo().getBlockX(), e.getTo().getBlockY(), e.getTo().getBlockZ());
		Location from = new Location(e.getFrom().getWorld(), e.getFrom().getBlockX(), e.getFrom().getBlockY(), e.getFrom().getBlockZ());
		
		if(to.equals(from)) return;
		User g = User.getUser(e.getPlayer());
		
		if(g.getTeam() == null || !g.isStoling()) return;
		Team team = g.getTeam();
		
		for(Zone z : team.getCapture())
			for(Location l : z.getSection()) if(equals(l, to)) {
				for(Entry<Flag, Location> f : Flag.getFlags()) {
					if(f.getKey().isStolen() && f.getKey().getLastStoler().equals(e.getPlayer())) {
						f.getKey().setCaptured();
						Bukkit.broadcastMessage("§6[CTF] §5" + e.getPlayer().getName() + " captured " + f.getKey().getName() + " from " + f.getKey().getTeam().getName() + "'s base!");
	
						if(f.getKey().getTeam().areAllFlagsStolen()) Match.instance.finish(team, FinishReason.WINNER);
					}
				}
			}
	}
	
	
	private boolean equals(Location a, Location b) {
		Location a1 = new Location(a.getWorld(), a.getBlockX(), a.getBlockY(), a.getBlockZ(), 0, 0);
		Location b1 = new Location(b.getWorld(), b.getBlockX(), b.getBlockY(), b.getBlockZ(), 0, 0);
		return a1.equals(b1);
	}
}
