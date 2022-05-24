package denniss17.dsTitle.permissions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import denniss17.dsTitle.DSTitle;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

public class LuckPermsHook{
	
	private DSTitle plugin;
	private LuckPerms api;
	
	public LuckPermsHook(DSTitle plugin) {
		this.plugin = plugin;
	}
	
	public boolean registerLuckPerms() {
		RegisteredServiceProvider<LuckPerms> provider = null;
		if(plugin.getPermissionManager()==null) {
			provider = this.plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);
	    }		
		if (provider != null) {
		    api = provider.getProvider();
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
		User user = this.api.getPlayerAdapter(Player.class).getUser(player);
		Node node = Node.builder(perm).build();
		user.data().add(node);
		this.api.getUserManager().saveUser(user);
	}
	
	public void removePermission(Player player, String perm) {
		User user = this.api.getPlayerAdapter(Player.class).getUser(player);
		Node node = Node.builder(perm).build();
		user.data().remove(node);
		this.api.getUserManager().saveUser(user);
	}
	
	public LuckPerms getLuckPermsAPIInstance() {
		return this.api;
	}
	
}