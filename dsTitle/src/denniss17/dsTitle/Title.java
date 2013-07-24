package denniss17.dsTitle;

/**
 * Class representing a single title
 */
class Title implements Comparable<Title>{
	public String name;
	public String prefix;
	public String suffix;
	public String headprefix;
	public String headsuffix;
	public String permission;
	public String description;
	
	public Title(String name, String prefix, String suffix, String headprefix, String headsuffix, String permission, String description){
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
		this.headprefix = headprefix;
		this.headsuffix = headsuffix;
		this.permission = permission;
		this.description = description;
	}
	
	@Override
	public int compareTo(Title otherTitle) {
		return otherTitle.name.compareTo(this.name);
	}
	
}
