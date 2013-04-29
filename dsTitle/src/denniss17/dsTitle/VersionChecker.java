package denniss17.dsTitle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * VersionChecker v 1.1
 * This generic class is used to check for updates of the corresponding plugin.
 * It is compatible with every plugin
 * @author Denniss17, All rights reserved
 * @copyright Denniss17, All rights reserved
 * @version 1.1.0
 */
public class VersionChecker implements Runnable{
	
	public String latestVersion = null;
	private static JavaPlugin plugin;
	
	public VersionChecker(JavaPlugin plugin){
		VersionChecker.plugin = plugin;
	}
	
	/**
	 * Returns the latest version for this plugin, or null if unknown
	 * @return the latest version or null
	 */
	public String getLatestVersion(){
		return this.latestVersion;
	}
	
	/** 
	 * Activate this versionChecker to check for updates with the given interval (in ticks)
	 * @param interval The interval in ticks
	 *  */
	public void activate(long interval){
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this, 0, interval);
	}

	@Override
	public void run() {
		URL url;
		plugin.getLogger().info("Checking for updates...");
		try {
			// Read version via http
			url = new URL("http://dennisschroer.nl/bukkitplugins/versioncheck/" + 
					plugin.getName() + '/' +
					plugin.getServer().getBukkitVersion().replace(" ", "") + '/' +
					plugin.getDescription().getVersion()
					);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String response = in.readLine();
			
			// Handle response
			if(response.equals("OK")){
				latestVersion = plugin.getDescription().getVersion();
				plugin.getLogger().info("You have the latest version.");
			}else if(response.equals("UK")){
				// unknown
			}else{
				plugin.getLogger().info("There is a new version available for this Bukkit version");
				latestVersion = response;
			}
			
			// Close connection
			in.close();
		} catch (MalformedURLException e) {
			plugin.getLogger().warning("Bad url: " + e.getMessage());
		} catch (IOException e) {
			plugin.getLogger().warning("Version check failed!");
			plugin.getLogger().warning("Reason: " + e.getMessage());
		}
	}

}
