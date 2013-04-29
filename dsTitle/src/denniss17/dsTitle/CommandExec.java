package denniss17.dsTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import denniss17.dsTitle.Title;

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
	
	/**
	 * Main method used when the /title command is executed
	 * @param sender The sender of the command
	 * @param cmd The command itself
	 * @param commandlabel The label of this command
	 * @param args The arguments passed to this command
	 * @return true if the command is executed correctly, false otherwise
	 */
	private boolean cmdTitle(CommandSender sender, Command cmd, String commandlabel, String[] args) {
		if(args.length==0){
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_header"));
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_list"));
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_set"));
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_clear"));
			if(plugin.getPermissionManager().hasPermission(sender, "ds_title.admin")){
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_grant"));
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_ungrant"));
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.menu_reload"));
			}
			return true;
		}else{
			if(args[0].equalsIgnoreCase("list")){
				return cmdTitleList(sender, cmd, commandlabel, args);
			}else if(args[0].equalsIgnoreCase("set")){
				return cmdTitleSet(sender, cmd, commandlabel, args);
			}else if(args[0].equalsIgnoreCase("clear")){
				return cmdTitleClear(sender, cmd, commandlabel, args);
			}else if(args[0].equalsIgnoreCase("reload")){
				return cmdTitleReload(sender, cmd, commandlabel, args);
			}else if(args[0].equalsIgnoreCase("grant")){
				return cmdTitleGrant(sender, cmd, commandlabel, args);
			}else if(args[0].equalsIgnoreCase("ungrant")){
				return cmdTitleUngrant(sender, cmd, commandlabel, args);
			}else{
				return false;
			}
		}
	}
	
	private boolean cmdTitleList(CommandSender sender, Command cmd, String commandlabel, String[] args){
		SortedSet<Title> titles = plugin.getTitles();
		List<String> available = new ArrayList<String>();
		List<String> unavailable = new ArrayList<String>();
		
		String description, preview, listitem;
		String listitemMask = plugin.getConfig().getString("messages.title_listitem");
		for(Title title: titles){
			description = title.description==null ? "-" : title.description;
			preview = title.prefix==null ? "" : title.prefix;
			preview += title.prefix!=null && title.suffix!=null ? "&r&8/&r" : "";
			preview += title.suffix==null ? "" : title.suffix;
			
			listitem = listitemMask
					.replace("{name}", title.name)
					.replace("{preview}", preview + "&r")
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
	}
	
	private boolean cmdTitleSet(CommandSender sender, Command cmd, String commandlabel, String[] args){
		if(args.length==1) return false;
		
		Player player;
		if(sender instanceof Player){
			player = (Player)sender;
		}else{
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_player"));
			return true;
		}
		
		if(plugin.titleExists(args[1])){
			Title current = plugin.getTitleOfPlayer(player);
			Title title = plugin.getTitle(args[1]);
			if(title.permission==null || plugin.getPermissionManager().hasPermission(player, title.permission)){
				// Clean up previous title
				if(plugin.getConfig().getBoolean("general.use_nametag")){
					if(current!=null) plugin.getTeamManager().removePlayerFromTeam(player, current);
				}
				
				// Set new title
				plugin.setTitleOfPlayer(player, title);
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.title_set"));
			}else{
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_permission"));
			}
			
		}else{
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_title_not_found"));
		}
		
		return true;	
	}
	
	private boolean cmdTitleClear(CommandSender sender, Command cmd, String commandlabel, String[] args){
		Player player;
		if(sender instanceof Player){
			player = (Player)sender;
		}else{
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_player"));
			return true;
		}
		
		
		plugin.clearTitleOfPlayer(player);
		plugin.sendMessage(sender, plugin.getConfig().getString("messages.title_cleared"));
		
		return true;
	}
	
	private boolean cmdTitleReload(CommandSender sender, Command cmd, String commandlabel, String[] args){
		if(plugin.getPermissionManager().hasPermission(sender, "ds_title.admin")){
			// Reload config
			plugin.reloadConfiguration();
			// Reload team tags
			if(plugin.getConfig().getBoolean("general.use_nametag")){
				plugin.getTeamManager().reloadTags();
			}
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.reloaded"));
			return true;
		}else{
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_permission"));
			return true;
		}
	}
	
	private boolean cmdTitleGrant(CommandSender sender, Command cmd, String commandlabel, String[] args){
		if(plugin.getPermissionManager().hasPermission(sender, "ds_title.admin")){
			if(plugin.getPermissionManager().isVaultEnabled()){
				if(args.length<3) return false;
				Title title = plugin.getTitle(args[2]);
				if(title!=null){
					plugin.getPermissionManager().getVaultPermissionInstance().playerAdd((String)null, args[1], title.permission);
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.title_granted").replace("{title}", title.name).replace("{name}", args[1]));
				}else{
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_title_not_found"));
				}
				
			}else{
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_vault"));
			}			
			return true;
		}else{
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_permission"));
			return true;
		}
	}
	
	private boolean cmdTitleUngrant(CommandSender sender, Command cmd, String commandlabel, String[] args){
		if(plugin.getPermissionManager().hasPermission(sender, "ds_title.admin")){
			if(plugin.getPermissionManager().isVaultEnabled()){
				if(args.length<3) return false;
				Title title = plugin.getTitle(args[2]);
				if(title!=null){
					plugin.getPermissionManager().getVaultPermissionInstance().playerRemove((String)null, args[1], title.permission);
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.title_ungranted").replace("{title}", title.name).replace("{name}", args[1]));
				}else{
					plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_title_not_found"));
				}
				
			}else{
				plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_vault"));
			}			
			return true;
		}else{
			plugin.sendMessage(sender, plugin.getConfig().getString("messages.error_no_permission"));
			return true;
		}
	}
}
