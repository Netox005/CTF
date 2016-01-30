package me.netux.ctf.utils;

import me.netux.ctf.utils.Color;
import me.netux.ctf.objects.Flag;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R1.block.CraftBanner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagUtils {

	public static ItemStack asItem(Flag flag) {
		ItemStack item = new ItemStack(Material.BANNER);
		ItemMeta meta = item.getItemMeta();
		((BannerMeta) meta).setBaseColor(Color.toDyeColor(flag.getColor()));
		meta.setDisplayName(Color.toChatColor(flag.getTeam().getColor()) + flag.getTeam().getName() + "'s Flag");
		item.setItemMeta(meta);
		return item;
	}
	
	@SuppressWarnings("deprecation")
	public static BlockState[] getAndSetIdleAfterBlocks(Flag flag) {
		BlockState[] toReturn = new BlockState[] {
			flag.getLastIdleLocation().getBlock().getState(),
			flag.getLastIdleLocation().clone().subtract(0.0, 1.0, 0.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().subtract(0.0, 2.0, 0.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().subtract(0.0, 3.0, 0.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(1.0, -3.0, 0.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(-1.0, -3.0, 0.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(0.0, -3.0, 1.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(0.0, -3.0, -1.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(1.0, -3.0, 1.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(1.0, -3.0, -1.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(-1.0, -3.0, 1.0).getBlock().getState(),
			flag.getLastIdleLocation().clone().add(-1.0, -3.0, -1.0).getBlock().getState()
		};
		
		toReturn[0].getBlock().setType(Material.STANDING_BANNER);
		CraftBanner bnr = new CraftBanner(toReturn[0].getBlock());
		bnr.setBaseColor(Color.toDyeColor(flag.getColor())); bnr.setRawData(getDirection((int) flag.getLastIdleLocation().getYaw())); bnr.update(true);
		toReturn[1].getBlock().setType(Material.STAINED_GLASS);
		toReturn[1].getBlock().setData(Color.toDyeColor(flag.getColor()).getData());
		toReturn[2].getBlock().setType(Material.BEACON);
		for(int i = 3; i < toReturn.length; i++)
			toReturn[i].getBlock().setType(Material.IRON_BLOCK);
		return toReturn;
	}
	
	/** <a href="http://forums.bukkit.org/threads/direction-the-playser-faces-north-south.20773/#post-371218">From Bukkit Forums</a> */
	public static byte getDirection(int yaw) {
        if(yaw < 0) yaw += 360;
        yaw %= 360;
		return (byte) ((yaw + 8) / 22.5);
	}
	
}
