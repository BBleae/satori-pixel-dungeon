package studio.baka.satoripixeldungeon.plants;

import studio.baka.satoripixeldungeon.Dungeon;
import studio.baka.satoripixeldungeon.actors.Char;
import studio.baka.satoripixeldungeon.actors.buffs.Blindness;
import studio.baka.satoripixeldungeon.actors.buffs.Buff;
import studio.baka.satoripixeldungeon.actors.buffs.Cripple;
import studio.baka.satoripixeldungeon.actors.buffs.Invisibility;
import studio.baka.satoripixeldungeon.actors.hero.Hero;
import studio.baka.satoripixeldungeon.actors.hero.HeroSubClass;
import studio.baka.satoripixeldungeon.actors.mobs.Mob;
import studio.baka.satoripixeldungeon.effects.CellEmitter;
import studio.baka.satoripixeldungeon.effects.Speck;
import studio.baka.satoripixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Blindweed extends Plant {

    {
        image = 11;
    }

    @Override
    public void activate(Char ch) {

        if (ch != null) {
            if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
                Buff.affect(ch, Invisibility.class, 10f);
            } else {
                int len = Random.Int(5, 10);
                Buff.prolong(ch, Blindness.class, len);
                Buff.prolong(ch, Cripple.class, len);
                if (ch instanceof Mob) {
                    if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
                    ((Mob) ch).beckon(Dungeon.level.randomDestination());
                }
            }
        }

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
        }
    }

    public static class Seed extends Plant.Seed {
        {
            image = ItemSpriteSheet.SEED_BLINDWEED;

            plantClass = Blindweed.class;
        }
    }
}
