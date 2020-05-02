package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Polarized extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {

        if (Random.Int(2) == 0) {
            return Math.round(1.5f * damage);
        } else {
            return 0;
        }

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
