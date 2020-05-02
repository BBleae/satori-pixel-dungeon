package com.watabou.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public abstract class PlatformSupport {
	
	public abstract void updateDisplaySize();
	
	public abstract void updateSystemUI();
	
	//FIXME this is currently used because no platform-agnostic text input has been implemented.
	//should look into doing that using either plain openGL or Libgdx's libraries
	public abstract void promptTextInput( String title, String hintText, int maxLen, boolean multiLine,
	                             String posTxt, String negTxt, TextCallback callback);
	
	public static abstract class TextCallback {
		public abstract void onSelect( boolean positive, String text );
	}
	
	//TODO should consider spinning this into its own class, rather than platform support getting ever bigger
	
	public abstract void setupFontGenerators(int pageSize, boolean systemFont );
	
	public abstract void resetGenerators();
	
	public abstract BitmapFont getFont(int size, String text);
	
	public abstract String[] splitforTextBlock( String text, boolean multiline );

}
