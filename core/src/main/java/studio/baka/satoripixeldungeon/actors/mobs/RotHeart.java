package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.ToxicGas;
import studio.baka.satoripixeldungeon.actors.buffs.*;
import studio.baka.satoripixeldungeon.plants.Rotberry;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.RotHeartSprite;
import com.watabou.utils.Random;

public class RotHeart extends Mob {

    {
        spriteClass = RotHeartSprite.class;

        HP = HT = 80;
        defenseSkill = 0;

        EXP = 4;

        state = PASSIVE;

        properties.add(Property.IMMOVABLE);
        properties.add(Property.MINIBOSS);
    }

    @Override
    public void damage(int dmg, Object src) {
        //TODO: when effect properties are done, change this to FIRE
        if (src instanceof Burning) {
            destroy();
            sprite.die();
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        GameScene.add(Blob.seed(pos, 20, ToxicGas.class));

        return super.defenseProc(enemy, damage);
    }

    @Override
    public void beckon(int cell) {
        //do nothing
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob instanceof RotLasher) {
                mob.die(null);
            }
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Dungeon.level.drop(new Rotberry.Seed(), pos).sprite.drop();
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public int damageRoll() {
        return 0;
    }

    @Override
    public int attackSkill(Char target) {
        return 0;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    {
        immunities.add(Paralysis.class);
        immunities.add(Amok.class);
        immunities.add(Sleep.class);
        immunities.add(ToxicGas.class);
        immunities.add(Terror.class);
        immunities.add(Vertigo.class);
    }

}
