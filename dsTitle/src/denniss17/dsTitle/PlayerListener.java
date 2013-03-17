package denniss17.dsTitle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import denniss17.dsTitle.DS_Title.Title;

public class PlayerListener implements Listener {

	private DS_Title plugin;
	
	public PlayerListener(DS_Title plugin){
		this.plugin = plugin;
	}
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String chatFormat;
		if(plugin.getConfig().getBoolean("general.overwrite_format")){
			chatFormat = plugin.getConfig().getString("general.chat_format");
		}else{
			chatFormat = event.getFormat();
			if(!chatFormat.contains("{titlesuffix}")) chatFormat = chatFormat.replace("%1$s", "%1$s{titlesuffix}");
			if(!chatFormat.contains("{titleprefix}")) chatFormat = chatFormat.replace("%1$s", "{titleprefix}%1$s");
		}
		Title title = plugin.getTitleOfPlayer(player.getName());
		if(title!=null){
			if(title.prefix != null ){
				chatFormat = chatFormat.replace("{titleprefix}", title.prefix + "&r");
			}else{
				chatFormat = chatFormat.replace("{titleprefix}", "");
			}
			if(title.suffix != null ){
				chatFormat = chatFormat.replace("{titlesuffix}", title.suffix + "&r");
			}else{
				chatFormat = chatFormat.replace("{titlesuffix}", "");
			}
		}else{
			chatFormat = chatFormat.replace("{titlesuffix}", "");
			chatFormat = chatFormat.replace("{titleprefix}", "");
		}
		
		chatFormat = ChatStyler.setTotalStyle(chatFormat);
		
		event.setFormat(chatFormat);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(plugin.getPermissionManager().hasPermission(event.getPlayer(), "ds_title.admin")){
			// If there is a new version
			if(DS_Title.versionChecker.getLatestVersion() != null && !DS_Title.versionChecker.getLatestVersion().equals(plugin.getDescription().getVersion())){
				// Send message to player with admin permissions
				plugin.sendMessage(event.getPlayer(), plugin.getConfig().getString("messages.update_notification")
						.replace("{version}", DS_Title.versionChecker.getLatestVersion())
						.replace("{website}", plugin.getDescription().getWebsite()));
			}
		}
	}
}
