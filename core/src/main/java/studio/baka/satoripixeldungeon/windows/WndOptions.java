package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;

public class WndOptions extends Window {

	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;

	private static final int MARGIN 		= 2;
	private static final int BUTTON_HEIGHT	= 20;
	
	public WndOptions( String title, String message, String... options ) {
		super();

		int width = SPDSettings.landscape() ? WIDTH_L : WIDTH_P;

		RenderedTextBlock tfTitle = PixelScene.renderTextBlock( title, 9 );
		tfTitle.hardlight( TITLE_COLOR );
		tfTitle.setPos(MARGIN, MARGIN);
		tfTitle.maxWidth(width - MARGIN * 2);
		add( tfTitle );
		
		RenderedTextBlock tfMesage = PixelScene.renderTextBlock( 6 );
		tfMesage.text(message, width - MARGIN * 2);
		tfMesage.setPos( MARGIN, tfTitle.top() + tfTitle.height() + 2*MARGIN );
		add( tfMesage );
		
		float pos = tfMesage.bottom() + 2*MARGIN;
		
		for (int i=0; i < options.length; i++) {
			final int index = i;
			RedButton btn = new RedButton( options[i] ) {
				@Override
				protected void onClick() {
					hide();
					onSelect( index );
				}
			};
			btn.setRect( MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT );
			add( btn );
			
			pos += BUTTON_HEIGHT + MARGIN;
		}
		
		resize( width, (int)pos );
	}
	
	protected void onSelect( int index ) {}
}
