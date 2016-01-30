package me.netux.ctf.configs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;

public class MapFile {
    private FileConfiguration mapfile;
    private File file;
    private World world;
    public MapFile(World world) {
    	this.file = new File(world.getWorldFolder().getPath(), "ctf.yml");
    	this.world = world;
    	saveDefault();
    	reload();
	}
    
    public FileConfiguration get() { if(mapfile == null) reload(); return mapfile; }

	public void reload() {
		if(!file.exists()) saveDefault();
		mapfile = YamlConfiguration.loadConfiguration(file);
	}

	public void save() {
		if(mapfile == null || file == null) return;
		try { get().save(file); } catch (IOException ex) { Bukkit.getLogger().severe("Error trying to save the MapFile for " + world.getName()); }
	}

	public void saveDefault() {
		if(file == null) { file = new File(world.getWorldFolder().getPath(), "ctf.yml"); }
		if(!file.exists()) {
			file.mkdir();
	        try {
                file.createNewFile();
                try (InputStream in = Bukkit.getPluginManager().getPlugin("CaptureTheFlag").getResource("defaultmapfile.yml");
                	OutputStream out = new FileOutputStream(file)) {
                	ByteStreams.copy(in, out);
                }
	        } catch(Exception e) { Bukkit.getLogger().severe("Error trying to save defaults of the MapFile for " + world.getName()); }
		}
	}
	
	public World getWorld() { return world; }
}
