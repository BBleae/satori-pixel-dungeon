package studio.baka.satoripixeldungeon.messages;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.messages.Languages;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.ResourceBundle;

/*
	Simple wrapper class for java resource bundles.

	The core idea here is that each string resource's key is a combination of the class definition and a local value.
	An object or static method would usually call this with an object/class reference (usually its own) and a local key.
	This means that an object can just ask for "name" rather than, say, "items.weapon.enchantments.death.name"
 */
public class Messages {

	/*
		use hashmap for two reasons. Firstly because android 2.2 doesn't support resourcebundle.containskey(),
		secondly so I can read in and combine multiple properties files,
		resulting in a more clean structure for organizing all the strings, instead of one big file.

		..Yes R.string would do this for me, but that's not multiplatform
	 */
	private static HashMap<String, String> strings;
	private static Languages lang;

	public static Languages lang(){
		return lang;
	}



	/**
	 * Setup Methods
	 */

	private static final String[] prop_files = new String[]{
			"studio.baka.satoripixeldungeon.messages.actors.actors",
			"studio.baka.satoripixeldungeon.messages.items.items",
			"studio.baka.satoripixeldungeon.messages.journal.journal",
			"studio.baka.satoripixeldungeon.messages.levels.levels",
			"studio.baka.satoripixeldungeon.messages.plants.plants",
			"studio.baka.satoripixeldungeon.messages.scenes.scenes",
			"studio.baka.satoripixeldungeon.messages.ui.ui",
			"studio.baka.satoripixeldungeon.messages.windows.windows",
			"studio.baka.satoripixeldungeon.messages.misc.misc"
	};

	static{
		setup(SPDSettings.language());
	}

	public static void setup( Languages lang ){
		strings = new HashMap<>();
		Messages.lang = lang;
		Locale locale = new Locale(lang.code());

		for (String file : prop_files) {
			ResourceBundle bundle = ResourceBundle.getBundle( file, locale);
			Enumeration<String> keys = bundle.getKeys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				String value = bundle.getString(key);
				
				//TODO do all desktop platforms read as ISO, or only windows?
				// should also move this to platform support, probably.
				if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
					try {
						value = new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
					} catch (Exception e) {
						SatoriPixelDungeon.reportException(e);
					}
				}
				
				strings.put(key, value);
			}
		}
	}



	/**
	 * Resource grabbing methods
	 */

	public static String get(String key, Object...args){
		return get(null, key, args);
	}

	public static String get(Object o, String k, Object...args){
		return get(o.getClass(), k, args);
	}

	public static String get(Class c, String k, Object...args){
		String key;
		if (c != null){
			key = c.getName().replace("studio.baka.satoripixeldungeon.", "");
			key += "." + k;
		} else
			key = k;

		if (strings.containsKey(key.toLowerCase(Locale.ENGLISH))){
			if (args.length > 0) return format(strings.get(key.toLowerCase(Locale.ENGLISH)), args);
			else return strings.get(key.toLowerCase(Locale.ENGLISH));
		} else {
			//this is so child classes can inherit properties from their parents.
			//in cases where text is commonly grabbed as a utility from classes that aren't mean to be instantiated
			//(e.g. flavourbuff.dispTurns()) using .class directly is probably smarter to prevent unnecessary recursive calls.
			if (c != null && c.getSuperclass() != null){
				return get(c.getSuperclass(), k, args);
			} else {
				return "!!!NO TEXT FOUND!!!";
			}
		}
	}



	/**
	 * String Utility Methods
	 */

	public static String format( String format, Object...args ) {
		try {
			return String.format(Locale.ENGLISH, format, args);
		} catch (IllegalFormatException e) {
			SatoriPixelDungeon.reportException( e );
			return format;
		}
	}

	public static String capitalize( String str ){
		if (str.length() == 0)  return str;
		else                    return Character.toTitleCase( str.charAt( 0 ) ) + str.substring( 1 );
	}

	//Words which should not be capitalized in title case, mostly prepositions which appear ingame
	//This list is not comprehensive!
	private static final HashSet<String> noCaps = new HashSet<>(
			Arrays.asList(//English
					"a", "an", "and", "of", "by", "to", "the", "x")
	);

	public static String titleCase( String str ){
		//English capitalizes every word except for a few exceptions
		if (lang == Languages.ENGLISH){
			StringBuilder result = new StringBuilder();
			//split by any unicode space character
			for (String word : str.split("(?<=\\p{Zs})")){
				if (noCaps.contains(word.trim().toLowerCase(Locale.ENGLISH).replaceAll(":|[0-9]", ""))){
					result.append(word);
				} else {
					result.append(capitalize(word));
				}
			}
			//first character is always capitalized.
			return capitalize(result.toString());
		}

		//Otherwise, use sentence case
		return capitalize(str);
	}
}