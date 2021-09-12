package com.kaltiz.dsTitle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import denniss17.dsTitle.DSTitle;
import denniss17.dsTitle.objects.Prefix;
import denniss17.dsTitle.objects.Suffix;
import denniss17.dsTitle.objects.Title;

public class TitleConfig
{
	private DSTitle plugin;
	private static final String TITLE_CONFIG_FILENAME = "titleConfig.yml";
	/** The FileConfiguration containing all titles */
    private FileConfiguration titleConfig = null;
    /** The File which contains the titleConfig */
    private File titleConfigFile = null;


    private List<Prefix> prefixes = new ArrayList<Prefix>();
    private List<Suffix> suffixes = new ArrayList<Suffix>();
	
	public TitleConfig(DSTitle plugin) {
		this.plugin = plugin;
        if (titleConfigFile == null) {
        	titleConfigFile = new File(plugin.getDataFolder(), TITLE_CONFIG_FILENAME);
	    }
	    if (!titleConfigFile.exists()) {           
	    	plugin.saveResource(TITLE_CONFIG_FILENAME, false);
	    }
    	
	    titleConfig = YamlConfiguration.loadConfiguration(titleConfigFile);
        prefixes.clear();
        suffixes.clear();
        loadTitles();
	}
	
	public void saveTitleConfig() {
        if (titleConfig == null || titleConfigFile == null) {
            return;
        }
        try {
            titleConfig.save(titleConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + titleConfigFile, ex);
        }
    }
	
	public void reloadTitleConfig() {
		if (titleConfigFile == null) {
        	titleConfigFile = new File(plugin.getDataFolder(), TITLE_CONFIG_FILENAME);
	    }
	    if (!titleConfigFile.exists()) {           
	    	plugin.saveResource(TITLE_CONFIG_FILENAME, false);
	    }
    	
	    titleConfig = YamlConfiguration.loadConfiguration(titleConfigFile);
        prefixes.clear();
        suffixes.clear();
        loadTitles();
	}
	
	public void saveTitle(Title title)
    {
        // Get path
        String path;
        if(title instanceof Prefix)
            path = "prefixes." + title.name + ".";
        else
            path = "suffixes." + title.name + ".";

        // Save title
        titleConfig.set(path + "permission", title.permission);
        titleConfig.set(path + "description", title.description);
        titleConfig.set(path + "chattag", title.chatTag);
        titleConfig.set(path + "headtag", title.headTag);
        titleConfig.set(path + "symbol", title.symbol);

        saveTitleConfig();

        // Update it in the Hashmaps
        if(title instanceof Prefix)
            prefixes.add((Prefix) title);
        else
            suffixes.add((Suffix) title);

        // Reload headtags
        plugin.getTeamManager().reloadTags();
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
                title = loadSuffix(name);
                if (title != null) suffixes.add((Suffix)title);
            }
        }

        if(titleConfig.contains("prefixes"))
        {
            Set<String> titles = titleConfig.getConfigurationSection("prefixes").getKeys(false);
            for(String name: titles){
                title = loadPrefix(name);
                if (title != null) prefixes.add((Prefix)title);
            }
        }
    }

    /**
     * Loads a Suffix from the TitleConfig
     * @param name name of the title to load
     * @return void
     */
    
    private Suffix loadSuffix(String name) {
    	String path = "suffixes" + '.' + name;

        if(path==null||path.equals("")){
            return null;
        }
        ConfigurationSection titleSection = titleConfig.getConfigurationSection(path);

        if(titleSection!=null)
        {
        	// These values could be null !
            String permission   = titleSection.getString("permission");
            String description  = titleSection.getString("description");
            String chatTag      = titleSection.getString("chattag");
            String headTag      = titleSection.getString("headtag");
            String symbol       = titleSection.getString("symbol");
            if (headTag!=null && headTag.length() > 16)
            {
                plugin.getLogger().warning("Title  '" + name + "has been disabled!");
                plugin.getLogger().warning("The headtag cannot be longer than 16 characters, as this would kick every online player from the server");
                return null;
            }
            return new Suffix(name, chatTag, headTag, permission, description, symbol);
        }
        else
        {
            plugin.getLogger().warning(name + "' not good configured and can't be used!");
            return null;
        }
    }
    
    /**
     * Loads a Prefix from the TitleConfig
     * @param name name of the title to load
     * @return void
     */
    
    private Prefix loadPrefix(String name) {
    	String path = "prefixes" + '.' + name;

        if(path==null||path.equals("")){
            return null;
        }
        ConfigurationSection titleSection = titleConfig.getConfigurationSection(path);

        if(titleSection!=null)
        {
        	// These values could be null !
            String permission   = titleSection.getString("permission");
            String description  = titleSection.getString("description");
            String chatTag      = titleSection.getString("chattag");
            String headTag      = titleSection.getString("headtag");
            String symbol       = titleSection.getString("symbol");
            if (headTag!=null && headTag.length() > 16)
            {
                plugin.getLogger().warning("Title  '" + name + "has been disabled!");
                plugin.getLogger().warning("The headtag cannot be longer than 16 characters, as this would kick every online player from the server");
                return null;
            }
            return new Prefix(name, chatTag, headTag, permission, description, symbol);
        }
        else
        {
            plugin.getLogger().warning(name + "' not good configured and can't be used!");
            return null;
        }
    }
    
    public String getDefaultPrefix() {
		return titleConfig.getString("default_prefix");
	}

	public String getDefaultSuffix() {
		return titleConfig.getString("default_prefix");
	}
    
    public List<Prefix> getPrefixes(){
    	return this.prefixes;
    }
    
    public List<Suffix> getSuffixes(){
    	return this.suffixes;
    }
    
    public Prefix getPrefixbyName(String name) {
    	for(Prefix pre : prefixes) {
    		if(pre.name.equals(name)) {
    			return pre;
    		}
    	}
    	return null;
    }
    
    public Suffix getSuffixbyName(String name) {
    	for(Suffix suf : suffixes) {
    		if(suf.name.equals(name)) {
    			return suf;
    		}
    	}
    	return null;
    }
}