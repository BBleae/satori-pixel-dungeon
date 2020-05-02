package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Actor;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.effects.particles.ShadowParticle;
import studio.baka.satoripixeldungeon.items.weapon.enchantments.Grim;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.WraithSprite;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Wraith extends Mob {

    private static final float SPAWN_DELAY = 2f;

    private int level;

    {
        spriteClass = WraithSprite.class;

        HP = HT = 1;
        EXP = 0;

        maxLvl = -2;

        flying = true;

        properties.add(Property.UNDEAD);
    }

    private static final String LEVEL = "level";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
        adjustStats(level);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1 + level / 2, 2 + level);
    }

    @Override
    public int attackSkill(Char target) {
        return 10 + level;
    }

    public void adjustStats(int level) {
        this.level = level;
        defenseSkill = attackSkill(null) * 5;
        enemySeen = true;
    }

    @Override
    public boolean reset() {
        state = WANDERING;
        return true;
    }

    public static void spawnAround(int pos) {
        for (int n : PathFinder.NEIGHBOURS4) {
            int cell = pos + n;
            if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null) {
                spawnAt(cell);
            }
        }
    }

    public static Wraith spawnAt(int pos) {
        if (Dungeon.level.passable[pos] && Actor.findChar(pos) == null) {

            Wraith w = new Wraith();
            w.adjustStats(Dungeon.depth);
            w.pos = pos;
            w.state = w.HUNTING;
            GameScene.add(w, SPAWN_DELAY);

            w.sprite.alpha(0);
            w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

            w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

            return w;
        } else {
            return null;
        }
    }

    {
        immunities.add(Grim.class);
        immunities.add(Terror.class);
    }
}
