package studio.baka.satoripixeldungeon.items.weapon.enchantments;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.weapon.Weapon;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;

public class Projecting extends Weapon.Enchantment {

    private static final ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        //Does nothing as a proc, instead increases weapon range.
        //See weapon.reachFactor, and MissileWeapon.throwPos;
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PURPLE;
    }

}
