package denniss17.dsTitle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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


public class DS_Title extends JavaPlugin{
    //public Chat chat = null;
	
	private FileConfiguration titleConfig = null;
	private File titleConfigFile = null;
	private PermissionManager permissionManager;
	private TeamManager teamManager;
	public static VersionChecker versionChecker;

	/**
	 * Enable this plugin
	 */
	public void onEnable(){
		// Register listeners
		Listener playerlistener = new PlayerListener(this);
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
		this.getServer().getScheduler().runTaskTimerAsynchronously(this, DS_Title.versionChecker, 0, this.getConfig().getInt("general.update_check_interval") * 60 * 20);
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
	 * Get the title a player has set currently
	 * @param playername
	 * @return Title
	 */
	public Title getTitleOfPlayer(Player player){
		return getTitle(titleConfig.getString("players." + player.getName(), ""));
	}
	
	/**
	 * Set the title of the player to the given title
	 * @param player The Player for who to set the title
	 * @param title The Title to set
	 */
	public void setTitleOfPlayer(Player player, Title title){
		// Set tag above player
		if(getConfig().getBoolean("general.use_nametag")){
			teamManager.getTeam(title).addPlayer(player);
		}
		// Set tag in chat
		titleConfig.set("players." + player.getName(), title.name);
		// Save to file
		saveTitleConfig();
	}
	
	/**
	 * Clear the title of this player
	 * @param playername
	 */
	public void clearTitleOfPlayer(Player player){
		if(getConfig().getBoolean("general.use_nametag")){
			Title current = getTitleOfPlayer(player);
			if(current!=null) teamManager.removePlayerFromTeam(player, current);
		}
		titleConfig.set("players." + player.getName(), null);
		saveTitleConfig();
	}
	
	/**
	 * Load a title from config
	 * @param name The name of the title
	 * @return A title instance
	 * @ensure result.name.equals(name)
	 */
	public Title getTitle(String name){
		if(name==null||name.equals("")){
			return null;
		}
		ConfigurationSection titleSection = titleConfig.getConfigurationSection("titles." + name);
		if(titleSection!=null){
			String permission = titleSection.contains("permission") ? titleSection.getString("permission") : null;
			String description = titleSection.contains("description") ? titleSection.getString("description") : null;
			String prefix = 	titleSection.contains("prefix") 	? titleSection.getString("prefix") 		: null;
			String suffix = 	titleSection.contains("suffix") 	? titleSection.getString("suffix") 		: null;
			String headprefix = titleSection.contains("headprefix") ? titleSection.getString("headprefix") 	: null;
			String headsuffix = titleSection.contains("headsuffix") ? titleSection.getString("headsuffix") 	: null;
			if(headprefix.length() >16 || headsuffix.length()>16){
				getLogger().warning("Title '" + name + "' has been disabled!");
				getLogger().warning("The headprefix and headsuffix cannot be longer than 16 characters, as this would kick every online player from the server");
				return null;
			}			
			return new Title(name, prefix, suffix, headprefix, headsuffix, permission, description);
		}else{
			this.getLogger().warning("Title '" + name + "' not good configured and can't be used!");
			return null;
		}
	}
	
	/**
	 * Check if a title with this name exists
	 * @param name The name of the title to search for
	 * @return true if it exists, false otherwise
	 */
	public boolean titleExists(String name){
		return titleConfig.contains("titles." + name);
	}
	
	/** 
	 * Get an ordered set of titles.
	 * @return SortedSet: a sorted set of all titles
	 */
	public SortedSet<Title> getTitles(){
		SortedSet<Title> result = new TreeSet<Title>();
		if(titleConfig.contains("titles")){
			Set<String> titles = titleConfig.getConfigurationSection("titles").getKeys(false);
			
			for(String name: titles){
				Title title = getTitle(name);
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
