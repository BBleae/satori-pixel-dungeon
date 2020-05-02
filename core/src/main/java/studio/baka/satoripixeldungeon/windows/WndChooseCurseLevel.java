package studio.baka.satoripixeldungeon.windows;

import studio.baka.satoripixeldungeon.items.weapon.melee.MagesStaff;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.PixelScene;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.ui.RedButton;
import studio.baka.satoripixeldungeon.ui.RenderedTextBlock;
import studio.baka.satoripixeldungeon.ui.Window;
import studio.baka.satoripixeldungeon.utils.GLog;

public class WndChooseCurseLevel extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 18;
	private static final float GAP		= 2;

	public WndChooseCurseLevel(MagesStaff staff, final int currentcurselevel ) {
		
		super();

		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( staff.image(), null ) );
		titlebar.label( staff.name() );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );

		RenderedTextBlock hl = PixelScene.renderTextBlock( 6 );
		hl.text( Messages.get(this, "message"), WIDTH );
		hl.setPos( titlebar.left(), titlebar.bottom() + GAP );
		add( hl );
		
		RedButton btnL1 = new RedButton( "Level 1" ) {
			@Override
			protected void onClick() {
				hide();
				staff.curseLevel = 1;
				GLog.w(Messages.get(WndChooseCurseLevel.class,"complete",1));
				staff.cursed = true;
			}
		};
		btnL1.setRect( 0, hl.bottom() + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btnL1 );
		
		RedButton btnL2 = new RedButton( "Level 2" ) {
			@Override
			protected void onClick() {
				hide();
				staff.curseLevel = 2;
				GLog.w(Messages.get(WndChooseCurseLevel.class,"complete",2));
				staff.cursed = true;
				staff.cursedKnown = true;
			}
		};
		btnL2.setRect( btnL1.right() + GAP,  btnL1.top(), btnL1.width(), BTN_HEIGHT );
		add( btnL2 );

		RedButton btnL3 = new RedButton( "Level 3" ) {
			@Override
			protected void onClick() {
				hide();
				staff.curseLevel = 3;
				GLog.w(Messages.get(WndChooseCurseLevel.class,"complete",3));
				staff.cursed = true;
				staff.cursedKnown = true;
			}
		};
		btnL3.setRect( btnL1.left(), btnL1.bottom() + GAP, btnL1.width(), BTN_HEIGHT );
		add( btnL3 );

		RedButton btnL4 = new RedButton( "Level 4" ) {
			@Override
			protected void onClick() {
				hide();
				staff.curseLevel = 4;
				GLog.w(Messages.get(WndChooseCurseLevel.class,"complete",4));
				staff.cursed = true;
				staff.cursedKnown = true;
			}
		};
		btnL4.setRect( btnL3.right() + GAP, btnL3.top(), btnL1.width(), BTN_HEIGHT );
		add( btnL4 );

		RedButton btnL0 = new RedButton("clean") {
			@Override
			protected void onClick() {
				hide();
				staff.curseLevel = 0;
				GLog.w(Messages.get(WndChooseCurseLevel.class,"clean"));
				staff.cursed = false;
				staff.cursedKnown = true;
			}
		};
		btnL0.setRect(btnL1.left(),btnL3.bottom() + GAP,WIDTH,BTN_HEIGHT);
		add( btnL0 );
		
		RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect( btnL1.left(), btnL0.bottom() + GAP, WIDTH, BTN_HEIGHT );
		add( btnCancel );
		
		resize( WIDTH, (int)btnCancel.bottom() + (int)GAP );
	}
}
