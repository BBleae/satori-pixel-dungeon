package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.items.Heap;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.ItemSlot;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;

public class WndInfoItem extends Window {
	
	private static final float GAP	= 2;
	
	private static final int WIDTH_P = 120;
	private static final int WIDTH_L = 144;
	
	public WndInfoItem( Heap heap ) {
		
		super();
		
		if (heap.type == Heap.Type.HEAP || heap.type == Heap.Type.FOR_SALE) {
			fillFields( heap.peek() );
			
		} else {
			fillFields( heap );
			
		}
	}
	
	public WndInfoItem( Item item ) {
		super();
		
		fillFields( item );
	}
	
	private void fillFields( Heap heap ) {
		
		int width = SPDSettings.landscape() ? WIDTH_L : WIDTH_P;
		
		IconTitle titlebar = new IconTitle( heap );
		titlebar.color( TITLE_COLOR );
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );
		
		RenderedTextBlock txtInfo = PixelScene.renderTextBlock( heap.info(), 6 );
		txtInfo.maxWidth(width);
		txtInfo.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add( txtInfo );
		
		resize( width, (int)(txtInfo.top() + txtInfo.height()) );
	}
	
	private void fillFields( Item item ) {
		
		int color = TITLE_COLOR;
		if (item.levelKnown && item.level() > 0) {
			color = ItemSlot.UPGRADED;
		} else if (item.levelKnown && item.level() < 0) {
			color = ItemSlot.DEGRADED;
		}
		
		int width = SPDSettings.landscape() ? WIDTH_L : WIDTH_P;

		IconTitle titlebar = new IconTitle( item );
		titlebar.color( color );
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );
		
		RenderedTextBlock txtInfo = PixelScene.renderTextBlock( item.info(), 6 );
		txtInfo.maxWidth(width);
		txtInfo.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add( txtInfo );
		
		resize( width, (int)(txtInfo.top() + txtInfo.height()) );
	}
}
