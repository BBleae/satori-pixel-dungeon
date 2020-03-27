/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

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
				staff.curselevel = 1;
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
				staff.curselevel = 2;
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
				staff.curselevel = 3;
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
				staff.curselevel = 4;
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
				hide();;
				staff.curselevel = 0;
				GLog.w(Messages.get(WndChooseCurseLevel.class,"clean"));
				staff.cursed = false;
				staff.cursedKnown = true;
			}
		};
		btnL0.setRect(btnL1.left(),btnL3.bottom() + GAP,btnL1.width() * 2,BTN_HEIGHT);
		add( btnL0 );
		
		RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect( btnL1.left(), btnL0.bottom() + GAP, btnL0.width(), BTN_HEIGHT );
		add( btnCancel );
		
		resize( WIDTH, (int)btnCancel.bottom() + (int)GAP );
	}
}
