package com.kaltiz.dsTitle.storage;

import com.kaltiz.dsTitle.TitleManager;
import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.Title;
import org.bukkit.OfflinePlayer;

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
