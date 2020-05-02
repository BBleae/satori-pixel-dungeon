package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Badges;
import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.Statistics;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Vertigo;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.sprites.PiranhaSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Piranha extends Mob {

    {
        spriteClass = PiranhaSprite.class;

        baseSpeed = 2f;

        EXP = 0;

        loot = MysteryMeat.class;
        lootChance = 1f;

        SLEEPING = new Sleeping();
        WANDERING = new Wandering();
        HUNTING = new Hunting();

        state = SLEEPING;

        properties.add(Property.BLOB_IMMUNE);
    }

    public Piranha() {
        super();

        HP = HT = 10 + Dungeon.depth * 5;
        defenseSkill = 10 + Dungeon.depth * 2;
    }

    @Override
    protected boolean act() {

        if (!Dungeon.level.water[pos]) {
            die(null);
            sprite.killAndErase();
            return true;
        } else {
            return super.act();
        }
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2);
    }

    @Override
    public int attackSkill(Char target) {
        return 20 + Dungeon.depth * 2;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, Dungeon.depth);
    }

    @Override
    public int defenseSkill(Char enemy) {
        enemySeen = state != SLEEPING
                && this.enemy != null
                && fieldOfView != null
                && fieldOfView[this.enemy.pos]
                && this.enemy.invisible == 0;
        return super.defenseSkill(enemy);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Statistics.piranhasKilled++;
        Badges.validatePiranhasKilled();
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected boolean getCloser(int target) {

        if (rooted) {
            return false;
        }

        int step = Dungeon.findStep(this, pos, target,
                Dungeon.level.water,
                fieldOfView);
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean getFurther(int target) {
        int step = Dungeon.flee(this, pos, target,
                Dungeon.level.water,
                fieldOfView);
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    {
        immunities.add(Burning.class);
        immunities.add(Vertigo.class);
    }

    //if there is not a path to the enemy, piranhas act as if they can't see them
    private class Sleeping extends Mob.Sleeping {
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.water, viewDistance);
                enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
            }

            return super.act(enemyInFOV, justAlerted);
        }
    }

    private class Wandering extends Mob.Wandering {
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.water, viewDistance);
                enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
            }

            return super.act(enemyInFOV, justAlerted);
        }
    }

    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(enemy.pos, Dungeon.level.water, viewDistance);
                enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
            }

            return super.act(enemyInFOV, justAlerted);
        }
    }
}
