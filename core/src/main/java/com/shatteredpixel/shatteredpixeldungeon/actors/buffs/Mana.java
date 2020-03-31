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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Mana extends Buff implements Hero.Doom {

	private static final float REGEN_TIME	= 10f;

	private float level;

	private static final String LEVEL			= "level";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( LEVEL, level );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getFloat( LEVEL );
	}

	@Override
	public boolean act() {
		if (target != Dungeon.hero) detach();

		if (Dungeon.level.locked || target.buff(WellFed.class) != null ||
				target.buff(Bless.class) != null ||
				target.buff(Recharging.class) != null){
			Hero hero = (Hero)target;
			manaregin(hero);
			spend(REGEN_TIME/2);
			return true;
		}

		if (target.isAlive() && target instanceof Hero) {
			Hero hero = (Hero)target;
			manaregin(hero);

			if (Dungeon.hero.heroClass != HeroClass.HUNTRESS){
				spend( REGEN_TIME );
			}
			else {
				spend( REGEN_TIME / 2);
			}
		}
		else {
			diactivate();
		}

		return true;
	}

	private void manaregin(Hero hero){
		int regenmana = (int)(hero.getMaxmana() * 0.1f);
		if (regenmana == 0) regenmana = 1;

		float manafactor;
		if (hero.heroClass == HeroClass.HUNTRESS) manafactor = 1.2f;
		else manafactor = 1f;

		if (hero.mana<hero.getMaxmana() && hero.mana + (int) (regenmana * manafactor) >= hero.getMaxmana()) {
			hero.setMana(hero.getMaxmana());
			GLog.w( Messages.get(this, "mana_full") );
		}
		else if (hero.mana + (int) (regenmana * manafactor) < hero.getMaxmana()){
			hero.setMana(hero.mana + (int) (regenmana * regenmana));
		}

		if (hero.mana > hero.getMaxmana())hero.setMana(hero.getMaxmana());
	}

	@Override
	public int icon() {
		return BuffIndicator.NONE;
	}

	@Override
	public String toString() {
		return Messages.get(this, "mana");
	}

	@Override
	public String desc() {
		String result;
		result = Messages.get(this, "current_mana");

		result += Messages.get(this, "desc");

		return result;
	}

	@Override
	public void onDeath() {

	}
}
