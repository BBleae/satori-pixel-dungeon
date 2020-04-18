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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ThreeDirectionsword extends MeleeWeapon {

	{
		image = ItemSpriteSheet.THREEDIRWPN;

		tier=5;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage ) {
		affected.clear();
		if(!(this.enchantment instanceof Projecting)) find3dir(attacker, defender, 2);
		else findalldir(attacker, defender, damage);
		affected.remove(defender); //or defender would hurt twice
		for (Char ch : affected) {
			ch.damage(damage, this);
		}
		return super.proc(attacker, defender, damage);
	}

	private ArrayList<Char> affected = new ArrayList<>();

	private void find3dir(Char attacker, Char defender, int dist ) {
		PathFinder.buildDistanceMap( attacker.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char n = Actor.findChar(i);
				if( n != null && isnear8(attacker,n) && isnear4(defender,n) )
					affected.add(n);
			}
		}
	}

	private void findalldir(Char attacker, Char defender, int dist ) {
		PathFinder.buildDistanceMap( attacker.pos, BArray.not( Dungeon.level.solid, null ), dist );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				Char n = Actor.findChar(i);
				if( n != null && isnear8(attacker,n))
					affected.add(n);
			}
		}
	}

	private boolean isnear8(Char from, Char target){
		for (int the_dir_pos : PathFinder.NEIGHBOURS8) {
			if(target.pos == from.pos + the_dir_pos)
				return true;
		}
		return false;
	}

	private boolean isnear4(Char from, Char target){
		for (int the_dir_pos : PathFinder.NEIGHBOURS4) {
			if(target.pos == from.pos + the_dir_pos)
				return true;
		}
		return false;
	}
}
