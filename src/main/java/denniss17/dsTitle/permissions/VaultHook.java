package denniss17.dsTitle.permissions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import denniss17.dsTitle.DSTitle;
import net.milkbowl.vault.permission.Permission;

public class VaultHook{
	
	private DSTitle plugin;
	private Permission permission;
	
	public VaultHook(DSTitle plugin) {
		this.plugin = plugin;
	}
	
	public boolean registerVault() {
		RegisteredServiceProvider<Permission> provider = null;
		if(plugin.getPermissionManager()==null) {
			provider = 	plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);			
		}
		if (provider != null) {
			this.permission = provider.getProvider();
			return true;
		}
		return false;
	}
	
	/** Add the permission to this player
	 * Only works if vault enabled
	 * @param player
	 * @param perm
	 * @return true on success
	 */
	public void addPermission(Player player, String perm){
		this.permission.playerAdd(player, perm);
	}
	
	public void removePermission(Player player, String perm){
		this.permission.playerRemove(player, perm);
	}
	
	public Permission getVaultPermissionInstance() {
		return this.permission;
	}
}