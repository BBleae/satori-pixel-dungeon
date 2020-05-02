package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Wandmaker;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.quest.CorpseDust;
import studio.baka.satoripixeldungeon.items.quest.Embers;
import studio.baka.satoripixeldungeon.items.wands.Wand;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.plants.Rotberry;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import studio.baka.satoripixeldungeon.utils.GLog;

public class WndWandmaker extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final float GAP		= 2;
	
	public WndWandmaker( final Wandmaker wandmaker, final Item item ) {
		
		super();
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Messages.titleCase(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add( titlebar );

		String msg = "";
		if (item instanceof CorpseDust){
			msg = Messages.get(this, "dust");
		} else if (item instanceof Embers){
			msg = Messages.get(this, "ember");
		} else if (item instanceof Rotberry.Seed){
			msg = Messages.get(this, "berry");
		}

		RenderedTextBlock message = PixelScene.renderTextBlock( msg, 6 );
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );
		
		RedButton btnWand1 = new RedButton( Messages.titleCase(Wandmaker.Quest.wand1.name()) ) {
			@Override
			protected void onClick() {
				selectReward( wandmaker, item, Wandmaker.Quest.wand1 );
			}
		};
		btnWand1.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
		add( btnWand1 );
		
		RedButton btnWand2 = new RedButton( Messages.titleCase(Wandmaker.Quest.wand2.name()) ) {
			@Override
			protected void onClick() {
				selectReward( wandmaker, item, Wandmaker.Quest.wand2 );
			}
		};
		btnWand2.setRect(0, btnWand1.bottom() + GAP, WIDTH, BTN_HEIGHT);
		add( btnWand2 );
		
		resize(WIDTH, (int) btnWand2.bottom());
	}
	
	private void selectReward( Wandmaker wandmaker, Item item, Wand reward ) {
		
		hide();
		
		item.detach( Dungeon.hero.belongings.backpack );

		reward.identify();
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.get(Dungeon.hero, "you_now_have", reward.name()) );
		} else {
			Dungeon.level.drop( reward, wandmaker.pos ).sprite.drop();
		}
		
		wandmaker.yell( Messages.get(this, "farewell", Dungeon.hero.givenName()) );
		wandmaker.destroy();
		
		wandmaker.sprite.die();
		
		Wandmaker.Quest.complete();
	}
}
