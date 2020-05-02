package studio.baka.satoripixeldungeon.items.armor.curses;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Burning;
import studio.baka.satoripixeldungeon.actors.buffs.Frost;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.FlameParticle;
import studio.baka.satoripixeldungeon.effects.particles.SnowParticle;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.items.armor.Armor.Glyph;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import studio.baka.satoripixeldungeon.sprites.ItemSprite.Glowing;
import com.watabou.utils.Random;

public class AntiEntropy extends Glyph {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (Random.Int(8) == 0) {

            if (Dungeon.level.adjacent(attacker.pos, defender.pos)) {
                Buff.prolong(attacker, Frost.class, Frost.duration(attacker) * Random.Float(0.5f, 1f));
                CellEmitter.get(attacker.pos).start(SnowParticle.FACTORY, 0.2f, 6);
            }

            Buff.affect(defender, Burning.class).reignite(defender);
            defender.sprite.emitter().burst(FlameParticle.FACTORY, 5);

        }

        return damage;
    }

    @Override
    public Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}
