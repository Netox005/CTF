package me.netux.ctf.utils;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TitleUtils {
	
	public static void sendTitle(String title, Player to) {
		sendTitle(title, to, 10, 20, 10);
	}
	
	public static void sendTitle(String title, String subtitle, Player to) {
		sendTitle(title, subtitle, to, 10, 20, 10);
	}
	
	public static void sendTitle(String title, Player to, int... time) {
		IChatBaseComponent msg = ChatSerializer.a("{text:\"" + title + "\"}");
		PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, msg, time[0], time[1], time[2]);
		((CraftPlayer) to).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void sendTitle(String title, String subtitle, Player to, int... time) {
		IChatBaseComponent submsg = ChatSerializer.a("{text:\"" + subtitle + "\"}");
		IChatBaseComponent msg = ChatSerializer.a("{text:\"" + title + "\"}");
		PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, submsg);
		PacketPlayOutTitle packet2 = new PacketPlayOutTitle(EnumTitleAction.TITLE, msg, time[0], time[1], time[2]);
		((CraftPlayer) to).getHandle().playerConnection.sendPacket(packet);
		((CraftPlayer) to).getHandle().playerConnection.sendPacket(packet2);
	}
	
	public static void sendAboveInv(String aboveInv, Player to) {
		IChatBaseComponent msg = ChatSerializer.a("{text:\"" + aboveInv + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(msg, (byte) 2);
		((CraftPlayer) to).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void sendAboveInv(final String aboveInv, final Player to, final int seconds) {
		new BukkitRunnable() {
			int time = 0;
			@Override
			public void run() {
				if(time >= (seconds * 2)) {
					this.cancel();
					sendAboveInv("", to);
					return;
				}
				sendAboveInv(aboveInv, to);
				time++;
			}
		}.runTaskTimer(Bukkit.getPluginManager().getPlugin("CaptureTheFlag"), 0L, 10L);
	}
	
}
