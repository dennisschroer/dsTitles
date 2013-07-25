package denniss17.dsTitle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import denniss17.dsTitle.Title;

public class PlayerListener implements Listener {

	private DS_Title plugin;
	
	private boolean asyncChatListener;
	
	public PlayerListener(DS_Title plugin, boolean asyncChatListener){
		this.plugin = plugin;
		this.asyncChatListener = asyncChatListener;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSyncChat(PlayerChatEvent event){
		if(asyncChatListener) return;
		
		event.setFormat(parseChatFormat(event.getFormat(), event.getPlayer()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerAsyncChat(AsyncPlayerChatEvent event) {
		if(!asyncChatListener) return;
		
		event.setFormat(parseChatFormat(event.getFormat(), event.getPlayer()));
	}

	private String parseChatFormat(String chatFormat, Player player){		
		if(plugin.getConfig().getBoolean("general.overwrite_format")){
			chatFormat = plugin.getConfig().getString("general.chat_format");
		}else{
			if(!chatFormat.contains("[titlesuffix]")) chatFormat = chatFormat.replace("%1$s", "%1$s{titlesuffix}");
			if(!chatFormat.contains("[titleprefix]")) chatFormat = chatFormat.replace("%1$s", "{titleprefix}%1$s");
		}
		Title title = plugin.getTitleOfPlayer(player);
		if(title!=null){
			if(title.prefix != null ){
				chatFormat = chatFormat.replace("[titleprefix]", title.prefix + "&r");
			}else{
				chatFormat = chatFormat.replace("[titleprefix]", "");
			}
			if(title.suffix != null ){
				chatFormat = chatFormat.replace("[titlesuffix]", title.suffix + "&r");
			}else{
				chatFormat = chatFormat.replace("[titlesuffix]", "");
			}
		}else{
			chatFormat = chatFormat.replace("[titlesuffix]", "");
			chatFormat = chatFormat.replace("[titleprefix]", "");
		}
		
		return ChatStyler.setTotalStyle(chatFormat);
	}
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		// Set tag above head
		if(plugin.getConfig().getBoolean("general.use_nametag")){
			Title title = plugin.getTitleOfPlayer(event.getPlayer());
			if(title!=null){
				plugin.getTeamManager().getTeam(title).addPlayer(event.getPlayer());
			}
		}
		
		// Check for update and send message
		if(plugin.getPermissionManager().hasPermission(event.getPlayer(), "ds_title.admin")){
			// If there is a new version
			if(DS_Title.versionChecker.getLatestVersion() != null && !DS_Title.versionChecker.getLatestVersion().equals(plugin.getDescription().getVersion())){
				// Send message to player with admin permissions
				plugin.sendMessage(event.getPlayer(), plugin.getConfig().getString("messages.update_notification")
						.replace("{version}", DS_Title.versionChecker.getLatestVersion())
						.replace("{current}", plugin.getDescription().getVersion())
						.replace("{website}", plugin.getDescription().getWebsite()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		if(plugin.getConfig().getBoolean("general.use_nametag")){
			Title title = plugin.getTitleOfPlayer(event.getPlayer());
			if(title!=null){
				plugin.getTeamManager().removePlayerFromTeam(event.getPlayer(), title);
			}
		}
	}
}
