package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Assets;
import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.LockedFloor;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.TomeOfMastery;
import studio.baka.satoripixeldungeon.items.artifacts.DriedRose;
import studio.baka.satoripixeldungeon.items.artifacts.LloydsBeacon;
import studio.baka.satoripixeldungeon.items.scrolls.ScrollOfMagicMapping;
import studio.baka.satoripixeldungeon.levels.Level;
import studio.baka.satoripixeldungeon.levels.OldPrisonBossLevel;
import studio.baka.satoripixeldungeon.levels.Terrain;
import studio.baka.satoripixeldungeon.levels.traps.GrippingTrap;
import studio.baka.satoripixeldungeon.mechanics.Ballistica;
import studio.baka.satoripixeldungeon.messages.Messages;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.TenguSprite;
import studio.baka.satoripixeldungeon.ui.BossHealthBar;
import studio.baka.satoripixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

//Exists to support pre-0.7.5 saves
public class OldTengu extends Mob {

    {
        spriteClass = TenguSprite.class;

        HP = HT = 120;
        EXP = 20;
        defenseSkill = 20;

        HUNTING = new Hunting();

        flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

        properties.add(Property.BOSS);
    }

    @Override
    protected void onAdd() {
        //when he's removed and re-added to the fight, his time is always set to now.
        spend(-cooldown());
        super.onAdd();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 20);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    @Override
    public void damage(int dmg, Object src) {

        OldPrisonBossLevel.State state = ((OldPrisonBossLevel) Dungeon.level).state();

        int hpBracket;
        if (state == OldPrisonBossLevel.State.FIGHT_START) {
            hpBracket = 12;
        } else {
            hpBracket = 20;
        }

        int beforeHitHP = HP;
        super.damage(dmg, src);
        dmg = beforeHitHP - HP;

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) {
            int multiple = state == OldPrisonBossLevel.State.FIGHT_START ? 1 : 4;
            lock.addTime(dmg * multiple);
        }

        //phase 2 of the fight is over
        if (HP == 0 && state == OldPrisonBossLevel.State.FIGHT_ARENA) {
            //let full attack action complete first
            Actor.add(new Actor() {

                {
                    actPriority = VFX_PRIO;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    ((OldPrisonBossLevel) Dungeon.level).progress();
                    return true;
                }
            });
            return;
        }

        //phase 1 of the fight is over
        if (state == OldPrisonBossLevel.State.FIGHT_START && HP <= HT / 2) {
            HP = (HT / 2) - 1;
            yell(Messages.get(this, "interesting"));
            ((OldPrisonBossLevel) Dungeon.level).progress();
            BossHealthBar.bleed(true);

            //if tengu has lost a certain amount of hp, jump
        } else if (beforeHitHP / hpBracket != HP / hpBracket) {
            jump();
        }
    }

    @Override
    public boolean isAlive() {
        return Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
    }

    @Override
    public void die(Object cause) {

        if (Dungeon.hero.subClass == HeroSubClass.NONE) {
            Dungeon.level.drop(new TomeOfMastery(), pos).sprite.drop();
        }

        GameScene.bossSlain();
        super.die(cause);

        Badges.validateBossSlain();

        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell(Messages.get(this, "defeated"));
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
    }

    //tengu's attack is always visible
    @Override
    protected boolean doAttack(Char enemy) {
        sprite.attack(enemy.pos);
        spend(attackDelay());
        return false;
    }

    private void jump() {

        Level level = Dungeon.level;

        //incase tengu hasn't had a chance to act yet
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
            fieldOfView = new boolean[Dungeon.level.length()];
            Dungeon.level.updateFieldOfView(this, fieldOfView);
        }

        if (enemy == null) enemy = chooseEnemy();
        if (enemy == null) return;

        int newPos;
        //if we're in phase 1, want to warp around within the room
        if (((OldPrisonBossLevel) Dungeon.level).state() == OldPrisonBossLevel.State.FIGHT_START) {

            //place new traps
            int tries;
            for (int i = 0; i < 4; i++) {
                int trapPos;
                tries = 15;
                do {
                    trapPos = Random.Int(level.length());
                } while (tries-- > 0 && level.map[trapPos] != Terrain.INACTIVE_TRAP
                        && level.map[trapPos] != Terrain.TRAP);

                if (level.map[trapPos] == Terrain.INACTIVE_TRAP) {
                    level.setTrap(new GrippingTrap().reveal(), trapPos);
                    Level.set(trapPos, Terrain.TRAP);
                    ScrollOfMagicMapping.discover(trapPos);
                }
            }

            tries = 50;
            do {
                newPos = Random.IntRange(3, 7) + 32 * Random.IntRange(26, 30);
            } while ((level.adjacent(newPos, enemy.pos) || Actor.findChar(newPos) != null)
                    && --tries > 0);
            if (tries <= 0) return;

            //otherwise go wherever, as long as it's a little bit away
        } else {
            do {
                newPos = Random.Int(level.length());
            } while (
                    level.solid[newPos] ||
                            level.distance(newPos, enemy.pos) < 8 ||
                            Actor.findChar(newPos) != null);
        }

        if (level.heroFOV[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);

        sprite.move(pos, newPos);
        move(newPos);

        if (level.heroFOV[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
        Sample.INSTANCE.play(Assets.SND_PUFF);

        spend(1 / speed());
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            if (HP <= HT / 2) BossHealthBar.bleed(true);
            if (HP == HT) {
                yell(Messages.get(this, "notice_mine", Dungeon.hero.givenName()));
                for (Char ch : Actor.chars()) {
                    if (ch instanceof DriedRose.GhostHero) {
                        GLog.n("\n");
                        ((DriedRose.GhostHero) ch).sayBoss();
                    }
                }
            } else {
                yell(Messages.get(this, "notice_face", Dungeon.hero.givenName()));
            }
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
        if (HP <= HT / 2) BossHealthBar.bleed(true);
    }

    //tengu is always hunting
    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                return doAttack(enemy);

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else {
                    chooseEnemy();
                    if (enemy != null) {
                        target = enemy.pos;
                    }
                }

                spend(TICK);
                return true;

            }
        }
    }
}
