package me.netux.ctf.objects.maps;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;

public class MapInfo {

	private String name;
	private String worldname;
	private String[] authors;
	private Location spawn;
	private boolean rain;
	private boolean timemove;
	
	public MapInfo(String name, String worldName, String[] authors, Location spawn) {
		this.name = name;
		this.worldname = worldName;
		this.authors = authors;
		this.spawn = spawn;
		this.rain = false;
		this.timemove = true;
		
		getWorld().setDifficulty(Difficulty.NORMAL);
		getWorld().setMonsterSpawnLimit(0);
		getWorld().setAnimalSpawnLimit(0);
		getWorld().setWaterAnimalSpawnLimit(0);
		getWorld().setGameRuleValue("doFireTick", "false");
	}
	
	public String getName() {
		return name;
	}
	
	public String getWorldName() {
		return worldname;
	}
	
	public World getWorld() {
		return Bukkit.getWorld(worldname);
	}
	
	public String getAuthors() {
		if(this.authors.length == 1) {
			if(authors[0].equals("") || authors[0].equals(null)) return "§cUnkwnown";
			return "§a" + authors[0];
		}
		StringBuilder authors = new StringBuilder();
		for(String atr : this.authors) authors.append("§a" + atr + "§7, ");
		authors.replace(authors.length() - 5, authors.length() -1, "");
		return authors.toString().trim();
	}
	
	public Location getSpawn() {
		return spawn;
	}

	public boolean getRain() {
		return rain;
	}
	
	public void setRain(boolean rain) {
		this.rain = rain;
	}
	
	public boolean getTimeMove(boolean timeMove) {
		return timemove;
	}

	public void setTimeMove(boolean timeMove) {
		this.timemove = timeMove;
		getWorld().setGameRuleValue("doDaylightCycle", String.valueOf(timeMove));
	}
	
}
