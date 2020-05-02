package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.GamesInProgress;
import studio.baka.satoripixeldungeon.SPDSettings;
import studio.baka.satoripixeldungeon.SatoriPixelDungeon;
import studio.baka.satoripixeldungeon.actors.hero.HeroClass;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.journal.Journal;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.InterlevelScene;
import studio.baka.satoripixeldungeon.scenes.IntroScene;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.ui.ActionIndicator;
import studio.baka.satoripixeldungeon.ui.IconButton;
import studio.baka.satoripixeldungeon.ui.Icons;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Button;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

public class WndStartGame extends Window {
	
	private static final int WIDTH    = 120;
	private static final int HEIGHT   = 140;

	public WndStartGame(final int slot){
		
		Badges.loadGlobal();
		Journal.loadGlobal();
		
		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 12 );
		title.hardlight(Window.TITLE_COLOR);
		title.setPos( (WIDTH - title.width())/2f, 3);
		PixelScene.align(title);
		add(title);
		
		float heroBtnSpacing = (WIDTH - 5*HeroBtn.WIDTH)/5f;
		
		float curX = heroBtnSpacing;
		for (HeroClass cl : HeroClass.values()){
			HeroBtn button = new HeroBtn(cl);
			button.setRect(curX, title.height() + 7, HeroBtn.WIDTH, HeroBtn.HEIGHT);
			curX += HeroBtn.WIDTH + heroBtnSpacing;
			add(button);
		}
		
		ColorBlock separator = new ColorBlock(1, 1, 0xFF222222);
		separator.size(WIDTH, 1);
		separator.x = 0;
		separator.y = title.bottom() + 6 + HeroBtn.HEIGHT;
		add(separator);
		
		HeroPane ava = new HeroPane();
		ava.setRect(20, separator.y + 2, WIDTH-30, 80);
		add(ava);
		
		RedButton start = new RedButton(Messages.get(this, "start")){
			@Override
			protected void onClick() {
				if (GamesInProgress.selectedClass == null) return;
				
				super.onClick();
				
				GamesInProgress.curSlot = slot;
				Dungeon.hero = null;
				ActionIndicator.action = null;
				InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
				
				if (SPDSettings.intro()) {
					SPDSettings.intro( false );
					Game.switchScene( IntroScene.class );
				} else {
					Game.switchScene( InterlevelScene.class );
				}
			}
			
			@Override
			public void update() {
				if( !visible && GamesInProgress.selectedClass != null){
					visible = true;
				}
				super.update();
			}
		};
		start.visible = false;
		start.setRect(0, HEIGHT - 20, WIDTH, 20);
		add(start);
		
		if (DeviceCompat.isDebug() || Badges.isUnlocked(Badges.Badge.VICTORY)){
			IconButton challengeButton = new IconButton(
					Icons.get( SPDSettings.challenges() > 0 ? Icons.CHALLENGE_ON :Icons.CHALLENGE_OFF)){
				@Override
				protected void onClick() {
					SatoriPixelDungeon.scene().add(new WndChallenges(SPDSettings.challenges(), true) {
						public void onBackPressed() {
							super.onBackPressed();
							icon( Icons.get( SPDSettings.challenges() > 0 ?
									Icons.CHALLENGE_ON :Icons.CHALLENGE_OFF ) );
						}
					} );
				}
				
				@Override
				public void update() {
					if( !visible && GamesInProgress.selectedClass != null){
						visible = true;
					}
					super.update();
				}
			};
			challengeButton.setRect(WIDTH - 20, HEIGHT - 20, 20, 20);
			challengeButton.visible = false;
			add(challengeButton);
			
		} else {
			Dungeon.challenges = 0;
			SPDSettings.challenges(0);
		}
		
		resize(WIDTH, HEIGHT);
		
	}
	
	private static class HeroBtn extends Button {
		
		private final HeroClass cl;
		
		private Image hero;
		
		private static final int WIDTH = 24;
		private static final int HEIGHT = 16;
		
		HeroBtn ( HeroClass cl ){
			super();
			
			this.cl = cl;
			
			if (cl == HeroClass.WARRIOR){
				hero = new Image(Assets.WARRIOR, 0, 90, 12, 15);
			} else if (cl == HeroClass.MAGE){
				hero = new Image(Assets.MAGE, 0, 90, 12, 15);
			} else if (cl == HeroClass.ROGUE){
				hero = new Image(Assets.ROGUE, 0, 90, 12, 15);
			} else if (cl == HeroClass.HUNTRESS){
				hero = new Image(Assets.HUNTRESS, 0, 90, 12, 15);
			} else if (cl == HeroClass.MAHOU_SHOUJO){
			    hero = new Image(Assets.MAHOU_SHOUJO, 0,90,12,15);
            }
			add(hero);
			
		}
		
		@Override
		protected void layout() {
			super.layout();
			if (hero != null){
				hero.x = x + (width - hero.width()) / 2f;
				hero.y = y + (height - hero.height()) / 2f;
				PixelScene.align(hero);
			}
		}
		
		@Override
		public void update() {
			super.update();
			if (cl != GamesInProgress.selectedClass){
				if (!cl.isUnlocked()){
					hero.brightness(0.3f);
				} else {
					hero.brightness(0.6f);
				}
			} else {
				hero.brightness(1f);
			}
		}
		
		@Override
		protected void onClick() {
			super.onClick();
			
			if( !cl.isUnlocked() ){
				SatoriPixelDungeon.scene().add(
						new WndMessage(cl.unlockMsg()));
			} else {
				GamesInProgress.selectedClass = cl;
			}
		}
	}
	
	private static class HeroPane extends Component {
		
		private HeroClass cl;
		
		private Image avatar;
		
		private IconButton heroItem;
		private IconButton heroLoadout;
		private IconButton heroMisc;
		private IconButton heroSubclass;
		
		private RenderedTextBlock name;
		
		private static final int BTN_SIZE = 20;
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			avatar = new Image(Assets.AVATARS);
			avatar.scale.set(2f);
			add(avatar);
			
			heroItem = new IconButton(){
				@Override
				protected void onClick() {
					if (cl == null) return;
					SatoriPixelDungeon.scene().add(new WndMessage(Messages.get(cl, cl.name() + "_desc_item")));
				}
			};
			heroItem.setSize(BTN_SIZE, BTN_SIZE);
			add(heroItem);
			
			heroLoadout = new IconButton(){
				@Override
				protected void onClick() {
					if (cl == null) return;
					SatoriPixelDungeon.scene().add(new WndMessage(Messages.get(cl, cl.name() + "_desc_loadout")));
				}
			};
			heroLoadout.setSize(BTN_SIZE, BTN_SIZE);
			add(heroLoadout);
			
			heroMisc = new IconButton(){
				@Override
				protected void onClick() {
					if (cl == null) return;
					SatoriPixelDungeon.scene().add(new WndMessage(Messages.get(cl, cl.name() + "_desc_misc")));
				}
			};
			heroMisc.setSize(BTN_SIZE, BTN_SIZE);
			add(heroMisc);
			
			heroSubclass = new IconButton(new ItemSprite(ItemSpriteSheet.MASTERY, null)){
				@Override
				protected void onClick() {
					if (cl == null) return;
					StringBuilder msg = new StringBuilder(Messages.get(cl, cl.name() + "_desc_subclasses"));
					for (HeroSubClass sub : cl.subClasses()){
						msg.append("\n\n").append(sub.desc());
					}
					SatoriPixelDungeon.scene().add(new WndMessage(msg.toString()));
				}
			};
			heroSubclass.setSize(BTN_SIZE, BTN_SIZE);
			add(heroSubclass);
			
			name = PixelScene.renderTextBlock(12);
			add(name);
			
			visible = false;
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			avatar.x = x;
			avatar.y = y + (height - avatar.height() - name.height() - 4)/2f;
			PixelScene.align(avatar);
			
			name.setPos(
					x + (avatar.width() - name.width())/2f,
					avatar.y + avatar.height() + 3
			);
			PixelScene.align(name);
			
			heroItem.setPos(x + width - BTN_SIZE, y);
			heroLoadout.setPos(x + width - BTN_SIZE, heroItem.bottom());
			heroMisc.setPos(x + width - BTN_SIZE, heroLoadout.bottom());
			heroSubclass.setPos(x + width - BTN_SIZE, heroMisc.bottom());
		}
		
		@Override
		public synchronized void update() {
			super.update();
			if (GamesInProgress.selectedClass != cl){
				cl = GamesInProgress.selectedClass;
				if (cl != null) {
					avatar.frame(cl.ordinal() * 24, 0, 24, 32);
					
					name.text(Messages.capitalize(cl.title()));
					
					switch(cl){
						case WARRIOR:
							heroItem.icon(new ItemSprite(ItemSpriteSheet.SEAL, null));
							heroLoadout.icon(new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, null));
							heroMisc.icon(new ItemSprite(ItemSpriteSheet.RATION, null));
							break;
						case MAGE:
							heroItem.icon(new ItemSprite(ItemSpriteSheet.MAGES_STAFF, null));
							heroLoadout.icon(new ItemSprite(ItemSpriteSheet.HOLDER, null));
							heroMisc.icon(new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE, null));
							break;
						case ROGUE:
							heroItem.icon(new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null));
							heroLoadout.icon(new ItemSprite(ItemSpriteSheet.DAGGER, null));
							heroMisc.icon(Icons.get(Icons.DEPTH));
							break;
						case HUNTRESS:
							heroItem.icon(new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null));
							heroLoadout.icon(new ItemSprite(ItemSpriteSheet.GLOVES, null));
							heroMisc.icon(new Image(Assets.TILES_SEWERS, 112, 96, 16, 16 ));
							break;
						case MAHOU_SHOUJO:
							heroItem.icon(new ItemSprite(ItemSpriteSheet.MAGES_STAFF,null));
							heroLoadout.icon(new ItemSprite(ItemSpriteSheet.REAGENTOFPELLOUXITE, null));
							heroMisc.icon(new ItemSprite(ItemSpriteSheet.WAND_DISINTEGRATION, null));
							break;
					}
					
					layout();
					
					visible = true;
				} else {
					visible = false;
				}
			}
		}
	}

}
