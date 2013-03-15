package denniss17.dsTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import denniss17.dsTitle.DS_Title.Title;

public class CommandExec implements CommandExecutor{

	private DS_Title plugin;
	
	public CommandExec(DS_Title plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if(cmd.getName().equals("title")){
			return cmdTitle(sender, cmd, commandlabel, args);
		}
		return false;		
	}
	
	private boolean cmdTitle(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if(args.length==0){
			return false;
		}else{
			if(args[0].equalsIgnoreCase("list")){
				SortedSet<Title> titles = plugin.getTitles();
				List<String> available = new ArrayList<String>();
				List<String> unavailable = new ArrayList<String>();
				
				String description;
				String listitem;
				String listitemMask = plugin.getConfig().getString("messages.title_listitem");
				for(Title title: titles){
					description = title.description==null ? "-" : title.description;
					listitem = listitemMask
							.replace("{name}", title.name)
							.replace("{preview}", title.title + "&r")
							.replace("{description}", description);
							
					if(title.permission==null || plugin.getPermissionManager().hasPermission(sender, title.permission)){
						available.add(listitem);
					}else{
						unavailable.add(listitem);
					}
				}
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.available_header"));
				for(String msg: available){
					plugin.sendMessage(sender, msg);
				}
				if(plugin.getConfig().getBoolean("general.show_unavailable_titles")){
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.unavailable_header"));
					for(String msg: unavailable){
						plugin.sendMessage(sender, msg);
					}
				}
				
				return true;
			}else if(args[0].equalsIgnoreCase("set")){
				if(args.length==1) return false;
				
				Player player;
				if(sender instanceof Player){
					player = (Player)sender;
				}else{
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_player"));
					return true;
				}
				
				if(plugin.titleExists(args[1])){
					Title title = plugin.getTitle(args[1]);
					if(title.permission==null || plugin.getPermissionManager().hasPermission(player, title.permission)){
						plugin.setTitleOfPlayer(player.getName(), args[1]);
						plugin.sendMessage(sender, plugin.getConfig().getString("messages.title_set"));
					}else{
						plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_permission"));
					}
					
				}else{
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_title_not_found"));
				}
				
				return true;		
			}else if(args[0].equalsIgnoreCase("clear")){
				Player player;
				if(sender instanceof Player){
					player = (Player)sender;
				}else{
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_player"));
					return true;
				}
				
				
				plugin.clearTitleOfPlayer(player.getName());
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.title_cleared"));
				
				return true;
			}else if(args[0].equalsIgnoreCase("reload")){
				if(plugin.getPermissionManager().hasPermission(sender, "ds_title.admin")){
					plugin.reloadConfiguration();
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.reloaded"));
					return true;
				}else{
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_permission"));
					return true;
				}
				
			}else{
				return false;
			}
		}
	}
}
