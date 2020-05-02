package studio.baka.satoripixeldungeon.android;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.android.windows.WndAndroidTextInput;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.watabou.utils.PlatformSupport;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AndroidPlatformSupport extends PlatformSupport {
	
	public void updateDisplaySize(){
		boolean landscape = SPDSettings.landscape();
		
		AndroidGame.instance.setRequestedOrientation(landscape ?
				ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE :
				ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		
		if (AndroidGame.view.getMeasuredWidth() == 0 || AndroidGame.view.getMeasuredHeight() == 0)
			return;
		
		Game.dispWidth = AndroidGame.view.getMeasuredWidth();
		Game.dispHeight = AndroidGame.view.getMeasuredHeight();
		
		if ((Game.dispWidth > Game.dispHeight) != landscape){
			int tmp = Game.dispWidth;
			Game.dispWidth = Game.dispHeight;
			Game.dispHeight = tmp;
		}
		
		float dispRatio = Game.dispWidth / (float)Game.dispHeight;
		
		float renderWidth = dispRatio > 1 ? PixelScene.MIN_WIDTH_L : PixelScene.MIN_WIDTH_P;
		float renderHeight = dispRatio > 1 ? PixelScene.MIN_HEIGHT_L : PixelScene.MIN_HEIGHT_P;
		
		//force power saver in this case as all devices must run at at least 2x scale.
		if (Game.dispWidth < renderWidth*2 || Game.dispHeight < renderHeight*2)
			SPDSettings.put( SPDSettings.KEY_POWER_SAVER, true );
		
		if (SPDSettings.powerSaver()){
			
			int maxZoom = (int)Math.min(Game.dispWidth/renderWidth, Game.dispHeight/renderHeight);
			
			renderWidth *= Math.max( 2, Math.round(1f + maxZoom*0.4f));
			renderHeight *= Math.max( 2, Math.round(1f + maxZoom*0.4f));
			
			if (dispRatio > renderWidth / renderHeight){
				renderWidth = renderHeight * dispRatio;
			} else {
				renderHeight = renderWidth / dispRatio;
			}
			
			final int finalW = Math.round(renderWidth);
			final int finalH = Math.round(renderHeight);
			if (finalW != Game.width || finalH != Game.height){
				
				AndroidGame.instance.runOnUiThread(() -> AndroidGame.view.getHolder().setFixedSize(finalW, finalH));
				
			}
		} else {
			AndroidGame.instance.runOnUiThread(() -> AndroidGame.view.getHolder().setSizeFromLayout());
		}
	}
	
	public void updateSystemUI() {
		
		AndroidGame.instance.runOnUiThread(() -> {
			boolean fullscreen = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
					|| !AndroidGame.instance.isInMultiWindowMode();

			if (fullscreen){
				AndroidGame.instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			} else {
				AndroidGame.instance.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
				if (SPDSettings.fullscreen()) {
					AndroidGame.instance.getWindow().getDecorView().setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
									| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
									| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
				} else {
					AndroidGame.instance.getWindow().getDecorView().setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
				}
			}
		});
		
	}
	
	@Override
	public void promptTextInput(final String title, final String hintText, final int maxLen, final boolean multiLine, final String posTxt, final String negTxt, final TextCallback callback) {
		Game.runOnRenderThread(() -> Game.scene().addToFront(new WndAndroidTextInput(title, hintText, maxLen, multiLine, posTxt, negTxt) {
			@Override
			protected void onSelect(boolean positive) {
				callback.onSelect(positive, getText());
			}
		})
		);
	}
	
	/* FONT SUPPORT */
	
	private int pageSize;
	private PixmapPacker packer;
	private boolean systemfont;
	
	//droid sans / roboto, or a custom pixel font, for use with Latin and Cyrillic languages
	private static FreeTypeFontGenerator basicFontGenerator;
	private static final HashMap<Integer, BitmapFont> basicFonts = new HashMap<>();
	
	//droid sans / nanum gothic / noto sans, for use with Korean
	private static FreeTypeFontGenerator KRFontGenerator;
	private static final HashMap<Integer, BitmapFont> KRFonts = new HashMap<>();
	
	//droid sans / noto sans, for use with Simplified Chinese
	private static FreeTypeFontGenerator SCFontGenerator;
	private static final HashMap<Integer, BitmapFont> SCFonts = new HashMap<>();
	
	//droid sans / noto sans, for use with Japanese
	private static FreeTypeFontGenerator JPFontGenerator;
	private static final HashMap<Integer, BitmapFont> JPFonts = new HashMap<>();
	
	private static HashMap<FreeTypeFontGenerator, HashMap<Integer, BitmapFont>> fonts;
	
	//special logic for handling korean android 6.0 font oddities
	private static boolean koreanAndroid6OTF = false;
	
	@Override
	public void setupFontGenerators(int pageSize, boolean systemfont) {
		//don't bother doing anything if nothing has changed
		if (fonts != null && this.pageSize == pageSize && this.systemfont == systemfont){
			return;
		}
		this.pageSize = pageSize;
		this.systemfont = systemfont;
		
		if (fonts != null){
			for (FreeTypeFontGenerator generator : fonts.keySet()){
				for (BitmapFont f : fonts.get(generator).values()){
					f.dispose();
				}
				fonts.get(generator).clear();
				generator.dispose();
			}
			fonts.clear();
			if (packer != null){
				for (PixmapPacker.Page p : packer.getPages()){
					p.getTexture().dispose();
				}
				packer.dispose();
			}
		}
		fonts = new HashMap<>();
		basicFontGenerator = KRFontGenerator = SCFontGenerator = JPFontGenerator = null;
		
		if (systemfont && Gdx.files.absolute("/system/fonts/Roboto-Regular.ttf").exists()) {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/Roboto-Regular.ttf"));
		} else if (systemfont && Gdx.files.absolute("/system/fonts/DroidSans.ttf").exists()){
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/DroidSans.ttf"));
		} else {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("pixel_font.ttf"));
		}
		
		//android 7.0+. all asian fonts are nicely contained in one spot
		if (Gdx.files.absolute("/system/fonts/NotoSansCJK-Regular.ttc").exists()) {
			//typefaces are 0-JP, 1-KR, 2-SC, 3-TC.
			int typeFace;
			/*case JAPANESE:
					typeFace = 0;
					break;
				case KOREAN:
					typeFace = 1;
					break;*/
            typeFace = 2;
            KRFontGenerator = SCFontGenerator = JPFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansCJK-Regular.ttc"), typeFace);
			
		//otherwise we have to go over a few possibilities.
		} else {
			
			//Korean font generators
			if (Gdx.files.absolute("/system/fonts/NanumGothic.ttf").exists()){
				KRFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NanumGothic.ttf"));
			} else if (Gdx.files.absolute("/system/fonts/NotoSansKR-Regular.otf").exists()){
				KRFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansKR-Regular.otf"));
				koreanAndroid6OTF = true;
			}
			
			//Chinese font generators
			if (Gdx.files.absolute("/system/fonts/NotoSansSC-Regular.otf").exists()){
				SCFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansSC-Regular.otf"));
			} else if (Gdx.files.absolute("/system/fonts/NotoSansHans-Regular.otf").exists()){
				SCFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansHans-Regular.otf"));
			}
			
			//Japaneses font generators
			if (Gdx.files.absolute("/system/fonts/NotoSansJP-Regular.otf").exists()){
				JPFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansJP-Regular.otf"));
			}
			
			//set up a fallback generator for any remaining fonts
			FreeTypeFontGenerator fallbackGenerator;
			if (Gdx.files.absolute("/system/fonts/DroidSansFallback.ttf").exists()){
				fallbackGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/DroidSansFallback.ttf"));
			} else {
				//no fallback font, just set to null =/
				fallbackGenerator = null;
			}
			
			if (KRFontGenerator == null) KRFontGenerator = fallbackGenerator;
			if (SCFontGenerator == null) SCFontGenerator = fallbackGenerator;
			if (JPFontGenerator == null) JPFontGenerator = fallbackGenerator;
			
		}
		
		if (basicFontGenerator != null) fonts.put(basicFontGenerator, basicFonts);
		if (KRFontGenerator != null) fonts.put(KRFontGenerator, KRFonts);
		if (SCFontGenerator != null) fonts.put(SCFontGenerator, SCFonts);
		if (JPFontGenerator != null) fonts.put(JPFontGenerator, JPFonts);
		
		//would be nice to use RGBA4444 to save memory, but this causes problems on some gpus =S
		packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
	}
	
	@Override
	public void resetGenerators() {
		for (FreeTypeFontGenerator generator : fonts.keySet()){
			for (BitmapFont f : fonts.get(generator).values()){
				f.dispose();
			}
			fonts.get(generator).clear();
			generator.dispose();
		}
		fonts.clear();
		if (packer != null){
			for (PixmapPacker.Page p : packer.getPages()){
				p.getTexture().dispose();
			}
			packer.dispose();
		}
		fonts = null;
		setupFontGenerators(pageSize, systemfont);
	}
	
	private static final Pattern KRMatcher = Pattern.compile("\\p{InHangul_Syllables}");
	private static final Pattern SCMatcher = Pattern.compile("\\p{InCJK_Unified_Ideographs}|\\p{InCJK_Symbols_and_Punctuation}|\\p{InHalfwidth_and_Fullwidth_Forms}");
	private static final Pattern JPMatcher = Pattern.compile("\\p{InHiragana}|\\p{InKatakana}");
	
	private static FreeTypeFontGenerator getGeneratorForString( String input ){
		if (KRMatcher.matcher(input).find()){
			return KRFontGenerator;
		} else if (SCMatcher.matcher(input).find()){
			return SCFontGenerator;
		} else if (JPMatcher.matcher(input).find()){
			return JPFontGenerator;
		} else {
			return basicFontGenerator;
		}
	}
	
	@Override
	public BitmapFont getFont(int size, String text) {
		FreeTypeFontGenerator generator = getGeneratorForString(text);
		
		if (generator == null){
			return null;
		}
		
		if (!fonts.get(generator).containsKey(size)) {
			FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
			parameters.size = size;
			parameters.flip = true;
			parameters.borderWidth = parameters.size / 10f;
			parameters.renderCount = 3;
			parameters.hinting = FreeTypeFontGenerator.Hinting.None;
			parameters.spaceX = -(int) parameters.borderWidth;
			parameters.incremental = true;
			if (generator == basicFontGenerator){
				//if we're using latin/cyrillic, we can safely pre-generate some common letters
				//(we define common as >4% frequency in english)
				parameters.characters = "�etaoinshrdl";
			} else {
				parameters.characters = "�";
			}
			parameters.packer = packer;
			
			try {
				BitmapFont font = generator.generateFont(parameters);
				font.getData().missingGlyph = font.getData().getGlyph('�');
				fonts.get(generator).put(size, font);
			} catch ( Exception e ){
				Game.reportException(e);
				return null;
			}
		}
		
		return fonts.get(generator).get(size);
	}
	
	//splits on newlines, underscores, and chinese/japaneses characters
	private final Pattern regularsplitter = Pattern.compile(
			"(?<=\n)|(?=\n)|(?<=_)|(?=_)|" +
					"(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
					"(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
					"(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
					"(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})|" +
					"(?<=\\p{InHalfwidth_and_Fullwidth_Forms})|(?=\\p{InHalfwidth_and_Fullwidth_Forms})");
	
	//additionally splits on words, so that each word can be arranged individually
	private final Pattern regularsplitterMultiline = Pattern.compile(
			"(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=_)|(?=_)|" +
					"(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
					"(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
					"(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
					"(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})|" +
					"(?<=\\p{InHalfwidth_and_Fullwidth_Forms})|(?=\\p{InHalfwidth_and_Fullwidth_Forms})");
	
	//splits on each non-hangul character. Needed for weird android 6.0 font files
	private final Pattern android6KRSplitter = Pattern.compile(
			"(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=_)|(?=_)|" +
					"(?!\\p{InHangul_Syllables})|(?<!\\p{InHangul_Syllables})");
	
	@Override
	public String[] splitforTextBlock(String text, boolean multiline) {
		if (koreanAndroid6OTF && getGeneratorForString(text) == KRFontGenerator){
			return android6KRSplitter.split(text);
		} else if (multiline) {
			return regularsplitterMultiline.split(text);
		} else {
			return regularsplitter.split(text);
		}
	}
	
}
