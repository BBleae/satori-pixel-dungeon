package com.watabou.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {
	
	//TODO might want to rename this file. this is the auto-generated name for android atm
	public static final String PREFS_FILE = "SatoriPixelDungeon";
	
	private static Preferences prefs;
	
	private static Preferences get() {
		if (prefs == null) {
			prefs = Gdx.app.getPreferences(PREFS_FILE);
		}
		return prefs;
	}
	
	//allows setting up of preferences without Gdx.app being initialized
	public static void setPrefsFromInstance (Application instance){
		prefs = instance.getPreferences(PREFS_FILE);
	}
	
	public static boolean contains( String key ){
		return get().contains( key );
	}
	
	public static int getInt( String key, int defValue ) {
		return getInt(key, defValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public static int getInt( String key, int defValue, int min, int max ) {
		try {
			int i = get().getInteger( key, defValue );
			if (i < min || i > max){
				int val = (int)GameMath.gate(min, i, max);
				put(key, val);
				return val;
			} else {
				return i;
			}
		} catch (ClassCastException e) {
			//SatoriPixelDungeon.reportException(e);
			put(key, defValue);
			return defValue;
		}
	}
	
	public static boolean getBoolean( String key, boolean defValue ) {
		try {
			return get().getBoolean(key, defValue);
		} catch (ClassCastException e) {
			//SatoriPixelDungeon.reportException(e);
			put(key, defValue);
			return defValue;
		}
	}
	
	public static String getString( String key, String defValue ) {
		return getString(key, defValue, Integer.MAX_VALUE);
	}
	
	public static String getString( String key, String defValue, int maxLength ) {
		try {
			String s = get().getString( key, defValue );
			if (s != null && s.length() > maxLength) {
				put(key, defValue);
				return defValue;
			} else {
				return s;
			}
		} catch (ClassCastException e) {
			//SatoriPixelDungeon.reportException(e);
			put(key, defValue);
			return defValue;
		}
	}
	
	public static void put( String key, int value ) {
		get().putInteger(key, value);
		get().flush();
	}
	
	public static void put( String key, boolean value ) {
		get().putBoolean(key, value);
		get().flush();
	}
	
	public static void put( String key, String value ) {
		get().putString(key, value);
		get().flush();
	}
	
}
