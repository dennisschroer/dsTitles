package denniss17.dsTitle.objects;

import java.io.Serializable;

public class Prefix extends Title implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6048116634165772150L;

	public Prefix(String name, String chatTag, String headTag, String permission, String description, String symbol) {
		super(name, chatTag, headTag, permission, description, symbol);
	}
}