package denniss17.dsTitle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.plugin.java.JavaPlugin;

public class VersionChecker implements Runnable{
	
	public String latestVersion = null;
	private static JavaPlugin plugin;
	
	public VersionChecker(JavaPlugin plugin){
		VersionChecker.plugin = plugin;
	}
	
	public String getLatestVersion(){
		return this.latestVersion;
	}

	@Override
	public void run() {
		URL url;
		try {
			url = new URL("http://dennisschroer.nl/bukkitplugins/versioncheck/dsTitle/" +
					plugin.getServer().getVersion().replace(" ", "") + '/' +
					plugin.getDescription().getVersion()					
					);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String response = in.readLine();
			
			if(response.equals("OK")){
				latestVersion = plugin.getDescription().getVersion();
			}else if(response.equals("UK")){
				// unknown
			}else{
				latestVersion = response;
			}
			
			in.close();
		} catch (MalformedURLException e) {
			plugin.getLogger().warning("Bad url: " + e.getMessage());
		} catch (IOException e) {
			plugin.getLogger().warning("Version check failed!");
		}
	}

}
