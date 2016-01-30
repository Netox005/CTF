package me.netux.ctf.listeners;

import java.util.Random;

import me.netux.ctf.utils.Color;
import me.netux.ctf.listeners.custom.PlayerChangeBlockEvent;
import me.netux.ctf.listeners.custom.PlayerInGameStatusChangeEvent;
import me.netux.ctf.listeners.custom.PlayerInGameStatusChangeEvent.Action;
import me.netux.ctf.objects.Match;
import me.netux.ctf.objects.User;
import me.netux.ctf.objects.User.StatType;
import me.netux.ctf.objects.User.ToggleSetting;
import me.netux.ctf.utils.ReasonToMessage;
import me.netux.ctf.utils.TitleUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoinFirstTime(PlayerLoginEvent e) { onPlayerJoin(e.getPlayer()); }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) { e.setJoinMessage(null); onPlayerJoin(e.getPlayer()); }
	
	public void onPlayerJoin(Player player) {
		Bukkit.getPluginManager().callEvent(new PlayerInGameStatusChangeEvent(Action.LEAVE, player, null));
		player.teleport(Match.instance.getCurrentMap().getInfo().getSpawn());
		new User(player);
	}
	
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent e) {
		e.setDroppedExp(0);
		e.getDrops().removeAll(e.getDrops());
		User.getUser(e.getEntity()).addToStat(StatType.DEATHS);
		
		DamageCause reason = e.getEntity().getLastDamageCause().getCause();
		Entity damager = e.getEntity();
		if(e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) damager = ((EntityDamageByEntityEvent) e.getEntity().getLastDamageCause()).getDamager();
		e.setDeathMessage(ReasonToMessage.get(reason, e.getEntity(), damager));
	}
	
	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e) {
		User g = User.getUser(e.getPlayer());
		if(g.getTeam() == null) {
			e.setRespawnLocation(Match.instance.getCurrentMap().getInfo().getSpawn());
			return;
		}
		e.setRespawnLocation(g.getTeam().getSpawnRandom());
		g.getKit().addLoadoutToPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerClickSlot(InventoryClickEvent e) { if(User.getUser((Player) e.getWhoClicked()).getTeam() != null) e.setCancelled(true); }
	
	@EventHandler
	public void onPvP(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
		
		User gv = User.getUser((Player) e.getEntity());
		User gd = User.getUser((Player) e.getDamager());
		
		if(gv.getTeam().equals(gd.getTeam())) { e.setCancelled(true); return; }
		if(gv.isLeavingFromMatch()) gv.setLeavingFromMatch(!gv.isLeavingFromMatch());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPvPWithProjectile(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Projectile)) return;
		
		User gv = User.getUser((Player) e.getEntity());
		User gd = User.getUser((Player) ((Projectile) e.getDamager()).getShooter());
		
		if(gv.getTeam().equals(gd.getTeam())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerChangeBlockEvent e) {
		User g = User.getUser(e.getPlayer());
		if(g.isLeavingFromMatch()) g.setLeavingFromMatch(!g.isLeavingFromMatch());
	}
	
	@EventHandler
	public void onPlayerLeft(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		e.getPlayer().getInventory().clear();
		e.getPlayer().updateInventory();
		User g = User.getUser(e.getPlayer());
		if(g.getTeam() != null) g.getTeam().removePlayer(e.getPlayer());
		Bukkit.getPluginManager().callEvent(new PlayerInGameStatusChangeEvent(Action.LEAVE, e.getPlayer(), null));
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		User g = User.getUser(e.getPlayer());
		String prefix = "§7GL";
		if(g.getToggleSetting(ToggleSetting.TEAMCHAT)) {
			e.getRecipients().clear();
			e.getRecipients().addAll(g.getTeam() == null ? Match.instance.getSpectatorsAsList() : g.getTeam().getPlayers());
			prefix = (g.getTeam() == null ? "§7" : Color.toChatColor(g.getTeam().getColor())) + "TM";
		}
		e.setFormat(prefix + " §r§l> " + e.getPlayer().getDisplayName() + " §l> §r" + e.getMessage());
	}
	
	@EventHandler
	public void onPlayerTab(PlayerChatTabCompleteEvent e) {
		if(!e.getChatMessage().contains(" ") || !e.getTabCompletions().isEmpty()) return;
		User g = User.getUser(e.getPlayer());
		g.setToggleSetting(ToggleSetting.TEAMCHAT, !g.getToggleSetting(ToggleSetting.TEAMCHAT));
		String tc = (String) (g.getTeam() == null ? "§7" : Color.toChatColor(g.getTeam().getColor()).toString());
		e.getPlayer().sendMessage("§6[CTF] §7Changed channel to " + (g.getToggleSetting(ToggleSetting.TEAMCHAT) ? tc + "TeamChannel" : "GlobalChannel"));
	}
	
	@EventHandler
	public void PlayerInGameStatusChangeEvent(PlayerInGameStatusChangeEvent e) {
		User g = User.getUser(e.getPlayer());
		if(e.getAction() == PlayerInGameStatusChangeEvent.Action.JOIN) {
			e.getPlayer().setPlayerListName(Color.toChatColor(e.getTeam().getColor()) + e.getPlayer().getName());
			e.getPlayer().setDisplayName(Color.toChatColor(e.getTeam().getColor()) + e.getPlayer().getName() + "§r");
			g.setTeam(e.getTeam());
			if(!Match.instance.isStarted()) return;
			e.getPlayer().setGameMode(GameMode.ADVENTURE);
			e.getPlayer().setHealth(20.0);
			e.getPlayer().setFoodLevel(20);
			e.getPlayer().teleport(e.getTeam().getSpawnRandom());
			if(g.getKit() == null) {
				g.setKit(g.getTeam().getKits()[new Random().nextInt(g.getTeam().getKits().length)]);
				if(!(g.getTeam().getKits().length == 1)) e.getPlayer().sendMessage("§9[CTF] §7Selected Kit §b" + g.getKit().getName());
			}
			g.getKit().addLoadoutToPlayer(e.getPlayer());
		} else {
			if(g.getTeam() != null) g.getTeam().removePlayer(e.getPlayer());
			g.setTeam(null);
			e.getPlayer().setGameMode(GameMode.SPECTATOR);
			e.getPlayer().setPlayerListName("§7" + e.getPlayer().getName());
			e.getPlayer().setDisplayName("§7" + e.getPlayer().getName() + "§r");
			for(PotionEffect pe : e.getPlayer().getActivePotionEffects())
				e.getPlayer().removePotionEffect(pe.getType());
			e.getPlayer().getInventory().clear();
			e.getPlayer().getInventory().setArmorContents(new ItemStack[] { null, null, null, null });
		}
	}
	
	@EventHandler
	public void onGamemodeChange(final PlayerGameModeChangeEvent e) {
		/* TODO: Add to messages.yml */
		if(e.getNewGameMode() != GameMode.SPECTATOR) return;
		
		new BukkitRunnable() {
			int i = 0;
			
			String[] help = new String[] {
				"§k|| §rYou are now §b§lspectating§r! §k||",
				"§k|| §rYou are now §b§lspectating§r! §k||",
				"§6§l>§a§l>§r Open your inventory and click on an item to use it §a§l<§6§l<",
				"§a§l>§6§l>§r Open your inventory and click on an item to use it §6§l<§a§l<",
				"§6§l>§a§l>§r Open your inventory and click on an item to use it §a§l<§6§l<",
				"§a§l>§6§l>§r Open your inventory and click on an item to use it §6§l<§a§l<",
				"§6§l>§a§l>§r Open your inventory and click on an item to use it §a§l<§6§l<",
				"§a§l>§6§l>§r Open your inventory and click on an item to use it §6§l<§a§l<"
			};
			
			@Override
			public void run() {
				if(i >= help.length) {
					TitleUtils.sendAboveInv("", e.getPlayer());
					this.cancel();
					return;
				}
				
				TitleUtils.sendAboveInv(help[i], e.getPlayer());
				i++;
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("CaptureTheFlag"), 0L, 10L);
	}

}
