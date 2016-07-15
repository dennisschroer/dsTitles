package denniss17.dsTitle.Placeholders;

import org.bukkit.entity.Player;

import com.kaltiz.dsTitle.TitleManager;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import denniss17.dsTitle.DSTitle;

public class mvdwPlaceholderAPIHook{
	public final static String blank = " ";
	public static boolean dsTitlePrefixHook(final DSTitle plugin){
		PlaceholderReplacer dsTitlePrefix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace (
				PlaceholderReplaceEvent event) {
					if(!event.getPlayer().equals(null)){
						Player p = event.getPlayer();
						TitleManager man = plugin.getTitleManager();
		                if (man != null)
		                {
		                	String prefixTag;
		                	prefixTag = man.getPrefixChatTag(p);
			                if (prefixTag != null)
			                {
			                	return man.getPrefixChatTag(p);
			                }else{
			                	return " ";
			                }
		                }else{
		                  return " ";
		                }
					}
					return blank;		
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "dsTitle_prefix", dsTitlePrefix); 
	}
	
	public static boolean dsTitleSuffixHook(final DSTitle plugin){
		PlaceholderReplacer dsTitleSuffix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace (
				PlaceholderReplaceEvent event) {
					if(!event.getPlayer().equals(null)){
						Player p = event.getPlayer();
						TitleManager man = plugin.getTitleManager();
	                    if (man != null)
	                    {
	                    	String suffixTag;
	                    	suffixTag = man.getSuffixChatTag(p);
	                    	if (suffixTag != null)
	                    	{
	                    		return man.getSuffixChatTag(p);
	                    	}else{
	                    		return " ";
	                    	}
	                    }else{
	                    	return " ";
	                    }
					}
					return blank;		
				}
		};
		return PlaceholderAPI.registerPlaceholder(plugin, "dsTitle_suffix", dsTitleSuffix); 
	}
}