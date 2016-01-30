package me.netux.ctf.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
 
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
 
public class ConfigFile {
        private static FileConfiguration config;
        private static File cfile;
        private static Plugin p;
       
        public ConfigFile() { if(p == null) throw new NullPointerException("null plugin for config.yml"); }
        public ConfigFile(Plugin plugin) { p = plugin; }
        public FileConfiguration get() { if(config == null) reload(); return config; }
       
        public void reload() {
            if(cfile == null) cfile = new File(p.getDataFolder(), "config.yml");
            config = YamlConfiguration.loadConfiguration(cfile);
         
            try {
                Reader defMessagesStream = new InputStreamReader(p.getResource("config.yml"), "UTF8");
                if(defMessagesStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defMessagesStream);
                    config.setDefaults(defConfig);
                }
            } catch (Exception x) { p.getLogger().severe("Error trying to reload the config file."); }
        }
       
        public void save() {
            if(config == null || cfile == null) return;
            try { get().save(cfile); } catch (IOException ex) { p.getLogger().severe("Error trying to save the config file."); }
        }
       
        public void saveDefault() {
            if(cfile == null) {  cfile = new File(p.getDataFolder(), "config.yml"); }
            if(!cfile.exists()) { p.saveResource("config.yml", false); }
        }
}