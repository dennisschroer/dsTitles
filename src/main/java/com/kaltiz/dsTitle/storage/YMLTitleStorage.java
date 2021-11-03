package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;
import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.objects.Title;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YMLTitleStorage extends TitleStorage {

    FileConfiguration playersConfig  = null;
    File playersFile  = null;

    public YMLTitleStorage(DSTitle plugin, TitleManager manager)
    {
        super(plugin,manager);

        String file = "players.yml";

        this.playersFile = new File(plugin.getDataFolder(), file);
        plugin.saveResource(file, false);
        this.playersConfig = YamlConfiguration.loadConfiguration(playersFile);

    }

    @Override
    public void loadTitlesPlayer(OfflinePlayer target)
    {
    	if(!playersConfig.contains("players." + target.getUniqueId())){
    		// First join -> load default titles
    		manager.setPlayerPrefix(plugin.getTitleManager().titlesConfig.getDefaultPrefix(), target);
    		manager.setPlayerSuffix(plugin.getTitleManager().titlesConfig.getDefaultSuffix(), target);
    	}else{
    		manager.setPlayerPrefix(playersConfig.getString("players." + target.getUniqueId() + ".prefix"), target);
            manager.setPlayerSuffix(playersConfig.getString("players." + target.getUniqueId() + ".suffix"), target);
    	}
    }

    @Override
    public void saveTitlesPlayer(OfflinePlayer target)
    {
        Title prefix = manager.getPlayerPrefix(target);
        Title suffix = manager.getPlayerSuffix(target);

        // Titles can also be null
        playersConfig.set("players." + target.getUniqueId() + ".prefix", prefix==null ? null : prefix.name);
        playersConfig.set("players." + target.getUniqueId() + ".suffix", suffix==null ? null : suffix.name);

        // Save to file
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> savePlayerConfig(plugin, playersConfig, playersFile));
    }


    protected void savePlayerConfig(Plugin plugin, FileConfiguration playersConfig, File playersFile) {
        if (playersConfig == null || playersFile == null) {
            return;
        }
        try {
            playersConfig.save(playersFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,"Could not save config to " + playersFile, ex);
        }
    }
}
