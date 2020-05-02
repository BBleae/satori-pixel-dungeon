package studio.baka.satoripixeldungeon.items.armor.curses;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.LeafParticle;
import studio.baka.satoripixeldungeon.items.Generator;
import studio.baka.satoripixeldungeon.items.armor.Armor;
import studio.baka.satoripixeldungeon.plants.BlandfruitBush;
import studio.baka.satoripixeldungeon.plants.Plant;
import studio.baka.satoripixeldungeon.plants.Starflower;
import studio.baka.satoripixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Overgrowth extends Armor.Glyph {

    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (Random.Int(20) == 0) {

            Plant.Seed s;
            do {
                s = (Plant.Seed) Generator.random(Generator.Category.SEED);
            } while (s instanceof BlandfruitBush.Seed || s instanceof Starflower.Seed);

            Plant p = s.couch(defender.pos, null);

            //momentarily revoke warden benefits, otherwise this curse would be incredibly powerful
            if (defender instanceof Hero && ((Hero) defender).subClass == HeroSubClass.WARDEN) {
                ((Hero) defender).subClass = HeroSubClass.NONE;
                p.activate(defender);
                ((Hero) defender).subClass = HeroSubClass.WARDEN;
            } else {
                p.activate(defender);
            }


            CellEmitter.get(defender.pos).burst(LeafParticle.LEVEL_SPECIFIC, 10);

        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    public boolean curse() {
        return true;
    }
}
