package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.*;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.effects.Pushing;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.effects.particles.SparkParticle;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.keys.SkeletonKey;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfRetribution;
import studio.baka.satoripixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.levels.traps.GrimTrap;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.*;
import studio.baka.satoripixeldungeon.ui.BossHealthBar;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Yog extends Mob {

    {
        spriteClass = YogSprite.class;

        HP = HT = 100;

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
            fist1.pos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            fist2.pos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            //fist3.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
        } while (!Dungeon.level.passable[fist1.pos] || !Dungeon.level.passable[fist2.pos] || fist1.pos == fist2.pos);

        GameScene.add(fist1);
        GameScene.add(fist2);
        //GameScene.add( fist3 );

        notice();
    }

    public void spawnFists2() {
        ElectricityFist fist3 = new ElectricityFist();
        FreezingFist fist4 = new FreezingFist();

        do {
            fist3.pos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            fist4.pos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
            //fist3.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
        } while (!Dungeon.level.passable[fist3.pos] || !Dungeon.level.passable[fist4.pos] || fist3.pos == fist4.pos);
        GLog.n(Messages.get(this, "morefist"));
        GameScene.add(fist3, 1);
        GameScene.add(fist4, 1);
    }

    private int HEALTHREGEN = 2;

    @Override
    protected boolean act() {
        //heals 1 health per turn
        HP = Math.min(HT, HP + HEALTHREGEN);
        if (HP <= HT / 2 && !smode) {
            spawnFists2();
            smode = true;
        }
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {

        HashSet<Mob> fists = new HashSet<>();

        for (Mob mob : Dungeon.level.mobs)
            if (mob instanceof BurningFist || mob instanceof RottingFist || mob instanceof ElectricityFist || mob instanceof FreezingFist)
                fists.add(mob);
        float tmp = dmg;

        for (int i = 1; i <= fists.size(); i++) {
            tmp *= 1.1f;
        }

        dmg = (int) tmp;

        super.damage(dmg, src);

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) lock.addTime(dmg * 0.5f);

    }

    @Override
    public int defenseProcess(Char enemy, int damage) {

        if (damage > HT / 10) {
            //HEALTHREGEN += 1;
            damage = HT / 10;
        }

        ArrayList<Integer> spawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                spawnPoints.add(p);
            }
        }

        if (spawnPoints.size() > 0) {
            Larva larva = new Larva();
            //noinspection ConstantConditions
            larva.pos = Random.element(spawnPoints);
            Buff.affect(larva, Adrenaline.class, 20f);
            Buff.affect(larva, MagicImmune.class, 5f);
            Buff.affect(larva, Barkskin.class).set(20, 1);
            GameScene.add(larva);
            Actor.addDelayed(new Pushing(larva, pos, larva.pos), -1);
        }

        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof BurningFist || mob instanceof RottingFist || mob instanceof Larva) {
                mob.aggro(enemy);
            }
        }

        return super.defenseProcess(enemy, damage);
    }

    @Override
    public void beckon(int cell) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void die(Object cause) {
        if (!smode) {
            HT += 200;
            HP = HT;

        }

        for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
            if (mob instanceof BurningFist || mob instanceof RottingFist || mob instanceof ElectricityFist || mob instanceof FreezingFist) {
                HEALTHREGEN += 2;
                HT += 100;
                HP += Random.Int(HT - HP);
                GLog.n(Messages.get(this, "regenup"));
                mob.die(cause);
                return;
            }
        }

        GameScene.bossSlain();
        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
        super.die(cause);

        yell(Messages.get(this, "defeated"));
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            yell(Messages.get(this, "notice"));
            for (Char ch : Actor.chars()) {
                if (ch instanceof DriedRose.GhostHero) {
                    GLog.n("\n");
                    ((DriedRose.GhostHero) ch).sayBoss();
                }
            }
        }
    }

    {
        immunities.add(Grim.class);
        immunities.add(GrimTrap.class);
        immunities.add(Terror.class);
        immunities.add(Amok.class);
        immunities.add(Charm.class);
        immunities.add(Sleep.class);
        immunities.add(Burning.class);
        immunities.add(ToxicGas.class);
        immunities.add(ScrollOfRetribution.class);
        immunities.add(ScrollOfPsionicBlast.class);
        immunities.add(Vertigo.class);
        immunities.add(Frost.class);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
    }

    public static class RottingFist extends Mob {

        private static final int REGENERATION = 4;

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
        public int attackSkill(Char target) {
            return 36;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(20, 50);
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 15);
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);

            if (Random.Int(3) == 0) {
                Buff.affect(enemy, Ooze.class).set(20f);
                enemy.sprite.burst(0xFF000000, 5);
            }

            return damage;
        }

        @Override
        public boolean act() {

            if (Dungeon.level.water[pos] && HP < HT) {
                sprite.emitter().burst(ShadowParticle.UP, 2);
                HP += REGENERATION;
            }

            return super.act();
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg * 0.5f);
        }

        {
            immunities.add(Paralysis.class);
            immunities.add(Amok.class);
            immunities.add(Sleep.class);
            immunities.add(Terror.class);
            immunities.add(Poison.class);
            immunities.add(Vertigo.class);
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
        public int attackSkill(Char target) {
            return 36;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(26, 32);
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 15);
        }

        @Override
        protected boolean canAttack(Char enemy) {
            return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }

        //used so resistances can differentiate between melee and magical attacks
        public static class DarkBolt {
        }

        @Override
        public boolean attack(Char enemy) {

            if (!Dungeon.level.adjacent(pos, enemy.pos)) {
                spend(attackDelay());

                if (hit(this, enemy, true)) {

                    int dmg = damageRoll();
                    enemy.damage(dmg, new DarkBolt());

                    enemy.sprite.bloodBurstA(sprite.center(), dmg);
                    enemy.sprite.flash();

                    if (!enemy.isAlive() && enemy == Dungeon.hero) {
                        Dungeon.fail(getClass());
                        GLog.n(Messages.get(Char.class, "kill", name));
                    }
                    return true;

                } else {

                    enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
                    return false;
                }
            } else {
                return super.attack(enemy);
            }
        }

        @Override
        public boolean act() {

            for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                GameScene.add(Blob.seed(pos + PathFinder.NEIGHBOURS9[i], 2, Fire.class));
            }

            return super.act();
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg * 0.5f);
        }

        {
            immunities.add(Amok.class);
            immunities.add(Sleep.class);
            immunities.add(Terror.class);
            immunities.add(Vertigo.class);
        }
    }

    public static class ElectricityFist extends Mob {

        private static final int REGENERATION = 10;

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
        public int attackSkill(Char target) {
            return 24;
        }

        public static class DarkBolt {
        }

        @Override
        public boolean attack(Char enemy) {
            if (enemy == Dungeon.hero) Camera.main.shake(2, 0.3f);
            enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 5);
            enemy.sprite.flash();
            return super.attack(enemy);
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(20, 30);
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 15);
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);

            if (Random.Int(3) == 0) {
                Buff.affect(enemy, Paralysis.class, 3f);
                enemy.sprite.burst(0xFFFF0000, 8);
            }

            return damage;
        }

        @Override
        public void move(int step) {
            super.move(step);
            for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                GameScene.add(Blob.seed(pos + PathFinder.NEIGHBOURS9[i], 1, Electricity.class));
            }
        }

        @Override
        public boolean act() {

            if (Dungeon.level.water[pos] && HP - REGENERATION >= 0) {
                sprite.emitter().burst(ShadowParticle.UP, 2);
                HP -= REGENERATION;
            }

            return super.act();
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg * 0.5f);
        }

        {
            immunities.add(Paralysis.class);
            immunities.add(Amok.class);
            immunities.add(Sleep.class);
            immunities.add(Terror.class);
            immunities.add(Vertigo.class);
            immunities.add(Chill.class);
            immunities.add(Frost.class);
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
        public int attackSkill(Char target) {
            return 36;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(18, 28);
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 15);
        }

        @Override
        protected boolean canAttack(Char enemy) {
            return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }

        //used so resistances can differentiate between melee and magical attacks
        static class DarkBolt {
        }

        @Override
        public boolean attack(Char enemy) {

            if (!Dungeon.level.adjacent(pos, enemy.pos)) {
                spend(attackDelay());

                if (hit(this, enemy, false)) {

                    int dmg = damageRoll();
                    enemy.damage(dmg, new DarkBolt());


                    if (enemy.buff(Frost.class) != null) {
                        enemy.damage(dmg / 2, new DarkBolt());
                    }
                    if (enemy.buff(Chill.class) != null) {
                        enemy.damage(dmg / 4, new DarkBolt());
                        if (Random.Int(0, 3) == 1)
                            Buff.affect(enemy, Frost.class, 1f);
                    } else {
                        Buff.affect(enemy, Chill.class, 2f);
                    }


                    enemy.sprite.burst(0xFF99CCFF, dmg / 2 + 2);
                    enemy.sprite.flash();

                    if (!enemy.isAlive() && enemy == Dungeon.hero) {
                        Dungeon.fail(getClass());
                        GLog.n(Messages.get(Char.class, "kill", name));
                    }
                    return true;

                } else {

                    enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
                    return false;
                }
            } else {
                return super.attack(enemy);
            }
        }

        @Override
        public boolean act() {

            for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                GameScene.add(Blob.seed(pos + PathFinder.NEIGHBOURS9[i], 2, Freezing.class));
            }

            return super.act();
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) lock.addTime(dmg * 0.5f);
        }

        {
            immunities.add(Amok.class);
            immunities.add(Sleep.class);
            immunities.add(Terror.class);
            immunities.add(Vertigo.class);
            immunities.add(Frost.class);
            immunities.add(Chill.class);
            immunities.add(Paralysis.class);
        }
    }

    public static class Larva extends Mob {

        {
            spriteClass = LarvaSprite.class;

            HP = HT = 10;
            defenseSkill = 20;

            EXP = 0;
            maxLvl = -2;

            state = HUNTING;

            properties.add(Property.DEMONIC);
        }

        @Override
        public int attackSkill(Char target) {
            return 30;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(22, 30);
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 8);
        }

    }
}
