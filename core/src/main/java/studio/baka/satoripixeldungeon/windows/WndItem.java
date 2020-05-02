package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.ui.ItemSlot;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;

import java.util.ArrayList;
import java.util.Comparator;

public class WndItem extends Window {
	
	//only one wnditem can appear at a time
	private static WndItem INSTANCE;

	private static final float BUTTON_HEIGHT	= 16;
	
	private static final float GAP	= 2;
	
	private static final int WIDTH_MIN = 120;
	private static final int WIDTH_MAX = 220;

	public WndItem( final WndBag owner, final Item item ){
		this( owner, item, owner != null );
	}
	
	public WndItem( final WndBag owner, final Item item , final boolean options ) {
		
		super();
		
		if( INSTANCE != null ){
			INSTANCE.hide();
		}
		INSTANCE = this;

		int width = WIDTH_MIN;
		
		RenderedTextBlock info = PixelScene.renderTextBlock( item.info(), 6 );
		info.maxWidth(width);
		
		//info box can go out of the screen on landscape, so widen it
		while (SPDSettings.landscape()
				&& info.height() > 100
				&& width < WIDTH_MAX){
			width += 20;
			info.maxWidth(width);
		}
		
		IconTitle titlebar = new IconTitle( item );
		titlebar.setRect( 0, 0, width, 0 );
		add( titlebar );

		if (item.levelKnown && item.level() > 0) {
			titlebar.color( ItemSlot.UPGRADED );
		} else if (item.levelKnown && item.level() < 0) {
			titlebar.color( ItemSlot.DEGRADED );
		}
		
		info.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add( info );
	
		float y = info.top() + info.height() + GAP;
		
		if (Dungeon.hero.isAlive() && options) {
			ArrayList<RedButton> buttons = new ArrayList<>();
			for (final String action:item.actions( Dungeon.hero )) {
				
				RedButton btn = new RedButton( Messages.get(item, "ac_" + action), 8 ) {
					@Override
					protected void onClick() {
						hide();
						if (owner != null && owner.parent != null) owner.hide();
						if (Dungeon.hero.isAlive()) item.execute( Dungeon.hero, action );
					}
				};
				btn.setSize( btn.reqWidth(), BUTTON_HEIGHT );
				buttons.add(btn);
				add( btn );

				if (action.equals(item.defaultAction)) {
					btn.textColor( TITLE_COLOR );
				}
				
			}
			y = layoutButtons(buttons, width, y);
		}
		
		resize( width, (int)(y) );
	}

	private static float layoutButtons(ArrayList<RedButton> buttons, float width, float y){
		ArrayList<RedButton> curRow = new ArrayList<>();
		float widthLeftThisRow = width;
		
		while( !buttons.isEmpty() ){
			RedButton btn = buttons.get(0);
			
			widthLeftThisRow -= btn.width();
			if (curRow.isEmpty()) {
				curRow.add(btn);
				buttons.remove(btn);
			} else {
				widthLeftThisRow -= 1;
				if (widthLeftThisRow >= 0) {
					curRow.add(btn);
					buttons.remove(btn);
				}
			}
			
			//layout current row. Currently forces a max of 3 buttons but can work with more
			if (buttons.isEmpty() || widthLeftThisRow <= 0 || curRow.size() >= 3){
				
				//re-use this variable for laying out the buttons
				widthLeftThisRow = width - (curRow.size()-1);
				for (RedButton b : curRow){
					widthLeftThisRow -= b.width();
				}
				
				//while we still have space in this row, find the shortest button(s) and extend them
				while (widthLeftThisRow > 0){
					
					ArrayList<RedButton> shortest = new ArrayList<>();
					RedButton secondShortest = null;
					
					for (RedButton b : curRow) {
						if (shortest.isEmpty()) {
							shortest.add(b);
						} else {
							if (b.width() < shortest.get(0).width()) {
								secondShortest = shortest.get(0);
								shortest.clear();
								shortest.add(b);
							} else if (b.width() == shortest.get(0).width()) {
								shortest.add(b);
							} else if (secondShortest == null || secondShortest.width() > b.width()){
								secondShortest = b;
							}
						}
					}
					
					float widthToGrow;
					
					if (secondShortest == null){
						widthToGrow = widthLeftThisRow / shortest.size();
						widthLeftThisRow = 0;
					} else {
						widthToGrow = secondShortest.width() - shortest.get(0).width();
						if ((widthToGrow * shortest.size()) >= widthLeftThisRow){
							widthToGrow = widthLeftThisRow / shortest.size();
							widthLeftThisRow = 0;
						} else {
							widthLeftThisRow -= widthToGrow * shortest.size();
						}
					}
					
					for (RedButton toGrow : shortest){
						toGrow.setRect(0, 0, toGrow.width()+widthToGrow, toGrow.height());
					}
				}
				
				//finally set positions
				float x = 0;
				for (RedButton b : curRow){
					b.setRect(x, y, b.width(), b.height());
					x += b.width() + 1;
				}
				
				//move to next line and reset variables
				y += BUTTON_HEIGHT+1;
				widthLeftThisRow = width;
				curRow.clear();
				
			}
			
		}
		
		return y - 1;
	}
	
	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this){
			INSTANCE = null;
		}
	}
	
	private static final Comparator<RedButton> widthComparator = (lhs, rhs) -> {
        if (lhs.width() < rhs.width()){
            return -1;
        } else if (lhs.width() == rhs.width()){
            return 0;
        } else {
            return 1;
        }
    };
}
