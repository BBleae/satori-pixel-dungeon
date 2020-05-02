package studio.baka.satoripixeldungeon.items.weapon.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;

public class Wayward extends Weapon.Enchantment {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        //no proc effect, see weapon.accuracyFactor for effect
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
