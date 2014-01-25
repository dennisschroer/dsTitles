package com.kaltiz.dsTitle;

import com.kaltiz.dsTitle.storage.TitleStorage;
import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.Title;
import denniss17.dsTitle.Title.Type;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;


public class TitleManager {

    private final DSTitle plugin;

    private FileConfiguration titleConfig = null;
    private File titleConfigFile = null;

    private HashMap<String,Title> prefixes = new HashMap<>();
    private HashMap<String,Title> suffixes = new HashMap<>();
    private HashMap<String,String> playerPrefixes = new HashMap<>();
    private HashMap<String,String> playerSuffixes = new HashMap<>();

    public TitleManager(DSTitle plugin)
    {
        this.plugin = plugin;
        titleConfigFile = new File(plugin.getDataFolder(), "titleConfig.yml");
        titleConfig = YamlConfiguration.loadConfiguration(titleConfigFile);
        prefixes.clear();
        suffixes.clear();
        loadTitles();
    }

    /**
     * Loads all Title into the Character Storage
     */
    private void loadTitles()
    {
        Title title;
        if(titleConfig.contains("suffixes"))
        {
            Set<String> titles = titleConfig.getConfigurationSection("suffixes").getKeys(false);
            for(String name: titles){
                title = loadTitle("suffixes", name);
                if (title != null) suffixes.put(name,title);
            }
        }

        if(titleConfig.contains("prefixes"))
        {
            Set<String> titles = titleConfig.getConfigurationSection("prefixes").getKeys(false);
            for(String name: titles){
                title = loadTitle("prefixes", name);
                if (title != null) prefixes.put(name,title);
            }
        }
    }

    /**
     * Loads a Title from the TitleConfig
     * @param type Suffix or Prefix
     * @param name name of the title to load
     * @return void
     */
    private Title loadTitle(String type,String name)
    {
        String path = type + '.' + name;

        if(path==null||path.equals("")){
            return null;
        }

        ConfigurationSection titleSection = titleConfig.getConfigurationSection(path);

        if(titleSection!=null)
        {
            String permission   = titleSection.contains("permission")   ? titleSection.getString("permission")  : null;
            String description  = titleSection.contains("description")  ? titleSection.getString("description") : null;
            String chatTag      = titleSection.contains("chattag") 	    ? titleSection.getString("chattag") 	: null;
            String headTag      = titleSection.contains("headtag") 	    ? titleSection.getString("headtag") 	: null;
            Type titleType      = type == "prefixes" ? Type.PREFIX : Type.SUFFIX;
            if (headTag!=null && headTag.length() > 16)
            {
                plugin.getLogger().warning("Title  '" + name + "has been disabled!");
                plugin.getLogger().warning("The headtag cannot be longer than 16 characters, as this would kick every online player from the server");
                return null;
            }
            return new Title(name, titleType, chatTag, headTag, permission, description);
        }
        else
        {
            plugin.getLogger().warning(name + "' not good configured and can't be used!");
            return null;
        }
    }

    /**
     * Get the Title a player has set currently as prefix
     * @param target the player
     * @return Title
     */
    public Title getPlayerPrefix(OfflinePlayer target)
    {
        if(playerPrefixes.containsKey(target.getName())){
            String title = playerPrefixes.get(target.getName());
            return prefixes.get(title);
        }
        else{
            return null;
        }
    }

    /**
     * Get the Title a player has set currently as suffix
     * @param target the player
     * @return Title
     */
    public Title getPlayerSuffix(OfflinePlayer target)
    {
        if(playerSuffixes.containsKey(target.getName())){
            String title = playerSuffixes.get(target.getName());
            return suffixes.get(title);
        }
        else{
            return null;
        }
    }

    /**
     * Sets the Players Prefix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    public void setPlayerPrefix(Title title,OfflinePlayer target)
    {
        if(plugin.getConfig().getBoolean("general.use_nametag"))
        {
            plugin.getTeamManager().getTeam(title, getPlayerSuffix(target)).addPlayer(target);
        }
        playerPrefixes.put(target.getName(), title.name);
    }

    /**
     * Sets the Players Suffix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    public void setPlayerPrefix(String title,OfflinePlayer target)
    {
        if (!prefixes.containsKey(title)){
            if (title != null)
                plugin.getLogger().info("prefix: " + title);
            else
                plugin.getLogger().info("Hello2!");

            return;
        }
        if(plugin.getConfig().getBoolean("general.use_nametag"))
        {
            Title tit = getPrefix(title);
            plugin.getTeamManager().getTeam(tit, getPlayerSuffix(target)).addPlayer(target);
        }
        playerSuffixes.put(target.getName(), title);
    }

    /**
     * Sets the Players Suffix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    public void setPlayerSuffix(Title title,OfflinePlayer target)
    {
        if(plugin.getConfig().getBoolean("general.use_nametag"))
        {
            plugin.getTeamManager().getTeam(getPlayerPrefix(target), title).addPlayer(target);
        }
        playerSuffixes.put(target.getName(), title.name);
    }

    /**
     * Sets the Players Suffix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    public void setPlayerSuffix(String title,OfflinePlayer target)
    {
        if (!suffixes.containsKey(title)) return;
        if(plugin.getConfig().getBoolean("general.use_nametag"))
        {
            Title tit = getSuffix(title);
            plugin.getTeamManager().getTeam(getPlayerPrefix(target), tit).addPlayer(target);
        }
        playerSuffixes.put(target.getName(), title);
    }

    /**
     * Clears the Players titles
     * @param target player to remove titles from
     */
    public void clearPlayerTitle(OfflinePlayer target)
    {
        playerPrefixes.remove(target.getName());
        playerSuffixes.remove(target.getName());
    }

    /**
     * Get an ordered set of prefixes.
     * @return SortedSet: a sorted set of all suffixes
     */
    public SortedSet<Title> getPrefixes()
    {
        return new TreeSet<Title>(prefixes.values());
    }

    /**
     * Get an ordered set of suffixes.
     * @return SortedSet: a sorted set of all suffixes
     */
    public SortedSet<Title> getSuffixes()
    {
        return new TreeSet<Title>(suffixes.values());
    }

    /**
     * Check if a prefix with this name exists
     * @param name The name of the title to search for
     * @return true if it exists, false otherwise
     */
    public boolean prefixExists(String name)
    {
        return prefixes.containsKey(name);
    }

    /**
     * Check if a suffix with this name exists
     * @param name The name of the title to search for
     * @return true if it exists, false otherwise
     */
    public boolean suffixExists(String name)
    {
        return suffixes.containsKey(name);
    }

    /**
     * Gets a Prefix
     * @param name The name of the suffix
     * @return A Title instance
     * @ensure result.name.equals(name)
     */
    public Title getPrefix(String name){
        if(name==null||name.equals(""))
            return null;

        return prefixes.get(name);
    }

    /**
     * Gets a Suffix
     * @param name The name of the suffix
     * @return A Title instance
     * @ensure result.name.equals(name)
     */
    public Title getSuffix(String name){
        if(name==null||name.equals(""))
            return null;

        return suffixes.get(name);
    }

    public void saveTitle(Title title)
    {
        // Get path
        String path;
        if(title.type.equals(Type.PREFIX))
            path = "prefixes." + title.name + ".";
        else
            path = "suffixes." + title.name + ".";

        // Save title
        titleConfig.set(path + "permission", title.permission);
        titleConfig.set(path + "description", title.description);
        titleConfig.set(path + "chattag", title.chatTag);
        titleConfig.set(path + "headtag", title.headTag);

        saveTitleConfig();

        // Update it in the Hashmaps
        if(title.type.equals(Type.PREFIX))
            prefixes.put(title.name,title);
        else
            suffixes.put(title.name,title);

        // Reload headtags
        plugin.getTeamManager().reloadTags();
    }

    protected void saveTitleConfig() {
        if (titleConfig == null || titleConfigFile == null) {
            return;
        }
        try {
            titleConfig.save(titleConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + titleConfigFile, ex);
        }
    }
}



