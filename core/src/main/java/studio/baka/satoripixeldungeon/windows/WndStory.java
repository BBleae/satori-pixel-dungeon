package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Chrome;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.PointerArea;
import com.watabou.utils.SparseArray;

public class WndStory extends Window {

	private static final int WIDTH_P = 125;
	private static final int WIDTH_L = 160;
	private static final int MARGIN = 2;
	
	private static final float bgR	= 0.77f;
	private static final float bgG	= 0.73f;
	private static final float bgB	= 0.62f;
	
	public static final int ID_SEWERS		= 0;
	public static final int ID_PRISON		= 1;
	public static final int ID_CAVES		= 2;
	public static final int ID_CITY     	= 3;
	public static final int ID_HALLS		= 4;
	public static final int ID_LAST			= 5;
	public static final int ID_HALLOW		= 6;
	
	private static final SparseArray<String> CHAPTERS = new SparseArray<>();
	
	static {
		CHAPTERS.put( ID_SEWERS, "sewers" );
		CHAPTERS.put( ID_PRISON, "prison" );
		CHAPTERS.put( ID_CAVES, "caves" );
		CHAPTERS.put( ID_CITY, "city" );
		CHAPTERS.put( ID_HALLS, "halls" );
		CHAPTERS.put( ID_LAST, "last" );
		CHAPTERS.put( ID_HALLOW, "hallow" );
	}
	
	private final RenderedTextBlock tf;
	
	private float delay;
	
	public WndStory( String text ) {
		super( 0, 0, Chrome.get( Chrome.Type.SCROLL ) );
		
		tf = PixelScene.renderTextBlock( text, 6 );
		tf.maxWidth(SPDSettings.landscape() ?
					WIDTH_L - MARGIN * 2:
					WIDTH_P - MARGIN *2);
		tf.invert();
		tf.setPos(MARGIN, 2);
		add( tf );
		
		add( new PointerArea( chrome ) {
			@Override
			protected void onClick( PointerEvent event ) {
				hide();
			}
		} );
		
		resize( (int)(tf.width() + MARGIN * 2), (int)Math.min( tf.height()+2, 180 ) );
	}
	
	@Override
	public void update() {
		super.update();
		
		if (delay > 0 && (delay -= Game.elapsed) <= 0) {
			shadow.visible = chrome.visible = tf.visible = true;
		}
	}
	
	public static void showChapter( int id ) {
		
		if (Dungeon.chapters.contains( id )) {
			return;
		}
		
		String text = Messages.get(WndStory.class, CHAPTERS.get( id ));
		if (text != null) {
			WndStory wnd = new WndStory( text );
			if ((wnd.delay = 0.6f) > 0) {
				wnd.shadow.visible = wnd.chrome.visible = wnd.tf.visible = false;
			}
			
			Game.scene().add( wnd );
			
			Dungeon.chapters.add( id );
		}
	}
}
