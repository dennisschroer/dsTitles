package denniss17.dsTitle;

import java.util.regex.Pattern;

public class ChatStyler {
	
	protected static Pattern ColorPattern = Pattern.compile("(?i)&([0-9A-F])");
	protected static Pattern RandomPattern = Pattern.compile("(?i)&([K])");
	protected static Pattern BoldPattern = Pattern.compile("(?i)&([L])");
	protected static Pattern StrikethroughPattern = Pattern.compile("(?i)&([M])");
	protected static Pattern UnderlinePattern = Pattern.compile("(?i)&([N])");
	protected static Pattern ItalicPattern = Pattern.compile("(?i)&([O])");
	protected static Pattern ResetPattern = Pattern.compile("(?i)&([R])");
	
	/** Sets the message color, message style and the random chars style in the given string
     * @param string The string of which the style has to be setted.
     * @return The string with the right style
     */
	public static String setTotalStyle(String string){
		string = setMessageColor(string);
		string = setMessageStyle(string);
		string = setRandomStyle(string);
		return string;
	}
	
	/** Sets the random chars style in the given string (&k)
     * @param string The string of which the style has to be setted.
     * @return The string with the right style
     */
	public static String setRandomStyle(String string) {
		return string==null ? null : RandomPattern.matcher(string).replaceAll("\u00A7$1");
	}
	
	/** Sets the message style in the given string (&l, &m, &n, &o)
     * @param string The string of which the style has to be setted.
     * @return The string with the right style
     */
	public static String setMessageStyle(String string) {
		if(string==null){
			return null;
		}else{
			String result = string;
			result = BoldPattern.matcher(result).replaceAll("\u00A7$1");
			result = StrikethroughPattern.matcher(result).replaceAll("\u00A7$1");
			result = UnderlinePattern.matcher(result).replaceAll("\u00A7$1");
			result = ItalicPattern.matcher(result).replaceAll("\u00A7$1");
			result = ResetPattern.matcher(result).replaceAll("\u00A7$1");
			return result;
		}
	}

	/** Sets the message color in the given string (&0 till &f)
     * @param string The string of which the style has to be setted.
     * @return The string with the right style
     */
	public static String setMessageColor(String string){
		return string==null ? null : ColorPattern.matcher(string).replaceAll("\u00A7$1");
	}
}
