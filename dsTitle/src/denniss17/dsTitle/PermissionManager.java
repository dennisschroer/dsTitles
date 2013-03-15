package denniss17.dsTitle;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/** Class for handling permissions and loading vault support */
public class PermissionManager {
	private JavaPlugin plugin;
	private boolean vaultEnabled;
	private Permission permissions;
	
	public PermissionManager(JavaPlugin plugin){
		this.vaultEnabled = setupPermissions();
	}
	
	/** Check if a player has this permission */
	public boolean hasPermission(Player player, String perm){
		return vaultEnabled ? this.permissions.has(player, perm) : player.hasPermission(perm);
	}
	
	/** Check if a player has this permission */
	public boolean hasPermission(CommandSender sender, String perm){
		return vaultEnabled ? this.permissions.has(sender, perm) : sender.hasPermission(perm);
	}
	
	/** Add the permission to this player
	 * Only works if vault enabled
	 * @param player
	 * @param perm
	 * @return true on success
	 */
	public boolean addPermission(Player player, String perm){
		if(vaultEnabled){ this.permissions.playerAdd(player, perm); }
		return vaultEnabled;
	}
	
	/**
	 * Is Vault enabled?
	 * @return true if Vault is enabled
	 */
	public boolean isVaultEnabled(){
		return this.vaultEnabled;
	}
	
	/** Load Vault */
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permissions = permissionProvider.getProvider();
		}
		return (permissions != null);
	}
}
