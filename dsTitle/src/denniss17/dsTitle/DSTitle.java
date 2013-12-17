package denniss17.dsTitle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import denniss17.dsTitle.Title.Type;


public class DSTitle extends JavaPlugin{	
	private FileConfiguration titleConfig = null;
	private File titleConfigFile = null;
	private PermissionManager permissionManager;
	private TeamManager teamManager;
	public static VersionChecker versionChecker;
	public Map<String, Title> prefixBuffer;
	public Map<String, Title> suffixBuffer;

	/**
	 * Enable this plugin
	 */
	public void onEnable(){
		prefixBuffer = new HashMap<String, Title>();
		suffixBuffer = new HashMap<String, Title>();
		
		// Register listeners
		Listener playerlistener = new PlayerListener(this, !this.getConfig().getBoolean("general.use_deprecated_listener"));
		this.getServer().getPluginManager()
				.registerEvents(playerlistener, this);
		
		// Set the command executors
		CommandExec commandExec = new CommandExec(this);
		this.getCommand("title").setExecutor(commandExec);	
		
		this.permissionManager = new PermissionManager(this);
		this.teamManager = new TeamManager(this);
		versionChecker = new VersionChecker(this);
		
		// Load the config values
		reloadConfiguration();	
		PlayerListener.prefixTag = getConfig().getString("general.chat_format_prefix_tag", "[titleprefix]");
		PlayerListener.suffixTag = getConfig().getString("general.chat_format_suffix_tag", "[titlesfix]");
		
		if(getConfig().getBoolean("general.use_nametag")){
			teamManager.reloadTags();
		}
		// Clean up teams, especialy when name tags are disabled
		teamManager.cleanUpTeams(!getConfig().getBoolean("general.use_nametag"));		
		
		// Check for newer versions
		if(this.getConfig().getBoolean("general.check_for_updates")){
			activateVersionChecker();
		}
	}
	
	/**
	 * Activate the versionCheckerThread to run on a timer
	 */
	private void activateVersionChecker(){
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, DSTitle.versionChecker, 0, this.getConfig().getInt("general.update_check_interval") * 60 * 20);
	}
	
	/**
	 * Reload the configuration
	 */
	public void reloadConfiguration(){
		this.reloadConfig();
		this.reloadTitleConfig();
		
		this.saveTitleConfig();
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();	
        
        PlayerListener.prefixTag = getConfig().getString("general.chat_format_prefix_tag", "[titleprefix]");
		PlayerListener.suffixTag = getConfig().getString("general.chat_format_suffix_tag", "[titlesuffix]");
		PlayerListener.playerTag = getConfig().getString("general.chat_format_player_tag", "%1$s");
	}
	
	/**
	 * Get the PermissionManager responsible for managing permissions
	 * @return PermissionManager
	 */
	public PermissionManager getPermissionManager(){
		return this.permissionManager;
	}
	
	public TeamManager getTeamManager() {
		return this.teamManager;
	}

	/**
	 * Get the Title a player has set currently as prefix
	 * @param playername
	 * @return Title
	 */
	public Title getPrefixOfPlayer(Player player){
		return getPrefix(titleConfig.getString("players." + player.getName() + ".prefix", ""));
	}
	
	/**
	 * Get the Title a player has set currently as suffix
	 * @param playername
	 * @return Title
	 */
	public Title getSuffixOfPlayer(Player player){
		return getSuffix(titleConfig.getString("players." + player.getName() + ".suffix", ""));
	}
	
	/**
	 * Set the prefix of the player to the given Title
	 * @param player The Player for who to set the Title
	 * @param title The Title to set
	 */
	public void setPrefixOfPlayer(Player player, Title title){
		// Set tag above player
		if(getConfig().getBoolean("general.use_nametag")){
			teamManager.getTeam(title, getSuffixOfPlayer(player)).addPlayer(player);
		}
		// Set tag in chat
		titleConfig.set("players." + player.getName() + ".prefix", title.name);
		// Save to file
		saveTitleConfig();
	}
	
	/**
	 * Set the suffix of the player to the given Title
	 * @param player The Player for who to set the Title
	 * @param title The Title to set
	 */
	public void setSuffixOfPlayer(Player player, Title title){
		// Set tag above player
		if(getConfig().getBoolean("general.use_nametag")){
			teamManager.getTeam(getPrefixOfPlayer(player), title).addPlayer(player);
		}
		// Set tag in chat
		titleConfig.set("players." + player.getName() + ".suffix", title.name);
		// Save to file
		saveTitleConfig();
	}
	
	/**
	 * Clear the title of this player
	 * @param playername
	 */
	public void clearTitleOfPlayer(Player player){
		if(getConfig().getBoolean("general.use_nametag")){
			Title prefix = getPrefixOfPlayer(player);
			Title suffix = getSuffixOfPlayer(player);
			if(prefix!=null || suffix!=null) teamManager.removePlayerFromTeam(player, prefix, suffix);
		}
		titleConfig.set("players." + player.getName(), null);
		saveTitleConfig();
	}
	
	/**
	 * Load a suffix from config
	 * @param name The name of the suffix
	 * @return A Title instance
	 * @ensure result.name.equals(name)
	 */
	public Title getSuffix(String name){
		if(name==null||name.equals("")){
			return null;
		}
		if(!suffixBuffer.containsKey(name)){
			ConfigurationSection titleSection = titleConfig.getConfigurationSection("suffixes." + name);
			if(titleSection!=null){
				String permission = titleSection.contains("permission") ? titleSection.getString("permission") : null;
				String description = titleSection.contains("description") ? titleSection.getString("description") : null;
				String chatTag = 	titleSection.contains("chattag") 	? titleSection.getString("chattag") 		: null;
				String headTag = 	titleSection.contains("headtag") 	? titleSection.getString("headtag") 		: null;
				if(headTag!=null && headTag.length() >16){
					getLogger().warning("Suffix  '" + name + "' has been disabled!");
					getLogger().warning("The headtag cannot be longer than 16 characters, as this would kick every online player from the server");
					return null;
				}			
				suffixBuffer.put(name, new Title(name, Type.PREFIX, chatTag, headTag, permission, description));
			}else{
				this.getLogger().warning("Suffix '" + name + "' not good configured and can't be used!");
				return null;
			}
		}
		return suffixBuffer.get(name);
	}
	
	/**
	 * Load a prefix from config
	 * @param name The name of the title
	 * @return A title instance
	 * @ensure result.name.equals(name)
	 */
	public Title getPrefix(String name){
		if(name==null||name.equals("")){
			return null;
		}
		if(!prefixBuffer.containsKey(name)){
			ConfigurationSection titleSection = titleConfig.getConfigurationSection("prefixes." + name);
			if(titleSection!=null){
				String permission = titleSection.contains("permission") ? titleSection.getString("permission") : null;
				String description = titleSection.contains("description") ? titleSection.getString("description") : null;
				String chatTag = 	titleSection.contains("chattag") 	? titleSection.getString("chattag") 		: null;
				String headTag = 	titleSection.contains("headtag") 	? titleSection.getString("headtag") 		: null;
				if(
						(headTag!=null && headTag.length() >16)
						){
					getLogger().warning("Prefix  '" + name + "' has been disabled!");
					getLogger().warning("The headtag cannot be longer than 16 characters, as this would kick every online player from the server");
					return null;
				}			
				prefixBuffer.put(name, new Title(name, Type.PREFIX, chatTag, headTag, permission, description));
			}else{
				this.getLogger().warning("Prefix '" + name + "' not good configured and can't be used!");
				return null;
			}
		}
		return prefixBuffer.get(name);
	}
	
	/**
	 * Check if a prefix with this name exists
	 * @param name The name of the title to search for
	 * @return true if it exists, false otherwise
	 */
	public boolean prefixExists(String name){
		return titleConfig.contains("prefixes." + name);
	}
	
	/**
	 * Check if a suffix with this name exists
	 * @param name The name of the title to search for
	 * @return true if it exists, false otherwise
	 */
	public boolean suffixExists(String name){
		return titleConfig.contains("suffixes." + name);
	}
	
	/** 
	 * Get an ordered set of prefixes.
	 * @return SortedSet: a sorted set of all prefixes
	 */
	public SortedSet<Title> getPrefixes(){
		SortedSet<Title> result = new TreeSet<Title>();
		if(titleConfig.contains("prefixes")){
			Set<String> titles = titleConfig.getConfigurationSection("prefixes").getKeys(false);
			
			for(String name: titles){
				Title title = getPrefix(name);
				if(title!=null) result.add(title);
			}
		}
		return result;
	}
	
	/** 
	 * Get an ordered set of suffixes.
	 * @return SortedSet: a sorted set of all suffixes
	 */
	public SortedSet<Title> getSuffixes(){
		SortedSet<Title> result = new TreeSet<Title>();
		if(titleConfig.contains("suffixes")){
			Set<String> titles = titleConfig.getConfigurationSection("suffixes").getKeys(false);
			
			for(String name: titles){
				Title title = getSuffix(name);
				if(title!=null) result.add(title);
			}
		}
		return result;
	}

	/** 
	 * Send a message to a receiver.
	 * This method applies chat styles to the message
	 * @param receiver CommandSender the receiver of the message
	 * @param message The message to send
	 */
	public void sendMessage(CommandSender receiver, String message){
		receiver.sendMessage(ChatStyler.setTotalStyle(message));
	}
	
	protected void reloadTitleConfig() {
		if (titleConfigFile == null) {
			titleConfigFile = new File(getDataFolder(), "titleConfig.yml");
		}
		titleConfig = YamlConfiguration.loadConfiguration(titleConfigFile);

		// Look for defaults in the jar
		InputStream defConfigStream = getResource("titleConfig.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			titleConfig.setDefaults(defConfig);
		}
	}

	protected void saveTitleConfig() {
		if (titleConfig == null || titleConfigFile == null) {
			return;
		}
		try {
			titleConfig.save(titleConfigFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE,
					"Could not save config to " + titleConfigFile, ex);
		}
	}

	protected MemorySection getTitleConfig() {
		if (titleConfig == null) {
			reloadTitleConfig();
		}
		return titleConfig;
	}
	
}
