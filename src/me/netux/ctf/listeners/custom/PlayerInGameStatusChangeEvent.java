package me.netux.ctf.listeners.custom;

import me.netux.ctf.objects.Team;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInGameStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Team team;
    private Player player;
    private Action action;
    public enum Action { JOIN, LEAVE };
 
    public PlayerInGameStatusChangeEvent(Action action, Player player, Team team) {
        this.action = action; this.player = player; this.team = team;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

	public Team getTeam() {
		return team;
	}

	public Player getPlayer() {
		return player;
	}

	public Action getAction() {
		return action;
	}

}
