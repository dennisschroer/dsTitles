package denniss17.dsTitle;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
			if(!chatFormat.contains("{title}")){
				chatFormat = chatFormat.replace("%1$s", "{title}%1$s");
			}
		}
		
		if(plugin.getTitleOfPlayer(player.getName())!=null){
			chatFormat = chatFormat.replace("{title}", plugin.getTitleOfPlayer(player.getName()).title + "&r");
		}else{
			chatFormat = chatFormat.replace("{title}", "");
		}
		
		chatFormat = ChatStyler.setTotalStyle(chatFormat);
		
		event.setFormat(chatFormat);
	}
}
