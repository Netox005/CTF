package me.netux.ctf.utils;

import java.util.Random;

import me.netux.ctf.utils.Color;
import me.netux.ctf.objects.User;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ReasonToMessage {
	
	@SuppressWarnings("deprecation")
	public static String get(DamageCause reason, Player victim, Entity damager) {
		/* TODO: Add to messages.yml */
		String dmgr = damager == null ? "§cUnkwnown§7" : "§b" + damager.getType().toString().toLowerCase() + "§7";
		String dmgrs = damager instanceof Projectile ? Color.toChatColor(User.getUser(((Player) ((Projectile) damager).getShooter())).getTeam().getColor()) + ((Player) ((Projectile) damager).getShooter()).getName() + "§7" : "§cUnkwnown§7";
		String vtm = Color.toChatColor(User.getUser(victim).getTeam().getColor()) + victim.getName() + "§7";
		if(damager instanceof Player) dmgr = Color.toChatColor(User.getUser((Player) damager).getTeam().getColor()) + ((Player) damager).getName();
		String result = "";
		int r = 0;
		switch(reason) {
		case BLOCK_EXPLOSION:
			r = new Random().nextInt(3);
			if(r == 0) result = vtm + " died on an explotion.";
			if(r == 1) result = vtm + " blew up.";
			if(r == 2) result = vtm + " got catched up by a TNT.";
			break;
		case CONTACT:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " tried to hug a cactus.";
			if(r == 1) result = vtm + " got spiked till death.";
			break;
		case DROWNING:
			r = new Random().nextInt(3);
			if(r == 0) result = vtm + " forget to breath.";
			if(r == 1) result = vtm + " couldn't reach the surface.";
			if(r == 2) result = vtm + " tried to play Titanic with real water.";
			break;
		case ENTITY_ATTACK:
			r = new Random().nextInt(3);
			if(r == 0) result = vtm + " has been beat by " + dmgr + ".";
			if(r == 1) result = vtm + " has been defeated by " + dmgr + ".";
			if(r == 2) result = vtm + " was slain by " + dmgr + ".";
			break;
		case ENTITY_EXPLOSION:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " told a creeper to say SSSSsss...";
			if(r == 1) result = vtm + " made a creeper angry.";
			break;
		case FALL:
			r = new Random().nextInt(3);
			if(r == 0) result = vtm + " fell from the sky to its death.";
			if(r == 1) result = vtm + " tried to fly.";
			if(r == 2) result = vtm + " didnt look were he stepped.";
			break;
		case FALLING_BLOCK:
			r = new Random().nextInt(1);
			if(r == 0) result = vtm + " got scrushed by a falling object.";
			if(r == 1) result = vtm + " got hit on the head by a falling object .";
			break;
		case FIRE:
			r = new Random().nextInt(3);
			if(r == 0) result = vtm + " burn to death.";
			if(r == 1) result = vtm + " didnt check the temperature.";
			if(r == 2) result = vtm + " got overcook.";
			break;
		case FIRE_TICK:
			r = new Random().nextInt(3);
			if(r == 0) result = vtm + " burn to death.";
			if(r == 1) result = vtm + " didnt check the temperature.";
			if(r == 2) result = vtm + " got overcook.";
			break;
		case LAVA:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " burn to death.";
			if(r == 1) result = vtm + " tried to swim in lava.";
			break;
		case LIGHTNING:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " got strike by a lightning.";
			if(r == 1) result = vtm + " was enemy with Zeus.";
			break;
		case MAGIC:
			r = new Random().nextInt(15);
			if(r == 0) result = vtm + " got killed by mageeek.";
			if(r >= 1) result = vtm + " got killed by magic.";
			break;
		case MELTING: result = vtm + " died by an Unkwnown reason."; break;
		case POISON: result = vtm + " was poisoned to death."; break;
		case CUSTOM: result = vtm + " died by on an Unknown reason."; break;
		case SUICIDE: result = vtm + " killed himself."; break;
		case PROJECTILE:
			r = new Random().nextInt(5);
			if(r == 0) result = vtm + " has been shot by " + dmgrs + ".";
			if(r >= 1) result = vtm + " has been hit by " + dmgrs + "'s " + dmgr + ".";
			break;
		case STARVATION:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " forget to eat.";
			if(r == 1) result = vtm + " was too hungry.";
			break;
		case SUFFOCATION:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + "  turned the noclip off at the wrong time.";
			if(r == 1) result = vtm + " suffocated.";
			break;
		case THORNS:
			r = new Random().nextInt(6);
			if(r == 0) result = vtm + " has been beat by " + dmgr + "'s Thorns Armor.";
			if(r == 1) result = vtm + " has been defeated by " + dmgr + "'s Thorns Armor.";
			if(r == 2) result = vtm + " was slain by " + dmgr + "'s Thorns Armor.";
			break;
		case VOID:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " felt into the Nether.";
			if(r == 1) result = vtm + " felt out of the world.";
			if(r == 2) result = vtm + " died on the void.";
			break;
		case WITHER:
			r = new Random().nextInt(2);
			if(r == 0) result = vtm + " tried to fight with " + dmgr + ".";
			if(r == 1) result = vtm + " got poisoned by " + dmgr + ".";
			break;
		}
		return result;
	}
	
}
