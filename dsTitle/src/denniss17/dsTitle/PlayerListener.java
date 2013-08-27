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
	
	public static String prefixTag;
	public static String suffixTag;
	public static String playerTag;
	
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
			if(!chatFormat.contains(prefixTag)) chatFormat = chatFormat.replace(playerTag, prefixTag + playerTag);
			if(!chatFormat.contains(suffixTag)) chatFormat = chatFormat.replace(playerTag, playerTag + suffixTag);
		}
		Title prefix = plugin.getPrefixOfPlayer(player);
		Title suffix = plugin.getSuffixOfPlayer(player);
		if(prefix!=null){
			if(!chatFormat.contains(prefixTag)){
				chatFormat = chatFormat.replace(playerTag, prefix.chatTag + playerTag + "&r");
			}else{
				chatFormat = chatFormat.replace(prefixTag, prefix.chatTag + "&r");
			}
		}else{
			chatFormat = chatFormat.replace(prefixTag, "");
		}
		if(suffix!=null){
			if(!chatFormat.contains(suffixTag)){
				chatFormat = chatFormat.replace(playerTag, suffix.chatTag + playerTag + "&r");
			}else{
				chatFormat = chatFormat.replace(suffixTag, suffix.chatTag + "&r");
			}
		}else{
			chatFormat = chatFormat.replace(suffixTag, "");
		}
		
		return ChatStyler.setTotalStyle(chatFormat);
	}
    
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		// Set tag above head
		if(plugin.getConfig().getBoolean("general.use_nametag")){
			Title prefix = plugin.getPrefixOfPlayer(event.getPlayer());
			Title suffix = plugin.getSuffixOfPlayer(event.getPlayer());
			if(prefix!=null || suffix!=null){
				plugin.getTeamManager().getTeam(prefix, suffix).addPlayer(event.getPlayer());
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
			plugin.getTeamManager().removePlayerFromTeam(event.getPlayer());
		}
	}
}
