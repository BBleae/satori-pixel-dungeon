package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.sprites.UnknownSprite;
import com.watabou.utils.Random;

public class Unknown extends Mob {

    {
        spriteClass = UnknownSprite.class;

        HP = HT = 6;
        defenseSkill = 25;

        EXP = 3;
        maxLvl = 7;

        loot = Generator.Category.POTION;
        lootChance = 0.25f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

}
