package studio.baka.satoripixeldungeon.actors.mobs;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.buffs.Poison;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.sprites.BanditSprite;
import com.watabou.utils.Random;

public class Bandit extends Thief {

    public Item item;

    {
        spriteClass = BanditSprite.class;

        //1 in 50 chance to be a crazy bandit, equates to overall 1/100 chance.
        lootChance = 0.5f;
    }

    @Override
    protected boolean steal(Hero hero) {
        if (super.steal(hero)) {

            Buff.prolong(hero, Blindness.class, Random.Int(2, 5));
            Buff.affect(hero, Poison.class).set(Random.Int(5, 7));
            Buff.prolong(hero, Cripple.class, Random.Int(3, 8));
            Dungeon.observe();

            return true;
        } else {
            return false;
        }
    }

}
