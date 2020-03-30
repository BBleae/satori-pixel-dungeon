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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BurningFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ElectricityFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FreezingFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LarvaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RottingFistSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Yog extends Mob {
	
	{
		spriteClass = YogSprite.class;
		
		HP = HT = 500;
		
		EXP = 50;
		
		state = PASSIVE;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.DEMONIC);
	}

	private boolean smode = false;
	
	public Yog() {
		super();
	}
	
	public void spawnFists() {
		RottingFist fist1 = new RottingFist();
		BurningFist fist2 = new BurningFist();
		//ElectricityFist fist3 = new ElectricityFist();
		
		do {
			fist1.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			fist2.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			//fist3.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
		} while (!Dungeon.level.passable[fist1.pos] || !Dungeon.level.passable[fist2.pos] || fist1.pos == fist2.pos );
		
		GameScene.add( fist1 );
		GameScene.add( fist2 );
		//GameScene.add( fist3 );

		notice();
	}

    public void spawnFists2() {
        ElectricityFist fist3 = new ElectricityFist();
        FreezingFist fist4 = new FreezingFist();

        do {
            fist3.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
            fist4.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
            //fist3.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
        } while (!Dungeon.level.passable[fist3.pos] || !Dungeon.level.passable[fist4.pos] || fist3.pos == fist4.pos );

        GameScene.add( fist3 );
        GameScene.add( fist4 );
    }

	@Override
	protected boolean act() {
		//heals 1 health per turn
		HP = Math.min( HT, HP+1 );
		if (HP <= HT / 2 && !smode) {
		    spawnFists2();
		    smode = true;
        }
		return super.act();
	}

	@Override
	public void damage( int dmg, Object src ) {

		HashSet<Mob> fists = new HashSet<>();

		for (Mob mob : Dungeon.level.mobs)
			if (mob instanceof RottingFist || mob instanceof BurningFist)
				fists.add( mob );

		dmg >>= fists.size();
		
		super.damage( dmg, src );

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*0.5f);

	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {

		ArrayList<Integer> spawnPoints = new ArrayList<>();
		
		for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
				spawnPoints.add( p );
			}
		}
		
		if (spawnPoints.size() > 0) {
			Larva larva = new Larva();
			larva.pos = Random.element( spawnPoints );
			
			GameScene.add( larva );
			Actor.addDelayed( new Pushing( larva, pos, larva.pos ), -1 );
		}

		for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof BurningFist || mob instanceof RottingFist || mob instanceof Larva) {
				mob.aggro( enemy );
			}
		}

		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public void beckon( int cell ) {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void die( Object cause ) {

		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (mob instanceof BurningFist || mob instanceof RottingFist || mob instanceof ElectricityFist || mob instanceof FreezingFist) {
				mob.die( cause );
			}
		}
		
		GameScene.bossSlain();
		Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();
		super.die( cause );
		
		yell( Messages.get(this, "defeated") );
	}
	
	@Override
	public void notice() {
		super.notice();
		if (!BossHealthBar.isAssigned()) {
			BossHealthBar.assignBoss(this);
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					GLog.n("\n");
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
		}
	}
	
	{
		immunities.add( Grim.class );
		immunities.add( GrimTrap.class );
		immunities.add( Terror.class );
		immunities.add( Amok.class );
		immunities.add( Charm.class );
		immunities.add( Sleep.class );
		immunities.add( Burning.class );
		immunities.add( ToxicGas.class );
		immunities.add( ScrollOfRetribution.class );
		immunities.add( ScrollOfPsionicBlast.class );
		immunities.add( Vertigo.class );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
	}

	public static class RottingFist extends Mob {
	
		private static final int REGENERATION	= 4;
		
		{
			spriteClass = RottingFistSprite.class;
			
			HP = HT = 300;
			defenseSkill = 25;
			
			EXP = 10;
			
			state = WANDERING;

			properties.add(Property.BOSS);
			properties.add(Property.DEMONIC);
			properties.add(Property.ACIDIC);
		}
		
		@Override
		public int attackSkill( Char target ) {
			return 36;
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 20, 50 );
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 15);
		}
		
		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );
			
			if (Random.Int( 3 ) == 0) {
				Buff.affect( enemy, Ooze.class ).set( 20f );
				enemy.sprite.burst( 0xFF000000, 5 );
			}
			
			return damage;
		}
		
		@Override
		public boolean act() {
			
			if (Dungeon.level.water[pos] && HP < HT) {
				sprite.emitter().burst( ShadowParticle.UP, 2 );
				HP += REGENERATION;
			}
			
			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null) lock.addTime(dmg*0.5f);
		}
		
		{
			immunities.add( Paralysis.class );
			immunities.add( Amok.class );
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Poison.class );
			immunities.add( Vertigo.class );
		}
	}
	
	public static class BurningFist extends Mob {
		
		{
			spriteClass = BurningFistSprite.class;
			
			HP = HT = 200;
			defenseSkill = 25;
			
			EXP = 10;
			
			state = WANDERING;

			properties.add(Property.BOSS);
			properties.add(Property.DEMONIC);
			properties.add(Property.FIERY);
		}
		
		@Override
		public int attackSkill( Char target ) {
			return 36;
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 26, 32 );
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 15);
		}
		
		@Override
		protected boolean canAttack( Char enemy ) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
		
		//used so resistances can differentiate between melee and magical attacks
		public static class DarkBolt{}
		
		@Override
		public boolean attack( Char enemy ) {
			
			if (!Dungeon.level.adjacent( pos, enemy.pos )) {
				spend( attackDelay() );
				
				if (hit( this, enemy, true )) {
					
					int dmg =  damageRoll();
					enemy.damage( dmg, new DarkBolt() );
					
					enemy.sprite.bloodBurstA( sprite.center(), dmg );
					enemy.sprite.flash();
					
					if (!enemy.isAlive() && enemy == Dungeon.hero) {
						Dungeon.fail( getClass() );
						GLog.n( Messages.get(Char.class, "kill", name) );
					}
					return true;
					
				} else {
					
					enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
					return false;
				}
			} else {
				return super.attack( enemy );
			}
		}
		
		@Override
		public boolean act() {
			
			for (int i=0; i < PathFinder.NEIGHBOURS9.length; i++) {
				GameScene.add( Blob.seed( pos + PathFinder.NEIGHBOURS9[i], 2, Fire.class ) );
			}
			
			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null) lock.addTime(dmg*0.5f);
		}
		
		{
			immunities.add( Amok.class );
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Vertigo.class );
		}
	}

	public static class ElectricityFist extends Mob {

		private static final int REGENERATION	= 10;

		{
			spriteClass = ElectricityFistSprite.class;

			HP = HT = 400;
			defenseSkill = 25;

			EXP = 10;

			state = WANDERING;

			properties.add(Property.BOSS);
			properties.add(Property.DEMONIC);
			properties.add(Property.ACIDIC);
		}

		@Override
		public int attackSkill( Char target ) {
			return 24;
		}

        public static class DarkBolt{}

        @Override
        public boolean attack( Char enemy ) {
		    if (enemy == Dungeon.hero) Camera.main.shake( 2, 0.3f );
		    enemy.sprite.centerEmitter().burst( SparkParticle.FACTORY, 5 );
		    enemy.sprite.flash();
            return super.attack( enemy );
        }

		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 20, 30 );
		}

		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 15);
		}

		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );

			if (Random.Int( 3 ) == 0) {
				Buff.affect( enemy, Paralysis.class, 3f);
				enemy.sprite.burst( 0xFFFF0000, 8 );
			}

			return damage;
		}

        @Override
        public void move( int step ) {
            super.move(step);
            for (int i=0; i < PathFinder.NEIGHBOURS9.length; i++) {
                GameScene.add( Blob.seed( pos + PathFinder.NEIGHBOURS9[i], 1, Electricity.class ) );
            }
        }

		@Override
		public boolean act() {

			if (Dungeon.level.water[pos] && HP-REGENERATION >= 0) {
				sprite.emitter().burst( ShadowParticle.UP, 2 );
				HP -= REGENERATION;
			}

			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null) lock.addTime(dmg*0.5f);
		}

		{
			immunities.add( Paralysis.class );
			immunities.add( Amok.class );
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Vertigo.class );
		}
	}

    public static class FreezingFist extends Mob {

        {
            spriteClass = FreezingFistSprite.class;

            HP = HT = 180;
            defenseSkill = 25;

            EXP = 10;

            state = WANDERING;

            properties.add(Property.BOSS);
            properties.add(Property.DEMONIC);
            properties.add(Property.FIERY);
        }

        @Override
        public int attackSkill( Char target ) {
            return 36;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange( 18, 28 );
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 15);
        }

        @Override
        protected boolean canAttack( Char enemy ) {
            return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }

        //used so resistances can differentiate between melee and magical attacks
        public static class DarkBolt{}

        @Override
        public boolean attack( Char enemy ) {

            if (!Dungeon.level.adjacent( pos, enemy.pos )) {
                spend( attackDelay() );

                if (hit( this, enemy, false )) {

                    int dmg =  damageRoll();
                    enemy.damage( dmg, new DarkBolt() );


                    if (enemy.buff(Frost.class) != null){
                        enemy.damage( dmg / 2 , new DarkBolt() );
                    }
                    if (enemy.buff(Chill.class) != null){
                        enemy.damage( dmg / 4 , new DarkBolt() );
                        if (Random.Int(0,3) == 1)
                            Buff.affect(enemy, Frost.class,1f);
                    }
                    else {
                        Buff.affect(enemy, Chill.class,2f);
                    }


                    enemy.sprite.burst( 0xFF99CCFF, dmg / 2 + 2 );
                    enemy.sprite.flash();

                    if (!enemy.isAlive() && enemy == Dungeon.hero) {
                        Dungeon.fail( getClass() );
                        GLog.n( Messages.get(Char.class, "kill", name) );
                    }
                    return true;

                } else {

                    enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
                    return false;
                }
            } else {
                return super.attack( enemy );
            }
        }

        @Override
        public boolean act() {

            for (int i=0; i < PathFinder.NEIGHBOURS9.length; i++) {
                GameScene.add( Blob.seed( pos + PathFinder.NEIGHBOURS9[i], 2, Freezing.class ) );
            }

            return super.act();
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg*0.5f);
        }

        {
            immunities.add( Amok.class );
            immunities.add( Sleep.class );
            immunities.add( Terror.class );
            immunities.add( Vertigo.class );
            immunities.add( Frost.class );
            immunities.add( Chill.class );
        }
    }
	
	public static class Larva extends Mob {
		
		{
			spriteClass = LarvaSprite.class;
			
			HP = HT = 25;
			defenseSkill = 20;
			
			EXP = 0;
			maxLvl = -2;
			
			state = HUNTING;

			properties.add(Property.DEMONIC);
		}
		
		@Override
		public int attackSkill( Char target ) {
			return 30;
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 22, 30 );
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 8);
		}

	}
}
