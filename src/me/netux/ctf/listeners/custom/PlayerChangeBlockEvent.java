package me.netux.ctf.listeners.custom;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerChangeBlockEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Location originalFrom;
	private Location to;
	private Location toignore;
	private Location from;
	private Location fromignore;
	private Player player;
	private boolean canceled = false;
	
	public PlayerChangeBlockEvent(PlayerMoveEvent event) {
		this.to = new Location(event.getTo().getWorld(), event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ(), event.getTo().getYaw(), event.getTo().getPitch());
		this.from = new Location(event.getFrom().getWorld(), event.getFrom().getBlockX(), event.getFrom().getBlockY(), event.getFrom().getBlockZ(), event.getFrom().getYaw(), event.getFrom().getPitch());
		this.originalFrom = event.getFrom();
		this.toignore = new Location(event.getTo().getWorld(), event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ(), 0, 0);
		this.fromignore = new Location(event.getFrom().getWorld(), event.getFrom().getBlockX(), event.getFrom().getBlockY(), event.getFrom().getBlockZ(), 0, 0);
		this.player = event.getPlayer();
	}
	
	@Override
	public boolean isCancelled() { return canceled; }

	@Override
	public void setCancelled(boolean canceled) { this.canceled = canceled; player.teleport(originalFrom); }

	public Location getTo() { return to; }
	public Location getToIgnoreHeadRotation() { return toignore; }
	public Location getFrom() { return from; }
	public Location getFromIgnoreHeadRotation() { return fromignore; }
	public Player getPlayer() { return player; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
}
