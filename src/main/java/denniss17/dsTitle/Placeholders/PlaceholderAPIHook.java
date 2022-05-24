package denniss17.dsTitle.Placeholders;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.kaltiz.dsTitle.TitleManager;
import denniss17.dsTitle.DSTitle;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {
	
	private static DSTitle plugin;
	
	public PlaceholderAPIHook(JavaPlugin dstitle) {
		plugin = (DSTitle) dstitle;
	}
	
    @Override
    public boolean persist(){
        return true;
    }  

   @Override
   public boolean canRegister(){
       return true;
   }

   @Override
   public String getAuthor(){
       return plugin.getDescription().getAuthors().toString();
   }

	@Override
	public String getIdentifier(){
		return "dsTitle";
	}

	@Override
	public String getVersion(){
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier){
		if(player == null){
			return "";
		}
		if(identifier.equals("prefix")){
        	TitleManager man = plugin.getTitleManager();
            if (man != null)
            {
          	String prefixTag;
          	prefixTag = man.getPrefixChatTag(player);
              if (prefixTag != null)
              {
                return man.getPrefixChatTag(player);
              }else
              {
                return " ";
              }
            }else
            {
              return " ";
            }
		}
		if(identifier.equals("suffix")){
        	TitleManager man = plugin.getTitleManager();
            if (man != null)
            {
          	String suffixTag;
          	suffixTag = man.getSuffixChatTag(player);
              if (suffixTag != null)
              {
                return man.getSuffixChatTag(player);
              }else
              {
                return " ";
              }
            }else
            {
              return " ";
            }
		}
		if(identifier.equals("prefix_symbol")){
        	TitleManager man = plugin.getTitleManager();
            if (man != null)
            {
          	String prefixSymbol;
          	prefixSymbol = man.getPrefixSymbol(player);
              if (prefixSymbol != null)
              {
                return man.getPrefixSymbol(player);
              }else
              {
                return " ";
              }
            }else
            {
              return " ";
            }
		}
		if(identifier.equals("suffix_symbol")){
        	TitleManager man = plugin.getTitleManager();
            if (man != null)
            {
          	String suffixSymbol;
          	suffixSymbol = man.getSuffixSymbol(player);
              if (suffixSymbol != null)
              {
                return man.getSuffixSymbol(player);
              }else
              {
                return " ";
              }
            }else
            {
              return " ";
            }
		}
		return null;
	}
}
