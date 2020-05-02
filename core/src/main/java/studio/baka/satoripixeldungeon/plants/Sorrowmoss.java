package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Poison;
import studio.baka.satoripixeldungeon.actors.buffs.ToxicImbue;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.PoisonParticle;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Sorrowmoss extends Plant {

    {
        image = 6;
    }

    @Override
    public void activate(Char ch) {
        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, ToxicImbue.class).set(15f);
        }

        if (ch != null) {
            Buff.affect(ch, Poison.class).set(5 + Math.round(2 * Dungeon.depth / 3f));
        }

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_SORROWMOSS;

            plantClass = Sorrowmoss.class;
        }
    }
}
