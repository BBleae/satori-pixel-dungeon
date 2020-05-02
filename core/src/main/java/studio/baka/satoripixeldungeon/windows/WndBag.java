package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.actors.hero.Belongings;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.EquipableItem;
import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.Recipe;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.bags.Bag;
import studio.baka.satoripixeldungeon.items.bags.MagicalHolster;
import studio.baka.satoripixeldungeon.items.bags.PotionBandolier;
import studio.baka.satoripixeldungeon.items.bags.ScrollHolder;
import studio.baka.satoripixeldungeon.items.bags.VelvetPouch;
import studio.baka.satoripixeldungeon.items.food.Food;
import studio.baka.satoripixeldungeon.items.potions.Potion;
import studio.baka.satoripixeldungeon.items.scrolls.Scroll;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTransmutation;
import studio.baka.satoripixeldungeon.items.spells.Recycle;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.items.weapon.SpiritBow;
import studio.baka.satoripixeldungeon.items.weapon.melee.MeleeWeapon;
import studio.baka.satoripixeldungeon.items.weapon.missiles.MissileWeapon;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Plant.Seed;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.Icons;
import studio.baka.satoripixeldungeon.ui.ItemSlot;
import studio.baka.satoripixeldungeon.ui.QuickSlotButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class WndBag extends WndTabbed {
	
	//only one wnditem can appear at a time
	private static WndBag INSTANCE;
	
	//FIXME this is getting cumbersome, there should be a better way to manage this
	public enum Mode {
		ALL,
		UNIDENTIFED,
		UNCURSABLE,
		CURSABLE,
		UPGRADEABLE,
		QUICKSLOT,
		FOR_SALE,
		WEAPON,
		ARMOR,
		ENCHANTABLE,
		WAND,
		SEED,
		FOOD,
		POTION,
		SCROLL,
		UNIDED_POTION_OR_SCROLL,
		EQUIPMENT,
		TRANMSUTABLE,
		ALCHEMY,
		RECYCLABLE,
		NOT_EQUIPPED
	}

	protected static final int COLS_P    = 5;
	protected static final int COLS_L    = 9;
	
	protected static final int SLOT_WIDTH	= 24;
	protected static final int SLOT_HEIGHT	= 24;
	protected static final int SLOT_MARGIN	= 1;
	
	protected static final int TITLE_HEIGHT	= 14;
	
	private final Listener listener;
	private final WndBag.Mode mode;
	private final String title;

	private final int nCols;

	protected int count;
	protected int col;
	protected int row;
	
	private static Mode lastMode;
	private static Bag lastBag;
	
	public WndBag( Bag bag, Listener listener, Mode mode, String title ) {
		
		super();
		
		if( INSTANCE != null ){
			INSTANCE.hide();
		}
		INSTANCE = this;
		
		this.listener = listener;
		this.mode = mode;
		this.title = title;
		
		lastMode = mode;
		lastBag = bag;

		nCols = SPDSettings.landscape() ? COLS_L : COLS_P;
		int nRows = (int) Math.ceil((Belongings.BACKPACK_SIZE + 4) / (float) nCols);

		int slotsWidth = SLOT_WIDTH * nCols + SLOT_MARGIN * (nCols - 1);
		int slotsHeight = SLOT_HEIGHT * nRows + SLOT_MARGIN * (nRows - 1);

		placeTitle( bag, slotsWidth );
		
		placeItems( bag );

		resize( slotsWidth, slotsHeight + TITLE_HEIGHT );

		Belongings stuff = Dungeon.hero.belongings;
		Bag[] bags = {
			stuff.backpack,
			stuff.getItem( VelvetPouch.class ),
			stuff.getItem( ScrollHolder.class ),
			stuff.getItem( PotionBandolier.class ),
			stuff.getItem( MagicalHolster.class )};

		for (Bag b : bags) {
			if (b != null) {
				BagTab tab = new BagTab( b );
				add( tab );
				tab.select( b == bag );
			}
		}

		layoutTabs();
	}
	
	public static WndBag lastBag( Listener listener, Mode mode, String title ) {
		
		if (mode == lastMode && lastBag != null &&
			Dungeon.hero.belongings.backpack.contains( lastBag )) {
			
			return new WndBag( lastBag, listener, mode, title );
			
		} else {
			
			return new WndBag( Dungeon.hero.belongings.backpack, listener, mode, title );
			
		}
	}

	public static WndBag getBag( Class<? extends Bag> bagClass, Listener listener, Mode mode, String title ) {
		Bag bag = Dungeon.hero.belongings.getItem( bagClass );
		return bag != null ?
				new WndBag( bag, listener, mode, title ) :
				lastBag( listener, mode, title );
	}
	
	protected void placeTitle( Bag bag, int width ){
		
		RenderedTextBlock txtTitle = PixelScene.renderTextBlock(
				title != null ? Messages.titleCase(title) : Messages.titleCase( bag.name() ), 9 );
		txtTitle.hardlight( TITLE_COLOR );
		txtTitle.setPos(
				1,
				(TITLE_HEIGHT - txtTitle.height()) / 2f - 1
		);
		PixelScene.align(txtTitle);
		add( txtTitle );
		
		ItemSprite gold = new ItemSprite(ItemSpriteSheet.GOLD, null);
		gold.x = width - gold.width() - 1;
		gold.y = (TITLE_HEIGHT - gold.height())/2f - 1;
		PixelScene.align(gold);
		add(gold);
		
		BitmapText amt = new BitmapText( Integer.toString(Dungeon.gold), PixelScene.pixelFont );
		amt.hardlight(TITLE_COLOR);
		amt.measure();
		amt.x = width - gold.width() - amt.width() - 2;
		amt.y = (TITLE_HEIGHT - amt.baseLine())/2f - 1;
		PixelScene.align(amt);
		add(amt);
	}
	
	protected void placeItems( Bag container ) {
		
		// Equipped items
		Belongings stuff = Dungeon.hero.belongings;
		placeItem( stuff.weapon != null ? stuff.weapon : new Placeholder( ItemSpriteSheet.WEAPON_HOLDER ) );
		placeItem( stuff.armor != null ? stuff.armor : new Placeholder( ItemSpriteSheet.ARMOR_HOLDER ) );
		placeItem( stuff.misc1 != null ? stuff.misc1 : new Placeholder( ItemSpriteSheet.RING_HOLDER ) );
		placeItem( stuff.misc2 != null ? stuff.misc2 : new Placeholder( ItemSpriteSheet.RING_HOLDER ) );
		placeItem( stuff.misc3 != null ? stuff.misc3 : new Placeholder( ItemSpriteSheet.RING_HOLDER ) );

		// Items in the bag
		for (Item item : container.items.toArray(new Item[0])) {
			placeItem( item );
		}
		
		// Free Space
		while ((count - 4) < container.size) {
			placeItem( null );
		}
	}
	
	protected void placeItem( final Item item ) {
		
		int x = col * (SLOT_WIDTH + SLOT_MARGIN);
		int y = TITLE_HEIGHT + row * (SLOT_HEIGHT + SLOT_MARGIN);
		
		add( new ItemButton( item ).setPos( x, y ) );
		
		if (++col >= nCols) {
			col = 0;
			row++;
		}
		
		count++;
	}
	
	@Override
	public void onMenuPressed() {
		if (listener == null) {
			hide();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (listener != null) {
			listener.onSelect( null );
		}
		super.onBackPressed();
	}
	
	@Override
	protected void onClick( Tab tab ) {
		hide();
		Game.scene().addToFront(new WndBag(((BagTab) tab).bag, listener, mode, title));
	}
	
	@Override
	public void hide() {
		super.hide();
		if (INSTANCE == this){
			INSTANCE = null;
		}
	}
	
	@Override
	protected int tabHeight() {
		return 20;
	}
	
	private Image icon( Bag bag ) {
		if (bag instanceof VelvetPouch) {
			return Icons.get( Icons.SEED_POUCH );
		} else if (bag instanceof ScrollHolder) {
			return Icons.get( Icons.SCROLL_HOLDER );
		} else if (bag instanceof MagicalHolster) {
			return Icons.get( Icons.WAND_HOLSTER );
		} else if (bag instanceof PotionBandolier) {
			return Icons.get( Icons.POTION_BANDOLIER );
		} else {
			return Icons.get( Icons.BACKPACK );
		}
	}
	
	private class BagTab extends IconTab {

		private final Bag bag;
		
		public BagTab( Bag bag ) {
			super( icon(bag) );
			
			this.bag = bag;
		}
		
	}
	
	public static class Placeholder extends Item {
		{
			name = null;
		}
		
		public Placeholder( int image ) {
			this.image = image;
		}
		
		@Override
		public boolean isIdentified() {
			return true;
		}
		
		@Override
		public boolean isEquipped( Hero hero ) {
			return true;
		}
	}
	
	private class ItemButton extends ItemSlot {
		
		private static final int NORMAL		= 0x9953564D;
		private static final int EQUIPPED	= 0x9991938C;
		
		private final Item item;
		private ColorBlock bg;
		
		public ItemButton( Item item ) {
			
			super( item );

			this.item = item;
			if (item instanceof Gold) {
				bg.visible = false;
			}
			
			width = SLOT_WIDTH;
			height = SLOT_HEIGHT;
		}
		
		@Override
		protected void createChildren() {
			bg = new ColorBlock( SLOT_WIDTH, SLOT_HEIGHT, NORMAL );
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
		public void item( Item item ) {
			
			super.item( item );
			if (item != null) {

				bg.texture( TextureCache.createSolid( item.isEquipped( Dungeon.hero ) ? EQUIPPED : NORMAL ) );
				if (item.cursed && item.cursedKnown) {
					bg.ra = +0.3f;
					bg.ga = -0.15f;
				} else if (!item.isIdentified()) {
					if ((item instanceof EquipableItem || item instanceof Wand) && item.cursedKnown){
						bg.ba = 0.3f;
					} else {
						bg.ra = 0.3f;
						bg.ba = 0.3f;
					}
				}
				
				if (item.name() == null) {
					enable( false );
				} else {
					enable(
						mode == Mode.FOR_SALE && !item.unique && (item.price() > 0) && (!item.isEquipped( Dungeon.hero ) || !item.cursed) ||
						mode == Mode.UPGRADEABLE && item.isUpgradable() ||
						mode == Mode.UNIDENTIFED && !item.isIdentified() ||
						mode == Mode.UNCURSABLE && ScrollOfRemoveCurse.uncursable(item) ||
						mode == Mode.CURSABLE && ((item instanceof EquipableItem && !(item instanceof MissileWeapon)) || item instanceof Wand) ||
						mode == Mode.QUICKSLOT && (item.defaultAction != null) ||
						mode == Mode.WEAPON && (item instanceof MeleeWeapon) ||
						mode == Mode.ARMOR && (item instanceof Armor) ||
						mode == Mode.ENCHANTABLE && (item instanceof MeleeWeapon || item instanceof SpiritBow || item instanceof Armor) ||
						mode == Mode.WAND && (item instanceof Wand) ||
						mode == Mode.SEED && (item instanceof Seed) ||
						mode == Mode.FOOD && (item instanceof Food) ||
						mode == Mode.POTION && (item instanceof Potion) ||
						mode == Mode.SCROLL && (item instanceof Scroll) ||
						mode == Mode.UNIDED_POTION_OR_SCROLL && (!item.isIdentified() && (item instanceof Scroll || item instanceof Potion)) ||
						mode == Mode.EQUIPMENT && (item instanceof EquipableItem || item instanceof Wand) ||
						mode == Mode.ALCHEMY && Recipe.usableInRecipe(item) ||
						mode == Mode.TRANMSUTABLE && ScrollOfTransmutation.canTransmute(item) ||
						mode == Mode.NOT_EQUIPPED && !item.isEquipped(Dungeon.hero) ||
						mode == Mode.RECYCLABLE && Recycle.isRecyclable(item) ||
						mode == Mode.ALL
					);
				}
			} else {
				bg.color( NORMAL );
			}
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
			if (!lastBag.contains(item) && !item.isEquipped(Dungeon.hero)){

				hide();

			} else if (listener != null) {
				
				hide();
				listener.onSelect( item );
				
			} else {
				
				Game.scene().addToFront(new WndItem( WndBag.this, item ) );
				
			}
		}
		
		@Override
		protected boolean onLongClick() {
			if (listener == null && (item.defaultAction != null )) {
				hide();
				Dungeon.quickslot.setSlot( 0 , item );
				QuickSlotButton.refresh();
				return true;
			} else {
				return false;
			}
		}
	}
	
	public interface Listener {
		void onSelect( Item item );
	}
}
