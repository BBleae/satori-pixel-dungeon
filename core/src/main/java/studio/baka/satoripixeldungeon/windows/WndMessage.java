package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;

public class WndMessage extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;
	private static final int MARGIN = 4;
	
	public WndMessage( String text ) {
		
		super();
		
		RenderedTextBlock info = PixelScene.renderTextBlock( text, 6 );
		info.maxWidth((SPDSettings.landscape() ? WIDTH_L : WIDTH_P) - MARGIN * 2);
		info.setPos(MARGIN, MARGIN);
		add( info );

		resize(
			(int)info.width() + MARGIN * 2,
			(int)info.height() + MARGIN * 2 );
	}
}
