package denniss17.dsTitle.objects;

/**
 * Class representing a single title
 */
public class Title implements Comparable<Title>
{
	public String name = "";
	public String chatTag = "";
	public String headTag = "";
	public String permission = "";
	public String description = "";
	public String symbol = "";
	
	public Title(String name, String chatTag, String headTag, String permission, String description, String symbol)
    {
		this.name = name;
		this.chatTag = chatTag;
		this.headTag = headTag;
		this.permission = permission;
		this.description = description;
		this.symbol = symbol;
	}
	
	@Override
	public int compareTo(Title otherTitle) {
		return otherTitle.name.compareTo(this.name);
	}
	
}
