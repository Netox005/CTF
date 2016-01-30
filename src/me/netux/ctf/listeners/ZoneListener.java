package me.netux.ctf.listeners;

import java.util.Arrays;

import me.netux.ctf.listeners.custom.PlayerChangeBlockEvent;
import me.netux.ctf.objects.User;
import me.netux.ctf.objects.zones.Zone;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ZoneListener implements Listener {

	@EventHandler
	public void onZoneEnter(PlayerChangeBlockEvent e) {
		User g = User.getUser(e.getPlayer());
		if(g.getTeam() == null) return;
		/* BRB KILLING MYSELF */
		for(Zone z : Zone.getZones()) {
			if(Arrays.asList(z.getSectionIgnoreHeadRotation()).contains(e.getToIgnoreHeadRotation()) && g.getZone() != z) {
				if(!z.getFlags()[1].isAllowed())
					if(z.getFlags()[1].getWhomList() != null)
						for(String s : z.getFlags()[1].getWhomList())
							if(s.equals(g.getTeam().getInternalName())) {
								e.setCancelled(true);
								return;
							}
				else g.setZone(z);
				return;
			}
			g.setZone(null);
		}
	}
	
	@EventHandler
	public void onZoneBuild(BlockBreakEvent e) {
		User g = User.getUser(e.getPlayer());
		if(g.getTeam() == null) return;
		/* BRB KILLING MYSELF */
		for(Zone z : Zone.getZones()) {
			if(Arrays.asList(z.getSectionIgnoreHeadRotation()).contains(e.getBlock().getLocation()) && g.getZone() != z) {
				if(!z.getFlags()[2].isAllowed())
					if(z.getFlags()[2].getWhomList() != null)
						for(String s : z.getFlags()[2].getWhomList())
							if(s.equals(g.getTeam().getInternalName())) {
								e.setCancelled(true);
								e.getPlayer().sendMessage("§cNOPE."); // TODO
								return;
							}
				else g.setZone(z);
				return;
			}
			g.setZone(null);
		}
	}
	
}
