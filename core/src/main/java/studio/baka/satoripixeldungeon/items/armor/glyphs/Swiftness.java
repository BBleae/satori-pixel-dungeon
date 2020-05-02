package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;

public class Swiftness extends Armor.Glyph {

    private static final ItemSprite.Glowing YELLOW = new ItemSprite.Glowing(0xFFFF00);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        //no proc effect, see armor.speedfactor for effect.
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return YELLOW;
    }

}
