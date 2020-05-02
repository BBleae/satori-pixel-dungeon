package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;

public class Flow extends Armor.Glyph {

    private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing(0x0000FF);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        //no proc effect, see armor.speedfactor for effect.
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLUE;
    }

}
