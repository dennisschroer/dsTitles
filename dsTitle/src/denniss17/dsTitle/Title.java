package denniss17.dsTitle;

/**
 * Class representing a single title
 */
class Title implements Comparable<Title>{
	enum Type {PREFIX, SUFFIX};
	public String name;
	public Type type;
	public String chatTag;
	public String headTag;
	/*public String prefix;
	public String suffix;
	public String headprefix;
	public String headsuffix;*/
	public String permission;
	public String description;
	
	public Title(String name, Type type, String chatTag, String headTag, String permission, String description){
		this.name = name;
		this.type = type;
		this.chatTag = chatTag;
		this.headTag = headTag;
		this.permission = permission;
		this.description = description;
	}
	
	@Override
	public int compareTo(Title otherTitle) {
		return otherTitle.name.compareTo(this.name);
	}
	
}
