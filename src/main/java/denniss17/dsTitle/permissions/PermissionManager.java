package denniss17.dsTitle.permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
//Provide handlings for Luckperms
/** Class for handling permissions and loading vault support */
public class PermissionManager {
	private boolean vaultEnabled = false;
	private boolean luckPermsEnabled = false;
	public VaultHook vHook;
    private LuckPermsHook lpHook;
	
	public PermissionManager(JavaPlugin plugin){
		// Enable LuckPerms
		if(plugin.getServer().getPluginManager().getPlugin("LuckPerms")!=null) {
			this.luckPermsEnabled = true;
			this.lpHook = new LuckPermsHook(plugin);
			lpHook.registerLuckPerms();
			plugin.getLogger().warning("LuckPerms Found. Using it for Permissions.");
		}
		// Enable Vault //
		if(plugin.getServer().getPluginManager().getPlugin("Vault")!=null){
			this.vHook = new VaultHook(plugin);
			vHook.registerVault();
			if(this.luckPermsEnabled)
				plugin.getLogger().warning("Vault Found. Using it for OfflinePlayer Permissions. "
						+ "Consider adding \'vault-unsafe-lookups = true\' to LuckPerms config.yml"
						+ " to prevent it from blocking Grant and Ungrant commands");
			else
				plugin.getLogger().warning("Vault Found. Using it for Permissions.");
			this.vaultEnabled = true;
		}
		if(this.luckPermsEnabled == false && this.vaultEnabled == false)
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
		if(isVaultEnabled()) {
			return vHook.hasPermission(player, perm);
		}else {
			return player.hasPermission(perm);
		}
		//check in Luckperms and Vault
	}
	
	/** Check if a player has this permission */
	public boolean hasPermission(CommandSender sender, String perm){
		if(isVaultEnabled()) {
			return vHook.hasPermission(sender, perm);
		}else {
			return sender.hasPermission(perm);
		}
		//check in Luckperms and Vault
	}
	
	/**
	 * Is Vault enabled?
	 * @return true if Vault is enabled
	 */
	public boolean isVaultEnabled(){
		return this.vaultEnabled;
	}
	
	/**
	 * Is LuckPerms enabled?
	 * @return true if LuckPerms is enabled
	 */
	public boolean isLuckPermsEnabled(){
		return this.luckPermsEnabled;
	}
	
}
