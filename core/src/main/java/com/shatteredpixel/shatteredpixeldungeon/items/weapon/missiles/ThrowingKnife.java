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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Noisemaker;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.RegrowthBomb;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ThrowingKnife extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.THROWING_KNIFE;
		
		bones = false;
		tier = 0;
		baseUses = 1;
	}

	@Override
	public int min(int lvl) {
		return 0;
	}
	
	@Override
	public int max(int lvl) {
		return 0;
	}

	@Override
	public boolean isUpgradable() {return false;}
	
	private Char enemy;

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_DROP);
		return actions;
	}
	
	@Override
	protected void onThrow(int cell) {
		enemy = Actor.findChar(cell);

		if (enemy != null && ! (enemy instanceof Hero) ){
            int postmp = enemy.pos;
            int postmp2 = curUser.pos;
            ScrollOfTeleportation.teleportToLocation(enemy,Dungeon.level.randomRespawnCell());
            ScrollOfTeleportation.teleportToLocation(curUser,postmp);
            ScrollOfTeleportation.teleportToLocation(enemy,postmp2);
        }
		else {
		    ScrollOfTeleportation.teleportToLocation(curUser,cell);
        }

		//new Noisemaker().explode(cell);
		//ThrowingKnife tk = new ThrowingKnife();
		this.quantity(1).collect();
		//super.onThrow(cell);
	}

	@Override
	public String info() {

		String info = desc();

		return info;
	}
	
	@Override
	public int damageRoll(Char owner) {
		return 0;
	}
}
