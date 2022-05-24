package denniss17.dsTitle.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import denniss17.dsTitle.DSTitle;
//Provide handlings for Luckperms
/** Class for handling permissions and loading vault support */
public class PermissionManager {
	public VaultHook vHook;
    private LuckPermsHook lpHook;
	
	public PermissionManager(DSTitle plugin){
		// Enable LuckPerms
		if(plugin.getServer().getPluginManager().getPlugin("LuckPerms")!=null) {
			this.lpHook = new LuckPermsHook(plugin);
			if(lpHook.registerLuckPerms()) {
				plugin.getLogger().warning("LuckPerms Found. Using it for Permissions.");
			}
		}
		// Enable Vault //
		if(plugin.getServer().getPluginManager().getPlugin("Vault")!=null){
			this.vHook = new VaultHook(plugin);
			if(vHook.registerVault()) {
				if(this.lpHook!=null)
					plugin.getLogger().warning("Vault Found. Using it for OfflinePlayer Permissions. "
							+ "Consider adding \'vault-unsafe-lookups = true\' to LuckPerms config.yml"
							+ " to prevent it from blocking Grant and Ungrant commands");
				else
					plugin.getLogger().warning("Vault Found. Using it for Permissions.");
			}		
		}
		if(this.lpHook == null && this.vHook == null)
			plugin.getLogger().warning("Vault and/or LuckPerms not found. /title grant and /title ungrant won't work!");			
	}
	
	/** Add the permission to this player
	 * Only works if Luckperms enabled
	 * @param player
	 * @param perm
	 * @return true on success
	 */
	public void addPermission(Player player, String perm){
		if(isLuckPermsEnabled() && player.isOnline()) {
			lpHook.addPermission(player, perm);			
		}else if(isVaultEnabled()){
			vHook.addPermission(player, perm);
		}
	}
	
	public void removePermission(Player player, String perm){
		if(isLuckPermsEnabled() && player.isOnline()) {
			lpHook.removePermission(player, perm);
		}else if(isVaultEnabled()){
			vHook.removePermission(player, perm);
		}
	}
	
	/** Check if a player has this permission */
	public boolean hasPermission(Player player, String perm){
		return player.hasPermission(perm);
	}
	
	/** Check if a player has this permission */
	public boolean hasPermission(CommandSender sender, String perm){
		return sender.hasPermission(perm);
	}
	
	/**
	 * Is Vault enabled?
	 * @return true if Vault is enabled
	 */
	public boolean isVaultEnabled(){
		return vHook!=null;
	}
	
	/**
	 * Is LuckPerms enabled?
	 * @return true if LuckPerms is enabled
	 */
	public boolean isLuckPermsEnabled(){
		return lpHook!=null;
	}
	
}
