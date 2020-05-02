package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;

public class Obfuscation extends Armor.Glyph {

    private static final ItemSprite.Glowing GREY = new ItemSprite.Glowing(0x888888);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {
        //no proc effect, see armor.stealthfactor for effect.
        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return GREY;
    }

}
