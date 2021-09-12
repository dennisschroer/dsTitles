package denniss17.dsTitle.permissions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import denniss17.dsTitle.DSTitle;
import net.milkbowl.vault.permission.Permission;

public class VaultHook{
	
	private Plugin plugin;
	private Permission permission;
	
	public VaultHook(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void registerVault() {
		RegisteredServiceProvider<Permission> provider = null;
		if(DSTitle.title.getPermissionManager()==null) {
			provider = 	plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);			
		}
		if (provider != null) {
			this.permission = provider.getProvider();
		}
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