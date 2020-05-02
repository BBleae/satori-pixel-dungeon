package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Paralysis;
import studio.baka.satoripixeldungeon.sprites.SeniorSprite;
import com.watabou.utils.Random;

public class Senior extends Monk {

    {
        spriteClass = SeniorSprite.class;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 24);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(10) == 0) {
            Buff.prolong(enemy, Paralysis.class, 1.1f);
        }
        return super.attackProc(enemy, damage);
    }

}
