package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;
import denniss17.dsTitle.DSTitle;
import org.bukkit.OfflinePlayer;

/**
 * Abstract class specifying methods to be implemented by any class
 * responsible for storing and loading titles
 */
public abstract class TitleStorage {

    DSTitle plugin;
    TitleManager manager;

    public TitleStorage(DSTitle plugin, TitleManager manager)
    {
        this.plugin = plugin;
        this.manager = manager;
    }

    public abstract void loadTitlesPlayer(OfflinePlayer target);

    public abstract void saveTitlesPlayer(OfflinePlayer target);
}
