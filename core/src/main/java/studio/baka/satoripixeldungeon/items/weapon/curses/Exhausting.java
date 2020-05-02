package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Weakness;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Exhausting extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        if (attacker == Dungeon.hero && Random.Int(15) == 0) {
            Buff.affect(attacker, Weakness.class, Random.NormalIntRange(5, 20));
        }

        return damage;
    }

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }
}
