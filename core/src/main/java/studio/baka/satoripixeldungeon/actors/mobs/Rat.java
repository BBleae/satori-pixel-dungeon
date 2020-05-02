package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.sprites.RatSprite;
import com.watabou.utils.Random;

public class Rat extends Mob {

    {
        spriteClass = RatSprite.class;

        HP = HT = 8;
        defenseSkill = 2;

        maxLvl = 5;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 8;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 1);
    }
}
