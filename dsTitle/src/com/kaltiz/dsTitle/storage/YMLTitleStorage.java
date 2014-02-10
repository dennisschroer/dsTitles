package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;
import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YMLTitleStorage extends TitleStorage {

    FileConfiguration playersConfig  = null;
    File playersFile  = null;

    public YMLTitleStorage(DSTitle plugin, TitleManager manager)
    {
        super(plugin,manager);

        String file = plugin.getConfig().contains("storage.file") ? plugin.getConfig().getString("storage.file") : "players.yml";

        this.playersFile = new File(plugin.getDataFolder(), file);
        this.playersConfig = YamlConfiguration.loadConfiguration(playersFile);

        try {
            playersConfig.save(playersFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,"Could not save config to " + playersFile, ex);
        }
    }

    @Override
    public void loadTitlesPlayer(OfflinePlayer target)
    {
    	if(!playersConfig.contains("players." + target.getName())){
    		// First join -> load default titles
    		manager.setPlayerPrefix(plugin.getTitleManager().getDefaultPrefix(), target);
    		manager.setPlayerSuffix(plugin.getTitleManager().getDefaultSuffix(), target);
    	}else{
    		manager.setPlayerPrefix(playersConfig.getString("players." + target.getName() + ".prefix"), target);
            manager.setPlayerSuffix(playersConfig.getString("players." + target.getName() + ".suffix"), target);
    	}
    }

    @Override
    public void saveTitlesPlayer(OfflinePlayer target)
    {
        Title prefix = manager.getPlayerPrefix(target);
        Title suffix = manager.getPlayerSuffix(target);

        // Titles can also be null
        playersConfig.set("players." + target.getName() + ".prefix", prefix==null ? null : prefix.name);
        playersConfig.set("players." + target.getName() + ".suffix", suffix==null ? null : suffix.name);

        // Save to file
        savePlayerConfig();
    }


    protected void savePlayerConfig() {
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
