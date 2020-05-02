package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bleeding;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Thorns extends Armor.Glyph {

    private static final ItemSprite.Glowing RED = new ItemSprite.Glowing(0x660022);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        int level = Math.max(0, armor.level());

        // lvl 0 - 16.7%
        // lvl 1 - 28.6%
        // lvl 2 - 37.5%
        if (Random.Int(level + 6) >= 5) {

            Buff.affect(attacker, Bleeding.class).set(4 + level);

        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return RED;
    }
}
