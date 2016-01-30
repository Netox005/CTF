package me.netux.ctf.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
 
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
 
public class MessagesFile {
        private static FileConfiguration messages;
        private static File mfile;
        private static Plugin p;
       
        public MessagesFile() { if(p == null) throw new NullPointerException("null plugin for messages.yml"); }
        public MessagesFile(Plugin plugin) { p = plugin; }
        public FileConfiguration get() { if(messages == null) reload(); return messages; }
       
        public void reload() {
            if(mfile == null) mfile = new File(p.getDataFolder(), "messages.yml");
            messages = YamlConfiguration.loadConfiguration(mfile);
         
            try {
                Reader defMessagesStream = new InputStreamReader(p.getResource("messages.yml"), "UTF8");
                if(defMessagesStream != null) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defMessagesStream);
                    messages.setDefaults(defConfig);
                }
            } catch (Exception x) { p.getLogger().severe("Error trying to reload the messages file."); }
        }
       
        public void save() {
            if(messages == null || mfile == null) return;
            try { get().save(mfile); } catch (IOException ex) { p.getLogger().severe("Error trying to save the config file."); }
        }
       
        public void saveDefault() {
            if(mfile == null) {  mfile = new File(p.getDataFolder(), "messages.yml"); }
            if(!mfile.exists()) { p.saveResource("messages.yml", false); }
        }
}