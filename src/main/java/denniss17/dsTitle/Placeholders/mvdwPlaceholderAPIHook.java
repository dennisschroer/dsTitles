package denniss17.dsTitle.Placeholders;

import org.bukkit.entity.Player;

import com.kaltiz.dsTitle.TitleManager;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import denniss17.dsTitle.DSTitle;

public class mvdwPlaceholderAPIHook{
	public static boolean dsTitlePrefixHook(){
		PlaceholderReplacer dsTitlePrefix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						TitleManager man = DSTitle.title.getTitleManager();
		                if (man != null)
		                {
		                	String prefixTag;
		                	prefixTag = man.getPrefixChatTag(p);
			                if (prefixTag != null)
			                {
			                	return man.getPrefixChatTag(p);
			                }else{
			                	return "tag is null";
			                }
		                }else{
		                  return "titlemanager is null";
		                }
					}
					return "";		
				}
		};
		return PlaceholderAPI.registerPlaceholder(DSTitle.title, "dstitle_prefix", dsTitlePrefix);
	}
	
	public static boolean dsTitleSuffixHook(){
		PlaceholderReplacer dsTitleSuffix = new PlaceholderReplacer (){
			@Override
			public String onPlaceholderReplace (
				PlaceholderReplaceEvent event) {
					if(event.getPlayer()!=null){
						Player p = event.getPlayer();
						TitleManager man = DSTitle.title.getTitleManager();
	                    if (man != null)
	                    {
	                    	String suffixTag;
	                    	suffixTag = man.getSuffixChatTag(p);
	                    	if (suffixTag != null)
	                    	{
	                    		return man.getSuffixChatTag(p);
	                    	}else{
	                    		return "";
	                    	}
	                    }else{
	                    	return "";
	                    }
					}	
					return "";
				}
		};
		return PlaceholderAPI.registerPlaceholder(DSTitle.title, "dstitle_suffix", dsTitleSuffix); 
	}
}