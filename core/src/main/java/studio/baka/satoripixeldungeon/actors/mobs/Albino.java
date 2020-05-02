package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.items.food.MysteryMeat;
import studio.baka.satoripixeldungeon.sprites.AlbinoSprite;
import com.watabou.utils.Random;

public class Albino extends Rat {

    {
        spriteClass = AlbinoSprite.class;

        HP = HT = 15;
        EXP = 2;

        loot = new MysteryMeat();
        lootChance = 1f;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Bleeding.class).set(damage);
        }

        return damage;
    }
}
