package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.Gold;
import studio.baka.satoripixeldungeon.items.Item;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Lucky extends Weapon.Enchantment {

    private static final ItemSprite.Glowing GREEN = new ItemSprite.Glowing(0x00FF00);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        int level = Math.max(0, weapon.level());

        // lvl 0 - 10%
        // lvl 1 ~ 12%
        // lvl 2 ~ 14%
        if (defender.HP <= damage
                && Random.Int(level + 40) >= 36) {
            Buff.affect(defender, LuckProc.class);
        }

        return damage;

    }

    public static Item genLoot() {
        float roll = Random.Float();
        if (roll < 0.6f) {
            Item result = new Gold().random();
            result.quantity(Math.round(result.quantity() * 0.5f));
            return result;
        } else if (roll < 0.9f) {
            return Random.Int(2) == 0
                    ? Generator.random(Generator.Category.SEED)
                    : Generator.random(Generator.Category.STONE);
        } else {
            return Random.Int(2) == 0
                    ? Generator.random(Generator.Category.POTION)
                    : Generator.random(Generator.Category.SCROLL);
        }
    }

    @Override
    public Glowing glowing() {
        return GREEN;
    }

    //used to keep track of whether a luck proc is incoming. see Mob.die()
    public static class LuckProc extends Buff {

        @Override
        public boolean act() {
            detach();
            return true;
        }
    }

}
