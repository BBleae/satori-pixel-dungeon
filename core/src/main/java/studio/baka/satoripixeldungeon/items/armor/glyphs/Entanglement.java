package studio.baka.satoripixeldungeon.items.armor.glyphs;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.EarthParticle;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.Armor.Glyph;
import studio.baka.satoripixeldungeon.plants.Earthroot;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;

public class Entanglement extends Glyph {

    private static final ItemSprite.Glowing BROWN = new ItemSprite.Glowing(0x663300);

    @Override
    public int proc(Armor armor, Char attacker, final Char defender, final int damage) {

        final int level = Math.max(0, armor.level());

        if (Random.Int(4) == 0) {

            Buff.affect(defender, Earthroot.Armor.class).level(5 + 2 * level);
            CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8);
            Camera.main.shake(1, 0.4f);

        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return BROWN;
    }

}
