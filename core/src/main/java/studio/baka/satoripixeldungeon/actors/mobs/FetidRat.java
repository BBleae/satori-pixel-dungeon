package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Blob;
import studio.baka.satoripixeldungeon.actors.blobs.StenchGas;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Ooze;
import studio.baka.satoripixeldungeon.actors.mobs.npcs.Ghost;
import studio.baka.satoripixeldungeon.scenes.GameScene;
import studio.baka.satoripixeldungeon.sprites.FetidRatSprite;
import com.watabou.utils.Random;

public class FetidRat extends Rat {

    {
        spriteClass = FetidRatSprite.class;

        HP = HT = 20;
        defenseSkill = 5;

        EXP = 4;

        state = WANDERING;

        properties.add(Property.MINIBOSS);
        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Ooze.class).set(20f);
        }

        return damage;
    }

    @Override
    public int defenseProcess(Char enemy, int damage) {

        GameScene.add(Blob.seed(pos, 20, StenchGas.class));

        return super.defenseProcess(enemy, damage);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Ghost.Quest.process();
    }

    {
        immunities.add(StenchGas.class);
    }
}