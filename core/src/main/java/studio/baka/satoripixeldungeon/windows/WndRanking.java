package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Rankings;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.hero.Belongings;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.HeroSprite;
import studio.baka.satoripixeldungeon.ui.BadgesList;
import studio.baka.satoripixeldungeon.ui.Icons;
import studio.baka.satoripixeldungeon.ui.ItemSlot;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.ScrollPane;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.Locale;

public class WndRanking extends WndTabbed {
	
	private static final int WIDTH			= 115;
	private static final int HEIGHT			= 144;
	
	private static Thread thread;
	private String error = null;
	
	private Image busy;
	
	public WndRanking( final Rankings.Record rec ) {
		
		super();
		resize( WIDTH, HEIGHT );
		
		if (thread != null){
			hide();
			return;
		}
		
		thread = new Thread(() -> {
            try {
                Badges.loadGlobal();
                Rankings.INSTANCE.loadGameData( rec );
            } catch ( Exception e ) {
                error = Messages.get(WndRanking.class, "error");
            }
        });

		busy = Icons.BUSY.get();
		busy.origin.set( busy.width / 2, busy.height / 2 );
		busy.angularSpeed = 720;
		busy.x = (WIDTH - busy.width) / 2;
		busy.y = (HEIGHT - busy.height) / 2;
		add( busy );
		
		thread.start();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (thread != null && !thread.isAlive() && busy != null) {
			if (error == null) {
				remove( busy );
				busy = null;
				if (Dungeon.hero != null) {
					createControls();
				} else {
					hide();
				}
			} else {
				hide();
				Game.scene().add( new WndError( error ) );
			}
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		thread = null;
	}
	
	private void createControls() {
		
		String[] labels =
			{Messages.get(this, "stats"), Messages.get(this, "items"), Messages.get(this, "badges")};
		Group[] pages =
			{new StatsTab(), new ItemsTab(), new BadgesTab()};
		
		for (int i=0; i < pages.length; i++) {
			
			add( pages[i] );
			
			Tab tab = new RankingTab( labels[i], pages[i] );
			add( tab );
		}

		layoutTabs();
		
		select( 0 );
	}

	private class RankingTab extends LabeledTab {
		
		private final Group page;
		
		public RankingTab( String label, Group page ) {
			super( label );
			this.page = page;
		}
		
		@Override
		protected void select( boolean value ) {
			super.select( value );
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}
	
	private static class StatsTab extends Group {

		private int GAP	= 5;
		
		public StatsTab() {
			super();

			if (Dungeon.challenges > 0) GAP--;
			
			String heroClass = Dungeon.hero.className();
			
			IconTitle title = new IconTitle();
			title.icon( HeroSprite.avatar( Dungeon.hero.heroClass, Dungeon.hero.tier() ) );
			title.label( Messages.get(this, "title", Dungeon.hero.lvl, heroClass ).toUpperCase( Locale.ENGLISH ) );
			title.color(Window.SHPX_COLOR);
			title.setRect( 0, 0, WIDTH, 0 );
			add( title );
			
			float pos = title.bottom() + GAP;

			if (Dungeon.challenges > 0) {
				RedButton btnChallenges = new RedButton( Messages.get(this, "challenges") ) {
					@Override
					protected void onClick() {
						Game.scene().add( new WndChallenges( Dungeon.challenges, false ) );
					}
				};
				float btnW = btnChallenges.reqWidth() + 2;
				btnChallenges.setRect( (WIDTH - btnW)/2, pos, btnW , btnChallenges.reqHeight() + 2 );
				add( btnChallenges );

				pos = btnChallenges.bottom();
			}

			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "str"), Integer.toString( Dungeon.hero.STR() ), pos );
			pos = statSlot( this, Messages.get(this, "health"), Integer.toString( Dungeon.hero.HT ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "duration"), Integer.toString( (int)Statistics.duration ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "depth"), Integer.toString( Statistics.deepestFloor ), pos );
			pos = statSlot( this, Messages.get(this, "enemies"), Integer.toString( Statistics.enemiesSlain ), pos );
			pos = statSlot( this, Messages.get(this, "gold"), Integer.toString( Statistics.goldCollected ), pos );
			
			pos += GAP;
			
			pos = statSlot( this, Messages.get(this, "food"), Integer.toString( Statistics.foodEaten ), pos );
			pos = statSlot( this, Messages.get(this, "alchemy"), Integer.toString( Statistics.potionsCooked ), pos );
			statSlot( this, Messages.get(this, "ankhs"), Integer.toString( Statistics.ankhsUsed ), pos );
		}
		
		private float statSlot( Group parent, String label, String value, float pos ) {
			
			RenderedTextBlock txt = PixelScene.renderTextBlock( label, 7 );
			txt.setPos(0, pos);
			parent.add( txt );
			
			txt = PixelScene.renderTextBlock( value, 7 );
			txt.setPos(WIDTH * 0.7f, pos);
			PixelScene.align(txt);
			parent.add( txt );
			
			return pos + GAP + txt.height();
		}
	}
	
	private class ItemsTab extends Group {
		
		private float pos;
		
		public ItemsTab() {
			super();
			
			Belongings stuff = Dungeon.hero.belongings;
			if (stuff.weapon != null) {
				addItem( stuff.weapon );
			}
			if (stuff.armor != null) {
				addItem( stuff.armor );
			}
			if (stuff.misc1 != null) {
				addItem( stuff.misc1);
			}
			if (stuff.misc2 != null) {
				addItem( stuff.misc2);
			}
			if (stuff.misc3 != null) {
				addItem( stuff.misc3);
			}

			pos = 0;
			for (int i = 0; i < 4; i++){
				if (Dungeon.quickslot.getItem(i) != null){
					QuickSlotButton slot = new QuickSlotButton(Dungeon.quickslot.getItem(i));

					slot.setRect( pos, 116, 28, 28 );

					add(slot);

				} else {
					ColorBlock bg = new ColorBlock( 28, 28, 0x9953564D );
					bg.x = pos;
					bg.y = 116;
					add(bg);
				}
				pos += 29;
			}
		}
		
		private void addItem( Item item ) {
			ItemButton slot = new ItemButton(item);
			slot.setRect( 0, pos, width, ItemButton.HEIGHT );
			add( slot );
			
			pos += slot.height() + 1;
		}
	}
	
	private class BadgesTab extends Group {
		
		public BadgesTab() {
			super();
			
			camera = WndRanking.this.camera;
			
			ScrollPane list = new BadgesList( false );
			add( list );
			
			list.setSize( WIDTH, HEIGHT );
		}
	}
	
	private static class ItemButton extends Button {
		
		public static final int HEIGHT	= 20;
		
		private final Item item;
		
		private ItemSlot slot;
		private ColorBlock bg;
		private RenderedTextBlock name;
		
		public ItemButton( Item item ) {
			
			super();

			this.item = item;
			
			slot.item( item );
			if (item.cursed && item.cursedKnown) {
				bg.ra = +0.2f;
				bg.ga = -0.1f;
			} else if (!item.isIdentified()) {
				bg.ra = 0.1f;
				bg.ba = 0.1f;
			}
		}
		
		@Override
		protected void createChildren() {

			//noinspection SuspiciousNameCombination
			bg = new ColorBlock( HEIGHT, HEIGHT, 0x9953564D );
			add( bg );
			
			slot = new ItemSlot();
			add( slot );
			
			name = PixelScene.renderTextBlock( 7 );
			add( name );
			
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			//noinspection SuspiciousNameCombination
			slot.setRect( x, y, HEIGHT, HEIGHT );
			PixelScene.align(slot);
			
			name.maxWidth((int)(width - slot.width() - 2));
			name.text(Messages.titleCase(item.name()));
			name.setPos(
					slot.right()+2,
					y + (height - name.height()) / 2
			);
			PixelScene.align(name);
			
			super.layout();
		}
		
		@Override
		protected void onPointerDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		}
		
		protected void onPointerUp() {
			bg.brightness( 1.0f );
		}
		
		@Override
		protected void onClick() {
			Game.scene().add( new WndItem( null, item ) );
		}
	}

	private static class QuickSlotButton extends ItemSlot{

		public static final int HEIGHT	= 28;

		private final Item item;
		private ColorBlock bg;

		QuickSlotButton(Item item){
			super(item);
			this.item = item;
		}

		@Override
		protected void createChildren() {
			//noinspection SuspiciousNameCombination
			bg = new ColorBlock( HEIGHT, HEIGHT, 0x9953564D );
			add( bg );

			super.createChildren();
		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			super.layout();
		}

		@Override
		protected void onPointerDown() {
			bg.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		}

		protected void onPointerUp() {
			bg.brightness( 1.0f );
		}

		@Override
		protected void onClick() {
			Game.scene().add(new WndItem(null, item));
		}
	}
}
