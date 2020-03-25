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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;

public class AboutScene extends PixelScene {

	private static final String TTL_SPD = "Satori's Pixel Dungeon v1.7.3.X";

	private static final String TXT_SPD =
			"Code & Graphics: Tomnycui\n" +
					"Companion & Instructor: BBleae";

	private static final String LNK_SPD = "https://gitee.com/\nsatori-pixel-dungeon-team";

	private static final String TTL_SHPX = "Shattered Pixel Dungeon";

	private static final String TXT_SHPX = "Design, Code, & Graphics: Evan";

	
	private static final String LNK_SHPX = "ShatteredPixel.com";;
	
	@Override
	public void create() {
		super.create();

		final float colWidth = Camera.main.width / (SPDSettings.landscape() ? 2 : 1);
		final float colTop = (Camera.main.height / 2) - (SPDSettings.landscape() ? 30 : 90);
		final float wataOffset = SPDSettings.landscape() ? colWidth : 0;

		Image spd = Icons.WATA.get();
		spd.x = (colWidth - spd.width()) / 2;
		spd.y = colTop;
		align(spd);
		add( spd );

		new Flare( 7, 64 ).color( 0x225511, true ).show( spd, 0 ).angularSpeed = +20;

		RenderedTextBlock shpxtitle = renderTextBlock(TTL_SPD, 8 );
		shpxtitle.hardlight( Window.SHPX_COLOR );
		add( shpxtitle );

		shpxtitle.setPos(
				(colWidth - shpxtitle.width()) / 2,
				spd.y + spd.height + 5
		);
		align(shpxtitle);

		RenderedTextBlock shpxtext = renderTextBlock(TXT_SPD, 8 );
		shpxtext.maxWidth((int)Math.min(colWidth, 120));
		add( shpxtext );

		shpxtext.setPos((colWidth - shpxtext.width()) / 2, shpxtitle.bottom() + 12);
		align(shpxtext);

		RenderedTextBlock shpxlink = renderTextBlock(LNK_SPD, 8 );
		shpxlink.maxWidth(shpxtext.maxWidth());
		shpxlink.hardlight( Window.SHPX_COLOR );
		add( shpxlink );

		shpxlink.setPos((colWidth - shpxlink.width()) / 2, shpxtext.bottom() + 6);
		align(shpxlink);

		PointerArea shpxhotArea = new PointerArea( shpxlink.left(), shpxlink.top(), shpxlink.width(), shpxlink.height() ) {
			@Override
			protected void onClick( PointerEvent event ) {
				//DeviceCompat.openURI( "https://" + LNK_SHPX );
			}
		};
		add( shpxhotArea );

		Image shpx = Icons.SHPX.get();
		shpx.x = wataOffset + (colWidth - shpx.width()) / 2;
		shpx.y = SPDSettings.landscape() ?
						colTop:
						shpxlink.top() + shpx.height + 20;
		align(shpx);
		add( shpx );

		new Flare( 7, 64 ).color( 0x112233, true ).show( shpx, 0 ).angularSpeed = +20;

		RenderedTextBlock wataTitle = renderTextBlock(TTL_SHPX, 8 );
		wataTitle.hardlight(Window.TITLE_COLOR);
		add( wataTitle );

		wataTitle.setPos(
				wataOffset + (colWidth - wataTitle.width()) / 2,
				shpx.y + shpx.height + 11
		);
		align(wataTitle);

		RenderedTextBlock wataText = renderTextBlock(TXT_SHPX, 8 );
		wataText.maxWidth((int)Math.min(colWidth, 120));
		wataText.setHightlighting(false); //underscore in cube_code
		add( wataText );

		wataText.setPos(wataOffset + (colWidth - wataText.width()) / 2, wataTitle.bottom() + 12);
		align(wataText);
		
		RenderedTextBlock wataLink = renderTextBlock(LNK_SHPX, 8 );
		wataLink.maxWidth((int)Math.min(colWidth, 120));
		wataLink.hardlight(Window.TITLE_COLOR);
		add(wataLink);
		
		wataLink.setPos(wataOffset + (colWidth - wataLink.width()) / 2 , wataText.bottom() + 6);
		align(wataLink);
		
		PointerArea hotArea = new PointerArea( wataLink.left(), wataLink.top(), wataLink.width(), wataLink.height() ) {
			@Override
			protected void onClick( PointerEvent event ) {
				//DeviceCompat.openURI( "https://" + LNK_WATA );
			}
		};
		add( hotArea );

		
		Archs archs = new Archs();
		archs.setSize( Camera.main.width, Camera.main.height );
		addToBack( archs );

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchNoFade(TitleScene.class);
	}
}
