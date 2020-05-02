package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.sprites.CrabSprite;
import com.watabou.utils.Random;

public class Crab extends Mob {

    {
        spriteClass = CrabSprite.class;

        HP = HT = 15;
        defenseSkill = 5;
        baseSpeed = 2f;

        EXP = 4;
        maxLvl = 9;

        loot = new MysteryMeat();
        lootChance = 0.167f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 7);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }
}
