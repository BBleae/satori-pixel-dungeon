package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Levitation;
import studio.baka.satoripixeldungeon.actors.buffs.Vertigo;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;

public class Stormvine extends Plant {

    {
        image = 5;
    }

    @Override
    public void activate(Char ch) {

        if (ch != null) {
            if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Levitation.class, 10f);
            } else {
                Buff.affect(ch, Vertigo.class, Vertigo.DURATION);
            }
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_STORMVINE;

            plantClass = Stormvine.class;
        }
    }
}
