package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.AdrenalineSurge;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.particles.LeafParticle;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Rotberry extends Plant {

    {
        image = 0;
    }

    @Override
    public void activate(Char ch) {
        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, AdrenalineSurge.class).reset(1, 200f);
        }

        Dungeon.level.drop(new Seed(), pos).sprite.drop();
    }

    @Override
    public void wither() {
        Dungeon.level.uproot(pos);

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6);
        }

        //no warden benefit
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_ROTBERRY;

            plantClass = Rotberry.class;
        }

        @Override
        public int price() {
            return 30 * quantity;
        }
    }
}
