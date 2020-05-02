package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.sprites.ShieldedSprite;
import com.watabou.utils.Random;

public class Shielded extends Brute {

    {
        spriteClass = ShieldedSprite.class;

        defenseSkill = 20;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

}
