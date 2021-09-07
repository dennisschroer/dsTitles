package denniss17.dsTitle.objects;

import java.io.Serializable;

public class Suffix extends Title implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8902372653829023225L;

	public Suffix(String name, String chatTag, String headTag, String permission, String description) {
		super(name, chatTag, headTag, permission, description);
	}
}
