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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal.WarriorShield;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Berserk extends Buff {

	private enum State{
		NORMAL, BERSERK, RECOVERING
	}
	private State state = State.NORMAL;

	private static final float LEVEL_RECOVER_START = 2f;
	private float levelRecovery;
	
	private int lasttime = 0;

	private static final String STATE = "state";
	private static final String LEVEL_RECOVERY = "levelrecovery";
	private static final String LAST_TIME = "lasttime";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(STATE, state);
		bundle.put(LAST_TIME, lasttime);
		if (state == State.RECOVERING) bundle.put(LEVEL_RECOVERY, levelRecovery);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		//pre-0.6.5 saves
		if (bundle.contains("exhaustion")){
			state = State.RECOVERING;
		} else {
			state = bundle.getEnum(STATE, State.class);
		}
		if (bundle.contains(LAST_TIME)){
			lasttime = bundle.getInt(LAST_TIME);
		} else {
			lasttime = 1;
		}
		if (state == State.RECOVERING) levelRecovery = bundle.getFloat(LEVEL_RECOVERY);
	}

	@Override
	public boolean act() {
		if (berserking()){
			ShieldBuff buff = target.buff(WarriorShield.class);
			if (target.HP <= 0) {
				if (buff != null && buff.shielding() > 0) {
					buff.absorbDamage(1 + (int)Math.ceil(target.shielding() * 0.1f));
				} else {
					//if there is no shield buff, or it is empty, then try to remove from other shielding buffs
					buff = target.buff(ShieldBuff.class);
					if (buff != null) buff.absorbDamage(1 + (int)Math.ceil(target.shielding() * 0.1f));
				}
				if (target.shielding() <= 0) {
					target.die(this);
					if (!target.isAlive()) Dungeon.fail(this.getClass());
				}
			} else {
				state = State.RECOVERING;
				levelRecovery = LEVEL_RECOVER_START;
				BuffIndicator.refreshHero();
				if (buff != null) buff.absorbDamage(buff.shielding());
			}
		} else if (state == State.NORMAL) {
			lasttime -= 1;
			if (lasttime <= 0){
				detach();
			}
			BuffIndicator.refreshHero();
		}
		spend(TICK);
		return true;
	}

	public int damageFactor(int dmg){
		/*
		float bonus = Math.min(1.5f, 1f + (power / 2f));
		return Math.round(dmg * bonus);
		 */
		return dmg;			//no more bonus now
	}

	public boolean berserking(){
		if (target.HP == 0 && state == State.NORMAL){

			WarriorShield shield = target.buff(WarriorShield.class);
			if (shield != null){
				state = State.BERSERK;
				BuffIndicator.refreshHero();
				shield.supercharge(shield.maxShield() * 10);

				SpellSprite.show(target, SpellSprite.BERSERK);
				Sample.INSTANCE.play( Assets.SND_CHALLENGE );
				GameScene.flash(0xFF0000);
			}

		}

		return state == State.BERSERK && target.shielding() > 0;
	}
	
	public void damage(){
		if (state == State.RECOVERING) return;
		lasttime += 2;
		BuffIndicator.refreshHero();
	}

	public void recover(float percent){
		if (levelRecovery > 0){
			levelRecovery -= percent;
			BuffIndicator.refreshHero();
			if (levelRecovery <= 0) {
				state = State.NORMAL;
				levelRecovery = 0;
			}
		}
	}

	@Override
	public int icon() {
		//return BuffIndicator.BERSERK;
		if (levelRecovery > 0 ) return BuffIndicator.BERSERK;
		return BuffIndicator.NONE;
	}
	
	@Override
	public void tintIcon(Image icon) {

	}
	
	@Override
	public String toString() {
		return Messages.get(this, "berserk");
	}

	@Override
	public String desc() {
		return Messages.get(this, "berserk_desc");
	}
}
