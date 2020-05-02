package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.Web;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Poison;
import studio.baka.satoripixeldungeon.actors.buffs.Terror;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.SpinnerSprite;
import com.watabou.utils.Random;

public class Spinner extends Mob {

    {
        spriteClass = SpinnerSprite.class;

        HP = HT = 50;
        defenseSkill = 14;

        EXP = 9;
        maxLvl = 17;

        loot = new MysteryMeat();
        lootChance = 0.125f;

        FLEEING = new Fleeing();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(10, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 6);
    }

    @Override
    protected boolean act() {
        boolean result = super.act();

        if (state == FLEEING && buff(Terror.class) == null &&
                enemy != null && enemySeen && enemy.buff(Poison.class) == null) {
            state = HUNTING;
        }
        return result;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Poison.class).set(Random.Int(7, 9));
            state = FLEEING;
        }

        return damage;
    }

    @Override
    public void move(int step) {
        int curWeb = Blob.volumeAt(pos, Web.class);
        if (state == FLEEING && curWeb < 5) {
            GameScene.add(Blob.seed(pos, Random.Int(5, 7) - curWeb, Web.class));
        }
        super.move(step);
    }

    {
        resistances.add(Poison.class);
    }

    {
        immunities.add(Web.class);
    }

    private class Fleeing extends Mob.Fleeing {
        @Override
        protected void nowhereToRun() {
            if (buff(Terror.class) == null) {
                state = HUNTING;
            } else {
                super.nowhereToRun();
            }
        }
    }
}
