package denniss17.dsTitle.Placeholders;

import org.bukkit.entity.Player;

import com.kaltiz.dsTitle.TitleManager;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import denniss17.dsTitle.DSTitle;

public class mvdwPlaceholderAPIHook{
	
	private DSTitle plugin;
	
	public mvdwPlaceholderAPIHook(DSTitle plugin) {
		this.plugin = plugin;
	}
	
	public boolean registerPlaceholders() {
		if(dsTitlePrefixHook() && dsTitleSuffixHook() && dsTitleSuffixSymbolHook() && dsTitlePrefixSymbolHook())
			return true;
		return false;
	}
	
	private boolean dsTitlePrefixHook(){
		PlaceholderReplacer dsTitlePrefix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						if(plugin!=null){
							TitleManager man = plugin.getTitleManager();
			                if (man != null)
			                {
			                	String prefixTag;
			                	prefixTag = man.getPrefixChatTag(p);
				                if (prefixTag != null)
				                {
				                	return man.getPrefixChatTag(p);
				                }
			                }
						}				
					}
					return "";		
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "dstitle_prefix", dsTitlePrefix);
	}
	
	private boolean dsTitleSuffixHook(){
		PlaceholderReplacer dsTitleSuffix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace (
				PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						if(plugin!=null){
							TitleManager man = plugin.getTitleManager();
		                    if (man != null)
		                    {
		                    	String suffixTag;
		                    	suffixTag = man.getSuffixChatTag(p);
		                    	if (suffixTag != null)
		                    	{
		                    		return man.getSuffixChatTag(p);
		                    	}
		                    }
						}						
					}	
					return "";
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "dstitle_suffix", dsTitleSuffix); 
	}
	
	private boolean dsTitleSuffixSymbolHook(){
		PlaceholderReplacer dsTitleSuffix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace (
				PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						if(plugin!=null){
							TitleManager man = plugin.getTitleManager();
		                    if (man != null)
		                    {
		                    	String symbol;
		                    	symbol = man.getSuffixSymbol(p);
		                    	if (symbol != null)
		                    	{
		                    		return man.getSuffixSymbol(p);
		                    	}
		                    }
						}						
					}	
					return "";
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "dstitle_suffix_symbol", dsTitleSuffix); 
	}
	
	private boolean dsTitlePrefixSymbolHook(){
		PlaceholderReplacer dsTitleSuffix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace (
				PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						if(plugin!=null){
							TitleManager man = plugin.getTitleManager();
		                    if (man != null)
		                    {
		                    	String symbol;
		                    	symbol = man.getPrefixSymbol(p);
		                    	if (symbol != null)
		                    	{
		                    		return man.getPrefixSymbol(p);
		                    	}
		                    }
						}						
					}	
					return "";
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "dstitle_prefix_symbol", dsTitleSuffix); 
	}
}