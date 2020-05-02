package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.effects.Flare;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.ArmorKit;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.artifacts.LloydsBeacon;
import studio.baka.satoripixeldungeon.items.keys.SkeletonKey;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfTeleportation;
import studio.baka.satoripixeldungeon.items.wands.WandOfDisintegration;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.levels.CityBossLevel;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.KingSprite;
import studio.baka.satoripixeldungeon.sprites.UndeadSprite;
import studio.baka.satoripixeldungeon.ui.BossHealthBar;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class King extends Mob {

    private static final int MAX_ARMY_SIZE = 5;

    {
        spriteClass = KingSprite.class;

        HP = HT = 300;
        EXP = 40;
        defenseSkill = 25;

        Undead.count = 0;

        properties.add(Property.BOSS);
        properties.add(Property.UNDEAD);
    }

    private boolean nextPedestal = true;

    private static final String PEDESTAL = "pedestal";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PEDESTAL, nextPedestal);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        nextPedestal = bundle.getBoolean(PEDESTAL);
        BossHealthBar.assignBoss(this);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(25, 40);
    }

    @Override
    public int attackSkill(Char target) {
        return 32;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 14);
    }

    @Override
    protected boolean getCloser(int target) {
        return canTryToSummon() ?
                super.getCloser(((CityBossLevel) Dungeon.level).pedestal(nextPedestal)) :
                super.getCloser(target);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return canTryToSummon() ?
                pos == ((CityBossLevel) Dungeon.level).pedestal(nextPedestal) :
                Dungeon.level.adjacent(pos, enemy.pos);
    }

    private boolean canTryToSummon() {
        if (paralysed <= 0 && Undead.count < maxArmySize()) {
            Char ch = Actor.findChar(((CityBossLevel) Dungeon.level).pedestal(nextPedestal));
            return ch == this || ch == null;
        } else {
            return false;
        }
    }

    @Override
    protected boolean act() {
        if (HP == 0) die(Dungeon.hero);
        if (canTryToSummon() && pos == ((CityBossLevel) Dungeon.level).pedestal(nextPedestal)) {
            summon();
            return true;
        } else {
            if (enemy != null && Actor.findChar(((CityBossLevel) Dungeon.level).pedestal(nextPedestal)) == enemy) {
                nextPedestal = !nextPedestal;
            }
            return super.act();
        }
    }

    @Override
    public void damage(int dmg, Object src) {
        if (dmg >= HP) {
            //noinspection unchecked
            for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
                if (mob instanceof Undead && HT > 99) {
                    HT -= 50;
                    HP += Random.Int(HT - HP);
                    GLog.n(Messages.get(this, "regenup"));
                    mob.die(src);
                    return;
                }
            }
        }

        super.damage(dmg, src);
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) lock.addTime(dmg);
    }

    @Override
    public void die(Object cause) {

        //noinspection unchecked
        for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
            if (mob instanceof Undead && HT > 99) {
                HT -= 50;
                HP += Random.Int(HT - HP - 1) + 1;
                GLog.n(Messages.get(this, "regenup"));
                mob.die(cause);
                return;
            }
        }

        GameScene.bossSlain();
        Dungeon.level.drop(new ArmorKit(), pos).sprite.drop();
        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();

        super.die(cause);

        Badges.validateBossSlain();

        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell(Messages.get(this, "defeated", Dungeon.hero.givenName()));
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof Undead) {
                mob.aggro(ch);
            }
        }
    }

    private int maxArmySize() {
        return 1 + MAX_ARMY_SIZE * (HT - HP) / HT;
    }

    private void summon() {

        nextPedestal = !nextPedestal;

        sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
        Sample.INSTANCE.play(Assets.SND_CHALLENGE);

        boolean[] passable = Dungeon.level.passable.clone();
        for (Char c : Actor.chars()) {
            passable[c.pos] = false;
        }

        int undeadsToSummon = maxArmySize() - Undead.count;

        PathFinder.buildDistanceMap(pos, passable, undeadsToSummon);
        PathFinder.distance[pos] = Integer.MAX_VALUE;
        int dist = 1;

        undeadLabel:
        for (int i = 0; i < undeadsToSummon; i++) {
            do {
                for (int j = 0; j < Dungeon.level.length(); j++) {
                    if (PathFinder.distance[j] == dist) {

                        Undead undead = new Undead();
                        undead.pos = j;
                        GameScene.add(undead);

                        ScrollOfTeleportation.appear(undead, j);
                        new Flare(3, 32).color(0x000000, false).show(undead.sprite, 2f);

                        PathFinder.distance[j] = Integer.MAX_VALUE;

                        continue undeadLabel;
                    }
                }
                dist++;
            } while (dist < undeadsToSummon);
        }

        yell(Messages.get(this, "arise"));
        spend(TICK);
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
        resistances.add(WandOfDisintegration.class);
        resistances.add(ToxicGas.class);
        resistances.add(Burning.class);
    }

    {
        immunities.add(Paralysis.class);
        immunities.add(Vertigo.class);
        immunities.add(Blindness.class);
        immunities.add(Terror.class);
    }

    public static class Undead extends Mob {

        public static int count = 0;

        {
            spriteClass = UndeadSprite.class;

            HP = HT = 28;
            defenseSkill = 15;

            maxLvl = -2;
            EXP = 0;

            state = WANDERING;

            properties.add(Property.UNDEAD);
            properties.add(Property.INORGANIC);
        }

        @Override
        protected void onAdd() {
            count++;
            super.onAdd();
        }

        @Override
        protected void onRemove() {
            count--;
            super.onRemove();
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(15, 25);
        }

        @Override
        public int attackSkill(Char target) {
            return 16;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);
            if (Random.Int(MAX_ARMY_SIZE) == 0) {
                Buff.prolong(enemy, Paralysis.class, 1);
            }

            return damage;
        }

        @Override
        public void damage(int dmg, Object src) {
            super.damage(dmg, src);
            if (src instanceof ToxicGas) {
                ((ToxicGas) src).clear(pos);
            }
        }

        @Override
        public void die(Object cause) {
            super.die(cause);

            if (Dungeon.level.heroFOV[pos]) {
                Sample.INSTANCE.play(Assets.SND_BONES);
            }
        }

        @Override
        public int drRoll() {
            return Random.NormalIntRange(0, 5);
        }

        {
            immunities.add(Grim.class);
            immunities.add(Paralysis.class);
        }
    }
}
