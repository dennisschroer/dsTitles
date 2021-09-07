package com.kaltiz.dsTitle;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.objects.Prefix;
import denniss17.dsTitle.objects.Suffix;
import denniss17.dsTitle.objects.Title;

/**
 * This class is responsible for managing titles of players.
 * It also functions as a buffer to avoid unnecessary memory access.
 * @author Kaltiz (and denniss17)
 */
public class TitleManager {
	/**
	 * The main plugin object
	 */
    private final DSTitle plugin;
    public TitleConfig titlesConfig;

    /** Buffer containing who has which prefix (by UUID) */
    private HashMap<UUID, Prefix> playerPrefixes = new HashMap<>();
    /** Buffer containing who has which suffix (by UUID) */
    private HashMap<UUID, Suffix> playerSuffixes = new HashMap<>();

    public TitleManager(DSTitle plugin)
    {
        this.plugin = plugin;
        titlesConfig = new TitleConfig(plugin);
        playerPrefixes.clear();
    	playerSuffixes.clear();
    	for(Player p : Bukkit.getServer().getOnlinePlayers()) {
    		plugin.getStorage().loadTitlesPlayer(p);
    	}
    }

    public void reloadTitleConfigs() {
    	titlesConfig.reloadTitleConfig();
    	playerPrefixes.clear();
    	playerSuffixes.clear();
    	for(Player p : Bukkit.getServer().getOnlinePlayers()) {
    		plugin.getStorage().loadTitlesPlayer(p);
    	}
    }

    /**
     * Get the Title a player has set currently as prefix
     * @param target the player
     * @return Title or null if the player has no prefix
     */
    public Prefix getPlayerPrefix(OfflinePlayer target)
    {	
    	if(target!=null){
    		UUID player = target.getUniqueId();
    		if(playerPrefixes.containsKey(player)){
                return playerPrefixes.get(player);
            }          
    	}
        return null;
    }
    
    /**
     * Get the Prefix a player has set currently
     * @param target the player
     * @return Title or null if the player has no prefix
     */
    public String getPrefixChatTag(OfflinePlayer target){
    	if(plugin.getConfig().getBoolean("general.use_chattag") || plugin.placeHolders){
	    	String chatTag;
	    	if(target!=null){
	    		if(getPlayerPrefix(target)==null) {
	    			chatTag = " ";
	    		}
	    		if(getPlayerPrefix(target).chatTag!=null){
	    			chatTag = getPlayerPrefix(target).chatTag;
	    			return chatTag;
	    		}    		
	    	}    	
	    	return " ";
    	}
    	return "ChatTags are Disabled";
    }

    /**
     * Get the Title a player has set currently as suffix
     * @param target the player
     * @return Title or null if the player has no suffix
     */
    public Title getPlayerSuffix(OfflinePlayer target)
    {
    	if(target!=null){
    		UUID player = target.getUniqueId();
    		if(playerSuffixes.containsKey(player)){
                return playerSuffixes.get(player);
            }
    	}
        return null;
    }
    
    /**
     * Get the Suffix a player has set currently
     * @param target the player
     * @return Title or null if the player has no suffix
     */

    public String getSuffixChatTag(OfflinePlayer target){
    	if(plugin.getConfig().getBoolean("general.use_chattag") || plugin.placeHolders){
	    	String chatTag;
	    	if(target!=null){
	    		if(getPlayerSuffix(target)==null) {
	    			chatTag = " ";
	    		}
	    		if(getPlayerSuffix(target).chatTag!=null){
	    			chatTag = getPlayerSuffix(target).chatTag;
	    			return chatTag;
	    		}    		
	    	}    	
	    	return " ";
    	}
    	return "ChatTags are Disabled";
    }

    /**
     * Sets the Players Prefix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    @SuppressWarnings("deprecation")
	public void setPlayerPrefix(Prefix title, OfflinePlayer target)
    {
        if(plugin.getConfig().getBoolean("general.use_nametag"))
        {
            plugin.getTeamManager().getTeam(title, getPlayerSuffix(target), target.getPlayer()).addPlayer(target);
        }
        if(title==null){
        	playerPrefixes.remove(target.getUniqueId());
        }else{
        	playerPrefixes.put(target.getUniqueId(), title);
        }
        // Save Changes
        plugin.getStorage().saveTitlesPlayer(target);
    }

    public void setPlayerPrefix(String title, OfflinePlayer target)
    {
        setPlayerPrefix(getPrefix(title), target);
    }

    /**
     * Sets the Players Suffix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    @SuppressWarnings("deprecation")
	public void setPlayerSuffix(Suffix title, OfflinePlayer target)
    {
    	if(plugin.getConfig().getBoolean("general.use_nametag"))
        {
            plugin.getTeamManager().getTeam(getPlayerPrefix(target), title, target.getPlayer()).addPlayer(target);
        }
        if(title==null){
        	playerSuffixes.remove(target.getUniqueId());
        }else{
        	playerSuffixes.put(target.getUniqueId(), title);
        }
        // Save Changes
        plugin.getStorage().saveTitlesPlayer(target);
    }

    /**
     * Sets the Players Suffix, Only in Memory
     * @param title the title to set
     * @param target The Player
     */
    public void setPlayerSuffix(String title,OfflinePlayer target)
    {
    	setPlayerSuffix(getSuffix(title), target);
    }

    /**
     * Clears the Players titles
     * @param target player to remove titles from
     */
    public void clearPlayerTitle(OfflinePlayer target)
    {
        playerPrefixes.remove(target.getUniqueId());
        playerSuffixes.remove(target.getUniqueId());
        this.setPlayerPrefix(this.titlesConfig.getDefaultPrefix(), target);
        this.setPlayerSuffix(this.titlesConfig.getDefaultSuffix(), target);
        // Save Changes
        plugin.getStorage().saveTitlesPlayer(target);
    }

    /**
     * Get an ordered set of prefixes.
     * @return SortedSet: a sorted set of all suffixes
     */
    public SortedSet<Title> getPrefixes()
    {
        return new TreeSet<Title>(titlesConfig.getPrefixes());
    }

    /**
     * Get an ordered set of suffixes.
     * @return SortedSet: a sorted set of all suffixes
     */
    public SortedSet<Title> getSuffixes()
    {
        return new TreeSet<Title>(titlesConfig.getSuffixes());
    }

    /**
     * Check if a prefix with this name exists
     * @param name The name of the title to search for
     * @return true if it exists, false otherwise
     */
    public boolean prefixExists(String name)
    {
    	for(Prefix pre : titlesConfig.getPrefixes()) {
    		if(pre.name.equals(name))
    			return true;
    	}
        return false;
    }

    /**
     * Check if a suffix with this name exists
     * @param name The name of the title to search for
     * @return true if it exists, false otherwise
     */
    public boolean suffixExists(String name)
    {
    	for(Suffix suf : titlesConfig.getSuffixes()) {
    		if(suf.name.equals(name))
    			return true;
    	}
        return false;
    }

    /**
     * Gets a Prefix
     * @param name The name of the suffix
     * @return A Title instance
     * @ensure result.name.equals(name)
     */
    public Prefix getPrefix(String name){
        if(name==null||name.equals(""))
            return null;

        return titlesConfig.getPrefixbyName(name);
    }

    /**
     * Gets a Suffix
     * @param name The name of the suffix
     * @return A Title instance
     * @ensure result.name.equals(name)
     */
    public Suffix getSuffix(String name){
        if(name==null||name.equals(""))
            return null;

        return titlesConfig.getSuffixbyName(name);
    }

	public static UUID getUUIDfromPlayerName(String playerName) {
	    OfflinePlayer op = Bukkit.getOfflinePlayer(playerName);
	    if (!op.equals((Object)null))
	      return op.getUniqueId(); 
	    return null;
	}
	  
	public static String getPlayerNamefromUUID(UUID uuid) {
	    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
	    if (!op.equals((Object)null))
	      return op.getName(); 
	    return "";
	}
	
}



