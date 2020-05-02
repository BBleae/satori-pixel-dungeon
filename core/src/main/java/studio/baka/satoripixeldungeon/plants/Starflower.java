package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Bless;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Recharging;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Starflower extends Plant {

    {
        image = 9;
    }

    @Override
    public void activate(Char ch) {

        if (ch != null) {
            Buff.prolong(ch, Bless.class, Bless.DURATION);
            if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
                Buff.prolong(ch, Recharging.class, Bless.DURATION);
            }
        }

        if (Random.Int(5) == 0) {
            Dungeon.level.drop(new Seed(), pos).sprite.drop();
        }
    }

    public static class Seed extends Plant.Seed {

        {
            image = ItemSpriteSheet.SEED_STARFLOWER;

            plantClass = Starflower.class;
        }

        @Override
        public int price() {
            return 30 * quantity;
        }
    }
}
