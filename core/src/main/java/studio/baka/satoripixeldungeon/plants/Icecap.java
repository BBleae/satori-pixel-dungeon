package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.blobs.Fire;
import studio.baka.satoripixeldungeon.actors.blobs.Freezing;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.FrostImbue;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import studio.baka.satoripixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;

public class Icecap extends Plant {

    {
        image = 4;
    }

    @Override
    public void activate(Char ch) {

        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, FrostImbue.class, 15f);
        }

        PathFinder.buildDistanceMap(pos, BArray.not(Dungeon.level.losBlocking, null), 1);

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);

        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Freezing.affect(i, fire);
            }
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_ICECAP;

            plantClass = Icecap.class;
        }
    }
}
