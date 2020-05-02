package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.sprites.SnakeSprite;
import com.watabou.utils.Random;

public class Snake extends Mob {

    {
        spriteClass = SnakeSprite.class;

        HP = HT = 4;
        defenseSkill = 25;

        EXP = 2;
        maxLvl = 7;

        loot = Generator.Category.SEED;
        lootChance = 0.25f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 10;
    }

}
