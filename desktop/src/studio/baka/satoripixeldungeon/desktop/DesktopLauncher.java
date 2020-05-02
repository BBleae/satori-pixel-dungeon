package studio.baka.satoripixeldungeon.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import com.watabou.noosa.Game;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

public class DesktopLauncher {
	public static void main (String[] arg) {
		
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            pw.flush();
            JOptionPane.showMessageDialog(null, "Satori Pixel Dungeon has crashed, sorry about that!\n\n" +
                    "If you could, please email this error message to me and I'll get it fixed (BBleae@outlook.com):\n\n" +
                    sw.toString(), "Game Crash!", JOptionPane.ERROR_MESSAGE);
            Gdx.app.exit();
        });
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.width = 1920/6;
		config.height = 1080/2;
		
		//uncapped (but vsynced) framerate when focused, paused when not focused
		config.foregroundFPS = 0;
		config.backgroundFPS = -1;
		
		//TODO rather than hardcoding these values when running debug
		// it would be nice to be able to fetch them from gradle in some way
		config.title = DesktopLauncher.class.getPackage().getSpecificationTitle();
		if (config.title == null) {
			config.title = "Satori's PD INDEV";
		}
		
		Game.version = DesktopLauncher.class.getPackage().getSpecificationVersion();
		if (Game.version == null) {
			Game.version = "1.7.3.x-INDEV";
		}
		
		try {
			Game.versionCode = Integer.parseInt(DesktopLauncher.class.getPackage().getImplementationVersion());
		} catch (NumberFormatException e) {
			Game.versionCode = 382;
		}
		
		new LwjglApplication(new SatoriPixelDungeon(new DesktopPlatformSupport()), config);
	}
}
