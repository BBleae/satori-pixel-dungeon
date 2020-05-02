package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.effects.particles.EnergyParticle;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.Armor.Glyph;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class Potential extends Glyph {

    private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF, 0.6f);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        int level = Math.max(0, armor.level());

        // lvl 0 - 16.7%
        // lvl 1 - 28.6%
        // lvl 2 - 37.5%
        if (defender instanceof Hero && Random.Int(level + 6) >= 5) {
            int wands = ((Hero) defender).belongings.charge(1f);
            if (wands > 0) {
                defender.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 10);
            }
        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return WHITE;
    }
}
