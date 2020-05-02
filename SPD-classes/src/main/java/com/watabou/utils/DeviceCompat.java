package com.watabou.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.watabou.noosa.Game;

//TODO migrate to platformSupport class
public class DeviceCompat {
	
	public static boolean supportsFullScreen(){
        if (Gdx.app.getType() == Application.ApplicationType.Android) {//Android 4.4 KitKat and later, this is for immersive mode
            return Gdx.app.getVersion() >= 19;
        }//TODO implement functionality for other platforms here
        return false;
    }
	
	public static boolean legacyDevice(){
        if (Gdx.app.getType() == Application.ApplicationType.Android) {//Devices prior to Android 4.1 Jelly Bean
            return Gdx.app.getVersion() < 16;
        }//TODO implement functionality for other platforms here
        return false;
    }
	
	public static boolean isDebug(){
		return Game.version.contains("INDEV");
	}
	
	public static void openURI( String URI ){
		Gdx.net.openURI( URI );
	}
	
	public static void log( String tag, String message ){
		Gdx.app.log( tag, message );
	}

}
