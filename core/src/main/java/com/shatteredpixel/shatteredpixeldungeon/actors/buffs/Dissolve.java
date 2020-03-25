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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PointF;

import java.text.DecimalFormat;

public class Dissolve extends FlavourBuff implements Hero.Doom{

	{
		type = buffType.NEGATIVE;
		announced = false;
	}

	@Override
	public int icon() {
		return BuffIndicator.NONE;
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}

	@Override
	public boolean act() {
		if (target.isAlive() && target == Dungeon.hero){
			if (Dungeon.level.map[target.pos] == Terrain.WATER && target.buff(Levitation.class) == null)
				if ( Dungeon.hero.heroClass == HeroClass.MAHOU_SHOUJO) {
					target.sprite.showStatus(CharSprite.NEGATIVE,this.toString());
					target.damage(1,this);
				}
			spend( TICK );
		}
		else {
			detach();
		}
		return true;
	}

	@Override
	public void onDeath() {
		Dungeon.fail(getClass());
		GLog.n(Messages.get(this, "ondeath"));
	}
}
