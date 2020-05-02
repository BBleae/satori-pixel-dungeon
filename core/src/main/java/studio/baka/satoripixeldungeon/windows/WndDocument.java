package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.journal.Document;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.ScrollPane;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

//FIXME lots of copy-pasta from WndJournal here. should generalize some of this. Primarily ListItem
public class WndDocument extends Window {
	
	private static final int WIDTH_P    = 120;
	private static final int HEIGHT_P   = 160;
	
	private static final int WIDTH_L    = 160;
	private static final int HEIGHT_L   = 128;
	
	private static final int ITEM_HEIGHT	= 18;

    private final ArrayList<docPage> pages = new ArrayList<>();
	
	public WndDocument( Document doc ){
		
		int w = SPDSettings.landscape() ? WIDTH_L : WIDTH_P;
		int h = SPDSettings.landscape() ? HEIGHT_L : HEIGHT_P;
		
		resize(w, h);

        ScrollPane list = new ScrollPane(new Component()) {
            @Override
            public void onClick(float x, float y) {
                int size = pages.size();
				for (docPage page : pages) {
					if (page.onClick(x, y)) {
						break;
					}
				}
            }
        };
		add(list);
		
		list.setRect( 0, 0, w, h);
		
		Component content = list.content();
		
		float pos = 0;
		
		ColorBlock line = new ColorBlock( w, 1, 0xFF222222);
		line.y = pos;
		content.add(line);
		
		RenderedTextBlock title = PixelScene.renderTextBlock(doc.title(), 9);
		title.hardlight(TITLE_COLOR);
		title.maxWidth( w - 2 );
		title.setPos( (w - title.width())/2f, pos + 1 + ((ITEM_HEIGHT) - title.height())/2f);
		PixelScene.align(title);
		content.add(title);
		
		pos += Math.max(ITEM_HEIGHT, title.height());
		
		for (String page : doc.pages()){
			docPage item = new docPage( doc, page );
			
			item.setRect( 0, pos, w, ITEM_HEIGHT );
			content.add( item );
			
			pos += item.height();
			pages.add(item);
		}
		
		content.setSize( w, pos );
		list.setSize( list.width(), list.height() );
		
	}
	
	private static class docPage extends ListItem {
		
		private boolean found;
		
		private final Document doc;
		private final String page;
		
		public docPage(Document doc, String page ){
			super( new ItemSprite( doc.pageSprite(), null),
					Messages.titleCase(doc.pageTitle(page)), -1);
			
			this.doc = doc;
			
			this.page = page;
			found = doc.hasPage(page);
			
			if (!found) {
				icon.hardlight( 0.5f, 0.5f, 0.5f);
				label.text( Messages.titleCase(Messages.get( WndDocument.class, "missing" )));
				label.hardlight( 0x999999 );
			}
			
		}
		
		public boolean onClick( float x, float y ) {
			if (inside( x, y ) && found) {
				SatoriPixelDungeon.scene().addToFront( new WndStory( doc.pageBody(page) ));
				return true;
			} else {
				return false;
			}
		}
		
	}
	
	private static class ListItem extends Component {
		
		protected RenderedTextBlock label;
		protected BitmapText depth;
		protected ColorBlock line;
		protected Image icon;
		
		public ListItem( Image icon, String text ) {
			this(icon, text, -1);
		}
		
		public ListItem( Image icon, String text, int d ) {
			super();
			
			this.icon.copy(icon);
			
			label.text( text );
			
			if (d >= 0) {
				depth.text(Integer.toString(d));
				depth.measure();
				
				if (d == Dungeon.depth) {
					label.hardlight(TITLE_COLOR);
					depth.hardlight(TITLE_COLOR);
				}
			}
		}
		
		@Override
		protected void createChildren() {
			label = PixelScene.renderTextBlock( 7 );
			add( label );
			
			icon = new Image();
			add( icon );
			
			depth = new BitmapText( PixelScene.pixelFont);
			add( depth );
			
			line = new ColorBlock( 1, 1, 0xFF222222);
			add(line);
			
		}
		
		@Override
		protected void layout() {
			
			icon.y = y + 1 + (height() - 1 - icon.height()) / 2f;
			PixelScene.align(icon);
			
			depth.x = icon.x + (icon.width - depth.width()) / 2f;
			depth.y = icon.y + (icon.height - depth.height()) / 2f + 1;
			PixelScene.align(depth);
			
			line.size(width, 1);
			line.x = 0;
			line.y = y;
			
			label.maxWidth((int)(width - icon.width() - 8 - 1));
			label.setPos(icon.x + icon.width() + 1, y + 1 + (height() - label.height()) / 2f);
			PixelScene.align(label);
		}
	}
	
}
