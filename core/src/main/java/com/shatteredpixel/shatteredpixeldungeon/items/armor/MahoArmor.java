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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.HashMap;

public class MahoArmor extends ClassArmor {

	
	{
		image = ItemSpriteSheet.ARMOR_MAHO;
	}
	
	private HashMap<Callback, Mob> targets = new HashMap<>();
	
	@Override
	public void doSpecial() {
		if (curUser.mana >= 20) {
			curUser.mana -= 20;

			int healcount = 0;

			for (Mob mob : Dungeon.level.mobs) {
				if (Dungeon.level.distance(curUser.pos, mob.pos) <= 12
					&& Dungeon.level.heroFOV[mob.pos]
					&& mob.alignment != Char.Alignment.ALLY) {
					Buff.affect(mob, Bleeding.class).set(curUser.lvl/5);
					mob.damage(curUser.lvl,this);
					healcount++;
				}
			}
		
			if (targets.size() == 0) {
				GLog.w( Messages.get(this, "no_enemies") );
				return;
			}

			Buff.affect(curUser, Healing.class).setHeal((int)(healcount*(curUser.HT/4f)),1/(healcount != 0? healcount:1f),0);

			curUser.spend(Actor.TICK);
			curUser.sprite.zap(curUser.pos);
			curUser.busy();
		}
		else{
			GLog.w(Messages.get(this,"not_enough_mana"),curUser.mana,curUser.getMaxmana(),20);
		}
	}

}