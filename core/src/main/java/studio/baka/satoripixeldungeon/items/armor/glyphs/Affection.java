package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Charm;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.Armor.Glyph;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Affection extends Glyph {

    private static final ItemSprite.Glowing PINK = new ItemSprite.Glowing(0xFF4488);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        int level = Math.max(0, armor.level());

        // lvl 0 - 15%
        // lvl 1 ~ 19%
        // lvl 2 ~ 23%
        if (Random.Int(level + 20) >= 17) {

            int duration = Random.IntRange(8, 12);

            Buff.affect(attacker, Charm.class, duration).object = defender.id();
            attacker.sprite.centerEmitter().start(Speck.factory(Speck.HEART), 0.2f, 5);

        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return PINK;
    }
}
