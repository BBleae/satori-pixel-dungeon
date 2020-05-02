package studio.baka.satoripixeldungeon.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class AndroidLauncher extends Activity {
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
			GdxNativesLoader.load();
			FreeType.initFreeType();
			
			Intent intent = new Intent(this, AndroidGame.class);
			startActivity(intent);
			finish();
		} catch (Exception e){
			TextView text = new TextView(this);
			text.setText("Shattered Pixel Dungeon cannot start because some of its code is missing!\n\n" +
					"This usually happens when the Google Play version of the game is installed from somewhere outside of Google Play.\n\n" +
					"If you're unsure of how to fix this, please email the developer (Evan@ShatteredPixel.com), and include this error message:\n\n" +
					e.getMessage());
			text.setTextSize(16);
			text.setTextColor(0xFFFFFFFF);
			text.setTypeface(Typeface.createFromAsset(getAssets(), "pixel_font.ttf"));
			text.setGravity(Gravity.CENTER_VERTICAL);
			text.setPadding(10, 10, 10, 10);
			setContentView(text);
		}
	}
}
