dsTitles
========

This is a bukkit plugin which adds titles to Minecraft.

New Features to this Fork :
* Registers Placeholders into PlaceholderAPI/DeluxeChat for dsTitle Suffixes/Prefixes. Use these in PlaceholderAPI by using %dsTitle_prefix% and %dsTitle_prefix_symbol% for prefix or %dsTitle_suffix% and %dsTitle_suffix_symbol% for suffix
* Registers PlaceHolders into mvdwPlaceholderAPI for dsTitle Suffixes/Prefixes. Use these in any mvdw plugin if you have mvdwPlaceholderAPI installed by using {dstitle_prefix} and {dstitle_prefix_symbol} for prefixes and {dstitle_suffix} and {dstitle_suffix_symbol} for suffix
* Ability to set a default prefix and default suffix
* Able to set prefix/suffix just by clicking on the prefix/suffix in the titles list (Json style messages)
* Fixed a few bugs, including the invisible players still having their nametags shown when dsTitle is handling nametags
* Added a prefix and suffix "symbol". Basically a one letter abbreviation of the prefix/suffix that can be used to shorten tags or use WolfieMario's Icon pack to set title icons.
* Ability to use Hex Colors in chatTag and description of titles. Use them like <#2AA8F2> and <gradient:#2AA8F2:#8BD448>
* Use HikariCP connection pooling and settings to prevent MySQL timeouts

For more info go to https://www.spigotmc.org/resources/dstitles.96043/

To compile, make sure you are running at least java 16 and Apache Maven.
Download the contents of this repo, then, from the command line,
navigate to the where you've stored the contents of this repo and run

mvn clean package

From the Command Line. The result will be located in the target directory.


* Special thanks to HexiTextLib for his hex lib
* Special Thanks to the HikariCP guys! <3