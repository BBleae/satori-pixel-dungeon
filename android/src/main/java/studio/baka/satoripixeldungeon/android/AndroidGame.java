package studio.baka.satoripixeldungeon.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;

import java.util.Objects;

public class AndroidGame extends AndroidApplication {
	
	public static AndroidApplication instance;
	@SuppressLint("StaticFieldLeak")
	protected static GLSurfaceView view;
	
	private AndroidPlatformSupport support;
	
	@SuppressLint("SourceLockedOrientationActivity")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		instance = this;
		
		try {
			Game.version = getPackageManager().getPackageInfo( getPackageName(), 0 ).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Game.version = "???";
		}
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				Game.versionCode = (int) getPackageManager().getPackageInfo( getPackageName(), 0 ).getLongVersionCode();
			} else {
				//noinspection deprecation
				Game.versionCode = getPackageManager().getPackageInfo( getPackageName(), 0 ).versionCode;
			}
		} catch (PackageManager.NameNotFoundException e) {
			Game.versionCode = 0;
		}
		
		// grab preferences directly using our instance first
		// so that we don't need to rely on Gdx.app, which isn't initialized yet.
		SPDSettings.setPrefsFromInstance(instance);
		
		//set desired orientation (if it exists) before initializing the app.
		if (SPDSettings.landscapeFromSettings() != null) {
			//noinspection ConstantConditions
			if (SPDSettings.landscapeFromSettings()){
				instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			} else {
				instance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}
		}
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.depth = 0;
		//use rgb888 on more modern devices for better visuals
		config.r = config.g = config.b = 8;

		config.useCompass = false;
		config.useAccelerometer = false;
		
		support = new AndroidPlatformSupport();
		
		support.updateSystemUI();
		
		initialize(new SatoriPixelDungeon(support), config);
		
		view = (GLSurfaceView)graphics.getView();

		TelephonyManager mgr =
				(TelephonyManager) instance.getSystemService(Activity.TELEPHONY_SERVICE);
		Objects.requireNonNull(mgr).listen(new PhoneStateListener(){

			@Override
			public void onCallStateChanged(int state, String incomingNumber)
			{
				if( state == TelephonyManager.CALL_STATE_RINGING ) {
					Music.INSTANCE.pause();

				} else if( state == TelephonyManager.CALL_STATE_IDLE ) {
					if (!Game.instance.isPaused()) {
						Music.INSTANCE.resume();
					}
				}

				super.onCallStateChanged(state, incomingNumber);
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		support.updateSystemUI();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
		super.onMultiWindowModeChanged(isInMultiWindowMode);
		support.updateSystemUI();
	}

	@Override
	public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration configuration) {
		super.onMultiWindowModeChanged(isInMultiWindowMode, configuration);
		support.updateSystemUI();
	}
}