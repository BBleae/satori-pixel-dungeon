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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ReagentOfPellouxite extends Item {
	
	public static final float TIME_TO_LIGHT = 1;
	
	{
		image = ItemSpriteSheet.REAGENTOFPELLOUXITE;
		
		stackable = true;
		
		defaultAction = AC_THROW;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );
	}

	@Override
	protected void onThrow( int cell ) {
		if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {

			super.onThrow( cell );

		} else  {

			Dungeon.level.pressCell( cell );
			shatter( cell );

		}
	}

	public void shatter( int cell ) {

		if (Dungeon.level.heroFOV[cell]) {
			splash( cell );
			Sample.INSTANCE.play( Assets.SND_SHATTER );
		}

		for (int offset : PathFinder.NEIGHBOURS9){
			if (Dungeon.level.water[cell+offset]) {
				Level.set(cell+offset, Terrain.EMPTY);
				DEM(cell+offset);
				//GameScene.add(Blob.seed(cell + offset, 1, Fire.class));
				GameScene.updateMap(cell+offset);
			}
		}
	}

	protected void splash( int cell ) {

		Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
		if (fire != null)
			fire.clear( cell );

		DEM(cell);
	}

	protected void DEM(int cell){
		final int color = splashColor();
		Char ch = Actor.findChar(cell);
		if (ch != null) {
			Buff.affect(ch,Burning.class).reignite(ch,8f);
			Buff.affect(ch, Blindness.class,8f);
			Splash.at( ch.sprite.center(), color, 5 );
		} else {
			Splash.at( cell, color, 5 );
		}
	}

	protected int splashColor(){
		return ItemSprite.pick( image, 5, 9 );
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int price() {
		return 10 * quantity;
	}
}
